package me.athlaeos.mmoskills.skills.smithing.managers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.athlaeos.mmoskills.MMOSkills;
import me.athlaeos.mmoskills.utility.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemDurabilityManager {
    private static ItemDurabilityManager manager = null;

    private final NamespacedKey key_durability = new NamespacedKey(MMOSkills.getPlugin(), "mmoskills_durability");
    private final NamespacedKey key_max_durability = new NamespacedKey(MMOSkills.getPlugin(), "mmoskills_max_durability");
    //tool properties:
    //  int custom durability
    //  double sharpness (flat dmg modifier)
    //  int temperature,
    //

    private BiMap<String, String> durabilityTranslations = HashBiMap.create();

    public ItemDurabilityManager(){
        durabilityTranslations.put(Utils.chat("&7durability:"), Utils.chat("&7durability:"));
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
            if (custom_durability < 0) custom_durability = 0;
            meta.getPersistentDataContainer().set(key_durability, PersistentDataType.INTEGER, custom_durability);
            item.setItemMeta(meta);
            updateItem(item);
        }
    }

    public void setDurability(ItemStack item, int durability, int max_durability){
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        if (!(item.getItemMeta() instanceof Damageable)) return;
        ItemMeta meta = item.getItemMeta();
        assert meta instanceof Damageable;
        if (durability > max_durability) durability = max_durability;
        meta.getPersistentDataContainer().set(key_durability, PersistentDataType.INTEGER, durability);
        meta.getPersistentDataContainer().set(key_max_durability, PersistentDataType.INTEGER, max_durability);
        item.setItemMeta(meta);
        updateItem(item);
    }

    public int getDurability(ItemStack item){
        if (item == null) return 0;
        if (item.getItemMeta() == null) return 0;
        if (!(item.getItemMeta() instanceof Damageable)) return 0;
        ItemMeta meta = item.getItemMeta();
        assert meta instanceof Damageable;
        if (meta.getPersistentDataContainer().has(key_durability, PersistentDataType.INTEGER)){
            return meta.getPersistentDataContainer().get(key_durability, PersistentDataType.INTEGER);
        } else {
            return 0;
        }
    }

    public int getMaxDurability(ItemStack item){
        if (item == null) return 0;
        if (item.getItemMeta() == null) return 0;
        if (!(item.getItemMeta() instanceof Damageable)) return 0;
        ItemMeta meta = item.getItemMeta();
        assert meta instanceof Damageable;
        if (meta.getPersistentDataContainer().has(key_max_durability, PersistentDataType.INTEGER)){
            return meta.getPersistentDataContainer().get(key_max_durability, PersistentDataType.INTEGER);
        } else {
            return 0;
        }
    }

    /*
    Updates an item's lore and durability meter to represent the item's custom tags and properties, such as durability,
    quality, damage, etc.
     */
    public void updateItem(ItemStack item){
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        ItemMeta meta = item.getItemMeta();
        if (!(meta instanceof Damageable)) return;
        short max_durability = item.getType().getMaxDurability();
        int custom_max_durability = getMaxDurability(item);
        int custom_durability = getDurability(item);
        short visual_durability = (short) (max_durability - (max_durability * ((double) custom_durability / (double) custom_max_durability)));

        ((Damageable) meta).setDamage(visual_durability);

        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        int durabilityLoreIndex = -1;
        for (String l : lore){
            if (l.contains(" ")){
                String[] splitString = l.split(" ");
                if (splitString.length >= 2){
                    String matchString = String.join(" ", Arrays.copyOfRange(splitString, 0, splitString.length - 1));
                    if (durabilityTranslations.get(matchString) != null){
                        durabilityLoreIndex = lore.indexOf(l);
                        break;
                    }
                }
            }
        }

        if (durabilityLoreIndex != -1) {
            lore.remove(durabilityLoreIndex);
        }
        lore.add(Utils.chat(String.format("&7durability: %d/%d", custom_durability, custom_max_durability)));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static ItemDurabilityManager getInstance(){
        if (manager == null){
            manager = new ItemDurabilityManager();
        }
        return manager;
    }
}
