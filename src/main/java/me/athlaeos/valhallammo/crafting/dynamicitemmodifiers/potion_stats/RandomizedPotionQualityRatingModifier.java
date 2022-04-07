package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.AlchemyPotionTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class RandomizedPotionQualityRatingModifier extends DuoArgDynamicItemModifier implements Cloneable{

    public RandomizedPotionQualityRatingModifier(String name, double strength, double strength2, ModifierPriority priority) {
        super(name, strength, strength2, priority);

        this.name = name;
        this.category = ModifierCategory.POTION_STATS;

        this.bigStepDecrease = 0.1;
        this.bigStepIncrease = 0.1;
        this.smallStepDecrease = 0.01;
        this.smallStepIncrease = 0.01;
        this.defaultStrength = 0;
        this.minStrength = 100D;
        this.maxStrength = 1000D;

        this.bigStepDecrease2 = 0.1;
        this.bigStepIncrease2 = 0.1;
        this.smallStepDecrease2 = 0.01;
        this.smallStepIncrease2 = 0.01;
        this.defaultStrength2 = 0;
        this.minStrength2 = -100D;
        this.maxStrength2 = 1000D;
        this.description = Utils.chat("&7Changes the current potion quality rating of the item to be randomized between " +
                "two percentages away from the current amount. Example: a potion with a quality rating of 200 " +
                "with this modifier set to -10% to 10% will get a quality rating between 180 and 220 (200 - 10% and " +
                "200 + 10%). The same item with the modifier set to 20% to 30% would get a rating between 240 and 260.");
        this.displayName = Utils.chat("&b&lUpdate Quality : RANDOM");
        this.icon = Material.POTION;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        int quality = AlchemyPotionTreatmentManager.getInstance().getPotionQuality(outputItem);
        if (quality == 0) return outputItem;
        if (strength > strength2) {
            crafter.sendMessage(Utils.chat("&cThis recipe has been improperly configured, randomized lower bound " +
                    "is not allowed to exceed the upper bound. Notify server owner(s)/admin(s)"));
            return null;
        }
        int lowerBound = quality + (int) Math.floor(strength * quality);
        int upperBound = quality + (int) Math.ceil(strength2 * quality);
        int newQuality = Utils.getRandom().nextInt((upperBound + 1) - lowerBound) + lowerBound;
        AlchemyPotionTreatmentManager.getInstance().setPotionQuality(outputItem, newQuality);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the potion's quality rating to a random value between &e%.1f%% &7and &e%.1f%%&7 of the potion's original quality.", strength * 100, strength2 * 100));
    }
}
