package me.athlaeos.valhallammo.loottables.chance_based_loot;

import me.athlaeos.valhallammo.loottables.TieredLootEntry;
import me.athlaeos.valhallammo.loottables.TieredLootTable;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.player.PlayerFishEvent;

import java.util.Collection;

public class TieredChancedLootTable extends TieredLootTable {
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
        return "Loot table responsible for fishing drops";
    }

    @Override
    public String getDisplayName() {
        return "&7Fishing Loot Table";
    }

    public void onFishEvent(PlayerFishEvent e){
        if (e.getState() != PlayerFishEvent.State.CAUGHT_FISH) return;
        if (e.getCaught() == null) return;
        if (e.getCaught() instanceof Item){
            double fishingTier = AccumulativeStatManager.getInstance().getStats("FARMING_FISHING_REWARD_TIER", e.getPlayer(), true);
            Item item = (Item) e.getCaught();
            Collection<TieredLootEntry> availableEntries = getAvailableEntries(e.getCaught().getLocation(), fishingTier);
            TieredLootEntry entry = pickRandomEntry(availableEntries);
            if (entry != null){
                item.setItemStack(entryToItem(entry, e.getPlayer()));
            }
        }
    }
}
