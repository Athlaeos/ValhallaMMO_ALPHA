package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.AbstractCustomCraftingRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.ItemClassImprovementRecipe;
import me.athlaeos.valhallammo.items.BlockCraftStateValidationManager;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.items.blockstatevalidations.CraftValidation;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.utility.ItemUtils;
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
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class ManageClassTinkerRecipeMenu extends Menu implements CraftingManagerMenu{
    private View view = View.VIEW_RECIPES;
    private final NamespacedKey buttonNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "button_recipe_name");
    private ItemClassImprovementRecipe current_class_improvement_recipe = null;
    private ItemClassImprovementRecipe old_class_improvement_recipe = null;

    private CraftValidation currentValidation = null;
    private int currentValidationIndex = 0;

    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;
    private int currentPage = 0;

    private final ItemStack confirmButton = Utils.createItemStack(Material.STRUCTURE_VOID,
            Utils.chat("&b&lSave"),
            null);
    private final ItemStack cancelButton = Utils.createItemStack(Material.BARRIER,
            Utils.chat("&cDelete"),
            null);
    private final ItemStack exactMetaButton = Utils.createItemStack(
            Material.PAPER,
            Utils.chat("&7&lRequire exact meta:"),
            new ArrayList<>()
    );
    private final ItemStack unlockedForEveryoneButton = Utils.createItemStack(
            Material.ARMOR_STAND,
            Utils.chat("&7&lUnlocked for everyone:"),
            new ArrayList<>()
    );

    private final ItemStack dynamicModifierButton = Utils.createItemStack(
            Material.BOOK,
            Utils.chat("&b&lDynamic Modifiers"),
            new ArrayList<>()
    );
    private final ItemStack ingredientsButton = Utils.createItemStack(
            Material.KNOWLEDGE_BOOK,
            Utils.chat("&7&lIngredients"),
            new ArrayList<>()
    );
    private final ItemStack timeButton = Utils.createItemStack(
            Material.CLOCK,
            Utils.chat("&e&lTime to craft"),
            new ArrayList<>()
    );
    private final ItemStack breakStationButton = Utils.createItemStack(
            Material.IRON_PICKAXE,
            Utils.chat("&7&lDestroy station after usage"),
            new ArrayList<>()
    );
    private final ItemStack craftStationButton = Utils.createItemStack(
            Material.CRAFTING_TABLE,
            Utils.chat("&7&lCrafting station"),
            new ArrayList<>()
    );
    private final ItemStack craftValidationButton = Utils.createItemStack(
            Material.CRAFTING_TABLE,
            Utils.chat("&7&lAlways works"),
            new ArrayList<>()
    );
    private ItemStack improveResultButton = Utils.createItemStack(
            Material.WOODEN_SWORD,
            Utils.chat("&7&lApplicable on: "),
            new ArrayList<>()
    );
    private final ItemStack returnToMenuButton = Utils.createItemStack(
            Material.WRITABLE_BOOK,
            Utils.chat("&7&lReturn to menu"),
            new ArrayList<>()
    );

    private boolean breakStation = false;
    private int craftTime = 2500;
    private boolean exactMeta = true;
    private boolean unlockedForEveryone = false;
    private Material craftStation = Material.CRAFTING_TABLE;
    private Collection<ItemStack> customRecipeIngredients = new ArrayList<>();
    private Collection<DynamicItemModifier> currentModifiers = new HashSet<>();
    private EquipmentClass equipmentClass = null;

    public ManageClassTinkerRecipeMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        this.currentValidation = BlockCraftStateValidationManager.getInstance().getDefaultValidation();
    }

    public ManageClassTinkerRecipeMenu(PlayerMenuUtility playerMenuUtility, ItemClassImprovementRecipe recipe){
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
        if (recipe != null){
            current_class_improvement_recipe = recipe;
            view = View.CREATE_RECIPE;
            this.currentValidation = recipe.getValidation();
            this.craftTime = recipe.getCraftingTime();
            this.craftStation = recipe.getCraftingBlock();
            this.breakStation = recipe.breakStation();
            this.exactMeta = recipe.requireExactMeta();
            this.equipmentClass = recipe.getRequiredEquipmentClass();
            this.unlockedForEveryone = recipe.isUnlockedForEveryone();
        }
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7Create New Class Tinker Recipe");
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
        if (view == View.PICK_INGREDIENTS){
            e.setCancelled(false);
            if (e.getCurrentItem() != null || e.getSlot() >= 45){
                if (e.getCurrentItem().equals(confirmButton)){
                    e.setCancelled(true);
                }
            }
            new BukkitRunnable(){
                @Override
                public void run() {
                    if (view == View.PICK_INGREDIENTS){
                        ItemStack[] originalContents = Arrays.copyOfRange(inventory.getContents(), 0, 45);
                        Map<ItemStack, Integer> contents = new HashMap<>();
                        for (ItemStack i : originalContents){
                            if (i == null) continue;
                            if (i.equals(confirmButton)) continue;
                            inventory.removeItem(i);
                            int itemAmount = i.getAmount();
                            i.setAmount(1);
                            if (contents.containsKey(i)){
                                contents.put(i, contents.get(i) + itemAmount);
                            } else {
                                contents.put(i, itemAmount);
                            }
                        }
                        for (ItemStack i : contents.keySet()){
                            i.setAmount(contents.get(i));
                            inventory.addItem(i);
                        }
                    }
                }
            }.runTaskLater(ValhallaMMO.getPlugin(), 2L);
        }
        if (e.getCurrentItem() != null){
            if (e.getCurrentItem().equals(returnToMenuButton)) {
                new RecipeCategoryMenu(playerMenuUtility).open();
                return;
            } else if (e.getCurrentItem().equals(nextPageButton)){
                currentPage++;
            } else if (e.getCurrentItem().equals(previousPageButton)){
                currentPage--;
            } else if (e.getCurrentItem().equals(ingredientsButton)){
                view = View.PICK_INGREDIENTS;
                setMenuItems();
                return;
            } else if (e.getCurrentItem().equals(dynamicModifierButton)){
                playerMenuUtility.setPreviousMenu(this);
                new DynamicModifierMenu(playerMenuUtility, currentModifiers).open();
                return;
            } else if (e.getCurrentItem().equals(exactMetaButton)) {
                exactMeta = !exactMeta;
                current_class_improvement_recipe.setRequireExactMeta(exactMeta);
            } else if (e.getCurrentItem().equals(unlockedForEveryoneButton)) {
                unlockedForEveryone = !unlockedForEveryone;
                current_class_improvement_recipe.setUnlockedForEveryone(unlockedForEveryone);
            } else if (e.getCurrentItem().equals(timeButton)){
                handleTimeButton(e.getClick());
            } else if (e.getCurrentItem().equals(breakStationButton)){
                breakStation = !breakStation;
                if (current_class_improvement_recipe != null){
                    current_class_improvement_recipe.setBreakStation(breakStation);
                }
            } else if (e.getCurrentItem().equals(confirmButton)){
                switch (view){
                    case PICK_INGREDIENTS: {
                        ItemStack[] originalContents = Arrays.copyOfRange(inventory.getContents(), 0, 45);
                        Map<ItemStack, Integer> contents = new HashMap<>();
                        for (ItemStack i : originalContents){
                            if (i == null) continue;
                            if (i.equals(confirmButton)) continue;
                            inventory.removeItem(i);
                            int itemAmount = i.getAmount();
                            i.setAmount(1);
                            if (contents.containsKey(i)){
                                contents.put(i, contents.get(i) + itemAmount);
                            } else {
                                contents.put(i, itemAmount);
                            }
                        }
                        customRecipeIngredients = new HashSet<>();
                        for (ItemStack i : contents.keySet()){
                            i.setAmount(contents.get(i));
                            customRecipeIngredients.add(i);
                        }
                        if (current_class_improvement_recipe != null){
                            current_class_improvement_recipe.setIngredients(customRecipeIngredients);
                            view = View.CREATE_RECIPE;
                        }
                        setMenuItems();
                        return;
                    }
                    case CREATE_RECIPE: {
                        if (current_class_improvement_recipe != null){
                            if (equipmentClass != null){
                                current_class_improvement_recipe.setIngredients(customRecipeIngredients);
                                current_class_improvement_recipe.setModifiers(currentModifiers);
                                current_class_improvement_recipe.setCraftingTime(craftTime);
                                current_class_improvement_recipe.setRequiredEquipmentClass(equipmentClass);
                                current_class_improvement_recipe.setBreakStation(breakStation);
                                current_class_improvement_recipe.setValidation(currentValidation);
                                current_class_improvement_recipe.setCraftingBlock(craftStationButton.getType());
                                CustomRecipeManager.getInstance().update(old_class_improvement_recipe, current_class_improvement_recipe);
                                current_class_improvement_recipe = null;
                                view = View.VIEW_RECIPES;
                            }
                        }
                        break;
                    }
                }
            } else if (e.getCurrentItem().equals(craftValidationButton)){
                List<CraftValidation> availableValidations = BlockCraftStateValidationManager.getInstance().getValidations(craftStation);
                if (!availableValidations.isEmpty()){
                    currentValidationIndex = Math.max(0, availableValidations.indexOf(currentValidation));
                    if (currentValidationIndex + 1 < availableValidations.size()){
                        currentValidationIndex += 1;
                    } else {
                        currentValidationIndex = 0;
                    }
                    currentValidation = availableValidations.get(currentValidationIndex);
                    current_class_improvement_recipe.setValidation(currentValidation);
                }
            } else if (e.getCurrentItem().equals(cancelButton)){
                if (view == View.CREATE_RECIPE) {
                    if (current_class_improvement_recipe != null) {
                        CustomRecipeManager.getInstance().unregister(old_class_improvement_recipe);
                        view = View.VIEW_RECIPES;
                    }
                }
            }
            if (view == View.CREATE_RECIPE){
                if (!(e.getClickedInventory() instanceof PlayerInventory)){
                    e.setCancelled(true);
                    if (e.getCursor() != null){
                        if (e.getCursor().getType() != Material.AIR){
                            if (e.getSlot() == 14){
                                if (current_class_improvement_recipe != null){
                                    EquipmentClass equipmentClass = EquipmentClass.getClass(e.getCursor().getType());
                                    if (equipmentClass != null){
                                        setResultButtonToEquipmentClass(equipmentClass);
                                        current_class_improvement_recipe.setRequiredEquipmentClass(equipmentClass);
                                        this.equipmentClass = equipmentClass;
                                    }
                                }
                            } else if (e.getSlot() == 30){
                                if (e.getCursor().getType().isBlock()){
                                    Material similarMaterial = ItemUtils.getBaseMaterial(e.getCursor().getType());
                                    craftStationButton.setType(e.getCursor().getType());
                                    if (similarMaterial != null) {
                                        craftStationButton.setType(similarMaterial);
                                    }
                                    craftStationButton.setAmount(1);
                                    craftStation = craftStationButton.getType();
                                    if (current_class_improvement_recipe != null){
                                        current_class_improvement_recipe.setCraftingBlock(craftStation);
                                    }
                                    currentValidationIndex = 0;
                                }
                            }
                        }
                    }
                }
            }
            if (e.getCurrentItem() != null){
                if (e.getCurrentItem().getItemMeta() != null){
                    if (e.getCurrentItem().getItemMeta().getPersistentDataContainer().has(buttonNameKey, PersistentDataType.STRING)){
                        if (view == View.VIEW_RECIPES) {
                            AbstractCustomCraftingRecipe improveRecipe = CustomRecipeManager.getInstance().getRecipeByName(
                                    e.getCurrentItem().getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING));
                            if (improveRecipe instanceof ItemClassImprovementRecipe) {
                                current_class_improvement_recipe = ((ItemClassImprovementRecipe) improveRecipe).clone();
                                old_class_improvement_recipe = ((ItemClassImprovementRecipe) improveRecipe).clone();
                                view = View.CREATE_RECIPE;
                            }
                        }
                    }
                }
            }
        }
        if (view != View.PICK_INGREDIENTS) {
            setMenuItems();
        }
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
        switch (view){
            case VIEW_RECIPES: setCustomClassImproveRecipesView();
                return;
            case CREATE_RECIPE: setCreateClassImproveRecipeView();
                return;
            case PICK_INGREDIENTS: setPickIngredientsView();
        }
    }

    private void setPickIngredientsView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        for (ItemStack ingredient : customRecipeIngredients.stream().limit(45L).collect(Collectors.toList())){
            inventory.addItem(ingredient);
        }
        inventory.setItem(49, confirmButton);
    }

    private void setCreateClassImproveRecipeView(){
        if (current_class_improvement_recipe != null){
            customRecipeIngredients = current_class_improvement_recipe.getIngredients();
            craftStation = current_class_improvement_recipe.getCraftingBlock();
            craftStationButton.setType(craftStation);
            currentModifiers = new ArrayList<>(current_class_improvement_recipe.getItemModifers());
            craftTime = current_class_improvement_recipe.getCraftingTime();
            breakStation = current_class_improvement_recipe.breakStation();
            exactMeta = current_class_improvement_recipe.requireExactMeta();
            unlockedForEveryone = current_class_improvement_recipe.isUnlockedForEveryone();
            currentValidation = current_class_improvement_recipe.getValidation();
            setResultButtonToEquipmentClass(current_class_improvement_recipe.getRequiredEquipmentClass());
        }

        List<String> ingredientButtonLore = new ArrayList<>();
        for (ItemStack ingredient : customRecipeIngredients){
            ingredientButtonLore.add(Utils.chat("&e"+ingredient.getAmount() + " &7x&e " + getItemName(ingredient)));
        }
        ItemMeta ingredientButtonMeta = ingredientsButton.getItemMeta();
        assert ingredientButtonMeta != null;
        ingredientButtonMeta.setLore(ingredientButtonLore);
        ingredientsButton.setItemMeta(ingredientButtonMeta);

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

        ItemMeta requireExactMetaMeta = exactMetaButton.getItemMeta();
        assert requireExactMetaMeta != null;
        requireExactMetaMeta.setDisplayName(Utils.chat(String.format("&e&lRequire exact meta: %s", ((exactMeta) ? "Yes" : "No"))));
        exactMetaButton.setItemMeta(requireExactMetaMeta);

        ItemMeta unlockedForEveryoneMeta = unlockedForEveryoneButton.getItemMeta();
        assert unlockedForEveryoneMeta != null;
        unlockedForEveryoneMeta.setDisplayName(Utils.chat(String.format("&e&lUnlocked for everyone: %s", ((unlockedForEveryone) ? "Yes" : "No"))));
        unlockedForEveryoneButton.setItemMeta(unlockedForEveryoneMeta);

        ItemMeta timeButtonMeta = timeButton.getItemMeta();
        assert timeButtonMeta != null;
        timeButtonMeta.setDisplayName(Utils.chat(String.format("&e&lTime to craft: %.1fs", ((double) craftTime) / 1000D)));
        timeButton.setItemMeta(timeButtonMeta);

        ItemMeta breakStationMeta = breakStationButton.getItemMeta();
        assert breakStationMeta != null;
        breakStationMeta.setDisplayName(Utils.chat("&c&lBreak station after use: " + ((breakStation) ? "Yes" : "No")));
        breakStationButton.setItemMeta(breakStationMeta);

        List<String> validationButtonLore;
        String validationButtonDisplayName;
        if (this.currentValidation != null){
            validationButtonLore = Utils.separateStringIntoLines(Utils.chat(this.currentValidation.getDescription()), 40);
            validationButtonDisplayName = Utils.chat(this.currentValidation.getDisplayName());
            craftValidationButton.setType(this.currentValidation.getIcon());
        } else {
            validationButtonLore = Utils.separateStringIntoLines(Utils.chat(
                    "&7No validation available and default is not set, this is not intended and you should contact the developer- but it should still work."
            ), 40);
            validationButtonDisplayName = Utils.chat("&7No validation, block state ignored");
            craftValidationButton.setType(Material.BARRIER);
        }
        ItemMeta validationButtonMeta = craftValidationButton.getItemMeta();
        assert validationButtonMeta != null;
        validationButtonMeta.setDisplayName(validationButtonDisplayName);
        validationButtonMeta.setLore(validationButtonLore);
        craftValidationButton.setItemMeta(validationButtonMeta);

        inventory.setItem(11, exactMetaButton);
        inventory.setItem(12, ingredientsButton);
        inventory.setItem(14, improveResultButton);
        inventory.setItem(16, unlockedForEveryoneButton);
        inventory.setItem(19, cancelButton);
        inventory.setItem(25, confirmButton);
        inventory.setItem(29, craftValidationButton);
        inventory.setItem(30, craftStationButton);
        inventory.setItem(32, dynamicModifierButton);
        inventory.setItem(39, timeButton);
        inventory.setItem(41, breakStationButton);
    }

    private void setCustomClassImproveRecipesView(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> totalRecipeButtons = new ArrayList<>();
        for (AbstractCustomCraftingRecipe recipe : CustomRecipeManager.getInstance().getAllCustomRecipes().values()){
            if (recipe instanceof ItemClassImprovementRecipe){
                ItemStack resultButton = new ItemStack(getEquipmentClassIcon(((ItemClassImprovementRecipe) recipe).getRequiredEquipmentClass()));
                ItemMeta resultMeta = resultButton.getItemMeta();
                assert resultMeta != null;
                resultMeta.setDisplayName(recipe.getName());
                List<String> resultLore = new ArrayList<>();
                for (ItemStack ingredient : recipe.getIngredients()){
                    resultLore.add(Utils.chat("&e"+ingredient.getAmount() + " &7x&e " + getItemName(ingredient)));
                }
                resultLore.add(Utils.chat("&8&m                                      "));
                List<DynamicItemModifier> modifiers = new ArrayList<>(recipe.getItemModifers());
                modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                for (DynamicItemModifier modifier : modifiers){
                    resultLore.addAll(Utils.separateStringIntoLines(Utils.chat("&7- " + modifier.toString()), 40));
                }
                if (resultMeta.getLore() != null){
                    resultLore.addAll(resultMeta.getLore());
                }
                resultLore.add(Utils.chat("&7Tinkered on: &e"+ recipe.getCraftingBlock().toString().toLowerCase().replace("_", " ")));
                resultMeta.setLore(resultLore);
                resultMeta.getPersistentDataContainer().set(buttonNameKey, PersistentDataType.STRING, recipe.getName());
                resultButton.setItemMeta(resultMeta);
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

    private void setResultButtonToEquipmentClass(EquipmentClass equipmentClass){
        if (equipmentClass == null){
            return;
        }
        switch (equipmentClass){
            case AXE:
                improveResultButton = Utils.createItemStack(Material.IRON_AXE, Utils.chat("&7Recipe works on all &eAxes"), null);
                break;
            case BOW:
                improveResultButton = Utils.createItemStack(Material.BOW, Utils.chat("&7Recipe works on Bows"),
                        Arrays.asList(Utils.chat("&8Since there's only 1 bow, you"),
                                Utils.chat("&8might as well just make this a regular"),
                                Utils.chat("&8improvement recipe.")));
                break;
            case HOE:
                improveResultButton = Utils.createItemStack(Material.IRON_HOE, Utils.chat("&7Recipe works on all &eHoes"), null);
                break;
            case BOOTS:
                improveResultButton = Utils.createItemStack(Material.IRON_BOOTS, Utils.chat("&7Recipe works on all &eBoots"), null);
                break;
            case SWORD:
                improveResultButton = Utils.createItemStack(Material.IRON_SWORD, Utils.chat("&7Recipe works on all &eSwords"), null);
                break;
            case ELYTRA:
                improveResultButton = Utils.createItemStack(Material.ELYTRA, Utils.chat("&7Recipe works on Elytras"),
                        Arrays.asList(Utils.chat("&8Since there's only 1 elytra, you"),
                                Utils.chat("&8might as well just make this a regular"),
                                Utils.chat("&8improvement recipe.")));
                break;
            case HELMET:
                improveResultButton = Utils.createItemStack(Material.IRON_HELMET, Utils.chat("&7Recipe works on all &eHelmets"), null);
                break;
            case SHEARS:
                improveResultButton = Utils.createItemStack(Material.SHEARS, Utils.chat("&7Recipe works on Shears"),
                        Arrays.asList(Utils.chat("&8Since there's only 1 shears, you"),
                                Utils.chat("&8might as well just make this a regular"),
                                Utils.chat("&8improvement recipe.")));
                break;
            case SHIELD:
                improveResultButton = Utils.createItemStack(Material.SHIELD, Utils.chat("&7Recipe works on Shields"),
                        Arrays.asList(Utils.chat("&8Since there's only 1 shield, you"),
                                Utils.chat("&8might as well just make this a regular"),
                                Utils.chat("&8improvement recipe.")));
                break;
            case SHOVEL:
                improveResultButton = Utils.createItemStack(Material.IRON_SHOVEL, Utils.chat("&7Recipe works on all &eShovels"), null);
                break;
            case PICKAXE:
                improveResultButton = Utils.createItemStack(Material.IRON_PICKAXE, Utils.chat("&7Recipe works on all &ePickaxes"), null);
                break;
            case TRIDENT:
                improveResultButton = Utils.createItemStack(Material.TRIDENT, Utils.chat("&7Recipe works on Tridents"),
                        Arrays.asList(Utils.chat("&8Since there's only 1 trident, you"),
                                Utils.chat("&8might as well just make this a regular"),
                                Utils.chat("&8improvement recipe.")));
                break;
            case CROSSBOW:
                improveResultButton = Utils.createItemStack(Material.CROSSBOW, Utils.chat("&7Recipe works on Crossbows"),
                        Arrays.asList(Utils.chat("&8Since there's only 1 crossbow, you"),
                                Utils.chat("&8might as well just make this a regular"),
                                Utils.chat("&8improvement recipe.")));
                break;
            case LEGGINGS:
                improveResultButton = Utils.createItemStack(Material.IRON_LEGGINGS, Utils.chat("&7Recipe works on all &eLeggings"), null);
                break;
            case CHESTPLATE:
                improveResultButton = Utils.createItemStack(Material.IRON_CHESTPLATE, Utils.chat("&7Recipe works on all &eChestplates"), null);
                break;
            case FISHING_ROD:
                improveResultButton = Utils.createItemStack(Material.FISHING_ROD, Utils.chat("&7Recipe works on Fishing Rods"),
                        Arrays.asList(Utils.chat("&8Since there's only 1 fishing rod, you"),
                                Utils.chat("&8might as well just make this a regular"),
                                Utils.chat("&8improvement recipe.")));
                break;
            case FLINT_AND_STEEL:
                improveResultButton = Utils.createItemStack(Material.FLINT_AND_STEEL, Utils.chat("&7Recipe works on Flint and Steels"),
                        Arrays.asList(Utils.chat("&8Since there's only 1 flint and steel, you"),
                                Utils.chat("&8might as well just make this a regular"),
                                Utils.chat("&8improvement recipe.")));
                break;
        }
        this.equipmentClass = equipmentClass;
    }

    private Material getEquipmentClassIcon(EquipmentClass equipmentClass){
        if (equipmentClass == null){
            return Material.BARRIER;
        }
        switch (equipmentClass){
            case AXE:
                return Material.IRON_AXE;
            case BOW:
                return Material.BOW;
            case HOE:
                return Material.IRON_HOE;
            case BOOTS:
                return Material.IRON_BOOTS;
            case SWORD:
                return Material.IRON_SWORD;
            case ELYTRA:
                return Material.ELYTRA;
            case HELMET:
                return Material.IRON_HELMET;
            case SHEARS:
                return Material.SHEARS;
            case SHIELD:
                return Material.SHIELD;
            case SHOVEL:
                return Material.IRON_SHOVEL;
            case PICKAXE:
                return Material.IRON_PICKAXE;
            case TRIDENT:
                return Material.TRIDENT;
            case CROSSBOW:
                return Material.CROSSBOW;
            case LEGGINGS:
                return Material.IRON_LEGGINGS;
            case CHESTPLATE:
                return Material.IRON_CHESTPLATE;
            case FISHING_ROD:
                return Material.FISHING_ROD;
            case FLINT_AND_STEEL:
                return Material.FLINT_AND_STEEL;
        }
        return Material.BARRIER;
    }

    @Override
    public void setCurrentModifiers(Collection<DynamicItemModifier> modifiers) {
        this.currentModifiers = modifiers;
        current_class_improvement_recipe.setModifiers(modifiers);
    }

    private enum View{
        VIEW_RECIPES,
        CREATE_RECIPE,
        PICK_INGREDIENTS
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

    private void handleTimeButton(ClickType clickType){
        switch (clickType){
            case LEFT: craftTime += 100;
                break;
            case RIGHT: {
                if (craftTime - 100 < 0) craftTime = 0;
                else craftTime -= 100;
                break;
            }
            case SHIFT_LEFT: craftTime += 1000;
                break;
            case SHIFT_RIGHT: {
                if (craftTime - 1000 < 0) craftTime = 0;
                else craftTime -= 1000;
            }
        }
        if (current_class_improvement_recipe != null){
            current_class_improvement_recipe.setCraftingTime(craftTime);
        }
    }
}
