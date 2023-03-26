package me.athlaeos.valhallammo.menus.temporary;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats.CustomModelDataAddModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicCraftingTableRecipe;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.menus.Menu;
import me.athlaeos.valhallammo.menus.PlayerMenuUtility;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ViewShapedRecipeMenu extends Menu {

    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;
    private int currentPage = 0;

    private final ItemStack returnToMenuButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lReturn to menu"),
            new ArrayList<>()
    );

    public ViewShapedRecipeMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
    }
    public boolean isRecipeUnlocked(Player player, DynamicCraftingTableRecipe recipe){
        if (player.hasPermission("valhalla.allrecipes") || recipe.isUnlockedForEveryone()) return true;
        AccountProfile profile = null;
        Profile p = ProfileManager.getManager().getProfile(player, "ACCOUNT");
        if (p instanceof AccountProfile){
            profile = (AccountProfile) p;
        }
        if (profile != null){
            return profile.getUnlockedRecipes().contains(recipe.getName());
        }
        return false;
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7View Shaped Recipes");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true);
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem != null){
            if (clickedItem.equals(returnToMenuButton)) {
                new TemporaryRecipeViewMenu(playerMenuUtility).open();
                return;
            } else if (clickedItem.equals(nextPageButton)){
                currentPage++;
            } else if (clickedItem.equals(previousPageButton)){
                currentPage--;
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
        for (int i = 0; i < getSlots(); i++){
            inventory.setItem(i, Utils.createItemStack(Material.GRAY_STAINED_GLASS_PANE, Utils.chat("&8 "), null));
        }
        setShapedRecipesView();
    }

    private void setShapedRecipesView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> totalRecipeButtons = new ArrayList<>();

        for (DynamicCraftingTableRecipe recipe : CustomRecipeManager.getInstance().getShapedRecipes().values()){
            if (!isRecipeUnlocked(playerMenuUtility.getOwner(), recipe)) continue;
            ItemStack resultButton = null;
            if (recipe.isTinkerFirstItem()){
                for (ItemStack ingredient : recipe.getExactItems().values()){
                    if (EquipmentClass.getClass(ingredient) != null) {
                        resultButton = new ItemStack(ingredient);
                        break;
                    }
                }
                if (resultButton == null) {
                    resultButton = new ItemStack(Material.BARRIER);
                }
            } else {
                resultButton = new ItemStack(recipe.getResult());
            }
            ItemMeta resultMeta = resultButton.getItemMeta();
            assert resultMeta != null;
            resultMeta.setDisplayName(Utils.toPascalCase(recipe.getName().replace("_", " ")));
            List<String> resultLore = new ArrayList<>();
            if (resultMeta.getLore() != null){
                resultLore.addAll(resultMeta.getLore());
                resultLore.add(Utils.chat("&8&m                                      "));
            }

            if (recipe.isShapeless()){
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

            resultMeta.setLore(resultLore);
            resultButton.setItemMeta(resultMeta);

            Optional<DynamicItemModifier> customModelDataAddModifier = recipe.getItemModifiers().stream().filter(m -> m instanceof CustomModelDataAddModifier).findFirst();
            if (customModelDataAddModifier.isPresent()){
                CustomModelDataAddModifier modifier = (CustomModelDataAddModifier) customModelDataAddModifier.get();
                modifier.processItem(null, resultButton);
            }

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
}
