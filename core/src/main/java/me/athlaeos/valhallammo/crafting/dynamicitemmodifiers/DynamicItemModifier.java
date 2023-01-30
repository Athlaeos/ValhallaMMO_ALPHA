package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers;

import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Comparator;
import java.util.List;

public abstract class DynamicItemModifier implements Cloneable{
    protected String name;
    protected double strength;
    protected ModifierCategory category = ModifierCategory.OTHER;

    // All these variables are related to how they're displayed in CreateCustomRecipeMenu
    protected String displayName; // Display name of its button
    protected String description; // Description displayed on its button
    protected String craftDescription; // Description displayed of modifier on crafting recipe
    protected boolean validate = true;
    protected boolean use = true;
    protected Material icon; // Icon of its button
    protected double minStrength; // Minimum range of strength
    protected double defaultStrength; // Where strength starts, must be within ranges
    protected double maxStrength; // Maximum range of strength
    protected double smallStepIncrease; // How much the strength is increased when the user left clicks the button
    protected double bigStepIncrease; // How much the strength is increased when the user shift-left clicks the button
    protected double smallStepDecrease; // How much the strength is increased when the user right clicks the button
    protected double bigStepDecrease; // How much the strength is increased when the user shift-right clicks the button
    protected ModifierPriority priority = ModifierPriority.NEUTRAL;

    public DynamicItemModifier(String name, double strength, ModifierPriority priority){
        this.name = name;
        this.strength = strength;
        if (priority != null){
            this.priority = priority;
        }

        craftDescription = TranslationManager.getInstance().getTranslation("modifier_" + this.name.toLowerCase());
    }

    /**
     * Allows edits to be made on items given a player, who's own personal stats can be used to dynamically alter the item.
     * @param crafter the player who's stats are to be used for the item
     * @param outputItem the resulting item after the modifier was executed. If null is returned, the recipe is considered "failed"
     * @return the item after processing
     */
    public ItemStack processItem(Player crafter, ItemStack outputItem){
        return processItem(crafter, outputItem, 1);
    }

