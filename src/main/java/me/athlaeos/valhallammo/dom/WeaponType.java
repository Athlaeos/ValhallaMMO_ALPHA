package me.athlaeos.valhallammo.dom;

import me.athlaeos.valhallammo.ValhallaMMO;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public enum WeaponType {
    LIGHT(Material.WOODEN_SWORD, Material.STONE_SWORD, Material.IRON_SWORD, Material.GOLDEN_SWORD,
            Material.DIAMOND_SWORD, Material.NETHERITE_SWORD),
    HEAVY(Material.WOODEN_AXE, Material.STONE_AXE, Material.IRON_AXE, Material.GOLDEN_AXE,
            Material.DIAMOND_AXE, Material.NETHERITE_AXE, Material.TRIDENT, Material.SHIELD),
    WEIGHTLESS;
    private final static NamespacedKey weaponTypeKey = new NamespacedKey(ValhallaMMO.getPlugin(), "weapon_type");
    private final Collection<Material> types = new HashSet<>();

    WeaponType(Material... types){
        this.types.addAll(Arrays.asList(types));
    }

    public static WeaponType getWeaponType(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return WEIGHTLESS;
        if (meta.getPersistentDataContainer().has(weaponTypeKey, PersistentDataType.STRING)){
            String value = meta.getPersistentDataContainer().get(weaponTypeKey, PersistentDataType.STRING);
            if (value != null) {
                try {
                    return WeaponType.valueOf(value);
                } catch (IllegalArgumentException ignored){}
            }
        }
        for (WeaponType type : values()){
            if (type.getDefaultTypes().contains(item.getType())) return type;
        }
        return WEIGHTLESS;
    }

    public static void setWeaponType(ItemStack item, WeaponType type){
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (type == null){
            meta.getPersistentDataContainer().remove(weaponTypeKey);
        } else {
            meta.getPersistentDataContainer().set(weaponTypeKey, PersistentDataType.STRING, type.toString());
        }
        item.setItemMeta(meta);
    }

    public Collection<Material> getDefaultTypes() {
        return types;
    }
}
