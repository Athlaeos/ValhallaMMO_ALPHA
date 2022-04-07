package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetToolIdModifier extends DynamicItemModifier implements Cloneable{

    public SetToolIdModifier(String name, double strength, ModifierPriority priority) {
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
        this.description = Utils.chat("&7Sets the item's tool ID, this then allows the item" +
                " to be used as a tool to craft items with if the recipe requires it.");
        this.displayName = Utils.chat("&b&lSet Tool ID");
        this.icon = Material.IRON_HOE;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        SmithingItemTreatmentManager.getInstance().setItemsToolId(outputItem, (int) strength);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's tool ID to &e%s&7.", strength));
    }
}
