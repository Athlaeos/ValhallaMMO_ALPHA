package me.athlaeos.valhallammo.loottables;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class ChancedAdditionalLootTable {
    private final Map<String, ChancedLootEntry> allLootEntries = new HashMap<>();

    public abstract String getName();
    public abstract Material getIcon();
    public abstract String getDescription();
    public abstract String getDisplayName();

    public void registerEntry(ChancedLootEntry entry){
        if (entry.getLoot() == null || entry.getName() == null) return;
        allLootEntries.put(entry.getName(), entry);
    }

    public void unRegisterEntry(String entry){
        allLootEntries.remove(entry);
    }

    public Map<String, ChancedLootEntry> getAllLootEntries() {
        return allLootEntries;
    }

    public ChancedLootEntry getRandomEntry(Location l, double multiplier){
        List<ChancedLootEntry> shuffledEntries = new ArrayList<>(allLootEntries.values());
        Collections.shuffle(shuffledEntries);
        for (ChancedLootEntry e : shuffledEntries){
            if (!e.getBiomeFilter().isEmpty()){
                if (!e.getBiomeFilter().contains(l.getBlock().getBiome())) continue;
            }
            double finalChance = e.getChance() * multiplier;

//            if (!e.getRegionFilter().isEmpty()){
//                // check if location is inside region
//            }
            if (Utils.getRandom().nextDouble() <= finalChance){
                return e;
            }
        }
        return null;
    }

    public ItemStack entryToItem(ChancedLootEntry entry, Player player){
        List<DynamicItemModifier> modifiers = new ArrayList<>(entry.getModifiers());
        modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));

        ItemStack result = entry.getLoot();
        for (DynamicItemModifier modifier : modifiers){
            if (result == null) break;
            modifier.processItem(player, result);
        }
        return result;
    }

    public ItemStack entryToItem(ChancedLootEntry entry){
        List<DynamicItemModifier> modifiers = new ArrayList<>(entry.getModifiers());
        modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));

        ItemStack result = entry.getLoot();
        for (DynamicItemModifier modifier : modifiers){
            if (result == null) break;
            modifier.processItem(null, result);
        }
        return result;
    }
}
