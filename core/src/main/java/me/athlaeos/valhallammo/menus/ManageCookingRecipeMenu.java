package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.*;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ManageCookingRecipeMenu extends Menu implements CraftingManagerMenu{
    private View view = View.VIEW_RECIPES;
    private final RecipeType type;
    private final NamespacedKey buttonNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "button_recipe_name");
    private DynamicCookingRecipe<?> currentRecipe = null;
    private DynamicCookingRecipe<?> oldRecipe = null;

    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;
    private int currentPage = 0;

    private final ItemStack confirmButton = Utils.createItemStack(Material.STRUCTURE_VOID,
            Utils.chat("&b&lSave"),
            null);
    private final ItemStack cancelButton = Utils.createItemStack(Material.BARRIER,
            Utils.chat("&cDelete"),
            null);

    private final ItemStack dynamicModifierButton = Utils.createItemStack(
            Material.BOOK,
            Utils.chat("&b&lDynamic Modifiers"),
            new ArrayList<>()
    );
    private final ItemStack requireExactMetaButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lRequire Precise Meta"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, the item placed on top of the campfire will need " +
                    "to exactly match this item. Tools/equipment are excluded from this rule. -n" +
                    "If false, only the item types will need to match. Meta is ignored."), 40)
    );
    private final ItemStack toggleCampfireModeButton = Utils.createItemStack(
            Material.CAMPFIRE,
            Utils.chat("&e&lWorks on either campfires"),
            Utils.separateStringIntoLines(Utils.chat("&7Toggle this if you want the recipe to work on either campfires," +
                    " on regular campfires only, or on soul campfires only. Do keep in mind two recipes " +
                    "using identical input requirements may clash with eachother regardless of mode."), 40)
    );
    private final ItemStack requireCustomToolsButton = Utils.createItemStack(
            Material.CRAFTING_TABLE,
            Utils.chat("&7&lRequire Custom Tool"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, if a recipe requires tools/armor, they need" +
                    " to have ValhallaMMO custom characteristics. Such as quality, durability, or custom attributes." +
                    " If 'Require Precise Meta' is also enabled, these tools/armor pieces will be ignored and do not" +
                    " need to exactly match.-n" +
                    " If false, any tool/armor piece can be used even if they have no custom traits.-n"), 40)
    );
    private final ItemStack tinkerItemButton = Utils.createItemStack(
            Material.ANVIL,
            Utils.chat("&7&lTinker Item"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, instead of creating a new item all-together " +
                    "the input item is also used as output. This is the " +
                    "vanilla campfire alternative for ValhallaMMO's custom tinkering mechanic. "), 40)
    );
    private final ItemStack timeButton = Utils.createItemStack(
            Material.CLOCK,
            Utils.chat("&7&lTime to cook:"),
            Utils.separateStringIntoLines(Utils.chat("&7Determines how long the item takes to finish on the campfire. " +
                    "20 ticks = 1 second, and for reference, regular campfire cooks usually take 600 ticks or 30 seconds. -n" +
                    "&eShift-click to increase or decrease by 5 seconds instead of half-seconds. "), 40)
    );
    private final ItemStack experienceButton = Utils.createItemStack(
            Material.CLOCK,
            Utils.chat("&7&lExperience to drop:"),
            Utils.separateStringIntoLines(Utils.chat("&7Determines how much experience the recipe should drop when " +
                    "it finishes cooking. Experience orbs can't hold fractions of exp, so this value represents an average. -n" +
                    "For example, 0.1 EXP averages on 1 exp every 10 cooks. -n" +
                    "&eShift-click to increase or decrease by 1 exp instead of 0.1 exp. "), 40)
    );
    private final ItemStack returnToMenuButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lReturn to menu"),
            new ArrayList<>()
    );

    private final ItemStack unlockedForEveryoneButton = Utils.createItemStack(
            Material.ARMOR_STAND,
            Utils.chat("&7&lUnlocked for everyone:"),
            new ArrayList<>()
    );

    private boolean requireExactMeta = false;
    private boolean requireCustomTools = true;
    private boolean tinkerItem = false;
    private boolean unlockedForEveryone = false;
    private float experience = 0.0f;
    private int time = 600;
    private int campfireMode = 0;
    private ItemStack input = new ItemStack(Material.IRON_ORE);
    private ItemStack result = new ItemStack(Material.IRON_INGOT);
    private List<DynamicItemModifier> currentModifiers = new ArrayList<>();

    public ManageCookingRecipeMenu(PlayerMenuUtility playerMenuUtility, RecipeType type) {
        super(playerMenuUtility);
        this.type = type;
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
    }

    public ManageCookingRecipeMenu(PlayerMenuUtility playerMenuUtility, RecipeType type, DynamicCookingRecipe<?> recipe){
        super(playerMenuUtility);
        this.type = type;
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        if (recipe != null){
            currentRecipe = recipe.clone();
            oldRecipe = recipe.clone();
            view = View.CREATE_RECIPE;
            requireExactMeta = recipe.isUseMetadata();
            requireCustomTools = recipe.requiresCustomTool();
            tinkerItem = recipe.isTinkerInput();
            unlockedForEveryone = recipe.isUnlockedForEveryone();
            result = recipe.getResult();
            input = recipe.getInput();
            time = recipe.getCookTime();
            experience = recipe.getExperience();
            currentModifiers = recipe.getModifiers();
            if (recipe instanceof DynamicCampfireRecipe){
                campfireMode = ((DynamicCampfireRecipe) recipe).getCampfireMode();
            }
        }
    }

    @Override
    public String getMenuName() {
        switch (type){
            case CAMPFIRE: return Utils.chat("&7Create New Campfire Recipe");
            case FURNACE: return Utils.chat("&7Create New Furnace Recipe");
            case SMOKER: return Utils.chat("&7Create New Smoker Recipe");
            case BLAST_FURNACE: return Utils.chat("&7Create New Blasting Recipe");
        }
        return Utils.chat("&7This should never be visible lol");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        if (!(e.getClickedInventory() instanceof PlayerInventory)){
            e.setCancelled(true);
        }
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem != null){
            if (clickedItem.equals(returnToMenuButton)) {
                new RecipeCategoryMenu(playerMenuUtility).open();
                return;
            } else if (clickedItem.equals(unlockedForEveryoneButton)){
                unlockedForEveryone = !unlockedForEveryone;
                if (currentRecipe != null){
                    currentRecipe.setUnlockedForEveryone(unlockedForEveryone);
                }
            } else if (clickedItem.equals(timeButton)){
                handleTimeButton(e.getClick());
            } else if (clickedItem.equals(experienceButton)){
                handleExperienceButton(e.getClick());
            } else if (clickedItem.equals(toggleCampfireModeButton)){
                handleCampfireModeButton(e.getClick());
            } else if (clickedItem.equals(nextPageButton)){
                currentPage++;
            } else if (clickedItem.equals(previousPageButton)){
                currentPage--;
            } else if (clickedItem.equals(dynamicModifierButton)){
                playerMenuUtility.setPreviousMenu(this);
                new DynamicModifierMenu(playerMenuUtility, currentModifiers).open();
                return;
            } else if (clickedItem.equals(requireExactMetaButton)){
                requireExactMeta = !requireExactMeta;
                if (currentRecipe != null){
                    currentRecipe.setUseMetadata(requireExactMeta);
                }
            } else if (clickedItem.equals(requireCustomToolsButton)){
                requireCustomTools = !requireCustomTools;
                if (currentRecipe != null){
                    currentRecipe.setRequireCustomTools(requireCustomTools);
                }
            } else if (clickedItem.equals(tinkerItemButton)){
                tinkerItem = !tinkerItem;
                if (currentRecipe != null){
                    currentRecipe.setUseMetadata(tinkerItem);
                }
            } else if (clickedItem.equals(confirmButton)){
                if (view == View.CREATE_RECIPE) {
                    if (currentRecipe != null) {
                        if (Utils.isItemEmptyOrNull(input) || Utils.isItemEmptyOrNull(result)){
                            e.getWhoClicked().sendMessage(Utils.chat("&cPlease add an input" + (tinkerItem ? "" : " and a result")));
                        } else {
                            switch (type){
                                case SMOKER: {
                                    currentRecipe = new DynamicSmokingRecipe(currentRecipe.getName(), input, result, time, experience, tinkerItem, requireExactMeta, requireCustomTools, currentModifiers);
                                    currentRecipe.setUnlockedForEveryone(unlockedForEveryone);
                                    CustomRecipeManager.getInstance().update(oldRecipe, currentRecipe);
                                    break;
                                }
                                case BLAST_FURNACE: {
                                    currentRecipe = new DynamicBlastingRecipe(currentRecipe.getName(), input, result, time, experience, tinkerItem, requireExactMeta, requireCustomTools, currentModifiers);
                                    currentRecipe.setUnlockedForEveryone(unlockedForEveryone);
                                    CustomRecipeManager.getInstance().update(oldRecipe, currentRecipe);
                                    break;
                                }
                                case FURNACE: {
                                    currentRecipe = new DynamicFurnaceRecipe(currentRecipe.getName(), input, result, time, experience, tinkerItem, requireExactMeta, requireCustomTools, currentModifiers);
                                    currentRecipe.setUnlockedForEveryone(unlockedForEveryone);
                                    CustomRecipeManager.getInstance().update(oldRecipe, currentRecipe);
                                    break;
                                }
                                case CAMPFIRE: {
                                    currentRecipe = new DynamicCampfireRecipe(currentRecipe.getName(), input, result, campfireMode, time, experience, tinkerItem, requireExactMeta, requireCustomTools, currentModifiers);
                                    currentRecipe.setUnlockedForEveryone(unlockedForEveryone);
                                    CustomRecipeManager.getInstance().update(oldRecipe, currentRecipe);
                                    break;
                                }
                            }
                            currentRecipe = null;
                            view = View.VIEW_RECIPES;
                        }
                    }
                }
            } else if (clickedItem.equals(cancelButton)){
                if (view == View.CREATE_RECIPE) {
                    if (currentRecipe != null) {
                        CustomRecipeManager.getInstance().unregister(oldRecipe);
                        view = View.VIEW_RECIPES;
                    }
                }
            }
            if (clickedItem.getItemMeta() != null){
                if (clickedItem.getItemMeta().getPersistentDataContainer().has(buttonNameKey, PersistentDataType.STRING)){
                    if (view == View.VIEW_RECIPES) {
                        DynamicCookingRecipe<?> cookingRecipe = CustomRecipeManager.getInstance().getCookingRecipes().get(
                                clickedItem.getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING));
                        if (cookingRecipe != null){
                            if ((type == RecipeType.SMOKER && cookingRecipe instanceof DynamicSmokingRecipe) ||
                                    (type == RecipeType.BLAST_FURNACE && cookingRecipe instanceof DynamicBlastingRecipe) ||
                                    (type == RecipeType.FURNACE && cookingRecipe instanceof DynamicFurnaceRecipe) ||
                                    (type == RecipeType.CAMPFIRE && cookingRecipe instanceof DynamicCampfireRecipe)){
                                if (e.getClick() == ClickType.MIDDLE){
                                    e.getWhoClicked().getInventory().addItem(cookingRecipe.getResult());
                                } else {
                                    currentRecipe = cookingRecipe.clone();
                                    oldRecipe = cookingRecipe.clone();
                                    view = View.CREATE_RECIPE;
                                    requireExactMeta = cookingRecipe.isUseMetadata();
                                    time = cookingRecipe.getCookTime();
                                    experience = cookingRecipe.getExperience();
                                    requireCustomTools = cookingRecipe.requiresCustomTool();
                                    tinkerItem = cookingRecipe.isTinkerInput();
                                    input = cookingRecipe.getInput();
                                    unlockedForEveryone = cookingRecipe.isUnlockedForEveryone();
                                    result = cookingRecipe.getResult();
                                    if (cookingRecipe instanceof DynamicCampfireRecipe){
                                        campfireMode = ((DynamicCampfireRecipe) cookingRecipe).getCampfireMode();
                                    }
                                }
                            } else {
                                e.getWhoClicked().sendMessage(Utils.chat("&cClicked recipe is not the right type of recipe, meaning this recipe was deleted and a different type of recipe with the same name was created"));
                            }
                        }
                    }
                }
            }
        }

        if (view == View.CREATE_RECIPE){
            if (!(e.getClickedInventory() instanceof PlayerInventory)){
                e.setCancelled(true);
                if (!Utils.isItemEmptyOrNull(e.getCursor())){
                    if (e.getSlot() == 22){
                        if (currentRecipe != null){
                            if (e.getClick() == ClickType.MIDDLE){
                                e.getWhoClicked().getInventory().addItem(input);
                            } else {
                                input = e.getCursor().clone();
                                input.setAmount(1);
                            }
                        }
                    } else if (e.getSlot() == 24){
                        if (!tinkerItem){
                            if (currentRecipe != null){
                                if (e.getClick() == ClickType.MIDDLE){
                                    e.getWhoClicked().getInventory().addItem(result);
                                } else {
                                    result = e.getCursor().clone();
                                }
                            }
                        }
                    }
                }
            } else {
                e.setCancelled(false);
            }
        }
        setMenuItems();
    }

    @Override
    public void handleMenu(InventoryDragEvent e) {
        if (view == View.CREATE_RECIPE){
            if (!(e.getInventory() instanceof PlayerInventory)){
                e.setCancelled(true);
                if (!Utils.isItemEmptyOrNull(e.getCursor())){
                    for (Integer slot : e.getRawSlots()){
                        if (slot == 22){
                            input = e.getCursor().clone();
                            input.setAmount(1);
                        } else if (slot == 24){
                            if (!tinkerItem){
                                result = e.getCursor().clone();
                            }
                        }
                    }
                }
            } else {
                e.setCancelled(false);
            }
        }
        setMenuItems();
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        for (int i = 0; i < getSlots(); i++){
            inventory.setItem(i, Utils.createItemStack(Material.GRAY_STAINED_GLASS_PANE, Utils.chat("&8 "), null));
        }
        switch (view){
            case VIEW_RECIPES: setRecipesView();
                return;
            case CREATE_RECIPE: setCreateShapedRecipeView();
        }
    }

    private void setCreateShapedRecipeView(){
        if (currentRecipe != null){
            requireExactMeta = currentRecipe.isUseMetadata();
            requireCustomTools = currentRecipe.requiresCustomTool();
            currentModifiers = new ArrayList<>(currentRecipe.getModifiers());
            unlockedForEveryone = currentRecipe.isUnlockedForEveryone();
        }

        List<String> modifierButtonLore = new ArrayList<>();
        List<DynamicItemModifier> modifiers = new ArrayList<>(currentModifiers);
        DynamicItemModifier.sortModifiers(modifiers);
        for (DynamicItemModifier modifier : modifiers){
            modifierButtonLore.addAll(Utils.separateStringIntoLines(Utils.chat("&7- " + modifier.toString()), 40));
        }
        ItemMeta modifierButtonMeta = dynamicModifierButton.getItemMeta();
        assert modifierButtonMeta != null;
        modifierButtonMeta.setLore(modifierButtonLore);
        dynamicModifierButton.setItemMeta(modifierButtonMeta);

        ItemMeta requireExactMetaButtonMeta = requireExactMetaButton.getItemMeta();
        assert requireExactMetaButtonMeta != null;
        requireExactMetaButtonMeta.setDisplayName(Utils.chat("&7Require exact item meta: &e" + ((requireExactMeta) ? "Yes" : "No")));
        requireExactMetaButton.setItemMeta(requireExactMetaButtonMeta);

        ItemMeta requireCustomToolButtonMeta = requireCustomToolsButton.getItemMeta();
        assert requireCustomToolButtonMeta != null;
        requireCustomToolButtonMeta.setDisplayName(Utils.chat("&7Require Custom Tool: &e" + ((requireCustomTools) ? "Yes" : "No")));
        requireCustomToolsButton.setItemMeta(requireCustomToolButtonMeta);

        ItemMeta tinkerItemButtonMeta = tinkerItemButton.getItemMeta();
        assert tinkerItemButtonMeta != null;
        tinkerItemButtonMeta.setDisplayName(Utils.chat("&7Tinker Item: &e" + ((tinkerItem) ? "Yes" : "No")));
        tinkerItemButton.setItemMeta(tinkerItemButtonMeta);

        ItemMeta unlockedForEveryoneMeta = unlockedForEveryoneButton.getItemMeta();
        assert unlockedForEveryoneMeta != null;
        unlockedForEveryoneMeta.setDisplayName(Utils.chat("&7Unlocked for everyone: &e" + ((unlockedForEveryone) ? "Yes" : "No")));
        unlockedForEveryoneButton.setItemMeta(unlockedForEveryoneMeta);

        ItemMeta campfireModeMeta = toggleCampfireModeButton.getItemMeta();
        assert campfireModeMeta != null;
        if (campfireMode == 0) campfireModeMeta.setDisplayName(Utils.chat("&eWorks on either campfires"));
        if (campfireMode == 1) campfireModeMeta.setDisplayName(Utils.chat("&6Works only on regular campfires"));
        if (campfireMode == 2) campfireModeMeta.setDisplayName(Utils.chat("&bWorks only on soul campfires"));
        toggleCampfireModeButton.setItemMeta(campfireModeMeta);

        ItemMeta timeButtonMeta = timeButton.getItemMeta();
        assert timeButtonMeta != null;
        timeButtonMeta.setDisplayName(Utils.chat(String.format("&7&lTime to cook: &e%.1f seconds", (time / 20D))));
        timeButton.setItemMeta(timeButtonMeta);

        ItemMeta experienceButtonMeta = experienceButton.getItemMeta();
        assert experienceButtonMeta != null;
        experienceButtonMeta.setDisplayName(Utils.chat(String.format("&7&lExperience to drop: &e%.1f", experience)));
        experienceButton.setItemMeta(experienceButtonMeta);

        inventory.setItem(22, input);
        if (!tinkerItem) inventory.setItem(24, result);

        if (currentRecipe instanceof DynamicCampfireRecipe) inventory.setItem(31, toggleCampfireModeButton);
        inventory.setItem(40, timeButton);
        inventory.setItem(41, experienceButton);
        inventory.setItem(25, dynamicModifierButton);
        inventory.setItem(21, requireExactMetaButton);
        inventory.setItem(12, requireCustomToolsButton);
        inventory.setItem(14, unlockedForEveryoneButton);
        inventory.setItem(15, tinkerItemButton);

        inventory.setItem(45, cancelButton);
        inventory.setItem(53, confirmButton);
    }

    private void setRecipesView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> totalRecipeButtons = new ArrayList<>();

        for (DynamicCookingRecipe<?> recipe : CustomRecipeManager.getInstance().getCookingRecipes().values()){
            if ((type == RecipeType.SMOKER && !(recipe instanceof DynamicSmokingRecipe)) ||
                    (type == RecipeType.BLAST_FURNACE && !(recipe instanceof DynamicBlastingRecipe)) ||
                    (type == RecipeType.FURNACE && !(recipe instanceof DynamicFurnaceRecipe)) ||
                    (type == RecipeType.CAMPFIRE && !(recipe instanceof DynamicCampfireRecipe))) continue;
            ItemStack resultButton = (recipe.isTinkerInput() ? new ItemStack(recipe.getInput()) : new ItemStack(recipe.getResult()));
            ItemMeta resultMeta = resultButton.getItemMeta();
            assert resultMeta != null;
            resultMeta.setDisplayName(recipe.getName());
            List<String> resultLore = new ArrayList<>();
            if (recipe.isTinkerInput()){
                resultLore.add(Utils.chat("&fTinkers input, no ingredient possible"));
            } else {
                resultLore.add(Utils.chat("&fProduces " + Utils.getItemName(recipe.getResult())));
                resultLore.add(Utils.chat("&fRequires " + (recipe.isUseMetadata() ? Utils.getItemName(recipe.getInput()) : Utils.toPascalCase(recipe.getInput().getType().toString().replace("_", " ")))));
            }
            resultLore.add(Utils.chat("&8&m                                      "));
            resultLore.add(Utils.chat(String.format("&7Time to cook: &e%.1f", (recipe.getCookTime() / 20D))));
            resultLore.add(Utils.chat(String.format("&7Experience to drop: &e%.1f", recipe.getExperience())));
            if (recipe instanceof DynamicCampfireRecipe) resultLore.add(Utils.chat(String.format("&7Works on &e%s", (((DynamicCampfireRecipe) recipe).getCampfireMode() == 0) ? "All Campfires" : (campfireMode == 1) ? "Regular Campfires Only" : "Soul Campfires Only")));

            resultLore.add(Utils.chat("&8&m                                      "));
            List<DynamicItemModifier> modifiers = new ArrayList<>(recipe.getModifiers());
            DynamicItemModifier.sortModifiers(modifiers);
            for (DynamicItemModifier modifier : modifiers){
                resultLore.addAll(Utils.separateStringIntoLines(Utils.chat("&7- " + modifier.toString()), 40));
            }
            if (resultMeta.getLore() != null){
                resultLore.addAll(resultMeta.getLore());
            }
            resultMeta.setLore(resultLore);
            resultMeta.getPersistentDataContainer().set(buttonNameKey, PersistentDataType.STRING, recipe.getName());
            resultButton.setItemMeta(resultMeta);
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

    @Override
    public void setResultModifiers(List<DynamicItemModifier> modifiers) {
        this.currentModifiers = modifiers;
        currentRecipe.setModifiers(modifiers);
    }

    private enum View{
        VIEW_RECIPES,
        CREATE_RECIPE
    }

    public enum RecipeType{
        CAMPFIRE,
        FURNACE,
        BLAST_FURNACE,
        SMOKER
    }

    private void handleTimeButton(ClickType clickType){
        switch (clickType){
            case LEFT: time += 10;
                break;
            case RIGHT: {
                if (time - 10 < 0) time = 0;
                else time -= 10;
                break;
            }
            case SHIFT_LEFT: time += 100;
                break;
            case SHIFT_RIGHT: {
                if (time - 100 < 0) time = 0;
                else time -= 100;
            }
        }
        if (currentRecipe != null){
            currentRecipe.setCookTime(time);
        }
    }

    private void handleExperienceButton(ClickType clickType){
        switch (clickType){
            case LEFT: experience += 0.1;
                break;
            case RIGHT: {
                if (experience - 0.1 < 0) experience = 0;
                else experience -= 0.1;
                break;
            }
            case SHIFT_LEFT: experience += 1;
                break;
            case SHIFT_RIGHT: {
                if (experience - 1 < 0) experience = 0;
                else experience -= 1;
            }
        }
        if (currentRecipe != null){
            currentRecipe.setExperience(experience);
        }
    }

    private void handleCampfireModeButton(ClickType clickType){
        switch (clickType){
            case LEFT:
            case SHIFT_LEFT: {
                if (campfireMode + 1 > 2) campfireMode = 0;
                else campfireMode += 1;
                break;
            }
            case RIGHT:
            case SHIFT_RIGHT: {
                if (campfireMode - 1 < 0) campfireMode = 2;
                else campfireMode -= 1;
                break;
            }
        }
        if (campfireMode == 1) toggleCampfireModeButton.setType(Material.SOUL_CAMPFIRE);
        else toggleCampfireModeButton.setType(Material.CAMPFIRE);
        if (currentRecipe != null && currentRecipe instanceof DynamicCampfireRecipe){
            ((DynamicCampfireRecipe) currentRecipe).setCampfireMode(campfireMode);
        }
    }
}
