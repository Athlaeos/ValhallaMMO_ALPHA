package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
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

public class DynamicDamageModifier extends DynamicItemModifier {
    public DynamicDamageModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_VANILLA;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1000D;
        this.description = Utils.chat("&7Updates the item's attack damage value if it has one. The strength of the modifier " +
                "represents the % of the item's quality rating used in determining its attack damage. Example: " +
                "if an item has a quality rating of 150, setting a strength of 50% will update the " +
                "item's attack damage to if it had a rating of 75, and 200% results in a rating of 300.");
        this.displayName = Utils.chat("&c&lUpdate Damage");
        this.icon = Material.IRON_SWORD;
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
        SmithingItemTreatmentManager.getInstance().applyAttributeScaling(outputItem, finalQuality, "GENERIC_ATTACK_DAMAGE");
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's attack damage to be &e%s%%&7 efficient with its quality score.", strength));
    }
}
