package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class ItemCraftingRecipe extends AbstractCustomCraftingRecipe implements Cloneable{
    // Crafts an item for the player if the player has the required ingredients
    protected ItemStack result;
    protected int requiredToolId = -1;
    protected int toolRequirementType = 0;

    public ItemCraftingRecipe(String name, String displayName, ItemStack result, Material craftingBlock, Collection<ItemStack> ingredients){
        super(name, displayName, craftingBlock, ingredients);
        this.result = result;
    }

    public ItemCraftingRecipe(String name, String displayName, ItemStack result, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime){
        super(name, displayName, craftingBlock, ingredients, craftingTime);
        this.result = result;
    }

    public ItemCraftingRecipe(String name, String displayName, ItemStack result, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation);
        this.result = result;
    }

    public ItemCraftingRecipe(String name, String displayName, ItemStack result, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation, List<DynamicItemModifier> itemModifiers){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation, itemModifiers);
        this.result = result;
    }

    public ItemCraftingRecipe(String name, String displayName, ItemStack result, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation, List<DynamicItemModifier> itemModifiers, boolean requireExactMeta){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation, itemModifiers, requireExactMeta);
        this.result = result;
    }

    public ItemCraftingRecipe(String name, String displayName, ItemStack result, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation, List<DynamicItemModifier> itemModifiers, boolean requireExactMeta, int subsequentCrafts){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation, itemModifiers, requireExactMeta, subsequentCrafts);
        this.result = result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public ItemStack getResult() {
        return result;
    }

    public void setRequiredToolId(int requiredToolId) {
        this.requiredToolId = requiredToolId;
    }

    public void setToolRequirementType(int toolRequirementType) {
        this.toolRequirementType = toolRequirementType;
    }

    public int getRequiredToolId() {
        return requiredToolId;
    }

    public int getToolRequirementType() {
        return toolRequirementType;
    }

    @Override
    public ItemCraftingRecipe clone() {
        final ItemCraftingRecipe clone;
        try {
            clone = (ItemCraftingRecipe) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Exception occurred calling ItemCraftingRecipe.clone()", ex);
        }
        clone.result = this.result.clone();
        return clone;
    }
}
