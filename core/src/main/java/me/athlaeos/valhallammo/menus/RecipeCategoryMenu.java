package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;

public class RecipeCategoryMenu extends Menu{
    private final ItemStack viewCustomCraftRecipesButton = Utils.createItemStack(
            Material.ANVIL,
            Utils.chat("&7&lCustom Item Crafting Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Crafting items by selecting a recipe through a menu and holding right click on a station until the item is crafted."), 40));
    private final ItemStack viewCustomImproveRecipesButton = Utils.createItemStack(
            Material.GRINDSTONE,
            Utils.chat("&7&lCustom Item Improvement Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Improving items by holding right click on a station until the item is improved."), 40));
    private final ItemStack viewCustomSmithingRecipesButton = Utils.createItemStack(
            Material.SMITHING_TABLE,
            Utils.chat("&7&lCustom Smithing Table Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Crafting or improving items using the smithing table. -n" +
                    "Custom smithing table recipes are unique in that it can execute modifiers on two items at once, the " +
                    "base item used and the addition ingredient, and it can also utilize a special type of item modifiers using both of these " +
                    "items. For example: a modifier that transfers enchantments from one item to the other."), 40));
    private final ItemStack viewShapedRecipesButton = Utils.createItemStack(
            Material.CRAFTING_TABLE,
            Utils.chat("&7&lShaped Crafting Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Crafting items on the crafting table just like any vanilla recipe."), 40));
    private final ItemStack viewCauldronRecipesButton = Utils.createItemStack(
            Material.CAULDRON,
            Utils.chat("&7&lCustom Cauldron Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Crafting or improving items by dropping ingredients into it and finishing with a catalyst item. " +
                    "Cauldrons can only store a limited amount of items and are emptied when right clicked or broken"), 40));
    private final ItemStack viewCustomClassImproveRecipesButton = Utils.createItemStack(
            Material.GRINDSTONE,
            Utils.chat("&7&lCustom Class Improvement Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Improving items by holding right click on a station until the item is improved. " +
                    "The difference between this and regular improvement recipes is that regular improvement recipes require " +
                    "a specific item type, while this one just requires a more generic item class. (e.g. CHESTPLATES, SWORDS, AXES instead of " +
                    "DIAMOND_CHESTPLATE, DIAMOND_SWORD, GOLD_AXE)"), 40));
    private final ItemStack viewBrewingRecipesButton = Utils.createItemStack(
            Material.BREWING_STAND,
            Utils.chat("&7&lBrewing Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Improving/altering items by infusing them with an ingredient in the brewing stand"), 40));
    private final ItemStack viewCampfireRecipesButton = Utils.createItemStack(
            Material.CAMPFIRE,
            Utils.chat("&7&lCampfire Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Improving or crafting items by cooking them on a campfire"), 40));
    private final ItemStack viewFurnaceRecipesButton = Utils.createItemStack(
            Material.FURNACE,
            Utils.chat("&7&lFurnace Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Improving or crafting items by cooking them in a furnace"), 40));
    private final ItemStack viewBlastFurnaceRecipesButton = Utils.createItemStack(
            Material.BLAST_FURNACE,
            Utils.chat("&7&lBlast Furnace Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Improving or crafting items by cooking them in a blast furnace"), 40));
    private final ItemStack viewSmokerRecipesButton = Utils.createItemStack(
            Material.SMOKER,
            Utils.chat("&7&lSmoker Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Improving or crafting items by cooking them in a smoker"), 40));

    public RecipeCategoryMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7Pick Recipe Type");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem != null) {
            if (clickedItem.equals(viewCustomCraftRecipesButton)) {
                new ManageCraftRecipeMenu(playerMenuUtility).open();
                return;
            } else if (clickedItem.equals(viewCustomImproveRecipesButton)){
                new ManageTinkerRecipeMenu(playerMenuUtility).open();
                return;
            } else if (clickedItem.equals(viewShapedRecipesButton)){
                new ManageShapedRecipeMenu(playerMenuUtility).open();
                return;
            } else if (clickedItem.equals(viewCampfireRecipesButton)){
                new ManageCookingRecipeMenu(playerMenuUtility, ManageCookingRecipeMenu.RecipeType.CAMPFIRE).open();
                return;
            } else if (clickedItem.equals(viewFurnaceRecipesButton)){
                new ManageCookingRecipeMenu(playerMenuUtility, ManageCookingRecipeMenu.RecipeType.FURNACE).open();
                return;
            } else if (clickedItem.equals(viewCustomSmithingRecipesButton)){
                new ManageSmithingRecipeMenu(playerMenuUtility).open();
                return;
            } else if (clickedItem.equals(viewCauldronRecipesButton)){
                new ManageCauldronRecipeMenu(playerMenuUtility).open();
                return;
            } else if (clickedItem.equals(viewBlastFurnaceRecipesButton)){
                new ManageCookingRecipeMenu(playerMenuUtility, ManageCookingRecipeMenu.RecipeType.BLAST_FURNACE).open();
                return;
            } else if (clickedItem.equals(viewSmokerRecipesButton)){
                new ManageCookingRecipeMenu(playerMenuUtility, ManageCookingRecipeMenu.RecipeType.SMOKER).open();
                return;
            } else if (clickedItem.equals(viewBrewingRecipesButton)){
                new ManageBrewingRecipeMenu(playerMenuUtility).open();
                return;
            } else if (clickedItem.equals(viewCustomClassImproveRecipesButton)){
                new ManageClassTinkerRecipeMenu(playerMenuUtility).open();
                return;
            }
        }
        setMenuItems();
    }

    @Override
    public void handleMenu(InventoryDragEvent e) {

    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        inventory.setItem(10, viewShapedRecipesButton);
        inventory.setItem(19, viewCustomSmithingRecipesButton);
        inventory.setItem(28, viewBrewingRecipesButton);

        inventory.setItem(13, viewFurnaceRecipesButton);
        inventory.setItem(22, viewBlastFurnaceRecipesButton);
        inventory.setItem(31, viewSmokerRecipesButton);
        inventory.setItem(40, viewCampfireRecipesButton);

        inventory.setItem(16, viewCustomCraftRecipesButton);
        inventory.setItem(25, viewCustomImproveRecipesButton);
        inventory.setItem(34, viewCustomClassImproveRecipesButton);
        inventory.setItem(43, viewCauldronRecipesButton);
    }
}
