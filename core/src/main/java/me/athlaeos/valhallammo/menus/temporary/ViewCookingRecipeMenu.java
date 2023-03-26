package me.athlaeos.valhallammo.menus.temporary;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats.CustomModelDataAddModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.*;
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

public class ViewCookingRecipeMenu extends Menu {
    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;
    private int currentPage = 0;
    private final RecipeType type;
    private final ItemStack returnToMenuButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lReturn to menu"),
            new ArrayList<>()
    );

    public ViewCookingRecipeMenu(PlayerMenuUtility playerMenuUtility, RecipeType type) {
        super(playerMenuUtility);
        this.type = type;
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
    }

    @Override
    public String getMenuName() {
        switch (type){
            case CAMPFIRE: return Utils.chat("&7View Campfire Recipes");
            case FURNACE: return Utils.chat("&7View Furnace Recipes");
            case SMOKER: return Utils.chat("&7View Smoker Recipes");
            case BLAST_FURNACE: return Utils.chat("&7View Blasting Recipes");
        }
        return Utils.chat("&7This should never be visible lol");
    }
    public boolean isRecipeUnlocked(Player player, DynamicCookingRecipe<?> recipe){
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
        setRecipesView();
    }

    private void setRecipesView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> totalRecipeButtons = new ArrayList<>();

        for (DynamicCookingRecipe<?> recipe : CustomRecipeManager.getInstance().getCookingRecipes().values()){
            if (!isRecipeUnlocked(playerMenuUtility.getOwner(), recipe)) continue;
            if ((type == RecipeType.SMOKER && !(recipe instanceof DynamicSmokingRecipe)) ||
                    (type == RecipeType.BLAST_FURNACE && !(recipe instanceof DynamicBlastingRecipe)) ||
                    (type == RecipeType.FURNACE && !(recipe instanceof DynamicFurnaceRecipe)) ||
                    (type == RecipeType.CAMPFIRE && !(recipe instanceof DynamicCampfireRecipe))) continue;
            ItemStack resultButton = (recipe.isTinkerInput() ? new ItemStack(recipe.getInput()) : new ItemStack(recipe.getResult()));
            ItemMeta resultMeta = resultButton.getItemMeta();
            assert resultMeta != null;
            resultMeta.setDisplayName(Utils.toPascalCase(recipe.getName().replace("_", " ")));
            List<String> resultLore = new ArrayList<>();
            if (resultMeta.getLore() != null){
                resultLore.addAll(resultMeta.getLore());
                resultLore.add(Utils.chat("&8&m                                      "));
            }
            if (recipe.isTinkerInput()){
                resultLore.add(Utils.chat("&fInput is changed"));
            } else {
                resultLore.add(Utils.chat("&fProduces " + Utils.getItemName(recipe.getResult())));
                resultLore.add(Utils.chat("&fRequires " + (recipe.isUseMetadata() ? Utils.getItemName(recipe.getInput()) : Utils.toPascalCase(recipe.getInput().getType().toString().replace("_", " ")))));
            }
            resultLore.add(Utils.chat("&8&m                                      "));
            resultLore.add(Utils.chat(String.format("&7Time to cook: &e%.1fs", (recipe.getCookTime() / 20D))));
            if (recipe instanceof DynamicCampfireRecipe) resultLore.add(Utils.chat(String.format("&7Works on &e%s", (((DynamicCampfireRecipe) recipe).getCampfireMode() == 0) ? "All Campfires" : (((DynamicCampfireRecipe) recipe).getCampfireMode() == 1) ? "Regular Campfires Only" : "Soul Campfires Only")));

            resultLore.add(Utils.chat("&8&m                                      "));

            resultMeta.setLore(resultLore);
            resultButton.setItemMeta(resultMeta);

            Optional<DynamicItemModifier> customModelDataAddModifier = recipe.getModifiers().stream().filter(m -> m instanceof CustomModelDataAddModifier).findFirst();
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

    public enum RecipeType{
        CAMPFIRE,
        FURNACE,
        BLAST_FURNACE,
        SMOKER
    }
}
