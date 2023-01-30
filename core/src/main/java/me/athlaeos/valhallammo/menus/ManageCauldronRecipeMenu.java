package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.CauldronCraftingListener;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicCauldronRecipe;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class ManageCauldronRecipeMenu extends Menu implements CraftingManagerMenu{
    private View view = View.VIEW_RECIPES;
    private final NamespacedKey buttonNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "button_recipe_name");
    private DynamicCauldronRecipe currentRecipe = null;
    private DynamicCauldronRecipe oldRecipe = null;

    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;
    private int currentPage = 0;

    private final ItemStack confirmButton = Utils.createItemStack(Material.STRUCTURE_VOID,
            Utils.chat("&b&lSave"),
            null);
    private final ItemStack cancelButton = Utils.createItemStack(Material.BARRIER,
            Utils.chat("&cDelete"),
            null);

    private final ItemStack dynamicModifierButton = Utils.createItemStack(
            Material.BOOK,
            Utils.chat("&b&lDynamic Modifiers"),
            new ArrayList<>()
    );
    private final ItemStack ingredientsButton = Utils.createItemStack(
            Material.KNOWLEDGE_BOOK,
            Utils.chat("&7&lIngredients"),
            new ArrayList<>()
    );
    private final ItemStack exactIngredientsMetaButton = Utils.createItemStack(
            Material.PAPER,
            Utils.chat("&7&lRequire Exact Meta on Ingredients:"),
            new ArrayList<>()
    );
    private final ItemStack exactCatalystMetaButton = Utils.createItemStack(
            Material.PAPER,
            Utils.chat("&7&lRequire Exact Meta on Catalyst:"),
            new ArrayList<>()
    );
    private final ItemStack requireCustomCatalystButton = Utils.createItemStack(
            Material.CRAFTING_TABLE,
            Utils.chat("&7&lRequire Custom Catalyst"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, the catalyst needs" +
                    " to have ValhallaMMO custom characteristics. Such as quality, durability, or custom attributes." +
                    " If 'Require Exact Meta on Catalyst' is also enabled, it will be ignored and still does not" +
                    " need to exactly match.-n" +
                    " If false, any tool/armor piece can be used even if they have no custom traits."), 40)
    );
    private final ItemStack unlockedForEveryoneButton = Utils.createItemStack(
            Material.ARMOR_STAND,
            Utils.chat("&7&lUnlocked for everyone:"),
            new ArrayList<>()
    );
    private final ItemStack tinkerCatalystButton = Utils.createItemStack(
            Material.ANVIL,
            Utils.chat("&7&lTinker Catalyst:"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, instead of creating a new item all-together " +
                    "the catalyst is also used as output. "), 40)
    );
    private final ItemStack requiresBoilingWaterButton = Utils.createItemStack(
            Material.CAMPFIRE,
            Utils.chat("&7&lRequires Boiling Water:"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, the cauldron will require a campfire or lit (soul) fire under it for it to produce this recipe. "), 40)
    );
    private final ItemStack consumeWaterLevelButton = Utils.createItemStack(
            Material.ARMOR_STAND,
            Utils.chat("&7&lUnlocked for everyone:"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, the cauldron lower its water level by 1 when the recipe is completed."), 40)
    );
    private final ItemStack returnToMenuButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lReturn to menu"),
            new ArrayList<>()
    );

    private boolean exactIngredientsMeta = false;
    private boolean exactCatalystMeta = false;
    private boolean requireCustomCatalyst = false;
    private ItemStack result = new ItemStack(Material.CLAY);
    private ItemStack catalyst = new ItemStack(Material.DIRT);
    private boolean tinkerCatalyst = false;
    private boolean requiresBoilingWater = true;
    private boolean consumesWaterLevel = true;
    private boolean unlockedForEveryone = false;
    private Collection<ItemStack> ingredients = new ArrayList<>();
    private List<DynamicItemModifier> currentModifiers = new ArrayList<>();

    public ManageCauldronRecipeMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
    }

    public ManageCauldronRecipeMenu(PlayerMenuUtility playerMenuUtility, DynamicCauldronRecipe recipe){
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        if (recipe != null){
            currentRecipe = recipe;
            view = View.CREATE_RECIPE;
            this.catalyst = recipe.getCatalyst();
            this.result = recipe.getResult();
            this.exactIngredientsMeta = recipe.isIngredientsExactMeta();
            this.exactCatalystMeta = recipe.isCatalystExactMeta();
            this.requireCustomCatalyst = recipe.isRequireCustomCatalyst();
            this.tinkerCatalyst = recipe.isTinkerCatalyst();
            this.requiresBoilingWater = recipe.isRequiresBoilingWater();
            this.consumesWaterLevel = recipe.isConsumesWaterLevel();
            this.ingredients = recipe.getIngredients();
            this.currentModifiers = recipe.getItemModifiers();
            this.unlockedForEveryone = recipe.isUnlockedForEveryone();
        }
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7Create New Cauldron Recipe");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if (!(e.getClickedInventory() instanceof PlayerInventory)){
            e.setCancelled(true);
        }
        if (view == View.PICK_INGREDIENTS){
            e.setCancelled(false);
            if (e.getCurrentItem() != null){
                if (e.getCurrentItem().equals(confirmButton) || e.getSlot() >= 45){
                    e.setCancelled(true);
                }
            }
            new BukkitRunnable(){
                @Override
                public void run() {
                    if (view == View.PICK_INGREDIENTS){
                        ItemStack[] originalContents = Arrays.copyOfRange(inventory.getContents(), 0, 45);
                        Map<ItemStack, Integer> contents = new HashMap<>();
                        for (ItemStack i : originalContents){
                            if (i == null) continue;
                            if (i.equals(confirmButton)) continue;
                            inventory.removeItem(i);
                            int itemAmount = i.getAmount();
                            i.setAmount(1);
                            if (contents.containsKey(i)){
                                contents.put(i, contents.get(i) + itemAmount);
                            } else {
                                contents.put(i, itemAmount);
                            }
                        }
                        for (ItemStack i : contents.keySet()){
                            i.setAmount(contents.get(i));
                            inventory.addItem(i);
                        }
                    }
                }
            }.runTaskLater(ValhallaMMO.getPlugin(), 2L);
        }
        if (e.getCurrentItem() != null){
            if (e.getCurrentItem().equals(returnToMenuButton)) {
                new RecipeCategoryMenu(playerMenuUtility).open();
                return;
            } else if (e.getCurrentItem().equals(nextPageButton)){
                currentPage++;
            } else if (e.getCurrentItem().equals(previousPageButton)){
                currentPage--;
            } else if (e.getCurrentItem().equals(ingredientsButton)){
                view = View.PICK_INGREDIENTS;
                setMenuItems();
                return;
            } else if (e.getCurrentItem().equals(exactIngredientsMetaButton)) {
                exactIngredientsMeta = !exactIngredientsMeta;
                if (currentRecipe != null) currentRecipe.setIngredientsExactMeta(exactIngredientsMeta);
            } else if (e.getCurrentItem().equals(unlockedForEveryoneButton)) {
                unlockedForEveryone = !unlockedForEveryone;
                if (currentRecipe != null) currentRecipe.setUnlockedForEveryone(unlockedForEveryone);
            } else if (e.getCurrentItem().equals(consumeWaterLevelButton)) {
                consumesWaterLevel = !consumesWaterLevel;
                if (currentRecipe != null) currentRecipe.setConsumesWaterLevel(consumesWaterLevel);
            } else if (e.getCurrentItem().equals(requiresBoilingWaterButton)) {
                requiresBoilingWater = !requiresBoilingWater;
                if (currentRecipe != null) currentRecipe.setRequiresBoilingWater(requiresBoilingWater);
            } else if (e.getCurrentItem().equals(tinkerCatalystButton)) {
                tinkerCatalyst = !tinkerCatalyst;
                if (currentRecipe != null) currentRecipe.setTinkerCatalyst(tinkerCatalyst);
            } else if (e.getCurrentItem().equals(requireCustomCatalystButton)) {
                requireCustomCatalyst = !requireCustomCatalyst;
                if (currentRecipe != null) currentRecipe.setRequireCustomCatalyst(requireCustomCatalyst);
            } else if (e.getCurrentItem().equals(exactCatalystMetaButton)) {
                exactCatalystMeta = !exactCatalystMeta;
                if (currentRecipe != null) currentRecipe.setCatalystExactMeta(exactCatalystMeta);
            } else if (e.getCurrentItem().equals(dynamicModifierButton)){
                playerMenuUtility.setPreviousMenu(this);
                new DynamicModifierMenu(playerMenuUtility, currentModifiers).open();
                return;
            } else if (e.getCurrentItem().equals(confirmButton)){
                switch (view){
                    case PICK_INGREDIENTS: {
                        ItemStack[] originalContents = Arrays.copyOfRange(inventory.getContents(), 0, 45);
                        Map<ItemStack, Integer> contents = new HashMap<>();
                        for (ItemStack i : originalContents){
                            if (i == null) continue;
                            if (i.equals(confirmButton)) continue;
                            inventory.removeItem(i);
                            int itemAmount = i.getAmount();
                            i.setAmount(1);
                            if (contents.containsKey(i)){
                                contents.put(i, contents.get(i) + itemAmount);
                            } else {
                                contents.put(i, itemAmount);
                            }
                        }
                        ingredients = new HashSet<>();
                        for (ItemStack i : contents.keySet()){
                            i.setAmount(contents.get(i));
                            ingredients.add(i);
                        }
                        if (currentRecipe != null){
                            currentRecipe.setIngredients(ingredients);
                            view = View.CREATE_RECIPE;
                        }
                        setMenuItems();
                        return;
                    }
                    case CREATE_RECIPE: {
                        if (currentRecipe != null){
                            currentRecipe.setIngredients(ingredients);
                            currentRecipe.setItemModifiers(currentModifiers);
                            currentRecipe.setUnlockedForEveryone(unlockedForEveryone);
                            currentRecipe.setResult(result);
                            currentRecipe.setCatalyst(catalyst);
                            currentRecipe.setConsumesWaterLevel(consumesWaterLevel);
                            currentRecipe.setRequiresBoilingWater(requiresBoilingWater);
                            currentRecipe.setTinkerCatalyst(tinkerCatalyst);
                            currentRecipe.setRequireCustomCatalyst(requireCustomCatalyst);
                            currentRecipe.setCatalystExactMeta(exactCatalystMeta);
                            currentRecipe.setIngredientsExactMeta(exactIngredientsMeta);
                            CustomRecipeManager.getInstance().update(oldRecipe, currentRecipe);
                            currentRecipe = null;
                            view = View.VIEW_RECIPES;
                        }
                        break;
                    }
                }
            } else if (e.getCurrentItem().equals(cancelButton)){
                if (view == View.CREATE_RECIPE) {
                    if (currentRecipe != null) {
                        CustomRecipeManager.getInstance().unregister(oldRecipe);
                        view = View.VIEW_RECIPES;
                    }
                }
            }
            if (view == View.CREATE_RECIPE){
                if (!(e.getClickedInventory() instanceof PlayerInventory)){
                    e.setCancelled(true);
                    if (!Utils.isItemEmptyOrNull(e.getCursor())){
                        if (e.getSlot() == 22){
                            if (currentRecipe != null){
                                if (e.getClick() == ClickType.MIDDLE){
                                    e.getWhoClicked().getInventory().addItem(currentRecipe.getCatalyst());
                                } else {
                                    catalyst = e.getCursor().clone();
                                    catalyst.setAmount(1);
                                    currentRecipe.setCatalyst(catalyst);
                                }
                            }
                        } else if (e.getSlot() == 24){
                            if (currentRecipe != null && !tinkerCatalyst){
                                if (e.getClick() == ClickType.MIDDLE){
                                    e.getWhoClicked().getInventory().addItem(currentRecipe.getResult());
                                } else {
                                    result = e.getCursor().clone();
                                    currentRecipe.setResult(result);
                                }
                            }
                        }
                    } else {
                        if (currentRecipe != null && e.getClick() == ClickType.MIDDLE){
                            if (e.getSlot() == 22){
                                e.getWhoClicked().getInventory().addItem(currentRecipe.getCatalyst());
                            } else if (e.getSlot() == 24){
                                e.getWhoClicked().getInventory().addItem(currentRecipe.getResult());
                            }
                        }
                    }
                }
            }
            if (e.getCurrentItem() != null){
                if (e.getCurrentItem().getItemMeta() != null){
                    if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(buttonNameKey, PersistentDataType.STRING)){
                        if (view == View.VIEW_RECIPES) {
                            DynamicCauldronRecipe recipe = CustomRecipeManager.getInstance().getCauldronRecipes().get(
                                    e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING));
                            if (recipe != null){
                                if (e.getClick() == ClickType.MIDDLE){
                                    e.getWhoClicked().getInventory().addItem(recipe.getResult());
                                } else {
                                    currentRecipe = recipe.clone();
                                    oldRecipe = recipe.clone();
                                    view = View.CREATE_RECIPE;
                                }
                            }
                        }
                    }
                }
            }
        }
        if (view != View.PICK_INGREDIENTS) {
            setMenuItems();
        }
    }

    @Override
    public void handleMenu(InventoryDragEvent e) {

    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        for (int i = 0; i < getSlots(); i++){
            inventory.setItem(i, Utils.createItemStack(Material.GRAY_STAINED_GLASS_PANE, Utils.chat("&8 "), null));
        }
        switch (view){
            case VIEW_RECIPES: setCustomRecipesView();
                return;
            case CREATE_RECIPE: setCreateRecipeView();
                return;
            case PICK_INGREDIENTS: setPickIngredientsView();
        }
    }

    private void setPickIngredientsView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        for (ItemStack ingredient : ingredients.stream().limit(45L).collect(Collectors.toList())){
            inventory.addItem(ingredient);
        }
        if (ingredients.size() > CauldronCraftingListener.getMaxCauldronCapacity()){
            for (int i = 45; i < 54; i++){
                inventory.setItem(i, Utils.createItemStack(Material.RED_STAINED_GLASS_PANE, Utils.chat("&cCauldron capacity exceeded, recipe is uncraftable!"),
                        Utils.separateStringIntoLines(Utils.chat("&7In config.yml under the cauldron_max_capacity option " +
                                "you can increase this limit if the current limit is not sufficient. -nIf this limit of &c" +
                                CauldronCraftingListener.getMaxCauldronCapacity() + "&7 is exceeded players cannot fit enough " +
                                "items into the cauldron to craft this recipe"), 40)));
            }
        }
        inventory.setItem(49, confirmButton);
    }

    private void setCreateRecipeView(){
        if (currentRecipe != null){
            this.catalyst = currentRecipe.getCatalyst();
            this.result = currentRecipe.getResult();
            this.exactIngredientsMeta = currentRecipe.isIngredientsExactMeta();
            this.exactCatalystMeta = currentRecipe.isCatalystExactMeta();
            this.requireCustomCatalyst = currentRecipe.isRequireCustomCatalyst();
            this.tinkerCatalyst = currentRecipe.isTinkerCatalyst();
            this.requiresBoilingWater = currentRecipe.isRequiresBoilingWater();
            this.consumesWaterLevel = currentRecipe.isConsumesWaterLevel();
            this.ingredients = currentRecipe.getIngredients();
            this.unlockedForEveryone = currentRecipe.isUnlockedForEveryone();
            this.currentModifiers = new ArrayList<>(currentRecipe.getItemModifiers());
        }

        List<String> ingredientButtonLore = new ArrayList<>();
        for (ItemStack ingredient : ingredients){
            ingredientButtonLore.add(Utils.chat("&e"+ingredient.getAmount() + " &7x&e " + getItemName(ingredient)));
        }
        ItemMeta ingredientButtonMeta = ingredientsButton.getItemMeta();
        assert ingredientButtonMeta != null;
        ingredientButtonMeta.setLore(ingredientButtonLore);
        ingredientsButton.setItemMeta(ingredientButtonMeta);

        List<String> modifierButtonLore = new ArrayList<>();
        List<DynamicItemModifier> modifiers = new ArrayList<>(currentModifiers);
        DynamicItemModifier.sortModifiers(modifiers);
        for (DynamicItemModifier modifier : modifiers){
            modifierButtonLore.addAll(Utils.separateStringIntoLines(Utils.chat("&7- " + modifier.toString()), 40));
        }

        ItemMeta modifierButtonMeta = dynamicModifierButton.getItemMeta();
        assert modifierButtonMeta != null;
        modifierButtonMeta.setLore(modifierButtonLore);
        dynamicModifierButton.setItemMeta(modifierButtonMeta);

        ItemMeta requireExactIngredientsMetaMeta = exactIngredientsMetaButton.getItemMeta();
        assert requireExactIngredientsMetaMeta != null;
        requireExactIngredientsMetaMeta.setDisplayName(Utils.chat(String.format("&7Require Exact Meta on Ingredients: &e%s", ((exactIngredientsMeta) ? "Yes" : "No"))));
        exactIngredientsMetaButton.setItemMeta(requireExactIngredientsMetaMeta);

        ItemMeta requireExactCatalystMetaMeta = exactCatalystMetaButton.getItemMeta();
        assert requireExactCatalystMetaMeta != null;
        requireExactCatalystMetaMeta.setDisplayName(Utils.chat(String.format("&7Require Exact Meta on Catalyst: &e%s", ((exactCatalystMeta) ? "Yes" : "No"))));
        exactCatalystMetaButton.setItemMeta(requireExactCatalystMetaMeta);

        ItemMeta requireCustomCatalystMeta = requireCustomCatalystButton.getItemMeta();
        assert requireCustomCatalystMeta != null;
        requireCustomCatalystMeta.setDisplayName(Utils.chat(String.format("&7Require Custom Catalyst: &e%s", ((requireCustomCatalyst) ? "Yes" : "No"))));
        requireCustomCatalystButton.setItemMeta(requireCustomCatalystMeta);

        ItemMeta unlockedForEveryoneMeta = unlockedForEveryoneButton.getItemMeta();
        assert unlockedForEveryoneMeta != null;
        unlockedForEveryoneMeta.setDisplayName(Utils.chat(String.format("&7Unlocked for everyone: &e%s", ((unlockedForEveryone) ? "Yes" : "No"))));
        unlockedForEveryoneButton.setItemMeta(unlockedForEveryoneMeta);

        ItemMeta tinkerCatalystMeta = tinkerCatalystButton.getItemMeta();
        assert tinkerCatalystMeta != null;
        tinkerCatalystMeta.setDisplayName(Utils.chat(String.format("&7Tinker Catalyst: &e%s", ((tinkerCatalyst) ? "Yes" : "No"))));
        tinkerCatalystButton.setItemMeta(tinkerCatalystMeta);

        ItemMeta requireBoilingWaterMeta = requiresBoilingWaterButton.getItemMeta();
        assert requireBoilingWaterMeta != null;
        requireBoilingWaterMeta.setDisplayName(Utils.chat(String.format("&7Requires Boiling Water: &e%s", ((requiresBoilingWater) ? "Yes" : "No"))));
        requiresBoilingWaterButton.setItemMeta(requireBoilingWaterMeta);

        ItemMeta consumeWaterLevelMeta = consumeWaterLevelButton.getItemMeta();
        assert consumeWaterLevelMeta != null;
        consumeWaterLevelMeta.setDisplayName(Utils.chat(String.format("&7Consume Water level: &e%s", ((consumesWaterLevel) ? "Yes" : "No"))));
        consumeWaterLevelButton.setItemMeta(consumeWaterLevelMeta);

        inventory.setItem(20, ingredientsButton);
        inventory.setItem(22, catalyst);
        if (!tinkerCatalyst) inventory.setItem(24, result);
        inventory.setItem(31, consumeWaterLevelButton);
        inventory.setItem(40, requiresBoilingWaterButton);
        inventory.setItem(13, tinkerCatalystButton);
        inventory.setItem(15, dynamicModifierButton);
        inventory.setItem(2, exactIngredientsMetaButton);
        inventory.setItem(4, exactCatalystMetaButton);
        inventory.setItem(6, requireCustomCatalystButton);
        inventory.setItem(26, unlockedForEveryoneButton);
        inventory.setItem(45, cancelButton);
        inventory.setItem(53, confirmButton);
    }

    private void setCustomRecipesView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> totalRecipeButtons = new ArrayList<>();
        for (DynamicCauldronRecipe recipe : CustomRecipeManager.getInstance().getCauldronRecipes().values()){
            ItemStack resultButton = (recipe.isTinkerCatalyst() ? new ItemStack(recipe.getCatalyst()) : new ItemStack(recipe.getResult()));
            resultButton = resultButton.clone();
            ItemMeta resultMeta = resultButton.getItemMeta();
            assert resultMeta != null;
            resultMeta.setDisplayName(recipe.getName());
            List<String> resultLore = new ArrayList<>();
            for (ItemStack ingredient : recipe.getIngredients()){
                resultLore.add(Utils.chat("&e"+ingredient.getAmount() + " &7x&e " + getItemName(ingredient)));
            }
            resultLore.add(Utils.chat("&8&m                                      "));
            List<DynamicItemModifier> modifiers = new ArrayList<>(recipe.getItemModifiers());
            DynamicItemModifier.sortModifiers(modifiers);
            for (DynamicItemModifier modifier : modifiers){
                resultLore.addAll(Utils.separateStringIntoLines(Utils.chat("&7- " + modifier.toString()), 40));
            }
            if (resultMeta.getLore() != null){
                resultLore.addAll(resultMeta.getLore());
            }
            resultMeta.setLore(resultLore);
            resultMeta.getPersistentDataContainer().set(buttonNameKey, PersistentDataType.STRING, recipe.getName());
            resultButton.setItemMeta(resultMeta);
            totalRecipeButtons.add(resultButton);
        }
        totalRecipeButtons.sort(Comparator.comparing(ItemStack::getType));
        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, totalRecipeButtons);

        if (currentPage > pages.size()) currentPage = pages.size();
        if (currentPage < 1) currentPage = 1;
        if (pages.size() > 0){
            for (ItemStack i : pages.get(currentPage - 1)){
                inventory.addItem(i);
            }
        }

        inventory.setItem(45, previousPageButton);
        inventory.setItem(49, returnToMenuButton);
        inventory.setItem(53, nextPageButton);
    }

    @Override
    public void setResultModifiers(List<DynamicItemModifier> modifiers) {
        this.currentModifiers = modifiers;
        currentRecipe.setItemModifiers(modifiers);
    }

    private enum View{
        VIEW_RECIPES,
        CREATE_RECIPE,
        PICK_INGREDIENTS
    }

    private String getItemName(ItemStack i){
        String name;
        assert i.getItemMeta() != null;
        if (i.getItemMeta().hasDisplayName()){
            name = Utils.chat(i.getItemMeta().getDisplayName());
        } else if (i.getItemMeta().hasLocalizedName()){
            name = Utils.chat(i.getItemMeta().getLocalizedName());
        } else {
            name = i.getType().toString().toLowerCase().replace("_", " ");
        }
        return name;
    }
}
