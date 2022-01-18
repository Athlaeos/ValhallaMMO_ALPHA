package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.Main;
import me.athlaeos.valhallammo.crafting.CustomRecipeManager;
import me.athlaeos.valhallammo.crafting.DynamicItemModifierManager;
import me.athlaeos.valhallammo.crafting.dom.AbstractCustomCraftingRecipe;
import me.athlaeos.valhallammo.crafting.dom.ItemCraftingRecipe;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.materials.CraftValidationManager;
import me.athlaeos.valhallammo.materials.blockstatevalidations.CraftValidation;
import me.athlaeos.valhallammo.materials.blockstatevalidations.DefaultValidation;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class CreateCraftRecipe extends Menu{
    private View view = View.VIEW_RECIPES;
    private final NamespacedKey buttonNameKey = new NamespacedKey(Main.getPlugin(), "button_recipe_name");
    private ItemCraftingRecipe current_craft_recipe = null;
    private ItemCraftingRecipe old_craft_recipe = null;

    private CraftValidation currentValidation = null;
    private int currentValidationIndex = 0;

    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;
    private int currentPage = 0;

    private final ItemStack viewCustomCraftRecipesButton = Utils.createItemStack(
            Material.ANVIL,
            Utils.chat("&7&lCustom Item Crafting Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Crafting items by selecting a recipe through a menu and holding right click on a station until the item is crafted."), 40));

    private final ItemStack confirmButton = Utils.createItemStack(Material.STRUCTURE_VOID,
            Utils.chat("&b&lSave"),
            null);
    private final ItemStack cancelButton = Utils.createItemStack(Material.BARRIER,
            Utils.chat("&cDelete"),
            null);
    private final ItemStack createNewButton = Utils.createItemStack(Material.LIME_STAINED_GLASS_PANE,
            Utils.chat("&b&lNew"),
            null);
    private final ItemStack modifierStrengthButton = Utils.createItemStack(
            Material.NETHER_STAR,
            Utils.chat("&6&lStrength"),
            new ArrayList<>()
    );
    private final ItemStack modifierPriorityButton = Utils.createItemStack(
            Material.CLOCK,
            Utils.chat("&6&lPriority"),
            new ArrayList<>()
    );

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
    private final ItemStack timeButton = Utils.createItemStack(
            Material.CLOCK,
            Utils.chat("&e&lTime to craft"),
            new ArrayList<>()
    );
    private final ItemStack breakStationButton = Utils.createItemStack(
            Material.IRON_PICKAXE,
            Utils.chat("&7&lDestroy station after usage"),
            new ArrayList<>()
    );
    private final ItemStack craftStationButton = Utils.createItemStack(
            Material.CRAFTING_TABLE,
            Utils.chat("&7&lCrafting station"),
            new ArrayList<>()
    );
    private final ItemStack craftValidationButton = Utils.createItemStack(
            Material.CRAFTING_TABLE,
            Utils.chat("&7&lAlways works"),
            new ArrayList<>()
    );
    private final ItemStack improveResultButton = Utils.createItemStack(
            Material.WOODEN_SWORD,
            Utils.chat("&7&lApplicable on: "),
            new ArrayList<>()
    );
    private ItemStack craftResultButton = Utils.createItemStack(
            Material.WOODEN_SWORD,
            Utils.chat("&7&lResult"),
            new ArrayList<>()
    );
    private final ItemStack returnToMenuButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lReturn to menu"),
            new ArrayList<>()
    );

    private DynamicItemModifier currentModifier = null;
    private double modifierStrength = 0D;
    private boolean breakStation = false;
    private int craftTime = 2500;
    private Material craftStation = Material.CRAFTING_TABLE;
    private Collection<ItemStack> customRecipeIngredients = new ArrayList<>();
    private Collection<DynamicItemModifier> currentModifiers = new HashSet<>();
    private ModifierPriority priority = ModifierPriority.NEUTRAL;

    public CreateCraftRecipe(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        this.currentValidation = CraftValidationManager.getInstance().getDefaultValidation();
    }

    public CreateCraftRecipe(PlayerMenuUtility playerMenuUtility, AbstractCustomCraftingRecipe recipe){
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        if (recipe != null){
            current_craft_recipe = (ItemCraftingRecipe) recipe;
            view = View.CREATE_RECIPE;
            this.currentValidation = recipe.getValidation();
            this.craftTime = recipe.getCraftingTime();
            this.craftStation = recipe.getCraftingBlock();
            this.breakStation = recipe.breakStation();
        }
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7Create New Craft Recipe");
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
                if (e.getCurrentItem().equals(confirmButton)){
                    e.setCancelled(true);
                }
            }
            new BukkitRunnable(){
                @Override
                public void run() {
                    if (view == View.PICK_INGREDIENTS){
                        ItemStack[] originalContents = inventory.getContents();
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
            }.runTaskLater(Main.getPlugin(), 2L);
        }
        if (e.getCurrentItem() != null){
            if (e.getCurrentItem().equals(returnToMenuButton)) {
                // go back to main menu
            } else if (e.getCurrentItem().equals(viewCustomCraftRecipesButton)){
                currentPage = 1;
                view = View.VIEW_RECIPES;
            } else if (e.getCurrentItem().equals(nextPageButton)){
                currentPage++;
            } else if (e.getCurrentItem().equals(previousPageButton)){
                currentPage--;
            } else if (e.getCurrentItem().equals(ingredientsButton)){
                view = View.PICK_INGREDIENTS;
                setMenuItems();
                return;
            } else if (e.getCurrentItem().equals(dynamicModifierButton)){
                view = View.VIEW_MODIFIERS;
            } else if (e.getCurrentItem().equals(timeButton)){
                handleTimeButton(e.getClick());
            } else if (e.getCurrentItem().equals(breakStationButton)){
                breakStation = !breakStation;
                if (current_craft_recipe != null){
                    current_craft_recipe.setBreakStation(breakStation);
                }
            } else if (e.getCurrentItem().equals(confirmButton)){
                switch (view){
                    case NEW_MODIFIER: {
                        if (currentModifier != null){
                            for (DynamicItemModifier mod : currentModifiers){
                                if (mod.getName().equalsIgnoreCase(currentModifier.getName())){
                                    currentModifiers.remove(mod);
                                    break;
                                }
                            }
                            currentModifier.setPriority(priority);
                            priority = ModifierPriority.NEUTRAL;
                            currentModifier.setStrength(modifierStrength);
                            modifierStrength = 0D;
                            currentModifiers.add(currentModifier);
                            currentModifier = null;
                            view = View.VIEW_MODIFIERS;
                        }
                        break;
                    }
                    case PICK_MODIFIERS: {
                        view = View.VIEW_MODIFIERS;
                        break;
                    }
                    case VIEW_MODIFIERS: {
                        if (current_craft_recipe != null){
                            current_craft_recipe.setItemModifers(currentModifiers);
                            view = View.CREATE_RECIPE;
                        }
                        break;
                    }
                    case PICK_INGREDIENTS: {
                        ItemStack[] originalContents = inventory.getContents();
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
                        customRecipeIngredients = new HashSet<>();
                        for (ItemStack i : contents.keySet()){
                            i.setAmount(contents.get(i));
                            customRecipeIngredients.add(i);
                        }
                        if (current_craft_recipe != null){
                            current_craft_recipe.setIngredients(customRecipeIngredients);
                            view = View.CREATE_RECIPE;
                        }
                        setMenuItems();
                        return;
                    }
                    case CREATE_RECIPE: {
                        if (current_craft_recipe != null){
                            current_craft_recipe.setIngredients(customRecipeIngredients);
                            current_craft_recipe.setItemModifers(currentModifiers);
                            current_craft_recipe.setCraftingTime(craftTime);
                            current_craft_recipe.setResult(craftResultButton);
                            current_craft_recipe.setBreakStation(breakStation);
                            if (!(currentValidation instanceof DefaultValidation)){
                                current_craft_recipe.setValidation(currentValidation);
                            }
                            current_craft_recipe.setCraftingBlock(craftStationButton.getType());
                            CustomRecipeManager.getInstance().update(old_craft_recipe, current_craft_recipe);
                            current_craft_recipe = null;
                            view = View.VIEW_RECIPES;
                        }
                        break;
                    }
                }
            } else if (e.getCurrentItem().equals(createNewButton)){
                view = View.PICK_MODIFIERS;
            } else if (e.getCurrentItem().equals(modifierStrengthButton)){
                handleModifierStrength(e.getClick());
            } else if (e.getCurrentItem().equals(craftValidationButton)){
                List<CraftValidation> availableValidations = CraftValidationManager.getInstance().getValidations(craftStation);
                if (!availableValidations.isEmpty()){
                    if (currentValidationIndex + 1 < availableValidations.size()){
                        currentValidationIndex += 1;
                    } else {
                        currentValidationIndex = 0;
                    }
                    currentValidation = availableValidations.get(currentValidationIndex);
                }
            } else if (e.getCurrentItem().equals(modifierPriorityButton)){
                handleModifierPriority(e.getClick());
            } else if (e.getCurrentItem().equals(cancelButton)){
                switch (view){
                    case NEW_MODIFIER:{
                        if (currentModifier != null){
                            currentModifiers.remove(currentModifier);
                            view = View.VIEW_MODIFIERS;
                        }
                        break;
                    }
                    case CREATE_RECIPE:{
                        if (current_craft_recipe != null){
                            CustomRecipeManager.getInstance().unregister(old_craft_recipe);
                            view = View.VIEW_RECIPES;
                        }
                    }
                }
            }
            if (view == View.CREATE_RECIPE){
                if (!(e.getClickedInventory() instanceof PlayerInventory)){
                    e.setCancelled(true);
                    if (e.getCursor() != null){
                        if (e.getCursor().getType() != Material.AIR){
                            if (e.getSlot() == 14){
                                if (current_craft_recipe != null){
                                    craftResultButton = e.getCursor().clone();
                                    current_craft_recipe.setResult(craftResultButton);
                                }
                            } else if (e.getSlot() == 30){
                                if (e.getCursor().getType().isBlock()){
                                    List<Material> similarMaterials = ItemUtils.getSimilarMaterials(craftStationButton.getType());
                                    craftStationButton.setType(e.getCursor().getType());
                                    if (similarMaterials != null) {
                                        craftStationButton.setType(similarMaterials.get(0));
                                    }
                                    craftStationButton.setAmount(1);
                                    craftStation = craftStationButton.getType();
                                    if (current_craft_recipe != null){
                                        current_craft_recipe.setCraftingBlock(craftStation);
                                    }
                                    currentValidationIndex = 0;
                                }
                            }
                        }
                    }
                }
            }
            if (e.getCurrentItem() != null){
                if (e.getCurrentItem().getItemMeta() != null){
                    if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(buttonNameKey, PersistentDataType.STRING)){
                        switch (view){
//                case VIEW_VANILLA:
//                    DynamicShapedRecipe vanillaRecipe = CustomRecipeManager.getInstance().getDynamicShapedRecipe(
//                            e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING));
//                    if (vanillaRecipe != null){
//                        current_vanilla_recipe = vanillaRecipe;
//                        view = View.CREATE_VANILLA;
//                    }
//                    break;
                            case VIEW_RECIPES:
                                AbstractCustomCraftingRecipe craftRecipe = CustomRecipeManager.getInstance().getRecipeByName(
                                        e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING));
                                if (craftRecipe instanceof ItemCraftingRecipe){
                                    current_craft_recipe = ((ItemCraftingRecipe) craftRecipe).clone();
                                    old_craft_recipe = ((ItemCraftingRecipe) craftRecipe).clone();
                                    view = View.CREATE_RECIPE;
                                }
                                break;
                            case PICK_MODIFIERS:
                                currentModifier = DynamicItemModifierManager.getInstance().createModifier(
                                        e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING),
                                        0D,
                                        ModifierPriority.NEUTRAL
                                );
                                currentModifier.setStrength(currentModifier.getDefaultStrength());
                                view = View.NEW_MODIFIER;
                                break;
                            case VIEW_MODIFIERS:
                                String clickedModifier = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING);
                                for (DynamicItemModifier modifier : currentModifiers){
                                    if (modifier.getName().equals(clickedModifier)){
                                        currentModifier = modifier;
                                        modifierStrength = modifier.getStrength();
                                        priority = modifier.getPriority();
                                        view = View.NEW_MODIFIER;
                                        break;
                                    }
                                }
                                break;
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
    public void setMenuItems() {
        inventory.clear();
        switch (view){
            case VIEW_RECIPES: setCustomCraftRecipeView();
                return;
            case CREATE_RECIPE: setCreateCraftRecipeView();
                return;
            case PICK_INGREDIENTS: setPickIngredientsView();
                return;
            case VIEW_MODIFIERS: setViewModifiersView();
                return;
            case PICK_MODIFIERS: setPickModifiersView();
                return;
            case NEW_MODIFIER: setNewModifierView();
        }
    }

    private void setNewModifierView(){
        if (currentModifier != null){
            List<String> modifierIconLore = new ArrayList<>(Utils.separateStringIntoLines(currentModifier.toString(), 40));
            modifierIconLore.add(Utils.chat("&8&m                                        "));
            modifierIconLore.addAll(Utils.separateStringIntoLines(currentModifier.getDescription(), 40));
            ItemStack modifierButton = Utils.createItemStack(currentModifier.getIcon(),
                    Utils.chat(currentModifier.getDisplayName()),
                    modifierIconLore);

            List<String> strengthButtonLore = new ArrayList<>(Utils.separateStringIntoLines(currentModifier.toString(), 40));
            ItemMeta strengthButtonMeta = modifierStrengthButton.getItemMeta();
            assert strengthButtonMeta != null;
            strengthButtonMeta.setLore(strengthButtonLore);
            modifierStrengthButton.setItemMeta(strengthButtonMeta);

            List<String> modifierPriorityLore = new ArrayList<>(Utils.separateStringIntoLines(priority.getDescription().replace("%priority%", priorityToNumber(priority)), 40));
            ItemMeta priorityButtonMeta = modifierPriorityButton.getItemMeta();
            assert priorityButtonMeta != null;
            priorityButtonMeta.setLore(modifierPriorityLore);
            modifierPriorityButton.setItemMeta(priorityButtonMeta);

            inventory.setItem(13, modifierButton);
            inventory.setItem(30, modifierPriorityButton);
            inventory.setItem(32, modifierStrengthButton);
        }
        inventory.setItem(53, confirmButton);
        inventory.setItem(45, cancelButton);
    }

    private void setPickModifiersView(){
        Map<String, DynamicItemModifier> modifiers = DynamicItemModifierManager.getInstance().getModifiers();
        List<String> currentStringModifiers = currentModifiers.stream().map(DynamicItemModifier::getName).collect(Collectors.toList());
        List<ItemStack> totalModifierButtons = new ArrayList<>();
        for (String modifier : modifiers.keySet()){
            if (currentStringModifiers.contains(modifier)) continue;
            DynamicItemModifier currentModifier = DynamicItemModifierManager.getInstance().createModifier(modifier, 0D, ModifierPriority.NEUTRAL);
            List<String> modifierIconLore = new ArrayList<>(Utils.separateStringIntoLines(currentModifier.getDescription(), 40));
            ItemStack modifierIcon = Utils.createItemStack(currentModifier.getIcon(),
                    currentModifier.getDisplayName(),
                    modifierIconLore);

            ItemMeta modifierIconMeta = modifierIcon.getItemMeta();
            assert modifierIconMeta != null;
            modifierIconMeta.getPersistentDataContainer().set(buttonNameKey, PersistentDataType.STRING, currentModifier.getName());
            modifierIcon.setItemMeta(modifierIconMeta);
            totalModifierButtons.add(modifierIcon);
        }
        totalModifierButtons.sort(Comparator.comparing(ItemStack::getType));
        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, totalModifierButtons);

        if (currentPage > pages.size()) currentPage = pages.size();
        if (currentPage < 1) currentPage = 1;

        if (pages.size() > 0){
            for (ItemStack i : pages.get(currentPage - 1)){
                inventory.addItem(i);
            }
        }
        inventory.setItem(45, previousPageButton);
        inventory.setItem(53, nextPageButton);
        inventory.setItem(49, confirmButton);
    }

    private void setViewModifiersView(){
        List<DynamicItemModifier> modifiers = currentModifiers.stream().limit(45).collect(Collectors.toList());
        modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
        for (DynamicItemModifier modifier : modifiers){
            List<String> modifierIconLore = new ArrayList<>();
            modifierIconLore.addAll(Utils.separateStringIntoLines(Utils.chat(modifier.toString()), 40));
            modifierIconLore.add(Utils.chat("&8&m                            "));
            modifierIconLore.addAll(Utils.separateStringIntoLines(modifier.getDescription(), 40));
            ItemStack modifierIcon = Utils.createItemStack(modifier.getIcon(),
                    modifier.getDisplayName(),
                    modifierIconLore);

            ItemMeta modifierIconMeta = modifierIcon.getItemMeta();
            assert modifierIconMeta != null;
            modifierIconMeta.getPersistentDataContainer().set(buttonNameKey, PersistentDataType.STRING, modifier.getName());
            modifierIcon.setItemMeta(modifierIconMeta);
            inventory.addItem(modifierIcon);
        }
        if (currentModifiers.size() <= 44){
            inventory.addItem(createNewButton);
        }
        inventory.setItem(49, confirmButton);
    }

    private void setPickIngredientsView(){
        for (ItemStack ingredient : customRecipeIngredients.stream().limit(45L).collect(Collectors.toList())){
            inventory.addItem(ingredient);
        }
        inventory.setItem(49, confirmButton);
    }

    private void setCreateCraftRecipeView(){
        if (current_craft_recipe != null){
            craftResultButton = current_craft_recipe.getResult();
            customRecipeIngredients = current_craft_recipe.getIngredients();
            craftStation = current_craft_recipe.getCraftingBlock();
            craftStationButton.setType(craftStation);
            currentModifiers = new ArrayList<>(current_craft_recipe.getItemModifers());
            craftTime = current_craft_recipe.getCraftingTime();
            breakStation = current_craft_recipe.breakStation();
        }

        List<String> ingredientButtonLore = new ArrayList<>();
        for (ItemStack ingredient : customRecipeIngredients){
            ingredientButtonLore.add(Utils.chat("&e"+ingredient.getAmount() + " &7x&e " + getItemName(ingredient)));
        }
        ItemMeta ingredientButtonMeta = ingredientsButton.getItemMeta();
        assert ingredientButtonMeta != null;
        ingredientButtonMeta.setLore(ingredientButtonLore);
        ingredientsButton.setItemMeta(ingredientButtonMeta);

        List<String> modifierButtonLore = new ArrayList<>();
        for (DynamicItemModifier modifier : currentModifiers){
            modifierButtonLore.addAll(Utils.separateStringIntoLines(Utils.chat("&7- " + modifier.toString()), 40));
        }
        ItemMeta modifierButtonMeta = dynamicModifierButton.getItemMeta();
        assert modifierButtonMeta != null;
        modifierButtonMeta.setLore(modifierButtonLore);
        dynamicModifierButton.setItemMeta(modifierButtonMeta);

        ItemMeta timeButtonMeta = timeButton.getItemMeta();
        assert timeButtonMeta != null;
        timeButtonMeta.setDisplayName(Utils.chat(String.format("&e&lTime to craft: %.1fs", ((double) craftTime) / 1000D)));
        timeButton.setItemMeta(timeButtonMeta);

        ItemMeta breakStationMeta = breakStationButton.getItemMeta();
        assert breakStationMeta != null;
        breakStationMeta.setDisplayName(Utils.chat("&c&lBreak station after use: " + ((breakStation) ? "Yes" : "No")));
        breakStationButton.setItemMeta(breakStationMeta);

        List<String> validationButtonLore = new ArrayList<>();
        ItemMeta validationButtonMeta = dynamicModifierButton.getItemMeta();
        assert validationButtonMeta != null;
        if (this.currentValidation != null){
            validationButtonLore.addAll(Utils.separateStringIntoLines(Utils.chat(this.currentValidation.getDescription()), 40));
            validationButtonMeta.setDisplayName(Utils.chat(this.currentValidation.getDisplayName()));
            if (this.currentValidation.getBlock() == null){
                craftValidationButton.setType(Material.BARRIER);
            } else {
                craftValidationButton.setType(this.currentValidation.getBlock());
            }
        } else {
            validationButtonLore.addAll(Utils.separateStringIntoLines(Utils.chat(
                    "&7No validation available and default is not set, this is not intended and you should contact the developer- but it should still work."
            ), 40));
            validationButtonMeta.setDisplayName(Utils.chat("&7No validation, block state ignored"));
            craftValidationButton.setType(Material.BARRIER);
        }
        validationButtonMeta.setLore(validationButtonLore);
        craftValidationButton.setItemMeta(validationButtonMeta);

        inventory.setItem(12, ingredientsButton);
        inventory.setItem(14, craftResultButton);
        inventory.setItem(19, cancelButton);
        inventory.setItem(25, confirmButton);
        inventory.setItem(29, craftValidationButton);
        inventory.setItem(30, craftStationButton);
        inventory.setItem(32, dynamicModifierButton);
        inventory.setItem(39, timeButton);
        inventory.setItem(41, breakStationButton);
    }

    private void setCustomCraftRecipeView(){
        List<ItemStack> totalRecipeButtons = new ArrayList<>();
        for (AbstractCustomCraftingRecipe recipe : CustomRecipeManager.getInstance().getAllRecipes().values()){
            if (recipe instanceof ItemCraftingRecipe){
                ItemStack resultButton = ((ItemCraftingRecipe) recipe).getResult().clone();
                ItemMeta resultMeta = resultButton.getItemMeta();
                assert resultMeta != null;
                resultMeta.setDisplayName(recipe.getName());
                List<String> resultLore = new ArrayList<>();
                for (ItemStack ingredient : recipe.getIngredients()){
                    resultLore.add(Utils.chat("&e"+ingredient.getAmount() + " &7x&e " + getItemName(ingredient)));
                }
                resultLore.add(Utils.chat("&8&m                                      "));
                for (DynamicItemModifier modifier : recipe.getItemModifers()){
                    resultLore.addAll(Utils.separateStringIntoLines(Utils.chat("&7- " + modifier.toString()), 40));
                }
                if (resultMeta.getLore() != null){
                    resultLore.addAll(resultMeta.getLore());
                }
                resultLore.add(Utils.chat("&7Crafted on: &e"+ recipe.getCraftingBlock().toString().toLowerCase().replace("_", " ")));
                resultMeta.setLore(resultLore);
                resultMeta.getPersistentDataContainer().set(buttonNameKey, PersistentDataType.STRING, recipe.getName());
                resultButton.setItemMeta(resultMeta);
                totalRecipeButtons.add(resultButton);
            }
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

    private enum View{
        VIEW_RECIPES,
        CREATE_RECIPE,
        PICK_INGREDIENTS,
        PICK_MODIFIERS,
        VIEW_MODIFIERS,
        NEW_MODIFIER
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

    private void handleTimeButton(ClickType clickType){
        switch (clickType){
            case LEFT: craftTime += 100;
                break;
            case RIGHT: {
                if (craftTime - 100 < 0) craftTime = 0;
                else craftTime -= 100;
                break;
            }
            case SHIFT_LEFT: craftTime += 1000;
                break;
            case SHIFT_RIGHT: {
                if (craftTime - 1000 < 0) craftTime = 0;
                else craftTime -= 1000;
            }
        }
        if (current_craft_recipe != null){
            current_craft_recipe.setCraftingTime(craftTime);
        }
    }

    private void handleModifierPriority(ClickType clickType){
        switch (clickType){
            case LEFT: {
                switch (priority){
                    case NEUTRAL: priority = ModifierPriority.SOON;
                        break;
                    case SOON: priority = ModifierPriority.SOONEST;
                        break;
                    case SOONEST: priority = ModifierPriority.LAST;
                        break;
                    case LAST: priority = ModifierPriority.LATER;
                        break;
                    case LATER: priority = ModifierPriority.NEUTRAL;
                        break;
                }
                break;
            }
            case RIGHT:{
                switch (priority){
                    case NEUTRAL: priority = ModifierPriority.LATER;
                        break;
                    case SOON: priority = ModifierPriority.NEUTRAL;
                        break;
                    case SOONEST: priority = ModifierPriority.SOON;
                        break;
                    case LAST: priority = ModifierPriority.SOONEST;
                        break;
                    case LATER: priority = ModifierPriority.LAST;
                        break;
                }
                break;
            }
        }
        currentModifier.setPriority(priority);
    }

    private void handleModifierStrength(ClickType clickType){
        if (currentModifier != null){
            switch (clickType){
                case LEFT: {
                    if (modifierStrength + currentModifier.getSmallStepIncrease() > currentModifier.getMaxStrength()) {
                        modifierStrength = currentModifier.getMaxStrength();
                    } else if (modifierStrength == currentModifier.getMaxStrength()){
                        modifierStrength = currentModifier.getMinStrength();
                    } else {
                        modifierStrength += currentModifier.getSmallStepIncrease();
                    }
                    break;
                }
                case RIGHT: {
                    if (modifierStrength - currentModifier.getSmallStepDecrease() < currentModifier.getMinStrength()){
                        modifierStrength = currentModifier.getMinStrength();
                    } else if (modifierStrength == currentModifier.getMinStrength()){
                        modifierStrength = currentModifier.getMaxStrength();
                    } else {
                        modifierStrength -= currentModifier.getSmallStepDecrease();
                    }
                    break;
                }
                case SHIFT_LEFT: {
                    if (modifierStrength + currentModifier.getBigStepIncrease() > currentModifier.getMaxStrength()) {
                        modifierStrength = currentModifier.getMaxStrength();
                    } else if (modifierStrength == currentModifier.getMaxStrength()){
                        modifierStrength = currentModifier.getMinStrength();
                    } else {
                        modifierStrength += currentModifier.getBigStepIncrease();
                    }
                    break;
                }
                case SHIFT_RIGHT: {
                    if (modifierStrength - currentModifier.getBigStepDecrease() < currentModifier.getMinStrength()){
                        modifierStrength = currentModifier.getMinStrength();
                    } else if (modifierStrength == currentModifier.getMinStrength()){
                        modifierStrength = currentModifier.getMaxStrength();
                    } else {
                        modifierStrength -= currentModifier.getBigStepDecrease();
                    }
                }
            }
            currentModifier.setStrength(modifierStrength);
        }
    }

    private String priorityToNumber(ModifierPriority priority){
        switch (priority){
            case SOONEST: return "5";
            case SOON: return "4";
            case NEUTRAL: return "3";
            case LATER: return "2";
            case LAST: return "1";
        }
        return "";
    }
}
