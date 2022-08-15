package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.managers.ItemDictionaryManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ItemDictionaryMenu extends Menu{
    private final NamespacedKey entryKey = new NamespacedKey(ValhallaMMO.getPlugin(), "item_key");
    private int pageNumber = 1;
    private final ItemStack nextPageButton;
    private final ItemStack previousPageButton;

    private final ItemStack newEntryButton = Utils.createItemStack(Material.GREEN_STAINED_GLASS_PANE, Utils.chat("&aAdd"),
            Arrays.asList(Utils.chat("&7Drag item from your inventory"), Utils.chat("&7here to add it to the item index")));

    public ItemDictionaryMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
        nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat(TranslationManager.getInstance().getTranslation("translation_next_page")), null);
        previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat(TranslationManager.getInstance().getTranslation("translation_previous_page")), null);
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&8Item Index");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        e.setCancelled(true);
        if (e.getClickedInventory() instanceof PlayerInventory){
            e.setCancelled(false);
        }
        ItemStack clickedItem = e.getCurrentItem();
        if (clickedItem != null){
            if (e.getCurrentItem().equals(previousPageButton)){
                pageNumber--;
            } else if (e.getCurrentItem().equals(nextPageButton)){
                pageNumber++;
            } else if (clickedItem.equals(newEntryButton)){
                 if (!Utils.isItemEmptyOrNull(e.getCursor())){
                     ItemDictionaryManager.getInstance().addItem(e.getCursor().clone());
                 }
            } else {
                int value = getStoredNumber(clickedItem);
                if (value >= 0){
                    ItemStack entry = ItemDictionaryManager.getInstance().getItemDictionary().get(value);
                    if (Utils.isItemEmptyOrNull(entry)){
                        playerMenuUtility.getOwner().sendMessage(Utils.chat("&cItem has been removed"));
                    } else {
                        ItemDictionaryManager.getInstance().removeItem(value);
                    }
                }
            }
        }
        setMenuItems();
    }

    private int getStoredNumber(ItemStack i){
        ItemMeta meta = i.getItemMeta();
        assert meta != null;
        if (meta.getPersistentDataContainer().has(entryKey, PersistentDataType.INTEGER)){
            return meta.getPersistentDataContainer().get(entryKey, PersistentDataType.INTEGER);
        }
        return -1;
    }

    @Override
    public void handleMenu(InventoryDragEvent e) {
        e.setCancelled(true);
    }

    @Override
    public void setMenuItems() {
        inventory.clear();

        List<ItemStack> entryButtons = new ArrayList<>();
        for (Integer i : ItemDictionaryManager.getInstance().getItemDictionary().keySet()){
            ItemStack entry = ItemDictionaryManager.getInstance().getItemDictionary().get(i);
            if (Utils.isItemEmptyOrNull(entry)) continue;
            ItemStack button = entry.clone();
            ItemMeta buttonMeta = button.getItemMeta();
            if (buttonMeta != null){
                List<String> buttonLore = new ArrayList<>(Collections.singletonList(Utils.chat("&eID: " + i)));
                buttonLore.addAll(buttonMeta.getLore() != null ? buttonMeta.getLore() : new ArrayList<>());
                buttonMeta.setLore(buttonLore);
                buttonMeta.getPersistentDataContainer().set(entryKey, PersistentDataType.INTEGER, i);
                button.setItemMeta(buttonMeta);

                entryButtons.add(button);
            }
        }
        entryButtons.sort(Comparator.comparing(this::getStoredNumber));
        entryButtons.add(newEntryButton);
        if (entryButtons.size() >= 45){
            Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, entryButtons);
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
            inventory.addItem(entryButtons.toArray(new ItemStack[]{}));
        }
    }
}
