package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FlagRemoveHideAttributeModifier extends DynamicItemModifier {
    public FlagRemoveHideAttributeModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Removes the HIDE_ATTRIBUTES flag from the item, making attribute " +
                "modifiers and custom potion effects visible again.");
        this.displayName = Utils.chat("&7&lRemove Flag: &e&lHide Attributes");
        this.icon = Material.PAPER;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        meta.removeItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        outputItem.setItemMeta(meta);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Removes the &eHIDE_ATTRIBUTES &7flag from the item.");
    }
}
