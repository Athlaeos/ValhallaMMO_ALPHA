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

import java.util.ArrayList;
import java.util.List;

public class ChangeItemToDictionaryItemKeepingAmountModifier extends DynamicItemModifier  {

    public ChangeItemToDictionaryItemKeepingAmountModifier(String name) {
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
        this.description = Utils.chat("&7Sets an item to one of the indexed items in the item index, accessible with /val itemindex. " +
                "This changes the item completely, so any previous properties &eexcept for amount&7 are lost. Keep good track of the index, as changing an item " +
                "in it can change the outcome of this recipe. ");
        this.displayName = Utils.chat("&a&lSet To Indexed Item, keeping amount");
        this.icon = Material.WRITABLE_BOOK;
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
        int originalAmount = outputItem.getAmount();
        ItemStack newItem = ItemDictionaryManager.getInstance().getItemDictionary().get((int) strength);
        if (newItem != null){
            newItem = newItem.clone();
            newItem.setAmount(originalAmount);
        }
        return newItem;
    }

    @Override
    public String toString() {
        ItemStack indexedItem = ItemDictionaryManager.getInstance().getItemDictionary().get((int) strength);
        if (Utils.isItemEmptyOrNull(indexedItem)){
            return Utils.chat("&7This recipe will not work, as the given ID &e" + ((int) strength) + " &7is not associated with an item. ");
        } else {
            return Utils.chat(String.format("&7Changes the item to &e%s&7. " +
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
