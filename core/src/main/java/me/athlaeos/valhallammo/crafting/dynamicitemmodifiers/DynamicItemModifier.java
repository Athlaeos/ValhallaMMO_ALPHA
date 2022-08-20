package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers;

import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

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

    public abstract ItemStack processItem(Player crafter, ItemStack outputItem);

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
}