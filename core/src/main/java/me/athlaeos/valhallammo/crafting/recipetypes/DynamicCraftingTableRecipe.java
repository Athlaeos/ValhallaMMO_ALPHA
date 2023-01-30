package me.athlaeos.valhallammo.crafting.recipetypes;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.*;

import java.util.*;
import java.util.stream.Collectors;

public class DynamicCraftingTableRecipe implements Cloneable{
    private Map<Integer, ItemStack> exactItems;
    private final NamespacedKey key;
    private ItemStack result;
    private boolean useMetadata;
    private boolean requireCustomTools;
    private boolean improveFirstEquipment;
    private boolean shapeless;
    private boolean allowIngredientVariations;
    private String name;
    private List<DynamicItemModifier> modifiers;
    private boolean unlockedForEveryone;
    protected int requiredToolId = -1;
    protected int toolRequirementType = 0;

    public DynamicCraftingTableRecipe(String name, ItemStack result, Map<Integer, ItemStack> exactItems, boolean unlockedForEveryone, boolean useMetadata, boolean allowIngredientVariations, boolean requireCustomTools, boolean improveFirstEquipment, boolean shapeless, List<DynamicItemModifier> modifiers) {
        this.key = new NamespacedKey(ValhallaMMO.getPlugin(), "dynamic_shaped_" + name);
        this.result = result;
        this.exactItems = exactItems;
        this.useMetadata = useMetadata;
        this.allowIngredientVariations = allowIngredientVariations;
        this.unlockedForEveryone = unlockedForEveryone;
        this.requireCustomTools = requireCustomTools;
        this.improveFirstEquipment = improveFirstEquipment;
        this.name = name;
        this.modifiers = modifiers;
        this.shapeless = shapeless;
    }

    public int getRequiredToolId() {
        return requiredToolId;
    }

    public int getToolRequirementType() {
        return toolRequirementType;
    }

    public void setRequiredToolId(int requiredToolId) {
        this.requiredToolId = requiredToolId;
    }

    public void setToolRequirementType(int toolRequirementType) {
        this.toolRequirementType = toolRequirementType;
    }

    public void setUnlockedForEveryone(boolean unlockedForEveryone) {
        this.unlockedForEveryone = unlockedForEveryone;
    }

    public boolean isUnlockedForEveryone() {
        return unlockedForEveryone;
    }

    public boolean isAllowIngredientVariations() {
        return allowIngredientVariations;
    }

    public void setAllowIngredientVariations(boolean allowIngredientVariations) {
        this.allowIngredientVariations = allowIngredientVariations;
    }

    public Recipe generateRecipe() {
        if (shapeless){
            ShapelessRecipe recipe = new ShapelessRecipe(key, improveFirstEquipment ? getFirstEquipmentFromMatrix() : result);
            if (allowIngredientVariations && !useMetadata){
                // if metadata is allowed it would break shapeless crafting, so allowIngredientVariations is ignored if useMetaData is enabled
                exactItems.forEach((integer, itemStack) -> {
                    Collection<Material> similarTypes = ItemUtils.getSimilarMaterials(itemStack.getType());
                    if (similarTypes == null)
                        recipe.addIngredient(itemStack.getType());
                    else
                        recipe.addIngredient(new RecipeChoice.MaterialChoice(similarTypes.toArray(new Material[0])));
                });
            } else {
                exactItems.forEach((integer, itemStack) -> recipe.addIngredient(itemStack.getType()));
            }
            return recipe;
        } else {
            ShapedRecipe recipe = new ShapedRecipe(key, improveFirstEquipment ? getFirstEquipmentFromMatrix() : result);
            ShapeDetails details = getRecipeShapeStrings();
            recipe.shape(details.shape);
            for (char ci : details.items.keySet()){
                ItemStack i = details.items.get(ci);
                if (i == null) continue;
                if (useMetadata) {
                    if (improveFirstEquipment){
                        // exact recipe choice unless item is equipment
                        if (EquipmentClass.getClass(i) == null)
                            recipe.setIngredient(ci, new RecipeChoice.ExactChoice(i));
                        else
                            recipe.setIngredient(ci, i.getType());
                    } else {
                        // all ingredients need to match exactly
                        recipe.setIngredient(ci, new RecipeChoice.ExactChoice(i));
                    }
                } else {
                    // all ingredients need to only match in type
                    if (allowIngredientVariations){
                        // ingredient variations of such a type is allowed, so checking if the material has family members and if so allow them too
                        Collection<Material> similarTypes = ItemUtils.getSimilarMaterials(i.getType());
                        if (similarTypes == null)
                            recipe.setIngredient(ci, i.getType());
                        else
                            recipe.setIngredient(ci, new RecipeChoice.MaterialChoice(similarTypes.toArray(new Material[0])));
                    } else {
                        // ingredient variations of this type are not allowed, so only this type is permitted
                        recipe.setIngredient(ci, i.getType());
                    }
                }
            }
            return recipe;
        }
    }

    public ItemStack getFirstEquipmentFromMatrix(){
        ItemStack equipment = Utils.createItemStack(Material.WOODEN_SWORD, Utils.chat("&cOops!"),
                Arrays.asList(
                        Utils.chat("&cThis recipe is uncraftable, tell an admin!"),
                        Utils.chat("&7Recipe: " + this.name)
                ));
        for (int i = 0; i < 9; i++){
            ItemStack matrixItem = exactItems.get(i);
            if (Utils.isItemEmptyOrNull(matrixItem)) continue;

            if (this.improveFirstEquipment && EquipmentClass.getClass(matrixItem) != null) {
                equipment = matrixItem.clone();
                break;
            }
        }
        return equipment;
    }

