package me.athlaeos.valhallammo.materials;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum MaterialClass {
    WOOD(Arrays.asList(Material.WOODEN_PICKAXE, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_SHOVEL,
            Material.WOODEN_SWORD, Material.FISHING_ROD, Material.CARROT_ON_A_STICK, Material.WARPED_FUNGUS_ON_A_STICK)),
    BOW(Collections.singletonList(Material.BOW)),
    CROSSBOW(Collections.singletonList(Material.CROSSBOW)),
    LEATHER(Arrays.asList(Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET,
            Material.LEATHER_LEGGINGS)),
    STONE(Arrays.asList(Material.STONE_PICKAXE, Material.STONE_AXE, Material.STONE_HOE, Material.STONE_SHOVEL,
            Material.STONE_SWORD)),
    CHAINMAIL(Arrays.asList(Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET,
            Material.CHAINMAIL_LEGGINGS)),
    GOLD(Arrays.asList(Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_HOE, Material.GOLDEN_AXE,
            Material.GOLDEN_SWORD, Material.GOLDEN_BOOTS, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE,
            Material.GOLDEN_LEGGINGS)),
    IRON(Arrays.asList(Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_HOE, Material.IRON_AXE,
            Material.IRON_SWORD, Material.IRON_BOOTS, Material.IRON_HELMET, Material.IRON_CHESTPLATE,
            Material.IRON_LEGGINGS, Material.SHIELD, Material.FLINT_AND_STEEL, Material.SHEARS)),
    DIAMOND(Arrays.asList(Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE,
            Material.DIAMOND_AXE, Material.DIAMOND_SWORD, Material.DIAMOND_BOOTS, Material.DIAMOND_HELMET,
            Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS)),
    NETHERITE(Arrays.asList(Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_HOE,
            Material.NETHERITE_AXE, Material.NETHERITE_SWORD, Material.NETHERITE_BOOTS, Material.NETHERITE_HELMET,
            Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS)),
    PRISMARINE(Collections.singletonList(Material.TRIDENT)),
    MEMBRANE(Collections.singletonList(Material.ELYTRA)),
    OTHER(null);

    private List<Material> matchingMaterials;

    MaterialClass(List<Material> matchingMaterials){
        if (matchingMaterials == null){
            List<Material> excludeItems = new ArrayList<>();
            excludeItems.addAll(Arrays.asList(Material.WOODEN_PICKAXE, Material.WOODEN_AXE, Material.WOODEN_HOE, Material.WOODEN_SHOVEL,
                    Material.WOODEN_SWORD, Material.FISHING_ROD, Material.CARROT_ON_A_STICK, Material.WARPED_FUNGUS_ON_A_STICK));
            excludeItems.addAll(Arrays.asList(Material.LEATHER_BOOTS, Material.LEATHER_CHESTPLATE, Material.LEATHER_HELMET,
                    Material.LEATHER_LEGGINGS));
            excludeItems.addAll(Arrays.asList(Material.STONE_PICKAXE, Material.STONE_AXE, Material.STONE_HOE, Material.STONE_SHOVEL,
                    Material.STONE_SWORD));
            excludeItems.addAll(Arrays.asList(Material.CHAINMAIL_BOOTS, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_HELMET,
                    Material.CHAINMAIL_LEGGINGS));
            excludeItems.addAll(Arrays.asList(Material.GOLDEN_PICKAXE, Material.GOLDEN_SHOVEL, Material.GOLDEN_HOE, Material.GOLDEN_AXE,
                    Material.GOLDEN_SWORD, Material.GOLDEN_BOOTS, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE,
                    Material.GOLDEN_LEGGINGS));
            excludeItems.addAll(Arrays.asList(Material.IRON_PICKAXE, Material.IRON_SHOVEL, Material.IRON_HOE, Material.IRON_AXE,
                    Material.IRON_SWORD, Material.IRON_BOOTS, Material.IRON_HELMET, Material.IRON_CHESTPLATE,
                    Material.IRON_LEGGINGS, Material.SHIELD, Material.FLINT_AND_STEEL, Material.SHEARS));
            excludeItems.addAll(Arrays.asList(Material.DIAMOND_PICKAXE, Material.DIAMOND_SHOVEL, Material.DIAMOND_HOE,
                    Material.DIAMOND_AXE, Material.DIAMOND_SWORD, Material.DIAMOND_BOOTS, Material.DIAMOND_HELMET,
                    Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS));
            excludeItems.addAll(Arrays.asList(Material.NETHERITE_PICKAXE, Material.NETHERITE_SHOVEL, Material.NETHERITE_HOE,
                    Material.NETHERITE_AXE, Material.NETHERITE_SWORD, Material.NETHERITE_BOOTS, Material.NETHERITE_HELMET,
                    Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS));
            excludeItems.add(Material.BOW);
            excludeItems.add(Material.CROSSBOW);
            excludeItems.add(Material.TRIDENT);
            excludeItems.add(Material.ELYTRA);
            List<Material> finalMaterials = new ArrayList<>(Arrays.asList(Material.values()));
            finalMaterials.removeAll(excludeItems);
            this.matchingMaterials = finalMaterials;
        } else {
            this.matchingMaterials = matchingMaterials;
        }
    }

    public List<Material> getMatchingMaterials() {
        return matchingMaterials;
    }

    public void setMatchingMaterials(List<Material> matchingMaterials) {
        this.matchingMaterials = matchingMaterials;
    }

    public static MaterialClass getMatchingClass(Material m){
        for (MaterialClass c : MaterialClass.values()){
            if (c.getMatchingMaterials().contains(m)) return c;
        }
        return null;
    }
}
