package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.CustomDurabilityManager;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class DynamicDurabilityModifier extends DuoArgDynamicItemModifier {

    public DynamicDurabilityModifier(String name) {
        super(name, 0D, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_CUSTOM;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1000D;

        this.bigStepDecrease2 = .25D;
        this.bigStepIncrease2 = .25D;
        this.smallStepDecrease2 = .01D;
        this.smallStepIncrease2 = .01D;
        this.defaultStrength2 = 0;
        this.minStrength2 = 0;
        this.maxStrength2 = Integer.MAX_VALUE;
        this.description = Utils.chat("&7Updates the item's custom durability values. The strength of the modifier " +
                "represents the % of the item's quality rating used in determining the max durability. Example: " +
                "if an item has a quality rating of 150, setting a strength of 50% will update the " +
                "item's durability to if it had a rating of 75, and 200% results in a rating of 300. " +
                "The second strength determines the minimum value of the attribute compared to its default. If this value " +
                "is 0.8 for example, a base durability of 100 cannot be reduced to anything below 80 and will always be 80" +
                " or higher. This is to make sure low skill levels don't suffer too much of a penalty when crafting such recipes");
        this.displayName = Utils.chat("&b&lUpdate Durability");
        this.icon = Material.DIAMOND;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<percentage_quality>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        int quality = SmithingItemTreatmentManager.getInstance().getItemsQuality(outputItem);
        int finalQuality = (int) Math.round((strength / 100D) * quality);
        CustomDurabilityManager.getInstance().applyCustomDurability(outputItem, finalQuality, strength2);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's custom durability to be &e%s%%&7 efficient with its quality score. " +
                "This value will at least be &e%.2fx&7 the attribute's default strength", strength, strength2));
    }
}
