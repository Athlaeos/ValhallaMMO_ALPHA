package me.athlaeos.valhallammo.loottables.tiered_loot_tables;

import me.athlaeos.valhallammo.loottables.TieredLootEntry;
import me.athlaeos.valhallammo.loottables.TieredLootTable;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

public class TieredFishingLootTable extends TieredLootTable {
    @Override
    public String getName() {
        return "farming_fishing";
    }

    @Override
    public Material getIcon() {
        return Material.FISHING_ROD;
    }

    @Override
    public String getDescription() {
        return "&7Loot table responsible for fishing drops. The available drops are equal to fisher's current fishing tier stat (example: tier 1.7 will have a 70% chance to use tier 2 and 30% tier 1)";
    }

    @Override
    public String getDisplayName() {
        return "&9Fishing Loot Table";
    }

    public void onFishEvent(PlayerFishEvent e){
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (e.getCaught() == null) return;
        if (e.getCaught() instanceof Item){
            double fishingTier = AccumulativeStatManager.getInstance().getStats("FARMING_FISHING_REWARD_TIER", e.getPlayer(), true);
            Item item = (Item) e.getCaught();
            int maxTier = Collections.max(getAllLootEntries().values().stream().map(TieredLootEntry::getTier).collect(Collectors.toSet()));
            Collection<TieredLootEntry> availableEntries = getAvailableEntries(e.getCaught().getLocation(), Math.min(fishingTier, maxTier));
            TieredLootEntry entry = pickRandomEntry(availableEntries);
            if (entry != null){
                ItemStack i = entryToItem(entry, e.getPlayer(), true);
                item.setItemStack(i);
            }
        }
    }
}
