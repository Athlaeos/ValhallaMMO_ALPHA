package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class DynamicQualityRatingModifier extends DynamicItemModifier implements Cloneable{

    public DynamicQualityRatingModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1000D;
        this.description = Utils.chat("&7Updates the item's quality rating. The strength of the modifier " +
                "represents the % of the player's crafting skill points used in determining the item's new quality. Example: " +
                "if a player has 150 crafting skill points, setting a strength of 50% will update the " +
                "item's quality rating to a quality rating of 75, and 200% results in a rating of 300.");
        this.displayName = Utils.chat("&b&lUpdate Quality : DYNAMIC");
        this.icon = Material.NETHER_STAR;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<percentage_skill>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (crafter == null) return null;
        double materialSkill = 0;
        double generalSkill = AccumulativeStatManager.getInstance().getStats("SMITHING_QUALITY_GENERAL", crafter, this.use);
        MaterialClass materialClass = MaterialClass.getMatchingClass(outputItem);
        if (materialClass != null){
            materialSkill = AccumulativeStatManager.getInstance().getStats("SMITHING_QUALITY_" + materialClass, crafter, this.use);
        }
        SmithingItemTreatmentManager.getInstance().setItemsQuality(outputItem, (int) (materialSkill + generalSkill));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's quality rating to be &e%.1f%%&7 efficient with the player's crafting skill.", strength));
    }
}
