package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.AbstractCustomCraftingRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.ItemClassImprovementRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.ItemCraftingRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.ItemImprovementRecipe;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CraftRecipeChoiceMenu extends Menu {
    private static final boolean advanced_crafting_preview = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("advanced_crafting_preview");

    private final NamespacedKey smithingRecipeKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_recipe_button");
    private AccountProfile profile;
    private List<AbstractCustomCraftingRecipe> unlockedRecipes = new ArrayList<>();
    private int pageNumber = 1;
    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;

    private final List<String> recipeButtonFormat;
    private final String recipeIngredientFormat;
    private final String recipeNotCraftable;

    public CraftRecipeChoiceMenu(PlayerMenuUtility playerMenuUtility, Collection<AbstractCustomCraftingRecipe> customRecipes) {
        super(playerMenuUtility);
        recipeButtonFormat = TranslationManager.getInstance().getList("recipe_button_format");
        recipeIngredientFormat = TranslationManager.getInstance().getTranslation("recipe_ingredient_format");
        recipeNotCraftable = TranslationManager.getInstance().getTranslation("recipe_uncraftable");

        Profile p = ProfileManager.getProfile(playerMenuUtility.getOwner(), "ACCOUNT");
        if (p instanceof AccountProfile){
            profile = (AccountProfile) p;
        }
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat(TranslationManager.getInstance().getTranslation("translation_next_page")), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat(TranslationManager.getInstance().getTranslation("translation_previous_page")), null);
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
        return Utils.chat(TranslationManager.getInstance().getTranslation("translation_recipes"));
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
        if (profile != null){
            buildMenuItems(items -> {
                items.sort(Comparator.comparing(Utils::getItemName));

                if (items.size() >= 45){
                    Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, items);
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
                    inventory.addItem(items.toArray(new ItemStack[]{}));
                }
            });
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

    private void buildMenuItems(final ItemBuilderCallback callback){
        ValhallaMMO.getPlugin().getServer().getScheduler().runTaskAsynchronously(ValhallaMMO.getPlugin(), () -> {
            List<ItemStack> pickableRecipes = new ArrayList<>();
            for (AbstractCustomCraftingRecipe recipe : unlockedRecipes){
                ItemStack button = null;
                if (recipe instanceof ItemCraftingRecipe){
                    button = ((ItemCraftingRecipe)recipe).getResult().clone();
                } else if (recipe instanceof ItemImprovementRecipe || recipe instanceof ItemClassImprovementRecipe){
                    button = playerMenuUtility.getOwner().getInventory().getItemInMainHand().clone();
                    ItemStack mainHandItem = playerMenuUtility.getOwner().getInventory().getItemInMainHand();
                    if (!Utils.isItemEmptyOrNull(mainHandItem)){
                        int quality = SmithingItemTreatmentManager.getInstance().getItemsQuality(mainHandItem);
                        SmithingItemTreatmentManager.getInstance().setItemsQuality(button, quality);

//                        ItemAttributesManager.getInstance().setDefaultStats(mainHandItem, ItemAttributesManager.getInstance().getDefaultStats(mainHandItem));
//                        ItemAttributesManager.getInstance().setStats(mainHandItem, ItemAttributesManager.getInstance().getCurrentStats(mainHandItem));
                    }
                }
                if (button != null){
                    ItemMeta buttonMeta = button.getItemMeta();
                    if (buttonMeta != null){
                        List<String> buttonLore = new ArrayList<>();
                        List<DynamicItemModifier> modifiers = new ArrayList<>(recipe.getItemModifers());
                        modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));

                        for (String s : recipeButtonFormat){
                            if (s.contains("%ingredients%")){
                                for (ItemStack ingredient : recipe.getIngredients()){
                                    buttonLore.add(Utils.chat(recipeIngredientFormat
                                            .replace("%item%", Utils.getItemName(ingredient))
                                            .replace("%amount%", String.format("%d", ingredient.getAmount()))));
                                }
                            } else if (s.contains("%modifiers%")) {
                                if (advanced_crafting_preview){
                                    for (DynamicItemModifier modifier : modifiers){
                                        String craftDescription = modifier.getCraftDescription();
                                        if (craftDescription != null){
                                            if (!craftDescription.equals("")){
                                                if (modifier instanceof TripleArgDynamicItemModifier){
                                                    buttonLore.add(Utils.chat(craftDescription
                                                            .replace("%strength%", "" + Utils.round(modifier.getStrength(), 2))
                                                            .replace("%strength2%", "" + Utils.round(((TripleArgDynamicItemModifier) modifier).getStrength2(), 2))
                                                            .replace("%strength3%", "" + Utils.round(((TripleArgDynamicItemModifier) modifier).getStrength3(), 2))));
                                                } else if (modifier instanceof DuoArgDynamicItemModifier){
                                                    buttonLore.add(Utils.chat(craftDescription
                                                            .replace("%strength%", "" + Utils.round(modifier.getStrength(), 2))
                                                            .replace("%strength2%", "" + Utils.round(((DuoArgDynamicItemModifier) modifier).getStrength2(), 2))));
                                                } else {
                                                    buttonLore.add(Utils.chat(craftDescription.replace("%strength%", "" + Utils.round(modifier.getStrength(), 2))));
                                                }
                                            }
                                        }
                                    }
                                }
                            } else {
                                buttonLore.add(Utils.chat(s
                                        .replace("%crafting_time%", String.format("%.1f", (recipe.getCraftingTime()/1000D)))));
                            }
                        }

                        if (recipe.getDisplayName() == null){
                            buttonMeta.setDisplayName(Utils.chat(getItemName(button)));
                        } else {
                            buttonMeta.setDisplayName(Utils.chat(recipe.getDisplayName()));
                        }

                        buttonMeta.setLore(buttonLore);
                        buttonMeta.getPersistentDataContainer().set(smithingRecipeKey, PersistentDataType.STRING, recipe.getName());
                        button.setItemMeta(buttonMeta);

                        boolean isCraftable = true;
                        if (advanced_crafting_preview){
                            for (DynamicItemModifier modifier : modifiers){
                                ItemStack buttonBackup = button.clone();
                                try {
                                    modifier = modifier.clone();
                                    modifier.setValidate(true);
                                    modifier.setUse(false);
                                    buttonBackup = modifier.processItem(playerMenuUtility.getOwner(), buttonBackup);
                                    if (buttonBackup == null) {
                                        if (isCraftable){
                                            isCraftable = false;
                                        }
                                    } else {
                                        button = buttonBackup.clone();
                                    }
                                } catch (CloneNotSupportedException ignored){
                                }
                            }
                        }

                        if (!isCraftable){
                            ItemMeta bMeta = button.getItemMeta();
                            assert bMeta != null;
                            List<String> bLore = bMeta.getLore();
                            assert bLore != null;
                            bLore.add(Utils.chat(recipeNotCraftable));
                            bMeta.setLore(bLore);
                            button.setItemMeta(bMeta);
                        }

                        pickableRecipes.add(button);
                    }
                }
            }

            ValhallaMMO.getPlugin().getServer().getScheduler().runTask(ValhallaMMO.getPlugin(), () -> {
                // call the callback with the result
                callback.onItemsBuilt(pickableRecipes);
            });
        });
    }

    private interface ItemBuilderCallback{
        void onItemsBuilt(List<ItemStack> items);
    }
}
