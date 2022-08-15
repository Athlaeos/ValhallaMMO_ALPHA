package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.items.blockstatevalidations.CraftValidation;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;

public abstract class AbstractCustomCraftingRecipe implements Cloneable{
    protected Material craftingBlock;
    protected String name;
    protected String displayName;
    protected Collection<ItemStack> ingredients;
    protected boolean requireExactMeta = true;
    protected int craftingTime = 2500;
    protected boolean breakStation = false;
    protected Collection<DynamicItemModifier> itemModifers = new HashSet<>();
    protected CraftValidation validation = null;
    protected int consecutiveCrafts = 8;
    protected boolean unlockedForEveryone = false;

    public AbstractCustomCraftingRecipe(String name, String displayName, Material craftingBlock, Collection<ItemStack> ingredients){
        this.name = name;
        this.displayName = displayName;
        this.craftingBlock = craftingBlock;
        this.ingredients = ingredients;
    }

    public AbstractCustomCraftingRecipe(String name, String displayName, Material craftingBlock, Collection<ItemStack> ingredients, int craftingtime){
        this.name = name;
        this.displayName = displayName;
        this.craftingBlock = craftingBlock;
        this.ingredients = ingredients;
        this.craftingTime = craftingtime;
    }

    public AbstractCustomCraftingRecipe(String name, String displayName, Material craftingBlock, Collection<ItemStack> ingredients, int craftingtime, boolean breakStation){
        this.name = name;
        this.displayName = displayName;
        this.craftingBlock = craftingBlock;
        this.ingredients = ingredients;
        this.craftingTime = craftingtime;
        this.breakStation = breakStation;
    }

    public AbstractCustomCraftingRecipe(String name, String displayName, Material craftingBlock, Collection<ItemStack> ingredients, int craftingtime, boolean breakStation, Collection<DynamicItemModifier> itemModifers){
        this.name = name;
        this.displayName = displayName;
        this.craftingBlock = craftingBlock;
        this.ingredients = ingredients;
        this.craftingTime = craftingtime;
        this.breakStation = breakStation;
        this.itemModifers = itemModifers;
    }

    public AbstractCustomCraftingRecipe(String name, String displayName, Material craftingBlock, Collection<ItemStack> ingredients, int craftingtime, boolean breakStation, Collection<DynamicItemModifier> itemModifers, boolean requireExactMeta){
        this.name = name;
        this.displayName = displayName;
        this.craftingBlock = craftingBlock;
        this.ingredients = ingredients;
        this.craftingTime = craftingtime;
        this.breakStation = breakStation;
        this.itemModifers = itemModifers;
        this.requireExactMeta = requireExactMeta;
    }

    public AbstractCustomCraftingRecipe(String name, String displayName, Material craftingBlock, Collection<ItemStack> ingredients, int craftingtime, boolean breakStation, Collection<DynamicItemModifier> itemModifers, boolean requireExactMeta, int consecutiveCrafts){
        this.name = name;
        this.displayName = displayName;
        this.craftingBlock = craftingBlock;
        this.ingredients = ingredients;
        this.craftingTime = craftingtime;
        this.breakStation = breakStation;
        this.itemModifers = itemModifers;
        this.requireExactMeta = requireExactMeta;
        this.consecutiveCrafts = consecutiveCrafts;
    }

    public void setUnlockedForEveryone(boolean unlockedForEveryone) {
        this.unlockedForEveryone = unlockedForEveryone;
    }

    public boolean isUnlockedForEveryone() {
        return unlockedForEveryone;
    }

    public Collection<DynamicItemModifier> getItemModifers() {
        return itemModifers;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Collection<ItemStack> getIngredients() {
        return ingredients;
    }

    public int getCraftingTime() {
        return craftingTime;
    }

    public Material getCraftingBlock() {
        return craftingBlock;
    }

    public void setModifiers(Collection<DynamicItemModifier> itemModifers) {
        this.itemModifers = itemModifers;
    }

    public void setBreakStation(boolean breakStation) {
        this.breakStation = breakStation;
    }

    public void setCraftingTime(int craftingTime) {
        this.craftingTime = craftingTime;
    }

    public void setIngredients(Collection<ItemStack> ingredients) {
        this.ingredients = ingredients;
    }

    public boolean breakStation() {
        return breakStation;
    }

    public void setCraftingBlock(Material craftingBlock) {
        this.craftingBlock = craftingBlock;
    }

    public CraftValidation getValidation() {
        return validation;
    }

    public void setValidation(CraftValidation validation) {
        this.validation = validation;
    }

    public boolean requireExactMeta() {
        return requireExactMeta;
    }

    public void setRequireExactMeta(boolean requireExactMeta) {
        this.requireExactMeta = requireExactMeta;
    }

    public int getConsecutiveCrafts() {
        return consecutiveCrafts;
    }

    public void setConsecutiveCrafts(int consecutiveCrafts) {
        this.consecutiveCrafts = consecutiveCrafts;
    }
}
