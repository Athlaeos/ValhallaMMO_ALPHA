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
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CraftRecipeChoiceMenuUpdated extends Menu {
    private View view;
    private static final boolean advanced_crafting_preview = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("advanced_crafting_preview");

    private static final String favouritePrefix = TranslationManager.getInstance().getTranslation("recipe_favourited_prefix");
    private static final String favouriteSuffix = TranslationManager.getInstance().getTranslation("recipe_favourited_suffix");

    private static final String tool_display_default = TranslationManager.getInstance().translatePlaceholders(ConfigManager.getInstance().getConfig("config.yml").get().getString("tool_display_default"));
    private static final String title = TranslationManager.getInstance().getTranslation("translation_recipes_5r");
    private static final Map<Integer, String> toolSpecialLines = getToolSpecialLines();
    private ItemStack recipeFilter = null;

    private final NamespacedKey smithingRecipeKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_recipe_button");
    private final NamespacedKey favouriteRecipesKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_recipe_favourites");
    private AccountProfile profile;
    private List<AbstractCustomCraftingRecipe> unlockedRecipes = new ArrayList<>();
    private int pageNumber = 1;
    private ItemStack nextPageButton;
    private ItemStack previousPageButton;
    private ItemStack craftingViewButton;
    private ItemStack tinkeringViewButton;

    private final List<String> recipeButtonFormat;
    private final String recipeIngredientFormat;
    private final String recipeNotCraftable;

    private static final Collection<UUID> playersWhoReceivedFirstTimeMessage = new HashSet<>();
    private final String statusOneTimeCraftingRecipeSelected;
    private final String statusOneTimeTinkeringRecipeSelected;
    private final String statusCraftingRecipeSelected;
    private final String statusTinkeringRecipeSelected;

    private final Map<View, List<ItemStack>> items = new HashMap<>();

    public CraftRecipeChoiceMenuUpdated(PlayerMenuUtility playerMenuUtility, Collection<AbstractCustomCraftingRecipe> customRecipes, boolean isCrafting) {
        super(playerMenuUtility);
        this.view = isCrafting ? View.CRAFTING_RECIPES : View.TINKERING_RECIPES;
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

        craftingViewButton = Utils.createItemStack(Material.CRAFTING_TABLE, Utils.chat(TranslationManager.getInstance().getTranslation("translation_craft_view")), null);
        tinkeringViewButton = Utils.createItemStack(Material.ANVIL, Utils.chat(TranslationManager.getInstance().getTranslation("translation_tinker_view")), null);
        craftingViewButton = Utils.setCustomModelData(craftingViewButton, config.getInt("model_data_craft_view", -1));
        tinkeringViewButton = Utils.setCustomModelData(tinkeringViewButton, config.getInt("model_data_tinker_view", -1));
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
        buildMenuItems();
    }

    @Override
    public String getMenuName() {
        return Utils.chat(ValhallaMMO.isPackEnabled() ? "&f\uF808\uF002" : title);
    }

    @Override
    public int getSlots() {
        return 54;
    }

    private void addFavouriteDisplay(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        String currentName = Utils.getItemName(item).replace(ChatColor.stripColor(Utils.chat(favouritePrefix)), "").replace(ChatColor.stripColor(Utils.chat(favouriteSuffix)), "");
        meta.setDisplayName(Utils.chat(favouritePrefix + currentName + favouriteSuffix));
        item.setItemMeta(meta);
    }

    private void removeFavouriteDisplay(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        if (meta == null) return;
        if (!meta.hasDisplayName()) return;
        meta.setDisplayName(meta.getDisplayName().replace(ChatColor.stripColor(Utils.chat(favouritePrefix)), "").replace(ChatColor.stripColor(Utils.chat(favouriteSuffix)), ""));
        item.setItemMeta(meta);
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true);
        if (e.getClickedInventory() instanceof PlayerInventory){
            if (Utils.isItemEmptyOrNull(e.getCurrentItem())){
                recipeFilter = null;
            } else {
                recipeFilter = e.getCurrentItem().clone();
                recipeFilter.setAmount(1);
            }
        } else {
            if (e.getSlot() == 49){
                recipeFilter = null;
            }
            if (Utils.isItemEmptyOrNull(e.getCurrentItem())) return;
        }
        assert e.getCurrentItem().getItemMeta() != null;
        if (e.getCurrentItem().equals(craftingViewButton)){
            if (view != View.CRAFTING_RECIPES) view = View.CRAFTING_RECIPES;
        } else if (e.getCurrentItem().equals(tinkeringViewButton)){
            if (view != View.TINKERING_RECIPES) view = View.TINKERING_RECIPES;
        } else if (e.getCurrentItem().equals(previousPageButton)){
            pageNumber--;
        } else if (e.getCurrentItem().equals(nextPageButton)){
            pageNumber++;
        } else if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(smithingRecipeKey, PersistentDataType.STRING)){
            if (e.getClick().isShiftClick()){
                String recipe = e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(smithingRecipeKey, PersistentDataType.STRING);
                if (isFavourited((Player) e.getWhoClicked(), recipe)){
                    removeFavourite((Player) e.getWhoClicked(), recipe);
                } else {
                    addFavourite((Player) e.getWhoClicked(), recipe);
                }
            } else {
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
        }
        setMenuItems();
    }

    @Override
    public void handleMenu(InventoryDragEvent e) {

    }

    private boolean isIconFavourited(ItemStack i){
        if (i.getItemMeta() == null) return false;
        String recipe = i.getItemMeta().getPersistentDataContainer().get(smithingRecipeKey, PersistentDataType.STRING);
        return isFavourited(playerMenuUtility.getOwner(), recipe);
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        if (!Utils.isItemEmptyOrNull(recipeFilter)){
            inventory.setItem(49, recipeFilter);
        }
        inventory.setItem(47, craftingViewButton);
        inventory.setItem(51, tinkeringViewButton);
        List<ItemStack> buttons = items.getOrDefault(view, new ArrayList<>());
        if (profile != null){
                List<ItemStack> filteredItems = new ArrayList<>(items.getOrDefault(view, new ArrayList<>()));
                for (ItemStack b : buttons){
                    if (!Utils.isItemEmptyOrNull(recipeFilter)){
                        if (b.getItemMeta() == null) continue;
                        if (b.getItemMeta().getPersistentDataContainer().has(smithingRecipeKey, PersistentDataType.STRING)) {
                            String recipeName = b.getItemMeta().getPersistentDataContainer().get(smithingRecipeKey, PersistentDataType.STRING);
                            AbstractCustomCraftingRecipe recipe = CustomRecipeManager.getInstance().getRecipeByName(recipeName);
                            if (recipe instanceof ItemCraftingRecipe){
                                if (!containsItem(recipe.getIngredients(), recipeFilter, recipe.requireExactMeta())) {
                                    filteredItems.remove(b);
                                    continue;
                                }
                            } else {
                                if (recipe instanceof ItemImprovementRecipe){
                                    // filter out item improvement recipes if filter material does not match recipe required material
                                    if (((ItemImprovementRecipe) recipe).getRequiredItemType() != recipeFilter.getType()) {
                                        filteredItems.remove(b);
                                        continue;
                                    }
                                } else if (recipe instanceof ItemClassImprovementRecipe){
                                    // filter out item class improvement recipes if filter item class does not match recipe required class
                                    if (((ItemClassImprovementRecipe) recipe).getRequiredEquipmentClass() != EquipmentClass.getClass(recipeFilter)) {
                                        filteredItems.remove(b);
                                        continue;
                                    }
                                }
                            }
                        }
                    }
                    if (isIconFavourited(b)) addFavouriteDisplay(b);
                    else removeFavouriteDisplay(b);
                    buttons = filteredItems;
                }
            buttons.sort(Comparator.comparing(this::isIconFavourited, Comparator.reverseOrder()).thenComparing(ItemStack::getType).thenComparing(item -> ChatColor.stripColor(Utils.getItemName(item))));

            if (buttons.size() >= 45){
                Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, buttons);
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
                inventory.addItem(buttons.toArray(new ItemStack[]{}));
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

    private void buildMenuItems(){
        ValhallaMMO.getPlugin().getServer().getScheduler().runTaskAsynchronously(ValhallaMMO.getPlugin(), () -> {
            recipeLoop:
            for (AbstractCustomCraftingRecipe recipe : unlockedRecipes){
                ItemStack button = null;
                if (recipe instanceof ItemCraftingRecipe){
                    button = ((ItemCraftingRecipe)recipe).getResult().clone();
                } else if (recipe instanceof ItemImprovementRecipe){
                    ItemStack inHandItem = playerMenuUtility.getOwner().getInventory().getItemInMainHand().clone();
                    if (inHandItem.getType() == ((ItemImprovementRecipe) recipe).getRequiredItemType()) {
                        button = inHandItem;
                        ItemStack mainHandItem = playerMenuUtility.getOwner().getInventory().getItemInMainHand();
                        if (!Utils.isItemEmptyOrNull(mainHandItem)){
                            int quality = SmithingItemTreatmentManager.getInstance().getItemsQuality(mainHandItem);
                            SmithingItemTreatmentManager.getInstance().setItemsQuality(button, quality);

//                        ItemAttributesManager.getInstance().setDefaultStats(mainHandItem, ItemAttributesManager.getInstance().getDefaultStats(mainHandItem));
//                        ItemAttributesManager.getInstance().setStats(mainHandItem, ItemAttributesManager.getInstance().getCurrentStats(mainHandItem));
                        }
                    } else {
                        button = new ItemStack(((ItemImprovementRecipe) recipe).getRequiredItemType());
                    }
                } else if (recipe instanceof ItemClassImprovementRecipe){
                    ItemStack inHandItem = playerMenuUtility.getOwner().getInventory().getItemInMainHand().clone();
                    if (EquipmentClass.getClass(inHandItem) == ((ItemClassImprovementRecipe) recipe).getRequiredEquipmentClass()) {
                        button = inHandItem;
                        ItemStack mainHandItem = playerMenuUtility.getOwner().getInventory().getItemInMainHand();
                        if (!Utils.isItemEmptyOrNull(mainHandItem)){
                            int quality = SmithingItemTreatmentManager.getInstance().getItemsQuality(mainHandItem);
                            SmithingItemTreatmentManager.getInstance().setItemsQuality(button, quality);

//                        ItemAttributesManager.getInstance().setDefaultStats(mainHandItem, ItemAttributesManager.getInstance().getDefaultStats(mainHandItem));
//                        ItemAttributesManager.getInstance().setStats(mainHandItem, ItemAttributesManager.getInstance().getCurrentStats(mainHandItem));
                        }
                    } else {
                        Material baseType;
                        switch (((ItemClassImprovementRecipe) recipe).getRequiredEquipmentClass()){
                            case AXE: baseType = Material.IRON_AXE; break;
                            case BOW: baseType = Material.BOW; break;
                            case HOE: baseType = Material.IRON_HOE; break;
                            case BOOTS: baseType = Material.IRON_BOOTS; break;
                            case SWORD: baseType = Material.IRON_SWORD; break;
                            case ELYTRA: baseType = Material.ELYTRA; break;
                            case HELMET: baseType = Material.IRON_HELMET; break;
                            case SHEARS: baseType = Material.SHEARS; break;
                            case SHIELD: baseType = Material.SHIELD; break;
                            case SHOVEL: baseType = Material.IRON_SHOVEL; break;
                            case PICKAXE: baseType = Material.IRON_PICKAXE; break;
                            case TRIDENT: baseType = Material.TRIDENT; break;
                            case TRINKET: baseType = Material.GOLD_NUGGET; break;
                            case CROSSBOW: baseType = Material.CROSSBOW; break;
                            case LEGGINGS: baseType = Material.IRON_LEGGINGS; break;
                            case CHESTPLATE: baseType = Material.IRON_CHESTPLATE; break;
                            case FISHING_ROD: baseType = Material.FISHING_ROD; break;
                            case FLINT_AND_STEEL: baseType = Material.FLINT_AND_STEEL; break;
                            default: baseType = Material.PAPER;
                        }
                        button = new ItemStack(baseType);
                    }
                }
                if (button != null){
                    ItemMeta buttonMeta = button.getItemMeta();
                    if (buttonMeta != null){
                        List<String> buttonLore = new ArrayList<>();

                        if (recipe instanceof ItemCraftingRecipe){
                            if (((ItemCraftingRecipe) recipe).getRequiredToolId() >= 0){
                                String specialLine = toolSpecialLines.get(((ItemCraftingRecipe) recipe).getRequiredToolId());
                                if (specialLine != null){
                                    buttonLore.add(Utils.chat(specialLine));
                                } else if (tool_display_default != null) {
                                    buttonLore.add(Utils.chat(tool_display_default.replace("%tool%", "" + ((ItemCraftingRecipe) recipe).getRequiredToolId())));
                                }
                            }
                        }

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
                                if (advanced_crafting_preview && (recipe instanceof ItemCraftingRecipe)){
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
                        View intendedView = (recipe instanceof ItemCraftingRecipe) ? View.CRAFTING_RECIPES : View.TINKERING_RECIPES;
                        List<ItemStack> buttons = items.getOrDefault(intendedView, new ArrayList<>());
                        buttons.add(button);
                        if (isFavourited(playerMenuUtility.getOwner(), recipe.getName())) addFavouriteDisplay(button);
                        items.put(intendedView, buttons);
                    }
                }
            }
            setMenuItems();
        });
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

    private enum View{
        CRAFTING_RECIPES,
        TINKERING_RECIPES
    }

    private static Map<Integer, String> getToolSpecialLines(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        Map<Integer, String> lines = new HashMap<>();
        ConfigurationSection section = config.getConfigurationSection("tool_displays");
        if (section != null){
            for (String tool : section.getKeys(false)){
                try {
                    int toolId = Integer.parseInt(tool);
                    String value = TranslationManager.getInstance().translatePlaceholders(config.getString("tool_displays." + tool));
                    lines.put(toolId, value);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getServer().getLogger().warning("Tool id in config.yml tool_displays." + tool + " is not a valid integer");
                }
            }
        }
        return lines;
    }

    public boolean isFavourited(Player p, String recipe){
        return getFavourites(p).contains(recipe);
    }

    public List<String> getFavourites(Player p){
        List<String> favourites = new ArrayList<>();
        if (p.getPersistentDataContainer().has(favouriteRecipesKey, PersistentDataType.STRING)){
            String value = p.getPersistentDataContainer().get(favouriteRecipesKey, PersistentDataType.STRING);
            if (value == null) return favourites;
            favourites.addAll(Arrays.asList(value.split("<splitter>")));
        }
        return favourites;
    }

    public void setFavourites(Player p, List<String> favourites){
        p.getPersistentDataContainer().set(favouriteRecipesKey, PersistentDataType.STRING, String.join("<splitter>", favourites));
    }

    public void addFavourite(Player p, String recipe){
        List<String> favourites = getFavourites(p);
        favourites.add(recipe);
        setFavourites(p, favourites);
    }

    public void removeFavourite(Player p, String favourite){
        List<String> favourites = getFavourites(p);
        favourites.remove(favourite);
        setFavourites(p, favourites);
    }
}
