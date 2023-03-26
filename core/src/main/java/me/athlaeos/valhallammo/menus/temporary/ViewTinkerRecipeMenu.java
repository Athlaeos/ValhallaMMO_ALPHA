package me.athlaeos.valhallammo.menus.temporary;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats.CustomModelDataAddModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.AbstractCustomCraftingRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.ItemImprovementRecipe;
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

public class ViewTinkerRecipeMenu extends Menu {
    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;
    private int currentPage = 0;
    private final ItemStack returnToMenuButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lReturn to menu"),
            new ArrayList<>()
    );

    public ViewTinkerRecipeMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7View Tinker Recipes");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true);

        if (e.getCurrentItem() != null){
            if (e.getCurrentItem().equals(returnToMenuButton)) {
                new TemporaryRecipeViewMenu(playerMenuUtility).open();
                return;
            } else if (e.getCurrentItem().equals(nextPageButton)){
                currentPage++;
            } else if (e.getCurrentItem().equals(previousPageButton)){
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
        setCustomImproveRecipesView();
    }
    public boolean isRecipeUnlocked(Player player, AbstractCustomCraftingRecipe recipe){
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

    private void setCustomImproveRecipesView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> totalRecipeButtons = new ArrayList<>();
        for (AbstractCustomCraftingRecipe recipe : CustomRecipeManager.getInstance().getAllCustomRecipes().values()){
            if (!isRecipeUnlocked(playerMenuUtility.getOwner(), recipe)) continue;
            if (recipe instanceof ItemImprovementRecipe){
                ItemStack resultButton = new ItemStack(((ItemImprovementRecipe) recipe).getRequiredItemType());
                ItemMeta resultMeta = resultButton.getItemMeta();
                assert resultMeta != null;
                resultMeta.setDisplayName(Utils.toPascalCase(recipe.getName().replace("_", " ")));
                List<String> resultLore = new ArrayList<>();
                if (resultMeta.getLore() != null){
                    resultLore.addAll(resultMeta.getLore());
                    resultLore.add(Utils.chat("&8&m                                      "));
                }
                for (ItemStack ingredient : recipe.getIngredients()){
                    resultLore.add(Utils.chat("&e"+ingredient.getAmount() + " &7x&e " + getItemName(ingredient)));
                }
                resultLore.add(Utils.chat("&8&m                                      "));

                resultLore.add(Utils.chat("&7Tinkered on: &e"+ recipe.getCraftingBlock().toString().toLowerCase().replace("_", " ")));
                resultMeta.setLore(resultLore);
                resultButton.setItemMeta(resultMeta);

                Optional<DynamicItemModifier> customModelDataAddModifier = recipe.getItemModifiers().stream().filter(m -> m instanceof CustomModelDataAddModifier).findFirst();
                if (customModelDataAddModifier.isPresent()){
                    CustomModelDataAddModifier modifier = (CustomModelDataAddModifier) customModelDataAddModifier.get();
                    modifier.processItem(null, resultButton);
                }

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
}
