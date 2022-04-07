package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ItemInventoryTinkerRecipe extends AbstractCustomCraftingRecipe implements Cloneable{
    // Crafts an item for the player if the player has the required ingredients
    protected Material applyOn;

    public ItemInventoryTinkerRecipe(String name, String displayName, Material applyOn, Material craftingBlock, Collection<ItemStack> ingredients){
        super(name, displayName, craftingBlock, ingredients);
        this.applyOn = applyOn;
    }

    public ItemInventoryTinkerRecipe(String name, String displayName, Material applyOn, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime){
        super(name, displayName, craftingBlock, ingredients, craftingTime);
        this.applyOn = applyOn;
    }

    public ItemInventoryTinkerRecipe(String name, String displayName, Material applyOn, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation);
        this.applyOn = applyOn;
    }

    public ItemInventoryTinkerRecipe(String name, String displayName, Material applyOn, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation, Collection<DynamicItemModifier> itemModifiers){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation, itemModifiers);
        this.applyOn = applyOn;
    }

    public ItemInventoryTinkerRecipe(String name, String displayName, Material applyOn, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation, Collection<DynamicItemModifier> itemModifiers, boolean requireExactMeta){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation, itemModifiers, requireExactMeta);
        this.applyOn = applyOn;
    }

    public ItemInventoryTinkerRecipe(String name, String displayName, Material applyOn, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation, Collection<DynamicItemModifier> itemModifiers, boolean requireExactMeta, int subsequentCrafts){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation, itemModifiers, requireExactMeta, subsequentCrafts);
        this.applyOn = applyOn;
    }

    public void setApplyOn(Material applyOn) {
        this.applyOn = applyOn;
    }

    public Material getApplyOn() {
        return applyOn;
    }

    @Override
    public ItemInventoryTinkerRecipe clone() {
        final ItemInventoryTinkerRecipe clone;
        try {
            clone = (ItemInventoryTinkerRecipe) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Exception occurred calling ItemCraftingRecipe.clone()", ex);
        }
        clone.applyOn = this.applyOn;
        return clone;
    }
}
