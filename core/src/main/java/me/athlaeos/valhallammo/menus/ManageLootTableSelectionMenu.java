package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.loottables.ChancedBlockLootTable;
import me.athlaeos.valhallammo.loottables.ChancedEntityLootTable;
import me.athlaeos.valhallammo.loottables.LootManager;
import me.athlaeos.valhallammo.loottables.TieredLootTable;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class ManageLootTableSelectionMenu extends Menu{
    private final ItemStack nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
    private final ItemStack previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);

    private final NamespacedKey lootTableNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "loot_table_identifier");

    private int currentPage = 0;

    public ManageLootTableSelectionMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7Loot Tables");
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack clickedItem = e.getCurrentItem();
        e.setCancelled(true);
        if (clickedItem != null){
            if (clickedItem.hasItemMeta()){
                ItemMeta meta = clickedItem.getItemMeta();
                assert meta != null;
                if (meta.getPersistentDataContainer().has(lootTableNameKey, PersistentDataType.STRING)){
                    String value = meta.getPersistentDataContainer().get(lootTableNameKey, PersistentDataType.STRING);
                    TieredLootTable tieredLootTable = LootManager.getInstance().getTieredLootTables().get(value);
                    if (tieredLootTable != null){
                        new ManageTieredLootTablesMenu(playerMenuUtility, tieredLootTable).open();
                        return;
                    } else {
                        ChancedBlockLootTable chancedBlockLootTable = LootManager.getInstance().getChancedBlockLootTables().get(value);
                        if (chancedBlockLootTable != null){
                            new ManageChancedBlockLootTablesMenu(playerMenuUtility, chancedBlockLootTable).open();
                            return;
                        } else {
                            ChancedEntityLootTable chancedEntityLootTable = LootManager.getInstance().getChancedEntityLootTables().get(value);
                            if (chancedEntityLootTable != null){
                                new ManageChancedEntityLootTablesMenu(playerMenuUtility, chancedEntityLootTable).open();
                                return;
                            }
                        }
                    }
                }
            }
            if (clickedItem.equals(nextPageButton)){
                currentPage++;
            } else if (clickedItem.equals(previousPageButton)){
                currentPage--;
            }
        }

        setMenuItems();
    }

    @Override
    public void handleMenu(InventoryDragEvent e) {
        e.setCancelled(true);
        setMenuItems();
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        List<ItemStack> lootTableIcons = new ArrayList<>();
        for (ChancedBlockLootTable c : LootManager.getInstance().getChancedBlockLootTables().values()){
            Material i = c.getIcon();
            if (i == null) i = Material.BOOK;
            if (i.isAir()) i = Material.BOOK;
            if (c.getName() == null) {
                return;
            }
            ItemStack icon = new ItemStack(i);
            String displayName = c.getDisplayName();
            if (displayName == null) displayName = "&r&f" + c.getName();
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;
            if (c.getDescription() != null){
                meta.setLore(Utils.separateStringIntoLines(Utils.chat(c.getDescription()), 40));
            }
            meta.setDisplayName(Utils.chat(displayName));

            meta.getPersistentDataContainer().set(lootTableNameKey, PersistentDataType.STRING, c.getName());
            icon.setItemMeta(meta);
            lootTableIcons.add(icon);
        }
        for (ChancedEntityLootTable c : LootManager.getInstance().getChancedEntityLootTables().values()){
            Material i = c.getIcon();
            if (i == null) i = Material.BOOK;
            if (i.isAir()) i = Material.BOOK;
            if (c.getName() == null) {
                return;
            }
            ItemStack icon = new ItemStack(i);
            String displayName = c.getDisplayName();
            if (displayName == null) displayName = "&r&f" + c.getName();
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;
            if (c.getDescription() != null){
                meta.setLore(Utils.separateStringIntoLines(Utils.chat(c.getDescription()), 40));
            }
            meta.setDisplayName(Utils.chat(displayName));

            meta.getPersistentDataContainer().set(lootTableNameKey, PersistentDataType.STRING, c.getName());
            icon.setItemMeta(meta);
            lootTableIcons.add(icon);
        }
        for (TieredLootTable t : LootManager.getInstance().getTieredLootTables().values()){
            Material i = t.getIcon();
            if (i == null) i = Material.BOOK;
            if (i.isAir()) i = Material.BOOK;
            if (t.getName() == null) {
                return;
            }
            ItemStack icon = new ItemStack(i);
            String displayName = t.getDisplayName();
            if (displayName == null) displayName = "&r&f" + t.getName();
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;
            if (t.getDescription() != null){
                meta.setLore(Utils.separateStringIntoLines(Utils.chat(t.getDescription()), 40));
            }
            meta.setDisplayName(Utils.chat(displayName));

            meta.getPersistentDataContainer().set(lootTableNameKey, PersistentDataType.STRING, t.getName());
            icon.setItemMeta(meta);
            lootTableIcons.add(icon);
        }

        lootTableIcons.sort(Comparator.comparing(Utils::getItemName));
        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, lootTableIcons);

        if (currentPage > pages.size()) currentPage = pages.size();
        if (currentPage < 1) currentPage = 1;

        if (pages.size() > 0){
            for (ItemStack i : pages.get(currentPage - 1)){
                inventory.addItem(i);
            }
        }

        inventory.setItem(45, previousPageButton);
        inventory.setItem(53, nextPageButton);
    }
}
