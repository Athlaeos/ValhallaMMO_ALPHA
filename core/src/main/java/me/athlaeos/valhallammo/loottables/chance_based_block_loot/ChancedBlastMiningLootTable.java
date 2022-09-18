package me.athlaeos.valhallammo.loottables.chance_based_block_loot;

import me.athlaeos.valhallammo.loottables.ChancedBlockLootEntry;
import me.athlaeos.valhallammo.loottables.ChancedBlockLootTable;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.mining.MiningSkill;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;

public class ChancedBlastMiningLootTable extends ChancedBlockLootTable {
    @Override
    public String getName() {
        return "mining_blast";
    }

    @Override
    public Material getIcon() {
        return Material.TNT;
    }

    @Override
    public String getDescription() {
        return "&7Loot table responsible for blast mining drops. " +
                "Drop chances are multiplied by the player's blast mining rare drops multiplier stat from their Mining skill. " +
                "Blocks affected are limited to block types that give Mining experience";
    }

    @Override
    public String getDisplayName() {
        return "&7Blast Mining Loot Table";
    }

    @Override
    public Collection<Material> getCompatibleMaterials() {
        Skill s = SkillProgressionManager.getInstance().getSkill("MINING");
        if (s != null){
            if (s instanceof MiningSkill){
                return ((MiningSkill) s).getBlockDropEXPReward().keySet();
            }
        }
        return new HashSet<>();
    }

    /**
     * to be called when a block is destroyed by an explosion
     * @param b the block that was destroyed
     * @param player the player responsible for the destruction
     * @return true if a custom drop was dropped as a result from this method, false if no custom drop was dropped
     */
    public boolean onBlockExplode(Block b, Player player, int fortuneLevel){
        double rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("MINING_BLAST_RARE_DROP_CHANCE_MULTIPLIER", player, true);
        Collection<ChancedBlockLootEntry> entries = getRandomEntries(b.getState(), rareDropMultiplier);
        boolean dropped = false;
        for (ChancedBlockLootEntry e : entries) {
            ItemStack item = entryToItem(e, player);
            if (item == null) continue;
            Location dropLocation = b.getLocation();
            if (dropLocation.getWorld() == null) continue;

            if (e.isOverwriteNaturalDrops()){
                b.setType(Material.AIR);
            } else {
                Utils.explodeBlock(player, b, false, fortuneLevel);
            }
            b.getWorld().dropItemNaturally(b.getLocation(), item);
            dropped = true;
        }
        return dropped;

//        double rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("MINING_BLAST_RARE_DROP_CHANCE_MULTIPLIER", player, true);
//        for (Block b : new HashSet<>(event.blockList())){
//            if (b.getType().isAir()) continue;
//            BlockStore.setBreakReason(b, BlockStore.BreakReason.EXPLOSION);
//            event.blockList().remove(b);
//            ChancedLootEntry entry = getRandomEntry(b.getState(), rareDropMultiplier);
//            if (entry != null && !BlockStore.isPlaced(b)){
//                ItemStack item = entryToItem(entry, player);
//                if (item == null) {
//                    return;
//                }
//
//                if (entry.isOverwriteNaturalDrops()){
//                    event.blockList().remove(b);
//                    b.setType(Material.AIR);
//                } else {
//                    Utils.explodeBlock(player, b, false);
//                }
//                Location dropLocation = b.getLocation().add(0.5, 0.5, 0.5);
//                if (dropLocation.getWorld() == null) return;
//                dropLocation.getWorld().dropItemNaturally(dropLocation, item);
//            } else {
//                Utils.explodeBlock(player, b, false);
//            }
//        }
    }
}
