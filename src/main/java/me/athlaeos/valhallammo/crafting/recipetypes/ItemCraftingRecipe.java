package me.athlaeos.valhallammo.crafting.dom;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ItemCraftingRecipe extends AbstractCustomCraftingRecipe implements Cloneable{
    // Crafts an item for the player if the player has the required ingredients
    protected ItemStack result;

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

    public ItemCraftingRecipe(String name, String displayName, ItemStack result, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation, Collection<DynamicItemModifier> itemModifiers){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation, itemModifiers);
        this.result = result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public ItemStack getResult() {
        return result;
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
