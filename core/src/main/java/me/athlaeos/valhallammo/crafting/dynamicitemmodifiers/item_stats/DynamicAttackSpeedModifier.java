package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class DynamicAttackSpeedModifier extends DuoArgDynamicItemModifier {
    public DynamicAttackSpeedModifier(String name, double strength, double strength2, ModifierPriority priority) {
        super(name, strength, strength2, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_VANILLA;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = Integer.MAX_VALUE;

        this.bigStepDecrease2 = .25D;
        this.bigStepIncrease2 = .25D;
        this.smallStepDecrease2 = .01D;
        this.smallStepIncrease2 = .01D;
        this.defaultStrength2 = 0;
        this.minStrength2 = 0;
        this.maxStrength2 = Integer.MAX_VALUE;
        this.description = Utils.chat("&7Updates the item's attack speed value if it has one. The strength of the modifier " +
                "represents the % of the item's quality rating used in determining its attack speed. Example: " +
                "if an item has a quality rating of 150, setting a strength of 50% will update the " +
                "item's attack speed to if it had a rating of 75, and 200% results in a rating of 300. " +
                "The second strength determines the minimum value of the attribute compared to its default. If this value " +
                "is 0.5 for example, a base attack speed of 1.6 cannot be reduced to anything below 0.8 and will always be 0.8" +
                " or higher. This is to make sure low skill levels don't suffer too much of a penalty when crafting such recipes");
        this.displayName = Utils.chat("&c&lUpdate Attack Speed");
        this.icon = Material.GOLDEN_SWORD;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<percentage_quality>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        int quality = SmithingItemTreatmentManager.getInstance().getItemsQuality(outputItem);
        int finalQuality = (int) Math.round((strength / 100D) * quality);
        SmithingItemTreatmentManager.getInstance().applyAttributeScaling(outputItem, finalQuality, "GENERIC_ATTACK_SPEED", strength2);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's attack speed to be &e%s%%&7 efficient with its quality score. " +
                "This value will at least be &e%.2fx&7 the attribute's default strength", strength, strength2));
    }
}
