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

public class AddQualityRatingModifier extends DynamicItemModifier implements Cloneable{

    public AddQualityRatingModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = -1000D;
        this.maxStrength = 1000D;
        this.description = Utils.chat("&7Updates the item's quality rating. The strength of the modifier " +
                "represents the amount of quality points added/subtracted from the item.");
        this.displayName = Utils.chat("&b&lAdd/Subtract Quality");
        this.icon = Material.NETHER_STAR;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<quality>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        int quality = SmithingItemTreatmentManager.getInstance().getItemsQuality(outputItem);
        int newQuality = Math.max(0, quality + (int) strength);
        SmithingItemTreatmentManager.getInstance().setItemsQuality(outputItem, newQuality);
        return outputItem;
    }

    @Override
    public String toString() {
        if (strength < 0){
            return Utils.chat(String.format("&7Subtracting &e%s&7 from the item's quality.", strength));
        } else {
            return Utils.chat(String.format("&Adding &e%s&7 to the item's quality.", strength));
        }
    }
}
