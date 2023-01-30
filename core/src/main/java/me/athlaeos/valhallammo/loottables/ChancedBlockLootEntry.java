package me.athlaeos.valhallammo.loottables;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class ChancedBlockLootEntry {
    private final String name;
    private final ItemStack loot;
    private final Set<Biome> biomeFilter;
    private final Material block;
    private final Set<String> regionFilter;
    private final double chance;
    private final List<DynamicItemModifier> modifiers;
    private final boolean overwriteNaturalDrops;

    public ChancedBlockLootEntry(String name, Material block, ItemStack loot, boolean overwriteNaturalDrops, double chance, List<DynamicItemModifier> modifiers, Set<Biome> biomeFilter, Set<String> regionFilter){
        this.name = name;
        this.block = block;
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

    public Material getBlock() {
        return block;
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

    public List<DynamicItemModifier> getModifiers() {
        return modifiers;
    }
}
