package me.athlaeos.valhallammo.loottables;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.block.Biome;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Set;

public class TieredLootEntry {
    private final String name;
    private final int tier;
    private final ItemStack loot;
    private final Set<Biome> biomeFilter;
    private final Set<String> regionFilter;
    private final int weight;
    private final List<DynamicItemModifier> modifiers;

    public TieredLootEntry(String name, int tier, ItemStack loot, int weight, List<DynamicItemModifier> modifiers, Set<Biome> biomeFilter, Set<String> regionFilter){
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

    public Set<Biome> getBiomeFilter() {
        return biomeFilter;
    }

    public Set<String> getRegionFilter() {
        return regionFilter;
    }

    public int getWeight() {
        return weight;
    }

    public List<DynamicItemModifier> getModifiers() {
        return modifiers;
    }
}
