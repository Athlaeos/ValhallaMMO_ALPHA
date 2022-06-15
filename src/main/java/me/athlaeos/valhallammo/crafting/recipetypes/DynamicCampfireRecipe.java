package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class DynamicCampfireRecipe extends DynamicCookingRecipe<CampfireRecipe> implements Cloneable{
    private final CampfireRecipe recipe;

    public DynamicCampfireRecipe(String name, CampfireRecipe recipe, ItemStack exactItem, ItemStack result, boolean sameResultAsInput, boolean useMetadata, boolean requireCustomTools, Collection<DynamicItemModifier> modifiers) {
        super(name, exactItem, result, sameResultAsInput, useMetadata, requireCustomTools, modifiers);
        this.recipe = recipe;
    }

    public CampfireRecipe getRecipe() {
        return recipe;
    }

    @Override
    public CampfireRecipe clone() {
        final CampfireRecipe clone;
        try {
            clone = (CampfireRecipe) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Exception occurred calling DynamicCampfireRecipe.clone()", ex);
        }
        return clone;
    }
}
