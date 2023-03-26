package me.athlaeos.valhallammo.menus.temporary;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats.CustomModelDataAddModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicSmithingTableRecipe;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.menus.Menu;
import me.athlaeos.valhallammo.menus.PlayerMenuUtility;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class ViewSmithingRecipeMenu extends Menu {
    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;
    private int currentPage = 0;
    private final ItemStack returnToMenuButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lReturn to menu"),
            new ArrayList<>()
    );

    public ViewSmithingRecipeMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
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
    public boolean isRecipeUnlocked(Player player, DynamicSmithingTableRecipe recipe){
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
    public void setMenuItems() {
        inventory.clear();
        for (int i = 0; i < getSlots(); i++){
            inventory.setItem(i, Utils.createItemStack(Material.GRAY_STAINED_GLASS_PANE, Utils.chat("&8 "), null));
        }
        setRecipesView();
    }

    private void setRecipesView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> totalRecipeButtons = new ArrayList<>();

        for (DynamicSmithingTableRecipe recipe : CustomRecipeManager.getInstance().getSmithingRecipes().values()){
            if (!isRecipeUnlocked(playerMenuUtility.getOwner(), recipe)) continue;
            ItemStack resultButton = (recipe.isTinkerBase() ? new ItemStack(recipe.getBase()) : new ItemStack(recipe.getResult()));
            ItemMeta resultMeta = resultButton.getItemMeta();
            assert resultMeta != null;
            resultMeta.setDisplayName(recipe.getName());
            List<String> resultLore = new ArrayList<>();
            if (resultMeta.getLore() != null){
                resultLore.addAll(resultMeta.getLore());
                resultLore.add(Utils.chat("&8&m                                      "));
            }
            resultLore.add(Utils.chat(String.format("&f%s&r&f + %s&r&f >>> %s", Utils.getItemName(recipe.getBase()), Utils.getItemName(recipe.getAddition()), recipe.isTinkerBase() ? "&dUpgraded " + Utils.getItemName(recipe.getBase()) : Utils.getItemName(recipe.getResult()))));

            resultLore.add(Utils.chat("&8&m                                      "));


            if (!recipe.isConsumeAddition() && !recipe.getModifiersAddition().isEmpty()){
                resultLore.add(Utils.chat("&fThe addition item is not consumed"));
            }

            resultMeta.setLore(resultLore);
            resultButton.setItemMeta(resultMeta);

            Optional<DynamicItemModifier> customModelDataAddModifier = recipe.getModifiersResult().stream().filter(m -> m instanceof CustomModelDataAddModifier).findFirst();
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
