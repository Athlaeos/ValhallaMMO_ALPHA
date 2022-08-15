package me.athlaeos.valhallammo.loottables.chance_based_block_loot;

import me.athlaeos.valhallammo.loottables.ChancedBlockLootTable;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.landscaping.LandscapingSkill;
import org.bukkit.Material;

import java.util.Collection;
import java.util.HashSet;

public class ChancedDiggingLootTable extends ChancedBlockLootTable {
    @Override
    public String getName() {
        return "landscaping_digging";
    }

    @Override
    public Material getIcon() {
        return Material.DIRT;
    }

    @Override
    public String getDescription() {
        return "&7Loot table responsible for broken diggable blocks. Drop chances are multiplied by the player's " +
                "digging rare drops multiplier stat from their Landscaping skill. " +
                "Blocks affected are limited to block types that give Landscaping experience";
    }

    @Override
    public String getDisplayName() {
        return "&7Digging Loot Table";
    }

    @Override
    public Collection<Material> getCompatibleMaterials() {
        Skill s = SkillProgressionManager.getInstance().getSkill("LANDSCAPING");
        if (s != null){
            if (s instanceof LandscapingSkill){
                return ((LandscapingSkill) s).getDiggingBreakEXPReward().keySet();
            }
        }
        return new HashSet<>();
    }
}
