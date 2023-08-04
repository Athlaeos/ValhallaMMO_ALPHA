package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.DynamicItemModifierManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.*;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.BlockCraftStateValidationManager;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.items.blockstatevalidations.CraftValidation;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.*;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CustomRecipeManager {
    private static boolean shouldSaveRecipes = false;

    public static void shouldSaveRecipes() {
        shouldSaveRecipes = true;
    }

    public static boolean isShouldSaveRecipes(){
        return shouldSaveRecipes;
    }

    private static CustomRecipeManager manager = null;
    private final List<NamespacedKey> disabledRecipes = new ArrayList<>();

    private final Collection<String> allRecipes = new HashSet<>();

    public Collection<String> getAllRecipes() {
        return allRecipes;
    }

    private final Map<String, AbstractCustomCraftingRecipe> allCustomRecipes = new TreeMap<>();
    private final Map<Material, Collection<ItemCraftingRecipe>> craftingStationRecipes = new HashMap<>();
    // crafting station, recipes to crafting station
    private final Map<Material, Map<Material, Collection<ItemImprovementRecipe>>> itemImprovementRecipes = new HashMap<>();
    // crafting station, apply material, improvement recipes to both apply material and crafting station
    private final Map<Material, Map<EquipmentClass, Collection<ItemClassImprovementRecipe>>> itemClassImprovementRecipes = new HashMap<>();
    // crafting station, equipment class, improvement recipes to this equipment class and crafting station

    private final Map<String, DynamicBrewingRecipe> brewingRecipes = new HashMap<>();
    private final Map<String, DynamicCauldronRecipe> cauldronRecipes = new HashMap<>();
    private final Map<String, DynamicCraftingTableRecipe> shapedRecipes = new HashMap<>();
    private final Map<String, DynamicSmithingTableRecipe> smithingRecipes = new HashMap<>();
    private final Map<String, DynamicCookingRecipe<?>> cookingRecipes = new HashMap<>();
    private final Map<NamespacedKey, DynamicCraftingTableRecipe> shapedRecipesByKey = new HashMap<>();
    private final Map<NamespacedKey, DynamicSmithingTableRecipe> smithingRecipesByKey = new HashMap<>();
    private final Map<NamespacedKey, DynamicCookingRecipe<?>> cookingRecipesByKey = new HashMap<>();

    private final Map<Material, Collection<DynamicBrewingRecipe>> unspecificBrewingRecipes = new HashMap<>();
    private final Map<ItemStack, Collection<DynamicBrewingRecipe>> specificBrewingRecipes = new HashMap<>();

    public static CustomRecipeManager getInstance(){
        if (manager == null) manager = new CustomRecipeManager();
        return manager;
    }

    /**
     * Registers a DynamicShapedRecipe to be used in the vanilla crafting table.
     * @param recipe the recipe to register
     * @return true if the recipe has been successfully registered, and false if a recipe with the given name already exists.
     */
    public boolean register(DynamicCraftingTableRecipe recipe){
        if (allRecipes.contains(recipe.getName())) {
            return false;
        }
        DynamicItemModifier.sortModifiers(recipe.getItemModifiers());
        shapedRecipes.put(recipe.getName(), recipe);
        shapedRecipesByKey.put(recipe.getKey(), recipe);
        allRecipes.add(recipe.getName());
        ValhallaMMO.getPlugin().getServer().removeRecipe(recipe.getKey());

        ValhallaMMO.getPlugin().getServer().addRecipe(recipe.generateRecipe());
        return true;
    }
    /**
     * Registers a DynamicSmithingTableRecipe to be used in the vanilla smithing table.
     * @param recipe the recipe to register
     * @return true if the recipe has been successfully registered, and false if a recipe with the given name already exists.
     */
    public boolean register(DynamicSmithingTableRecipe recipe){
        if (allRecipes.contains(recipe.getName())) {
            return false;
        }
        DynamicItemModifier.sortModifiers(recipe.getModifiersAddition());
        DynamicItemModifier.sortModifiers(recipe.getModifiersResult());
        smithingRecipes.put(recipe.getName(), recipe);
        smithingRecipesByKey.put(recipe.getKey(), recipe);
        allRecipes.add(recipe.getName());
        ValhallaMMO.getPlugin().getServer().removeRecipe(recipe.getKey());
        ValhallaMMO.getPlugin().getServer().addRecipe(recipe.generateRecipe());
        return true;
    }

    /**
     * Registers a DynamicShapedRecipe to be used in the vanilla crafting table.
     * @param recipe the recipe to register
     * @return true if the recipe has been successfully registered, and false if a recipe with the given name already exists.
     */
    public <T extends CookingRecipe<T>> boolean register(DynamicCookingRecipe<T> recipe){
        if (allRecipes.contains(recipe.getName())) {
            return false;
        }
        DynamicItemModifier.sortModifiers(recipe.getModifiers());
        cookingRecipes.put(recipe.getName(),  recipe);
        cookingRecipesByKey.put(recipe.generateRecipe().getKey(), recipe);
        allRecipes.add(recipe.getName());

        ValhallaMMO.getPlugin().getServer().addRecipe(recipe.generateRecipe());
        return true;
    }

    /**
     * Registers a BrewingRecipe to be used in the vanilla brewing stand.
     * @param recipe the recipe to register
     * @return true if the recipe has been successfully registered, and false if a recipe with the given name already exists.
     */
    public boolean register(DynamicBrewingRecipe recipe){
        if (allRecipes.contains(recipe.getName())) {
            return false;
        }
        DynamicItemModifier.sortModifiers(recipe.getItemModifiers());
        brewingRecipes.put(recipe.getName(), recipe);
        Collection<DynamicBrewingRecipe> existingRecipes;
        if (Utils.isItemEmptyOrNull(recipe.getIngredient())) return false;
        if (recipe.isPerfectMeta()){
            existingRecipes = specificBrewingRecipes.get(recipe.getIngredient());
            if (existingRecipes == null) existingRecipes = new HashSet<>();
            existingRecipes.add(recipe);
            specificBrewingRecipes.put(recipe.getIngredient(), existingRecipes);
            allRecipes.add(recipe.getName());
        } else {
            existingRecipes = unspecificBrewingRecipes.get(recipe.getIngredient().getType());
            if (existingRecipes == null) existingRecipes = new HashSet<>();
            existingRecipes.add(recipe);
            unspecificBrewingRecipes.put(recipe.getIngredient().getType(), existingRecipes);
            allRecipes.add(recipe.getName());
        }
        return true;
    }

    /**
     * Registers a DynamicCauldronRecipe to be used in ValhallaMMO's custom crafting methods.
     * @param recipe the recipe to register
     * @return true if the recipe has been successfully registered, and false if a recipe with the given name already exists.
     */
    public boolean register(DynamicCauldronRecipe recipe){
        if (allRecipes.contains(recipe.getName())) {
            return false;
        }
        DynamicItemModifier.sortModifiers(recipe.getItemModifiers());
        cauldronRecipes.put(recipe.getName(), recipe);
        allRecipes.add(recipe.getName());
        return true;
    }

    /**
     * Registers an AbstractCustomCraftingRecipe to be used in ValhallaMMO's custom crafting methods.
     * @param recipe the recipe to register
     * @return true if the recipe has been successfully registered, and false if a recipe with the given name already exists.
     */
    public boolean register(AbstractCustomCraftingRecipe recipe){
        if (allRecipes.contains(recipe.getName())) {
            return false;
        }
        DynamicItemModifier.sortModifiers(recipe.getItemModifiers());
        if (recipe instanceof ItemCraftingRecipe){
            Collection<ItemCraftingRecipe> recipes;
            if (craftingStationRecipes.containsKey(recipe.getCraftingBlock())){
                recipes = craftingStationRecipes.get(recipe.getCraftingBlock());
            } else {
                recipes = new HashSet<>();
            }
            recipes.add((ItemCraftingRecipe) recipe);
            craftingStationRecipes.put(recipe.getCraftingBlock(), recipes);
        } else if (recipe instanceof ItemImprovementRecipe){
            Map<Material, Collection<ItemImprovementRecipe>> existingRecipes = new HashMap<>();
            if (itemImprovementRecipes.containsKey(recipe.getCraftingBlock())) {
                existingRecipes = itemImprovementRecipes.get(recipe.getCraftingBlock());
            }
            Collection<ItemImprovementRecipe> recipes = new HashSet<>();
            if (existingRecipes.containsKey(((ItemImprovementRecipe) recipe).getRequiredItemType())){
                recipes.addAll(existingRecipes.get(((ItemImprovementRecipe) recipe).getRequiredItemType()));
            }
            recipes.add((ItemImprovementRecipe) recipe);
            existingRecipes.put(((ItemImprovementRecipe) recipe).getRequiredItemType(), recipes);
            itemImprovementRecipes.put(recipe.getCraftingBlock(), existingRecipes);
        } else if (recipe instanceof ItemClassImprovementRecipe){
            Map<EquipmentClass, Collection<ItemClassImprovementRecipe>> existingRecipes = new HashMap<>();
            if (itemClassImprovementRecipes.containsKey(recipe.getCraftingBlock())) {
                existingRecipes = itemClassImprovementRecipes.get(recipe.getCraftingBlock());
            }
            Collection<ItemClassImprovementRecipe> recipes = new HashSet<>();
            if (existingRecipes.containsKey(((ItemClassImprovementRecipe) recipe).getRequiredEquipmentClass())){
                recipes.addAll(existingRecipes.get(((ItemClassImprovementRecipe) recipe).getRequiredEquipmentClass()));
            }
            recipes.add((ItemClassImprovementRecipe) recipe);
            existingRecipes.put(((ItemClassImprovementRecipe) recipe).getRequiredEquipmentClass(), recipes);
            itemClassImprovementRecipes.put(recipe.getCraftingBlock(), existingRecipes);
        }
        allCustomRecipes.put(recipe.getName(), recipe);
        allRecipes.add(recipe.getName());
        return true;
    }

    /**
     * Updates a AbstractCustomCraftingRecipe by unregistering the old recipe and registering the new. If the old recipe is null,
     * it just registers the new one.
     * @param oldRecipe the recipe to replace
     * @param newRecipe the updated recipe
     */
    public void update(AbstractCustomCraftingRecipe oldRecipe, AbstractCustomCraftingRecipe newRecipe){
        if (newRecipe == null) {
            return;
        }
        if (oldRecipe != null){
            unregister(oldRecipe);
        }
        register(newRecipe);
        CustomRecipeManager.shouldSaveRecipes();
    }
    /**
     * Updates a DynamicCauldronRecipe by unregistering the old recipe and registering the new. If the old recipe is null,
     * it just registers the new one.
     * @param oldRecipe the recipe to replace
     * @param newRecipe the updated recipe
     */
    public void update(DynamicCauldronRecipe oldRecipe, DynamicCauldronRecipe newRecipe){
        if (newRecipe == null) {
            return;
        }
        if (oldRecipe != null){
            unregister(oldRecipe);
        }
        register(newRecipe);
        CustomRecipeManager.shouldSaveRecipes();
    }

    /**
     * Updates a DynamicShapedRecipe by unregistering the old recipe and registering the new. If the old recipe is null,
     * it just registers the new one.
     * @param oldRecipe the recipe to replace
     * @param newRecipe the updated recipe
     */
    public void update(DynamicCraftingTableRecipe oldRecipe, DynamicCraftingTableRecipe newRecipe){
        if (newRecipe == null) {
            return;
        }
        if (oldRecipe != null){
            unregister(oldRecipe);
        }
        register(newRecipe);
        saveRecipe(newRecipe, ConfigManager.getInstance().getConfig("recipes/shaped_recipes.yml").get());
        CustomRecipeManager.shouldSaveRecipes();
    }
    /**
     * Updates a DynamicSmithingTableRecipe by unregistering the old recipe and registering the new. If the old recipe is null,
     * it just registers the new one.
     * @param oldRecipe the recipe to replace
     * @param newRecipe the updated recipe
     */
    public void update(DynamicSmithingTableRecipe oldRecipe, DynamicSmithingTableRecipe newRecipe){
        if (newRecipe == null) {
            return;
        }
        if (oldRecipe != null){
            unregister(oldRecipe);
        }
        register(newRecipe);
        saveRecipe(newRecipe, ConfigManager.getInstance().getConfig("recipes/smithing_table_recipes.yml").get());
        CustomRecipeManager.shouldSaveRecipes();
    }

    public void update(DynamicCookingRecipe<?> oldRecipe, DynamicCookingRecipe<?> newRecipe){
        if (newRecipe == null) {
            return;
        }
        if (oldRecipe != null){
            unregister(oldRecipe);
        }
        register(newRecipe);
        saveRecipe(newRecipe, ConfigManager.getInstance().getConfig("recipes/cooking_recipes.yml").get());
        CustomRecipeManager.shouldSaveRecipes();
    }

    /**
     * Updates a BrewingRecipe by unregistering the old recipe and registering the new. If the old recipe is null,
     * it just registers the new one.
     * @param oldRecipe the recipe to replace
     * @param newRecipe the updated recipe
     */
    public void update(DynamicBrewingRecipe oldRecipe, DynamicBrewingRecipe newRecipe){
        if (newRecipe == null) {
            return;
        }
        if (oldRecipe != null){
            unregister(oldRecipe);
        }
        register(newRecipe);
        saveRecipe(newRecipe, ConfigManager.getInstance().getConfig("recipes/brewing_recipes.yml").get());
        CustomRecipeManager.shouldSaveRecipes();
    }

    /**
     * Unregisters a AbstractCustomCraftingRecipe, after this the recipe will no longer be craftable.
     * The recipe is also deleted from the configs.
     * @param recipe the recipe to unregister
     * @return true if the recipe was successfully removed, and false if the recipe didn't exist.
     */
    public boolean unregister(AbstractCustomCraftingRecipe recipe){
        if (recipe == null) return true;
        if (!allCustomRecipes.containsKey(recipe.getName())) return false;
        allCustomRecipes.remove(recipe.getName());
        allRecipes.remove(recipe.getName());
        if (recipe instanceof ItemCraftingRecipe){
            if (craftingStationRecipes.containsKey(recipe.getCraftingBlock())){
                Collection<ItemCraftingRecipe> recipes = craftingStationRecipes.get(recipe.getCraftingBlock());
                if (recipes != null){
                    recipes.removeIf(itemCraftingRecipe -> itemCraftingRecipe.getName().equals(recipe.getName()));
                    craftingStationRecipes.put(recipe.getCraftingBlock(), recipes);
                    ConfigManager.getInstance().getConfig("recipes/crafting_recipes.yml").get().set("craft." + recipe.getName(), null);
                    ConfigManager.getInstance().getConfig("recipes/crafting_recipes.yml").save();
                }
            }
        } else if (recipe instanceof ItemImprovementRecipe){
            Map<Material, Collection<ItemImprovementRecipe>> improvementRecipes = itemImprovementRecipes.get(recipe.getCraftingBlock());
            if (improvementRecipes != null){
                Collection<ItemImprovementRecipe> recipes = improvementRecipes.get(((ItemImprovementRecipe) recipe).getRequiredItemType());
                if (recipes != null){
                    recipes.removeIf(itemImprovementRecipe -> itemImprovementRecipe.getName().equals(recipe.getName()));
                    improvementRecipes.put(((ItemImprovementRecipe) recipe).getRequiredItemType(), recipes);
                    itemImprovementRecipes.put(recipe.getCraftingBlock(), improvementRecipes);
                    ConfigManager.getInstance().getConfig("recipes/improvement_recipes.yml").get().set("improve." + recipe.getName(), null);
                    ConfigManager.getInstance().getConfig("recipes/improvement_recipes.yml").save();
                }
            }
        } else if (recipe instanceof ItemClassImprovementRecipe){
            Map<EquipmentClass, Collection<ItemClassImprovementRecipe>> improvementRecipes = itemClassImprovementRecipes.get(recipe.getCraftingBlock());
            if (improvementRecipes != null){
                Collection<ItemClassImprovementRecipe> recipes = improvementRecipes.get(((ItemClassImprovementRecipe) recipe).getRequiredEquipmentClass());
                if (recipes != null){
                    recipes.removeIf(itemImprovementRecipe -> itemImprovementRecipe.getName().equals(recipe.getName()));
                    improvementRecipes.put(((ItemClassImprovementRecipe) recipe).getRequiredEquipmentClass(), recipes);
                    itemClassImprovementRecipes.put(recipe.getCraftingBlock(), improvementRecipes);
                    ConfigManager.getInstance().getConfig("recipes/class_improvement_recipes.yml").get().set("class_improve." + recipe.getName(), null);
                    ConfigManager.getInstance().getConfig("recipes/class_improvement_recipes.yml").save();
                }
            }
        }
        shouldSaveRecipes();
        return true;
    }

    /**
     * Unregisters a DynamicShapedRecipe, after this the recipe will no longer be craftable.
     * The recipe is also deleted from the configs.
     * @param recipe the recipe to unregister
     * @return true if the recipe was successfully removed, and false if the recipe didn't exist.
     */
    public boolean unregister(DynamicCraftingTableRecipe recipe){
        if (recipe == null) return true;
        if (!shapedRecipes.containsKey(recipe.getName())) {
            return false;
        }
        shapedRecipes.remove(recipe.getName());
        shapedRecipesByKey.remove(recipe.getKey());
        allRecipes.remove(recipe.getName());
        ValhallaMMO.getPlugin().getServer().removeRecipe(recipe.getKey());
        ConfigManager.getInstance().getConfig("recipes/shaped_recipes.yml").get().set("shaped." + recipe.getName(), null);
        ConfigManager.getInstance().getConfig("recipes/shaped_recipes.yml").save();
        shouldSaveRecipes();
        return true;
    }
    /**
     * Unregisters a DynamicShapedRecipe, after this the recipe will no longer be craftable.
     * The recipe is also deleted from the configs.
     * @param recipe the recipe to unregister
     * @return true if the recipe was successfully removed, and false if the recipe didn't exist.
     */
    public boolean unregister(DynamicCauldronRecipe recipe){
        if (recipe == null) return true;
        if (!cauldronRecipes.containsKey(recipe.getName())) {
            return false;
        }
        cauldronRecipes.remove(recipe.getName());
        allRecipes.remove(recipe.getName());
        ConfigManager.getInstance().getConfig("recipes/cauldron_recipes.yml").get().set("cauldron." + recipe.getName(), null);
        ConfigManager.getInstance().getConfig("recipes/cauldron_recipes.yml").save();
        shouldSaveRecipes();
        return true;
    }

    /**
     * Unregisters a DynamicSmithingTableRecipe, after this the recipe will no longer be craftable.
     * The recipe is also deleted from the configs.
     * @param recipe the recipe to unregister
     * @return true if the recipe was successfully removed, and false if the recipe didn't exist.
     */
    public boolean unregister(DynamicSmithingTableRecipe recipe){
        if (recipe == null) return true;
        if (!smithingRecipes.containsKey(recipe.getName())) {
            return false;
        }
        smithingRecipes.remove(recipe.getName());
        smithingRecipesByKey.remove(recipe.getKey());
        allRecipes.remove(recipe.getName());
        ValhallaMMO.getPlugin().getServer().removeRecipe(recipe.getKey());
        ConfigManager.getInstance().getConfig("recipes/smithing_table_recipes.yml").get().set("smithing." + recipe.getName(), null);
        ConfigManager.getInstance().getConfig("recipes/smithing_table_recipes.yml").save();
        shouldSaveRecipes();
        return true;
    }

    public <T extends CookingRecipe<T>> boolean unregister(DynamicCookingRecipe<T> recipe){
        if (recipe == null) return true;
        if (!cookingRecipes.containsKey(recipe.getName())) {
            return false;
        }
        cookingRecipes.remove(recipe.getName());
        cookingRecipesByKey.remove(recipe.generateRecipe().getKey());
        allRecipes.remove(recipe.getName());
        ConfigManager.getInstance().getConfig("recipes/cooking_recipes.yml").get().set("cooking." + recipe.getName(), null);
        ConfigManager.getInstance().getConfig("recipes/cooking_recipes.yml").save();
        ValhallaMMO.getPlugin().getServer().removeRecipe(recipe.generateRecipe().getKey());
        shouldSaveRecipes();
        return true;
    }

    /**
     * Unregisters a DynamicShapedRecipe, after this the recipe will no longer be craftable.
     * The recipe is also deleted from the configs.
     * @param recipe the recipe to unregister
     * @return true if the recipe was successfully removed, and false if the recipe didn't exist.
     */
    public boolean unregister(DynamicBrewingRecipe recipe){
        if (recipe == null) return true;
        if (!brewingRecipes.containsKey(recipe.getName())) {
            return false;
        }
        brewingRecipes.remove(recipe.getName());
        allRecipes.remove(recipe.getName());
        if (!Utils.isItemEmptyOrNull(recipe.getIngredient())){
            Collection<DynamicBrewingRecipe> existingUnspecificRecipes = unspecificBrewingRecipes.get(recipe.getIngredient().getType());
            if (existingUnspecificRecipes != null){
                existingUnspecificRecipes.removeIf(dynamicBrewingRecipe -> dynamicBrewingRecipe.getName().equals(recipe.getName()));
                unspecificBrewingRecipes.put(recipe.getIngredient().getType(), existingUnspecificRecipes);
            }
            Collection<DynamicBrewingRecipe> existingSpecificRecipes = specificBrewingRecipes.get(recipe.getIngredient());
            if (existingSpecificRecipes != null){
                existingSpecificRecipes.removeIf(dynamicBrewingRecipe -> dynamicBrewingRecipe.getName().equals(recipe.getName()));
                specificBrewingRecipes.put(recipe.getIngredient(), existingSpecificRecipes);
            }
        }
        ConfigManager.getInstance().getConfig("recipes/brewing_recipes.yml").get().set("brewing." + recipe.getName(), null);
        ConfigManager.getInstance().getConfig("recipes/brewing_recipes.yml").save();
        shouldSaveRecipes();
        return true;
    }

    /**
     * @param name the name of the AbstractCustomCraftingRecipe
     * @return the SmithingRecipe with the given name, or null if it doesn't exist.
     */
    public AbstractCustomCraftingRecipe getRecipeByName(String name){
        if (allCustomRecipes.containsKey(name)){
            return allCustomRecipes.get(name);
        }
        return null;
    }

    public boolean recipeExists(String name) {
        return allRecipes.contains(name);
    }

    /**
     * Returns all the Item Crafting Recipes belonging to a given crafting station.
     * @param block the crafting station the player has to hold to craft an item
     * @return a list of all CustomCraftingRecipes applicable to a specific crafting station
     */
    public Collection<AbstractCustomCraftingRecipe> getRecipesByCraftingStation(Material block){
        if (craftingStationRecipes.containsKey(block)){
            return new HashSet<>(craftingStationRecipes.get(block));
        }
        return new HashSet<>();
    }

    /**
     * Returns all the Item Improvement Recipes belonging to the given crafting station and held item
     * @param block the crafting station the player has to hold to craft an item
     * @param item the held item type
     * @return a list of all CustomCraftingRecipes applicable to a specific crafting station
     */
    public Collection<AbstractCustomCraftingRecipe> getRecipesByCraftingStation(Material block, ItemStack item){
        Collection<AbstractCustomCraftingRecipe> recipes = new HashSet<>();
        if (itemImprovementRecipes.containsKey(block)){
            if (itemImprovementRecipes.get(block).containsKey(item.getType())){
                recipes.addAll(itemImprovementRecipes.get(block).get(item.getType()));
            }
        }
        EquipmentClass equipmentClass = EquipmentClass.getClass(item);
        if (equipmentClass != null){
            if (itemClassImprovementRecipes.containsKey(block)){
                if (itemClassImprovementRecipes.get(block).containsKey(equipmentClass)){
                    recipes.addAll(itemClassImprovementRecipes.get(block).get(equipmentClass));
                }
            }
        }
        return recipes;
    }

    /**
     * Returns all the Item Class Improvement Recipes belonging to the given crafting station and equipment class
     * @param block the crafting station the player has to hold to craft an item
     * @param clazz the item class
     * @return a list of all AbstractCustomCraftingRecipes applicable to a specific crafting station
     */
    public Collection<AbstractCustomCraftingRecipe> getRecipesByCraftingStation(Material block, EquipmentClass clazz){
        if (itemClassImprovementRecipes.containsKey(block)){
            if (itemClassImprovementRecipes.get(block).containsKey(clazz)){
                return new HashSet<>(itemClassImprovementRecipes.get(block).get(clazz));
            }
        }
        return new HashSet<>();
    }

    /**
     * @return all CustomCraftingRecipes in a map, where the key is the recipe's ID.
     */
    public Map<String, AbstractCustomCraftingRecipe> getAllCustomRecipes() {
        return allCustomRecipes;
    }

    /**
     * Get all registered crafting station CustomCraftingRecipes
     * @return a Map where the Material represents the crafting station and where the Collection represents all the
     * CustomCraftingRecipes applicable to that crafting station
     */
    public Map<Material, Collection<ItemCraftingRecipe>> getCraftingStationRecipes() {
        return craftingStationRecipes;
    }

    public Map<NamespacedKey, DynamicCookingRecipe<?>> getCookingRecipesByKey() {
        return cookingRecipesByKey;
    }

    public Map<String, DynamicCookingRecipe<?>> getCookingRecipes() {
        return cookingRecipes;
    }

    public Map<String, DynamicCauldronRecipe> getCauldronRecipes() {
        return cauldronRecipes;
    }

    /**
     * Persists an implementation of AbstractCraftingRecipe to the recipes config, asynchronously.
     * Use this if saving a recipe during server runtime
     * @param recipe the recipe to persist asynchronously
     */
    public void saveRecipeAsync(AbstractCustomCraftingRecipe recipe, String name){
        String path = "";
        if (recipe instanceof ItemCraftingRecipe) path = "recipes/crafting_recipes.yml";
        else if (recipe instanceof ItemClassImprovementRecipe) path = "recipes/class_improvement_recipes.yml";
        else if (recipe instanceof ItemImprovementRecipe) path = "recipes/improvement_recipes.yml";
        final String path1 = path;
        new BukkitRunnable(){
            @Override
            public void run() {
                saveRecipe(recipe, name);
                ConfigManager.getInstance().saveConfig(path1);
            }
        }.runTaskAsynchronously(ValhallaMMO.getPlugin());
    }

    /**
     * Persists all AbstractCraftingRecipes registered
     * @param async whether to do it asynchronously or not
     */
    public void saveRecipes(boolean async){
        YamlConfiguration brewingConfig = ConfigManager.getInstance().getConfig("recipes/brewing_recipes.yml").get();
        YamlConfiguration shapedConfig = ConfigManager.getInstance().getConfig("recipes/shaped_recipes.yml").get();
        YamlConfiguration smithingConfig = ConfigManager.getInstance().getConfig("recipes/smithing_table_recipes.yml").get();
        YamlConfiguration cookingConfig = ConfigManager.getInstance().getConfig("recipes/cooking_recipes.yml").get();
        YamlConfiguration cauldronConfig = ConfigManager.getInstance().getConfig("recipes/cauldron_recipes.yml").get();
        if (async){
            for (AbstractCustomCraftingRecipe recipe : allCustomRecipes.values()){
                if (recipe instanceof ItemCraftingRecipe) saveRecipeAsync(recipe, "recipes/crafting_recipes.yml");
                else if (recipe instanceof ItemClassImprovementRecipe) saveRecipeAsync(recipe, "recipes/class_improvement_recipes.yml");
                else if (recipe instanceof ItemImprovementRecipe) saveRecipeAsync(recipe, "recipes/improvement_recipes.yml");
            }
            ValhallaMMO.getPlugin().getLogger().info("ASYNC : Finished saving custom crafting recipes ");
            for (DynamicCraftingTableRecipe recipe : shapedRecipes.values()){
                saveRecipeAsync(recipe, shapedConfig);
            }
            ValhallaMMO.getPlugin().getLogger().info("ASYNC : Finished saving custom shaped recipes");
            for (DynamicSmithingTableRecipe recipe : smithingRecipes.values()){
                saveRecipeAsync(recipe, smithingConfig);
            }
            ValhallaMMO.getPlugin().getLogger().info("ASYNC : Finished saving custom smithing table recipes");
            for (DynamicCookingRecipe<?> recipe : cookingRecipes.values()){
                saveRecipeAsync(recipe, cookingConfig);
            }
            ValhallaMMO.getPlugin().getLogger().info("ASYNC : Finished saving custom cooking recipes");
            for (DynamicBrewingRecipe recipe : brewingRecipes.values()){
                saveRecipeAsync(recipe, brewingConfig);
            }
            ValhallaMMO.getPlugin().getLogger().info("ASYNC : Finished saving custom brewing recipes");
            for (DynamicCauldronRecipe recipe : cauldronRecipes.values()){
                saveRecipeAsync(recipe, cauldronConfig);
            }
            ValhallaMMO.getPlugin().getLogger().info("ASYNC : Finished saving custom cauldron recipes");
        } else {
            for (AbstractCustomCraftingRecipe recipe : allCustomRecipes.values()){
                if (recipe instanceof ItemCraftingRecipe) saveRecipe(recipe, "recipes/crafting_recipes.yml");
                else if (recipe instanceof ItemClassImprovementRecipe) saveRecipe(recipe, "recipes/class_improvement_recipes.yml");
                else if (recipe instanceof ItemImprovementRecipe) saveRecipe(recipe, "recipes/improvement_recipes.yml");
            }
            ValhallaMMO.getPlugin().getLogger().info("Finished saving custom crafting recipes");
            for (DynamicCraftingTableRecipe recipe : shapedRecipes.values()){
                saveRecipe(recipe, shapedConfig);
            }
            ValhallaMMO.getPlugin().getLogger().info("Finished saving custom shaped recipes");
            for (DynamicSmithingTableRecipe recipe : smithingRecipes.values()){
                saveRecipe(recipe, smithingConfig);
            }
            ValhallaMMO.getPlugin().getLogger().info("Finished saving custom shaped recipes");
            for (DynamicCookingRecipe<?> recipe : cookingRecipes.values()){
                saveRecipe(recipe, cookingConfig);
            }
            ValhallaMMO.getPlugin().getLogger().info("Finished saving custom cooking recipes");
            for (DynamicBrewingRecipe recipe : brewingRecipes.values()){
                saveRecipe(recipe, brewingConfig);
            }
            ValhallaMMO.getPlugin().getLogger().info("Finished saving custom brewing recipes");
            for (DynamicCauldronRecipe recipe : cauldronRecipes.values()){
                saveRecipe(recipe, cauldronConfig);
            }
            ValhallaMMO.getPlugin().getLogger().info("Finished saving custom cauldron recipes");
        }
    }

    /**
     * Persists an implementation of AbstractCraftingRecipe to the recipes config
     * @param recipe recipe to persist
     */
    public void saveRecipe(AbstractCustomCraftingRecipe recipe, String name){
        YamlConfiguration config = ConfigManager.getInstance().getConfig(name).get();
        String root = null;
        if (recipe instanceof ItemCraftingRecipe) {
            root = "craft.";
            config.set(root + recipe.getName() + ".result", ((ItemCraftingRecipe) recipe).getResult());
            config.set(root + recipe.getName() + ".tool_id", ((ItemCraftingRecipe) recipe).getRequiredToolId());
            config.set(root + recipe.getName() + ".tool_requirement_type", ((ItemCraftingRecipe) recipe).getToolRequirementType());
        } else if (recipe instanceof ItemImprovementRecipe) {
            root = "improve.";
            config.set(root + recipe.getName() + ".required_type", ((ItemImprovementRecipe) recipe).getRequiredItemType().toString());
        } else if (recipe instanceof ItemClassImprovementRecipe){
            root = "class_improve.";
            config.set(root + recipe.getName() + ".required_class", ((ItemClassImprovementRecipe) recipe).getRequiredEquipmentClass().toString());
        }
        if (root != null){
            config.set(root + recipe.getName() + ".unlocked_for_everyone", recipe.isUnlockedForEveryone());
            config.set(root + recipe.getName() + ".craft_block", recipe.getCraftingBlock().toString());
            config.set(root + recipe.getName() + ".craft_time", recipe.getCraftingTime());
            config.set(root + recipe.getName() + ".consecutive_crafts", recipe.getConsecutiveCrafts());
            config.set(root + recipe.getName() + ".exact_meta", recipe.requireExactMeta());
            config.set(root + recipe.getName() + ".break_station", recipe.breakStation());
            config.set(root + recipe.getName() + ".display_name", recipe.getDisplayName());
            if (recipe.getValidation() != null){
                config.set(root + recipe.getName() + ".validation", recipe.getValidation().getName());
            }
            for (DynamicItemModifier modifier : recipe.getItemModifiers()){
                if (modifier instanceof TripleArgDynamicItemModifier){
                    config.set(root + recipe.getName() + ".modifiers." + modifier.getName() + ".strength3", Utils.round(((TripleArgDynamicItemModifier) modifier).getStrength3(), 6));
                }
                if (modifier instanceof DuoArgDynamicItemModifier){
                    config.set(root + recipe.getName() + ".modifiers." + modifier.getName() + ".strength2", Utils.round(((DuoArgDynamicItemModifier) modifier).getStrength2(), 6));
                }
                config.set(root + recipe.getName() + ".modifiers." + modifier.getName() + ".strength", Utils.round(modifier.getStrength(), 6));
                config.set(root + recipe.getName() + ".modifiers." + modifier.getName() + ".priority", modifier.getPriority().toString());
            }
            int stepper = 0;
            for (ItemStack ingredient : recipe.getIngredients()){
                config.set(root + recipe.getName() + ".ingredients." + stepper, ingredient);
                stepper++;
            }
        }
        ConfigManager.getInstance().saveConfig(name);
    }

    /**
     * Persists a DynamicCauldronRecipe to the recipes config
     * @param recipe recipe to persist
     */
    public void saveRecipe(DynamicCauldronRecipe recipe, YamlConfiguration config){
        config.set("cauldron." + recipe.getName() + ".exact_meta_ingredients", recipe.isIngredientsExactMeta());
        config.set("cauldron." + recipe.getName() + ".catalyst", recipe.getCatalyst());
        config.set("cauldron." + recipe.getName() + ".exact_meta_catalyst", recipe.isCatalystExactMeta());
        config.set("cauldron." + recipe.getName() + ".require_custom_catalyst", recipe.isRequireCustomCatalyst());
        config.set("cauldron." + recipe.getName() + ".result", recipe.getResult());
        config.set("cauldron." + recipe.getName() + ".tinker_catalyst", recipe.isTinkerCatalyst());
        config.set("cauldron." + recipe.getName() + ".requires_boiling_water", recipe.isRequiresBoilingWater());
        config.set("cauldron." + recipe.getName() + ".consumes_water_level", recipe.isConsumesWaterLevel());
        config.set("cauldron." + recipe.getName() + ".unlocked_for_everyone", recipe.isUnlockedForEveryone());

        for (DynamicItemModifier modifier : recipe.getItemModifiers()){
            if (modifier instanceof TripleArgDynamicItemModifier){
                config.set("cauldron." + recipe.getName() + ".modifiers." + modifier.getName() + ".strength3", Utils.round(((TripleArgDynamicItemModifier) modifier).getStrength3(), 6));
            }
            if (modifier instanceof DuoArgDynamicItemModifier){
                config.set("cauldron." + recipe.getName() + ".modifiers." + modifier.getName() + ".strength2", Utils.round(((DuoArgDynamicItemModifier) modifier).getStrength2(), 6));
            }
            config.set("cauldron." + recipe.getName() + ".modifiers." + modifier.getName() + ".strength", Utils.round(modifier.getStrength(), 6));
            config.set("cauldron." + recipe.getName() + ".modifiers." + modifier.getName() + ".priority", modifier.getPriority().toString());
        }
        int stepper = 0;
        for (ItemStack ingredient : recipe.getIngredients()){
            config.set("cauldron." + recipe.getName() + ".ingredients." + stepper, ingredient);
            stepper++;
        }
        ConfigManager.getInstance().saveConfig("recipes/cauldron_recipes.yml");
    }

    /**
     * Loads all recipe types from the recipes directory native configs asynchronously
     */
    public void loadRecipesAsync(){
        loadDynamicShapedRecipes();
        ValhallaMMO.getPlugin().getLogger().info("Successfully loaded custom shaped recipes");
        loadDynamicSmithingTableRecipes();
        ValhallaMMO.getPlugin().getLogger().info("Successfully loaded custom smithing table recipes");
        loadDynamicCookingRecipes();
        ValhallaMMO.getPlugin().getLogger().info("Successfully loaded custom cooking recipes");
        new BukkitRunnable(){
            @Override
            public void run() {
                loadDynamicBrewingRecipes();
                ValhallaMMO.getPlugin().getLogger().info("Successfully loaded custom brewing recipes");
                loadItemCraftingRecipes();
                ValhallaMMO.getPlugin().getLogger().info("Successfully loaded custom crafting recipes");
                loadItemImprovementRecipes();
                ValhallaMMO.getPlugin().getLogger().info("Successfully loaded custom item improvement recipes");
                loadItemClassImprovementRecipes();
                ValhallaMMO.getPlugin().getLogger().info("Successfully loaded custom item class improvement recipes");
                loadCauldronRecipes();
                ValhallaMMO.getPlugin().getLogger().info("Successfully loaded custom cauldron recipes");
            }
        }.runTaskAsynchronously(ValhallaMMO.getPlugin());
    }

    public DynamicCraftingTableRecipe getDynamicShapedRecipe(String name){
        return shapedRecipes.get(name);
    }
    public DynamicSmithingTableRecipe getDynamicSmithingRecipe(String name){
        return smithingRecipes.get(name);
    }

    public DynamicBrewingRecipe getBrewingRecipe(String name) {
        return brewingRecipes.get(name);
    }

    public DynamicCraftingTableRecipe getDynamicShapedRecipe(NamespacedKey key){
        return shapedRecipesByKey.get(key);
    }
    public DynamicSmithingTableRecipe getDynamicSmithingRecipe(NamespacedKey key){
        return smithingRecipesByKey.get(key);
    }

    private void loadItemImprovementRecipes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes/improvement_recipes.yml").get();
        if (!Utils.doesPathExist(config, "", "improve")) {
            config = ConfigManager.getInstance().getConfig("recipes/improvement_recipes.yml").get();
        }
        for (ItemImprovementRecipe recipe : getImprovementRecipesFromConfig(config)){
            register(recipe);
        }
    }

    private void loadCauldronRecipes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes/cauldron_recipes.yml").get();
        if (!Utils.doesPathExist(config, "", "cauldron")) {
            config = ConfigManager.getInstance().getConfig("recipes/cauldron_recipes.yml").get();
        }
        for (DynamicCauldronRecipe recipe : getCauldronRecipesFromConfig(config)){
            register(recipe);
        }
    }

    public Collection<ItemImprovementRecipe> getImprovementRecipesFromConfig(YamlConfiguration config){
        Collection<ItemImprovementRecipe> recipes = new HashSet<>();
        ConfigurationSection section = config.getConfigurationSection("improve");
        if (section != null){
            for (String recipe : section.getKeys(false)){
                List<DynamicItemModifier> modifiers = new ArrayList<>();
                ConfigurationSection modifierSection = config.getConfigurationSection("improve." + recipe + ".modifiers");
                if (modifierSection != null){
                    for (String mod : modifierSection.getKeys(false)){
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("improve." + recipe + ".modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        double strength = config.getDouble("improve." + recipe + ".modifiers." + mod + ".strength");
                        DynamicItemModifier modifier;
                        if (Utils.doesPathExist(config, "improve." + recipe + ".modifiers." + mod, "strength2")){
                            // assuming at least DuoArgModifier
                            double strength2 = config.getDouble("improve." + recipe + ".modifiers." + mod + ".strength2");
                            if (Utils.doesPathExist(config, "improve." + recipe + ".modifiers." + mod, "strength3")){
                                // assuming TripleArgModifier
                                double strength3 = config.getDouble("improve." + recipe + ".modifiers." + mod + ".strength3");
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, strength3, priority);
                            } else {
                                // assuming DoubleArgModifier
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, priority);
                            }
                        } else {
                            modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
                        }
                        if (modifier != null){
                            modifiers.add(modifier);
                        }
                    }
                }
                String displayName = TranslationManager.getInstance().translatePlaceholders(config.getString("improve." + recipe + ".display_name"));
                Material requiredItem;
                String requiredItemString = config.getString("improve." + recipe + ".required_type");
                try {
                    requiredItem = Material.valueOf(requiredItemString);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("Invalid crafting material for " + recipe + ".required_type : " + requiredItemString + ", cancelled crafting recipe");
                    continue;
                }
                Material craftBlock;
                String craftBlockString = config.getString("improve." + recipe + ".craft_block");
                try {
                    craftBlock = Material.valueOf(craftBlockString);
                    if (!craftBlock.isBlock()) throw new IllegalArgumentException();
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("Invalid crafting material for " + recipe + ".craft_block : " + craftBlockString + ", cancelled crafting recipe");
                    continue;
                }

                List<ItemStack> ingredients = new ArrayList<>();
                ConfigurationSection ingredientSection = config.getConfigurationSection("improve." + recipe + ".ingredients");
                if (ingredientSection != null){
                    for (String ingredientIndex : ingredientSection.getKeys(false)){
                        ItemStack ingredient = config.getItemStack("improve." + recipe + ".ingredients." + ingredientIndex);
                        if (ingredient != null){
                            ingredient = TranslationManager.getInstance().translateItemStack(ingredient);
                            ingredients.add(ingredient);
                        }
                    }
                }

                int time = config.getInt("improve." + recipe + ".craft_time");
                boolean breakStation = config.getBoolean("improve." + recipe + ".break_station");
                boolean exactMeta = config.getBoolean("improve." + recipe + ".exact_meta", true);
                boolean unlockedForEveryone = config.getBoolean("improve." + recipe + ".unlocked_for_everyone");
                int consecutiveCrafts = config.getInt("improve." + recipe + ".consecutive_crafts", 8);

                ItemImprovementRecipe newRecipe = new ItemImprovementRecipe(recipe, displayName, requiredItem, craftBlock, ingredients, time, breakStation, modifiers, exactMeta, consecutiveCrafts);
                CraftValidation validation = BlockCraftStateValidationManager.getInstance().getValidation(craftBlock, config.getString("improve." + recipe + ".validation"));
                if (validation != null){
                    newRecipe.setValidation(validation);
                }
                newRecipe.setUnlockedForEveryone(unlockedForEveryone);
                recipes.add(newRecipe);
            }
        }
        return recipes;
    }

    public Collection<DynamicCauldronRecipe> getCauldronRecipesFromConfig(YamlConfiguration config){
        Collection<DynamicCauldronRecipe> recipes = new HashSet<>();
        ConfigurationSection section = config.getConfigurationSection("cauldron");
        if (section != null){
            for (String recipe : section.getKeys(false)){
                List<DynamicItemModifier> modifiers = new ArrayList<>();
                ConfigurationSection modifierSection = config.getConfigurationSection("cauldron." + recipe + ".modifiers");
                if (modifierSection != null){
                    for (String mod : modifierSection.getKeys(false)){
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("cauldron." + recipe + ".modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        double strength = config.getDouble("cauldron." + recipe + ".modifiers." + mod + ".strength");
                        DynamicItemModifier modifier;
                        if (Utils.doesPathExist(config, "cauldron." + recipe + ".modifiers." + mod, "strength2")){
                            // assuming at least DuoArgModifier
                            double strength2 = config.getDouble("cauldron." + recipe + ".modifiers." + mod + ".strength2");
                            if (Utils.doesPathExist(config, "cauldron." + recipe + ".modifiers." + mod, "strength3")){
                                // assuming TripleArgModifier
                                double strength3 = config.getDouble("cauldron." + recipe + ".modifiers." + mod + ".strength3");
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, strength3, priority);
                            } else {
                                // assuming DoubleArgModifier
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, priority);
                            }
                        } else {
                            modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
                        }
                        if (modifier != null){
                            modifiers.add(modifier);
                        }
                    }
                }
                List<ItemStack> ingredients = new ArrayList<>();
                ConfigurationSection ingredientSection = config.getConfigurationSection("cauldron." + recipe + ".ingredients");
                if (ingredientSection != null){
                    for (String ingredientIndex : ingredientSection.getKeys(false)){
                        ItemStack ingredient = config.getItemStack("cauldron." + recipe + ".ingredients." + ingredientIndex);
                        if (ingredient != null){
                            ingredient = TranslationManager.getInstance().translateItemStack(ingredient);
                            ingredients.add(ingredient);
                        }
                    }
                }
                boolean ingredientsExactMeta = config.getBoolean("cauldron." + recipe + ".exact_meta_ingredients", true);
                ItemStack catalyst = config.getItemStack("cauldron." + recipe + ".catalyst");
                boolean catalystExactMeta = config.getBoolean("cauldron." + recipe + ".exact_meta_catalyst", true);
                boolean requireCustomCatalyst = config.getBoolean("cauldron." + recipe + ".require_custom_catalyst", true);

                ItemStack result = config.getItemStack("cauldron." + recipe + ".result");

                boolean tinkerCatalyst = config.getBoolean("cauldron." + recipe + ".tinker_catalyst");
                boolean requiresBoilingWater = config.getBoolean("cauldron." + recipe + ".requires_boiling_water");
                boolean consumesWaterLevel = config.getBoolean("cauldron." + recipe + ".consumes_water_level");
                boolean unlockedForEveryone = config.getBoolean("cauldron." + recipe + ".unlocked_for_everyone");

                DynamicCauldronRecipe newRecipe = new DynamicCauldronRecipe(recipe, ingredients, catalyst, result, ingredientsExactMeta, catalystExactMeta, requireCustomCatalyst, tinkerCatalyst, requiresBoilingWater, consumesWaterLevel, modifiers);
                newRecipe.setUnlockedForEveryone(unlockedForEveryone);
                recipes.add(newRecipe);
            }
        }
        return recipes;
    }

    private void loadItemClassImprovementRecipes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes.yml").get();
        if (!Utils.doesPathExist(config, "", "class_improve")) {
            config = ConfigManager.getInstance().getConfig("recipes/class_improvement_recipes.yml").get();
        }
        for (ItemClassImprovementRecipe recipe : getItemClassImprovementRecipesFromConfig(config)){
            register(recipe);
        }
    }

    public Collection<ItemClassImprovementRecipe> getItemClassImprovementRecipesFromConfig(YamlConfiguration config){
        Collection<ItemClassImprovementRecipe> recipes = new HashSet<>();
        ConfigurationSection section = config.getConfigurationSection("class_improve");
        if (section != null){
            for (String recipe : section.getKeys(false)){
                List<DynamicItemModifier> modifiers = new ArrayList<>();
                ConfigurationSection modifierSection = config.getConfigurationSection("class_improve." + recipe + ".modifiers");
                if (modifierSection != null){
                    for (String mod : modifierSection.getKeys(false)){
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("class_improve." + recipe + ".modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        double strength = config.getDouble("class_improve." + recipe + ".modifiers." + mod + ".strength");
                        DynamicItemModifier modifier;
                        if (Utils.doesPathExist(config, "class_improve." + recipe + ".modifiers." + mod, "strength2")){
                            // assuming at least DuoArgModifier
                            double strength2 = config.getDouble("class_improve." + recipe + ".modifiers." + mod + ".strength2");
                            if (Utils.doesPathExist(config, "class_improve." + recipe + ".modifiers." + mod, "strength3")){
                                // assuming TripleArgModifier
                                double strength3 = config.getDouble("class_improve." + recipe + ".modifiers." + mod + ".strength3");
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, strength3, priority);
                            } else {
                                // assuming DoubleArgModifier
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, priority);
                            }
                        } else {
                            modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
                        }
                        if (modifier != null){
                            modifiers.add(modifier);
                        }
                    }
                }
                String displayName = TranslationManager.getInstance().translatePlaceholders(config.getString("class_improve." + recipe + ".display_name"));
                EquipmentClass requiredClass;
                String requiredItemString = config.getString("class_improve." + recipe + ".required_class");
                try {
                    requiredClass = EquipmentClass.valueOf(requiredItemString);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("Invalid equipment class for " + recipe + ".required_class : " + requiredItemString + ", cancelled crafting recipe");
                    continue;
                }
                Material craftBlock;
                String craftBlockString = config.getString("class_improve." + recipe + ".craft_block");
                try {
                    craftBlock = Material.valueOf(craftBlockString);
                    if (!craftBlock.isBlock()) throw new IllegalArgumentException();
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("Invalid crafting material for " + recipe + ".craft_block : " + craftBlockString + ", cancelled crafting recipe");
                    continue;
                }

                List<ItemStack> ingredients = new ArrayList<>();
                ConfigurationSection ingredientSection = config.getConfigurationSection("class_improve." + recipe + ".ingredients");
                if (ingredientSection != null){
                    for (String ingredientIndex : ingredientSection.getKeys(false)){
                        ItemStack ingredient = config.getItemStack("class_improve." + recipe + ".ingredients." + ingredientIndex);
                        if (ingredient != null){
                            ingredient = TranslationManager.getInstance().translateItemStack(ingredient);
                            ingredients.add(ingredient);
                        }
                    }
                }

                int time = config.getInt("class_improve." + recipe + ".craft_time");
                boolean breakStation = config.getBoolean("class_improve." + recipe + ".break_station");
                boolean unlockedForEveryone = config.getBoolean("class_improve." + recipe + ".unlocked_for_everyone");
                boolean exactMeta = config.getBoolean("class_improve." + recipe + ".exact_meta", true);
                int consecutiveCrafts = config.getInt("class_improve." + recipe + ".consecutive_crafts", 8);

                ItemClassImprovementRecipe newRecipe = new ItemClassImprovementRecipe(recipe, displayName, requiredClass, craftBlock, ingredients, time, breakStation, modifiers, exactMeta, consecutiveCrafts);
                CraftValidation validation = BlockCraftStateValidationManager.getInstance().getValidation(craftBlock, config.getString("class_improve." + recipe + ".validation"));
                if (validation != null){
                    newRecipe.setValidation(validation);
                }
                newRecipe.setUnlockedForEveryone(unlockedForEveryone);
                recipes.add(newRecipe);
            }
        }
        return recipes;
    }

    private void loadItemCraftingRecipes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes/crafting_recipes.yml").get();
        if (!Utils.doesPathExist(config, "", "craft")) {
            config = ConfigManager.getInstance().getConfig("recipes/crafting_recipes.yml").get();
        }
        for (ItemCraftingRecipe recipe : getItemCraftingRecipesFromConfig(config)){
            register(recipe);
        }
    }

    public Collection<ItemCraftingRecipe> getItemCraftingRecipesFromConfig(YamlConfiguration config){
        Collection<ItemCraftingRecipe> recipes = new HashSet<>();
        ConfigurationSection section = config.getConfigurationSection("craft");
        if (section != null){
            for (String recipe : section.getKeys(false)){
                List<DynamicItemModifier> modifiers = new ArrayList<>();
                ConfigurationSection modifierSection = config.getConfigurationSection("craft." + recipe + ".modifiers");
                if (modifierSection != null){
                    for (String mod : modifierSection.getKeys(false)){
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("craft." + recipe + ".modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        double strength = config.getDouble("craft." + recipe + ".modifiers." + mod + ".strength");
                        DynamicItemModifier modifier;
                        if (Utils.doesPathExist(config, "craft." + recipe + ".modifiers." + mod, "strength2")){
                            // assuming at least DuoArgModifier
                            double strength2 = config.getDouble("craft." + recipe + ".modifiers." + mod + ".strength2");
                            if (Utils.doesPathExist(config, "craft." + recipe + ".modifiers." + mod, "strength3")){
                                // assuming TripleArgModifier
                                double strength3 = config.getDouble("craft." + recipe + ".modifiers." + mod + ".strength3");
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, strength3, priority);
                            } else {
                                // assuming DoubleArgModifier
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, priority);
                            }
                        } else {
                            modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
                        }
                        if (modifier != null){
                            modifiers.add(modifier);
                        }
                    }
                }
                ItemStack result = config.getItemStack("craft." + recipe + ".result");
                if (result == null) continue;
                result = TranslationManager.getInstance().translateItemStack(result);
                Material craftBlock;
                String craftBlockString = config.getString("craft." + recipe + ".craft_block");
                try {
                    craftBlock = Material.valueOf(craftBlockString);
                    if (!craftBlock.isBlock()) throw new IllegalArgumentException();
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("Invalid crafting material for " + recipe + ".craft_block : " + craftBlockString + ", cancelled crafting recipe");
                    continue;
                }
                String displayName = TranslationManager.getInstance().translatePlaceholders(config.getString("craft." + recipe + ".display_name"));
                if (displayName == null){
                    displayName = "&f" + recipe;
                }
                List<ItemStack> ingredients = new ArrayList<>();
                ConfigurationSection ingredientSection = config.getConfigurationSection("craft." + recipe + ".ingredients");
                if (ingredientSection != null){
                    for (String ingredientIndex : ingredientSection.getKeys(false)){
                        ItemStack ingredient = config.getItemStack("craft." + recipe + ".ingredients." + ingredientIndex);
                        ingredient = TranslationManager.getInstance().translateItemStack(ingredient);
                        if (ingredient != null){
                            ingredients.add(ingredient);
                        }
                    }
                }

                int time = config.getInt("craft." + recipe + ".craft_time");
                boolean breakStation = config.getBoolean("craft." + recipe + ".break_station");
                boolean exactMeta = config.getBoolean("craft." + recipe + ".exact_meta", true);
                boolean unlockedForEveryone = config.getBoolean("craft." + recipe + ".unlocked_for_everyone");
                int consecutiveCrafts = config.getInt("craft." + recipe + ".consecutive_crafts", 8);
                int tool_id = config.getInt("craft." + recipe + ".tool_id", -1);
                int tool_requirement_type = config.getInt("craft." + recipe + ".tool_requirement_type", 0);


                ItemCraftingRecipe newRecipe = new ItemCraftingRecipe(recipe, displayName,
                        result, craftBlock, ingredients, time, breakStation, modifiers, exactMeta, consecutiveCrafts);
                newRecipe.setRequiredToolId(tool_id);
                newRecipe.setToolRequirementType(tool_requirement_type);
                CraftValidation validation = BlockCraftStateValidationManager.getInstance().getValidation(craftBlock, config.getString("craft." + recipe + ".validation"));
                if (validation != null){
                    newRecipe.setValidation(validation);
                }
                newRecipe.setUnlockedForEveryone(unlockedForEveryone);
                recipes.add(newRecipe);
            }
        }
        return recipes;
    }

    public void disableRecipes(){
        List<String> disabledRecipes = ConfigManager.getInstance().getConfig("config.yml").get().getStringList("disabled");
        for (String s : disabledRecipes){
            try {
                NamespacedKey.fromString(s);
                NamespacedKey recipeKey = NamespacedKey.minecraft(s.toLowerCase());
                Recipe recipe = ValhallaMMO.getPlugin().getServer().getRecipe(recipeKey);
                if (recipe != null){
//                    this.disabledRecipes.add(recipeKey);
                    ValhallaMMO.getPlugin().getServer().removeRecipe(recipeKey);
                }
            } catch (IllegalArgumentException ignored){
                ValhallaMMO.getPlugin().getLogger().warning("Invalid recipe key '" + s + "' found, recipe contains illegal characters. Allowed characters: [a-zA-Z0-9/. -], cancelled crafting recipe removal");
            }
        }

    }

    public List<NamespacedKey> getDisabledRecipes() {
        return disabledRecipes;
    }

    /**
     * Loads all DynamicShapedRecipe from recipes/shaped_recipes.yml and registers them on the server
     * and within the plugin.
     */
    private void loadDynamicShapedRecipes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes/shaped_recipes.yml").get();
        for (DynamicCraftingTableRecipe recipe : getDynamicShapedRecipesFromConfig(config)){
            register(recipe);
        }
    }
    /**
     * Loads all DynamicSmithingTableRecipes from recipes/smithing_table_recipes.yml and registers them on the server
     * and within the plugin.
     */
    private void loadDynamicSmithingTableRecipes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes/smithing_table_recipes.yml").get();
        for (DynamicSmithingTableRecipe recipe : getDynamicSmithingTableRecipesFromConfig(config)){
            register(recipe);
        }
    }

    public Collection<DynamicCraftingTableRecipe> getDynamicShapedRecipesFromConfig(YamlConfiguration config){
        Collection<DynamicCraftingTableRecipe> recipes = new HashSet<>();
        ConfigurationSection section = config.getConfigurationSection("shaped");
        if (section != null){
            recipeLoop:
            for (String recipe : section.getKeys(false)){
                ItemStack result = config.getItemStack("shaped." + recipe + ".result");
                if (Utils.isItemEmptyOrNull(result)) continue;
                result = TranslationManager.getInstance().translateItemStack(result);

                boolean requireCustomTools = config.getBoolean("shaped." + recipe + ".require_custom_tools");
                boolean useMetaData = config.getBoolean("shaped." + recipe + ".use_meta");
                boolean allowIngredientVariations = config.getBoolean("shaped." + recipe + ".allow_material_variations");
                boolean unlockedForEveryone = config.getBoolean("shaped." + recipe + ".unlocked_for_everyone");
                boolean isShapeless = config.getBoolean("shaped." + recipe + ".shapeless");
                boolean improveMiddleItem = config.getBoolean("shaped." + recipe + ".improve_center_item");
                int requiredToolId = config.getInt("shaped." + recipe + ".tool_id", -1);
                int toolRequirementType = config.getInt("shaped." + recipe + ".tool_requirement_type", 0);

                Map<Integer, ItemStack> exactIngredients = new HashMap<>();

                List<DynamicItemModifier> modifiers = new ArrayList<>();

                ConfigurationSection modifierSection = config.getConfigurationSection("shaped." + recipe + ".modifiers");
                if (modifierSection != null){
                    for (String mod : modifierSection.getKeys(false)){
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("shaped." + recipe + ".modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        double strength = config.getDouble("shaped." + recipe + ".modifiers." + mod + ".strength");
                        DynamicItemModifier modifier;
                        if (Utils.doesPathExist(config, "shaped." + recipe + ".modifiers." + mod, "strength2")){
                            // assuming at least DuoArgModifier
                            double strength2 = config.getDouble("shaped." + recipe + ".modifiers." + mod + ".strength2");
                            if (Utils.doesPathExist(config, "shaped." + recipe + ".modifiers." + mod, "strength3")){
                                // assuming TripleArgModifier
                                double strength3 = config.getDouble("shaped." + recipe + ".modifiers." + mod + ".strength3");
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, strength3, priority);
                            } else {
                                // assuming DoubleArgModifier
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, priority);
                            }
                        } else {
                            modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
                        }
                        if (modifier != null){
                            modifiers.add(modifier);
                        }
                    }
                }

                List<String> shape = config.getStringList("shaped." + recipe + ".shape");
                if (shape.isEmpty()){
                    // new system
                    for (int i = 0; i < 9; i++){
                        if (Utils.doesPathExist(config, "shaped." + recipe + ".ingredients.", "" + i)){
                            ItemStack charItemStack = config.getItemStack("shaped." + recipe + ".ingredients." + i);
                            if (Utils.isItemEmptyOrNull(charItemStack)) continue;
                            charItemStack = TranslationManager.getInstance().translateItemStack(charItemStack);
                            exactIngredients.put(i, charItemStack);
                        }
                    }
                    if (exactIngredients.isEmpty()) {
                        ValhallaMMO.getPlugin().getLogger().warning("Shaped recipe " + recipe + " does not have ingredients, not loaded in");
                    }
                } else {
                    // old system
                    try {
                        int index = 0;
                        for (String s : shape){
                            s = s.substring(0, Math.min(3, shape.size()));
                            for (Character c : s.toCharArray()){
                                if (c.equals(' ')) {
                                    index++;
                                    continue;
                                }
                                ItemStack charItemStack = config.getItemStack("shaped." + recipe + ".ingredients." + c);
                                if (charItemStack == null) {
                                    try {
                                        String material = config.getString("shaped." + recipe + ".ingredients." + c);
                                        if (material != null){
                                            charItemStack = new ItemStack(Material.valueOf(material));
                                        } else throw new IllegalArgumentException();
                                    } catch (IllegalArgumentException ignored){
                                        ValhallaMMO.getPlugin().getLogger().warning("Invalid material ingredient " + c + " for recipe " + recipe);
                                        continue recipeLoop;
                                    }
                                }
                                charItemStack = TranslationManager.getInstance().translateItemStack(charItemStack);
                                exactIngredients.put(index, charItemStack);
                                index++;
                            }
                        }
                    } catch (NullPointerException | IndexOutOfBoundsException e){
                        ValhallaMMO.getPlugin().getLogger().warning("Invalid crafting shape for shaped recipe " + recipe + ", cancelled crafting recipe");
                        e.printStackTrace();
                        continue;
                    }
                }

                DynamicCraftingTableRecipe finalRecipe = new DynamicCraftingTableRecipe(recipe, result, exactIngredients, unlockedForEveryone, useMetaData, allowIngredientVariations, requireCustomTools, improveMiddleItem, isShapeless, modifiers);
                finalRecipe.setRequiredToolId(requiredToolId);
                finalRecipe.setToolRequirementType(toolRequirementType);
                recipes.add(finalRecipe);
            }
        }
        return recipes;
    }

    public Collection<DynamicSmithingTableRecipe> getDynamicSmithingTableRecipesFromConfig(YamlConfiguration config){
        Collection<DynamicSmithingTableRecipe> recipes = new HashSet<>();
        ConfigurationSection section = config.getConfigurationSection("smithing");
        if (section != null){
            for (String recipe : section.getKeys(false)){
                ItemStack result = config.getItemStack("smithing." + recipe + ".result");
                if (result == null) continue;
                result = TranslationManager.getInstance().translateItemStack(result);

                ItemStack base = config.getItemStack("smithing." + recipe + ".base");
                if (base == null) continue;
                base = TranslationManager.getInstance().translateItemStack(base);
                ItemStack addition = config.getItemStack("smithing." + recipe + ".addition");
                if (addition == null) continue;
                addition = TranslationManager.getInstance().translateItemStack(addition);

                boolean requireCustomTools = config.getBoolean("smithing." + recipe + ".require_custom_tools");
                boolean useMetaBase = config.getBoolean("smithing." + recipe + ".use_meta_base");
                boolean useMetaAddition = config.getBoolean("smithing." + recipe + ".use_meta_addition");
                boolean allowBaseVariations = config.getBoolean("smithing." + recipe + ".allow_base_variations");
                boolean allowAdditionVariations = config.getBoolean("smithing." + recipe + ".allow_addition_variations");
                boolean unlockedForEveryone = config.getBoolean("smithing." + recipe + ".unlocked_for_everyone");
                boolean improveBase = config.getBoolean("smithing." + recipe + ".improve_base");
                boolean consumeAddition = config.getBoolean("smithing." + recipe + ".consume_addition");

                List<DynamicItemModifier> resultModifiers = new ArrayList<>();

                ConfigurationSection baseModifierSection = config.getConfigurationSection("smithing." + recipe + ".result_modifiers");
                if (baseModifierSection != null){
                    for (String mod : baseModifierSection.getKeys(false)){
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("smithing." + recipe + ".result_modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        double strength = config.getDouble("smithing." + recipe + ".result_modifiers." + mod + ".strength");
                        DynamicItemModifier modifier;
                        if (Utils.doesPathExist(config, "smithing." + recipe + ".result_modifiers." + mod, "strength2")){
                            // assuming at least DuoArgModifier
                            double strength2 = config.getDouble("smithing." + recipe + ".result_modifiers." + mod + ".strength2");
                            if (Utils.doesPathExist(config, "smithing." + recipe + ".result_modifiers." + mod, "strength3")){
                                // assuming TripleArgModifier
                                double strength3 = config.getDouble("smithing." + recipe + ".result_modifiers." + mod + ".strength3");
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, strength3, priority);
                            } else {
                                // assuming DoubleArgModifier
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, priority);
                            }
                        } else {
                            modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
                        }
                        if (modifier != null){
                            resultModifiers.add(modifier);
                        }
                    }
                }

                List<DynamicItemModifier> additionModifiers = new ArrayList<>();

                ConfigurationSection additionModifierSection = config.getConfigurationSection("smithing." + recipe + ".addition_modifiers");
                if (additionModifierSection != null){
                    for (String mod : additionModifierSection.getKeys(false)){
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("smithing." + recipe + ".addition_modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        double strength = config.getDouble("smithing." + recipe + ".addition_modifiers." + mod + ".strength");
                        DynamicItemModifier modifier;
                        if (Utils.doesPathExist(config, "smithing." + recipe + ".addition_modifiers." + mod, "strength2")){
                            // assuming at least DuoArgModifier
                            double strength2 = config.getDouble("smithing." + recipe + ".addition_modifiers." + mod + ".strength2");
                            if (Utils.doesPathExist(config, "smithing." + recipe + ".addition_modifiers." + mod, "strength3")){
                                // assuming TripleArgModifier
                                double strength3 = config.getDouble("smithing." + recipe + ".addition_modifiers." + mod + ".strength3");
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, strength3, priority);
                            } else {
                                // assuming DoubleArgModifier
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, priority);
                            }
                        } else {
                            modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
                        }
                        if (modifier != null){
                            additionModifiers.add(modifier);
                        }
                    }
                }

                DynamicSmithingTableRecipe finalRecipe = new DynamicSmithingTableRecipe(recipe, result, base, addition, useMetaBase, useMetaAddition, requireCustomTools, improveBase, consumeAddition, allowBaseVariations,
                        allowAdditionVariations, resultModifiers, additionModifiers);
                finalRecipe.setUnlockedForEveryone(unlockedForEveryone);
                recipes.add(finalRecipe);
            }
        }
        return recipes;
    }

    private void loadDynamicCookingRecipes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes/cooking_recipes.yml").get();
        for (DynamicCookingRecipe<?> recipe : getCookingRecipesFromConfig(config)){
            register(recipe);
        }
    }

    public Collection<DynamicCookingRecipe<?>> getCookingRecipesFromConfig(YamlConfiguration config){
        Collection<DynamicCookingRecipe<?>> recipes = new HashSet<>();
        Collection<String> validCookingTypes = new HashSet<>(Arrays.asList("campfire", "furnace", "blasting", "smoking"));
        ConfigurationSection section = config.getConfigurationSection("cooking");
        if (section != null){
            for (String recipe : section.getKeys(false)){
                String type = config.getString("cooking." + recipe + ".type");
                if (type == null || !validCookingTypes.contains(type)) continue;
                int cookingTime = config.getInt("cooking." + recipe + ".time");
                boolean unlockedForEveryone = config.getBoolean("cooking." + recipe + ".unlocked_for_everyone");
                float experience = (float) config.getDouble("cooking." + recipe + ".exp");
                ItemStack exactItemRequirement = config.getItemStack("cooking." + recipe + ".required");
                if (exactItemRequirement == null) continue;
                exactItemRequirement = TranslationManager.getInstance().translateItemStack(exactItemRequirement);
                ItemStack result = config.getItemStack("cooking." + recipe + ".result");

                boolean sameResultAsInput = config.getBoolean("cooking." + recipe + ".tinker_mode");
                boolean exactMeta = config.getBoolean("cooking." + recipe + ".exact_meta");
                boolean requireCustomTool = config.getBoolean("cooking." + recipe + ".require_custom_tool");
                if (result == null) continue;
                result = TranslationManager.getInstance().translateItemStack(result);
                if (sameResultAsInput) result = exactItemRequirement.clone();

                List<DynamicItemModifier> modifiers = new ArrayList<>();

                ConfigurationSection modifierSection = config.getConfigurationSection("cooking." + recipe + ".modifiers");
                if (modifierSection != null){
                    for (String mod : modifierSection.getKeys(false)){
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("cooking." + recipe + ".modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        double strength = config.getDouble("cooking." + recipe + ".modifiers." + mod + ".strength");
                        DynamicItemModifier modifier;
                        if (Utils.doesPathExist(config, "cooking." + recipe + ".modifiers." + mod, "strength2")){
                            // assuming at least DuoArgModifier
                            double strength2 = config.getDouble("cooking." + recipe + ".modifiers." + mod + ".strength2");
                            if (Utils.doesPathExist(config, "cooking." + recipe + ".modifiers." + mod, "strength3")){
                                // assuming TripleArgModifier
                                double strength3 = config.getDouble("cooking." + recipe + ".modifiers." + mod + ".strength3");
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, strength3, priority);
                            } else {
                                // assuming DoubleArgModifier
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, priority);
                            }
                        } else {
                            modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
                        }
                        if (modifier != null){
                            modifiers.add(modifier);
                        }
                    }
                }

                switch (type){
                    case "campfire": {
                        int campfireMode = config.getInt("cooking." + recipe + ".campfire_mode");
                        DynamicCookingRecipe<CampfireRecipe> r = new DynamicCampfireRecipe(recipe, exactItemRequirement,
                                result, campfireMode, cookingTime, experience, sameResultAsInput, exactMeta, requireCustomTool, modifiers);
                        r.setUnlockedForEveryone(unlockedForEveryone);
                        recipes.add(r);
                        break;
                    }
                    case "furnace": {
                        DynamicCookingRecipe<FurnaceRecipe> r = new DynamicFurnaceRecipe(recipe, exactItemRequirement,
                                result, cookingTime, experience, sameResultAsInput, exactMeta, requireCustomTool, modifiers);
                        r.setUnlockedForEveryone(unlockedForEveryone);
                        recipes.add(r);
                        break;
                    }
                    case "blasting": {
                        DynamicCookingRecipe<BlastingRecipe> r = new DynamicBlastingRecipe(recipe, exactItemRequirement,
                                result, cookingTime, experience, sameResultAsInput, exactMeta, requireCustomTool, modifiers);
                        r.setUnlockedForEveryone(unlockedForEveryone);
                        recipes.add(r);
                        break;
                    }
                    case "smoking": {
                        DynamicCookingRecipe<SmokingRecipe> r = new DynamicSmokingRecipe(recipe, exactItemRequirement,
                                result, cookingTime, experience, sameResultAsInput, exactMeta, requireCustomTool, modifiers);
                        r.setUnlockedForEveryone(unlockedForEveryone);
                        recipes.add(r);
                        break;
                    }
                }
            }
        }
        return recipes;
    }

    private <T extends CookingRecipe<T>> void saveRecipe(DynamicCookingRecipe<T> recipe, YamlConfiguration config){
        String type;
        if (recipe instanceof DynamicFurnaceRecipe){
            type = "furnace";
        } else if (recipe instanceof DynamicCampfireRecipe){
            type = "campfire";
            config.set("cooking." + recipe.getName() + ".campfire_mode", ((DynamicCampfireRecipe) recipe).getCampfireMode());
        } else if (recipe instanceof DynamicBlastingRecipe){
            type = "blasting";
        } else if (recipe instanceof DynamicSmokingRecipe){
            type = "smoking";
        } else return;
        config.set("cooking." + recipe.getName() + ".type", type);
        config.set("cooking." + recipe.getName() + ".time", recipe.getCookTime());
        config.set("cooking." + recipe.getName() + ".exp", recipe.getExperience());
        config.set("cooking." + recipe.getName() + ".unlocked_for_everyone", recipe.isUnlockedForEveryone());
        config.set("cooking." + recipe.getName() + ".required", recipe.getInput());
        config.set("cooking." + recipe.getName() + ".result", recipe.getResult());
        if (recipe.getModifiers().size() > 0){
            for (DynamicItemModifier m : recipe.getModifiers()){
                if (m instanceof TripleArgDynamicItemModifier){
                    config.set("cooking." + recipe.getName() + ".modifiers." + m.getName() + ".strength3", Utils.round(((TripleArgDynamicItemModifier) m).getStrength3(), 6));
                }
                if (m instanceof DuoArgDynamicItemModifier){
                    config.set("cooking." + recipe.getName() + ".modifiers." + m.getName() + ".strength2", Utils.round(((DuoArgDynamicItemModifier) m).getStrength2(), 6));
                }
                config.set("cooking." + recipe.getName() + ".modifiers." + m.getName() + ".strength", Utils.round(m.getStrength(), 6));
                config.set("cooking." + recipe.getName() + ".modifiers." + m.getName() + ".priority", m.getPriority().toString());
            }
        }
        config.set("cooking." + recipe.getName() + ".tinker_mode", recipe.isTinkerInput());
        config.set("cooking." + recipe.getName() + ".exact_meta", recipe.isUseMetadata());
        config.set("cooking." + recipe.getName() + ".require_custom_tool", recipe.requiresCustomTool());
        ConfigManager.getInstance().saveConfig("recipes/cooking_recipes.yml");
    }

    /**
     * Loads all DynamicBrewingRecipe from the config recipes.yml and registers them on the server
     * and within the plugin.
     */
    private void loadDynamicBrewingRecipes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes.yml").get();
        if (!Utils.doesPathExist(config, "", "brewing")) {
            config = ConfigManager.getInstance().getConfig("recipes/brewing_recipes.yml").get();
        }
        for (DynamicBrewingRecipe recipe : getBrewingRecipesFromConfig(config)){
            register(recipe);
        }
    }

    public Collection<DynamicBrewingRecipe> getBrewingRecipesFromConfig(YamlConfiguration config){
        Collection<DynamicBrewingRecipe> recipes = new HashSet<>();
        ConfigurationSection section = config.getConfigurationSection("brewing");
        if (section != null){
            for (String recipe : section.getKeys(false)){
                String applyOnString = config.getString("brewing." + recipe + ".required_type");
                if (applyOnString == null) continue;
                Material applyOn;
                try {
                    applyOn = Material.valueOf(applyOnString);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("Invalid material required " + applyOnString + " for recipe " + recipe);
                    continue;
                }

                boolean useMetaData = config.getBoolean("brewing." + recipe + ".use_meta");

                List<DynamicItemModifier> modifiers = new ArrayList<>();

                ConfigurationSection modifierSection = config.getConfigurationSection("brewing." + recipe + ".modifiers");
                if (modifierSection != null){
                    for (String mod : modifierSection.getKeys(false)){
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("brewing." + recipe + ".modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        double strength = config.getDouble("brewing." + recipe + ".modifiers." + mod + ".strength");
                        DynamicItemModifier modifier;
                        if (Utils.doesPathExist(config, "brewing." + recipe + ".modifiers." + mod, "strength2")){
                            // assuming at least DuoArgModifier
                            double strength2 = config.getDouble("brewing." + recipe + ".modifiers." + mod + ".strength2");
                            if (Utils.doesPathExist(config, "brewing." + recipe + ".modifiers." + mod, "strength3")){
                                // assuming TripleArgModifier
                                double strength3 = config.getDouble("brewing." + recipe + ".modifiers." + mod + ".strength3");
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, strength3, priority);
                            } else {
                                // assuming DoubleArgModifier
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, priority);
                            }
                        } else {
                            modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
                        }
                        if (modifier != null){
                            modifiers.add(modifier);
                        }
                    }
                }
                boolean unlockedForEveryone = config.getBoolean("brewing." + recipe + ".unlocked_for_everyone");

                ItemStack ingredient = config.getItemStack("brewing." + recipe + ".ingredient");
                if (ingredient == null) continue;

                ingredient = TranslationManager.getInstance().translateItemStack(ingredient);
                DynamicBrewingRecipe finalRecipe = new DynamicBrewingRecipe(recipe, ingredient, applyOn, useMetaData, modifiers);
                finalRecipe.setUnlockedForEveryone(unlockedForEveryone);
                recipes.add(finalRecipe);
            }
        }
        return recipes;
    }

    private void saveRecipe(DynamicCraftingTableRecipe recipe, YamlConfiguration config){
        Map<Integer, ItemStack> ingredientMap = recipe.getExactItems();
//        int index = 0;
//        config.set("shaped." + recipe.getName() + ".shape", recipe.getShape());
//        Map<Integer, Character> charLayout = new HashMap<>();
//        for (String shape : recipe.getShape()){
//            for (char c : shape.toCharArray()){
//                charLayout.put(index, c);
//                index++;
//            }
//        }

//        if (ingredientMap.size() != charLayout.size()) {
//            ValhallaMMO.getPlugin().getLogger().warning("recipe " + recipe.getName() + " could not save properly");
//            return;
//        }
        for (Integer i : ingredientMap.keySet()){
            ItemStack ingredient = ingredientMap.get(i);
            if (ingredient == null) {
                continue;
            }
            config.set("shaped." + recipe.getName() + ".ingredients." + i, ingredient);
//            if (charLayout.containsKey(i)){
//            } else {
//                ValhallaMMO.getPlugin().getLogger().warning("recipe " + recipe.getName() + " could not save properly");
//                return;
//            }
        }
        config.set("shaped." + recipe.getName() + ".result", recipe.getResult());
        if (recipe.getItemModifiers().size() > 0){
            for (DynamicItemModifier m : recipe.getItemModifiers()){
                if (m instanceof TripleArgDynamicItemModifier){
                    config.set("shaped." + recipe.getName() + ".modifiers." + m.getName() + ".strength3", Utils.round(((TripleArgDynamicItemModifier) m).getStrength3(), 6));
                }
                if (m instanceof DuoArgDynamicItemModifier){
                    config.set("shaped." + recipe.getName() + ".modifiers." + m.getName() + ".strength2", Utils.round(((DuoArgDynamicItemModifier) m).getStrength2(), 6));
                }
                config.set("shaped." + recipe.getName() + ".modifiers." + m.getName() + ".strength", Utils.round(m.getStrength(), 6));
                config.set("shaped." + recipe.getName() + ".modifiers." + m.getName() + ".priority", m.getPriority().toString());
            }
        }
        config.set("shaped." + recipe.getName() + ".require_custom_tools", recipe.isRequireCustomTools());
        config.set("shaped." + recipe.getName() + ".use_meta", recipe.isUseMetadata());
        config.set("shaped." + recipe.getName() + ".unlocked_for_everyone", recipe.isUnlockedForEveryone());
        config.set("shaped." + recipe.getName() + ".improve_center_item", recipe.isTinkerFirstItem());
        config.set("shaped." + recipe.getName() + ".shapeless", recipe.isShapeless());
        config.set("shaped." + recipe.getName() + ".tool_id", recipe.getRequiredToolId());
        config.set("shaped." + recipe.getName() + ".tool_requirement_type", recipe.getToolRequirementType());
        config.set("shaped." + recipe.getName() + ".allow_material_variations", recipe.isAllowIngredientVariations());
        ConfigManager.getInstance().saveConfig("recipes/shaped_recipes.yml");
    }


    private void saveRecipe(DynamicSmithingTableRecipe recipe, YamlConfiguration config){
        config.set("smithing." + recipe.getName() + ".result", recipe.getResult());
        config.set("smithing." + recipe.getName() + ".base", recipe.getBase());
        config.set("smithing." + recipe.getName() + ".addition", recipe.getAddition());
        if (recipe.getModifiersResult().size() > 0){
            for (DynamicItemModifier m : recipe.getModifiersResult()){
                if (m instanceof TripleArgDynamicItemModifier){
                    config.set("smithing." + recipe.getName() + ".result_modifiers." + m.getName() + ".strength3", Utils.round(((TripleArgDynamicItemModifier) m).getStrength3(), 6));
                }
                if (m instanceof DuoArgDynamicItemModifier){
                    config.set("smithing." + recipe.getName() + ".result_modifiers." + m.getName() + ".strength2", Utils.round(((DuoArgDynamicItemModifier) m).getStrength2(), 6));
                }
                config.set("smithing." + recipe.getName() + ".result_modifiers." + m.getName() + ".strength", Utils.round(m.getStrength(), 6));
                config.set("smithing." + recipe.getName() + ".result_modifiers." + m.getName() + ".priority", m.getPriority().toString());
            }
        }
        if (recipe.getModifiersAddition().size() > 0){
            for (DynamicItemModifier m : recipe.getModifiersAddition()){
                if (m instanceof TripleArgDynamicItemModifier){
                    config.set("smithing." + recipe.getName() + ".addition_modifiers." + m.getName() + ".strength3", Utils.round(((TripleArgDynamicItemModifier) m).getStrength3(), 6));
                }
                if (m instanceof DuoArgDynamicItemModifier){
                    config.set("smithing." + recipe.getName() + ".addition_modifiers." + m.getName() + ".strength2", Utils.round(((DuoArgDynamicItemModifier) m).getStrength2(), 6));
                }
                config.set("smithing." + recipe.getName() + ".addition_modifiers." + m.getName() + ".strength", Utils.round(m.getStrength(), 6));
                config.set("smithing." + recipe.getName() + ".addition_modifiers." + m.getName() + ".priority", m.getPriority().toString());
            }
        }
        config.set("smithing." + recipe.getName() + ".require_custom_tools", recipe.requireCustomTools());
        config.set("smithing." + recipe.getName() + ".unlocked_for_everyone", recipe.isUnlockedForEveryone());
        config.set("smithing." + recipe.getName() + ".use_meta_base", recipe.isUseMetaBase());
        config.set("smithing." + recipe.getName() + ".use_meta_addition", recipe.isUseMetaAddition());
        config.set("smithing." + recipe.getName() + ".improve_base", recipe.isTinkerBase());
        config.set("smithing." + recipe.getName() + ".consume_addition", recipe.isConsumeAddition());
        config.set("smithing." + recipe.getName() + ".allow_base_variation", recipe.isAllowBaseVariations());
        config.set("smithing." + recipe.getName() + ".allow_addition_variation", recipe.isAllowAdditionVariations());
        ConfigManager.getInstance().saveConfig("recipes/smithing_table_recipes.yml");
    }

    private void saveRecipe(DynamicBrewingRecipe recipe, YamlConfiguration config){
        config.set("brewing." + recipe.getName() + ".name", recipe.getName());
        config.set("brewing." + recipe.getName() + ".required_type", recipe.getRequiredType().toString());
        if (recipe.getItemModifiers().size() > 0){
            for (DynamicItemModifier m : recipe.getItemModifiers()){
                if (m instanceof TripleArgDynamicItemModifier){
                    config.set("brewing." + recipe.getName() + ".modifiers." + m.getName() + ".strength3", Utils.round(((TripleArgDynamicItemModifier) m).getStrength3(), 6));
                }
                if (m instanceof DuoArgDynamicItemModifier){
                    config.set("brewing." + recipe.getName() + ".modifiers." + m.getName() + ".strength2", Utils.round(((DuoArgDynamicItemModifier) m).getStrength2(), 6));
                }
                config.set("brewing." + recipe.getName() + ".modifiers." + m.getName() + ".strength", Utils.round(m.getStrength(), 6));
                config.set("brewing." + recipe.getName() + ".modifiers." + m.getName() + ".priority", m.getPriority().toString());
            }
        }
        config.set("brewing." + recipe.getName() + ".ingredient", recipe.getIngredient());
        config.set("brewing." + recipe.getName() + ".unlocked_for_everyone", recipe.isUnlockedForEveryone());
        config.set("brewing." + recipe.getName() + ".use_meta", recipe.isPerfectMeta());
        ConfigManager.getInstance().saveConfig("recipes/brewing_recipes.yml");
    }

    private void saveRecipeAsync(DynamicCookingRecipe<?> recipe, YamlConfiguration config){
        new BukkitRunnable(){
            @Override
            public void run() {
                saveRecipe(recipe, config);
            }
        }.runTaskAsynchronously(ValhallaMMO.getPlugin());
    }

    private void saveRecipeAsync(DynamicCraftingTableRecipe recipe, YamlConfiguration config){
        new BukkitRunnable(){
            @Override
            public void run() {
                saveRecipe(recipe, config);
            }
        }.runTaskAsynchronously(ValhallaMMO.getPlugin());
    }

    private void saveRecipeAsync(DynamicSmithingTableRecipe recipe, YamlConfiguration config){
        new BukkitRunnable(){
            @Override
            public void run() {
                saveRecipe(recipe, config);
            }
        }.runTaskAsynchronously(ValhallaMMO.getPlugin());
    }

    private void saveRecipeAsync(DynamicBrewingRecipe recipe, YamlConfiguration config){
        new BukkitRunnable(){
            @Override
            public void run() {
                saveRecipe(recipe, config);
            }
        }.runTaskAsynchronously(ValhallaMMO.getPlugin());
    }
    private void saveRecipeAsync(DynamicCauldronRecipe recipe, YamlConfiguration config){
        new BukkitRunnable(){
            @Override
            public void run() {
                saveRecipe(recipe, config);
            }
        }.runTaskAsynchronously(ValhallaMMO.getPlugin());
    }

    public Map<Material, Map<Material, Collection<ItemImprovementRecipe>>> getItemImprovementRecipes() {
        return itemImprovementRecipes;
    }

    public Map<Material, Map<EquipmentClass, Collection<ItemClassImprovementRecipe>>> getItemClassImprovementRecipes() {
        return itemClassImprovementRecipes;
    }

    public Map<String, DynamicCraftingTableRecipe> getShapedRecipes() {
        return shapedRecipes;
    }

    public Map<String, DynamicBrewingRecipe> getBrewingRecipes() {
        return brewingRecipes;
    }

    /**
     * Fetches a brewing stand's possible recipes judging its contents. Recipes that require specific item metadata
     * are prioritized as there can technically be several recipes with the same ingredients, and since there is no
     * amazing way to distinguish individual potions without compromising on some features i'd rather have it done like
     * this.
     * @param inventory the inventory
     * @param brewer the brewer
     * @return a map with 3 entries, the key represents the output slot of the brewing stand, the value represents
     * the brewing recipe used for that slot.
     */
    public Map<Integer, DynamicBrewingRecipe> getBrewingRecipes(BrewerInventory inventory, Player brewer){
        Collection<String> unlockedRecipes = new HashSet<>();
        Profile p = ProfileManager.getManager().getProfile(brewer, "ACCOUNT");
        AccountProfile profile;
        boolean allowedAllRecipes = brewer.hasPermission("valhalla.allrecipes");
        if (p instanceof AccountProfile){
            profile = (AccountProfile) p;
            unlockedRecipes = profile.getUnlockedRecipes();
        }

        Map<Integer, DynamicBrewingRecipe> recipes = new HashMap<>();
        if (Utils.isItemEmptyOrNull(inventory.getIngredient())) {
            return recipes;
        }
        assert inventory.getIngredient() != null;

        ItemStack ingredient = inventory.getIngredient();
        if (!Utils.isItemEmptyOrNull(ingredient)) {
            ingredient = ingredient.clone();
            ingredient.setAmount(1);
        }
        Collection<DynamicBrewingRecipe> specificRecipes = specificBrewingRecipes.get(ingredient);
        if (specificRecipes != null){
            recipeLoop:
            for (DynamicBrewingRecipe r : specificRecipes){
                if (!r.isPerfectMeta()) continue; // If a recipe in specific brewing recipes is not specific in meta,
                if (!allowedAllRecipes){
                    if (!r.isUnlockedForEveryone()){
                        if (!unlockedRecipes.contains(r.getName())) continue;
                    }
                }
                // something went wrong.
                for (int i = 0; i < 3; i++){
                    if (recipes.size() == 3) break recipeLoop; // If all recipes are already determined, we're done. Can cancel!
                    if (recipes.containsKey(i)) continue; // If this recipe slot is already occupied, skip to next

                    ItemStack slotItem = inventory.getItem(i);
                    if (Utils.isItemEmptyOrNull(slotItem)) {
                        continue;
                    } // If the slot is empty, might as well continue to next slot
                    slotItem = slotItem.clone();
                    if (slotItem.getType() != r.getRequiredType()) continue; // If the slot item does not match the required type, skip to next

                    slotItem = DynamicItemModifier.modify(slotItem, brewer, r.getItemModifiers(), false, false, true);

                    //List<DynamicItemModifier> modifiers = new ArrayList<>(r.getItemModifiers());
                    //modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                    //for (DynamicItemModifier modifier : modifiers){
                    //    if (slotItem == null) continue recipeLoop;
                    //    try {
                    //        DynamicItemModifier tempMod = modifier.clone();
                    //        tempMod.setUse(false);
                    //        tempMod.setValidate(true);
                    //        slotItem = tempMod.processItem(brewer, slotItem);
                    //    } catch (CloneNotSupportedException ignored) {
                    //    }
                    //}
                    if (slotItem != null){
                        recipes.put(i, r); // If the item is not null by the end of processing, recipes is added.
                    }
                }
            }
        }

        Collection<DynamicBrewingRecipe> unSpecificRecipes = unspecificBrewingRecipes.get(ingredient.getType());
        if (unSpecificRecipes != null){ // Because specificrecipes runs before unspecificrecipes, specific item recipes
            // are prioritized
            recipeLoop:
            for (DynamicBrewingRecipe r : unSpecificRecipes){
                if (r.isPerfectMeta()) {
                    continue;
                }
                if (!allowedAllRecipes){
                    if (!r.isUnlockedForEveryone()) {
                        if (!unlockedRecipes.contains(r.getName())) continue;
                    }
                }
                for (int i = 0; i < 3; i++){
                    if (recipes.size() == 3) {
                        break recipeLoop;
                    }
                    if (recipes.containsKey(i)) {
                        continue;
                    }

                    ItemStack slotItem = inventory.getItem(i);
                    if (Utils.isItemEmptyOrNull(slotItem)) {
                        continue;
                    }
                    slotItem = slotItem.clone();
                    if (slotItem.getType() != r.getRequiredType()) {
                        continue;
                    }

                    slotItem = DynamicItemModifier.modify(slotItem, brewer, r.getItemModifiers(), false, false, true);

                    //List<DynamicItemModifier> modifiers = new ArrayList<>(r.getItemModifiers());
                    //modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                    //for (DynamicItemModifier modifier : modifiers){
                    //    if (slotItem == null) continue recipeLoop;
                    //    try {
                    //        DynamicItemModifier tempMod = modifier.clone();
                    //        tempMod.setUse(false);
                    //        tempMod.setValidate(true);
                    //        slotItem = tempMod.processItem(brewer, slotItem);
                    //    } catch (CloneNotSupportedException ignored) {
                    //    }
                    //}

                    if (slotItem != null){
                        recipes.put(i, r);
                    }
                }
            }
        }

//        recipeLoop:
//        for(DynamicBrewingRecipe recipe : brewingRecipes.values())
//        {
//
//            for (int i = 0; i < 3; i++){
//                if (recipes.size() == 3) {
//                    break recipeLoop;
//                }
//                if (recipes.containsKey(i)) {
//                    continue;
//                }
//                ItemStack slotItem = inventory.getItem(i);
//                if (Utils.isItemEmptyOrNull(slotItem)) {
//                    continue;
//                }
//                if (slotItem.getType() == recipe.getRequiredType()){
//                    if (recipe.isPerfectMeta()){
//                        if (inventory.getIngredient().isSimilar(recipe.getIngredient())){
//                            recipes.put(i, recipe);
//                        }
//                    } else {
//                        if (inventory.getIngredient().getType() == recipe.getIngredient().getType()){
//                            recipes.put(i, recipe);
//                        }
//                    }
//                }
//            }
//        }
        return recipes;
    }

    public Map<NamespacedKey, DynamicCraftingTableRecipe> getShapedRecipesByKey() {
        return shapedRecipesByKey;
    }

    public Map<NamespacedKey, DynamicSmithingTableRecipe> getSmithingRecipesByKey() {
        return smithingRecipesByKey;
    }

    public Map<String, DynamicSmithingTableRecipe> getSmithingRecipes() {
        return smithingRecipes;
    }
}
