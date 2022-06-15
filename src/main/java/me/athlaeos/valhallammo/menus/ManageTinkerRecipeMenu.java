package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.AbstractCustomCraftingRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.ItemImprovementRecipe;
import me.athlaeos.valhallammo.items.BlockCraftStateValidationManager;
import me.athlaeos.valhallammo.items.blockstatevalidations.CraftValidation;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.utility.ItemUtils;
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

public class ManageTinkerRecipeMenu extends Menu implements CraftingManagerMenu{
    private View view = View.VIEW_RECIPES;
    private final NamespacedKey buttonNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "button_recipe_name");
    private ItemImprovementRecipe current_improvement_recipe = null;
    private ItemImprovementRecipe old_improvement_recipe = null;

    private CraftValidation currentValidation = null;
    private int currentValidationIndex = 0;

    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;
    private int currentPage = 0;

    private final ItemStack confirmButton = Utils.createItemStack(Material.STRUCTURE_VOID,
            Utils.chat("&b&lSave"),
            null);
    private final ItemStack cancelButton = Utils.createItemStack(Material.BARRIER,
            Utils.chat("&cDelete"),
            null);
    private final ItemStack exactMetaButton = Utils.createItemStack(
            Material.PAPER,
            Utils.chat("&7&lRequire exact meta:"),
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
    private final ItemStack returnToMenuButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lReturn to menu"),
            new ArrayList<>()
    );

    private boolean breakStation = false;
    private int craftTime = 2500;
    private boolean exactMeta = true;
    private Material craftStation = Material.CRAFTING_TABLE;
    private Collection<ItemStack> customRecipeIngredients = new ArrayList<>();
    private Collection<DynamicItemModifier> currentModifiers = new HashSet<>();

    public ManageTinkerRecipeMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        this.currentValidation = BlockCraftStateValidationManager.getInstance().getDefaultValidation();
    }

    public ManageTinkerRecipeMenu(PlayerMenuUtility playerMenuUtility, ItemImprovementRecipe recipe){
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        if (recipe != null){
            current_improvement_recipe = recipe;
            view = View.CREATE_RECIPE;
            this.currentValidation = recipe.getValidation();
            this.craftTime = recipe.getCraftingTime();
            this.craftStation = recipe.getCraftingBlock();
            this.breakStation = recipe.breakStation();
            this.exactMeta = recipe.requireExactMeta();
        }
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7Create New Tinker Recipe");
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
            if (e.getCurrentItem() != null || e.getSlot() >= 45){
                if (e.getCurrentItem().equals(confirmButton)){
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
            } else if (e.getCurrentItem().equals(dynamicModifierButton)){
                playerMenuUtility.setPreviousMenu(this);
                new DynamicModifierMenu(playerMenuUtility, currentModifiers).open();
                return;
            } else if (e.getCurrentItem().equals(exactMetaButton)) {
                exactMeta = !exactMeta;
                current_improvement_recipe.setRequireExactMeta(exactMeta);
            } else if (e.getCurrentItem().equals(timeButton)){
                handleTimeButton(e.getClick());
            } else if (e.getCurrentItem().equals(breakStationButton)){
                breakStation = !breakStation;
                if (current_improvement_recipe != null){
                    current_improvement_recipe.setBreakStation(breakStation);
                }
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
                        customRecipeIngredients = new HashSet<>();
                        for (ItemStack i : contents.keySet()){
                            i.setAmount(contents.get(i));
                            customRecipeIngredients.add(i);
                        }
                        if (current_improvement_recipe != null){
                            current_improvement_recipe.setIngredients(customRecipeIngredients);
                            view = View.CREATE_RECIPE;
                        }
                        setMenuItems();
                        return;
                    }
                    case CREATE_RECIPE: {
                        if (current_improvement_recipe != null){
                            current_improvement_recipe.setIngredients(customRecipeIngredients);
                            current_improvement_recipe.setModifiers(currentModifiers);
                            current_improvement_recipe.setCraftingTime(craftTime);
                            current_improvement_recipe.setRequiredItemType(improveResultButton.getType());
                            current_improvement_recipe.setBreakStation(breakStation);
                            current_improvement_recipe.setValidation(currentValidation);
                            current_improvement_recipe.setCraftingBlock(craftStationButton.getType());
                            CustomRecipeManager.getInstance().update(old_improvement_recipe, current_improvement_recipe);
                            current_improvement_recipe = null;
                            view = View.VIEW_RECIPES;
                        }
                        break;
                    }
                }
            } else if (e.getCurrentItem().equals(craftValidationButton)){
                List<CraftValidation> availableValidations = BlockCraftStateValidationManager.getInstance().getValidations(craftStation);
                if (!availableValidations.isEmpty()){
                    if (currentValidationIndex + 1 < availableValidations.size()){
                        currentValidationIndex += 1;
                    } else {
                        currentValidationIndex = 0;
                    }
                    currentValidation = availableValidations.get(currentValidationIndex);
                }
            } else if (e.getCurrentItem().equals(cancelButton)){
                if (view == View.CREATE_RECIPE) {
                    if (current_improvement_recipe != null) {
                        CustomRecipeManager.getInstance().unregister(old_improvement_recipe);
                        view = View.VIEW_RECIPES;
                    }
                }
            }
            if (view == View.CREATE_RECIPE){
                if (!(e.getClickedInventory() instanceof PlayerInventory)){
                    e.setCancelled(true);
                    if (e.getCursor() != null){
                        if (e.getCursor().getType() != Material.AIR){
                            if (e.getSlot() == 14){
                                if (current_improvement_recipe != null){
                                    improveResultButton.setType(e.getCursor().getType());
                                    current_improvement_recipe.setRequiredItemType(improveResultButton.getType());
                                }
                            } else if (e.getSlot() == 30){
                                if (e.getCursor().getType().isBlock()){
                                    Material similarMaterial = ItemUtils.getBaseMaterial(e.getCursor().getType());
                                    craftStationButton.setType(e.getCursor().getType());
                                    if (similarMaterial != null) {
                                        craftStationButton.setType(similarMaterial);
                                    }
                                    craftStationButton.setAmount(1);
                                    craftStation = craftStationButton.getType();
                                    if (current_improvement_recipe != null){
                                        current_improvement_recipe.setCraftingBlock(craftStation);
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
                                AbstractCustomCraftingRecipe improveRecipe = CustomRecipeManager.getInstance().getRecipeByName(
                                        e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING));
                                if (improveRecipe instanceof ItemImprovementRecipe){
                                    current_improvement_recipe = ((ItemImprovementRecipe) improveRecipe).clone();
                                    old_improvement_recipe = ((ItemImprovementRecipe) improveRecipe).clone();
                                    view = View.CREATE_RECIPE;
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
    public void handleMenu(InventoryDragEvent e) {

    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        for (int i = 0; i < getSlots(); i++){
            inventory.setItem(i, Utils.createItemStack(Material.GRAY_STAINED_GLASS_PANE, Utils.chat("&8 "), null));
        }
        switch (view){
            case VIEW_RECIPES: setCustomImproveRecipesView();
                return;
            case CREATE_RECIPE: setCreateImproveRecipeView();
                return;
            case PICK_INGREDIENTS: setPickIngredientsView();
        }
    }

    private void setPickIngredientsView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        for (ItemStack ingredient : customRecipeIngredients.stream().limit(45L).collect(Collectors.toList())){
            inventory.addItem(ingredient);
        }
        inventory.setItem(49, confirmButton);
    }

    private void setCreateImproveRecipeView(){
        if (current_improvement_recipe != null){
            improveResultButton.setType(current_improvement_recipe.getRequiredItemType());
            customRecipeIngredients = current_improvement_recipe.getIngredients();
            craftStation = current_improvement_recipe.getCraftingBlock();
            craftStationButton.setType(craftStation);
            currentModifiers = new ArrayList<>(current_improvement_recipe.getItemModifers());
            craftTime = current_improvement_recipe.getCraftingTime();
            breakStation = current_improvement_recipe.breakStation();
            exactMeta = current_improvement_recipe.requireExactMeta();
            currentValidation = current_improvement_recipe.getValidation();
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
        List<DynamicItemModifier> modifiers = new ArrayList<>(currentModifiers);
        modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
        for (DynamicItemModifier modifier : modifiers){
            modifierButtonLore.addAll(Utils.separateStringIntoLines(Utils.chat("&7- " + modifier.toString()), 40));
        }
        ItemMeta modifierButtonMeta = dynamicModifierButton.getItemMeta();
        assert modifierButtonMeta != null;
        modifierButtonMeta.setLore(modifierButtonLore);
        dynamicModifierButton.setItemMeta(modifierButtonMeta);

        ItemMeta requireExactMetaMeta = exactMetaButton.getItemMeta();
        assert requireExactMetaMeta != null;
        requireExactMetaMeta.setDisplayName(Utils.chat(String.format("&e&lRequire exact meta: %s", ((exactMeta) ? "Yes" : "No"))));
        exactMetaButton.setItemMeta(requireExactMetaMeta);

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
            craftValidationButton.setType(this.currentValidation.getIcon());
        } else {
            validationButtonLore.addAll(Utils.separateStringIntoLines(Utils.chat(
                    "&7No validation available and default is not set, this is not intended and you should contact the developer- but it should still work."
            ), 40));
            validationButtonMeta.setDisplayName(Utils.chat("&7No validation, block state ignored"));
            craftValidationButton.setType(Material.BARRIER);
        }
        validationButtonMeta.setLore(validationButtonLore);
        craftValidationButton.setItemMeta(validationButtonMeta);

        inventory.setItem(11, exactMetaButton);
        inventory.setItem(12, ingredientsButton);
        inventory.setItem(14, improveResultButton);
        inventory.setItem(19, cancelButton);
        inventory.setItem(25, confirmButton);
        inventory.setItem(29, craftValidationButton);
        inventory.setItem(30, craftStationButton);
        inventory.setItem(32, dynamicModifierButton);
        inventory.setItem(39, timeButton);
        inventory.setItem(41, breakStationButton);
    }

    private void setCustomImproveRecipesView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> totalRecipeButtons = new ArrayList<>();
        for (AbstractCustomCraftingRecipe recipe : CustomRecipeManager.getInstance().getAllCustomRecipes().values()){
            if (recipe instanceof ItemImprovementRecipe){
                ItemStack resultButton = new ItemStack(((ItemImprovementRecipe) recipe).getRequiredItemType());
                ItemMeta resultMeta = resultButton.getItemMeta();
                assert resultMeta != null;
                resultMeta.setDisplayName(recipe.getName());
                List<String> resultLore = new ArrayList<>();
                for (ItemStack ingredient : recipe.getIngredients()){
                    resultLore.add(Utils.chat("&e"+ingredient.getAmount() + " &7x&e " + getItemName(ingredient)));
                }
                resultLore.add(Utils.chat("&8&m                                      "));
                List<DynamicItemModifier> modifiers = new ArrayList<>(recipe.getItemModifers());
                modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                for (DynamicItemModifier modifier : modifiers){
                    resultLore.addAll(Utils.separateStringIntoLines(Utils.chat("&7- " + modifier.toString()), 40));
                }
                if (resultMeta.getLore() != null){
                    resultLore.addAll(resultMeta.getLore());
                }
                resultLore.add(Utils.chat("&7Tinkered on: &e"+ recipe.getCraftingBlock().toString().toLowerCase().replace("_", " ")));
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

    @Override
    public void setCurrentModifiers(Collection<DynamicItemModifier> modifiers) {
        this.currentModifiers = modifiers;
        current_improvement_recipe.setModifiers(modifiers);
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
        if (current_improvement_recipe != null){
            current_improvement_recipe.setCraftingTime(craftTime);
        }
    }
}
