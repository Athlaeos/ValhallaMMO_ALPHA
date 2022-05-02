package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.BlockConversion;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class BlockConversionManager {
    private static BlockConversionManager manager = null;
    private final Map<String, BlockConversion> interactConversions = new HashMap<>();
    private final Map<String, BlockConversion> damageConversions = new HashMap<>();
    private final Map<String, BlockConversion> allConversions = new HashMap<>();

    public BlockConversionManager(){
        YamlConfiguration conversionConfig = ConfigManager.getInstance().getConfig("block_interact_conversions.yml").get();

        for (String type : Arrays.asList("interact", "damage")){
            ConfigurationSection section = conversionConfig.getConfigurationSection("conversions." + type);
            if (section != null){
                for (String name : section.getKeys(false)){
                    String fs = conversionConfig.getString("conversions." + type + "." + name + ".block");
                    String ts = conversionConfig.getString("conversions." + type + "." + name + ".to");
                    String ss = conversionConfig.getString("conversions." + type + "." + name + ".sound");
                    if (fs == null || ts == null) throw new IllegalArgumentException("'block' and 'to' properties in block interact conversions may not be null");
                    try {
                        Material from = Material.valueOf(fs);
                        Material to = Material.valueOf(ts);
                        Sound sound = null;
                        if (ss != null){
                            sound = Sound.valueOf(ss);
                        }
                        Map<Material, BlockConversion.BlockConversionData> tools = new HashMap<>();
                        ConfigurationSection withSection = conversionConfig.getConfigurationSection("conversions." + type + "." + name + ".with");
                        if (withSection != null){
                            for (String withItem : withSection.getKeys(false)){
                                boolean consumed = withItem.startsWith("c:");
                                Material with = Material.valueOf(withItem.replace("c:", ""));
                                int customModelData = conversionConfig.getInt("conversions." + type + "." + name + ".with." + withItem, -1);

                                tools.put(with, new BlockConversion.BlockConversionData(customModelData, consumed));
                            }
                        }
                        if (withSection == null || tools.isEmpty()){
                            ValhallaMMO.getPlugin().getLogger().warning("Block interact conversion " + name + " did not have any items to trigger it, conversion registration cancelled");
                            continue;
                        }

                        if (type.equals("interact")){
                            registerInteractConversion(new BlockConversion(name, tools, from, to, sound));
                        } else if (type.equals("damage")){
                            registerDamageConversion(new BlockConversion(name, tools, from, to, sound));
                        }
                    } catch (IllegalArgumentException no){
                        ValhallaMMO.getPlugin().getLogger().warning("Could not register block interact conversion " + name + ", invalid material or sound used. " + fs + "/" + ts + "/" + ss);
                    }
                }
            }
        }
    }

    /**
     * Converts the damaged block to its converted type based on the item held in the player's main hand.
     * If the item is consumable (or damageable and about to break) it empties the player's hand or lowers amount by 1.
     * If no conversion is found, nothing happens.
     * If BlockPlaceEvents are not allowed where the block is damaged, nothing happens.
     * @param who the player damaging the block
     * @param b the block damaged
     */
    public void triggerDamageConversion(Player who, Block b){
        ItemStack tool = who.getInventory().getItemInMainHand();
        if (Utils.isItemEmptyOrNull(tool)) return;
        if (tool.getItemMeta() == null) return;
        // contains all block conversions where its FROM block matches the trigger block, and where its compatible tools contains the trigger tool type
        Collection<BlockConversion> conversions = damageConversions.values().stream().filter(blockConversion -> blockConversion.getFrom() == b.getType() && blockConversion.getCompatibleItems().containsKey(tool.getType())).collect(Collectors.toSet());
        if (conversions.isEmpty()) return;
        // contains all above block conversions where the custom model data, if required, matches.
        Collection<BlockConversion> conversion = conversions.stream().filter(blockConversion -> {
            BlockConversion.BlockConversionData data = blockConversion.getCompatibleItems().get(tool.getType());
            if (data == null) return false; // block conversion not compatible with tool
            if (data.getCustomModelData() >= 0){
                if (tool.getItemMeta().hasCustomModelData()){
                    return tool.getItemMeta().getCustomModelData() == data.getCustomModelData();
                } else {
                    return data.getCustomModelData() != 0;
                }
            }
            return true;
        }).collect(Collectors.toSet());
        if (conversion.size() > 0){
            // the final list should preferably only contain 0-1 elements if configuration is proper,
            // but if there are more one of them is picked
            BlockConversion finalConversion = conversion.iterator().next();
            BlockConversion.BlockConversionData data = finalConversion.getCompatibleItems().get(tool.getType());
            assert data != null;
            BlockState previousBlockState = b.getState();
            Block placedAgainst = b.getWorld().getBlockAt(b.getLocation().add(0, -1, 0));

            BlockPlaceEvent placeEvent = new BlockPlaceEvent(b, previousBlockState, placedAgainst, tool, who, true, EquipmentSlot.HAND);
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(placeEvent);
            if (!placeEvent.isCancelled()) {
                b.setType(finalConversion.getTo());
                if (finalConversion.getSound() != null){
                    who.playSound(who.getLocation(), finalConversion.getSound(), 1F, 1F);
                }
                if (data.isConsumed()){
                    if (EquipmentClass.getClass(tool.getType()) != null){
                        ItemUtils.damageItem(who, tool, 1, EntityEffect.BREAK_EQUIPMENT_MAIN_HAND);
                    } else {
                        if (tool.getAmount() <= 1){
                            who.getInventory().setItemInMainHand(null);
                        } else {
                            tool.setAmount(tool.getAmount() - 1);
                            who.getInventory().setItemInMainHand(tool);
                        }
                    }
                }
            }
        }
    }

    /**
     * Converts the interacted block to its converted type based on the item held in the player's hands.
     * If the item in their main hand has no block conversions associated with it, off hand is used.
     * If the item is consumable (or damageable and about to break) it empties the player's hand or lowers amount by 1.
     * If no conversion is found, nothing happens.
     * If BlockPlaceEvents are not allowed where the block is damaged, nothing happens.
     * @param who the player damaging the block
     * @param b the block damaged
     */
    public void triggerInteractConversion(Player who, Block b, Collection<String> possibleConversions){
        ItemStack t = who.getInventory().getItemInMainHand();
        if (Utils.isItemEmptyOrNull(t) || t.getItemMeta() == null) return;
        // contains all block conversions where its FROM block matches the trigger block, and where its compatible tools contains the trigger tool type
        Collection<BlockConversion> conversions = interactConversions.values().stream().filter(blockConversion -> blockConversion.getFrom() == b.getType() && blockConversion.getCompatibleItems().containsKey(t.getType())).collect(Collectors.toSet());
        if (conversions.isEmpty()) {
            return;
//            t = who.getInventory().getItemInOffHand();
//            if (Utils.isItemEmptyOrNull(t)) return;
//            if (t.getItemMeta() == null) return;
        }
        final ItemStack tool = t;
        conversions = interactConversions.values().stream().filter(blockConversion -> blockConversion.getFrom() == b.getType() && blockConversion.getCompatibleItems().containsKey(tool.getType())).collect(Collectors.toSet());
        // contains all above block conversions where the custom model data, if required, matches.
        Collection<BlockConversion> conversion = conversions.stream().filter(blockConversion -> {
            if (!possibleConversions.contains(blockConversion.getName())) return false;
            BlockConversion.BlockConversionData data = blockConversion.getCompatibleItems().get(tool.getType());
            if (data == null) return false; // block conversion not compatible with tool
            if (data.getCustomModelData() >= 0){
                if (tool.getItemMeta().hasCustomModelData()){
                    return tool.getItemMeta().getCustomModelData() == data.getCustomModelData();
                } else {
                    return data.getCustomModelData() != 0;
                }
            }
            return true;
        }).collect(Collectors.toSet());
        if (conversion.size() > 0){
            // the final list should preferably only contain 0-1 elements if configuration is proper,
            // but if there are more one of them is picked
            BlockConversion finalConversion = conversion.iterator().next();
            BlockConversion.BlockConversionData data = finalConversion.getCompatibleItems().get(t.getType());
            assert data != null;
            BlockState previousBlockState = b.getState();
            Block placedAgainst = b.getWorld().getBlockAt(b.getLocation().add(0, -1, 0));

            BlockPlaceEvent placeEvent = new BlockPlaceEvent(b, previousBlockState, placedAgainst, t, who, true, EquipmentSlot.HAND);
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(placeEvent);
            if (!placeEvent.isCancelled()) {
                b.setType(finalConversion.getTo());
                if (finalConversion.getSound() != null){
                    who.playSound(who.getLocation(), finalConversion.getSound(), 1F, 1F);
                }
                if (data.isConsumed()){
                    if (EquipmentClass.getClass(t.getType()) != null){
                        ItemUtils.damageItem(who, t, 1, EntityEffect.BREAK_EQUIPMENT_MAIN_HAND);
                    } else {
                        if (t.getAmount() <= 1){
                            who.getInventory().setItemInMainHand(null);
                        } else {
                            t.setAmount(t.getAmount() - 1);
                        }
                    }
                }
            }
        }
    }

    public void reload(){
        manager = null;
        getInstance();
    }

    public void registerInteractConversion(BlockConversion c){
        this.allConversions.put(c.getName(), c);
        this.interactConversions.put(c.getName(), c);
    }

    public void registerDamageConversion(BlockConversion c){
        this.allConversions.put(c.getName(), c);
        this.damageConversions.put(c.getName(), c);
    }

    public static BlockConversionManager getInstance(){
        if (manager == null) manager = new BlockConversionManager();
        return manager;
    }

    public Map<String, BlockConversion> getDamageConversions() {
        return damageConversions;
    }

    public Map<String, BlockConversion> getInteractConversions() {
        return interactConversions;
    }

    public Map<String, BlockConversion> getAllConversions(){
        return allConversions;
    }
}
