package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class MultiplyQualityRatingModifier extends DynamicItemModifier {

    public MultiplyQualityRatingModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 0.1;
        this.bigStepIncrease = 0.1;
        this.smallStepDecrease = 0.01;
        this.smallStepIncrease = 0.01;
        this.defaultStrength = 1;
        this.minStrength = 0D;
        this.maxStrength = 100D;
        this.description = Utils.chat("&7Updates the item's quality rating. The strength of the modifier " +
                "represents the amount the item's quality rating will be multiplied by. As example, an item with " +
                "a quality rating of 100 with a multiplier of 1.5x will have 150 quality");
        this.displayName = Utils.chat("&b&lMultiply Quality");
        this.icon = Material.NETHER_STAR;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<multiplier>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        int quality = SmithingItemTreatmentManager.getInstance().getItemsQuality(outputItem);
        int newQuality = Math.max(0, (int) Math.floor(quality * strength));
        SmithingItemTreatmentManager.getInstance().setItemsQuality(outputItem, newQuality);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Multiplying item quality by &e%.2fx&7.", strength));
    }
}
