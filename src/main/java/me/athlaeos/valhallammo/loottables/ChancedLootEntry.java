package me.athlaeos.valhallammo.loottables;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class TieredLootEntry {
    private final String name;
    private final int tier;
    private final ItemStack loot;
    private final List<Biome> biomeFilter;
    private final List<String> regionFilter;
    private final int weight;
    private final Collection<DynamicItemModifier> modifiers;

    public TieredLootEntry(String name, int tier, ItemStack loot, int weight, Collection<DynamicItemModifier> modifiers, List<Biome> biomeFilter, List<String> regionFilter){
        this.name = name;
        this.tier = tier;
        this.loot = loot;
        this.weight = weight;
        this.modifiers = modifiers;
        this.biomeFilter = biomeFilter;
        this.regionFilter = regionFilter;
    }

    public String getName() {
        return name;
    }

    public int getTier() {
        return tier;
    }

    public ItemStack getLoot() {
        return loot;
    }

    public List<Biome> getBiomeFilter() {
        return biomeFilter;
    }

    public List<String> getRegionFilter() {
        return regionFilter;
    }

    public int getWeight() {
        return weight;
    }

    public Collection<DynamicItemModifier> getModifiers() {
        return modifiers;
    }
}
