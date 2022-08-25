package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.TreeMap;

public class ItemDictionaryManager {
    private static boolean shouldSaveItems = false;

    public static void shouldSaveItems() {
        shouldSaveItems = true;
    }

    public static boolean isShouldSaveItems(){
        return shouldSaveItems;
    }
    private static ItemDictionaryManager manager = null;

    private final TreeMap<Integer, ItemStack> itemDictionary = new TreeMap<>();

    public static ItemDictionaryManager getInstance(){
        if (manager == null) manager = new ItemDictionaryManager();
        return manager;
    }
    public void saveItemsAsync(){
        ValhallaMMO.getPlugin().getServer().getScheduler().runTaskAsynchronously(ValhallaMMO.getPlugin(), this::saveItems);
    }

    public void saveItems(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("item_dictionary.yml").get();
        config.set("items", null);
        for (Integer i : itemDictionary.keySet()){
            ItemStack item = itemDictionary.get(i);
            if (Utils.isItemEmptyOrNull(item)) continue;
            config.set("items." + i, item);
        }
        ConfigManager.getInstance().saveConfig("item_dictionary.yml");
    }

    public void loadItemsAsync(){
        ValhallaMMO.getPlugin().getServer().getScheduler().runTaskAsynchronously(ValhallaMMO.getPlugin(), this::loadItemDictionary);
    }

    public Map<Integer, ItemStack> getItemDictionary() {
        return itemDictionary;
    }

    public int addItem(ItemStack i){
        shouldSaveItems();
        int id = getNextAvailableID();
        itemDictionary.put(id, i);
        return id;
    }

    public int getNextAvailableID(){
        if (itemDictionary.isEmpty()) return 0;
        int lastKey = itemDictionary.lastKey();
        for (int i = 0; i < lastKey; i++){
            if (itemDictionary.get(i) == null) return i;
        }
        return lastKey + 1;
    }

    public void removeItem(int id){
        shouldSaveItems();
        itemDictionary.remove(id);
    }

    public void loadItemDictionary(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("item_dictionary.yml").get();
        ConfigurationSection itemSection = config.getConfigurationSection("items.");
        if (itemSection != null){
            for (String id : itemSection.getKeys(false)){
                try {
                    int i = Integer.parseInt(id);
                    ItemStack item = config.getItemStack("items." + id);
                    if (!Utils.isItemEmptyOrNull(item)){
                        item = TranslationManager.getInstance().translateItemStack(item);
                        itemDictionary.put(i, item);
                    } else {
                        ValhallaMMO.getPlugin().getServer().getLogger().warning("Invalid item in item_dictionary.yml: " + id + ", " + (item == null ? "null" : "AIR"));
                    }
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getServer().getLogger().warning("Invalid item id in item_dictionary.yml: " + id);
                }
            }
        }
    }
}
