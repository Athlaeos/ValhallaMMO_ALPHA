package me.athlaeos.valhallammo.dom;

import me.athlaeos.valhallammo.crafting.recipetypes.ItemCraftingRecipe;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.inventory.ItemStack;

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

    public static boolean doItemFitsCraftingConditions(ItemStack tool, int requirementType, int toolIdRequired){
        if (Utils.isItemEmptyOrNull(tool)) return false;
        if (requirementType < 0) return true;
        if (requirementType < values().length){
            RequirementType type = values()[requirementType];
            if (type == NO_TOOL_REQUIRED) return true;
            int toolId = SmithingItemTreatmentManager.getInstance().getItemsToolId(tool);
            switch (type){
                case EQUALS: return toolId == toolIdRequired;
                case EQUALS_OR_LESSER: return toolId <= toolIdRequired;
                case EQUALS_OR_GREATER: return toolId >= toolIdRequired;
            }
        }
        return true;
    }
}
