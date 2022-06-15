package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.inventory.FurnaceRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class DynamicFurnaceRecipe extends DynamicCookingRecipe<FurnaceRecipe> implements Cloneable{
    private final FurnaceRecipe recipe;

    public DynamicFurnaceRecipe(String name, FurnaceRecipe recipe, ItemStack exactItem, ItemStack result, boolean sameResultAsInput, boolean useMetadata, boolean requireCustomTools, Collection<DynamicItemModifier> modifiers) {
        super(name, exactItem, result, sameResultAsInput, useMetadata, requireCustomTools, modifiers);
        this.recipe = recipe;
    }

    public FurnaceRecipe getRecipe() {
        return recipe;
    }

    @Override
    public FurnaceRecipe clone() {
        final FurnaceRecipe clone;
        try {
            clone = (FurnaceRecipe) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Exception occurred calling DynamicFurnaceRecipe.clone()", ex);
        }
        return clone;
    }
}
