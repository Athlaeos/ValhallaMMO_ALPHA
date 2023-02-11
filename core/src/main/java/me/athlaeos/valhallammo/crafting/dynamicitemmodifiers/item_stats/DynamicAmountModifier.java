package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.dom.Scaling;
import me.athlaeos.valhallammo.dom.ScalingMode;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Collections;
import java.util.List;

public class DynamicAmountModifier extends TripleArgDynamicItemModifier {
    private final Scaling scaling;

    public DynamicAmountModifier(String name) {
        super(name, 0D, 0D, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = Integer.MAX_VALUE;

        this.bigStepDecrease2 = 0.1D;
        this.bigStepIncrease2 = 0.1D;
        this.smallStepDecrease2 = .01D;
        this.smallStepIncrease2 = .01D;
        this.defaultStrength2 = 0;
        this.minStrength2 = 0;
        this.maxStrength2 = Integer.MAX_VALUE;

        this.bigStepDecrease3 = 1D;
        this.bigStepIncrease3 = 1D;
        this.smallStepDecrease3 = 1D;
        this.smallStepIncrease3 = 1D;
        this.defaultStrength3 = 0;
        this.minStrength3 = 0;
        this.maxStrength3 = 1;
        this.description = Utils.chat("&7Scales the item's amount using a function defined in skill_smithing.yml." +
                " By default, this function will scale the item from 0-80% of its original amount, rounding downwards." +
                "The second strength determines the minimum value of the attribute compared to its default. If this value " +
                "is 0.8 for example, a base amount of 10 cannot be reduced to anything below 8 and will always be 8" +
                " or higher. This is to make sure low skill levels don't suffer too much of a penalty when crafting such recipes. " +
                "-nLastly, the third arg determines if the amount should also be reduced by the item's durability damage. An item with " +
                "half durability will first be halved in amount before the scaling occurs. " +
                "&eIf the final item's amount is 0, the recipe is cancelled" +
                "");
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
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        double materialSkill = 0;
        double generalSkill = AccumulativeStatManager.getInstance().getStats("SMITHING_QUALITY_GENERAL", crafter, this.use);
        MaterialClass materialClass = MaterialClass.getMatchingClass(outputItem);
        if (materialClass != null){
            materialSkill = AccumulativeStatManager.getInstance().getStats("SMITHING_QUALITY_" + materialClass, crafter, this.use);
        }
        int originalAmount = outputItem.getAmount();

        if (strength3 == 0){
            if (outputItem.getItemMeta() instanceof Damageable && outputItem.getType().getMaxDurability() > 0){
                double fractionDurability = (outputItem.getType().getMaxDurability() - ((Damageable) outputItem.getItemMeta()).getDamage()) / (double) outputItem.getType().getMaxDurability();
                originalAmount = (int) Math.floor(originalAmount * fractionDurability);
            }
       }

        int minimumAmount = Math.max(0, (int) (originalAmount * strength2));
        double multiplier = Utils.eval(scaling.getScaling().replace("%rating%", "" + ((generalSkill + materialSkill) * (strength / 100D))));
        int newAmount = Math.max(minimumAmount, (int) Math.floor(originalAmount * Math.max(scaling.getLowerBound(), Math.min(scaling.getUpperBound(), multiplier))));
        if (newAmount <= 0) return null;
        outputItem.setAmount(newAmount);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's amount to an amount scaling with &e%s%%&7 of the player's smithing skill. Amount will be at least &e%.2fx the original item amount. %s", strength, strength2, strength3 == 0 ? "This item's amount is first reduced based on durability damage" : ""));
    }
}
