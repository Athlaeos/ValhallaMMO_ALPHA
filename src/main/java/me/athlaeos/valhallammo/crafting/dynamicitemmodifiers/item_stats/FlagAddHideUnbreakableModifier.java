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

import java.util.Collections;
import java.util.List;

public class FlagAddHideUnbreakableModifier extends DynamicItemModifier {
    public FlagAddHideUnbreakableModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Adds the HIDE_UNBREAKABLE flag to the item. HIDE_UNBREAKABLE hides " +
                "the \"&9Unbreakable\" &7tag if present.");
        this.displayName = Utils.chat("&7&lAdd Flag: &e&lHide Unbreakable");
        this.icon = Material.PAPER;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
        outputItem.setItemMeta(meta);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Adds the &eHIDE_UNBREAKABLE &7flag to the item. This hides the Unbreakable tag if present.");
    }
}
