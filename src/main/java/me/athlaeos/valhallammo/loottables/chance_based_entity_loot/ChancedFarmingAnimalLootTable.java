package me.athlaeos.valhallammo.loottables.chance_based_entity_loot;

import me.athlaeos.valhallammo.loottables.ChancedEntityLootTable;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.farming.FarmingSkill;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Collection;
import java.util.HashSet;

public class ChancedFarmingAnimalLootTable extends ChancedEntityLootTable {
    @Override
    public String getName() {
        return "farming_animals";
    }

    @Override
    public Material getIcon() {
        return Material.BEEF;
    }

    @Override
    public String getDescription() {
        return "&7Loot table responsible for animal drops. " +
                "Drop chances are multiplied by the player's animal rare drops multiplier stat from their farming skill. " +
                "Entities affected are limited to entities that give Farming experience when bred together";
    }

    @Override
    public String getDisplayName() {
        return "&7Animal Loot Table";
    }

    @Override
    public Collection<EntityType> getCompatibleEntities() {
        Skill s = SkillProgressionManager.getInstance().getSkill("FARMING");
        if (s != null){
            if (s instanceof FarmingSkill){
                return ((FarmingSkill) s).getEntityBreedEXPReward().keySet();
            }
        }
        return new HashSet<>();
    }
}
