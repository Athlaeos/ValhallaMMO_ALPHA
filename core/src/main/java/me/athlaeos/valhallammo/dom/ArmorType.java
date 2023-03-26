package me.athlaeos.valhallammo.dom;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.utility.EntityUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public enum ArmorType {
    LIGHT(Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS,
            Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS,
            Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
            Material.TURTLE_HELMET),
    HEAVY(Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
            Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS),
    WEIGHTLESS;
    private final static NamespacedKey armorTypeKey = new NamespacedKey(ValhallaMMO.getPlugin(), "armor_type");
    private final Collection<Material> types = new HashSet<>();

    ArmorType(Material... types){
        this.types.addAll(Arrays.asList(types));
    }

    public static ArmorType getArmorType(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return WEIGHTLESS;
        if (meta.getPersistentDataContainer().has(armorTypeKey, PersistentDataType.STRING)){
            String value = meta.getPersistentDataContainer().get(armorTypeKey, PersistentDataType.STRING);
            if (value != null) {
                try {
                    return ArmorType.valueOf(value);
                } catch (IllegalArgumentException ignored){}
            }
        }
        for (ArmorType type : values()){
            if (type.getDefaultTypes().contains(item.getType())) return type;
        }
        return WEIGHTLESS;
    }

    public static void setArmorType(ItemStack item, ArmorType type){
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (type == null){
            meta.getPersistentDataContainer().remove(armorTypeKey);
        } else {
            meta.getPersistentDataContainer().set(armorTypeKey, PersistentDataType.STRING, type.toString());
        }
        item.setItemMeta(meta);
    }

    public static int getArmorTypeCount(LivingEntity entity, ArmorType type){
        int count = 0;
        for (ItemStack i : EntityUtils.getEntityProperties(entity).getIterable(false)){
            if (getArmorType(i) == type) count++;
        }
        return count;
    }

    public Collection<Material> getDefaultTypes() {
        return types;
    }
}
