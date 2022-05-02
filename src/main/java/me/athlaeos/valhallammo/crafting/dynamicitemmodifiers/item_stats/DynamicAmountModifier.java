package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.dom.Scaling;
import me.athlaeos.valhallammo.dom.ScalingMode;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class DynamicAmountModifier extends DynamicItemModifier {
    private final Scaling scaling;

    public DynamicAmountModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1000D;
        this.description = Utils.chat("&7Changes the item's amount based on the crafter's skill in Smithing. " +
                "The calculation is defined in the skill_smithing.yml. The strength of the modifier represents " +
                "the % of the player's skill that is used in the calculation. The output of the calculation is " +
                "the multiplier of the output's amount. If a recipe for 8 iron ingots with this modifier " +
                "ended up getting an amount multiplier of 0.5, 8 ingots would be reduced to 4. " +
                "This modifier is intended to be used in 'salvaging' recipes, to extract some material from " +
                "tools and armor.");
        this.displayName = Utils.chat("&7&lDynamic Amount");
        this.icon = Material.IRON_INGOT;

        YamlConfiguration smithingConfig = ConfigManager.getInstance().getConfig("skill_smithing.yml").get();
        String scalingString = smithingConfig.getString("scaling_quantity");
        double min = smithingConfig.getDouble("scaling_minimum");
        double max = smithingConfig.getDouble("scaling_maximum");
        this.scaling = new Scaling(scalingString, ScalingMode.MULTIPLIER, min, max, false, false);
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<percentage_skill>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        double materialSkill = 0;
        double generalSkill = AccumulativeStatManager.getInstance().getStats("SMITHING_QUALITY_GENERAL", crafter, this.use);
        MaterialClass materialClass = MaterialClass.getMatchingClass(outputItem.getType());
        if (materialClass != null){
            materialSkill = AccumulativeStatManager.getInstance().getStats("SMITHING_QUALITY_" + materialClass, crafter, this.use);
        }
        int originalAmount = outputItem.getAmount();
        double multiplier = Utils.eval(scaling.getScaling().replace("%rating%", "" + ((generalSkill + materialSkill) * (strength / 100D))));
        int newAmount = Math.max(1, Utils.excessChance(originalAmount * Math.max(scaling.getLowerBound(), Math.min(scaling.getUpperBound(), multiplier))));
        outputItem.setAmount(newAmount);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's amount to an amount scaling with &e%s%%&7 of the player's smithing skill.", strength));
    }
}
