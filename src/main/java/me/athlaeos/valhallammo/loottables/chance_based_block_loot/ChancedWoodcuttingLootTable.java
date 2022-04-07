package me.athlaeos.valhallammo.loottables.chance_based_block_loot;

import me.athlaeos.valhallammo.loottables.ChancedBlockLootTable;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.landscaping.LandscapingSkill;
import org.bukkit.Material;

import java.util.Collection;
import java.util.HashSet;

public class ChancedWoodcuttingLootTable extends ChancedBlockLootTable {
    @Override
    public String getName() {
        return "landscaping_woodcutting";
    }

    @Override
    public Material getIcon() {
        return Material.OAK_LOG;
    }

    @Override
    public String getDescription() {
        return "&7Loot table responsible for broken logs. " +
                "Drop chances are multiplied by the player's woodcutting rare drops multiplier stat from their " +
                "landscaping skill. Blocks affected are limited to block types that give Landscaping experience";
    }

    @Override
    public String getDisplayName() {
        return "&7Woodcutting Loot Table";
    }

    @Override
    public Collection<Material> getCompatibleMaterials() {
        Skill s = SkillProgressionManager.getInstance().getSkill("LANDSCAPING");
        if (s != null){
            if (s instanceof LandscapingSkill){
                return ((LandscapingSkill) s).getWoodcuttingBreakEXPReward().keySet();
            }
        }
        return new HashSet<>();
    }
}
