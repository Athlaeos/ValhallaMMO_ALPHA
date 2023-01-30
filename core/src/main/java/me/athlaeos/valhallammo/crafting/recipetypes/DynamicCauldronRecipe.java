package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.List;

public class DynamicCauldronRecipe implements Cloneable{
    protected final String name;
    protected Collection<ItemStack> ingredients;
    protected boolean ingredientsExactMeta;
    protected ItemStack catalyst;
    protected boolean catalystExactMeta;
    protected boolean requireCustomCatalyst;
    protected ItemStack result;
    protected boolean tinkerCatalyst;
    protected boolean requiresBoilingWater;
    protected boolean consumesWaterLevel;
    protected List<DynamicItemModifier> itemModifiers;
    protected boolean unlockedForEveryone = false;

    public DynamicCauldronRecipe(String name, Collection<ItemStack> ingredients, ItemStack catalyst, ItemStack result, boolean ingredientsExactMeta, boolean catalystExactMeta, boolean requireCustomCatalyst, boolean tinkerCatalyst, boolean requiresBoilingWater, boolean consumesWaterLevel,
                                 List<DynamicItemModifier> itemModifers){
        this.name = name;
        this.ingredients = ingredients;
        this.ingredientsExactMeta = ingredientsExactMeta;
        this.catalyst = catalyst;
        this.requireCustomCatalyst = requireCustomCatalyst;
        this.catalystExactMeta = catalystExactMeta;
        this.result = result;
        this.tinkerCatalyst = tinkerCatalyst;
        this.requiresBoilingWater = requiresBoilingWater;
        this.consumesWaterLevel = consumesWaterLevel;
        this.itemModifiers = itemModifers;
    }

    public void setRequireCustomCatalyst(boolean requireCustomCatalyst) {
        this.requireCustomCatalyst = requireCustomCatalyst;
    }

    public boolean isRequireCustomCatalyst() {
        return requireCustomCatalyst;
    }

    public void setIngredients(Collection<ItemStack> ingredients) {
        this.ingredients = ingredients;
    }

    public void setIngredientsExactMeta(boolean ingredientsExactMeta) {
        this.ingredientsExactMeta = ingredientsExactMeta;
    }

    public void setCatalyst(ItemStack catalyst) {
        this.catalyst = catalyst;
    }

    public void setCatalystExactMeta(boolean catalystExactMeta) {
        this.catalystExactMeta = catalystExactMeta;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public void setTinkerCatalyst(boolean tinkerCatalyst) {
        this.tinkerCatalyst = tinkerCatalyst;
    }

    public void setRequiresBoilingWater(boolean requiresBoilingWater) {
        this.requiresBoilingWater = requiresBoilingWater;
    }

    public void setConsumesWaterLevel(boolean consumesWaterLevel) {
        this.consumesWaterLevel = consumesWaterLevel;
    }

    public void setItemModifiers(List<DynamicItemModifier> itemModifiers) {
        this.itemModifiers = itemModifiers;
    }

    public void setUnlockedForEveryone(boolean unlockedForEveryone) {
        this.unlockedForEveryone = unlockedForEveryone;
    }

    public String getName() {
        return name;
    }

    public Collection<ItemStack> getIngredients() {
        return ingredients;
    }

    public boolean isIngredientsExactMeta() {
        return ingredientsExactMeta;
    }

    public ItemStack getCatalyst() {
        return catalyst;
    }

    public boolean isCatalystExactMeta() {
        return catalystExactMeta;
    }

    public ItemStack getResult() {
        return result;
    }

    public boolean isTinkerCatalyst() {
        return tinkerCatalyst;
    }

    public boolean isRequiresBoilingWater() {
        return requiresBoilingWater;
    }

    public boolean isConsumesWaterLevel() {
        return consumesWaterLevel;
    }

    public List<DynamicItemModifier> getItemModifiers() {
        return itemModifiers;
    }

    public boolean isUnlockedForEveryone() {
        return unlockedForEveryone;
    }

    @Override
    public DynamicCauldronRecipe clone() {
        try {
            return (DynamicCauldronRecipe) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
