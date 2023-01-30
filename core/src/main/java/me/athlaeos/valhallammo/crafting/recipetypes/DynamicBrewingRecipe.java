package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class DynamicBrewingRecipe implements Cloneable{
    private final String name;
    private ItemStack ingredient;
    private Material applyOn;
    private boolean perfectMeta;
    private List<DynamicItemModifier> modifiers;
    private boolean unlockedForEveryone = false;

    public DynamicBrewingRecipe(String name, ItemStack ingredient, Material applyOn, boolean perfect, List<DynamicItemModifier> modifiers) {
        this.name = name;
        this.ingredient = ingredient;
        this.applyOn = applyOn;
        this.perfectMeta = perfect;
        this.modifiers = modifiers;
    }

    public void setUnlockedForEveryone(boolean unlockedForEveryone) {
        this.unlockedForEveryone = unlockedForEveryone;
    }

    public boolean isUnlockedForEveryone() {
        return unlockedForEveryone;
    }

    public boolean isPerfectMeta() {
        return perfectMeta;
    }

    public ItemStack getIngredient() {
        return ingredient;
    }

    public List<DynamicItemModifier> getItemModifiers() {
        return modifiers;
    }

    public Material getRequiredType() {
        return applyOn;
    }

    public String getName() {
        return name;
    }

    public void setApplyOn(Material applyOn) {
        this.applyOn = applyOn;
    }

    public void setIngredient(ItemStack ingredient) {
        this.ingredient = ingredient;
    }

    public void setModifiers(List<DynamicItemModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public void setPerfectMeta(boolean perfectMeta) {
        this.perfectMeta = perfectMeta;
    }

    @Override
    public DynamicBrewingRecipe clone() {
        final DynamicBrewingRecipe clone;
        try {
            clone = (DynamicBrewingRecipe) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Exception occurred calling DynamicShapedRecipe.clone()", ex);
        }
        return clone;
    }
}