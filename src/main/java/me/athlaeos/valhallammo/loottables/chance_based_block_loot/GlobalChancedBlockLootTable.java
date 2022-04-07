package me.athlaeos.valhallammo.loottables.chance_based_block_loot;

import me.athlaeos.valhallammo.loottables.ChancedBlockLootTable;
import org.bukkit.Material;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class GlobalChancedBlockLootTable extends ChancedBlockLootTable {
    @Override
    public String getName() {
        return "global_blocks";
    }

    @Override
    public Material getIcon() {
        return Material.DIAMOND_ORE;
    }

    @Override
    public String getDescription() {
        return "&7Loot table responsible for custom block drops. Drop chances are not multiplied by any of the player's stats. " +
                "Affected blocks are not limited to a selection";
    }

    @Override
    public String getDisplayName() {
        return "&7Global Block Loot Table";
    }

    @Override
    public Collection<Material> getCompatibleMaterials() {
        return new HashSet<>(Arrays.asList(Material.values()));
    }
}
