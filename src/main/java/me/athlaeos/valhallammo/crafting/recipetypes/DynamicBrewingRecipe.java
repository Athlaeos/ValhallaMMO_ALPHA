package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BrewingRecipe {
    private final String name;
    private final ItemStack ingredient;
    private final Material applyOn;
    private final boolean perfectMeta;
    private List<DynamicItemModifier> modifiers;

    public BrewingRecipe(String name, ItemStack ingredient, Material applyOn, boolean perfect, List<DynamicItemModifier> modifiers) {
        this.name = name;
        this.ingredient = ingredient;
        this.applyOn = applyOn;
        this.perfectMeta = perfect;
        this.modifiers = modifiers;
    }

    public boolean isPerfectMeta() {
        return perfectMeta;
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public List<DynamicItemModifier> getItemModifiers() {
        return modifiers;
    }

    public Material getApplyOn() {
        return applyOn;
    }

    public String getName() {
        return name;
    }
}