    /**
     * Allows edits to be made on items given a player, who's own personal stats can be used to dynamically alter the item.
     * timesExecuted is a parameter that is by default 1, and often unused in implementations. This parameter may be used in situations where
     * the creation of the item rewards the player, such as experience or money. As example, if several of said item are crafted consecutively
     * then that reward should be multiplied by the amount of times the item is crafted.
     * @param crafter the player who's stats are to be used for the item
     * @param outputItem the resulting item after the modifier was executed. If null is returned, the recipe is considered "failed"
     * @param timesExecuted the amount of times the recipe is executed, may be used to multiply rewards if the recipe has been crafted several times at once
     * @return the item after processing
     */
    public abstract ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted);

    public void setValidate(boolean validate) {
        this.validate = validate;
    }

    public boolean validate() {
        return validate;
    }

    public abstract String toString();

    public String getCraftDescription() {
        return craftDescription;
    }

    public void setCraftDescription(String craftDescription) {
        this.craftDescription = craftDescription;
    }

    public String getName() {
        return name;
    }

    public double getStrength() {
        return strength;
    }

    public void setStrength(double strength) {
        this.strength = Utils.round(strength, 6);
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public Material getIcon() {
        return icon;
    }

    public double getMinStrength() {
        return minStrength;
    }

    public double getDefaultStrength() {
        return defaultStrength;
    }

    public double getMaxStrength() {
        return maxStrength;
    }

    public double getSmallStepIncrease() {
        return smallStepIncrease;
    }

    public double getBigStepIncrease() {
        return bigStepIncrease;
    }

    public double getSmallStepDecrease() {
        return smallStepDecrease;
    }

    public double getBigStepDecrease() {
        return bigStepDecrease;
    }

    public ModifierPriority getPriority() {
        return priority;
    }

    public ModifierCategory getCategory() {
        return category;
    }

    public boolean use() {
        return use;
    }

    public void setUse(boolean use) {
        this.use = use;
    }

    public void setPriority(ModifierPriority priority) {
        if (priority != null){
            this.priority = priority;
        }
    }

    public List<String> tabAutoCompleteFirstArg(){
        return null;
    }

    @Override
    public DynamicItemModifier clone() throws CloneNotSupportedException {
        return (DynamicItemModifier) super.clone();
    }

    /**
     * Modifies an itemstack given the player responsible for modifying and the modifiers to use
     * If -use- is true, modifiers will assume they're genuinely being used and so are allowed to maybe reward players with something.
     * This is because sometimes modifiers are executed with the purpose to predict an item outcome without actually rewarding the player with
     * whatever said modifiers are intended to reward.
     * -validate- can be used to ensure that a recipe is craftable.
     * Setting validate to false will not ignore fail conditions present in the modifiers.
     * @param i the item to modify
     * @param p the person responsible for modifying the item
     * @param modifiers the modifiers to use
     * @param sort if true, the modifiers will be sorted according to their execution priority
     * @param use whether to assume modifiers are genuinely being used with intent to create a new item for the player
     * @param validate whether to assume the modifiers are being executed with the intent to check if any fail conditions in the recipe pass
     * @return the item after modification, or null if the modifiers triggered a fail condition on the item.
     */
    public static ItemStack modify(ItemStack i, Player p, List<DynamicItemModifier> modifiers, boolean sort, boolean use, boolean validate, int count){
        ItemStack item = i.clone();
        if (sort) sortModifiers(modifiers);
        for (DynamicItemModifier modifier : modifiers){
            if (modifier instanceof AdvancedDynamicItemModifier) continue;
            if (item == null) break;
            try {
                DynamicItemModifier tempMod = modifier.clone();
                tempMod.setUse(use);
                tempMod.setValidate(validate);
                item = tempMod.processItem(p, item, count);
            } catch (CloneNotSupportedException ignored){
            }
        }

        return item;
    }
    public static ItemStack modify(ItemStack i, Player p, List<DynamicItemModifier> modifiers, boolean sort, boolean use, boolean validate){
        return modify(i, p, modifiers, sort, use, validate, 1);
    }

    public static AdvancedDynamicItemModifier.Pair<ItemStack, ItemStack> modify(ItemStack i1, ItemStack i2, Player p, List<DynamicItemModifier> modifiers, boolean sort, boolean use, boolean validate, int count){
        AdvancedDynamicItemModifier.Pair<ItemStack, ItemStack> pair = new AdvancedDynamicItemModifier.Pair<>(i1.clone(), i2.clone());
        if (sort) sortModifiers(modifiers);
        for (DynamicItemModifier modifier : modifiers){
            if (pair.getValue1() == null || pair.getValue2() == null) break;
            try {
                DynamicItemModifier tempMod = modifier.clone();
                tempMod.setUse(use);
                tempMod.setValidate(validate);
                if (tempMod instanceof AdvancedDynamicItemModifier)
                    pair = ((AdvancedDynamicItemModifier) tempMod).processItem(pair.getValue1(), pair.getValue2(), p, count);
                else {
                    ItemStack item1 = pair.getValue1();
                    ItemStack item2 = pair.getValue2();
                    item1 = tempMod.processItem(p, item1, count);
                    pair = new AdvancedDynamicItemModifier.Pair<>(item1, item2);
                }
            } catch (CloneNotSupportedException ignored){
            }
        }

        return pair;
    }

    public static AdvancedDynamicItemModifier.Pair<ItemStack, ItemStack> modify(ItemStack i1, ItemStack i2, Player p, List<DynamicItemModifier> modifiers, boolean sort, boolean use, boolean validate){
        return modify(i1, i2, p, modifiers, sort, use, validate, 1);
    }

    public static void sortModifiers(List<DynamicItemModifier> modifiers){
        modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
    }
}
