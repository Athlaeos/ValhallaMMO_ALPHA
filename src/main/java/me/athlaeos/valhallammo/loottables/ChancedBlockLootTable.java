package me.athlaeos.valhallammo.loottables;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class ChancedBlockLootTable {
    private final Map<String, ChancedBlockLootEntry> allLootEntries = new HashMap<>();
    private final Map<Material, Collection<ChancedBlockLootEntry>> blockLootTables = new HashMap<>();

    public abstract String getName();
    public abstract Material getIcon();
    public abstract String getDescription();
    public abstract String getDisplayName();

    public abstract Collection<Material> getCompatibleMaterials();

    public void registerEntry(ChancedBlockLootEntry entry){
        if (entry.getLoot() == null || entry.getName() == null) {
            return;
        }
        if (allLootEntries.containsKey(entry.getName())) {
            return;
        }
        allLootEntries.put(entry.getName(), entry);

        Collection<ChancedBlockLootEntry> existingBlockLootEntries = blockLootTables.get(entry.getBlock());
        if (existingBlockLootEntries == null) existingBlockLootEntries = new HashSet<>();
        existingBlockLootEntries.add(entry);
        blockLootTables.put(entry.getBlock(), existingBlockLootEntries);
    }

    public void unRegisterEntry(ChancedBlockLootEntry entry){
        allLootEntries.remove(entry.getName());
        for (Material block : blockLootTables.keySet()){
            Collection<ChancedBlockLootEntry> existingLootTables = blockLootTables.get(block);
            boolean removed = existingLootTables.remove(entry);
            if (removed){
                blockLootTables.put(block, existingLootTables);
                break;
            }
        }
        ConfigManager.getInstance().getConfig("loot_tables/" + this.getName() + ".yml").get().set("entries." + entry.getName(), null);
        ConfigManager.getInstance().getConfig("loot_tables/" + this.getName() + ".yml").save();
    }

    public void unRegisterEntry(String entry){
        ChancedBlockLootEntry e = allLootEntries.get(entry);
        if (e == null) return;
        allLootEntries.remove(entry);
        for (Material block : blockLootTables.keySet()){
            Collection<ChancedBlockLootEntry> existingLootTables = blockLootTables.get(block);
            boolean removed = existingLootTables.remove(e);
            if (removed){
                blockLootTables.put(block, existingLootTables);
                break;
            }
        }
        ConfigManager.getInstance().getConfig("loot_tables/" + this.getName() + ".yml").get().set("entries." + entry, null);
        ConfigManager.getInstance().getConfig("loot_tables/" + this.getName() + ".yml").save();
    }

    public Map<String, ChancedBlockLootEntry> getAllLootEntries() {
        return allLootEntries;
    }

    public Map<Material, Collection<ChancedBlockLootEntry>> getBlockLootTables() {
        return blockLootTables;
    }

    public Collection<ChancedBlockLootEntry> getRandomEntries(BlockState b, double multiplier){
        Collection<ChancedBlockLootEntry> entries = new HashSet<>();
        if (blockLootTables.get(b.getType()) == null) {
            return entries;
        }
        for (ChancedBlockLootEntry e : blockLootTables.get(b.getType())){
            if (e.getBlock() != b.getType()) {
                continue;
            }
            if (!e.getBiomeFilter().isEmpty()){
                if (!e.getBiomeFilter().contains(b.getBlock().getBiome())) {
                    continue;
                }
            }
            double finalChance = e.getChance() * multiplier;

//            if (!e.getRegionFilter().isEmpty()){
//                // check if location is inside region
//            }
            if (Utils.getRandom().nextDouble() <= finalChance){
                entries.add(e);
            }
        }
        return entries;
    }

    public ItemStack entryToItem(ChancedBlockLootEntry entry, Player player){
        List<DynamicItemModifier> modifiers = new ArrayList<>(entry.getModifiers());
        modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));

        ItemStack result = entry.getLoot().clone();
        for (DynamicItemModifier modifier : modifiers){
            if (result == null) break;
            result = modifier.processItem(player, result);
        }
        return result;
    }

    public ItemStack entryToItem(ChancedBlockLootEntry entry){
        List<DynamicItemModifier> modifiers = new ArrayList<>(entry.getModifiers());
        modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));

        ItemStack result = entry.getLoot().clone();
        for (DynamicItemModifier modifier : modifiers){
            if (result == null) break;
            result = modifier.processItem(null, result);
        }
        return result;
    }

    public void onItemDrop(BlockState broken, Collection<Item> drops, Player who, double multiplier){
        Collection<ChancedBlockLootEntry> entries = getRandomEntries(broken, multiplier);
        Collection<Item> extraDrops = new HashSet<>();
        for (ChancedBlockLootEntry e : entries){
            ItemStack item = entryToItem(e, who);
            if (item == null) {
                continue;
            }
            if (e.isOverwriteNaturalDrops()){
                drops.clear();
            }
            Location dropLocation = broken.getLocation().add(0.5, 0.5, 0.5);
            if (dropLocation.getWorld() == null) {
                continue;
            }
            extraDrops.add(dropLocation.getWorld().dropItem(dropLocation, item));
        }
        drops.addAll(extraDrops);
    }

    public void onItemStackDrop(BlockState broken, Collection<ItemStack> drops, Player who, double multiplier){
        Collection<ChancedBlockLootEntry> entries = getRandomEntries(broken, multiplier);
        Collection<ItemStack> extraDrops = new HashSet<>();
        for (ChancedBlockLootEntry e : entries){
            ItemStack item = entryToItem(e, who);
            if (item == null) {
                continue;
            }
            if (e.isOverwriteNaturalDrops()){
                drops.clear();
            }
            extraDrops.add(item);
        }
        drops.addAll(extraDrops);
    }
}
