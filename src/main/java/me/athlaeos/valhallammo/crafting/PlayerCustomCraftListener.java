package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.Main;
import me.athlaeos.valhallammo.configs.ConfigManager;
import me.athlaeos.valhallammo.domain.Skill;
import me.athlaeos.valhallammo.domain.SkillType;
import me.athlaeos.valhallammo.events.PlayerCustomCraftEvent;
import me.athlaeos.valhallammo.managers.CooldownManager;
import me.athlaeos.valhallammo.managers.MaterialCosmeticManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.smithing.SmithingSkill;
import me.athlaeos.valhallammo.skills.smithing.materials.EquipmentClass;
import me.athlaeos.valhallammo.skills.smithing.materials.MaterialClass;
import me.athlaeos.valhallammo.skills.smithing.recipes.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.skills.smithing.recipes.managers.PlayerCraftChoiceManager;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayerCustomCraftListener implements Listener {
    CooldownManager cooldownManager = CooldownManager.getInstance();
    private final boolean spawnOnTopOfBlock;
    private final String errorNoSpace;
    private final String errorTinkeringFailed;

    public PlayerCustomCraftListener(){
        spawnOnTopOfBlock = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("craft_item_drop");
        errorNoSpace = ConfigManager.getInstance().getConfig("messages.yml").get().getString("error_no_space");
        errorTinkeringFailed = ConfigManager.getInstance().getConfig("messages.yml").get().getString("error_conditions_failed");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCustomCraft(PlayerCustomCraftEvent e){
        if (!e.isCancelled()){
            boolean playerHasSpace = e.getPlayer().getInventory().firstEmpty() > -1;
            if (playerHasSpace || spawnOnTopOfBlock){
                ItemStack result = e.getRecipe().getResult().clone();
                // Modify item based on the recipe's improvement modifiers
                List<DynamicItemModifier> modifiers = new ArrayList<>(e.getRecipe().getItemModifers());
                modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                for (DynamicItemModifier modifier : modifiers){
                    if (result == null) break;
                    result = modifier.processItem(e.getPlayer(), result);
                }
                if (result != null){
                    if (e.getRecipe().breakStation()){
                        BlockBreakEvent event = new BlockBreakEvent(e.getCraftingStation(), e.getPlayer());
                        Main.getPlugin().getServer().getPluginManager().callEvent(event);
                        if (event.isCancelled()){
                            return;
                        } else {
                            event.getBlock().breakNaturally();
                        }
                    }
                    // player has inventory space and crafting requirements are met, ingredients are removed and result is added
                    for (ItemStack ingredient : e.getRecipe().getIngredients()){
                        e.getPlayer().getInventory().removeItem(ingredient);
                    }

                    if (spawnOnTopOfBlock){
                        Item itemDrop = (Item) e.getPlayer().getWorld().spawnEntity(e.getCraftingStation().getLocation().add(0.5, 1.2, 0.5), EntityType.DROPPED_ITEM);
                        itemDrop.setItemStack(result);
                        itemDrop.setPickupDelay(0);
                        itemDrop.setOwner(e.getPlayer().getUniqueId());
                        itemDrop.setThrower(e.getPlayer().getUniqueId());
                    } else {
                        e.getPlayer().getInventory().addItem(result);
                    }
                    Sound sound = MaterialCosmeticManager.getInstance().getCraftFinishSounds(e.getCraftingStation().getType());
                    if (sound != null){
                        e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), sound, .7F, 1F);
                    }
                    e.getPlayer().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, e.getCraftingStation().getLocation().add(0.5, 1, 0.5), 15);
                    PlayerCraftChoiceManager.getInstance().setPlayerCurrentRecipe(e.getPlayer(), null);
                    MaterialClass materialClass = MaterialClass.getMatchingClass(result.getType());
                    EquipmentClass equipmentClass = EquipmentClass.getClass(result.getType());
                    Skill skill = SkillProgressionManager.getInstance().getSkill(SkillType.SMITHING);
                    if (skill != null && materialClass != null && equipmentClass != null){
                        if (skill instanceof SmithingSkill){
                            SmithingSkill smithingSkill = (SmithingSkill) skill;
                            double materialClassBase = 0;
                            if (smithingSkill.getBaseExperienceValues().get(materialClass) != null) materialClassBase = smithingSkill.getBaseExperienceValues().get(materialClass);
                            double typeMultiplier = 0;
                            if (smithingSkill.getExperienceMultipliers().get(equipmentClass) != null) typeMultiplier = smithingSkill.getExperienceMultipliers().get(equipmentClass);
                            double expToAdd = materialClassBase * typeMultiplier;
                            smithingSkill.addSmithingEXP(e.getPlayer(), expToAdd, materialClass);
                        }
                    }
                } else {
                    // The recipe is cancelled
                    if (!errorTinkeringFailed.equals("")){
                        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(errorTinkeringFailed)));
                    }
                }
                cooldownManager.setCooldown(e.getPlayer().getUniqueId(), 750, "cancel_block_interactions");
            } else {
                if (!errorNoSpace.equals("")){
                    e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(errorNoSpace)));
                }
            }
        }
    }
}
