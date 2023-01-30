package me.athlaeos.valhallammo.crafting.recipetypes;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.utility.ItemUtils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.RecipeChoice;
import org.bukkit.inventory.SmithingRecipe;

import java.util.Collection;
import java.util.List;

public class DynamicSmithingTableRecipe implements Cloneable{
    private final NamespacedKey key;
    private ItemStack result;
    private ItemStack base;
    private ItemStack addition;
    private boolean useMetaBase;
    private boolean useMetaAddition;
    private boolean requireCustomTools;
    private boolean improveBase;
    private boolean consumeAddition;
    private boolean allowBaseVariations;
    private boolean allowAdditionVariations;
    private String name;
    private List<DynamicItemModifier> modifiersResult;
    private List<DynamicItemModifier> modifiersAddition;
    private boolean unlockedForEveryone;

    public DynamicSmithingTableRecipe(String name, ItemStack result, ItemStack base, ItemStack addition, boolean useMetaBase, boolean useMetaAddition, boolean requireCustomTools,
                                      boolean improveBase, boolean consumeAddition, boolean allowBaseVariations, boolean allowAdditionVariations, List<DynamicItemModifier> modifiersResult,
                                      List<DynamicItemModifier> modifiersAddition) {
        this.key = new NamespacedKey(ValhallaMMO.getPlugin(), "dynamic_smithing_" + name);
        this.name = name;
        this.result = result;
        this.base = base;
        this.addition = addition;
        this.useMetaBase = useMetaBase;
        this.useMetaAddition = useMetaAddition;
        this.requireCustomTools = requireCustomTools;
        this.improveBase = improveBase;
        this.consumeAddition = consumeAddition;
        this.allowBaseVariations = allowBaseVariations;
        this.allowAdditionVariations = allowAdditionVariations;
        this.modifiersResult = modifiersResult;
        this.modifiersAddition = modifiersAddition;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public void setBase(ItemStack base) {
        this.base = base;
    }

    public void setAddition(ItemStack addition) {
        this.addition = addition;
    }

    public void setUseMetaBase(boolean useMetaBase) {
        this.useMetaBase = useMetaBase;
    }

    public void setUseMetaAddition(boolean useMetaAddition) {
        this.useMetaAddition = useMetaAddition;
    }

    public void setRequireCustomTools(boolean requireCustomTools) {
        this.requireCustomTools = requireCustomTools;
    }

    public void setTinkerBase(boolean improveBase) {
        this.improveBase = improveBase;
    }

    public void setConsumeAddition(boolean consumeAddition) {
        this.consumeAddition = consumeAddition;
    }

    public void setAllowBaseVariations(boolean allowBaseVariations) {
        this.allowBaseVariations = allowBaseVariations;
    }

    public void setAllowAdditionVariations(boolean allowAdditionVariations) {
        this.allowAdditionVariations = allowAdditionVariations;
    }

    public void setModifiersResult(List<DynamicItemModifier> modifiersResult) {
        this.modifiersResult = modifiersResult;
    }

    public void setModifiersAddition(List<DynamicItemModifier> modifiersAddition) {
        this.modifiersAddition = modifiersAddition;
    }

    public void setUnlockedForEveryone(boolean unlockedForEveryone) {
        this.unlockedForEveryone = unlockedForEveryone;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public ItemStack getResult() {
        return result;
    }

    public ItemStack getBase() {
        return base;
    }

    public ItemStack getAddition() {
        return addition;
    }

    public boolean isUseMetaBase() {
        return useMetaBase;
    }

    public boolean isUseMetaAddition() {
        return useMetaAddition;
    }

    public boolean requireCustomTools() {
        return requireCustomTools;
    }

    public boolean isTinkerBase() {
        return improveBase;
    }

    public boolean isConsumeAddition() {
        return consumeAddition;
    }

    public boolean isAllowBaseVariations() {
        return allowBaseVariations;
    }

    public boolean isAllowAdditionVariations() {
        return allowAdditionVariations;
    }

    public String getName() {
        return name;
    }

    public List<DynamicItemModifier> getModifiersResult() {
        return modifiersResult;
    }

    public List<DynamicItemModifier> getModifiersAddition() {
        return modifiersAddition;
    }

    public boolean isUnlockedForEveryone() {
        return unlockedForEveryone;
    }

    public Recipe generateRecipe() {
        RecipeChoice base;
        RecipeChoice addition;
        if (allowBaseVariations){
            Collection<Material> similarTypes = ItemUtils.getSimilarMaterials(this.base.getType());
            if (similarTypes == null)
                base = useMetaBase ? (EquipmentClass.getClass(this.base) == null ? new RecipeChoice.ExactChoice(this.base) : new RecipeChoice.MaterialChoice(this.base.getType())) : new RecipeChoice.MaterialChoice(this.base.getType());
            else
                base = useMetaBase ? (EquipmentClass.getClass(this.base) == null ? new RecipeChoice.ExactChoice(this.base) : new RecipeChoice.MaterialChoice(similarTypes.toArray(new Material[0]))) : new RecipeChoice.MaterialChoice(similarTypes.toArray(new Material[0]));
        } else {
            base = useMetaBase ? (EquipmentClass.getClass(this.base) == null ? new RecipeChoice.ExactChoice(this.base) : new RecipeChoice.MaterialChoice(this.base.getType())) : new RecipeChoice.MaterialChoice(this.base.getType());
        }
        if (allowAdditionVariations){
            Collection<Material> similarTypes = ItemUtils.getSimilarMaterials(this.addition.getType());
            if (similarTypes == null)
                addition = useMetaAddition ? (EquipmentClass.getClass(this.addition) == null ? new RecipeChoice.ExactChoice(this.addition) : new RecipeChoice.MaterialChoice(this.addition.getType())) : new RecipeChoice.MaterialChoice(this.addition.getType());
            else
                addition = useMetaAddition ? (EquipmentClass.getClass(this.addition) == null ? new RecipeChoice.ExactChoice(this.addition) : new RecipeChoice.MaterialChoice(similarTypes.toArray(new Material[0]))) : new RecipeChoice.MaterialChoice(similarTypes.toArray(new Material[0]));
        } else {
            addition = useMetaAddition ? (EquipmentClass.getClass(this.addition) == null ? new RecipeChoice.ExactChoice(this.addition) : new RecipeChoice.MaterialChoice(this.addition.getType())) : new RecipeChoice.MaterialChoice(this.addition.getType());
        }

        return new SmithingRecipe(key, result, base, addition);
    }

    @Override
    public DynamicSmithingTableRecipe clone() {
        final DynamicSmithingTableRecipe clone;
        try {
            clone = (DynamicSmithingTableRecipe) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Exception occurred calling DynamicSmithingTableRecipe.clone()", ex);
        }
        return clone;
    }
}
