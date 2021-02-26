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

public class ItemDamageManager {
    private static ItemDamageManager manager = null;

    private final NamespacedKey key_damage = new NamespacedKey(MMOSkills.getPlugin(), "mmoskills_damage");
    //tool properties:
    //  int custom durability
    //  double sharpness (flat dmg modifier)
    //  int temperature,
    //

    private BiMap<String, String> damageTranslations = HashBiMap.create();

    public ItemDamageManager(){
        damageTranslations.put(Utils.chat("&cDamage:"), Utils.chat("&cDamage:"));
    }

    public void setDamage(ItemStack item, double damage){
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        if (!(item.getItemMeta() instanceof Damageable)) return;
        ItemMeta meta = item.getItemMeta();
        assert meta instanceof Damageable;
        meta.getPersistentDataContainer().set(key_damage, PersistentDataType.DOUBLE, damage);
        item.setItemMeta(meta);
        updateItem(item);
    }

    public double getDamage(ItemStack item){
        if (item == null) return 0;
        if (item.getItemMeta() == null) return 0;
        if (!(item.getItemMeta() instanceof Damageable)) return 0;
        ItemMeta meta = item.getItemMeta();
        assert meta instanceof Damageable;
        if (meta.getPersistentDataContainer().has(key_damage, PersistentDataType.DOUBLE)){
            return meta.getPersistentDataContainer().get(key_damage, PersistentDataType.DOUBLE);
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
        double custom_damage = getDamage(item);

        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        int damageLoreIndex = -1;
        for (String l : lore){
            if (l.contains(" ")){
                String[] splitString = l.split(" ");
                if (splitString.length >= 2){
                    String matchString = String.join(" ", Arrays.copyOfRange(splitString, 0, splitString.length - 1));
                    if (damageTranslations.get(matchString) != null){
                        damageLoreIndex = lore.indexOf(l);
                        break;
                    }
                }
            }
        }

        if (damageLoreIndex != -1) {
            lore.remove(damageLoreIndex);
        }
        lore.add(Utils.chat(String.format("&cdamage: +%.2f", custom_damage)));
        meta.setLore(lore);
        item.setItemMeta(meta);
    }

    public static ItemDamageManager getInstance(){
        if (manager == null){
            manager = new ItemDamageManager();
        }
        return manager;
    }
}
