package me.athlaeos.valhallammo.loottables;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class TieredLootTable {
    private final Map<String, TieredLootEntry> allLootEntries = new HashMap<>();

    public abstract String getName();
    public abstract Material getIcon();
    public abstract String getDescription();
    public abstract String getDisplayName();

    public void registerEntry(TieredLootEntry entry){
        if (entry.getLoot() == null || entry.getName() == null) return;
        DynamicItemModifier.sortModifiers(entry.getModifiers());
        allLootEntries.put(entry.getName(), entry);
    }

    public void unRegisterEntry(String entry){
        allLootEntries.remove(entry);

        ConfigManager.getInstance().getConfig("loot_tables/" + this.getName() + ".yml").get().set("entries." + entry, null);
        ConfigManager.getInstance().getConfig("loot_tables/" + this.getName() + ".yml").save();
    }

    public Map<String, TieredLootEntry> getAllLootEntries() {
        return allLootEntries;
    }

    public Collection<TieredLootEntry> getAvailableEntries(Location l, double tier){
        Collection<TieredLootEntry> availableEntries = new HashSet<>();
        int tierToPick = Utils.excessChance(tier);
        for (TieredLootEntry e : allLootEntries.values()){
            if (!e.getBiomeFilter().isEmpty()){
                if (!e.getBiomeFilter().contains(l.getBlock().getBiome())) continue;
            }

//            if (!e.getRegionFilter().isEmpty()){
//                // check if location is inside region
//            }
            if (e.getTier() == tierToPick){
                availableEntries.add(e);
            }
        }
        return availableEntries;
    }

    public TieredLootEntry pickRandomEntry(Collection<TieredLootEntry> availableEntries){
        if (availableEntries.isEmpty()) return null;
        int combinedWeight = 0;
        for (TieredLootEntry e : availableEntries){
            combinedWeight += e.getWeight();
        }

        if (combinedWeight == 0) return null;
        int randomInt = Utils.getRandom().nextInt(combinedWeight);
        for (TieredLootEntry e : availableEntries){
            randomInt -= e.getWeight();
            if (randomInt < 0) return e;
        }
        return null;
    }

    public ItemStack entryToItem(TieredLootEntry entry, Player player, boolean use){
        if (entry.getLoot() == null) return null;
        ItemStack result = entry.getLoot().clone();
        result = DynamicItemModifier.modify(result, player, entry.getModifiers(), false, use, true);
        //List<DynamicItemModifier> modifiers = new ArrayList<>(entry.getModifiers());
        //modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
        //for (DynamicItemModifier modifier : modifiers){
        //    modifier.setUse(use);
        //    if (result == null) break;
        //    result = modifier.processItem(player, result);
        //}
        return result;
    }

    public ItemStack entryToItem(TieredLootEntry entry, boolean use){
        return entryToItem(entry, null, use);
    }
}