    public ShapeDetails getRecipeShapeStrings(){
        List<String> shape = new ArrayList<>();
        BiMap<Character, ItemStack> ingredientMap = HashBiMap.create();
        int i = 0;
        StringBuilder usedChars = new StringBuilder();
        for (int r = 0; r < 3; r++) {
            StringBuilder row = new StringBuilder();
            for (int c = 0; c < 3; c++){
                ItemStack item = exactItems.get(i);
                i++;
                Character itemChar = ingredientMap.inverse().get(item);
                if (itemChar == null) itemChar = getItemChar(item, usedChars.toString());
                row.append(itemChar);
                if (!ingredientMap.containsValue(item)) {
                    usedChars.append(itemChar);
                    ingredientMap.put(itemChar, item);
                }
            }
            shape.add(row.toString());
        }

        // trimming shape
        if (shape.get(0).equalsIgnoreCase("   ") && shape.get(1).equalsIgnoreCase("   ")) { shape.remove(0); shape.remove(0); } // if top two rows are empty, remove them
        else if (shape.get(0).equalsIgnoreCase("   ")) shape.remove(0); // if only top row is empty, remove it

        if (shape.size() == 3 && shape.get(shape.size() - 1).equalsIgnoreCase("   ") && shape.get(shape.size() - 2).equalsIgnoreCase("   ")) { shape.remove(shape.size() - 1); shape.remove(shape.size() - 1); } // if bottom two rows are empty, remove them
        else if (shape.get(shape.size() - 1).equalsIgnoreCase("   ")) shape.remove(shape.size() - 1); // if only bottom row is empty, remove it

        if (shape.stream().allMatch(s -> s.endsWith("  "))) shape = shape.stream().map(s -> s = s.substring(0, s.length() - 2)).collect(Collectors.toList()); // if last 2 characters of each line is empty, remove them
        if (shape.stream().allMatch(s -> s.endsWith(" "))) shape = shape.stream().map(s -> s = s.substring(0, s.length() - 1)).collect(Collectors.toList()); // if last character of each line is empty, remove it
        if (shape.stream().allMatch(s -> s.startsWith("  "))) shape = shape.stream().map(s -> s = s.substring(2)).collect(Collectors.toList()); // if first 2 characters of each line is empty, remove them
        if (shape.stream().allMatch(s -> s.startsWith(" "))) shape = shape.stream().map(s -> s = s.substring(1)).collect(Collectors.toList()); // if first character of each line is empty, remove it

        return new ShapeDetails(shape.toArray(new String[0]), ingredientMap);
    }

    /**
     * Returns a freely available character given an itemstack. The character will first prioritize the first letter of
     * any custom name the item has, and then the first letter of the material name if that character is either already
     * in use or if the item has no custom name at all. If the character is occupied still, it will return the first available
     * letter in the alphabet (there are only 9 max characters, so this is limited to the first 9 letters of the alphabet)
     * @param i the itemstack to base a character on
     * @param usedChars a string of characters that have already been used
     * @return the best available character for the itemstack
     */
    private char getItemChar(ItemStack i, String usedChars){
        if (Utils.isItemEmptyOrNull(i)) return ' ';
        String itemName = Utils.getItemName(i);
        char possibleCharacter = ChatColor.stripColor(itemName.isEmpty() ? getTypeName(i) : itemName).toUpperCase().charAt(0);
        if (usedChars.contains("" + possibleCharacter)){
            possibleCharacter = i.getType().toString().toUpperCase().charAt(0);
            if (usedChars.contains("" + possibleCharacter)) {
                for (Character c : Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I')) {
                    if (!usedChars.contains("" + c)) {
                        return c;
                    }
                }
            }
        }
        return possibleCharacter;
    }

    private String getTypeName(ItemStack i){
        String name;
        assert i.getItemMeta() != null;
        if (TranslationManager.getInstance().getLocalizedMaterialNames().containsKey(i.getType())){
            name = Utils.chat(TranslationManager.getInstance().getLocalizedMaterialNames().get(i.getType()));
        } else if (i.getItemMeta().hasLocalizedName()){
            name = Utils.chat(i.getItemMeta().getLocalizedName());
        } else {
            name = i.getType().toString().toLowerCase().replace("_", " ");
        }
        return name;
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

    public ItemStack getResult() {
        return result;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public List<DynamicItemModifier> getItemModifiers() {
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

    public void setModifiers(List<DynamicItemModifier> modifiers) {
        this.modifiers = modifiers;
    }

    public boolean isTinkerFirstItem() {
        return improveFirstEquipment;
    }

    public void setImproveFirstEquipment(boolean improveFirstEquipment) {
        this.improveFirstEquipment = improveFirstEquipment;
    }

    public boolean isShapeless() {
        return shapeless;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public void setShapeless(boolean shapeless) {
        this.shapeless = shapeless;
    }

    @Override
    public DynamicCraftingTableRecipe clone() {
        final DynamicCraftingTableRecipe clone;
        try {
            clone = (DynamicCraftingTableRecipe) super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new RuntimeException("Exception occurred calling DynamicShapedRecipe.clone()", ex);
        }
        return clone;
    }

    public static class ShapeDetails{
        String[] shape;
        Map<Character, ItemStack> items;

        public ShapeDetails(String[] shape, Map<Character, ItemStack> items){
            this.shape = shape;
            this.items = items;
        }

        public String[] getShape() {
            return shape;
        }

        public Map<Character, ItemStack> getItems() {
            return items;
        }
    }
}
