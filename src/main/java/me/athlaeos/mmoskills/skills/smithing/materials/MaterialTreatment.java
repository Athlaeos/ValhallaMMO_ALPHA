package me.athlaeos.mmoskills.skills.smithing.materials;

import org.bukkit.Material;

public enum MaterialTreatment {
    TEMPERED(Material.GOLDEN_PICKAXE, Material.GOLDEN_AXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_SWORD, Material.GOLDEN_HOE,
            Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,
            Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SHOVEL, Material.IRON_SWORD, Material.IRON_HOE,
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
            Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD, Material.DIAMOND_HOE,
            Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
            Material.NETHERITE_PICKAXE, Material.NETHERITE_AXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD, Material.NETHERITE_HOE,
            Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS),
    QUENCHED(Material.GOLDEN_PICKAXE, Material.GOLDEN_AXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_SWORD, Material.GOLDEN_HOE,
            Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,
            Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SHOVEL, Material.IRON_SWORD, Material.IRON_HOE,
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
            Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD, Material.DIAMOND_HOE,
            Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
            Material.NETHERITE_PICKAXE, Material.NETHERITE_AXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD, Material.NETHERITE_HOE,
            Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS),
    WORK_HARDENED(Material.GOLDEN_PICKAXE, Material.GOLDEN_AXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_SWORD, Material.GOLDEN_HOE,
            Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS,
            Material.IRON_PICKAXE, Material.IRON_AXE, Material.IRON_SHOVEL, Material.IRON_SWORD, Material.IRON_HOE,
            Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS,
            Material.DIAMOND_PICKAXE, Material.DIAMOND_AXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_SWORD, Material.DIAMOND_HOE,
            Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS,
            Material.NETHERITE_PICKAXE, Material.NETHERITE_AXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_SWORD, Material.NETHERITE_HOE,
            Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS),
    POLISHED(),
    ENGRAVED(),
    LEATHER_BOUND(),
    WAX_COATED(Material.WOODEN_PICKAXE, Material.WOODEN_AXE, Material.WOODEN_SHOVEL, Material.WOODEN_SWORD, Material.WOODEN_HOE,
            Material.STONE_PICKAXE, Material.STONE_AXE, Material.STONE_SHOVEL, Material.STONE_SWORD, Material.STONE_HOE);

    private final Material[] compatibleTools;
    MaterialTreatment(Material... compatibleTools){
        this.compatibleTools = compatibleTools;
    }

    public Material[] getCompatibleTools() {
        return compatibleTools;
    }
}
