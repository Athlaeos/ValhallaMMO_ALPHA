package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.DynamicItemModifierManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicBrewingRecipe;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ManageBrewingRecipeMenu extends Menu implements CraftingManagerMenu{
    private View view = View.VIEW_RECIPES;
    private final NamespacedKey buttonNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "button_recipe_name");
    private DynamicBrewingRecipe currentRecipe = null;
    private DynamicBrewingRecipe oldRecipe = null;

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
    private final ItemStack requireExactMetaButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lRequire Precise Meta"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, during crafting the items used in the crafting table " +
                    "will need to have item metas exactly matching the items given in the recipe. Recommended if you're" +
                    " using custom materials. -n" +
                    "If false, only the item types will need to match. Meta is ignored."), 40)
    );
    private final ItemStack returnToMenuButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lReturn to menu"),
            new ArrayList<>()
    );

    private ItemStack ingredient = null;
    private Material applyOn = Material.GLASS_BOTTLE;
    private boolean requireExactMeta = false;
    private Collection<DynamicItemModifier> currentModifiers = new HashSet<>();

    public ManageBrewingRecipeMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);

        DynamicItemModifier modifier = DynamicItemModifierManager.getInstance().createModifier("exp_bonus_alchemy", 100, ModifierPriority.LAST);
        if (modifier != null){
            currentModifiers.add(modifier);
        }
    }

    public ManageBrewingRecipeMenu(PlayerMenuUtility playerMenuUtility, DynamicBrewingRecipe recipe){
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        if (recipe != null){
            currentRecipe = recipe.clone();
            oldRecipe = recipe.clone();
            view = View.CREATE_RECIPE;
            ingredient = recipe.getIngredient();
            applyOn = recipe.getRequiredType();
            requireExactMeta = recipe.isPerfectMeta();
        } else {
            DynamicItemModifier modifier = DynamicItemModifierManager.getInstance().createModifier("exp_bonus_alchemy", 100, ModifierPriority.LAST);
            if (modifier != null){
                currentModifiers.add(modifier);
            }
        }
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7Create New Brewing Recipe");
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
            } else if (clickedItem.equals(nextPageButton)){
                currentPage++;
            } else if (clickedItem.equals(previousPageButton)){
                currentPage--;
            } else if (clickedItem.equals(dynamicModifierButton)){
                playerMenuUtility.setPreviousMenu(this);
                new DynamicModifierMenu(playerMenuUtility, currentModifiers).open();
                return;
            } else if (clickedItem.equals(requireExactMetaButton)){
                requireExactMeta = !requireExactMeta;
                if (currentRecipe != null){
                    currentRecipe.setPerfectMeta(requireExactMeta);
                }
            } else if (clickedItem.equals(confirmButton)){
                if (view == View.CREATE_RECIPE) {
                    if (currentRecipe != null) {
                        DynamicBrewingRecipe backupRecipe = currentRecipe.clone();
                        currentRecipe = new DynamicBrewingRecipe(backupRecipe.getName(), ingredient, applyOn, requireExactMeta, currentModifiers);
                        if (currentRecipe.getIngredient() != null && currentRecipe.getRequiredType() != null) {
                            CustomRecipeManager.getInstance().update(oldRecipe, currentRecipe);
                            currentRecipe = null;
                            view = View.VIEW_RECIPES;
                        } else {
                            e.getWhoClicked().sendMessage(Utils.chat("&cPlease add an ingredient and base material"));
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
                        DynamicBrewingRecipe brewingRecipe = CustomRecipeManager.getInstance().getBrewingRecipe(
                                clickedItem.getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING));
                        currentRecipe = brewingRecipe.clone();
                        oldRecipe = brewingRecipe.clone();
                        view = View.CREATE_RECIPE;
                        requireExactMeta = brewingRecipe.isPerfectMeta();
                        ingredient = brewingRecipe.getIngredient();
                        applyOn = brewingRecipe.getRequiredType();
                    }
                }
            }
        }

        if (view == View.CREATE_RECIPE){
            if (!(e.getClickedInventory() instanceof PlayerInventory)){
                e.setCancelled(true);
                if (!Utils.isItemEmptyOrNull(e.getCursor())){
                    if (e.getSlot() == 13){
                        if (!Utils.isItemEmptyOrNull(e.getCursor())){
                            ingredient = e.getCursor().clone();
                            ingredient.setAmount(1);
                            currentRecipe.setIngredient(ingredient);
                        }
                    } else if (e.getSlot() == 38 || e.getSlot() == 40 || e.getSlot() == 42){
                        if (!Utils.isItemEmptyOrNull(e.getCursor())){
                            applyOn = e.getCursor().getType();
                            currentRecipe.setApplyOn(applyOn);
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
                    for (int i : e.getRawSlots()){
                        if (i == 11){
                            if (!Utils.isItemEmptyOrNull(e.getCursor())){
                                ingredient = e.getCursor().clone();
                            }
                        } else if (i == 37 || i == 38 || i == 49){
                            if (!Utils.isItemEmptyOrNull(e.getCursor())){
                                applyOn = e.getCursor().getType();
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
            case VIEW_RECIPES: setBrewingRecipesView();
                return;
            case CREATE_RECIPE: setCreateBrewingRecipeView();
        }
    }

    private void setCreateBrewingRecipeView(){
        if (currentRecipe != null){
            requireExactMeta = currentRecipe.isPerfectMeta();
            ingredient = currentRecipe.getIngredient();
            applyOn = currentRecipe.getRequiredType();
            currentModifiers = new HashSet<>(currentRecipe.getItemModifiers());
        }

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

        ItemMeta requireExactMetaButtonMeta = requireExactMetaButton.getItemMeta();
        assert requireExactMetaButtonMeta != null;
        requireExactMetaButtonMeta.setDisplayName(Utils.chat("&7Require exact item meta: &e" + ((requireExactMeta) ? "Yes" : "No")));
        requireExactMetaButton.setItemMeta(requireExactMetaButtonMeta);

        ItemStack requiredTypeButton;
        if (applyOn != null) {
            requiredTypeButton = new ItemStack(applyOn);
        } else {
            requiredTypeButton = Utils.createItemStack(Material.GLASS_BOTTLE, Utils.chat("&7Apply on"), null);
        }

        if (Utils.isItemEmptyOrNull(ingredient)){
            inventory.setItem(13, Utils.createItemStack(Material.NETHER_WART, Utils.chat("&7Ingredient"), null));
        } else {
            inventory.setItem(13, ingredient);
        }
        inventory.setItem(14, requireExactMetaButton);
        inventory.setItem(22, Utils.createItemStack(Material.ORANGE_STAINED_GLASS_PANE, Utils.chat("&8 "), null));
        inventory.setItem(29, Utils.createItemStack(Material.ORANGE_STAINED_GLASS_PANE, Utils.chat("&8 "), null));
        inventory.setItem(30, Utils.createItemStack(Material.ORANGE_STAINED_GLASS_PANE, Utils.chat("&8 "), null));
        inventory.setItem(31, Utils.createItemStack(Material.ORANGE_STAINED_GLASS_PANE, Utils.chat("&8 "), null));
        inventory.setItem(32, Utils.createItemStack(Material.ORANGE_STAINED_GLASS_PANE, Utils.chat("&8 "), null));
        inventory.setItem(33, Utils.createItemStack(Material.ORANGE_STAINED_GLASS_PANE, Utils.chat("&8 "), null));
        inventory.setItem(25, dynamicModifierButton);
        inventory.setItem(38, requiredTypeButton);
        inventory.setItem(40, requiredTypeButton);
        inventory.setItem(42, requiredTypeButton);

        inventory.setItem(45, cancelButton);
        inventory.setItem(53, confirmButton);
    }

    private void setBrewingRecipesView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> totalRecipeButtons = new ArrayList<>();
        for (DynamicBrewingRecipe recipe : CustomRecipeManager.getInstance().getBrewingRecipes().values()){
            ItemStack resultButton = new ItemStack(recipe.getIngredient().getType());
            ItemMeta resultMeta = resultButton.getItemMeta();
            assert resultMeta != null;
            resultMeta.setDisplayName(recipe.getName());
            List<String> resultLore = new ArrayList<>();
            resultLore.add(Utils.chat("&e" + Utils.getItemName(recipe.getIngredient())));
            resultLore.add(Utils.chat("&8&m                                      "));
            List<DynamicItemModifier> modifiers = new ArrayList<>(recipe.getItemModifiers());
            modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
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
    public void setCurrentModifiers(Collection<DynamicItemModifier> modifiers) {
        this.currentModifiers = modifiers;
        currentRecipe.setModifiers(modifiers);
    }

    private enum View{
        VIEW_RECIPES,
        CREATE_RECIPE
    }
}
