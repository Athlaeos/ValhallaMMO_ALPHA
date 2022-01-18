package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.Main;
import me.athlaeos.valhallammo.crafting.CustomRecipeManager;
import me.athlaeos.valhallammo.crafting.PlayerCraftChoiceManager;
import me.athlaeos.valhallammo.crafting.dom.AbstractCustomCraftingRecipe;
import me.athlaeos.valhallammo.crafting.dom.ItemCraftingRecipe;
import me.athlaeos.valhallammo.crafting.dom.ItemImprovementRecipe;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.dom.AccountProfile;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.dom.SkillType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CustomCraftRecipeMenu extends Menu {
    private final NamespacedKey smithingRecipeKey = new NamespacedKey(Main.getPlugin(), "valhalla_recipe_button");
    private AccountProfile profile;
    private List<AbstractCustomCraftingRecipe> unlockedRecipes = new ArrayList<>();
    private int pageNumber = 1;
    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;

    public CustomCraftRecipeMenu(PlayerMenuUtility playerMenuUtility, Collection<AbstractCustomCraftingRecipe> customRecipes) {
        super(playerMenuUtility);
        Profile p = ProfileUtil.getProfile(playerMenuUtility.getOwner(), SkillType.ACCOUNT);
        if (p instanceof AccountProfile){
            profile = (AccountProfile) p;
        }
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        if (profile != null){
            boolean allowedAllRecipes = playerMenuUtility.getOwner().hasPermission("valhalla.allrecipes");
            if (allowedAllRecipes){
                unlockedRecipes = new ArrayList<>(customRecipes);
            } else {
                for (AbstractCustomCraftingRecipe r : customRecipes){
                    if (profile.getUnlockedRecipes().contains(r.getName())){
                        unlockedRecipes.add(r);
                    }
                }
//                for (String recipe : profile.getUnlockedRecipes()){
//                    AbstractCustomCraftingRecipe smithingRecipe = CustomRecipeManager.getInstance().getRecipeByName(recipe);
//                    if (smithingRecipe != null){
//                        if (smithingRecipe.getCraftingBlock() == customRecipes){
//                            if (smithingRecipe instanceof ItemCraftingRecipe){
//                                unlockedRecipes.add((ItemCraftingRecipe) smithingRecipe);
//                            }
//                        }
//                    }
//                }
            }
        }
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7&lSmithing Recipes");
    }

    @Override
    public int getSlots() {
        if (unlockedRecipes.size() > 45){
            return 54;
        } else {
            return Math.max((int) (9*Math.ceil((float) unlockedRecipes.size()/9)), 9);
        }
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true);
        if (e.getCurrentItem() == null) return;
        assert e.getCurrentItem().getItemMeta() != null;
        if (e.getCurrentItem().equals(previousPageButton)){
            pageNumber--;
        } else if (e.getCurrentItem().equals(nextPageButton)){
            pageNumber++;
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(smithingRecipeKey, PersistentDataType.STRING)){
            String recipe = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(smithingRecipeKey, PersistentDataType.STRING);
            AbstractCustomCraftingRecipe chosenRecipe = CustomRecipeManager.getInstance().getRecipeByName(recipe);
            PlayerCraftChoiceManager.getInstance().setPlayerCurrentRecipe(playerMenuUtility.getOwner(), chosenRecipe);
            e.getWhoClicked().closeInventory();
        }
        setMenuItems();
    }

    @Override
    public void handleMenu(InventoryDragEvent e) {

    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        List<ItemStack> pickableRecipes = new ArrayList<>();
        if (profile != null){
            for (AbstractCustomCraftingRecipe recipe : unlockedRecipes){
                ItemStack button = null;
                if (recipe instanceof ItemCraftingRecipe){
                    button = ((ItemCraftingRecipe)recipe).getResult().clone();
                } else if (recipe instanceof ItemImprovementRecipe){
                    button = new ItemStack(((ItemImprovementRecipe) recipe).getRequiredItemType());
                }
                if (button != null){
                    ItemMeta buttonMeta = button.getItemMeta();
                    assert buttonMeta != null;
                    List<String> buttonLore = new ArrayList<>();
                    for (ItemStack ingredient : recipe.getIngredients()){
                        buttonLore.add(Utils.chat("&e"+ingredient.getAmount() + " &7x&e " + getItemName(ingredient)));
                    }
                    if (recipe.getDisplayName() == null){
                        buttonMeta.setDisplayName(Utils.chat(getItemName(button)));
                    } else {
                        buttonMeta.setDisplayName(Utils.chat(recipe.getDisplayName()));
                    }
                    buttonLore.add(Utils.chat(String.format("&e&lTime to craft: %.1fs", ((double) recipe.getCraftingTime()) / 1000D)));
                    buttonLore.add(Utils.chat("&8&m                                        "));

                    List<DynamicItemModifier> modifiers = new ArrayList<>(recipe.getItemModifers());
                    modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                    for (DynamicItemModifier modifier : modifiers){
                        buttonLore.add(Utils.chat(modifier.getCraftDescription().replace("%strength%", "" + Utils.round(modifier.getStrength(), 2))));
                    }

                    buttonMeta.setLore(buttonLore);
                    buttonMeta.getPersistentDataContainer().set(smithingRecipeKey, PersistentDataType.STRING, recipe.getName());
                    button.setItemMeta(buttonMeta);

                    boolean isCraftable = true;
                    for (DynamicItemModifier modifier : modifiers){
                        ItemStack buttonBackup = button.clone();
                        buttonBackup = modifier.processItem(playerMenuUtility.getOwner(), buttonBackup);
                        if (buttonBackup == null) {
                            if (isCraftable){
                                isCraftable = false;
                            }
                        } else {
                            button = buttonBackup.clone();
                        }
                    }

                    if (!isCraftable){
                        ItemMeta bMeta = button.getItemMeta();
                        assert bMeta != null;
                        List<String> bLore = bMeta.getLore();
                        assert bLore != null;
                        bLore.add(Utils.chat("&cNot Craftable: Not all conditions met"));
                        bMeta.setLore(bLore);
                        button.setItemMeta(bMeta);
                    }

                    pickableRecipes.add(button);
                }
            }
            pickableRecipes.sort(Comparator.comparing(ItemStack::getType));

            if (pickableRecipes.size() >= 45){
                Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, pickableRecipes);
                if (pageNumber > pages.size()){
                    pageNumber = pages.size();
                } else if (pageNumber < 1){
                    pageNumber = 1;
                }
                for (ItemStack button : pages.get(pageNumber - 1)){
                    inventory.addItem(button);
                }
                if (getSlots() == 54){
                    if (pageNumber < pages.size()){
                        inventory.setItem(53, nextPageButton);
                    }
                    if (pageNumber > 1){
                        inventory.setItem(45, previousPageButton);
                    }
                }
            } else {
                inventory.addItem(pickableRecipes.toArray(new ItemStack[]{}));
            }
        }
    }

    public List<AbstractCustomCraftingRecipe> getUnlockedRecipes() {
        return unlockedRecipes;
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
