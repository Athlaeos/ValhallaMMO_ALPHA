package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;

public abstract class DynamicCookingRecipe<T extends CookingRecipe<T>> {

    private final String name;
    private ItemStack exactItem;
    private ItemStack result;
    private boolean sameResultAsInput;
    private boolean useMetadata = true;
    private boolean requireCustomTools = true;
    private Collection<DynamicItemModifier> modifiers;

    public DynamicCookingRecipe(String name, ItemStack exactItem, ItemStack result, boolean sameResultAsInput, boolean useMetadata, boolean requireCustomTools, Collection<DynamicItemModifier> modifiers){
        this.name = name;
        this.exactItem = exactItem;
        this.result = result;
        this.sameResultAsInput = sameResultAsInput;
        this.useMetadata = useMetadata;
        this.requireCustomTools = requireCustomTools;
        this.modifiers = modifiers;
    }

    public abstract CookingRecipe<T> getRecipe();

    public void setExactItem(ItemStack exactItem) {
        this.exactItem = exactItem;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public void setSameResultAsInput(boolean sameResultAsInput) {
        this.sameResultAsInput = sameResultAsInput;
    }

    public void setUseMetadata(boolean useMetadata) {
        this.useMetadata = useMetadata;
    }

    public void setRequireCustomTools(boolean requireCustomTools) {
        this.requireCustomTools = requireCustomTools;
    }

    public void setModifiers(Collection<DynamicItemModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public String getName() {
        return name;
    }

    public ItemStack getExactItem() {
        return exactItem;
    }

    public ItemStack getResult() {
        return result;
    }

    public boolean isSameResultAsInput() {
        return sameResultAsInput;
    }

    public boolean isUseMetadata() {
        return useMetadata;
    }

    public boolean isRequireCustomTool() {
        return requireCustomTools;
    }

    public Collection<DynamicItemModifier> getModifiers() {
        return modifiers;
    }
}
