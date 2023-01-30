package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.InvisibleIfIncompatibleModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.AbstractCustomCraftingRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.ItemClassImprovementRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.ItemCraftingRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.ItemImprovementRecipe;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CraftRecipeChoiceMenu extends Menu {
    private static final boolean advanced_crafting_preview = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("advanced_crafting_preview");

    private static final String craftingTitle5r = TranslationManager.getInstance().getTranslation("translation_recipes_5r");
    private static final String tinkerTitle5r = TranslationManager.getInstance().getTranslation("translation_tinker_recipes_5r");
    private static final String craftingTitle4r = TranslationManager.getInstance().getTranslation("translation_recipes_4r");
    private static final String tinkerTitle4r = TranslationManager.getInstance().getTranslation("translation_tinker_recipes_4r");
    private static final String craftingTitle3r = TranslationManager.getInstance().getTranslation("translation_recipes_3r");
    private static final String tinkerTitle3r = TranslationManager.getInstance().getTranslation("translation_tinker_recipes_3r");
    private static final String craftingTitle2r = TranslationManager.getInstance().getTranslation("translation_recipes_2r");
    private static final String tinkerTitle2r = TranslationManager.getInstance().getTranslation("translation_tinker_recipes_2r");
    private static final String craftingTitle1r = TranslationManager.getInstance().getTranslation("translation_recipes_1r");
    private static final String tinkerTitle1r = TranslationManager.getInstance().getTranslation("translation_tinker_recipes_1r");
    private static final String craftingTitle0r = TranslationManager.getInstance().getTranslation("translation_recipes_0r");
    private static final String tinkerTitle0r = TranslationManager.getInstance().getTranslation("translation_tinker_recipes_0r");

    private final int slots;
    private final int rows;
    private int getAvailableSlots(){
        if (unlockedRecipes.size() >= 45){
            return 54;
        } else {
            return Math.max((int) (9*Math.ceil((float) unlockedRecipes.size()/9)), 9) + (isCrafting ? 9 : 0);
        }
    }
    private ItemStack recipeFilter = null;

    private final NamespacedKey smithingRecipeKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_recipe_button");
    private AccountProfile profile;
    private List<AbstractCustomCraftingRecipe> unlockedRecipes = new ArrayList<>();
    private int pageNumber = 1;
    private ItemStack nextPageButton;
    private ItemStack previousPageButton;

    private final List<String> recipeButtonFormat;
    private final String recipeIngredientFormat;
    private final String recipeNotCraftable;

    private static final Collection<UUID> playersWhoReceivedFirstTimeMessage = new HashSet<>();
    private final String statusOneTimeCraftingRecipeSelected;
    private final String statusOneTimeTinkeringRecipeSelected;
    private final String statusCraftingRecipeSelected;
    private final String statusTinkeringRecipeSelected;
    private final boolean isCrafting;

    public CraftRecipeChoiceMenu(PlayerMenuUtility playerMenuUtility, Collection<AbstractCustomCraftingRecipe> customRecipes, boolean isCrafting) {
        super(playerMenuUtility);
        this.isCrafting = isCrafting;
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        recipeButtonFormat = TranslationManager.getInstance().getList("recipe_button_format");
        recipeIngredientFormat = TranslationManager.getInstance().getTranslation("recipe_ingredient_format");
        recipeNotCraftable = TranslationManager.getInstance().getTranslation("recipe_uncraftable");

        statusOneTimeCraftingRecipeSelected = TranslationManager.getInstance().getTranslation("status_onetime_crafting_recipe_selected");
        statusOneTimeTinkeringRecipeSelected = TranslationManager.getInstance().getTranslation("status_onetime_tinkering_recipe_selected");
        statusCraftingRecipeSelected = TranslationManager.getInstance().getTranslation("status_crafting_recipe_selected");
        statusTinkeringRecipeSelected = TranslationManager.getInstance().getTranslation("status_tinkering_recipe_selected");

        Profile p = ProfileManager.getManager().getProfile(playerMenuUtility.getOwner(), "ACCOUNT");
        if (p instanceof AccountProfile){
            profile = (AccountProfile) p;
        }
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat(TranslationManager.getInstance().getTranslation("translation_next_page")), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat(TranslationManager.getInstance().getTranslation("translation_previous_page")), null);
        nextPageButton = Utils.setCustomModelData(nextPageButton, config.getInt("model_data_nextpage", -1));
        previousPageButton = Utils.setCustomModelData(previousPageButton, config.getInt("model_data_prevpage", -1));
        if (profile != null){
            boolean allowedAllRecipes = playerMenuUtility.getOwner().hasPermission("valhalla.allrecipes");
            if (allowedAllRecipes){
                unlockedRecipes = new ArrayList<>(customRecipes);
            } else {
                for (AbstractCustomCraftingRecipe r : customRecipes){
                    if (r.isUnlockedForEveryone() || profile.getUnlockedRecipes().contains(r.getName())){
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

        slots = getAvailableSlots();
        rows = (slots - 9) / 9;
    }

    @Override
    public String getMenuName() {
        switch (rows){
            case 5: return Utils.chat(isCrafting ? (ValhallaMMO.isPackEnabled() ? "&f\uF808\uF002" : craftingTitle5r) : (ValhallaMMO.isPackEnabled() ? "&f\uF808\uF102" : tinkerTitle5r));
            case 4: return Utils.chat(isCrafting ? (ValhallaMMO.isPackEnabled() ? "&f\uF808\uF003" : craftingTitle4r) : (ValhallaMMO.isPackEnabled() ? "&f\uF808\uF103" : tinkerTitle4r));
            case 3: return Utils.chat(isCrafting ? (ValhallaMMO.isPackEnabled() ? "&f\uF808\uF004" : craftingTitle3r) : (ValhallaMMO.isPackEnabled() ? "&f\uF808\uF104" : tinkerTitle3r));
            case 2: return Utils.chat(isCrafting ? (ValhallaMMO.isPackEnabled() ? "&f\uF808\uF005" : craftingTitle2r) : (ValhallaMMO.isPackEnabled() ? "&f\uF808\uF105" : tinkerTitle2r));
            case 1: return Utils.chat(isCrafting ? (ValhallaMMO.isPackEnabled() ? "&f\uF808\uF006" : craftingTitle1r) : (ValhallaMMO.isPackEnabled() ? "&f\uF808\uF106" : tinkerTitle1r));
            default: return Utils.chat(isCrafting ? (ValhallaMMO.isPackEnabled() ? "&f\uF808\uF007" : craftingTitle0r) : (ValhallaMMO.isPackEnabled() ? "&f\uF808\uF107" : tinkerTitle0r));
        }
    }

    @Override
    public int getSlots() {
        return slots;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true);
        if (isCrafting){
            if (e.getClickedInventory() instanceof PlayerInventory){
                if (Utils.isItemEmptyOrNull(e.getCurrentItem())){
                    recipeFilter = null;
                } else {
                    recipeFilter = e.getCurrentItem().clone();
                    recipeFilter.setAmount(1);
                }
            } else {
                if (e.getSlot() == rows * 9 + 4){
                    recipeFilter = null;
                }
                if (Utils.isItemEmptyOrNull(e.getCurrentItem())) return;
            }
        } else {
            if (Utils.isItemEmptyOrNull(e.getCurrentItem())) return;
        }
        assert e.getCurrentItem().getItemMeta() != null;
        if (e.getCurrentItem().equals(previousPageButton)){
            pageNumber--;
        } else if (e.getCurrentItem().equals(nextPageButton)){
            pageNumber++;
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(smithingRecipeKey, PersistentDataType.STRING)){
            String recipe = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(smithingRecipeKey, PersistentDataType.STRING);
            AbstractCustomCraftingRecipe chosenRecipe = CustomRecipeManager.getInstance().getRecipeByName(recipe);
            PlayerCraftChoiceManager.getInstance().setPlayerCurrentRecipe(playerMenuUtility.getOwner(), chosenRecipe);
            if (chosenRecipe instanceof ItemCraftingRecipe){
                if (playersWhoReceivedFirstTimeMessage.contains(playerMenuUtility.getOwner().getUniqueId())){
                    playerMenuUtility.getOwner().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                            Utils.chat(statusCraftingRecipeSelected
                                    .replace("%recipe%", chosenRecipe.getDisplayName())
                                    .replace("%item%", Utils.getItemName(((ItemCraftingRecipe) chosenRecipe).getResult())))
                    ));
                } else {
                    playerMenuUtility.getOwner().sendMessage(Utils.chat(
                            statusOneTimeCraftingRecipeSelected
                                    .replace("%recipe%", chosenRecipe.getDisplayName())
                                    .replace("%item%", Utils.getItemName(((ItemCraftingRecipe) chosenRecipe).getResult()))
                    ));
                    playersWhoReceivedFirstTimeMessage.add(playerMenuUtility.getOwner().getUniqueId());
                }
            } else {
                String item = null;
                ItemStack i = playerMenuUtility.getOwner().getInventory().getItemInMainHand();
                if (!Utils.isItemEmptyOrNull(i)){
                    item = Utils.getItemName(i);
                } else {
                    if (chosenRecipe instanceof ItemImprovementRecipe){
                        item = Utils.toPascalCase(((ItemImprovementRecipe) chosenRecipe).getRequiredItemType().toString().replace("_", " "));
                    } else if (chosenRecipe instanceof ItemClassImprovementRecipe){
                        item = Utils.toPascalCase(((ItemClassImprovementRecipe) chosenRecipe).getRequiredEquipmentClass().toString().replace("_", " "));
                    }
                }
                if (playersWhoReceivedFirstTimeMessage.contains(playerMenuUtility.getOwner().getUniqueId())){
                    playerMenuUtility.getOwner().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(
                            Utils.chat(statusTinkeringRecipeSelected
                                    .replace("%recipe%", chosenRecipe.getDisplayName())
                                    .replace("%item%", item == null ? "" : item))
                    ));
                } else {
                    playerMenuUtility.getOwner().sendMessage(Utils.chat(
                            statusOneTimeTinkeringRecipeSelected
                                    .replace("%recipe%", chosenRecipe.getDisplayName())
                                    .replace("%item%", item == null ? "" : item))
                    );
                    playersWhoReceivedFirstTimeMessage.add(playerMenuUtility.getOwner().getUniqueId());
                }
            }
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
        if (isCrafting){
            if (!Utils.isItemEmptyOrNull(recipeFilter)){
                inventory.setItem(Math.min(49, rows * 9 + 4), recipeFilter);
            }
        }
        if (profile != null){
            buildMenuItems(items -> {
                items.sort(Comparator.comparing(ItemStack::getType).thenComparing(item -> ChatColor.stripColor(Utils.getItemName(item))));

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
//                System.out.println("Items built after " + CooldownManager.getInstance().getTimerResult(playerMenuUtility.getOwner().getUniqueId(), "benchmark"));
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
            recipeLoop:
            for (AbstractCustomCraftingRecipe recipe : unlockedRecipes){
                if (isCrafting){
                    if (!Utils.isItemEmptyOrNull(recipeFilter)){
                        if (!containsItem(recipe.getIngredients(), recipeFilter, recipe.requireExactMeta())) continue;
                    }
                }

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
                        List<DynamicItemModifier> modifiers = new ArrayList<>(recipe.getItemModifiers());
                        DynamicItemModifier.sortModifiers(modifiers);

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
                                        .replace("%crafting_time%", String.format("%.1f", ((recipe.getCraftingTime() * (1 - AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("CRAFTING_TIME_REDUCTION", playerMenuUtility.getOwner(), 5000, true)))/1000D)))));
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
                                            if (modifier instanceof InvisibleIfIncompatibleModifier){
                                                continue recipeLoop;
                                            }
                                            isCraftable = false;
                                        }
                                    } else {
                                        button = buttonBackup.clone();
                                    }
                                } catch (CloneNotSupportedException ignored){
                                }
                            }
                        } else {
                            for (DynamicItemModifier modifier : modifiers){
                                if (!(modifier instanceof InvisibleIfIncompatibleModifier)) continue;
                                ItemStack buttonBackup = button.clone();
                                try {
                                    modifier = modifier.clone();
                                    modifier.setValidate(true);
                                    modifier.setUse(false);
                                    buttonBackup = modifier.processItem(playerMenuUtility.getOwner(), buttonBackup);
                                    if (buttonBackup == null) {
                                        isCraftable = false;
                                        break;
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

    private boolean containsItem(Collection<ItemStack> items, ItemStack item, boolean exactMeta){
        for (ItemStack i : items){
            if (exactMeta){
                if (i.isSimilar(item)) return true;
            } else {
                if (ItemUtils.isSimilarTo(i.getType(), item.getType())) return true;
            }
        }
        return false;
    }
}
