package me.athlaeos.valhallammo.dom;

import me.athlaeos.valhallammo.crafting.recipetypes.ItemCraftingRecipe;

public enum RequirementType {
    NO_TOOL_REQUIRED,
    EQUALS,
    EQUALS_OR_GREATER,
    EQUALS_OR_LESSER;

    public static boolean isRecipeCraftable(ItemCraftingRecipe recipe, int toolId){
        if (recipe.getToolRequirementType() < 0) return true;
        if (recipe.getToolRequirementType() < values().length){
            switch (values()[recipe.getToolRequirementType()]){
                case NO_TOOL_REQUIRED: return true;
                case EQUALS: return toolId == recipe.getRequiredToolId();
                case EQUALS_OR_LESSER: return toolId <= recipe.getRequiredToolId();
                case EQUALS_OR_GREATER: return toolId >= recipe.getRequiredToolId();
            }
        }
        return true;
    }
}
