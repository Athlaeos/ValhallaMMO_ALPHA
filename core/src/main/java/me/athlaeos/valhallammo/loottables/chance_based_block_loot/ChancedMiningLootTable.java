package me.athlaeos.valhallammo.loottables.chance_based_block_loot;

import me.athlaeos.valhallammo.loottables.ChancedBlockLootTable;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.mining.MiningSkill;
import org.bukkit.Material;

import java.util.Collection;
import java.util.HashSet;

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
        return "&7Loot table responsible for mining drops. " +
                "Drop chances are multiplied by the player's mining rare drops multiplier stat from their Mining skill. " +
                "Blocks affected are limited to block types that give Mining experience";
    }

    @Override
    public String getDisplayName() {
        return "&7Mining Loot Table";
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
}
