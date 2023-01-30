package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class ItemImprovementRecipe extends AbstractCustomCraftingRecipe implements Cloneable{
    // An item improvement recipe instead of outputting an item modifies the item held by the player given a list of
    // DynamicItemModifiers
    protected Material requiredItemType;

    public ItemImprovementRecipe(String name, String displayName, Material requiredItemType, Material craftingBlock, Collection<ItemStack> ingredients){
        super(name, displayName, craftingBlock, ingredients);
        this.requiredItemType = requiredItemType;
    }

    public ItemImprovementRecipe(String name, String displayName, Material requiredItemType, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime){
        super(name, displayName, craftingBlock, ingredients, craftingTime);
        this.requiredItemType = requiredItemType;
    }

    public ItemImprovementRecipe(String name, String displayName, Material requiredItemType, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation);
        this.requiredItemType = requiredItemType;
    }

    public ItemImprovementRecipe(String name, String displayName, Material requiredItemType, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation, List<DynamicItemModifier> itemModifiers){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation, itemModifiers);
        this.requiredItemType = requiredItemType;
    }

    public ItemImprovementRecipe(String name, String displayName, Material requiredItemType, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation, List<DynamicItemModifier> itemModifiers, boolean requireExactMeta){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation, itemModifiers, requireExactMeta);
        this.requiredItemType = requiredItemType;
    }

    public ItemImprovementRecipe(String name, String displayName, Material requiredItemType, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation, List<DynamicItemModifier> itemModifiers, boolean requireExactMeta, int subsequentCrafts){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation, itemModifiers, requireExactMeta, subsequentCrafts);
        this.requiredItemType = requiredItemType;
    }

    public Material getRequiredItemType() {
        return requiredItemType;
    }

    public void setRequiredItemType(Material requiredItemType) {
        this.requiredItemType = requiredItemType;
    }

    @Override
    public ItemImprovementRecipe clone() {
        final ItemImprovementRecipe clone;
        try {
            clone = (ItemImprovementRecipe) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Exception occurred calling ItemImprovementRecipe.clone()", ex);
        }
        clone.requiredItemType = this.requiredItemType;
        return clone;
    }
}
