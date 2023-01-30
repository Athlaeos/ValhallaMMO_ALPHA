package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.ItemDictionaryManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ChangeItemNameToDictionaryItemNameModifier extends DynamicItemModifier  {

    public ChangeItemNameToDictionaryItemNameModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 3D;
        this.bigStepIncrease = 3D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = Integer.MAX_VALUE;
        this.description = Utils.chat("&7Sets the item's name to that of one of the indexed items in the item index, accessible with /val itemindex. " +
                "This only changes the item display name, and keeps its other properties intact. If no entry with the given ID exists " +
                "the recipe is cancelled. If the indexed item has no display name, it is instead removed off the item. " +
                "Keep good track of the index, as changing an item in it can change the outcome of this recipe. ");
        this.displayName = Utils.chat("&a&lSet Name To Indexed Item Name");
        this.icon = Material.NAME_TAG;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        List<String> suggestions = new ArrayList<>();
        for (int i : ItemDictionaryManager.getInstance().getItemDictionary().keySet()){
            ItemStack item = ItemDictionaryManager.getInstance().getItemDictionary().get(i);
            if (Utils.isItemEmptyOrNull(item)) continue;
            suggestions.add(i + "-" + ChatColor.stripColor(Utils.getItemName(item).replace(" ", "")));
        }
        return suggestions;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        ItemStack index = ItemDictionaryManager.getInstance().getItemDictionary().get((int) strength);
        if (Utils.isItemEmptyOrNull(index)) return null;
        ItemMeta indexMeta = index.getItemMeta();
        if (indexMeta == null) return null;
        ItemMeta outputMeta = outputItem.getItemMeta();
        if (outputMeta == null) return null;
        if (indexMeta.hasDisplayName()){
            outputMeta.setDisplayName(Utils.chat(indexMeta.getDisplayName()));
        } else {
            outputMeta.setDisplayName(null);
        }
        outputItem.setItemMeta(outputMeta);
        return outputItem;
    }

    @Override
    public String toString() {
        ItemStack indexedItem = ItemDictionaryManager.getInstance().getItemDictionary().get((int) strength);
        if (Utils.isItemEmptyOrNull(indexedItem)){
            return Utils.chat("&7This recipe will not work, as the given ID &e" + ((int) strength) + " &7is not associated with an item. ");
        } else {
            return Utils.chat(String.format("&7Changes the item's name to &e%s&7. " +
                    "-n -n" + String.join(" -n", getSuggestions()), Utils.getItemName(indexedItem)));
        }
    }

    private List<String> previousSuggestions = new ArrayList<>();
    private List<String> getSuggestions(){
        List<String> suggestions = new ArrayList<>();
        int selectedItem = (int) strength;
        int selectedLineIndex = 0;
        if (ItemDictionaryManager.getInstance().getItemDictionary().get(selectedItem) == null) return previousSuggestions;
        int index = 0;
        for (int i : ItemDictionaryManager.getInstance().getItemDictionary().keySet()) {
            ItemStack item = ItemDictionaryManager.getInstance().getItemDictionary().get(i);
            if (Utils.isItemEmptyOrNull(item)) continue;
            String line = i + " : " + Utils.getItemName(item);
            if (i == selectedItem) {
                selectedLineIndex = index;
                suggestions.add(Utils.chat("&e" + ChatColor.stripColor(line)));
            } else {
                suggestions.add(line);
            }
            index++;
        }
        List<String> newSuggestions = new ArrayList<>(suggestions);
        for (int i = -3; i < 3; i++){
            if (selectedLineIndex + i < 0 || selectedLineIndex + i >= suggestions.size()) continue;
            newSuggestions.add(suggestions.get(selectedLineIndex + i));
        }
        previousSuggestions = newSuggestions;
        return newSuggestions;
    }
}
