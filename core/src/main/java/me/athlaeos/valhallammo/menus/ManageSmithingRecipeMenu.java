package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicSmithingTableRecipe;
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

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ManageSmithingRecipeMenu extends Menu implements AdvancedCraftingManagerMenu{
    private View view = View.VIEW_RECIPES;
    private final NamespacedKey buttonNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "button_recipe_name");
    private DynamicSmithingTableRecipe currentRecipe = null;
    private DynamicSmithingTableRecipe oldRecipe = null;

    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;
    private int currentPage = 0;

    private final ItemStack confirmButton = Utils.createItemStack(Material.STRUCTURE_VOID,
            Utils.chat("&b&lSave"),
            null);
    private final ItemStack cancelButton = Utils.createItemStack(Material.BARRIER,
            Utils.chat("&cDelete"),
            null);

    private final ItemStack resultModifierButton = Utils.createItemStack(
            Material.BOOK,
            Utils.chat("&b&lResult Dynamic Modifiers"),
            Utils.separateStringIntoLines(Utils.chat("&7Modifiers to be executed on the resulting item"), 40)
    );
    private final ItemStack additionModifierButton = Utils.createItemStack(
            Material.BOOK,
            Utils.chat("&b&lAddition Dynamic Modifiers"),
            Utils.separateStringIntoLines(Utils.chat("&7Modifiers to be executed on the addition (2nd) item if it is not consumed"), 40)
    );
    private final ItemStack baseExactMetaButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lRequire Precise Meta on Base"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, the base item placed in the first smithing table slot will need " +
                    "to exactly match this item. Tools/equipment are excluded from this rule. -n" +
                    "If false, only the item types will need to match. Meta is ignored."), 40)
    );
    private final ItemStack additionExactMetaButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lRequire Precise Meta on Addition"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, the addition item placed in the first smithing table slot will need " +
                    "to exactly match this item. Tools/equipment are excluded from this rule. -n" +
                    "If false, only the item types will need to match. Meta is ignored."), 40)
    );
    private final ItemStack allowBaseVariationsButton = Utils.createItemStack(
            Material.OAK_PLANKS,
            Utils.chat("&7&lAllow Ingredient Type Variations on Base"),
            Utils.separateStringIntoLines(Utils.chat("&cIgnored if 'Require Precise Meta on Base' is enabled. -n" +
                    "&7If true, during crafting the base item used in the smithing table " +
                    "may also match some similar item types, like oak planks and birch planks, or cobblestone and blackstone. -n" +
                    "If false, these variations are not allowed. If oak planks are inserted, only oak planks can be used"), 40)
    );
    private final ItemStack allowAdditionVariationsButton = Utils.createItemStack(
            Material.OAK_PLANKS,
            Utils.chat("&7&lAllow Ingredient Type Variations on Addition"),
            Utils.separateStringIntoLines(Utils.chat("&cIgnored if 'Require Precise Meta on Addition' is enabled. -n" +
                    "&7If true, during crafting the base item used in the smithing table " +
                    "may also match some similar item types, like oak planks and birch planks, or cobblestone and blackstone. -n" +
                    "If false, these variations are not allowed. If oak planks are inserted, only oak planks can be used"), 40)
    );
    private final ItemStack requireCustomToolsButton = Utils.createItemStack(
            Material.CRAFTING_TABLE,
            Utils.chat("&7&lRequire Custom Tools"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, if a recipe requires tools/armor, they need" +
                    " to have ValhallaMMO custom characteristics. Such as quality, durability, or custom attributes." +
                    " If 'Require Precise Meta' is also enabled, these tools/armor pieces will be ignored and do not" +
                    " need to exactly match.-n" +
                    " If false, any tool/armor piece can be used even if they have no custom traits.-n"), 40)
    );
    private final ItemStack tinkerBaseButton = Utils.createItemStack(
            Material.ANVIL,
            Utils.chat("&7&lTinker Base"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, instead of creating a new item all-together " +
                    "the base item is also used as output. This is the " +
                    "vanilla smithing table alternative for ValhallaMMO's custom tinkering mechanic. "), 40)
    );
    private final ItemStack consumeAdditionButton = Utils.createItemStack(
            Material.NETHERITE_INGOT,
            Utils.chat("&7&lConsume Addition"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, the ingredient will be consumed when the recipe is complete. -n" +
                    "If this is the case, the addition ingredient will not be improved with dynamic item modifiers. -n" +
                    "If false, the ingredient will remain in the smithing table and can also accept dynamic item modifiers."), 40)
    );
    private final ItemStack returnToMenuButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lReturn to menu"),
            new ArrayList<>()
    );

    private final ItemStack unlockedForEveryoneButton = Utils.createItemStack(
            Material.ARMOR_STAND,
            Utils.chat("&7&lUnlocked for everyone:"),
            new ArrayList<>()
    );

    private boolean baseExactMeta = false;
    private boolean additionExactMeta = false;
    private boolean requireCustomTools = true;
    private boolean tinkerBase = false;
    private boolean consumeAddition = true;
    private boolean allowBaseVariations = false;
    private boolean allowAdditionVariations = false;
    private boolean unlockedForEveryone = false;
    private ItemStack base = new ItemStack(Material.DIAMOND_PICKAXE);
    private ItemStack addition = new ItemStack(Material.NETHERITE_INGOT);
    private ItemStack result = new ItemStack(Material.NETHERITE_PICKAXE);
    private List<DynamicItemModifier> resultModifiers = new ArrayList<>();
    private List<DynamicItemModifier> additionModifiers = new ArrayList<>();

    public ManageSmithingRecipeMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
    }

    public ManageSmithingRecipeMenu(PlayerMenuUtility playerMenuUtility, DynamicSmithingTableRecipe recipe){
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        if (recipe != null){
            currentRecipe = recipe.clone();
            oldRecipe = recipe.clone();
            view = View.CREATE_RECIPE;
            baseExactMeta = recipe.isUseMetaBase();
            additionExactMeta = recipe.isUseMetaAddition();
            requireCustomTools = recipe.requireCustomTools();
            tinkerBase = recipe.isTinkerBase();
            consumeAddition = recipe.isConsumeAddition();
            unlockedForEveryone = recipe.isUnlockedForEveryone();
            allowBaseVariations = recipe.isAllowBaseVariations();
            allowAdditionVariations = recipe.isAllowAdditionVariations();
            //resultModifiers = recipe.getModifiersResult();
            //additionModifiers = recipe.getModifiersAddition();
            base = recipe.getBase();
            addition = recipe.getAddition();
            result = recipe.getResult();
        }
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7Create New Smithing Recipe");
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
            } else if (clickedItem.equals(nextPageButton)){
                currentPage++;
            } else if (clickedItem.equals(previousPageButton)){
                currentPage--;
            } else if (clickedItem.equals(resultModifierButton)){
                playerMenuUtility.setPreviousMenu(this);
                new DynamicModifierMenu(playerMenuUtility, resultModifiers, false, true).open();
                return;
            } else if (clickedItem.equals(additionModifierButton)){
                playerMenuUtility.setPreviousMenu(this);
                new DynamicModifierMenu(playerMenuUtility, additionModifiers, true, true).open();
                return;
            } else if (clickedItem.equals(baseExactMetaButton)){
                baseExactMeta = !baseExactMeta;
                if (currentRecipe != null){
                    currentRecipe.setUseMetaBase(baseExactMeta);
                }
            } else if (clickedItem.equals(additionExactMetaButton)){
                additionExactMeta = !additionExactMeta;
                if (currentRecipe != null){
                    currentRecipe.setUseMetaAddition(additionExactMeta);
                }
            } else if (clickedItem.equals(requireCustomToolsButton)){
                requireCustomTools = !requireCustomTools;
                if (currentRecipe != null){
                    currentRecipe.setRequireCustomTools(requireCustomTools);
                }
            } else if (clickedItem.equals(allowBaseVariationsButton)){
                allowBaseVariations = !allowBaseVariations;
                if (currentRecipe != null){
                    currentRecipe.setAllowBaseVariations(allowBaseVariations);
                }
            } else if (clickedItem.equals(allowAdditionVariationsButton)){
                allowAdditionVariations = !allowAdditionVariations;
                if (currentRecipe != null){
                    currentRecipe.setAllowAdditionVariations(allowAdditionVariations);
                }
            } else if (clickedItem.equals(tinkerBaseButton)){
                tinkerBase = !tinkerBase;
                if (currentRecipe != null){
                    currentRecipe.setTinkerBase(tinkerBase);
                }
            } else if (clickedItem.equals(consumeAdditionButton)){
                consumeAddition = !consumeAddition;
                if (currentRecipe != null){
                    currentRecipe.setConsumeAddition(consumeAddition);
                }
            } else if (clickedItem.equals(confirmButton)){
                if (view == View.CREATE_RECIPE) {
                    if (currentRecipe != null) {
                        if (Utils.isItemEmptyOrNull(base) || Utils.isItemEmptyOrNull(addition) || Utils.isItemEmptyOrNull(result)){
                            e.getWhoClicked().sendMessage(Utils.chat("&cPlease add a base, an addition, and result"));
                        } else {
                            currentRecipe = new DynamicSmithingTableRecipe(currentRecipe.getName(), result, base, addition, baseExactMeta, additionExactMeta, requireCustomTools, tinkerBase, consumeAddition, allowBaseVariations, allowAdditionVariations, resultModifiers, additionModifiers);
                            currentRecipe.setUnlockedForEveryone(unlockedForEveryone);
                            CustomRecipeManager.getInstance().update(oldRecipe, currentRecipe);
                            currentRecipe = null;
                            view = View.VIEW_RECIPES;
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
                        DynamicSmithingTableRecipe smithingRecipe = CustomRecipeManager.getInstance().getDynamicSmithingRecipe(
                                clickedItem.getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING)
                        );
                        if (smithingRecipe != null){
                            if (e.getClick() == ClickType.MIDDLE){
                                e.getWhoClicked().getInventory().addItem(smithingRecipe.getResult());
                            } else {
                                currentRecipe = smithingRecipe.clone();
                                oldRecipe = smithingRecipe.clone();
                                view = View.CREATE_RECIPE;
                                baseExactMeta = smithingRecipe.isUseMetaBase();
                                additionExactMeta = smithingRecipe.isUseMetaAddition();
                                requireCustomTools = smithingRecipe.requireCustomTools();
                                tinkerBase = smithingRecipe.isTinkerBase();
                                consumeAddition = smithingRecipe.isConsumeAddition();
                                allowBaseVariations = smithingRecipe.isAllowBaseVariations();
                                allowAdditionVariations = smithingRecipe.isAllowAdditionVariations();
                                base = smithingRecipe.getBase();
                                addition = smithingRecipe.getAddition();
                                result = smithingRecipe.getResult();
                                unlockedForEveryone = smithingRecipe.isUnlockedForEveryone();
                                resultModifiers = smithingRecipe.getModifiersResult();
                                additionModifiers = smithingRecipe.getModifiersAddition();
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
                    if (e.getSlot() == 20){
                        if (currentRecipe != null){
                            if (e.getClick() == ClickType.MIDDLE){
                                e.getWhoClicked().getInventory().addItem(base);
                            } else {
                                base = e.getCursor().clone();
                                base.setAmount(1);
                            }
                        }
                    } else if (e.getSlot() == 22){
                        if (currentRecipe != null){
                            if (e.getClick() == ClickType.MIDDLE){
                                e.getWhoClicked().getInventory().addItem(addition);
                            } else {
                                addition = e.getCursor().clone();
                                addition.setAmount(1);
                            }
                        }
                    } else if (e.getSlot() == 24){
                        if (!tinkerBase){
                            if (currentRecipe != null){
                                if (e.getClick() == ClickType.MIDDLE){
                                    e.getWhoClicked().getInventory().addItem(result);
                                } else {
                                    result = e.getCursor().clone();
                                }
                            }
                        }
                    }
                } else if (e.getClick() == ClickType.MIDDLE && currentRecipe != null) {
                    if (e.getSlot() == 20){
                        e.getWhoClicked().getInventory().addItem(base);
                    } else if (e.getSlot() == 22){
                        e.getWhoClicked().getInventory().addItem(addition);
                    } else if (e.getSlot() == 24){
                        if (!tinkerBase){
                            e.getWhoClicked().getInventory().addItem(result);
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
                        if (slot == 20){
                            base = e.getCursor().clone();
                            base.setAmount(1);
                        } else if (slot == 22){
                            addition = e.getCursor().clone();
                            addition.setAmount(1);
                        } else if (slot == 24){
                            if (!tinkerBase) {
                                result = e.getCursor().clone();
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
            case VIEW_RECIPES: setRecipesView();
                return;
            case CREATE_RECIPE: setCreateShapedRecipeView();
        }
    }

    private void setCreateShapedRecipeView(){
        if (currentRecipe != null){
            baseExactMeta = currentRecipe.isUseMetaBase();
            additionExactMeta = currentRecipe.isUseMetaAddition();
            requireCustomTools = currentRecipe.requireCustomTools();
            resultModifiers = new ArrayList<>(currentRecipe.getModifiersResult());
            additionModifiers = new ArrayList<>(currentRecipe.getModifiersAddition());
            unlockedForEveryone = currentRecipe.isUnlockedForEveryone();
        }

        List<String> resultModifierButtonLore = new ArrayList<>(Utils.separateStringIntoLines(Utils.chat("&7Modifiers to be executed on the resulting item"), 40));
        List<DynamicItemModifier> resultModifiers = new ArrayList<>(this.resultModifiers);
        DynamicItemModifier.sortModifiers(resultModifiers);
        for (DynamicItemModifier modifier : resultModifiers){
            resultModifierButtonLore.addAll(Utils.separateStringIntoLines(Utils.chat("&7- " + modifier.toString()), 40));
        }
        ItemMeta resultModifierButtonMeta = resultModifierButton.getItemMeta();
        assert resultModifierButtonMeta != null;
        resultModifierButtonMeta.setLore(resultModifierButtonLore);
        resultModifierButton.setItemMeta(resultModifierButtonMeta);


        List<String> additionModifierButtonLore = new ArrayList<>(Utils.separateStringIntoLines(Utils.chat("&7Modifiers to be executed on the addition (2nd) item if it is not consumed"), 40));
        List<DynamicItemModifier> additionModifiers = new ArrayList<>(this.additionModifiers);
        DynamicItemModifier.sortModifiers(additionModifiers);
        for (DynamicItemModifier modifier : additionModifiers){
            additionModifierButtonLore.addAll(Utils.separateStringIntoLines(Utils.chat("&7- " + modifier.toString()), 40));
        }
        ItemMeta additionModifierButtonMeta = additionModifierButton.getItemMeta();
        assert additionModifierButtonMeta != null;
        additionModifierButtonMeta.setLore(additionModifierButtonLore);
        additionModifierButton.setItemMeta(additionModifierButtonMeta);


        ItemMeta requireExactBaseMetaButtonMeta = baseExactMetaButton.getItemMeta();
        assert requireExactBaseMetaButtonMeta != null;
        requireExactBaseMetaButtonMeta.setDisplayName(Utils.chat("&7Require exact base item meta: &e" + ((baseExactMeta) ? "Yes" : "No")));
        baseExactMetaButton.setItemMeta(requireExactBaseMetaButtonMeta);

        ItemMeta requireExactAdditionMetaButtonMeta = additionExactMetaButton.getItemMeta();
        assert requireExactAdditionMetaButtonMeta != null;
        requireExactAdditionMetaButtonMeta.setDisplayName(Utils.chat("&7Require exact addition item meta: &e" + ((additionExactMeta) ? "Yes" : "No")));
        additionExactMetaButton.setItemMeta(requireExactAdditionMetaButtonMeta);

        ItemMeta requireCustomToolButtonMeta = requireCustomToolsButton.getItemMeta();
        assert requireCustomToolButtonMeta != null;
        requireCustomToolButtonMeta.setDisplayName(Utils.chat("&7Require Custom Tool: &e" + ((requireCustomTools) ? "Yes" : "No")));
        requireCustomToolsButton.setItemMeta(requireCustomToolButtonMeta);

        ItemMeta tinkerBaseButtonMeta = tinkerBaseButton.getItemMeta();
        assert tinkerBaseButtonMeta != null;
        tinkerBaseButtonMeta.setDisplayName(Utils.chat("&7Tinker Item: &e" + ((tinkerBase) ? "Yes" : "No")));
        tinkerBaseButton.setItemMeta(tinkerBaseButtonMeta);

        ItemMeta consumeAdditionButtonMeta = consumeAdditionButton.getItemMeta();
        assert consumeAdditionButtonMeta != null;
        consumeAdditionButtonMeta.setDisplayName(Utils.chat("&7Consume Addition: &e" + ((consumeAddition) ? "Yes" : "No")));
        consumeAdditionButton.setItemMeta(consumeAdditionButtonMeta);

        ItemMeta unlockedForEveryoneMeta = unlockedForEveryoneButton.getItemMeta();
        assert unlockedForEveryoneMeta != null;
        unlockedForEveryoneMeta.setDisplayName(Utils.chat("&7Unlocked for everyone: &e" + ((unlockedForEveryone) ? "Yes" : "No")));
        unlockedForEveryoneButton.setItemMeta(unlockedForEveryoneMeta);

        ItemMeta allowBaseVariationsMeta = allowBaseVariationsButton.getItemMeta();
        assert allowBaseVariationsMeta != null;
        allowBaseVariationsMeta.setDisplayName(Utils.chat("&7Allow Base Variations: &e" + ((allowBaseVariations) ? "Yes" : "No")));
        allowBaseVariationsButton.setItemMeta(allowBaseVariationsMeta);

        ItemMeta allowAdditionVariationMeta = allowAdditionVariationsButton.getItemMeta();
        assert allowAdditionVariationMeta != null;
        allowAdditionVariationMeta.setDisplayName(Utils.chat("&7Allow Addition Variations: &e" + ((allowAdditionVariations) ? "Yes" : "No")));
        allowAdditionVariationsButton.setItemMeta(allowAdditionVariationMeta);

        inventory.setItem(20, base);
        inventory.setItem(22, addition);
        if (!tinkerBase) inventory.setItem(24, result);

        if (!consumeAddition) inventory.setItem(31, additionModifierButton);
        inventory.setItem(33, resultModifierButton);

        inventory.setItem(11, baseExactMetaButton);
        inventory.setItem(13, additionExactMetaButton);

        inventory.setItem(2, allowBaseVariationsButton);
        inventory.setItem(4, allowAdditionVariationsButton);

        inventory.setItem(19, requireCustomToolsButton);
        inventory.setItem(16, unlockedForEveryoneButton);

        inventory.setItem(40, consumeAdditionButton);
        inventory.setItem(42, tinkerBaseButton);

        inventory.setItem(45, cancelButton);
        inventory.setItem(53, confirmButton);
    }

    private void setRecipesView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> totalRecipeButtons = new ArrayList<>();

        for (DynamicSmithingTableRecipe recipe : CustomRecipeManager.getInstance().getSmithingRecipes().values()){
            ItemStack resultButton = (recipe.isTinkerBase() ? new ItemStack(recipe.getBase()) : new ItemStack(recipe.getResult()));
            ItemMeta resultMeta = resultButton.getItemMeta();
            assert resultMeta != null;
            resultMeta.setDisplayName(recipe.getName());
            List<String> resultLore = new ArrayList<>();
            resultLore.add(Utils.chat(String.format("&f%s&r&f + %s&r&f >>> %s", Utils.getItemName(recipe.getBase()), Utils.getItemName(recipe.getAddition()), recipe.isTinkerBase() ? "&dUpgraded " + Utils.getItemName(recipe.getBase()) : Utils.getItemName(recipe.getResult()))));

            resultLore.add(Utils.chat("&8&m                                      "));
            List<DynamicItemModifier> resultModifiers = new ArrayList<>(recipe.getModifiersResult());
            DynamicItemModifier.sortModifiers(resultModifiers);
            for (DynamicItemModifier modifier : resultModifiers){
                resultLore.addAll(Utils.separateStringIntoLines(Utils.chat("&7- " + modifier.toString()), 40));
            }
            if (resultMeta.getLore() != null){
                resultLore.addAll(resultMeta.getLore());
            }

            if (!recipe.isConsumeAddition() && !recipe.getModifiersAddition().isEmpty()){
                resultLore.add(Utils.chat("&8&m                                      "));
                resultLore.add(Utils.chat("&fThe addition item is not consumed"));
                resultLore.add(Utils.chat("&fand receives the following modifiers:"));
                List<DynamicItemModifier> additionModifiers = new ArrayList<>(recipe.getModifiersAddition());
                DynamicItemModifier.sortModifiers(additionModifiers);
                for (DynamicItemModifier modifier : additionModifiers){
                    resultLore.addAll(Utils.separateStringIntoLines(Utils.chat("&7- " + modifier.toString()), 40));
                }
                if (resultMeta.getLore() != null){
                    resultLore.addAll(resultMeta.getLore());
                }
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
        this.resultModifiers = modifiers;
        currentRecipe.setModifiersResult(modifiers);
    }
    @Override
    public void setSecondaryModifiers(List<DynamicItemModifier> modifiers) {
        this.additionModifiers = modifiers;
        currentRecipe.setModifiersAddition(modifiers);
    }

    private enum View{
        VIEW_RECIPES,
        CREATE_RECIPE
    }
}
