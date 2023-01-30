package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.dom.ArmorType;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ArmorHeavyModifier extends DynamicItemModifier {
    public ArmorHeavyModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 0D;
        this.bigStepIncrease = 0D;
        this.smallStepDecrease = 0D;
        this.smallStepIncrease = 0D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Changes the armor weight class to be Heavy Armor");
        this.displayName = Utils.chat("&7&lHeavy Armor");
        this.icon = Material.NETHERITE_CHESTPLATE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        ArmorType.setArmorType(outputItem, ArmorType.HEAVY);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7This armor is now considered &eHeavy Armor");
    }
}
