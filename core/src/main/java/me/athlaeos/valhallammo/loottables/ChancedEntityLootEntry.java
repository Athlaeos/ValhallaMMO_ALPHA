package me.athlaeos.valhallammo.loottables;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Set;

public class ChancedEntityLootEntry {
    private final String name;
    private final ItemStack loot;
    private final Set<Biome> biomeFilter;
    private final EntityType entity;
    private final Set<String> regionFilter;
    private final double chance;
    private final Collection<DynamicItemModifier> modifiers;
    private final boolean overwriteNaturalDrops;

    public ChancedEntityLootEntry(String name, EntityType entity, ItemStack loot, boolean overwriteNaturalDrops, double chance, Collection<DynamicItemModifier> modifiers, Set<Biome> biomeFilter, Set<String> regionFilter){
        this.name = name;
        this.entity = entity;
        this.loot = loot;
        this.chance = chance;
        this.modifiers = modifiers;
        this.biomeFilter = biomeFilter;
        this.regionFilter = regionFilter;
        this.overwriteNaturalDrops = overwriteNaturalDrops;
    }

    public String getName() {
        return name;
    }

    public EntityType getEntity() {
        return entity;
    }

    public boolean isOverwriteNaturalDrops() {
        return overwriteNaturalDrops;
    }

    public ItemStack getLoot() {
        return loot;
    }

    public Set<Biome> getBiomeFilter() {
        return biomeFilter;
    }

    public Set<String> getRegionFilter() {
        return regionFilter;
    }

    public double getChance() {
        return chance;
    }

    public Collection<DynamicItemModifier> getModifiers() {
        return modifiers;
    }
}
