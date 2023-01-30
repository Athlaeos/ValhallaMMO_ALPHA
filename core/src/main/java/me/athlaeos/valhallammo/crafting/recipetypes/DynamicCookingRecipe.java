package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.CookingRecipe;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public abstract class DynamicCookingRecipe<T extends CookingRecipe<T>> implements Cloneable{

    protected final String name;
    protected final NamespacedKey key;
    protected ItemStack input;
    protected ItemStack result;
    protected boolean tinkerInput;
    protected boolean useMetadata;
    protected boolean requireCustomTools;
    protected int cookTime;
    protected float experience;
    protected List<DynamicItemModifier> modifiers;
    protected boolean unlockedForEveryone = false;

    public DynamicCookingRecipe(String name, ItemStack exactItem, ItemStack result, int time, float experience, boolean sameResultAsInput, boolean useMetadata, boolean requireCustomTools, List<DynamicItemModifier> modifiers){
        this.key = new NamespacedKey(ValhallaMMO.getPlugin(), "dynamic_cook_" + name);
        this.name = name;
        this.input = exactItem;
        this.result = result;
        this.cookTime = time;
        this.experience = experience;
        this.tinkerInput = sameResultAsInput;
        this.useMetadata = useMetadata;
        this.requireCustomTools = requireCustomTools;
        this.modifiers = modifiers;
    }

    public void setUnlockedForEveryone(boolean unlockedForEveryone) {
        this.unlockedForEveryone = unlockedForEveryone;
    }

    public boolean isUnlockedForEveryone() {
        return unlockedForEveryone;
    }

    public abstract CookingRecipe<T> generateRecipe();

    public NamespacedKey getKey() {
        return key;
    }

    public float getExperience() {
        return experience;
    }

    public void setExperience(float experience) {
        this.experience = experience;
    }

    public int getCookTime() {
        return cookTime;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public void setInput(ItemStack input) {
        this.input = input;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public void setTinkerInput(boolean tinkerInput) {
        this.tinkerInput = tinkerInput;
    }

    public void setUseMetadata(boolean useMetadata) {
        this.useMetadata = useMetadata;
    }

    public void setRequireCustomTools(boolean requireCustomTools) {
        this.requireCustomTools = requireCustomTools;
    }

    public void setModifiers(List<DynamicItemModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public String getName() {
        return name;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getResult() {
        return result;
    }

    public boolean isTinkerInput() {
        return tinkerInput;
    }

    public boolean isUseMetadata() {
        return useMetadata;
    }

    public boolean requiresCustomTool() {
        return requireCustomTools;
    }

    public List<DynamicItemModifier> getModifiers() {
        return modifiers;
    }

    @Override
    public DynamicCookingRecipe<T> clone() {
        final DynamicCookingRecipe<T> clone;
        try {
            clone = (DynamicCookingRecipe<T>) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Exception occurred calling DynamicCookingRecipe.clone()", ex);
        }
        return clone;
    }
}
