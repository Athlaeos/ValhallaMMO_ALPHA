package me.athlaeos.valhallammo.loottables.chance_based_block_loot;

import me.athlaeos.valhallammo.loottables.ChancedBlockLootEntry;
import me.athlaeos.valhallammo.loottables.ChancedBlockLootTable;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.landscaping.LandscapingSkill;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;

public class ChancedWoodstrippingLootTable extends ChancedBlockLootTable {
    @Override
    public String getName() {
        return "landscaping_woodstripping";
    }

    @Override
    public Material getIcon() {
        return Material.STRIPPED_OAK_LOG;
    }

    @Override
    public String getDescription() {
        return "&7Loot table responsible for stripped logs. Drop chances are multiplied by the player's wood stripping " +
                "rare drops multiplier stat from their Landscaping skill. " +
                "Blocks affected are limited to block types that give Landscaping experience";
    }

    @Override
    public String getDisplayName() {
        return "&7Woodstripping Loot Table";
    }

    @Override
    public Collection<Material> getCompatibleMaterials() {
        Skill s = SkillProgressionManager.getInstance().getSkill("LANDSCAPING");
        if (s != null){
            if (s instanceof LandscapingSkill){
                return ((LandscapingSkill) s).getWoodcuttingStripEXPReward().keySet();
            }
        }
        return new HashSet<>();
    }

    public void onLogStripped(BlockPlaceEvent event){
        double rareDropMultiplier = AccumulativeStatManager.getInstance().getStats("WOODSTRIPPING_RARE_DROP_CHANCE_MULTIPLIER", event.getPlayer(), true);
        Collection<ChancedBlockLootEntry> entries = getRandomEntries(event.getBlockReplacedState(), rareDropMultiplier);
        for (ChancedBlockLootEntry e : entries) {
            ItemStack item = entryToItem(e, event.getPlayer());
            if (item == null) return;
            Location pLoc = event.getPlayer().getLocation();
            Location dropLocation = event.getBlockReplacedState().getBlock().getRelative(pLoc.getBlockX(), event.getBlock().getLocation().getBlockY(), pLoc.getBlockZ()).getLocation();
            if (dropLocation.getWorld() == null) return;

            dropLocation.getWorld().dropItemNaturally(dropLocation, item);
        }
    }
}
