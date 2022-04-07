package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class SetAmountModifier extends DynamicItemModifier implements Cloneable{

    public SetAmountModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 1;
        this.minStrength = 1;
        this.maxStrength = 64D;
        this.description = Utils.chat("&7Sets the amount of the item stack. Cancels recipe is the stack already has this amount.");
        this.displayName = Utils.chat("&b&lSet Amount");
        this.icon = Material.BARREL;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (outputItem.getAmount() == (int) strength) return null;
        outputItem.setAmount((int) strength);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's amount to &e%s&7.", strength));
    }
}
