package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.Main;
import me.athlaeos.valhallammo.configs.ConfigManager;
import me.athlaeos.valhallammo.crafting.dom.*;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.materials.CraftValidationManager;
import me.athlaeos.valhallammo.materials.blockstatevalidations.CraftValidation;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CustomRecipeManager {
    private static CustomRecipeManager manager = null;
    private Map<String, AbstractCustomCraftingRecipe> allRecipes = new TreeMap<>();
    private final Map<Material, Collection<ItemCraftingRecipe>> craftingStationRecipes = new HashMap<>();
    private final Map<Material, Map<Material, Collection<ItemImprovementRecipe>>> itemImprovementRecipes = new HashMap<>();
    private final List<NamespacedKey> disabledRecipes = new ArrayList<>();

    private final Map<String, DynamicShapedRecipe> shapedRecipes = new HashMap<>();
    private final Map<NamespacedKey, DynamicShapedRecipe> shapedRecipesBykey = new HashMap<>();

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
        shapedRecipesBykey.put(recipe.getRecipe().getKey(), recipe);
        Main.getPlugin().getServer().addRecipe(recipe.getRecipe());
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
        saveRecipe(newRecipe, ConfigManager.getInstance().getConfig("recipes.yml").get());
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
                    ConfigManager.getInstance().getConfig("recipes.yml").get().set("craft." + recipe.getName(), null);
                    ConfigManager.getInstance().getConfig("recipes.yml").save();
                }
            }
        } else if (recipe instanceof ItemImprovementRecipe){
            Map<Material, Collection<ItemImprovementRecipe>> improvementRecipes = itemImprovementRecipes.get(recipe.getCraftingBlock());
            if (improvementRecipes != null){
                Collection<ItemImprovementRecipe> recipes = improvementRecipes.get(((ItemImprovementRecipe) recipe).getRequiredItemType());
                if (recipes != null){
                    recipes.removeIf(itemImprovementRecipe -> itemImprovementRecipe.getName().equals(recipe.getName()));
                    improvementRecipes.put(recipe.getCraftingBlock(), recipes);
                    itemImprovementRecipes.put(recipe.getCraftingBlock(), improvementRecipes);
                    ConfigManager.getInstance().getConfig("recipes.yml").get().set("improve." + recipe.getName(), null);
                    ConfigManager.getInstance().getConfig("recipes.yml").save();
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
        shapedRecipesBykey.remove(recipe.getRecipe().getKey());
        ConfigManager.getInstance().getConfig("recipes.yml").get().set("shaped." + recipe.getName(), null);
        ConfigManager.getInstance().getConfig("recipes.yml").save();
        Main.getPlugin().getServer().removeRecipe(recipe.getRecipe().getKey());
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
        if (itemImprovementRecipes.containsKey(block)){
            if (itemImprovementRecipes.get(block).containsKey(item)){
                return new HashSet<>(itemImprovementRecipes.get(block).get(item));
            }
        }
        return new HashSet<>();
    }

    /**
     * @return all CustomCraftingRecipes in a map, where the key is the recipe's ID.
     */
    public Map<String, AbstractCustomCraftingRecipe> getAllRecipes() {
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
    public void saveRecipeAsync(AbstractCustomCraftingRecipe recipe, YamlConfiguration config){
        new BukkitRunnable(){
            @Override
            public void run() {
                saveRecipe(recipe, config);
                ConfigManager.getInstance().saveConfig("recipes.yml");
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    /**
     * Persists all AbstractCraftingRecipes registered
     * @param async whether to do it asynchronously or not
     */
    public void saveRecipes(boolean async){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes.yml").get();
        if (async){
            for (AbstractCustomCraftingRecipe recipe : allRecipes.values()){
                saveRecipeAsync(recipe, config);
            }
            System.out.println("[ValhallaMMO] ASYNC : Finished saving custom crafting recipes ");
            for (DynamicShapedRecipe recipe : shapedRecipes.values()){
                saveRecipeAsync(recipe, config);
            }
            System.out.println("[ValhallaMMO] ASYNC : Finished saving custom shaped recipes");
        } else {
            for (AbstractCustomCraftingRecipe recipe : allRecipes.values()){
                saveRecipe(recipe, config);
            }
            System.out.println("[ValhallaMMO] Finished saving custom crafting recipes");
            for (DynamicShapedRecipe recipe : shapedRecipes.values()){
                saveRecipe(recipe, config);
            }
            System.out.println("[ValhallaMMO] Finished saving custom shaped recipes");
        }
    }

    /**
     * Persists an implementation of AbstractCraftingRecipe to the recipes config
     * @param recipe recipe to persist
     */
    public void saveRecipe(AbstractCustomCraftingRecipe recipe, YamlConfiguration config){
        String root = null;
        if (recipe instanceof ItemCraftingRecipe) {
            root = "craft.";
            config.set(root + recipe.getName() + ".result", ((ItemCraftingRecipe) recipe).getResult());
        } else if (recipe instanceof ItemImprovementRecipe) {
            root = "improve.";
            config.set(root + recipe.getName() + ".required_type", ((ItemImprovementRecipe) recipe).getRequiredItemType().toString());
        }
        if (root != null){
            config.set(root + recipe.getName() + ".craft_block", recipe.getCraftingBlock().toString());
            config.set(root + recipe.getName() + ".craft_time", recipe.getCraftingTime());
            config.set(root + recipe.getName() + ".break_station", recipe.breakStation());
            config.set(root + recipe.getName() + ".display_name", recipe.getDisplayName());
            if (recipe.getValidation() != null){
                config.set(root + recipe.getName() + ".validation", recipe.getValidation().getName());
            }
            for (DynamicItemModifier modifier : recipe.getItemModifers()){
                config.set(root + recipe.getName() + ".modifiers." + modifier.getName() + ".strength", modifier.getStrength());
                config.set(root + recipe.getName() + ".modifiers." + modifier.getName() + ".priority", modifier.getPriority().toString());
            }
            int stepper = 0;
            for (ItemStack ingredient : recipe.getIngredients()){
                config.set(root + recipe.getName() + ".ingredients." + stepper, ingredient);
                stepper++;
            }
        }
        ConfigManager.getInstance().saveConfig("recipes.yml");
    }

    /**
     * Loads all DynamicShapedRecipes and AbstractCraftingRecipes from the recipes.yml configs asynchronously
     */
    public void loadRecipesAsync(){
        loadDynamicShapedRecipes();
        System.out.println("[ValhallaMMO] Successfully loaded custom shaped recipes");
        new BukkitRunnable(){
            @Override
            public void run() {
                loadItemCraftingRecipes();
                System.out.println("[ValhallaMMO] Successfully loaded custom crafting recipes");
                loadItemImprovementRecipes();
                System.out.println("[ValhallaMMO] Successfully loaded custom item improvement recipes");
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    private void updateDynamicShapedRecipe(DynamicShapedRecipe recipe, DynamicShapedRecipe newRecipe){
        if (recipe.getName().equals(newRecipe.getName())){
            removeDynamicShapedRecipe(recipe);
            registerDynamicShapedRecipe(newRecipe);
        }
    }

    private void removeDynamicShapedRecipe(DynamicShapedRecipe r){
        shapedRecipesBykey.remove(r.getRecipe().getKey());
        Main.getPlugin().getServer().removeRecipe(r.getRecipe().getKey());
        shapedRecipes.remove(r.getName());
    }

    public DynamicShapedRecipe getDynamicShapedRecipe(String name){
        return shapedRecipes.get(name);
    }

    public DynamicShapedRecipe getDynamicShapedRecipe(NamespacedKey key){
        return shapedRecipesBykey.get(key);
    }

    private boolean registerDynamicShapedRecipe(DynamicShapedRecipe recipe){
        if (shapedRecipes.containsKey(recipe.getName())) return false;
        if (shapedRecipesBykey.containsKey(recipe.getRecipe().getKey())) return false;
        if (Main.getPlugin().getServer().addRecipe(recipe.getRecipe())){
            shapedRecipes.put(recipe.getName(), recipe);
            shapedRecipesBykey.put(recipe.getRecipe().getKey(), recipe);
            return true;
        } else return false;
    }

    private void loadItemImprovementRecipes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes.yml").get();
        ConfigurationSection section = config.getConfigurationSection("improve");
        if (section != null){
            for (String recipe : section.getKeys(false)){
                List<DynamicItemModifier> modifiers = new ArrayList<>();
                ConfigurationSection modifierSection = config.getConfigurationSection("improve." + recipe + ".modifiers");
                if (modifierSection != null){
                    for (String mod : modifierSection.getKeys(false)){
                        double strength = config.getDouble("improve." + recipe + ".modifiers." + mod + ".strength");
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("improve." + recipe + ".modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        DynamicItemModifier modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
                        if (modifier != null){
                            modifiers.add(modifier);
                        }
                    }
                }
                String displayName = config.getString("improve." + recipe + ".display_name");
                if (displayName == null){
                    displayName = "&f" + recipe;
                }
                Material requiredItem;
                String requiredItemString = config.getString("improve." + recipe + ".required_type");
                try {
                    requiredItem = Material.valueOf(requiredItemString);
                    if (!requiredItem.isItem()) throw new IllegalArgumentException();
                } catch (IllegalArgumentException ignored){
                    System.out.println("Invalid crafting material for " + recipe + ".required_type : " + requiredItemString + ", cancelled crafting recipe");
                    return;
                }
                Material craftBlock;
                String craftBlockString = config.getString("improve." + recipe + ".craft_block");
                try {
                    craftBlock = Material.valueOf(craftBlockString);
                    if (!craftBlock.isBlock()) throw new IllegalArgumentException();
                } catch (IllegalArgumentException ignored){
                    System.out.println("Invalid crafting material for " + recipe + ".craft_block : " + craftBlockString + ", cancelled crafting recipe");
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
                ItemImprovementRecipe newRecipe = new ItemImprovementRecipe(recipe, displayName, requiredItem, craftBlock, ingredients, time, breakStation, modifiers);
                CraftValidation validation = CraftValidationManager.getInstance().getValidation(craftBlock, config.getString("improve." + recipe + ".validation"));
                if (validation != null){
                    newRecipe.setValidation(validation);
                }
                register(newRecipe);
            }
        }
    }

    private void loadItemCraftingRecipes(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("recipes.yml").get();
        ConfigurationSection section = config.getConfigurationSection("craft");
        if (section != null){
            for (String recipe : section.getKeys(false)){
                List<DynamicItemModifier> modifiers = new ArrayList<>();
                ConfigurationSection modifierSection = config.getConfigurationSection("craft." + recipe + ".modifiers");
                if (modifierSection != null){
                    for (String mod : modifierSection.getKeys(false)){
                        double strength = config.getDouble("craft." + recipe + ".modifiers." + mod + ".strength");
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("craft." + recipe + ".modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        DynamicItemModifier modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
                        if (modifier != null){
                            modifiers.add(modifier);
                        }
                    }
                }
                ItemStack result = config.getItemStack("craft." + recipe + ".result");
                Material craftBlock;
                String craftBlockString = config.getString("craft." + recipe + ".craft_block");
                try {
                    craftBlock = Material.valueOf(craftBlockString);
                    if (!craftBlock.isBlock()) throw new IllegalArgumentException();
                } catch (IllegalArgumentException ignored){
                    System.out.println("Invalid crafting material for " + recipe + ".craft_block : " + craftBlockString + ", cancelled crafting recipe");
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

                ItemCraftingRecipe newRecipe = new ItemCraftingRecipe(recipe, displayName,
                        result, craftBlock, ingredients, time, breakStation, modifiers);
                CraftValidation validation = CraftValidationManager.getInstance().getValidation(craftBlock, config.getString("improve." + recipe + ".validation"));
                if (validation != null){
                    newRecipe.setValidation(validation);
                }
                register(newRecipe);
            }
        }
    }

    public void disableRecipes(){
        List<String> disabledRecipes = ConfigManager.getInstance().getConfig("recipes.yml").get().getStringList("disabled");
        for (String s : disabledRecipes){
            NamespacedKey.fromString(s);
            NamespacedKey recipeKey = NamespacedKey.minecraft(s);
            Recipe recipe = Main.getPlugin().getServer().getRecipe(recipeKey);
            if (recipe != null){
                this.disabledRecipes.add(recipeKey);
                Main.getPlugin().getServer().removeRecipe(recipeKey);
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
        ConfigurationSection section = config.getConfigurationSection("shaped");
        if (section != null){
            recipeLoop:
            for (String recipe : section.getKeys(false)){
                ItemStack result = config.getItemStack("shaped." + recipe + ".result");
                if (result == null) continue;
                NamespacedKey recipeKey = new NamespacedKey(Main.getPlugin(), "valhalla_" + recipe);
                ShapedRecipe r = new ShapedRecipe(recipeKey, result);
                boolean requireCustomTools = config.getBoolean("shaped." + recipe + ".require_custom_tools");
                boolean useMetaData = config.getBoolean("shaped." + recipe + ".use_meta");
                boolean improveMiddleItem = config.getBoolean("shaped." + recipe + ".improve_center_item");
                Map<Integer, ItemStack> exactIngredients = new HashMap<>();

                List<DynamicItemModifier> modifiers = new ArrayList<>();

                ConfigurationSection modifierSection = config.getConfigurationSection("shaped." + recipe + ".modifiers");
                if (modifierSection != null){
                    for (String mod : modifierSection.getKeys(false)){
                        double strength = config.getDouble("shaped." + recipe + ".modifiers." + mod + ".strength");
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("shaped." + recipe + ".modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        DynamicItemModifier modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
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
                                    System.out.println("[ValhallaMMO] Invalid material ingredient " + c + " for recipe " + recipe);
                                    continue recipeLoop;
                                }
                            }
                            exactIngredients.put(index, charItemStack);
                            r.setIngredient(c, charItemStack.getType());
                            index++;
                        }
                    }
                } catch (NullPointerException | IndexOutOfBoundsException e){
                    System.out.println("Invalid crafting shape for shaped recipe " + recipe + ", cancelled crafting recipe");
                    e.printStackTrace();
                    continue;
                }
                DynamicShapedRecipe finalRecipe = new DynamicShapedRecipe(recipe, r, exactIngredients, useMetaData, requireCustomTools, improveMiddleItem, modifiers);
                shapedRecipes.put(recipe, finalRecipe);
                shapedRecipesBykey.put(recipeKey, finalRecipe);
                Main.getPlugin().getServer().addRecipe(r);
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
            System.out.println("[ValhallaMMO] recipe " + recipe.getName() + " could not save properly");
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
                System.out.println("[ValhallaMMO] recipe " + recipe.getName() + " could not save properly");
                return;
            }
        }
        config.set("shaped." + recipe.getName() + ".result", recipe.getRecipe().getResult());
        if (recipe.getModifiers().size() > 0){
            for (DynamicItemModifier m : recipe.getModifiers()){
                config.set("shaped." + recipe.getName() + ".modifiers." + m.getName() + ".strength", m.getStrength());
                config.set("shaped." + recipe.getName() + ".modifiers." + m.getName() + ".priority", m.getPriority().toString());
            }
        }
        config.set("shaped." + recipe.getName() + ".require_custom_tools", recipe.isRequireCustomTools());
        config.set("shaped." + recipe.getName() + ".use_meta", recipe.isUseMetadata());
        config.set("shaped." + recipe.getName() + ".improve_center_item", recipe.isTinkerFirstItem());
        ConfigManager.getInstance().saveConfig("recipes.yml");
    }

    private void saveRecipeAsync(DynamicShapedRecipe recipe, YamlConfiguration config){
        new BukkitRunnable(){
            @Override
            public void run() {
                saveRecipe(recipe, config);
            }
        }.runTaskAsynchronously(Main.getPlugin());
    }

    public Map<Material, Map<Material, Collection<ItemImprovementRecipe>>> getItemImprovementRecipes() {
        return itemImprovementRecipes;
    }

    public Map<String, DynamicShapedRecipe> getShapedRecipes() {
        return shapedRecipes;
    }

    public Map<NamespacedKey, DynamicShapedRecipe> getShapedRecipesBykey() {
        return shapedRecipesBykey;
    }
}
