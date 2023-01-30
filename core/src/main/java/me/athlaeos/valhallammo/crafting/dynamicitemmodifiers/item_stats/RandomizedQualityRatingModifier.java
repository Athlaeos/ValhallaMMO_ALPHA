package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class RandomizedQualityRatingModifier extends DuoArgDynamicItemModifier {

    public RandomizedQualityRatingModifier(String name) {
        super(name, 0D, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 0.1;
        this.bigStepIncrease = 0.1;
        this.smallStepDecrease = 0.01;
        this.smallStepIncrease = 0.01;
        this.defaultStrength = 0;
        this.minStrength = -1D;
        this.maxStrength = 100D;

        this.bigStepDecrease2 = 0.1;
        this.bigStepIncrease2 = 0.1;
        this.smallStepDecrease2 = 0.01;
        this.smallStepIncrease2 = 0.01;
        this.defaultStrength2 = 0;
        this.minStrength2 = -1D;
        this.maxStrength2 = 100D;
        this.description = Utils.chat("&7Changes the current quality rating of the item to be randomized between " +
                "two percentages away from the current amount. Example: an item with a quality rating of 200 " +
                "with this modifier set to -10% to 10% will get a quality rating between 180 and 220 (200 - 10% and " +
                "200 + 10%). The same item with the modifier set to 20% to 30% would get a rating between 240 and 260.");
        this.displayName = Utils.chat("&b&lUpdate Quality : RANDOM");
        this.icon = Material.NETHER_STAR;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<minimum_multiplier>");
    }

    @Override
    public List<String> tabAutoCompleteSecondArg() {
        return Collections.singletonList("<maximum_multiplier>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        if (!this.use) return outputItem;
        int quality = SmithingItemTreatmentManager.getInstance().getItemsQuality(outputItem);
        if (quality == 0) return outputItem;
        if (strength > strength2) {
            crafter.sendMessage(Utils.chat("&cThis recipe has been improperly configured, randomized lower bound " +
                    "is not allowed to exceed the upper bound. Notify server owner(s)/admin(s)"));
            return null;
        }
        int lowerBound = (int) Math.floor(strength * quality);
        int upperBound = (int) Math.floor(strength2 * quality);
        int newQuality = Utils.getRandom().nextInt((upperBound + 1) - lowerBound) + lowerBound;
        SmithingItemTreatmentManager.getInstance().setItemsQuality(outputItem, Math.max(0, newQuality));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's quality rating to a random value between &e%.1f &7and &e%.1f&7 of the item's original quality.", strength * 100, strength2 * 100));
    }
}
