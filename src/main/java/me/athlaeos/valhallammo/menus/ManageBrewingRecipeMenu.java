package me.athlaeos.valhallammo.menus;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.managers.DynamicItemModifierManager;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicShapedRecipe;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

public class ManageShapedRecipeMenu extends Menu{
    private View view = View.VIEW_RECIPES;
    private final NamespacedKey buttonNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "button_recipe_name");
    private DynamicShapedRecipe currentRecipe = null;
    private DynamicShapedRecipe oldRecipe = null;

    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;
    private int currentPage = 0;

    private final ItemStack confirmButton = Utils.createItemStack(Material.STRUCTURE_VOID,
            Utils.chat("&b&lSave"),
            null);
    private final ItemStack cancelButton = Utils.createItemStack(Material.BARRIER,
            Utils.chat("&cDelete"),
            null);
    private final ItemStack createNewButton = Utils.createItemStack(Material.LIME_STAINED_GLASS_PANE,
            Utils.chat("&b&lNew"),
            null);
    private final ItemStack modifierStrengthButton = Utils.createItemStack(
            Material.NETHER_STAR,
            Utils.chat("&6&lStrength"),
            new ArrayList<>()
    );
    private final ItemStack modifierPriorityButton = Utils.createItemStack(
            Material.CLOCK,
            Utils.chat("&6&lPriority"),
            new ArrayList<>()
    );

    private final ItemStack dynamicModifierButton = Utils.createItemStack(
            Material.BOOK,
            Utils.chat("&b&lDynamic Modifiers"),
            new ArrayList<>()
    );
    private final ItemStack requireExactMetaButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lRequire Precise Meta"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, during crafting the items used in the crafting table " +
                    "will need to have item metas exactly matching the items given in the recipe. Recommended if you're" +
                    " using custom materials. -n" +
                    "If false, only the item types will need to match. Meta is ignored."), 40)
    );
    private final ItemStack toggleGridSizeButton = Utils.createItemStack(
            Material.CRAFTING_TABLE,
            Utils.chat("&7&lToggle Grid Size"),
            Utils.separateStringIntoLines(Utils.chat("&7Toggle if you want the recipe to be available in a 2x2" +
                    " grid or a 3x3 grid."), 40)
    );
    private final ItemStack requireCustomToolsButton = Utils.createItemStack(
            Material.CRAFTING_TABLE,
            Utils.chat("&7&lRequire Custom Tools"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, if a recipe contains tools/armor, they need" +
                    " to have ValhallaMMO custom characteristics. Such as quality, durability, or custom attributes." +
                    " If 'Require Precise Meta' is also enabled, these tools/armor pieces will be ignored and do not" +
                    " need to exactly match.-n" +
                    " If false, any tool/armor piece can be used even if they have no custom traits.-n" +
                    " Tools/Armor need to be fully repaired regardless of the option used for crafting."), 40)
    );
    private final ItemStack tinkerFirstItemButton = Utils.createItemStack(
            Material.ANVIL,
            Utils.chat("&7&lTinker First Item"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, instead of crafting a new item all-together " +
                    "the first item in the crafting grid is used to produce an output. This is the " +
                    "vanilla crafting table alternative for ValhallaMMO's custom tinkering mechanic. "), 40)
    );
    private final ItemStack returnToMenuButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lReturn to menu"),
            new ArrayList<>()
    );
    private final ItemStack skillTypeButton = Utils.createItemStack(
            Material.SMITHING_TABLE,
            Utils.chat("&7&lEXP type on completion"),
            new ArrayList<>()
    );

    private final Integer[] grid3x3 = new Integer[]{10, 11, 12, 19, 20, 21, 28, 29, 30};
    private final Integer[] grid2x2 = new Integer[]{10, 11, 19, 20};
    private boolean requireExactMeta = false;
    private boolean requireCustomTools = true;
    private SkillType skillType = SkillType.SMITHING;
    private boolean tinkerFirstItem = false;
    private Map<Integer, ItemStack> exactItems = new HashMap<>();
    private ItemStack result = new ItemStack(Material.WOODEN_SWORD);
    private boolean is3x3 = true;
    private DynamicItemModifier currentModifier = null;
    private double modifierStrength = 0D;
    private Collection<DynamicItemModifier> currentModifiers = new HashSet<>();
    private ModifierPriority priority = ModifierPriority.NEUTRAL;

    public ManageShapedRecipeMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
    }

    public ManageShapedRecipeMenu(PlayerMenuUtility playerMenuUtility, DynamicShapedRecipe recipe){
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        if (recipe != null){
            currentRecipe = recipe.clone();
            oldRecipe = recipe.clone();
            view = View.CREATE_RECIPE;
            requireExactMeta = recipe.isUseMetadata();
            requireCustomTools = recipe.isRequireCustomTools();
            tinkerFirstItem = recipe.isTinkerFirstItem();
            skillType = recipe.getExpReward();

            int index = 0;
            for (Integer i : recipe.getExactItems().keySet()){
                exactItems.put(grid3x3[index], recipe.getExactItems().get(i));
                index++;
            }

            is3x3 = recipe.getExactItems().size() == 9 || recipe.getExactItems().size() == 0;
        }
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7Create New Shaped Recipe");
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
            } else if (clickedItem.equals(nextPageButton)){
                currentPage++;
            } else if (clickedItem.equals(previousPageButton)){
                currentPage--;
            } else if (clickedItem.equals(dynamicModifierButton)){
                view = View.VIEW_MODIFIERS;
            } else if (clickedItem.equals(requireExactMetaButton)){
                requireExactMeta = !requireExactMeta;
                if (currentRecipe != null){
                    currentRecipe.setUseMetadata(requireExactMeta);
                }
            } else if (e.getCurrentItem().equals(skillTypeButton)) {
                switch (skillType){
                    case SMITHING:
                        skillType = SkillType.ENCHANTING;
                        skillTypeButton.setType(Material.ENCHANTING_TABLE);
                        break;
                    case ENCHANTING:
                        skillType = SkillType.ALCHEMY;
                        skillTypeButton.setType(Material.BREWING_STAND);
                        break;
                    default:
                        skillType = SkillType.SMITHING;
                        skillTypeButton.setType(Material.ANVIL);
                        break;
                }
                currentRecipe.setExpReward(skillType);
            } else if (clickedItem.equals(requireCustomToolsButton)){
                requireCustomTools = !requireCustomTools;
                if (currentRecipe != null){
                    currentRecipe.setRequireCustomTools(requireCustomTools);
                }
            } else if (clickedItem.equals(tinkerFirstItemButton)){
                tinkerFirstItem = !tinkerFirstItem;
                if (currentRecipe != null){
                    currentRecipe.setUseMetadata(tinkerFirstItem);
                }
            } else if (clickedItem.equals(toggleGridSizeButton)){
                is3x3 = !is3x3;
            } else if (clickedItem.equals(confirmButton)){
                switch (view){
                    case NEW_MODIFIER: {
                        if (currentModifier != null){
                            for (DynamicItemModifier mod : currentModifiers){
                                if (mod.getName().equalsIgnoreCase(currentModifier.getName())){
                                    currentModifiers.remove(mod);
                                    break;
                                }
                            }
                            currentModifier.setPriority(priority);
                            priority = ModifierPriority.NEUTRAL;
                            currentModifier.setStrength(modifierStrength);
                            modifierStrength = 0D;
                            currentModifiers.add(currentModifier);
                            currentModifier = null;
                            view = View.VIEW_MODIFIERS;
                        }
                        break;
                    }
                    case PICK_MODIFIERS: {
                        view = View.VIEW_MODIFIERS;
                        break;
                    }
                    case VIEW_MODIFIERS: {
                        currentRecipe.setModifiers(currentModifiers);
                        view = View.CREATE_RECIPE;
                        break;
                    }
                    case CREATE_RECIPE: {
                        if (currentRecipe != null){
                            DynamicShapedRecipe backupRecipe = currentRecipe.clone();
                            currentRecipe = generateRecipe();
                            if (currentRecipe != null){
                                CustomRecipeManager.getInstance().update(oldRecipe, currentRecipe);
                                currentRecipe = null;
                                view = View.VIEW_RECIPES;
                            } else {
                                e.getWhoClicked().sendMessage(Utils.chat("&cPlease add some ingredients"));
                                currentRecipe = backupRecipe;
                            }
                        }
                        break;
                    }
                }
            } else if (clickedItem.equals(createNewButton)){
                view = View.PICK_MODIFIERS;
            } else if (clickedItem.equals(modifierStrengthButton)){
                handleModifierStrength(e.getClick());
            } else if (clickedItem.equals(modifierPriorityButton)){
                handleModifierPriority(e.getClick());
            } else if (clickedItem.equals(cancelButton)){
                switch (view){
                    case NEW_MODIFIER:{
                        if (currentModifier != null){
                            currentModifiers.remove(currentModifier);
                            view = View.VIEW_MODIFIERS;
                        }
                        break;
                    }
                    case CREATE_RECIPE:{
                        if (currentRecipe != null){
                            CustomRecipeManager.getInstance().unregister(oldRecipe);
                            view = View.VIEW_RECIPES;
                        }
                    }
                }
            }
            if (clickedItem.getItemMeta() != null){
                if (clickedItem.getItemMeta().getPersistentDataContainer().has(buttonNameKey, PersistentDataType.STRING)){
                    switch (view){
                        case VIEW_RECIPES:
                            DynamicShapedRecipe shapedRecipe = CustomRecipeManager.getInstance().getDynamicShapedRecipe(
                                    clickedItem.getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING));
                            currentRecipe = shapedRecipe.clone();
                            oldRecipe = shapedRecipe.clone();
                            view = View.CREATE_RECIPE;
                            requireExactMeta = shapedRecipe.isUseMetadata();
                            requireCustomTools = shapedRecipe.isRequireCustomTools();
                            tinkerFirstItem = shapedRecipe.isTinkerFirstItem();

                            int index = 0;
                            for (Integer i : shapedRecipe.getExactItems().keySet()){
                                exactItems.put(grid3x3[index], shapedRecipe.getExactItems().get(i));
                                index++;
                            }

                            is3x3 = shapedRecipe.getExactItems().size() == 9 || shapedRecipe.getExactItems().size() == 0;
                            break;
                        case PICK_MODIFIERS:
                            currentModifier = DynamicItemModifierManager.getInstance().createModifier(
                                    clickedItem.getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING),
                                    0D,
                                    ModifierPriority.NEUTRAL
                            );
                            currentModifier.setStrength(currentModifier.getDefaultStrength());
                            modifierStrength = currentModifier.getDefaultStrength();
                            view = View.NEW_MODIFIER;
                            break;
                        case VIEW_MODIFIERS:
                            String clickedModifier = clickedItem.getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING);
                            for (DynamicItemModifier modifier : currentModifiers){
                                if (modifier.getName().equals(clickedModifier)){
                                    currentModifier = modifier;
                                    modifierStrength = modifier.getStrength();
                                    priority = modifier.getPriority();
                                    view = View.NEW_MODIFIER;
                                    break;
                                }
                            }
                            break;
                    }
                }
            }
        }

        if (view == View.CREATE_RECIPE){
            if (!(e.getClickedInventory() instanceof PlayerInventory)){
                e.setCancelled(true);
                if (!Utils.isItemEmptyOrNull(e.getCursor())){
                    if (e.getSlot() == 22){
                        if (!tinkerFirstItem){
                            if (currentRecipe != null){
                                result = e.getCursor().clone();
                            }
                        }
                    } else {
                        List<Integer> grid = Arrays.asList(grid3x3);
                        if (grid.contains(e.getSlot())){
                            ItemStack itemToPut = e.getCursor().clone();
                            if (!Utils.isItemEmptyOrNull(itemToPut)){
                                itemToPut.setAmount(1);
                                exactItems.put(e.getSlot(), itemToPut.clone());
                            }
                        }
                    }
                } else {
                    List<Integer> grid = (is3x3) ? Arrays.asList(grid3x3) : Arrays.asList(grid2x2);
                    if (grid.contains(e.getSlot())){
                        exactItems.remove(e.getSlot());
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
                    List<Integer> grid = Arrays.asList(grid3x3);
                    for (Integer slot : e.getRawSlots()){
                        if (grid.contains(slot)){
                            ItemStack itemToPut = e.getCursor().clone();
                            if (!Utils.isItemEmptyOrNull(itemToPut)){
                                itemToPut.setAmount(1);
                                exactItems.put(slot, itemToPut.clone());
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
            inventory.setItem(i, new ItemStack(Material.GRAY_STAINED_GLASS_PANE));
        }
        switch (view){
            case VIEW_RECIPES: setShapedRecipesView();
                return;
            case CREATE_RECIPE: setCreateShapedRecipeView();
                return;
            case VIEW_MODIFIERS: setViewModifiersView();
                return;
            case PICK_MODIFIERS: setPickModifiersView();
                return;
            case NEW_MODIFIER: setNewModifierView();
        }
    }

    private void setNewModifierView(){
        if (currentModifier != null){
            List<String> modifierIconLore = new ArrayList<>(Utils.separateStringIntoLines(currentModifier.toString(), 40));
            modifierIconLore.add(Utils.chat("&8&m                                        "));
            modifierIconLore.addAll(Utils.separateStringIntoLines(currentModifier.getDescription(), 40));
            ItemStack modifierButton = Utils.createItemStack(currentModifier.getIcon(),
                    Utils.chat(currentModifier.getDisplayName()),
                    modifierIconLore);

            List<String> strengthButtonLore = new ArrayList<>(Utils.separateStringIntoLines(currentModifier.toString(), 40));
            ItemMeta strengthButtonMeta = modifierStrengthButton.getItemMeta();
            assert strengthButtonMeta != null;
            strengthButtonMeta.setLore(strengthButtonLore);
            modifierStrengthButton.setItemMeta(strengthButtonMeta);

            List<String> modifierPriorityLore = new ArrayList<>(Utils.separateStringIntoLines(priority.getDescription().replace("%priority%", priorityToNumber(priority)), 40));
            ItemMeta priorityButtonMeta = modifierPriorityButton.getItemMeta();
            assert priorityButtonMeta != null;
            priorityButtonMeta.setLore(modifierPriorityLore);
            modifierPriorityButton.setItemMeta(priorityButtonMeta);

            inventory.setItem(13, modifierButton);
            inventory.setItem(30, modifierPriorityButton);
            inventory.setItem(32, modifierStrengthButton);
        }
        inventory.setItem(53, confirmButton);
        inventory.setItem(45, cancelButton);
    }

    private void setPickModifiersView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        Map<String, DynamicItemModifier> modifiers = DynamicItemModifierManager.getInstance().getModifiers();
        List<String> currentStringModifiers = currentModifiers.stream().map(DynamicItemModifier::getName).collect(Collectors.toList());
        List<ItemStack> totalModifierButtons = new ArrayList<>();
        for (String modifier : modifiers.keySet()){
            if (currentStringModifiers.contains(modifier)) continue;
            DynamicItemModifier currentModifier = DynamicItemModifierManager.getInstance().createModifier(modifier, 0D, ModifierPriority.NEUTRAL);
            List<String> modifierIconLore = new ArrayList<>(Utils.separateStringIntoLines(currentModifier.getDescription(), 40));
            ItemStack modifierIcon = Utils.createItemStack(currentModifier.getIcon(),
                    currentModifier.getDisplayName(),
                    modifierIconLore);

            ItemMeta modifierIconMeta = modifierIcon.getItemMeta();
            assert modifierIconMeta != null;
            modifierIconMeta.getPersistentDataContainer().set(buttonNameKey, PersistentDataType.STRING, currentModifier.getName());
            modifierIcon.setItemMeta(modifierIconMeta);
            totalModifierButtons.add(modifierIcon);
        }
        totalModifierButtons.sort(Comparator.comparing(ItemStack::getType));
        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, totalModifierButtons);

        if (currentPage > pages.size()) currentPage = pages.size();
        if (currentPage < 1) currentPage = 1;

        if (pages.size() > 0){
            for (ItemStack i : pages.get(currentPage - 1)){
                inventory.addItem(i);
            }
        }
        inventory.setItem(45, previousPageButton);
        inventory.setItem(53, nextPageButton);
        inventory.setItem(49, confirmButton);
    }

    private void setViewModifiersView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<DynamicItemModifier> modifiers = currentModifiers.stream().limit(45).sorted(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating())).collect(Collectors.toList());
        for (DynamicItemModifier modifier : modifiers){
            List<String> modifierIconLore = new ArrayList<>(Utils.separateStringIntoLines(Utils.chat(modifier.toString()), 40));
            modifierIconLore.add(Utils.chat("&8&m                            "));
            modifierIconLore.addAll(Utils.separateStringIntoLines(modifier.getDescription(), 40));
            ItemStack modifierIcon = Utils.createItemStack(modifier.getIcon(),
                    modifier.getDisplayName(),
                    modifierIconLore);

            ItemMeta modifierIconMeta = modifierIcon.getItemMeta();
            assert modifierIconMeta != null;
            modifierIconMeta.getPersistentDataContainer().set(buttonNameKey, PersistentDataType.STRING, modifier.getName());
            modifierIcon.setItemMeta(modifierIconMeta);
            inventory.addItem(modifierIcon);
        }
        if (currentModifiers.size() <= 44){
            inventory.addItem(createNewButton);
        }
        inventory.setItem(49, confirmButton);
    }

    private void setCreateShapedRecipeView(){
        if (currentRecipe != null){
            requireExactMeta = currentRecipe.isUseMetadata();
            requireCustomTools = currentRecipe.isRequireCustomTools();
            currentModifiers = new HashSet<>(currentRecipe.getItemModifiers());
            skillType = currentRecipe.getExpReward();
        }

        List<String> modifierButtonLore = new ArrayList<>();
        List<DynamicItemModifier> modifiers = new ArrayList<>(currentModifiers);
        modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
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

        ItemMeta expSkillRewardMeta = skillTypeButton.getItemMeta();
        assert expSkillRewardMeta != null;
        expSkillRewardMeta.setDisplayName(Utils.chat(String.format("&e&lExp type on craft: %s",
                ((skillType == SkillType.SMITHING) ? "Smithing" : ((skillType == SkillType.ENCHANTING) ? "Enchanting" :
                        (skillType == SkillType.ALCHEMY) ? "Alchemy" : "Uhh??")))));
        skillTypeButton.setItemMeta(expSkillRewardMeta);

        ItemMeta requireCustomToolsButtonMeta = requireCustomToolsButton.getItemMeta();
        assert requireCustomToolsButtonMeta != null;
        requireCustomToolsButtonMeta.setDisplayName(Utils.chat("&7Require Custom Tools: &e" + ((requireCustomTools) ? "Yes" : "No")));
        requireCustomToolsButton.setItemMeta(requireCustomToolsButtonMeta);

        ItemMeta gridButtonMeta = toggleGridSizeButton.getItemMeta();
        assert gridButtonMeta != null;
        gridButtonMeta.setDisplayName(Utils.chat("&7Grid Size: &e" + ((is3x3) ? "3x3" : "2x2")));
        toggleGridSizeButton.setItemMeta(gridButtonMeta);

        ItemMeta centerTinkerButtonMeta = tinkerFirstItemButton.getItemMeta();
        assert centerTinkerButtonMeta != null;
        centerTinkerButtonMeta.setDisplayName(Utils.chat("&7Tinker Center Item: &e" + ((tinkerFirstItem) ? "Yes" : "No")));
        tinkerFirstItemButton.setItemMeta(centerTinkerButtonMeta);

        // 10,11,12, 19,20,21, 28,29,30
        for (Integer i : grid3x3){
            inventory.setItem(i, new ItemStack(Material.RED_STAINED_GLASS_PANE));
        }
        List<Integer> grid = (is3x3) ? Arrays.asList(grid3x3) : Arrays.asList(grid2x2);
        for (Integer i : grid) {
            ItemStack item = exactItems.get(i);
            if (item != null) item.setAmount(1);
            inventory.setItem(i, item);
        }

        inventory.setItem(4, skillTypeButton);
        if (!tinkerFirstItem) inventory.setItem(22, result);
        inventory.setItem(31, toggleGridSizeButton);
        inventory.setItem(24, dynamicModifierButton);
        inventory.setItem(25, requireExactMetaButton);
        inventory.setItem(33, requireCustomToolsButton);
        inventory.setItem(34, tinkerFirstItemButton);

        inventory.setItem(45, cancelButton);
        inventory.setItem(53, confirmButton);
    }

    private void setShapedRecipesView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> totalRecipeButtons = new ArrayList<>();
        for (DynamicShapedRecipe recipe : CustomRecipeManager.getInstance().getShapedRecipes().values()){
            ItemStack resultButton = new ItemStack(recipe.getRecipe().getResult());
            ItemMeta resultMeta = resultButton.getItemMeta();
            assert resultMeta != null;
            resultMeta.setDisplayName(recipe.getName());
            List<String> resultLore = new ArrayList<>();
            for (String shapeLine : recipe.getRecipe().getShape()){
                resultLore.add(Utils.chat("&7[&e" + shapeLine + "&7]&7"));
            }
            for (Character c : recipe.getRecipe().getIngredientMap().keySet()){
                if (recipe.getRecipe().getIngredientMap().get(c) == null) continue;
                resultLore.add(Utils.chat("&e" + c + "&7: &e" + Utils.getItemName(recipe.getRecipe().getIngredientMap().get(c))));
            }
            resultLore.add(Utils.chat("&8&m                                      "));
            List<DynamicItemModifier> modifiers = new ArrayList<>(recipe.getItemModifiers());
            modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
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

    private enum View{
        VIEW_RECIPES,
        CREATE_RECIPE,
        PICK_MODIFIERS,
        VIEW_MODIFIERS,
        NEW_MODIFIER
    }

    private void updateExactItemsToInventory(){
        for (Integer i : grid3x3) {
            ItemStack item = inventory.getItem(i);
            if (item != null) item.setAmount(1);
            if (Utils.isItemEmptyOrNull(item)) item = null;
            exactItems.put(i, item);
        }
    }

    private DynamicShapedRecipe generateRecipe(){
        ItemStack result = (tinkerFirstItem) ? exactItems.get(10) : this.result;
        if (Utils.isItemEmptyOrNull(result)){
            return null;
        }
        if (this.exactItems.size() == 0) {
            return null;
        }
        NamespacedKey recipeKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_" + currentRecipe.getName());
        ShapedRecipe shapedRecipe = new ShapedRecipe(recipeKey, result);

        Map<Integer, ItemStack> exactItems = new HashMap<>();
        List<Integer> grid = (is3x3) ? Arrays.asList(grid3x3) : Arrays.asList(grid2x2);
        int index = 0;
        for (Integer i : grid) {
            ItemStack item = this.exactItems.get(i);
            if (item != null) item.setAmount(1);
            if (Utils.isItemEmptyOrNull(item)) item = null;
            exactItems.put(index, item);
            index++;
        }
        Set<ItemStack> uniqueIngredients = new HashSet<>(exactItems.values());
        BiMap<Character, ItemStack> ingredientMap = getIngredientMap(uniqueIngredients);
        String[] shape = (is3x3) ? new String[]{"", "", ""} : new String[]{"", ""};
        index = 0;
        for (int row = 0; row < shape.length; row++){
            StringBuilder rowShape = new StringBuilder();
            for (int column = 0; column < shape.length; column++){
                Character charToAppend = ingredientMap.inverse().get(exactItems.get(index));
                if (charToAppend != null){
                    rowShape.append(charToAppend);
                } else {
                    rowShape.append(' ');
                }
                index++;
            }
            shape[row] = rowShape.toString();
        }

        shapedRecipe.shape(shape);
        for (Character c : ingredientMap.keySet()){
            shapedRecipe.setIngredient(c, ingredientMap.get(c).getType());
        }

        return new DynamicShapedRecipe(currentRecipe.getName(), shapedRecipe, exactItems, requireExactMeta, requireCustomTools, tinkerFirstItem, currentModifiers, skillType);
    }

    private BiMap<Character, ItemStack> getIngredientMap(Set<ItemStack> items){
        BiMap<Character, ItemStack> ingredientMap = HashBiMap.create();
        for (ItemStack i : items){
            if (Utils.isItemEmptyOrNull(i)) continue;
            char possibleCharacter = Utils.getItemName(i).toUpperCase().charAt(0);
            if (ingredientMap.containsKey(possibleCharacter)){
                possibleCharacter = i.getType().toString().toUpperCase().charAt(0);
                if (ingredientMap.containsKey(possibleCharacter)){
                    for (Character c : Arrays.asList('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I')){
                        if (!ingredientMap.containsKey(c)){
                            ingredientMap.put(c, i);
                            break;
                        }
                    }
                } else {
                    ingredientMap.put(possibleCharacter, i);
                }
            } else {
                ingredientMap.put(possibleCharacter, i);
            }
        }
        return ingredientMap;
    }

    private void handleModifierPriority(ClickType clickType){
        switch (clickType){
            case LEFT: {
                switch (priority){
                    case NEUTRAL: priority = ModifierPriority.SOON;
                        break;
                    case SOON: priority = ModifierPriority.SOONEST;
                        break;
                    case SOONEST: priority = ModifierPriority.LAST;
                        break;
                    case LAST: priority = ModifierPriority.LATER;
                        break;
                    case LATER: priority = ModifierPriority.NEUTRAL;
                        break;
                }
                break;
            }
            case RIGHT:{
                switch (priority){
                    case NEUTRAL: priority = ModifierPriority.LATER;
                        break;
                    case SOON: priority = ModifierPriority.NEUTRAL;
                        break;
                    case SOONEST: priority = ModifierPriority.SOON;
                        break;
                    case LAST: priority = ModifierPriority.SOONEST;
                        break;
                    case LATER: priority = ModifierPriority.LAST;
                        break;
                }
                break;
            }
        }
        currentModifier.setPriority(priority);
    }

    private void handleModifierStrength(ClickType clickType){
        if (currentModifier != null){
            switch (clickType){
                case LEFT: {
                    if (modifierStrength + currentModifier.getSmallStepIncrease() > currentModifier.getMaxStrength()) {
                        modifierStrength = currentModifier.getMaxStrength();
                    } else if (modifierStrength == currentModifier.getMaxStrength()){
                        modifierStrength = currentModifier.getMinStrength();
                    } else {
                        modifierStrength += currentModifier.getSmallStepIncrease();
                    }
                    break;
                }
                case RIGHT: {
                    if (modifierStrength - currentModifier.getSmallStepDecrease() < currentModifier.getMinStrength()){
                        modifierStrength = currentModifier.getMinStrength();
                    } else if (modifierStrength == currentModifier.getMinStrength()){
                        modifierStrength = currentModifier.getMaxStrength();
                    } else {
                        modifierStrength -= currentModifier.getSmallStepDecrease();
                    }
                    break;
                }
                case SHIFT_LEFT: {
                    if (modifierStrength + currentModifier.getBigStepIncrease() > currentModifier.getMaxStrength()) {
                        modifierStrength = currentModifier.getMaxStrength();
                    } else if (modifierStrength == currentModifier.getMaxStrength()){
                        modifierStrength = currentModifier.getMinStrength();
                    } else {
                        modifierStrength += currentModifier.getBigStepIncrease();
                    }
                    break;
                }
                case SHIFT_RIGHT: {
                    if (modifierStrength - currentModifier.getBigStepDecrease() < currentModifier.getMinStrength()){
                        modifierStrength = currentModifier.getMinStrength();
                    } else if (modifierStrength == currentModifier.getMinStrength()){
                        modifierStrength = currentModifier.getMaxStrength();
                    } else {
                        modifierStrength -= currentModifier.getBigStepDecrease();
                    }
                }
            }
            currentModifier.setStrength(modifierStrength);
        }
    }

    private String priorityToNumber(ModifierPriority priority){
        switch (priority){
            case SOONEST: return "5";
            case SOON: return "4";
            case NEUTRAL: return "3";
            case LATER: return "2";
            case LAST: return "1";
        }
        return "";
    }
}
