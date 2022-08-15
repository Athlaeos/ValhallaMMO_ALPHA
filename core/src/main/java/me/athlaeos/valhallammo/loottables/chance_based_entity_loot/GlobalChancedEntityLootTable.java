package me.athlaeos.valhallammo.loottables.chance_based_entity_loot;

import me.athlaeos.valhallammo.loottables.ChancedEntityLootTable;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class GlobalChancedEntityLootTable extends ChancedEntityLootTable {
    @Override
    public String getName() {
        return "global_entities";
    }

    @Override
    public Material getIcon() {
        return Material.PARROT_SPAWN_EGG;
    }

    @Override
    public String getDescription() {
        return "&7Loot table responsible for custom entity drops. Drop chances are not multiplied by any of the player's stats." +
                " Affected entities are not limited to a selection";
    }

    @Override
    public String getDisplayName() {
        return "&7Global Entity Loot Table";
    }

    @Override
    public Collection<EntityType> getCompatibleEntities() {
        return new HashSet<>(Arrays.asList(EntityType.values()));
    }
}
