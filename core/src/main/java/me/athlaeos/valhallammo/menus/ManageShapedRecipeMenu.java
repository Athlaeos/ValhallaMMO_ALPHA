package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicCraftingTableRecipe;
import me.athlaeos.valhallammo.dom.RequirementType;
import me.athlaeos.valhallammo.items.EquipmentClass;
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

import java.util.*;

public class ManageShapedRecipeMenu extends Menu implements CraftingManagerMenu{
    private View view = View.VIEW_RECIPES;
    private final NamespacedKey buttonNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "button_recipe_name");
    private DynamicCraftingTableRecipe currentRecipe = null;
    private DynamicCraftingTableRecipe oldRecipe = null;

    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;
    private int currentPage = 0;

    private final ItemStack confirmButton = Utils.createItemStack(Material.STRUCTURE_VOID,
            Utils.chat("&b&lSave"),
            null);
    private final ItemStack cancelButton = Utils.createItemStack(Material.BARRIER,
            Utils.chat("&cDelete"),
            null);


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

    private final ItemStack dynamicModifierButton = Utils.createItemStack(
            Material.BOOK,
            Utils.chat("&b&lDynamic Modifiers"),
            new ArrayList<>()
    );
    private final ItemStack requireExactMetaButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lRequire Precise Meta"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, during crafting the items used in the crafting table " +
                    "will need to have item metas exactly matching the items given in the recipe. Recommended if you're" +
                    " using custom materials. -n" +
                    "&cIf 'Tinker First Tool' is enabled, tools will not require exact metas. -n" +
                    "If false, only the item types will need to match. Meta is ignored."), 40)
    );
    private final ItemStack allowMaterialVariationsButton = Utils.createItemStack(
            Material.OAK_PLANKS,
            Utils.chat("&7&lAllow Ingredient Type Variations"),
            Utils.separateStringIntoLines(Utils.chat("&cIgnored if 'Require Precise Meta' is enabled. -n" +
                    "&7If true, during crafting the items used in the crafting table " +
                    "may also match some similar item types, like oak planks and birch planks, or cobblestone and blackstone. -n" +
                    "If false, these variations are not allowed. If oak planks are inserted, only oak planks can be used"), 40)
    );
    private final ItemStack requireCustomToolsButton = Utils.createItemStack(
            Material.CRAFTING_TABLE,
            Utils.chat("&7&lRequire Custom Tools"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, if a recipe contains tools/armor, they need" +
                    " to have ValhallaMMO custom characteristics. Such as quality, durability, or custom attributes." +
                    " If 'Require Precise Meta' is also enabled, these tools/armor pieces will be ignored and do not" +
                    " need to exactly match.-n" +
                    " If false, any tool/armor piece can be used even if they have no custom traits.-n"), 40)
    );
    private final ItemStack tinkerFirstItemButton = Utils.createItemStack(
            Material.ANVIL,
            Utils.chat("&7&lTinker First Tool"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, instead of crafting a new item all-together " +
                    "the first tool found in the crafting grid is used to produce an output. This is the " +
                    "vanilla crafting table alternative for ValhallaMMO's custom tinkering mechanic. "), 40)
    );
    private final ItemStack returnToMenuButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lReturn to menu"),
            new ArrayList<>()
    );
    private final ItemStack shapelessRecipeButton = Utils.createItemStack(
            Material.SLIME_BALL,
            Utils.chat("&7&lShapeless:"),
            new ArrayList<>()
    );

    private final ItemStack unlockedForEveryoneButton = Utils.createItemStack(
            Material.ARMOR_STAND,
            Utils.chat("&7&lUnlocked for everyone:"),
            new ArrayList<>()
    );

    private int toolIDRequired = -1;
    private int toolRequiredType = 0;
    private final List<Integer> grid3x3 = Arrays.asList(10, 11, 12, 19, 20, 21, 28, 29, 30);
    private boolean requireExactMeta = false;
    private boolean allowMaterialVariations = false;
    private boolean requireCustomTools = true;
    private boolean tinkerFirstItem = false;
    private boolean unlockedForEveryone = false;
    private boolean shapeless = false;
    private Map<Integer, ItemStack> exactItems = new HashMap<>();
    private ItemStack result = new ItemStack(Material.WOODEN_SWORD);
    private List<DynamicItemModifier> currentModifiers = new ArrayList<>();

    public ManageShapedRecipeMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
    }

    public ManageShapedRecipeMenu(PlayerMenuUtility playerMenuUtility, DynamicCraftingTableRecipe recipe){
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        if (recipe != null){
            currentRecipe = recipe.clone();
            oldRecipe = recipe.clone();
            view = View.CREATE_RECIPE;
            requireExactMeta = recipe.isUseMetadata();
            requireCustomTools = recipe.isRequireCustomTools();
            tinkerFirstItem = recipe.isTinkerFirstItem();
            allowMaterialVariations = recipe.isAllowIngredientVariations();
            unlockedForEveryone = recipe.isUnlockedForEveryone();
            toolIDRequired = recipe.getRequiredToolId();
            toolRequiredType = recipe.getToolRequirementType();
            result = recipe.getResult();
            shapeless = recipe.isShapeless();

            exactItems = new HashMap<>(recipe.getExactItems());
        }
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7Create New Shaped Recipe");
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
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem != null){
            if (clickedItem.equals(returnToMenuButton)) {
                new RecipeCategoryMenu(playerMenuUtility).open();
                return;
            } else if (clickedItem.equals(unlockedForEveryoneButton)){
                unlockedForEveryone = !unlockedForEveryone;
                if (currentRecipe != null){
                    currentRecipe.setUnlockedForEveryone(unlockedForEveryone);
                }
            } else if (clickedItem.equals(shapelessRecipeButton)){
                shapeless = !shapeless;
                if (currentRecipe != null){
                    currentRecipe.setShapeless(shapeless);
                }
            } else if (clickedItem.equals(nextPageButton)){
                currentPage++;
            } else if (clickedItem.equals(previousPageButton)){
                currentPage--;
            } else if (e.getCurrentItem().equals(toolIDRequiredButton)){
                handleToolRequiredButton(e.getClick());
            } else if (e.getCurrentItem().equals(toolRequiredTypeButton)){
                handleToolRequiredTypeButton(e.getClick());
            } else if (clickedItem.equals(dynamicModifierButton)){
                playerMenuUtility.setPreviousMenu(this);
                new DynamicModifierMenu(playerMenuUtility, currentModifiers).open();
                return;
            } else if (clickedItem.equals(requireExactMetaButton)){
                requireExactMeta = !requireExactMeta;
                if (currentRecipe != null){
                    currentRecipe.setUseMetadata(requireExactMeta);
                }
            } else if (clickedItem.equals(allowMaterialVariationsButton)){
                allowMaterialVariations = !allowMaterialVariations;
                if (currentRecipe != null){
                    currentRecipe.setAllowIngredientVariations(allowMaterialVariations);
                }
            } else if (clickedItem.equals(requireCustomToolsButton)){
                requireCustomTools = !requireCustomTools;
                if (currentRecipe != null){
                    currentRecipe.setRequireCustomTools(requireCustomTools);
                }
            } else if (clickedItem.equals(tinkerFirstItemButton)){
                tinkerFirstItem = !tinkerFirstItem;
                if (currentRecipe != null){
                    currentRecipe.setUseMetadata(tinkerFirstItem);
                }
            } else if (clickedItem.equals(confirmButton)){
                if (view == View.CREATE_RECIPE) {
                    if (currentRecipe != null) {
                        DynamicCraftingTableRecipe backupRecipe = currentRecipe.clone();
                        currentRecipe = generateRecipe();
                        if (currentRecipe != null) {
                            currentRecipe.setUnlockedForEveryone(unlockedForEveryone);
                            currentRecipe.setShapeless(shapeless);
                            currentRecipe.setAllowIngredientVariations(allowMaterialVariations);
                            currentRecipe.setExactItems(exactItems);
                            currentRecipe.setImproveFirstEquipment(tinkerFirstItem);
                            currentRecipe.setUseMetadata(requireExactMeta);
                            currentRecipe.setModifiers(currentModifiers);
                            currentRecipe.setToolRequirementType(toolRequiredType);
                            currentRecipe.setRequiredToolId(toolIDRequired);
                            CustomRecipeManager.getInstance().update(oldRecipe, currentRecipe);
                            currentRecipe = null;
                            view = View.VIEW_RECIPES;
                        } else {
                            e.getWhoClicked().sendMessage(Utils.chat("&cPlease add some ingredients"));
                            currentRecipe = backupRecipe;
                        }
                    }
                }
            } else if (clickedItem.equals(cancelButton)){
                if (view == View.CREATE_RECIPE) {
                    if (currentRecipe != null) {
                        CustomRecipeManager.getInstance().unregister(oldRecipe);
                        view = View.VIEW_RECIPES;
                    }
                }
            }
            if (clickedItem.getItemMeta() != null){
                if (clickedItem.getItemMeta().getPersistentDataContainer().has(buttonNameKey, PersistentDataType.STRING)){
                    if (view == View.VIEW_RECIPES) {
                        DynamicCraftingTableRecipe shapedRecipe = CustomRecipeManager.getInstance().getDynamicShapedRecipe(
                                clickedItem.getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING));
                        if (shapedRecipe != null){
                            if (e.getClick() == ClickType.MIDDLE){
                                e.getWhoClicked().getInventory().addItem(shapedRecipe.getResult());
                            } else {
                                currentRecipe = shapedRecipe.clone();
                                oldRecipe = shapedRecipe.clone();
                                view = View.CREATE_RECIPE;
                                requireExactMeta = shapedRecipe.isUseMetadata();
                                shapeless = shapedRecipe.isShapeless();
                                allowMaterialVariations = shapedRecipe.isAllowIngredientVariations();
                                requireCustomTools = shapedRecipe.isRequireCustomTools();
                                tinkerFirstItem = shapedRecipe.isTinkerFirstItem();
                                exactItems = shapedRecipe.getExactItems();
                                unlockedForEveryone = shapedRecipe.isUnlockedForEveryone();
                                result = shapedRecipe.getResult();
                                setMenuItems();
                                return;
                            }
                        }
                    }
                }
            }
        }

        if (view == View.CREATE_RECIPE){
            if (!(e.getClickedInventory() instanceof PlayerInventory)){
                e.setCancelled(true);
                if (!Utils.isItemEmptyOrNull(e.getCursor())){
                    if (e.getSlot() == 22){
                        if (!tinkerFirstItem){
                            if (currentRecipe != null){
                                if (e.getClick() == ClickType.MIDDLE){
                                    e.getWhoClicked().getInventory().addItem(result);
                                } else {
                                    result = e.getCursor().clone();
                                }
                            }
                        }
                    } else if (grid3x3.contains(e.getSlot())){
                        ItemStack itemToPut = e.getCursor().clone();
                        if (!Utils.isItemEmptyOrNull(itemToPut)){
                            itemToPut.setAmount(1);
                            exactItems.put(grid3x3.indexOf(e.getSlot()), itemToPut.clone());
                        } else {
                            if (e.getClick() == ClickType.MIDDLE && !Utils.isItemEmptyOrNull(exactItems.get(grid3x3.indexOf(e.getSlot())))){
                                e.getWhoClicked().getInventory().addItem(exactItems.get(grid3x3.indexOf(e.getSlot())));
                            }
                        }
                    }
                } else {
                    if (grid3x3.contains(e.getSlot())){
                        if (e.getClick() == ClickType.MIDDLE && !Utils.isItemEmptyOrNull(exactItems.get(grid3x3.indexOf(e.getSlot())))){
                            e.getWhoClicked().getInventory().addItem(exactItems.get(grid3x3.indexOf(e.getSlot())));
                        } else {
                            exactItems.remove(grid3x3.indexOf(e.getSlot()));
                        }
                    }
                }
            } else {
                e.setCancelled(false);
            }
        }
        setMenuItems();
    }

    @Override
    public void handleMenu(InventoryDragEvent e) {
        if (view == View.CREATE_RECIPE){
            if (!(e.getInventory() instanceof PlayerInventory)){
                e.setCancelled(true);
                if (!Utils.isItemEmptyOrNull(e.getCursor())){
                    for (Integer slot : e.getRawSlots()){
                        if (grid3x3.contains(slot)){
                            ItemStack itemToPut = e.getCursor().clone();
                            if (!Utils.isItemEmptyOrNull(itemToPut)){
                                itemToPut.setAmount(1);
                                exactItems.put(grid3x3.indexOf(slot), itemToPut.clone());
                            }
                        }
                    }
                }
            } else {
                e.setCancelled(false);
            }
        }
        setMenuItems();
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        for (int i = 0; i < getSlots(); i++){
            inventory.setItem(i, Utils.createItemStack(Material.GRAY_STAINED_GLASS_PANE, Utils.chat("&8 "), null));
        }
        switch (view){
            case VIEW_RECIPES: setShapedRecipesView();
                return;
            case CREATE_RECIPE: setCreateShapedRecipeView();
        }
    }

    private void setCreateShapedRecipeView(){
        if (currentRecipe != null){
            requireExactMeta = currentRecipe.isUseMetadata();
            allowMaterialVariations = currentRecipe.isAllowIngredientVariations();
            requireCustomTools = currentRecipe.isRequireCustomTools();
            currentModifiers = new ArrayList<>(currentRecipe.getItemModifiers());
            unlockedForEveryone = currentRecipe.isUnlockedForEveryone();
            shapeless = currentRecipe.isShapeless();
            toolIDRequired = currentRecipe.getRequiredToolId();
            toolRequiredType = currentRecipe.getToolRequirementType();
        }

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

        ItemMeta requireExactMetaButtonMeta = requireExactMetaButton.getItemMeta();
        assert requireExactMetaButtonMeta != null;
        requireExactMetaButtonMeta.setDisplayName(Utils.chat("&7Require exact item meta: &e" + ((requireExactMeta) ? "Yes" : "No")));
        requireExactMetaButton.setItemMeta(requireExactMetaButtonMeta);

        ItemMeta allowMaterialVariationsButtonMeta = allowMaterialVariationsButton.getItemMeta();
        assert allowMaterialVariationsButtonMeta != null;
        allowMaterialVariationsButtonMeta.setDisplayName(Utils.chat("&7&lAllow Ingredient Type Variations: &e" + ((allowMaterialVariations) ? "Yes" : "No")));
        allowMaterialVariationsButton.setItemMeta(allowMaterialVariationsButtonMeta);

        ItemMeta requireCustomToolsButtonMeta = requireCustomToolsButton.getItemMeta();
        assert requireCustomToolsButtonMeta != null;
        requireCustomToolsButtonMeta.setDisplayName(Utils.chat("&7Require Custom Tools: &e" + ((requireCustomTools) ? "Yes" : "No")));
        requireCustomToolsButton.setItemMeta(requireCustomToolsButtonMeta);

        ItemMeta centerTinkerButtonMeta = tinkerFirstItemButton.getItemMeta();
        assert centerTinkerButtonMeta != null;
        centerTinkerButtonMeta.setDisplayName(Utils.chat("&7Tinker First Tool: &e" + ((tinkerFirstItem) ? "Yes" : "No")));
        tinkerFirstItemButton.setItemMeta(centerTinkerButtonMeta);

        ItemMeta unlockedForEveryoneMeta = unlockedForEveryoneButton.getItemMeta();
        assert unlockedForEveryoneMeta != null;
        unlockedForEveryoneMeta.setDisplayName(Utils.chat("&7Unlocked for everyone: &e" + ((unlockedForEveryone) ? "Yes" : "No")));
        unlockedForEveryoneButton.setItemMeta(unlockedForEveryoneMeta);

        ItemMeta shapelessMeta = shapelessRecipeButton.getItemMeta();
        assert shapelessMeta != null;
        shapelessMeta.setDisplayName(Utils.chat("&7Shapeless: &e" + ((shapeless) ? "Yes" : "No")));
        shapelessRecipeButton.setItemMeta(shapelessMeta);


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


        // 10,11,12, 19,20,21, 28,29,30
        for (Integer i : grid3x3) {
            ItemStack item = exactItems.get(grid3x3.indexOf(i));
            if (item != null) item.setAmount(1);
            inventory.setItem(i, item);
        }

        inventory.setItem(6, toolIDRequiredButton);
        inventory.setItem(7, toolRequiredTypeButton);
        if (!tinkerFirstItem) inventory.setItem(22, result);
        inventory.setItem(14, unlockedForEveryoneButton);
        inventory.setItem(40, shapelessRecipeButton);
        inventory.setItem(24, dynamicModifierButton);
        inventory.setItem(25, requireExactMetaButton);
        inventory.setItem(26, allowMaterialVariationsButton);
        inventory.setItem(33, requireCustomToolsButton);
        inventory.setItem(34, tinkerFirstItemButton);

        inventory.setItem(45, cancelButton);
        inventory.setItem(53, confirmButton);
    }

    private void setShapedRecipesView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> totalRecipeButtons = new ArrayList<>();

        for (DynamicCraftingTableRecipe recipe : CustomRecipeManager.getInstance().getShapedRecipes().values()){
            ItemStack resultButton = null;
            boolean wrong = false;
            if (recipe.isTinkerFirstItem()){
                for (ItemStack ingredient : recipe.getExactItems().values()){
                    if (EquipmentClass.getClass(ingredient) != null) {
                        resultButton = new ItemStack(ingredient);
                        break;
                    }
                }
                if (resultButton == null) {
                    resultButton = new ItemStack(Material.BARRIER);
                    wrong = true;
                }
            } else {
                resultButton = new ItemStack(recipe.getResult());
            }
            ItemMeta resultMeta = resultButton.getItemMeta();
            assert resultMeta != null;
            resultMeta.setDisplayName(recipe.getName());
            List<String> resultLore = new ArrayList<>();
            if (wrong) {
                resultLore.add(Utils.chat("&4! &cThis recipe is intended to tinker,"));
                resultLore.add(Utils.chat("&cbut no ingredient can be tinkered with."));
                resultLore.add(Utils.chat("&cRecipe may not work under usual circumstances."));
            }
            if (recipe.isShapeless()){
                // TODO
                Map<String, Integer> contents = ItemUtils.getItemTotals(recipe.getExactItems().values());
                List<ItemStack> listedIngredients = new ArrayList<>(recipe.getExactItems().values());
                for (int i = 0; i < contents.size(); i++){
                    ItemStack item = listedIngredients.get(i);
                    resultLore.add(Utils.chat("&e" + contents.getOrDefault(item.toString(), 0) + "&7x &e" + Utils.getItemName(item)));
                }
            } else {
                DynamicCraftingTableRecipe.ShapeDetails details = recipe.getRecipeShapeStrings();
                for (String shapeLine : details.getShape()){
                    resultLore.add(Utils.chat("&7[&e" + shapeLine + "&7]&7"));
                }
                for (Character c : details.getItems().keySet()){
                    if (details.getItems().get(c) == null) continue;
                    resultLore.add(Utils.chat("&e" + c + "&7: &e" + Utils.getItemName(details.getItems().get(c))));
                }
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
        currentRecipe.setModifiers(modifiers);
    }

    private enum View{
        VIEW_RECIPES,
        CREATE_RECIPE
    }

    private DynamicCraftingTableRecipe generateRecipe(){
        ItemStack result = (tinkerFirstItem) ? currentRecipe.getFirstEquipmentFromMatrix() : this.result;
        if (this.exactItems.size() == 0) {
            return null;
        }

        return new DynamicCraftingTableRecipe(currentRecipe.getName(), result, exactItems, unlockedForEveryone, requireExactMeta, allowMaterialVariations, requireCustomTools, tinkerFirstItem, shapeless, currentModifiers);
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
        if (currentRecipe != null){
            currentRecipe.setRequiredToolId(toolIDRequired);
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
        if (currentRecipe != null){
            currentRecipe.setToolRequirementType(toolRequiredType);
        }
    }
}
