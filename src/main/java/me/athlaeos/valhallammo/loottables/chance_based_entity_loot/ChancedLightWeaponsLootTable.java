package me.athlaeos.valhallammo.loottables.chance_based_entity_loot;

import me.athlaeos.valhallammo.loottables.ChancedEntityLootTable;
import me.athlaeos.valhallammo.utility.EntityUtils;
import org.bukkit.Material;
import org.bukkit.entity.EntityType;

import java.util.Collection;
import java.util.HashSet;

public class ChancedLightWeaponsLootTable extends ChancedEntityLootTable {
    private final Collection<EntityType> compatibleEntities;
    public ChancedLightWeaponsLootTable(){
        Collection<EntityType> compatibleEntities = new HashSet<>();
        for (EntityType e : EntityType.values()){
            if (!EntityUtils.EntityClassification.isMatchingClassification(e, EntityUtils.EntityClassification.UNALIVE)){
                compatibleEntities.add(e);
            }
        }
        this.compatibleEntities = compatibleEntities;
    }

    @Override
    public String getName() {
        return "light_weapons";
    }

    @Override
    public Material getIcon() {
        return Material.IRON_SWORD;
    }

    @Override
    public String getDescription() {
        return "&7Loot table responsible for monsters killed with a light weapon. " +
                "Drop chances are multiplied by the player's light weapons drops multiplier stat from their light " +
                "weapons skill. ";
    }

    @Override
    public String getDisplayName() {
        return "&7Light Weapons Table";
    }

    @Override
    public Collection<EntityType> getCompatibleEntities() {
        return compatibleEntities;
    }
}
