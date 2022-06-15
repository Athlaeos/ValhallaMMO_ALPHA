package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.SmokingRecipe;

import java.util.Collection;

public class DynamicSmokingRecipe extends DynamicCookingRecipe<SmokingRecipe> implements Cloneable{
    private final SmokingRecipe recipe;

    public DynamicSmokingRecipe(String name, SmokingRecipe recipe, ItemStack exactItem, ItemStack result, boolean sameResultAsInput, boolean useMetadata, boolean requireCustomTools, Collection<DynamicItemModifier> modifiers) {
        super(name, exactItem, result, sameResultAsInput, useMetadata, requireCustomTools, modifiers);
        this.recipe = recipe;
    }

    public SmokingRecipe getRecipe() {
        return recipe;
    }

    @Override
    public SmokingRecipe clone() {
        final SmokingRecipe clone;
        try {
            clone = (SmokingRecipe) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Exception occurred calling DynamicSmokingRecipe.clone()", ex);
        }
        return clone;
    }
}
