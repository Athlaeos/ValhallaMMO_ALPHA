package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class DynamicBlastingRecipe extends DynamicCookingRecipe<BlastingRecipe> implements Cloneable{
    private final BlastingRecipe recipe;

    public DynamicBlastingRecipe(String name, BlastingRecipe recipe, ItemStack exactItem, ItemStack result, boolean sameResultAsInput, boolean useMetadata, boolean requireCustomTools, Collection<DynamicItemModifier> modifiers) {
        super(name, exactItem, result, sameResultAsInput, useMetadata, requireCustomTools, modifiers);
        this.recipe = recipe;
    }

    public BlastingRecipe getRecipe() {
        return recipe;
    }

    @Override
    public BlastingRecipe clone() {
        final BlastingRecipe clone;
        try {
            clone = (BlastingRecipe) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Exception occurred calling DynamicBlastingRecipe.clone()", ex);
        }
        return clone;
    }
}
