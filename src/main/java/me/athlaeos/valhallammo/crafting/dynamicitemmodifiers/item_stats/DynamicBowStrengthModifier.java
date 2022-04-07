package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class DynamicBowStrengthModifier extends DynamicItemModifier {

    public DynamicBowStrengthModifier(String name, double strength, ModifierPriority priority) {
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
        this.description = Utils.chat("&7Updates the bow/crossbow's custom draw strength multiplier. The strength of the modifier " +
                "represents the % of the bow's quality rating used in determining the strength multiplier. Example: " +
                "if an item has a quality rating of 150, setting a strength of 50% will update the " +
                "bow's strength to if it had a rating of 75, and 200% results in a rating of 300.");
        this.displayName = Utils.chat("&b&lUpdate Bow Strength");
        this.icon = Material.BOW;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (!(outputItem.getType() == Material.BOW || outputItem.getType() == Material.CROSSBOW)) return null;
        int quality = SmithingItemTreatmentManager.getInstance().getItemsQuality(outputItem);
        int finalQuality = (int) Math.round((strength / 100D) * quality);
        SmithingItemTreatmentManager.getInstance().applyAttributeScaling(outputItem, finalQuality, "CUSTOM_DRAW_STRENGTH");
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's custom draw strength to be &e%.1f%%&7 efficient with its quality score.", strength));
    }
}
