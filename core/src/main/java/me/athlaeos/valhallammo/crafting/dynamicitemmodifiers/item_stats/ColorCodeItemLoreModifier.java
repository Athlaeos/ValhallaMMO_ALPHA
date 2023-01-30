package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ColorCodeItemLoreModifier extends DynamicItemModifier  {

    public ColorCodeItemLoreModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Converts any color codes to actual colors in the item's display lore. " +
                "Recipe is cancelled if the item has no lore at all or if the lore didn't change");
        this.displayName = Utils.chat("&d&lColor Code Lore");
        this.icon = Material.NAME_TAG;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        ItemMeta outputMeta = outputItem.getItemMeta();
        if (outputMeta == null) return null;
        if (outputMeta.getLore() != null){
            List<String> lore = outputMeta.getLore().stream().map(Utils::chat).collect(Collectors.toList());
            if (lore.equals(outputMeta.getLore())) return null;
            outputMeta.setLore(lore);
        } else return null;
        outputItem.setItemMeta(outputMeta);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Converts any color codes in the lore to actual colors");
    }
}
