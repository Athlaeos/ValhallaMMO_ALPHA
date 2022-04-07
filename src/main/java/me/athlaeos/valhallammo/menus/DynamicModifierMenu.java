package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.DynamicItemModifierManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.*;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

public class DynamicModifierMenu extends Menu{
    private final NamespacedKey buttonNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "button_recipe_name");
    private final NamespacedKey categoryNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "button_category_name");

    private final List<ItemStack> scrollItems = new ArrayList<>();
    private final int[] scrollBarIndexes = new int[]{46, 47, 48, 50, 51, 52};

    private ItemStack modifierStrengthButton = Utils.createItemStack(
            Material.NETHER_STAR,
            Utils.chat("&6&lPrimary Options"),
            new ArrayList<>()
    );
    private ItemStack modifierStrength2Button = Utils.createItemStack(
            Material.NETHER_STAR,
            Utils.chat("&6&lSecondary Options"),
            new ArrayList<>()
    );
    private ItemStack modifierStrength3Button = Utils.createItemStack(
            Material.NETHER_STAR,
            Utils.chat("&6&lTernary Options"),
            new ArrayList<>()
    );
    private final ItemStack modifierPriorityButton = Utils.createItemStack(
            Material.CLOCK,
            Utils.chat("&6&lPriority"),
            new ArrayList<>()
    );
    private final ItemStack confirmButton = Utils.createItemStack(Material.STRUCTURE_VOID,
            Utils.chat("&b&lSave"),
            null);
    private final ItemStack createNewButton = Utils.createItemStack(Material.LIME_STAINED_GLASS_PANE,
            Utils.chat("&b&lNew"),
            null);
    private final ItemStack cancelButton = Utils.createItemStack(Material.BARRIER,
            Utils.chat("&cDelete"),
            null);
    private final ItemStack nextPageButton = Utils.createItemStack(Material.ARROW,
            Utils.chat("&7&lNext page"),
            null);
    private final ItemStack previousPageButton = Utils.createItemStack(Material.ARROW,
            Utils.chat("&7&lPrevious page"),
            null);


    private View view = View.VIEW_MODIFIERS;
    private int currentPage = 0;
    private ModifierCategory currentCategory = ModifierCategory.ALL;

    private double modifierStrength = 0D;
    private double modifierStrength2 = 0D;
    private double modifierStrength3 = 0D;
    private ModifierPriority priority = ModifierPriority.NEUTRAL;
    private final Collection<DynamicItemModifier> currentModifiers;
    private DynamicItemModifier currentModifier = null;

    public DynamicModifierMenu(PlayerMenuUtility playerMenuUtility, Collection<DynamicItemModifier> modifiers) {
        super(playerMenuUtility);
        this.currentModifiers = modifiers;

        for (ModifierCategory s : ModifierCategory.values()){
            ItemStack skillIcon = s.getIcon().clone();

            ItemMeta iconMeta = skillIcon.getItemMeta();
            assert iconMeta != null;
            iconMeta.getPersistentDataContainer().set(categoryNameKey, PersistentDataType.STRING, s.toString());
            skillIcon.setItemMeta(iconMeta);

            scrollItems.add(skillIcon);
        }
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7Dynamic Modifiers");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem != null){
            e.setCancelled(true);
            if (clickedItem.equals(confirmButton)){
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
                        if (playerMenuUtility.getPreviousMenu() != null){
                            if (playerMenuUtility.getPreviousMenu() instanceof CraftingManagerMenu){
                                ((CraftingManagerMenu) playerMenuUtility.getPreviousMenu()).setCurrentModifiers(currentModifiers);
                            }

                            playerMenuUtility.getPreviousMenu().open();
                        }
                        break;
                    }
                }
            } else if (clickedItem.equals(createNewButton)){
                view = View.PICK_MODIFIERS;
            } else if (clickedItem.equals(nextPageButton)){
                currentPage++;
            } else if (clickedItem.equals(previousPageButton)){
                currentPage--;
            } else if (clickedItem.equals(modifierStrengthButton)){
                handleModifierStrength(e.getClick());
            } else if (clickedItem.equals(modifierPriorityButton)){
                handleModifierPriority(e.getClick());
            } else if (clickedItem.equals(modifierStrength2Button)){
                handleModifierStrength2(e.getClick());
            } else if (clickedItem.equals(modifierStrength3Button)){
                handleModifierStrength3(e.getClick());
            } else if (clickedItem.equals(cancelButton)){
                if (view == View.NEW_MODIFIER) {
                    if (currentModifier != null) {
                        currentModifiers.remove(currentModifier);
                        view = View.VIEW_MODIFIERS;
                    }
                }
            }

            if (clickedItem.getItemMeta() != null){
                if (clickedItem.getItemMeta().getPersistentDataContainer().has(buttonNameKey, PersistentDataType.STRING)){
                    switch (view){
                        case PICK_MODIFIERS:
                            currentModifier = DynamicItemModifierManager.getInstance().createModifier(
                                    clickedItem.getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING),
                                    0D,
                                    ModifierPriority.NEUTRAL
                            );
                            currentModifier.setStrength(currentModifier.getDefaultStrength());
                            modifierStrength = currentModifier.getDefaultStrength();
                            if (currentModifier instanceof DuoArgDynamicItemModifier){
                                modifierStrength2 = ((DuoArgDynamicItemModifier) currentModifier).getDefaultStrength2();
                                if (currentModifier instanceof TripleArgDynamicItemModifier){
                                    modifierStrength3 = ((TripleArgDynamicItemModifier) currentModifier).getDefaultStrength3();
                                }
                            }
                            resetModifierButtons();
                            view = View.NEW_MODIFIER;
                            break;
                        case VIEW_MODIFIERS:
                            String clickedModifier = clickedItem.getItemMeta().getPersistentDataContainer().get(buttonNameKey, PersistentDataType.STRING);
                            for (DynamicItemModifier modifier : currentModifiers){
                                if (modifier.getName().equals(clickedModifier)){
                                    currentModifier = modifier;
                                    modifierStrength = modifier.getStrength();
                                    if (modifier instanceof DuoArgDynamicItemModifier){
                                        modifierStrength2 = ((DuoArgDynamicItemModifier) modifier).getStrength2();
                                        if (modifier instanceof TripleArgDynamicItemModifier){
                                            modifierStrength3 = ((TripleArgDynamicItemModifier) modifier).getStrength3();
                                        }
                                    }
                                    priority = modifier.getPriority();
                                    view = View.NEW_MODIFIER;
                                    break;
                                }
                            }
                            break;
                    }
                } else if (clickedItem.getItemMeta().getPersistentDataContainer().has(categoryNameKey, PersistentDataType.STRING)) {
                    if (view == View.PICK_MODIFIERS){
                        try {
                            currentCategory = ModifierCategory.valueOf(clickedItem.getItemMeta().getPersistentDataContainer().get(categoryNameKey, PersistentDataType.STRING));
                        } catch (IllegalArgumentException ignored){
                        }
                    }
                }
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
        switch (view){
            case VIEW_MODIFIERS: setViewModifiersView();
                return;
            case PICK_MODIFIERS: setPickModifiersView();
                setScrollBar();
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

            if (currentModifier instanceof DynamicEditable){
                ((DynamicEditable) currentModifier).editIcon(modifierStrengthButton, 1);
                ((DynamicEditable) currentModifier).editIcon(modifierStrength2Button, 2);
                ((DynamicEditable) currentModifier).editIcon(modifierStrength3Button, 3);
            }
            if (currentModifier instanceof TripleArgDynamicItemModifier){
                List<String> strengthButton3Lore = new ArrayList<>(Utils.separateStringIntoLines(currentModifier.toString(), 40));
                ItemMeta strengthButton3Meta = modifierStrength3Button.getItemMeta();
                assert strengthButton3Meta != null;
                strengthButton3Meta.setLore(strengthButton3Lore);
                modifierStrength3Button.setItemMeta(strengthButton3Meta);

                List<String> strengthButton2Lore = new ArrayList<>(Utils.separateStringIntoLines(currentModifier.toString(), 40));
                ItemMeta strengthButton2Meta = modifierStrength2Button.getItemMeta();
                assert strengthButton2Meta != null;
                strengthButton2Meta.setLore(strengthButton2Lore);
                modifierStrength2Button.setItemMeta(strengthButton2Meta);

                inventory.setItem(23, modifierStrengthButton);
                inventory.setItem(40, modifierStrength2Button);
                inventory.setItem(42, modifierStrength3Button);
            } else if (currentModifier instanceof DuoArgDynamicItemModifier){
                List<String> strengthButton2Lore = new ArrayList<>(Utils.separateStringIntoLines(currentModifier.toString(), 40));
                ItemMeta strengthButton2Meta = modifierStrength2Button.getItemMeta();
                assert strengthButton2Meta != null;
                strengthButton2Meta.setLore(strengthButton2Lore);
                modifierStrength2Button.setItemMeta(strengthButton2Meta);

                inventory.setItem(23, modifierStrengthButton);
                inventory.setItem(41, modifierStrength2Button);
            } else {
                inventory.setItem(32, modifierStrengthButton);
            }
            inventory.setItem(13, modifierButton);
            inventory.setItem(30, modifierPriorityButton);
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
            DynamicItemModifier currentModifier = DynamicItemModifierManager.getInstance().createModifier(modifier, 0D, 0D, 0D, ModifierPriority.NEUTRAL);
            if (currentCategory != ModifierCategory.ALL){
                if (currentModifier.getCategory() != currentCategory){
                    continue;
                }
            }

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

    private void handleModifierStrength2(ClickType clickType){
        if (currentModifier != null){
            if (currentModifier instanceof DuoArgDynamicItemModifier){
                DuoArgDynamicItemModifier modifier = (DuoArgDynamicItemModifier) currentModifier;
                switch (clickType){
                    case LEFT: {
                        if (modifierStrength2 + modifier.getSmallStepDecrease2() > modifier.getMaxStrength2()) {
                            modifierStrength2 = modifier.getMaxStrength2();
                        } else if (modifierStrength2 == modifier.getMaxStrength2()){
                            modifierStrength2 = modifier.getMinStrength2();
                        } else {
                            modifierStrength2 += modifier.getSmallStepIncrease2();
                        }
                        break;
                    }
                    case RIGHT: {
                        if (modifierStrength2 - modifier.getSmallStepDecrease2() < modifier.getMinStrength2()){
                            modifierStrength2 = modifier.getMinStrength2();
                        } else if (modifierStrength2 == modifier.getMinStrength2()){
                            modifierStrength2 = modifier.getMaxStrength2();
                        } else {
                            modifierStrength2 -= modifier.getSmallStepDecrease2();
                        }
                        break;
                    }
                    case SHIFT_LEFT: {
                        if (modifierStrength2 + modifier.getBigStepIncrease2() > modifier.getMaxStrength2()) {
                            modifierStrength2 = modifier.getMaxStrength2();
                        } else if (modifierStrength2 == modifier.getMaxStrength2()){
                            modifierStrength2 = modifier.getMinStrength2();
                        } else {
                            modifierStrength2 += modifier.getBigStepIncrease2();
                        }
                        break;
                    }
                    case SHIFT_RIGHT: {
                        if (modifierStrength2 - modifier.getBigStepDecrease2() < modifier.getMinStrength2()){
                            modifierStrength2 = modifier.getMinStrength2();
                        } else if (modifierStrength2 == modifier.getMinStrength2()){
                            modifierStrength2 = modifier.getMaxStrength2();
                        } else {
                            modifierStrength2 -= modifier.getBigStepDecrease2();
                        }
                    }
                }
                ((DuoArgDynamicItemModifier) currentModifier).setStrength2(modifierStrength2);
            }
        }
    }

    private void handleModifierStrength3(ClickType clickType){
        if (currentModifier != null){
            if (currentModifier instanceof TripleArgDynamicItemModifier){
                TripleArgDynamicItemModifier modifier = (TripleArgDynamicItemModifier) currentModifier;
                switch (clickType){
                    case LEFT: {
                        if (modifierStrength3 + modifier.getSmallStepDecrease3() > modifier.getMaxStrength3()) {
                            modifierStrength3 = modifier.getMaxStrength3();
                        } else if (modifierStrength3 == modifier.getMaxStrength3()){
                            modifierStrength3 = modifier.getMinStrength3();
                        } else {
                            modifierStrength3 += modifier.getSmallStepIncrease3();
                        }
                        break;
                    }
                    case RIGHT: {
                        if (modifierStrength3 - modifier.getSmallStepDecrease3() < modifier.getMinStrength3()){
                            modifierStrength3 = modifier.getMinStrength3();
                        } else if (modifierStrength3 == modifier.getMinStrength3()){
                            modifierStrength3 = modifier.getMaxStrength3();
                        } else {
                            modifierStrength3 -= modifier.getSmallStepDecrease3();
                        }
                        break;
                    }
                    case SHIFT_LEFT: {
                        if (modifierStrength3 + modifier.getBigStepIncrease3() > modifier.getMaxStrength3()) {
                            modifierStrength3 = modifier.getMaxStrength3();
                        } else if (modifierStrength3 == modifier.getMaxStrength3()){
                            modifierStrength3 = modifier.getMinStrength3();
                        } else {
                            modifierStrength3 += modifier.getBigStepIncrease3();
                        }
                        break;
                    }
                    case SHIFT_RIGHT: {
                        if (modifierStrength3 - modifier.getBigStepDecrease3() < modifier.getMinStrength3()){
                            modifierStrength3 = modifier.getMinStrength3();
                        } else if (modifierStrength3 == modifier.getMinStrength3()){
                            modifierStrength3 = modifier.getMaxStrength3();
                        } else {
                            modifierStrength3 -= modifier.getBigStepDecrease3();
                        }
                    }
                }
                modifier.setStrength3(modifierStrength3);
            }
        }
    }

    private void resetModifierButtons(){
        this.modifierStrengthButton = Utils.createItemStack(
                Material.NETHER_STAR,
                Utils.chat("&6&lPrimary Options"),
                new ArrayList<>()
        );
        this.modifierStrength2Button = Utils.createItemStack(
                Material.NETHER_STAR,
                Utils.chat("&6&lSecondary Options"),
                new ArrayList<>()
        );
        this.modifierStrength3Button = Utils.createItemStack(
                Material.NETHER_STAR,
                Utils.chat("&6&lTernary Options"),
                new ArrayList<>()
        );
    }

    private void setScrollBar(){
        List<ItemStack> icons = new ArrayList<>(scrollItems);
        int iconsSize = icons.size();
        if (iconsSize > 0){
            for (int i = 0; i < 6; i++){
                for (int o = 0; o < 6; o++){
                    if (o >= scrollItems.size()) break;
                    ItemStack iconToPut = scrollItems.get(o);
                    inventory.setItem(scrollBarIndexes[o], iconToPut);
                }
                ItemStack centerItem = inventory.getItem(48);
                if (centerItem != null){
                    assert centerItem.getItemMeta() != null;
                    if (getItemStoredCategory(centerItem) == this.currentCategory){
                        break;
                    }
                }
                Collections.rotate(scrollItems, 1);
            }
        }
    }

    private ModifierCategory getItemStoredCategory(ItemStack i){
        assert i.getItemMeta() != null;
        if (i.getItemMeta().getPersistentDataContainer().has(categoryNameKey, PersistentDataType.STRING)){
            String t = i.getItemMeta().getPersistentDataContainer().get(categoryNameKey, PersistentDataType.STRING);
            try {
                return ModifierCategory.valueOf(t);
            } catch (IllegalArgumentException ignored){
            }
        }
        return null;
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

    private enum View{
        PICK_MODIFIERS,
        VIEW_MODIFIERS,
        NEW_MODIFIER
    }
}
