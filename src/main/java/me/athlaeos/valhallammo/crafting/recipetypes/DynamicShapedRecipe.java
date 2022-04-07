package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;

import java.util.Collection;
import java.util.Map;

public class DynamicShapedRecipe implements Cloneable{
    private final ShapedRecipe recipe;
    private Map<Integer, ItemStack> exactItems;
    private boolean useMetadata = true;
    private boolean requireCustomTools = true;
    private boolean improveMiddleItem = false;
    private String name;
    private Collection<DynamicItemModifier> modifiers;

    public DynamicShapedRecipe(String name, ShapedRecipe recipe, Map<Integer, ItemStack> exactItems, boolean useMetadata, boolean requireCustomTools, boolean improveMiddleItem, Collection<DynamicItemModifier> modifiers) {
        this.recipe = recipe;
        this.exactItems = exactItems;
        this.useMetadata = useMetadata;
        this.requireCustomTools = requireCustomTools;
        this.improveMiddleItem = improveMiddleItem;
        this.name = name;
        this.modifiers = modifiers;
    }

    public ShapedRecipe getRecipe() {
        return recipe;
    }

    public Map<Integer, ItemStack> getExactItems() {
        return exactItems;
    }

    public boolean isUseMetadata() {
        return useMetadata;
    }

    public boolean isRequireCustomTools() {
        return requireCustomTools;
    }

    public String getName() {
        return name;
    }

    public Collection<DynamicItemModifier> getItemModifiers() {
        return modifiers;
    }

    public void setExactItems(Map<Integer, ItemStack> exactItems) {
        this.exactItems = exactItems;
    }

    public void setUseMetadata(boolean useMetadata) {
        this.useMetadata = useMetadata;
    }

    public void setRequireCustomTools(boolean requireCustomTools) {
        this.requireCustomTools = requireCustomTools;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setModifiers(Collection<DynamicItemModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public boolean isTinkerFirstItem() {
        return improveMiddleItem;
    }

    public void setImproveMiddleItem(boolean improveMiddleItem) {
        this.improveMiddleItem = improveMiddleItem;
    }

    @Override
    public DynamicShapedRecipe clone() {
        final DynamicShapedRecipe clone;
        try {
            clone = (DynamicShapedRecipe) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Exception occurred calling DynamicShapedRecipe.clone()", ex);
        }
        return clone;
    }
}
