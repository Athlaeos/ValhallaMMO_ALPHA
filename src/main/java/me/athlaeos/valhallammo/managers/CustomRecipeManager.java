package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.DynamicItemModifierManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.*;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.BlockCraftStateValidationManager;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.items.blockstatevalidations.CraftValidation;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CustomRecipeManager {
    private static CustomRecipeManager manager = null;
    private final List<NamespacedKey> disabledRecipes = new ArrayList<>();

    private final Map<String, AbstractCustomCraftingRecipe> allRecipes = new TreeMap<>();
    private final Map<Material, Collection<ItemCraftingRecipe>> craftingStationRecipes = new HashMap<>();
    // crafting station, recipes to crafting station
    private final Map<Material, Map<Material, Collection<ItemImprovementRecipe>>> itemImprovementRecipes = new HashMap<>();
    // crafting station, apply material, improvement recipes to both apply material and crafting station
    private final Map<Material, Map<EquipmentClass, Collection<ItemClassImprovementRecipe>>> itemClassImprovementRecipes = new HashMap<>();
    // crafting station, equipment class, improvement recipes to this equipment class and crafting station

    private final Map<String, DynamicBrewingRecipe> brewingRecipes = new HashMap<>();
    private final Map<String, DynamicShapedRecipe> shapedRecipes = new HashMap<>();
    private final Map<NamespacedKey, DynamicShapedRecipe> shapedRecipesByKey = new HashMap<>();

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
    public boolean register(DynamicShapedRecipe recipe){
        if (allRecipes.containsKey(recipe.getName())) {
            return false;
        }
        shapedRecipes.put(recipe.getName(), recipe);
        shapedRecipesByKey.put(recipe.getRecipe().getKey(), recipe);
        ValhallaMMO.getPlugin().getServer().addRecipe(recipe.getRecipe());
        return true;
    }

    /**
     * Registers a BrewingRecipe to be used in the vanilla brewing stand.
     * @param recipe the recipe to register
     * @return true if the recipe has been successfully registered, and false if a recipe with the given name already exists.
     */
    public boolean register(DynamicBrewingRecipe recipe){
        if (allRecipes.containsKey(recipe.getName())) {
            return false;
        }
        brewingRecipes.put(recipe.getName(), recipe);
        Collection<DynamicBrewingRecipe> existingRecipes;
        if (Utils.isItemEmptyOrNull(recipe.getIngredient())) return false;
        if (recipe.isPerfectMeta()){
            existingRecipes = specificBrewingRecipes.get(recipe.getIngredient());
            if (existingRecipes == null) existingRecipes = new HashSet<>();
            existingRecipes.add(recipe);
            specificBrewingRecipes.put(recipe.getIngredient(), existingRecipes);
        } else {
            existingRecipes = unspecificBrewingRecipes.get(recipe.getIngredient().getType());
            if (existingRecipes == null) existingRecipes = new HashSet<>();
            existingRecipes.add(recipe);
            unspecificBrewingRecipes.put(recipe.getIngredient().getType(), existingRecipes);
        }
        return true;
    }

    /**
     * Registers an AbstractCustomCraftingRecipe to be used in ValhallaMMO's custom crafting methods.
     * @param recipe the recipe to register
     * @return true if the recipe has been successfully registered, and false if a recipe with the given name already exists.
     */
    public boolean register(AbstractCustomCraftingRecipe recipe){
        if (allRecipes.containsKey(recipe.getName())) {
            return false;
        }
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
        allRecipes.put(recipe.getName(), recipe);
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
    }

    /**
     * Updates a DynamicShapedRecipe by unregistering the old recipe and registering the new. If the old recipe is null,
     * it just registers the new one.
     * @param oldRecipe the recipe to replace
     * @param newRecipe the updated recipe
     */
    public void update(DynamicShapedRecipe oldRecipe, DynamicShapedRecipe newRecipe){
        if (newRecipe == null) {
            return;
        }
        if (oldRecipe != null){
            unregister(oldRecipe);
        }
        register(newRecipe);
        saveRecipe(newRecipe, ConfigManager.getInstance().getConfig("recipes/shaped_recipes.yml").get());
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
    }

    /**
     * Unregisters a AbstractCustomCraftingRecipe, after this the recipe will no longer be craftable.
     * The recipe is also deleted from the configs.
     * @param recipe the recipe to unregister
     * @return true if the recipe was successfully removed, and false if the recipe didn't exist.
     */
    public boolean unregister(AbstractCustomCraftingRecipe recipe){
        if (recipe == null) return true;
        if (!allRecipes.containsKey(recipe.getName())) return false;
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
        return true;
    }

    /**
     * Unregisters a DynamicShapedRecipe, after this the recipe will no longer be craftable.
     * The recipe is also deleted from the configs.
     * @param recipe the recipe to unregister
     * @return true if the recipe was successfully removed, and false if the recipe didn't exist.
     */
    public boolean unregister(DynamicShapedRecipe recipe){
        if (recipe == null) return true;
        if (!shapedRecipes.containsKey(recipe.getName())) {
            return false;
        }
        shapedRecipes.remove(recipe.getName());
        shapedRecipesByKey.remove(recipe.getRecipe().getKey());
        ConfigManager.getInstance().getConfig("recipes/shaped_recipes.yml").get().set("shaped." + recipe.getName(), null);
        ConfigManager.getInstance().getConfig("recipes/shaped_recipes.yml").save();
        ValhallaMMO.getPlugin().getServer().removeRecipe(recipe.getRecipe().getKey());
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
        return true;
    }

    /**
     * @param name the name of the AbstractCustomCraftingRecipe
     * @return the SmithingRecipe with the given name, or null if it doesn't exist.
     */
    public AbstractCustomCraftingRecipe getRecipeByName(String name){
        if (allRecipes.containsKey(name)){
            return allRecipes.get(name);
        }
        return null;
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
    public Collection<AbstractCustomCraftingRecipe> getRecipesByCraftingStation(Material block, Material item){
        Collection<AbstractCustomCraftingRecipe> recipes = new HashSet<>();
        if (itemImprovementRecipes.containsKey(block)){
            if (itemImprovementRecipes.get(block).containsKey(item)){
                recipes.addAll(itemImprovementRecipes.get(block).get(item));
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
        return allRecipes;
    }

    /**
     * Get all registered crafting station CustomCraftingRecipes
     * @return a Map where the Material represents the crafting station and where the Collection represents all the
     * CustomCraftingRecipes applicable to that crafting station
     */
    public Map<Material, Collection<ItemCraftingRecipe>> getCraftingStationRecipes() {
        return craftingStationRecipes;
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
        if (async){
            for (AbstractCustomCraftingRecipe recipe : allRecipes.values()){
                if (recipe instanceof ItemCraftingRecipe) saveRecipeAsync(recipe, "recipes/crafting_recipes.yml");
                else if (recipe instanceof ItemClassImprovementRecipe) saveRecipeAsync(recipe, "recipes/class_improvement_recipes.yml");
                else if (recipe instanceof ItemImprovementRecipe) saveRecipeAsync(recipe, "recipes/improvement_recipes.yml");
            }
            ValhallaMMO.getPlugin().getLogger().info("[ValhallaMMO] ASYNC : Finished saving custom crafting recipes ");
            for (DynamicShapedRecipe recipe : shapedRecipes.values()){
                saveRecipeAsync(recipe, shapedConfig);
            }
            ValhallaMMO.getPlugin().getLogger().info("[ValhallaMMO] ASYNC : Finished saving custom shaped recipes");
            for (DynamicBrewingRecipe recipe : brewingRecipes.values()){
                saveRecipeAsync(recipe, brewingConfig);
            }
            ValhallaMMO.getPlugin().getLogger().info("[ValhallaMMO] ASYNC : Finished saving custom brewing recipes");
        } else {
            for (AbstractCustomCraftingRecipe recipe : allRecipes.values()){
                if (recipe instanceof ItemCraftingRecipe) saveRecipe(recipe, "recipes/crafting_recipes.yml");
                else if (recipe instanceof ItemClassImprovementRecipe) saveRecipe(recipe, "recipes/class_improvement_recipes.yml");
                else if (recipe instanceof ItemImprovementRecipe) saveRecipe(recipe, "recipes/improvement_recipes.yml");
            }
            ValhallaMMO.getPlugin().getLogger().info("[ValhallaMMO] Finished saving custom crafting recipes");
            for (DynamicShapedRecipe recipe : shapedRecipes.values()){
                saveRecipe(recipe, shapedConfig);
            }
            ValhallaMMO.getPlugin().getLogger().info("[ValhallaMMO] Finished saving custom shaped recipes");
            for (DynamicBrewingRecipe recipe : brewingRecipes.values()){
                saveRecipe(recipe, brewingConfig);
            }
            ValhallaMMO.getPlugin().getLogger().info("[ValhallaMMO] Finished saving custom brewing recipes");
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
            config.set(root + recipe.getName() + ".craft_block", recipe.getCraftingBlock().toString());
            config.set(root + recipe.getName() + ".craft_time", recipe.getCraftingTime());
            config.set(root + recipe.getName() + ".consecutive_crafts", recipe.getConsecutiveCrafts());
            config.set(root + recipe.getName() + ".exact_meta", recipe.requireExactMeta());
            config.set(root + recipe.getName() + ".break_station", recipe.breakStation());
            config.set(root + recipe.getName() + ".display_name", recipe.getDisplayName());
            if (recipe.getValidation() != null){
                config.set(root + recipe.getName() + ".validation", recipe.getValidation().getName());
            }
            for (DynamicItemModifier modifier : recipe.getItemModifers()){
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
     * Loads all DynamicShapedRecipes and AbstractCraftingRecipes from the recipes.yml configs asynchronously
     */
    public void loadRecipesAsync(){
        loadDynamicShapedRecipes();
        loadDynamicBrewingRecipes();
        ValhallaMMO.getPlugin().getLogger().info("[ValhallaMMO] Successfully loaded custom shaped recipes");
        new BukkitRunnable(){
            @Override
            public void run() {
                loadItemCraftingRecipes();
                ValhallaMMO.getPlugin().getLogger().info("[ValhallaMMO] Successfully loaded custom crafting recipes");
                loadItemImprovementRecipes();
                ValhallaMMO.getPlugin().getLogger().info("[ValhallaMMO] Successfully loaded custom item improvement recipes");
                loadItemClassImprovementRecipes();
                ValhallaMMO.getPlugin().getLogger().info("[ValhallaMMO] Successfully loaded custom item class improvement recipes");
            }
        }.runTaskAsynchronously(ValhallaMMO.getPlugin());
    }

    public DynamicShapedRecipe getDynamicShapedRecipe(String name){
        return shapedRecipes.get(name);
    }

    public DynamicBrewingRecipe getBrewingRecipe(String name) {
        return brewingRecipes.get(name);
    }

    public DynamicShapedRecipe getDynamicShapedRecipe(NamespacedKey key){
        return shapedRecipesByKey.get(key);
    }

    private void loadItemImprovementRecipes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes/improvement_recipes.yml").get();
        if (!Utils.doesPathExist(config, "", "improve")) {
            config = ConfigManager.getInstance().getConfig("recipes/improvement_recipes.yml").get();
        }
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
                String displayName = config.getString("improve." + recipe + ".display_name");
                Material requiredItem;
                String requiredItemString = config.getString("improve." + recipe + ".required_type");
                try {
                    requiredItem = Material.valueOf(requiredItemString);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("Invalid crafting material for " + recipe + ".required_type : " + requiredItemString + ", cancelled crafting recipe");
                    return;
                }
                Material craftBlock;
                String craftBlockString = config.getString("improve." + recipe + ".craft_block");
                try {
                    craftBlock = Material.valueOf(craftBlockString);
                    if (!craftBlock.isBlock()) throw new IllegalArgumentException();
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("Invalid crafting material for " + recipe + ".craft_block : " + craftBlockString + ", cancelled crafting recipe");
                    return;
                }

                List<ItemStack> ingredients = new ArrayList<>();
                ConfigurationSection ingredientSection = config.getConfigurationSection("improve." + recipe + ".ingredients");
                if (ingredientSection != null){
                    for (String ingredientIndex : ingredientSection.getKeys(false)){
                        ItemStack ingredient = config.getItemStack("improve." + recipe + ".ingredients." + ingredientIndex);
                        if (ingredient != null){
                            ingredients.add(ingredient);
                        }
                    }
                }

                int time = config.getInt("improve." + recipe + ".craft_time");
                boolean breakStation = config.getBoolean("improve." + recipe + ".break_station");
                boolean exactMeta = config.getBoolean("improve." + recipe + ".exact_meta", true);
                int consecutiveCrafts = config.getInt("improve." + recipe + ".consecutive_crafts", 8);

                ItemImprovementRecipe newRecipe = new ItemImprovementRecipe(recipe, displayName, requiredItem, craftBlock, ingredients, time, breakStation, modifiers, exactMeta, consecutiveCrafts);
                CraftValidation validation = BlockCraftStateValidationManager.getInstance().getValidation(craftBlock, config.getString("improve." + recipe + ".validation"));
                if (validation != null){
                    newRecipe.setValidation(validation);
                }
                register(newRecipe);
            }
        }
    }

    private void loadItemClassImprovementRecipes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes.yml").get();
        if (!Utils.doesPathExist(config, "", "class_improve")) {
            config = ConfigManager.getInstance().getConfig("recipes/class_improvement_recipes.yml").get();
        }
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
                String displayName = config.getString("class_improve." + recipe + ".display_name");
                EquipmentClass requiredClass;
                String requiredItemString = config.getString("class_improve." + recipe + ".required_class");
                try {
                    requiredClass = EquipmentClass.valueOf(requiredItemString);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("Invalid equipment class for " + recipe + ".required_class : " + requiredItemString + ", cancelled crafting recipe");
                    return;
                }
                Material craftBlock;
                String craftBlockString = config.getString("class_improve." + recipe + ".craft_block");
                try {
                    craftBlock = Material.valueOf(craftBlockString);
                    if (!craftBlock.isBlock()) throw new IllegalArgumentException();
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("Invalid crafting material for " + recipe + ".craft_block : " + craftBlockString + ", cancelled crafting recipe");
                    return;
                }

                List<ItemStack> ingredients = new ArrayList<>();
                ConfigurationSection ingredientSection = config.getConfigurationSection("class_improve." + recipe + ".ingredients");
                if (ingredientSection != null){
                    for (String ingredientIndex : ingredientSection.getKeys(false)){
                        ItemStack ingredient = config.getItemStack("class_improve." + recipe + ".ingredients." + ingredientIndex);
                        if (ingredient != null){
                            ingredients.add(ingredient);
                        }
                    }
                }

                int time = config.getInt("class_improve." + recipe + ".craft_time");
                boolean breakStation = config.getBoolean("class_improve." + recipe + ".break_station");
                boolean exactMeta = config.getBoolean("class_improve." + recipe + ".exact_meta", true);
                int consecutiveCrafts = config.getInt("class_improve." + recipe + ".consecutive_crafts", 8);

                ItemClassImprovementRecipe newRecipe = new ItemClassImprovementRecipe(recipe, displayName, requiredClass, craftBlock, ingredients, time, breakStation, modifiers, exactMeta, consecutiveCrafts);
                CraftValidation validation = BlockCraftStateValidationManager.getInstance().getValidation(craftBlock, config.getString("class_improve." + recipe + ".validation"));
                if (validation != null){
                    newRecipe.setValidation(validation);
                }
                register(newRecipe);
            }
        }
    }

    private void loadItemCraftingRecipes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes/crafting_recipes.yml").get();
        if (!Utils.doesPathExist(config, "", "craft")) {
            config = ConfigManager.getInstance().getConfig("recipes/crafting_recipes.yml").get();
        }
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
                Material craftBlock;
                String craftBlockString = config.getString("craft." + recipe + ".craft_block");
                try {
                    craftBlock = Material.valueOf(craftBlockString);
                    if (!craftBlock.isBlock()) throw new IllegalArgumentException();
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("Invalid crafting material for " + recipe + ".craft_block : " + craftBlockString + ", cancelled crafting recipe");
                    return;
                }
                String displayName = config.getString("craft." + recipe + ".display_name");
                if (displayName == null){
                    displayName = "&f" + recipe;
                }
                List<ItemStack> ingredients = new ArrayList<>();
                ConfigurationSection ingredientSection = config.getConfigurationSection("craft." + recipe + ".ingredients");
                if (ingredientSection != null){
                    for (String ingredientIndex : ingredientSection.getKeys(false)){
                        ItemStack ingredient = config.getItemStack("craft." + recipe + ".ingredients." + ingredientIndex);
                        if (ingredient != null){
                            ingredients.add(ingredient);
                        }
                    }
                }

                int time = config.getInt("craft." + recipe + ".craft_time");
                boolean breakStation = config.getBoolean("craft." + recipe + ".break_station");
                boolean exactMeta = config.getBoolean("craft." + recipe + ".exact_meta", true);
                int consecutiveCrafts = config.getInt("craft." + recipe + ".consecutive_crafts", 8);
                int tool_id = config.getInt("craft." + recipe + ".tool_id", -1);
                int tool_requirement_type = config.getInt("craft." + recipe + ".tool_requirement_type", 0);


                ItemCraftingRecipe newRecipe = new ItemCraftingRecipe(recipe, displayName,
                        result, craftBlock, ingredients, time, breakStation, modifiers, exactMeta, consecutiveCrafts);
                newRecipe.setRequiredToolId(tool_id);
                newRecipe.setToolRequirementType(tool_requirement_type);
                CraftValidation validation = BlockCraftStateValidationManager.getInstance().getValidation(craftBlock, config.getString("improve." + recipe + ".validation"));
                if (validation != null){
                    newRecipe.setValidation(validation);
                }
                register(newRecipe);
            }
        }
    }

    public void disableRecipes(){
        List<String> disabledRecipes = ConfigManager.getInstance().getConfig("config.yml").get().getStringList("disabled");
        for (String s : disabledRecipes){
            try {
                NamespacedKey.fromString(s);
                NamespacedKey recipeKey = NamespacedKey.minecraft(s.toLowerCase());
                Recipe recipe = ValhallaMMO.getPlugin().getServer().getRecipe(recipeKey);
                if (recipe != null){
                    this.disabledRecipes.add(recipeKey);
//                    if (ValhallaMMO.getPlugin().getServer().getRecipe(recipeKey) != null){
//                        ValhallaMMO.getPlugin().getServer().removeRecipe(recipeKey);
//                    }
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
     * Loads all DynamicShapedRecipe from the config recipes.yml and registers them on the server
     * and within the plugin.
     */
    private void loadDynamicShapedRecipes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes.yml").get();
        if (!Utils.doesPathExist(config, "", "shaped")) {
            config = ConfigManager.getInstance().getConfig("recipes/shaped_recipes.yml").get();
        }
        ConfigurationSection section = config.getConfigurationSection("shaped");
        if (section != null){
            recipeLoop:
            for (String recipe : section.getKeys(false)){
                ItemStack result = config.getItemStack("shaped." + recipe + ".result");
                if (result == null) continue;
                NamespacedKey recipeKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_" + recipe);
                ShapedRecipe r = new ShapedRecipe(recipeKey, result);
                boolean requireCustomTools = config.getBoolean("shaped." + recipe + ".require_custom_tools");
                boolean useMetaData = config.getBoolean("shaped." + recipe + ".use_meta");
                boolean improveMiddleItem = config.getBoolean("shaped." + recipe + ".improve_center_item");
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
                int gridSize = shape.size();
                try {
                    if (gridSize == 2) {
                        r.shape(shape.get(0).substring(0, 2), shape.get(1).substring(0, 2));
                    } else if (gridSize == 3){
                        r.shape(shape.get(0).substring(0, 3), shape.get(1).substring(0, 3), shape.get(2).substring(0, 3));
                    }
                    int index = 0;
                    for (String s : shape){
                        s = s.substring(0, gridSize);
                        for (Character c : s.toCharArray()){
                            if (c.equals(' ')) {
                                exactIngredients.put(index, null);
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
                                    ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] Invalid material ingredient " + c + " for recipe " + recipe);
                                    continue recipeLoop;
                                }
                            }
                            exactIngredients.put(index, charItemStack);
                            r.setIngredient(c, charItemStack.getType());
                            index++;
                        }
                    }
                } catch (NullPointerException | IndexOutOfBoundsException e){
                    ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] Invalid crafting shape for shaped recipe " + recipe + ", cancelled crafting recipe");
                    e.printStackTrace();
                    continue;
                }

                DynamicShapedRecipe finalRecipe = new DynamicShapedRecipe(recipe, r, exactIngredients, useMetaData, requireCustomTools, improveMiddleItem, modifiers);
                register(finalRecipe);
            }
        }
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
        ConfigurationSection section = config.getConfigurationSection("brewing");
        if (section != null){
            for (String recipe : section.getKeys(false)){
                String applyOnString = config.getString("brewing." + recipe + ".required_type");
                if (applyOnString == null) continue;
                Material applyOn;
                try {
                    applyOn = Material.valueOf(applyOnString);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] Invalid material required " + applyOnString + " for recipe " + recipe);
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

                ItemStack ingredient = config.getItemStack("brewing." + recipe + ".ingredient");
                if (ingredient == null) continue;

                DynamicBrewingRecipe finalRecipe = new DynamicBrewingRecipe(recipe, ingredient, applyOn, useMetaData, modifiers);
                register(finalRecipe);
            }
        }
    }

    private void saveRecipe(DynamicShapedRecipe recipe, YamlConfiguration config){
        config.set("shaped." + recipe.getName() + ".shape", recipe.getRecipe().getShape());
        Map<Integer, ItemStack> ingredientMap = recipe.getExactItems();
        int index = 0;
        Map<Integer, Character> charLayout = new HashMap<>();
        for (String shape : recipe.getRecipe().getShape()){
            for (char c : shape.toCharArray()){
                charLayout.put(index, c);
                index++;
            }
        }
        if (ingredientMap.size() != charLayout.size()) {
            ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] recipe " + recipe.getName() + " could not save properly");
            return;
        }
        for (Integer i : ingredientMap.keySet()){
            ItemStack ingredient = ingredientMap.get(i);
            if (ingredient == null) {
                continue;
            }
            if (charLayout.containsKey(i)){
                config.set("shaped." + recipe.getName() + ".ingredients." + charLayout.get(i), ingredient);
            } else {
                ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] recipe " + recipe.getName() + " could not save properly");
                return;
            }
        }
        config.set("shaped." + recipe.getName() + ".result", recipe.getRecipe().getResult());
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
        config.set("shaped." + recipe.getName() + ".improve_center_item", recipe.isTinkerFirstItem());
        ConfigManager.getInstance().saveConfig("recipes/shaped_recipes.yml");
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
        config.set("brewing." + recipe.getName() + ".use_meta", recipe.isPerfectMeta());
        ConfigManager.getInstance().saveConfig("recipes/brewing_recipes.yml");
    }

    private void saveRecipeAsync(DynamicShapedRecipe recipe, YamlConfiguration config){
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

    public Map<Material, Map<Material, Collection<ItemImprovementRecipe>>> getItemImprovementRecipes() {
        return itemImprovementRecipes;
    }

    public Map<Material, Map<EquipmentClass, Collection<ItemClassImprovementRecipe>>> getItemClassImprovementRecipes() {
        return itemClassImprovementRecipes;
    }

    public Map<String, DynamicShapedRecipe> getShapedRecipes() {
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
        Profile p = ProfileManager.getProfile(brewer, "ACCOUNT");
        AccountProfile profile = null;
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
        Collection<DynamicBrewingRecipe> specificRecipes = specificBrewingRecipes.get(ingredient);
        if (specificRecipes != null){
            recipeLoop:
            for (DynamicBrewingRecipe r : specificRecipes){
                if (!r.isPerfectMeta()) continue; // If a recipe in specific brewing recipes is not specific in meta,
                if (!allowedAllRecipes){
                    if (!unlockedRecipes.contains(r.getName())) continue;
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

                    List<DynamicItemModifier> modifiers = new ArrayList<>(r.getItemModifiers());
                    modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                    for (DynamicItemModifier modifier : modifiers){
                        if (slotItem == null) continue recipeLoop;
                        try {
                            DynamicItemModifier tempMod = modifier.clone();
                            tempMod.setUse(false);
                            tempMod.setValidate(true);
                            slotItem = tempMod.processItem(brewer, slotItem);
                        } catch (CloneNotSupportedException ignored) {
                        }
                    }
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
                    if (!unlockedRecipes.contains(r.getName())) {
                        continue;
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

                    List<DynamicItemModifier> modifiers = new ArrayList<>(r.getItemModifiers());
                    modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                    for (DynamicItemModifier modifier : modifiers){
                        if (slotItem == null) continue recipeLoop;
                        try {
                            DynamicItemModifier tempMod = modifier.clone();
                            tempMod.setUse(false);
                            tempMod.setValidate(true);
                            slotItem = tempMod.processItem(brewer, slotItem);
                        } catch (CloneNotSupportedException ignored) {
                        }
                    }

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

    public Map<NamespacedKey, DynamicShapedRecipe> getShapedRecipesByKey() {
        return shapedRecipesByKey;
    }
}
