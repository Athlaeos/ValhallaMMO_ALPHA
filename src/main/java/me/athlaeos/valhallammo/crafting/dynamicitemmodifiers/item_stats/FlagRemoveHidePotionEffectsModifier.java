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

public class FlagRemoveHidePotionEffectsModifier extends DynamicItemModifier {
    public FlagRemoveHidePotionEffectsModifier(String name, double strength, ModifierPriority priority) {
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
        this.description = Utils.chat("&7Removes the HIDE_POTION_EFFECTS flag from the item, making vanilla potion" +
                " effects visible again.");
        this.displayName = Utils.chat("&7&lRemove Flag: &e&lHide Potion Effects");
        this.icon = Material.PAPER;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        meta.removeItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        outputItem.setItemMeta(meta);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Removes the &eHIDE_POTION_EFFECTS &7flag from the item.");
    }
}
