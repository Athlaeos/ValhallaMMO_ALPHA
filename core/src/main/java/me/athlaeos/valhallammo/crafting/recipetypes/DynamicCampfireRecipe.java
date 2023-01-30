package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.items.EquipmentClass;
import org.bukkit.inventory.CampfireRecipe;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.RecipeChoice;

import java.util.List;

public class DynamicCampfireRecipe extends DynamicCookingRecipe<CampfireRecipe> {
    private int campfireMode; // determines what campfires the recipe works for. 0 = both, 1 = regular campfire only, 2 = soul campfire only
    public DynamicCampfireRecipe(String name, ItemStack exactItem, ItemStack result, int campfireMode, int time, float experience, boolean sameResultAsInput, boolean useMetadata, boolean requireCustomTools, List<DynamicItemModifier> modifiers) {
        super(name, exactItem, result, time, experience, sameResultAsInput, useMetadata, requireCustomTools, modifiers);
        this.campfireMode = campfireMode;
    }

    public CampfireRecipe generateRecipe() {
        CampfireRecipe recipe;
        // if exact meta is required, but the item is equipment, then its base type should be used
        if (useMetadata){
            if (EquipmentClass.getClass(input) != null)
                recipe = new CampfireRecipe(key, result, input.getType(), experience, cookTime);
            else
                recipe = new CampfireRecipe(key, result, new RecipeChoice.ExactChoice(input), experience, cookTime);
        } else {
            recipe = new CampfireRecipe(key, result, input.getType(), experience, cookTime);
        }
        return recipe;
    }

    public int getCampfireMode() {
        return campfireMode;
    }

    public void setCampfireMode(int campfireMode) {
        this.campfireMode = campfireMode;
    }

    public boolean worksForCampfire(){
        return campfireMode == 0 || campfireMode == 1;
    }

    public boolean worksForSoulCampfire(){
        return campfireMode == 0 || campfireMode == 2;
    }
}
