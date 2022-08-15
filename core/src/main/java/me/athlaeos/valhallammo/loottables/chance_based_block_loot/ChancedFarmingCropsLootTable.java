package me.athlaeos.valhallammo.loottables.chance_based_block_loot;

import me.athlaeos.valhallammo.loottables.ChancedBlockLootTable;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.farming.FarmingSkill;
import org.bukkit.Material;

import java.util.Collection;
import java.util.HashSet;

public class ChancedFarmingCropsLootTable extends ChancedBlockLootTable {
    @Override
    public String getName() {
        return "farming_farming";
    }

    @Override
    public Material getIcon() {
        return Material.WHEAT;
    }

    @Override
    public String getDescription() {
        return "&7Loot table responsible for farming drops. Drop chances are multiplied by the player's crop rare " +
                "drops multiplier stat from their farming skill. " +
                "Blocks affected are limited to block types that give Farming experience";
    }

    @Override
    public String getDisplayName() {
        return "&7Farming Loot Table";
    }

    @Override
    public Collection<Material> getCompatibleMaterials() {
        Skill s = SkillProgressionManager.getInstance().getSkill("FARMING");
        if (s != null){
            if (s instanceof FarmingSkill){
                return ((FarmingSkill) s).getBlockBreakEXPReward().keySet();
            }
        }
        return new HashSet<>();
    }
}
