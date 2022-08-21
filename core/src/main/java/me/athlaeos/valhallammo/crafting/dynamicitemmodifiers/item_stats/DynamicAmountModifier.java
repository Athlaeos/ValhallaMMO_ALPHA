package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
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

public class DynamicAmountModifier extends DuoArgDynamicItemModifier {
    private final Scaling scaling;

    public DynamicAmountModifier(String name, double strength, double strength2, ModifierPriority priority) {
        super(name, strength, strength2, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = Integer.MAX_VALUE;

        this.bigStepDecrease2 = 1D;
        this.bigStepIncrease2 = 1D;
        this.smallStepDecrease2 = .1D;
        this.smallStepIncrease2 = .1D;
        this.defaultStrength2 = 0;
        this.minStrength2 = 0;
        this.maxStrength2 = Integer.MAX_VALUE;
        this.description = Utils.chat("&7Changes the item's amount based on the crafter's skill in Smithing. " +
                "The calculation is defined in the skill_smithing.yml. The strength of the modifier represents " +
                "the % of the player's skill that is used in the calculation. The output of the calculation is " +
                "the multiplier of the output's amount. If a recipe for 8 iron ingots with this modifier " +
                "ended up getting an amount multiplier of 0.5, 8 ingots would be reduced to 4. " +
                "This modifier is intended to be used in 'salvaging' recipes, to extract some material from " +
                "tools and armor. " +
                "The second strength determines the minimum value of the attribute compared to its default. If this value " +
                "is 0.8 for example, a base amount of 10 cannot be reduced to anything below 8 and will always be 8" +
                " or higher. This is to make sure low skill levels don't suffer too much of a penalty when crafting such recipes");
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
        MaterialClass materialClass = MaterialClass.getMatchingClass(outputItem);
        if (materialClass != null){
            materialSkill = AccumulativeStatManager.getInstance().getStats("SMITHING_QUALITY_" + materialClass, crafter, this.use);
        }
        int originalAmount = outputItem.getAmount();
        int minimumAmount = Math.max(1, (int) (originalAmount * strength2));
        double multiplier = Utils.eval(scaling.getScaling().replace("%rating%", "" + ((generalSkill + materialSkill) * (strength / 100D))));
        int newAmount = Math.max(minimumAmount, Utils.excessChance(originalAmount * Math.max(scaling.getLowerBound(), Math.min(scaling.getUpperBound(), multiplier))));
        outputItem.setAmount(newAmount);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's amount to an amount scaling with &e%s%%&7 of the player's smithing skill. Amount will be at least &e%.2fx the original item amount", strength, strength2));
    }
}
