package me.athlaeos.valhallammo.loottables;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public abstract class ChancedEntityLootTable {
    private final Map<String, ChancedEntityLootEntry> allLootEntries = new HashMap<>();
    private final Map<EntityType, Collection<ChancedEntityLootEntry>> entityLootTables = new HashMap<>();

    public abstract String getName();
    public abstract Material getIcon();
    public abstract String getDescription();
    public abstract String getDisplayName();
    public abstract Collection<EntityType> getCompatibleEntities();

    public void registerEntry(ChancedEntityLootEntry entry){
        if (entry.getLoot() == null || entry.getName() == null) {
            return;
        }
        if (allLootEntries.containsKey(entry.getName())) {
            return;
        }
        allLootEntries.put(entry.getName(), entry);

        Collection<ChancedEntityLootEntry> existingBlockLootEntries = entityLootTables.get(entry.getEntity());
        if (existingBlockLootEntries == null) existingBlockLootEntries = new HashSet<>();
        existingBlockLootEntries.add(entry);
        entityLootTables.put(entry.getEntity(), existingBlockLootEntries);
    }

    public void unRegisterEntry(ChancedEntityLootEntry entry){
        allLootEntries.remove(entry.getName());
        for (EntityType block : entityLootTables.keySet()){
            Collection<ChancedEntityLootEntry> existingLootTables = entityLootTables.get(block);
            boolean removed = existingLootTables.remove(entry);
            if (removed){
                entityLootTables.put(block, existingLootTables);
                break;
            }
        }
        ConfigManager.getInstance().getConfig("loot_tables/" + this.getName() + ".yml").get().set("entries." + entry.getName(), null);
        ConfigManager.getInstance().getConfig("loot_tables/" + this.getName() + ".yml").save();
    }

    public void unRegisterEntry(String entry){
        ChancedEntityLootEntry e = allLootEntries.get(entry);
        if (e == null) return;
        allLootEntries.remove(entry);
        for (EntityType block : entityLootTables.keySet()){
            Collection<ChancedEntityLootEntry> existingLootTables = entityLootTables.get(block);
            boolean removed = existingLootTables.remove(e);
            if (removed){
                entityLootTables.put(block, existingLootTables);
                break;
            }
        }
        ConfigManager.getInstance().getConfig("loot_tables/" + this.getName() + ".yml").get().set("entries." + entry, null);
        ConfigManager.getInstance().getConfig("loot_tables/" + this.getName() + ".yml").save();
    }

    public Map<String, ChancedEntityLootEntry> getAllLootEntries() {
        return allLootEntries;
    }

    public Map<EntityType, Collection<ChancedEntityLootEntry>> getEntityLootTables() {
        return entityLootTables;
    }

    public Collection<ChancedEntityLootEntry> getRandomEntry(Entity entity, double multiplier){
        Collection<ChancedEntityLootEntry> entries = new HashSet<>();
        if (entityLootTables.get(entity.getType()) == null) {
            return entries;
        }
        for (ChancedEntityLootEntry e : entityLootTables.get(entity.getType())){
            if (e.getEntity() != entity.getType()) {
                continue;
            }
            if (!e.getBiomeFilter().isEmpty()){
                if (!e.getBiomeFilter().contains(entity.getLocation().getBlock().getBiome())) {
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

    public ItemStack entryToItem(ChancedEntityLootEntry entry, Player player){
        List<DynamicItemModifier> modifiers = new ArrayList<>(entry.getModifiers());
        modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));

        ItemStack result = entry.getLoot().clone();
        for (DynamicItemModifier modifier : modifiers){
            if (result == null) break;
            result = modifier.processItem(player, result);
        }
        return result;
    }

    public ItemStack entryToItem(ChancedEntityLootEntry entry){
        List<DynamicItemModifier> modifiers = new ArrayList<>(entry.getModifiers());
        modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));

        ItemStack result = entry.getLoot().clone();
        for (DynamicItemModifier modifier : modifiers){
            if (result == null) break;
            result = modifier.processItem(null, result);
        }
        return result;
    }

    public void onEntityKilled(LivingEntity killed, Collection<ItemStack> drops, double multiplier){
        Player killer = killed.getKiller();
        if (killer == null) return;
        Collection<ChancedEntityLootEntry> entries = getRandomEntry(killed, multiplier);
        Collection<ItemStack> extraDrops = new HashSet<>();
        for (ChancedEntityLootEntry e : entries){
            ItemStack item = entryToItem(e, killer);
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
