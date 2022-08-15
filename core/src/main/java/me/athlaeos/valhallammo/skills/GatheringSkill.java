package me.athlaeos.valhallammo.skills;

import me.athlaeos.valhallammo.events.BlockDropItemStackEvent;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerHarvestBlockEvent;

public interface GatheringSkill {
    void onBlockBreak(BlockBreakEvent event);

    void onBlockDamage(BlockDamageEvent event);

    void onBlockPlaced(BlockPlaceEvent event);

    void onBlockHarvest(PlayerHarvestBlockEvent event);

    void onItemsDropped(BlockDropItemEvent event);

    void onItemStacksDropped(BlockDropItemStackEvent event);
}
