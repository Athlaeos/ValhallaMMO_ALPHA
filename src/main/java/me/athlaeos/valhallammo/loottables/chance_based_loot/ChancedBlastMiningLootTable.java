package me.athlaeos.valhallammo.loottables.chance_based_loot;

import me.athlaeos.valhallammo.events.BlockDropItemStackEvent;
import me.athlaeos.valhallammo.loottables.ChancedBlockLootTable;
import me.athlaeos.valhallammo.loottables.ChancedLootEntry;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class ChancedMiningLootTable extends ChancedBlockLootTable {
    @Override
    public String getName() {
        return "mining_mining";
    }

    @Override
    public Material getIcon() {
        return Material.DIAMOND_PICKAXE;
    }

    @Override
    public String getDescription() {
        return "&7Loot table responsible for mining drops";
    }

    @Override
    public String getDisplayName() {
        return "&7Mining Loot Table";
    }

    public void onItemDrop(BlockDropItemEvent event){
        double rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("MINING_MINING_RARE_DROP_CHANCE_MULTIPLIER", event.getPlayer(), true);
        ChancedLootEntry entry = getRandomEntry(event.getBlockState(), rareDropMultiplier);
        if (entry != null){
            ItemStack item = entryToItem(entry, event.getPlayer());
            if (item == null) {
                return;
            }
            if (entry.isOverwriteNaturalDrops()){
                event.getItems().clear();
            }
            Location dropLocation = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
            if (dropLocation.getWorld() == null) {
                return;
            }
            event.getItems().add(dropLocation.getWorld().dropItem(dropLocation, item));
        }
    }

    public void onItemStackDrop(BlockDropItemStackEvent event){
        double rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("MINING_MINING_RARE_DROP_CHANCE_MULTIPLIER", event.getPlayer(), true);
        ChancedLootEntry entry = getRandomEntry(event.getBlockState(), rareDropMultiplier);
        if (entry != null){
            ItemStack item = entryToItem(entry, event.getPlayer());
            if (item == null) {
                return;
            }
            if (entry.isOverwriteNaturalDrops()){
                event.getItems().clear();
            }
            Location dropLocation = event.getBlock().getLocation().add(0.5, 0.5, 0.5);
            if (dropLocation.getWorld() == null) {
                return;
            }
            event.getItems().add(item);
        }
    }
}
