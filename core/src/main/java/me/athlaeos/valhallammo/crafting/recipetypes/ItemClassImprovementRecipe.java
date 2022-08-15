package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.items.EquipmentClass;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public class ItemClassImprovementRecipe extends AbstractCustomCraftingRecipe implements Cloneable{
    // An item improvement recipe instead of outputting an item modifies the item held by the player given a list of
    // DynamicItemModifiers
    protected EquipmentClass requiredEquipmentClass;

    public ItemClassImprovementRecipe(String name, String displayName, EquipmentClass requiredEquipmentClass, Material craftingBlock, Collection<ItemStack> ingredients){
        super(name, displayName, craftingBlock, ingredients);
        this.requiredEquipmentClass = requiredEquipmentClass;
    }

    public ItemClassImprovementRecipe(String name, String displayName, EquipmentClass requiredEquipmentClass, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime){
        super(name, displayName, craftingBlock, ingredients, craftingTime);
        this.requiredEquipmentClass = requiredEquipmentClass;
    }

    public ItemClassImprovementRecipe(String name, String displayName, EquipmentClass requiredEquipmentClass, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation);
        this.requiredEquipmentClass = requiredEquipmentClass;
    }

    public ItemClassImprovementRecipe(String name, String displayName, EquipmentClass requiredEquipmentClass, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation, Collection<DynamicItemModifier> itemModifiers){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation, itemModifiers);
        this.requiredEquipmentClass = requiredEquipmentClass;
    }

    public ItemClassImprovementRecipe(String name, String displayName, EquipmentClass requiredEquipmentClass, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation, Collection<DynamicItemModifier> itemModifiers, boolean requireExactMeta){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation, itemModifiers, requireExactMeta);
        this.requiredEquipmentClass = requiredEquipmentClass;
    }

    public ItemClassImprovementRecipe(String name, String displayName, EquipmentClass requiredEquipmentClass, Material craftingBlock, Collection<ItemStack> ingredients, int craftingTime, boolean breakStation, Collection<DynamicItemModifier> itemModifiers, boolean requireExactMeta, int subsequentCrafts){
        super(name, displayName, craftingBlock, ingredients, craftingTime, breakStation, itemModifiers, requireExactMeta, subsequentCrafts);
        this.requiredEquipmentClass = requiredEquipmentClass;
    }

    public EquipmentClass getRequiredEquipmentClass() {
        return requiredEquipmentClass;
    }

    public void setRequiredEquipmentClass(EquipmentClass requiredEquipmentClass) {
        this.requiredEquipmentClass = requiredEquipmentClass;
    }

    @Override
    public ItemClassImprovementRecipe clone() {
        final ItemClassImprovementRecipe clone;
        try {
            clone = (ItemClassImprovementRecipe) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Exception occurred calling ItemImprovementRecipe.clone()", ex);
        }
        clone.requiredEquipmentClass = this.requiredEquipmentClass;
        return clone;
    }
}
