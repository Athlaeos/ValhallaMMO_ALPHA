package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Scaling;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomDurabilityManager {

    private static CustomDurabilityManager manager = null;

    private final NamespacedKey key_durability = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_durability");
    private final NamespacedKey key_max_durability = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_max_durability");
    private String durabilityTranslation;

    public CustomDurabilityManager(){
        durabilityTranslation = Utils.chat(TranslationManager.getInstance().getTranslation("translation_durability"));
    }

    public void reload(){
        durabilityTranslation = Utils.chat(TranslationManager.getInstance().getTranslation("translation_durability"));
    }

    public static CustomDurabilityManager getInstance(){
        if (manager == null) manager = new CustomDurabilityManager();
        return manager;
    }

    public NamespacedKey getKey_durability() {
        return key_durability;
    }

    public NamespacedKey getKey_max_durability() {
        return key_max_durability;
    }

    /*
            Decreases (or increases) the item's custom durability, does not take into account enchantments like unbreaking.
            If the item does not have a custom durability tag, it is not damaged.
            If the item can't be damaged, nothing happens.
             */
    public void damageItem(ItemStack item, int damage){
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        if (!(item.getItemMeta() instanceof Damageable)) return;
        ItemMeta meta = item.getItemMeta();
        assert meta instanceof Damageable;
        if (meta.getPersistentDataContainer().has(key_durability, PersistentDataType.INTEGER) && meta.getPersistentDataContainer().has(key_max_durability, PersistentDataType.INTEGER)){
            int custom_max_durability = meta.getPersistentDataContainer().get(key_max_durability, PersistentDataType.INTEGER);
            int custom_durability = meta.getPersistentDataContainer().get(key_durability, PersistentDataType.INTEGER);
            if (custom_durability > custom_max_durability) custom_durability = custom_max_durability;
            custom_durability -= damage;
            if (custom_durability > custom_max_durability) custom_durability = custom_max_durability;
            meta.getPersistentDataContainer().set(key_durability, PersistentDataType.INTEGER, custom_durability);
            item.setItemMeta(meta);
            updateItemDurability(item);
        }
    }

    public void setDurability(ItemStack item, int durability, int max_durability){
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        if (!(item.getItemMeta() instanceof Damageable)) return;
        ItemMeta meta = item.getItemMeta();
        assert meta instanceof Damageable;
        if (max_durability < 1) max_durability = 1;
        if (durability > max_durability) durability = max_durability;
        meta.getPersistentDataContainer().set(key_durability, PersistentDataType.INTEGER, durability);
        meta.getPersistentDataContainer().set(key_max_durability, PersistentDataType.INTEGER, max_durability);
        item.setItemMeta(meta);
        updateItemDurability(item);
    }

    public int getDurability(ItemStack item){
        if (item == null) return 0;
        if (item.getItemMeta() == null) return 0;
        if (!(item.getItemMeta() instanceof Damageable) || item.getType().getMaxDurability() <= 0) return 0;
        ItemMeta meta = item.getItemMeta();
        assert meta instanceof Damageable;
        if (meta.getPersistentDataContainer().has(key_durability, PersistentDataType.INTEGER)){
            int durability = meta.getPersistentDataContainer().getOrDefault(key_durability, PersistentDataType.INTEGER, 0);
            if (durability < 0) durability = 0;
            return durability;
        } else {
            return item.getType().getMaxDurability() - ((Damageable) meta).getDamage();
        }
    }

    public boolean hasCustomDurability(ItemStack item){
        if (item == null) return false;
        if (item.getItemMeta() == null) return false;
        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(key_durability, PersistentDataType.INTEGER);
    }
    public int getMaxDurability(ItemStack item){
        if (item == null) return 0;
        if (item.getItemMeta() == null) return 0;
        if (!(item.getItemMeta() instanceof Damageable) || item.getType().getMaxDurability() <= 0) return 0;
        ItemMeta meta = item.getItemMeta();
        assert meta instanceof Damageable;
        if (meta.getPersistentDataContainer().has(key_max_durability, PersistentDataType.INTEGER)){
            return meta.getPersistentDataContainer().getOrDefault(key_max_durability, PersistentDataType.INTEGER, 0);
        } else {
            return item.getType().getMaxDurability();
        }
    }

    /*
    Updates an item's lore and durability meter to represent the item's custom tags and properties, such as durability,
    quality, damage, etc.
     */
    public void updateItemDurability(ItemStack item){
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) return;
        short max_durability = item.getType().getMaxDurability();
        int custom_max_durability = getMaxDurability(item);
        if (custom_max_durability < 1) custom_max_durability = 1;
        int custom_durability = getDurability(item);
        short visual_durability = (short) (max_durability - (max_durability * ((double) custom_durability / (double) custom_max_durability)));
        if (visual_durability < 1) visual_durability = 0;
        ((Damageable) meta).setDamage(visual_durability);

        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        int durabilityLoreIndex = -1;
        for (String l : lore){
            if (l.contains(" ")){
                String[] splitString = l.split(" ");
                if (splitString.length >= 2){
                    String matchString = String.join(" ", Arrays.copyOfRange(splitString, 0, splitString.length - 1));
                    if (durabilityTranslation.equals(matchString)){
                        durabilityLoreIndex = lore.indexOf(l);
                        break;
                    }
                }
            }
        }

        if (durabilityLoreIndex != -1) {
            lore.remove(durabilityLoreIndex);
        }
        if (!durabilityTranslation.equals("")){
            if (!meta.getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES) || !meta.isUnbreakable()) {
                lore.add(Utils.chat(String.format(durabilityTranslation + " %d/%d", custom_durability, custom_max_durability)));
            }
        }
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public void applyCustomDurability(ItemStack i, int qualityRating, double minimumMultiplier){
        if (i == null) return;
        Scaling scaling = SmithingItemTreatmentManager.getInstance().getScaling(i, "CUSTOM_MAX_DURABILITY");
        if (scaling == null) return;
        assert i.getItemMeta() != null;
        if (i.getItemMeta() instanceof Damageable){
            try {
                AttributeWrapper toolWrapper = ItemAttributesManager.getInstance().getAttributeWrapper(i, "CUSTOM_MAX_DURABILITY");
                int toolDefaultDurability = (int) ((toolWrapper == null) ? i.getType().getMaxDurability() : toolWrapper.getAmount());
                int minimum = Math.max(1, (int) (toolDefaultDurability * minimumMultiplier));
                int maxDurability = Math.max(minimum, (int) Math.round(((double) toolDefaultDurability) * Utils.eval(scaling.getScaling().replace("%rating%", "" + qualityRating))));
                double fractionDurability;
                if (i.getItemMeta().getPersistentDataContainer().has(key_durability, PersistentDataType.INTEGER)){
                    fractionDurability = ((double) getDurability(i))/((double) getMaxDurability(i));
                } else {
                    Damageable itemMeta = (Damageable) i.getItemMeta();
                    fractionDurability = ((double) (i.getType().getMaxDurability() - itemMeta.getDamage())) / ((double) i.getType().getMaxDurability());
                }
                if (fractionDurability < 0) fractionDurability = 0D;
                if (fractionDurability > 1) fractionDurability = 1D;
                if (maxDurability < 1) maxDurability = 1;
                int customDurability = (int) Math.ceil(maxDurability * fractionDurability);
                if (customDurability < 1) customDurability = 1;
                setDurability(i, customDurability, maxDurability);
            } catch (RuntimeException e){
                ValhallaMMO.getPlugin().getLogger().severe("Attempting to parse formula " + scaling + ", but something went wrong. ");
                e.printStackTrace();
            }
        }
    }
}
