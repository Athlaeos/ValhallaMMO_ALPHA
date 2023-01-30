package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.items.EquipmentClass;
import org.bukkit.inventory.BlastingRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;

public class DynamicBlastingRecipe extends DynamicCookingRecipe<BlastingRecipe> {
    public DynamicBlastingRecipe(String name, ItemStack exactItem, ItemStack result, int time, float experience, boolean sameResultAsInput, boolean useMetadata, boolean requireCustomTools, List<DynamicItemModifier> modifiers) {
        super(name, exactItem, result, time, experience, sameResultAsInput, useMetadata, requireCustomTools, modifiers);
    }

    public BlastingRecipe generateRecipe() {
        BlastingRecipe recipe;
        // if exact meta is required, but the item is equipment, then its base type should be used
        if (useMetadata){
            if (EquipmentClass.getClass(input) != null)
                recipe = new BlastingRecipe(key, result, input.getType(), experience, cookTime);
            else
                recipe = new BlastingRecipe(key, result, new RecipeChoice.ExactChoice(input), experience, cookTime);
        } else {
            recipe = new BlastingRecipe(key, result, input.getType(), experience, cookTime);
        }
        return recipe;
    }
}
