package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class MaterialTypeChangeModifier extends DynamicItemModifier {
    public MaterialTypeChangeModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 1D;
        this.bigStepIncrease = 1D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = MaterialClass.values().length - 1;
        this.description = Utils.chat("&7After this modifier the item will be considered a different material, without actually changing the material.");
        this.displayName = Utils.chat("&7&lSet Material Type");
        this.icon = Material.OAK_PLANKS;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Arrays.asList("0-WOOD", "1-BOW", "2-CROSSBOW", "3-LEATHER", "4-STONE", "5-CHAINMAIL", "6-GOLD", "7-IRON",
                "8-DIAMOND", "9-NETHERITE", "10-PRISMARINE", "11-MEMBRANE", "12-OTHER");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        int id = (int) strength;
        if (id >= MaterialClass.values().length || id < 0) return null;
        MaterialClass.setMaterialType(outputItem, MaterialClass.values()[id]);
        return outputItem;
    }

    @Override
    public String toString() {
        int id = (int) strength;
        if (id >= MaterialClass.values().length || id < 0) id = MaterialClass.values().length - 1;
        return Utils.chat("&7Setting the item's material type to &e" + MaterialClass.values()[id] + "&7.");
    }
}
