package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class EquipmentTypeChangeModifier extends DynamicItemModifier {
    public EquipmentTypeChangeModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 1D;
        this.bigStepIncrease = 1D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = EquipmentClass.values().length - 1;
        this.description = Utils.chat("&7After this modifier the item will be considered a different equipment type, without actually changing the material.");
        this.displayName = Utils.chat("&7&lSet Equipment Type");
        this.icon = Material.LEATHER_HELMET;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Arrays.asList("0-SWORD", "1-BOW", "2-CROSSBOW", "3-TRIDENT", "4-HELMET", "5-CHESTPLATE", "6-LEGGINGS", "7-BOOTS",
                "8-SHEARS", "9-FLINT_AND_STEEL", "10-FISHING_ROD", "11-ELYTRA", "12-PICKAXE", "13-AXE", "14-SHOVEL", "15-HOE", "16-SHIELD");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        int id = (int) strength;
        if (id >= EquipmentClass.values().length || id < 0) return null;
        EquipmentClass.setEquipmentClass(outputItem, EquipmentClass.values()[id]);
        return outputItem;
    }

    @Override
    public String toString() {
        int id = (int) strength;
        if (id >= EquipmentClass.values().length || id < 0) id = EquipmentClass.values().length - 1;
        return Utils.chat("&7Setting the item's equipment type to &e" + EquipmentClass.values()[id] + "&7.");
    }
}
