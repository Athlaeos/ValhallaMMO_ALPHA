package me.athlaeos.valhallammo.items;

import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public enum EquipmentClass {
    SWORD(Arrays.asList(Material.GOLDEN_SWORD, Material.STONE_SWORD, Material.WOODEN_SWORD, Material.NETHERITE_SWORD, Material.DIAMOND_SWORD, Material.IRON_SWORD)),
    BOW(Collections.singletonList(Material.BOW)),
    CROSSBOW(Collections.singletonList(Material.CROSSBOW)),
    TRIDENT(Collections.singletonList(Material.TRIDENT)),
    HELMET(Arrays.asList(Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET, Material.GOLDEN_HELMET, Material.IRON_HELMET, Material.DIAMOND_HELMET, Material.NETHERITE_HELMET, Material.TURTLE_HELMET)),
    CHESTPLATE(Arrays.asList(Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE, Material.GOLDEN_CHESTPLATE, Material.IRON_CHESTPLATE, Material.DIAMOND_CHESTPLATE, Material.NETHERITE_CHESTPLATE)),
    LEGGINGS(Arrays.asList(Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS, Material.GOLDEN_LEGGINGS, Material.IRON_LEGGINGS, Material.DIAMOND_LEGGINGS, Material.NETHERITE_LEGGINGS)),
    BOOTS(Arrays.asList(Material.NETHERITE_BOOTS, Material.CHAINMAIL_BOOTS, Material.GOLDEN_BOOTS, Material.IRON_BOOTS, Material.DIAMOND_BOOTS, Material.NETHERITE_BOOTS)),
    SHEARS(Collections.singletonList(Material.SHEARS)),
    FLINT_AND_STEEL(Collections.singletonList(Material.FLINT_AND_STEEL)),
    FISHING_ROD(Collections.singletonList(Material.FISHING_ROD)),
    ELYTRA(Collections.singletonList(Material.ELYTRA)),
    PICKAXE(Arrays.asList(Material.WOODEN_PICKAXE, Material.STONE_PICKAXE, Material.GOLDEN_PICKAXE, Material.IRON_PICKAXE, Material.DIAMOND_PICKAXE, Material.NETHERITE_PICKAXE)),
    AXE(Arrays.asList(Material.WOODEN_AXE, Material.STONE_AXE, Material.GOLDEN_AXE, Material.IRON_AXE, Material.DIAMOND_AXE, Material.NETHERITE_AXE)),
    SHOVEL(Arrays.asList(Material.WOODEN_SHOVEL, Material.STONE_SHOVEL, Material.GOLDEN_SHOVEL, Material.IRON_SHOVEL, Material.DIAMOND_SHOVEL, Material.NETHERITE_SHOVEL)),
    HOE(Arrays.asList(Material.WOODEN_HOE, Material.STONE_HOE, Material.GOLDEN_HOE, Material.IRON_HOE, Material.DIAMOND_HOE, Material.NETHERITE_HOE)),
    SHIELD(Collections.singletonList(Material.SHIELD));

    private final Collection<Material> matches = new HashSet<>();

    EquipmentClass(Collection<Material> matches){
        this.matches.addAll(matches);
    }

    public Collection<Material> getMatches() {
        return matches;
    }

    public static EquipmentClass getClass(Material m){
        for (EquipmentClass tc : EquipmentClass.values()){
            if (tc.getMatches().contains(m)){
                return tc;
            }
        }
        return null;
    }

    public static boolean isArmor(Material m){
        if (EquipmentClass.HELMET.getMatches().contains(m)) return true;
        if (EquipmentClass.CHESTPLATE.getMatches().contains(m)) return true;
        if (EquipmentClass.LEGGINGS.getMatches().contains(m)) return true;
        return EquipmentClass.BOOTS.getMatches().contains(m);
    }
}
