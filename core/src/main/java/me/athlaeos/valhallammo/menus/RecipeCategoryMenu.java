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
            Material.SMITHING_TABLE,
            Utils.chat("&7&lCustom Item Improvement Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Improving items by holding right click on a station until the item is improved."), 40));
    private final ItemStack viewShapedRecipesButton = Utils.createItemStack(
            Material.CRAFTING_TABLE,
            Utils.chat("&7&lShaped Crafting Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Crafting items on the crafting table just like any vanilla recipe."), 40));
    private final ItemStack viewCustomClassImproveRecipesButton = Utils.createItemStack(
            Material.SMITHING_TABLE,
            Utils.chat("&7&lCustom Class Improvement Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Improving items by holding right click on a station until the item is improved. " +
                    "The difference between this and regular improvement recipes is that regular improvement recipes require " +
                    "a specific item type, while this one just requires a more generic item class. (e.g. CHESTPLATES, SWORDS, AXES instead of " +
                    "DIAMOND_CHESTPLATE, DIAMOND_SWORD, GOLD_AXE)"), 40));
    private final ItemStack viewBrewingRecipesButton = Utils.createItemStack(
            Material.BREWING_STAND,
            Utils.chat("&7&lBrewing Recipes"),
            Utils.separateStringIntoLines(Utils.chat("&7Improving/altering items by infusing them with an ingredient in the brewing stand"), 40));


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
        inventory.setItem(13, viewCustomCraftRecipesButton);
        inventory.setItem(16, viewCustomImproveRecipesButton);
        inventory.setItem(19, viewBrewingRecipesButton);
        inventory.setItem(25, viewCustomClassImproveRecipesButton);
    }
}
