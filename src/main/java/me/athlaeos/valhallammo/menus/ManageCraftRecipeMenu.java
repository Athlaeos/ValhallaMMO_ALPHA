package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.AbstractCustomCraftingRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.ItemCraftingRecipe;
import me.athlaeos.valhallammo.dom.RequirementType;
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

public class ManageCraftRecipeMenu extends Menu implements CraftingManagerMenu{
    private View view = View.VIEW_RECIPES;
    private final NamespacedKey buttonNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "button_recipe_name");
    private ItemCraftingRecipe current_craft_recipe = null;
    private ItemCraftingRecipe old_craft_recipe = null;

    private CraftValidation currentValidation = null;
    private int currentValidationIndex = 0;

    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;
    private int currentPage = 0;

    private final ItemStack toolIDRequiredButton = Utils.createItemStack(
            Material.IRON_HOE,
            Utils.chat("&7&lTool ID Required"),
            new ArrayList<>()
    );
    private final ItemStack toolRequiredTypeButton = Utils.createItemStack(
            Material.IRON_HOE,
            Utils.chat("&7&lTool Required Type"),
            new ArrayList<>()
    );

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
    private final ItemStack exactMetaButton = Utils.createItemStack(
            Material.PAPER,
            Utils.chat("&7&lRequire exact meta:"),
            new ArrayList<>()
    );
    private final ItemStack timeButton = Utils.createItemStack(
            Material.CLOCK,
            Utils.chat("&e&lTime to craft"),
            new ArrayList<>()
    );
    private final ItemStack consecutiveCraftsButton = Utils.createItemStack(
            Material.REPEATER,
            Utils.chat("&7&lRepeat crafting"),
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

    private int toolIDRequired = -1;
    private int toolRequiredType = 0;
    private boolean breakStation = false;
    private int craftTime = 2500;
    private int consecutiveCrafts = 8;
    private boolean exactMeta = true;
    private Material craftStation = Material.CRAFTING_TABLE;
    private Collection<ItemStack> customRecipeIngredients = new ArrayList<>();
    private Collection<DynamicItemModifier> currentModifiers = new HashSet<>();

    public ManageCraftRecipeMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        this.currentValidation = BlockCraftStateValidationManager.getInstance().getDefaultValidation();
    }

    public ManageCraftRecipeMenu(PlayerMenuUtility playerMenuUtility, ItemCraftingRecipe recipe){
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        if (recipe != null){
            current_craft_recipe = recipe;
            view = View.CREATE_RECIPE;
            this.currentValidation = recipe.getValidation();
            this.craftTime = recipe.getCraftingTime();
            this.craftStation = recipe.getCraftingBlock();
            this.breakStation = recipe.breakStation();
            this.exactMeta = recipe.requireExactMeta();
            this.consecutiveCrafts = recipe.getConsecutiveCrafts();
            this.toolIDRequired = recipe.getRequiredToolId();
            this.toolRequiredType = recipe.getToolRequirementType();
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
            } else if (e.getCurrentItem().equals(exactMetaButton)) {
                exactMeta = !exactMeta;
                current_craft_recipe.setRequireExactMeta(exactMeta);
            } else if (e.getCurrentItem().equals(consecutiveCraftsButton)) {
                handleConsecutiveCraftsButton(e.getClick());
            } else if (e.getCurrentItem().equals(dynamicModifierButton)){
                playerMenuUtility.setPreviousMenu(this);
                new DynamicModifierMenu(playerMenuUtility, currentModifiers).open();
                return;
            } else if (e.getCurrentItem().equals(timeButton)){
                handleTimeButton(e.getClick());
            } else if (e.getCurrentItem().equals(toolIDRequiredButton)){
                handleToolRequiredButton(e.getClick());
            } else if (e.getCurrentItem().equals(toolRequiredTypeButton)){
                handleToolRequiredTypeButton(e.getClick());
            } else if (e.getCurrentItem().equals(breakStationButton)){
                breakStation = !breakStation;
                if (current_craft_recipe != null){
                    current_craft_recipe.setBreakStation(breakStation);
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
                            current_craft_recipe.setModifiers(currentModifiers);
                            current_craft_recipe.setCraftingTime(craftTime);
                            current_craft_recipe.setResult(craftResultButton);
                            current_craft_recipe.setBreakStation(breakStation);
                            current_craft_recipe.setValidation(currentValidation);
                            current_craft_recipe.setCraftingBlock(craftStationButton.getType());
                            current_craft_recipe.setToolRequirementType(toolRequiredType);
                            current_craft_recipe.setRequiredToolId(toolIDRequired);
                            CustomRecipeManager.getInstance().update(old_craft_recipe, current_craft_recipe);
                            current_craft_recipe = null;
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
                    if (current_craft_recipe != null) {
                        CustomRecipeManager.getInstance().unregister(old_craft_recipe);
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
                                if (current_craft_recipe != null){
                                    craftResultButton = e.getCursor().clone();
                                    current_craft_recipe.setResult(craftResultButton);
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
                        if (view == View.VIEW_RECIPES) {
                            AbstractCustomCraftingRecipe craftRecipe = CustomRecipeManager.getInstance().getRecipeByName(
                                    e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING));
                            if (craftRecipe instanceof ItemCraftingRecipe) {
                                current_craft_recipe = ((ItemCraftingRecipe) craftRecipe).clone();
                                old_craft_recipe = ((ItemCraftingRecipe) craftRecipe).clone();
                                view = View.CREATE_RECIPE;
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
            case VIEW_RECIPES: setCustomCraftRecipeView();
                return;
            case CREATE_RECIPE: setCreateCraftRecipeView();
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

    private void setCreateCraftRecipeView(){
        if (current_craft_recipe != null){
            craftResultButton = current_craft_recipe.getResult();
            customRecipeIngredients = current_craft_recipe.getIngredients();
            craftStation = current_craft_recipe.getCraftingBlock();
            craftStationButton.setType(craftStation);
            currentModifiers = new ArrayList<>(current_craft_recipe.getItemModifers());
            craftTime = current_craft_recipe.getCraftingTime();
            breakStation = current_craft_recipe.breakStation();
            consecutiveCrafts = current_craft_recipe.getConsecutiveCrafts();
            exactMeta = current_craft_recipe.requireExactMeta();
            currentValidation = current_craft_recipe.getValidation();
            toolIDRequired = current_craft_recipe.getRequiredToolId();
            toolRequiredType = current_craft_recipe.getToolRequirementType();
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

        ItemMeta timeButtonMeta = timeButton.getItemMeta();
        assert timeButtonMeta != null;
        timeButtonMeta.setDisplayName(Utils.chat(String.format("&e&lTime to craft: %.1fs", ((double) craftTime) / 1000D)));
        timeButton.setItemMeta(timeButtonMeta);

        RequirementType type = null;
        if (toolRequiredType < RequirementType.values().length && toolRequiredType >= 0){
            type = RequirementType.values()[toolRequiredType];
        }
        ItemMeta toolIDRequiredMeta = toolIDRequiredButton.getItemMeta();
        assert toolIDRequiredMeta != null;
        ItemMeta toolRequirementTypeMeta = toolRequiredTypeButton.getItemMeta();
        assert toolRequirementTypeMeta != null;
        toolIDRequiredMeta.setLore(Collections.singletonList(
                Utils.chat("&cInvalid configuration")
        ));
        toolRequirementTypeMeta.setLore(Collections.singletonList(
                Utils.chat("&cInvalid configuration")
        ));
        if (type != null){
            if (toolIDRequired < 0){
                toolIDRequiredMeta.setLore(Arrays.asList(
                        Utils.chat("&7Recipe requires &eno special tool"),
                        Utils.chat("&7to craft. Can be crafted with"),
                        Utils.chat("&7empty hand")
                ));
                toolRequirementTypeMeta.setLore(Arrays.asList(
                        Utils.chat("&7Recipe requires &eno special tool"),
                        Utils.chat("&7to craft. Can be crafted with"),
                        Utils.chat("&7empty hand")
                ));
            } else {
                switch (type){
                    case NO_TOOL_REQUIRED:{
                        toolIDRequiredMeta.setLore(Arrays.asList(
                                Utils.chat("&7Recipe requires &eno special tool"),
                                Utils.chat("&7to craft. Can be crafted with"),
                                Utils.chat("&7empty hand")
                        ));
                        toolRequirementTypeMeta.setLore(Arrays.asList(
                                Utils.chat("&7Recipe requires &eno special tool"),
                                Utils.chat("&7to craft. Can be crafted with"),
                                Utils.chat("&7empty hand")
                        ));
                        break;
                    }
                    case EQUALS:{
                        toolIDRequiredMeta.setLore(Arrays.asList(
                                Utils.chat("&7Recipe requires &ea tool with ID"),
                                Utils.chat("&eof exactly " + toolIDRequired + "&7 to craft."),
                                Utils.chat("&7Can not be crafted with empty hand")
                        ));
                        toolRequirementTypeMeta.setLore(Arrays.asList(
                                Utils.chat("&7Recipe requires &ea tool with ID"),
                                Utils.chat("&eof exactly " + toolIDRequired + "&7 to craft."),
                                Utils.chat("&7Can not be crafted with empty hand")
                        ));
                        break;
                    }
                    case EQUALS_OR_GREATER: {
                        toolIDRequiredMeta.setLore(Arrays.asList(
                                Utils.chat("&7Recipe requires &ea tool with ID"),
                                Utils.chat("&eof " + toolIDRequired + " or higher &7 to craft."),
                                Utils.chat("&7Can not be crafted with empty hand")
                        ));
                        toolRequirementTypeMeta.setLore(Arrays.asList(
                                Utils.chat("&7Recipe requires &ea tool with ID"),
                                Utils.chat("&eof " + toolIDRequired + " or higher &7 to craft."),
                                Utils.chat("&7Can not be crafted with empty hand")
                        ));
                        break;
                    }
                    case EQUALS_OR_LESSER: {
                        toolIDRequiredMeta.setLore(Arrays.asList(
                                Utils.chat("&7Recipe requires &ea tool with ID"),
                                Utils.chat("&eof " + toolIDRequired + " or less &7 to craft."),
                                Utils.chat("&7Can not be crafted with empty hand")
                        ));
                        toolRequirementTypeMeta.setLore(Arrays.asList(
                                Utils.chat("&7Recipe requires &ea tool with ID"),
                                Utils.chat("&eof " + toolIDRequired + " or less &7 to craft."),
                                Utils.chat("&7Can not be crafted with empty hand")
                        ));
                        break;
                    }
                }
            }
            toolIDRequiredMeta.setDisplayName(Utils.chat(toolIDRequired >= 0 ? "&7&lTool ID Required: " + toolIDRequired : "&7&lNo special Tool Required"));
            toolRequirementTypeMeta.setDisplayName(Utils.chat("&7&lTool Required Type: " + type.toString().replace("_", " ")));
        }
        toolIDRequiredButton.setItemMeta(toolIDRequiredMeta);
        toolRequiredTypeButton.setItemMeta(toolRequirementTypeMeta);


        ItemMeta consecutiveCraftsMeta = consecutiveCraftsButton.getItemMeta();
        assert consecutiveCraftsMeta != null;
        consecutiveCraftsMeta.setDisplayName(Utils.chat(String.format("&e&lRepeat crafting: %dx", consecutiveCrafts)));
        consecutiveCraftsButton.setItemMeta(consecutiveCraftsMeta);

        ItemMeta requireExactMetaMeta = exactMetaButton.getItemMeta();
        assert requireExactMetaMeta != null;
        requireExactMetaMeta.setDisplayName(Utils.chat(String.format("&e&lRequire exact meta: %s", ((exactMeta) ? "Yes" : "No"))));
        exactMetaButton.setItemMeta(requireExactMetaMeta);

        ItemMeta breakStationMeta = breakStationButton.getItemMeta();
        assert breakStationMeta != null;
        breakStationMeta.setDisplayName(Utils.chat("&c&lBreak station after use: " + ((breakStation) ? "Yes" : "No")));
        breakStationButton.setItemMeta(breakStationMeta);

        List<String> validationButtonLore = new ArrayList<>();
        ItemMeta validationButtonMeta = craftValidationButton.getItemMeta();
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

        inventory.setItem(5, toolIDRequiredButton);
        inventory.setItem(6, toolRequiredTypeButton);
        inventory.setItem(11, exactMetaButton);
        inventory.setItem(12, ingredientsButton);
        inventory.setItem(14, craftResultButton);
        inventory.setItem(19, cancelButton);
        inventory.setItem(25, confirmButton);
        inventory.setItem(29, craftValidationButton);
        inventory.setItem(30, craftStationButton);
        inventory.setItem(32, dynamicModifierButton);
        inventory.setItem(39, timeButton);
        inventory.setItem(40, consecutiveCraftsButton);
        inventory.setItem(41, breakStationButton);
    }

    private void setCustomCraftRecipeView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> totalRecipeButtons = new ArrayList<>();
        for (AbstractCustomCraftingRecipe recipe : CustomRecipeManager.getInstance().getAllCustomRecipes().values()){
            if (recipe instanceof ItemCraftingRecipe){
                ItemStack resultButton = ((ItemCraftingRecipe) recipe).getResult();
                if (resultButton == null) continue;
                resultButton = resultButton.clone();
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

    @Override
    public void setCurrentModifiers(Collection<DynamicItemModifier> modifiers) {
        this.currentModifiers = modifiers;
        current_craft_recipe.setModifiers(modifiers);
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
        if (current_craft_recipe != null){
            current_craft_recipe.setCraftingTime(craftTime);
        }
    }

    private void handleToolRequiredButton(ClickType clickType){
        switch (clickType){
            case LEFT: toolIDRequired += 1;
                break;
            case RIGHT: {
                if (toolIDRequired - 1 < -1) toolIDRequired = -1;
                else toolIDRequired -= 1;
                break;
            }
            case SHIFT_LEFT: toolIDRequired += 10;
                break;
            case SHIFT_RIGHT: {
                if (toolIDRequired - 10 < 0) toolIDRequired = 0;
                else toolIDRequired -= 10;
            }
        }
        if (current_craft_recipe != null){
            current_craft_recipe.setRequiredToolId(toolIDRequired);
        }
    }

    private void handleToolRequiredTypeButton(ClickType clickType){
        switch (clickType){
            case LEFT:
            case SHIFT_LEFT: {
                if (toolRequiredType + 1 > RequirementType.values().length - 1) toolRequiredType = RequirementType.values().length - 1;
                else toolRequiredType += 1;
                break;
            }
            case RIGHT:
            case SHIFT_RIGHT:{
                if (toolRequiredType - 1 < 0) toolRequiredType = 0;
                else toolRequiredType -= 1;
                break;
            }
        }
        if (current_craft_recipe != null){
            current_craft_recipe.setToolRequirementType(toolRequiredType);
        }
    }

    private void handleConsecutiveCraftsButton(ClickType clickType){
        switch (clickType){
            case LEFT: consecutiveCrafts += 1;
                break;
            case RIGHT: {
                if (consecutiveCrafts - 1 < 1) consecutiveCrafts = 1;
                else consecutiveCrafts -= 1;
                break;
            }
            case SHIFT_LEFT: consecutiveCrafts += 10;
                break;
            case SHIFT_RIGHT: {
                if (consecutiveCrafts - 10 < 1) consecutiveCrafts = 1;
                else consecutiveCrafts -= 10;
            }
        }
        if (current_craft_recipe != null){
            current_craft_recipe.setConsecutiveCrafts(consecutiveCrafts);
        }
    }
}
