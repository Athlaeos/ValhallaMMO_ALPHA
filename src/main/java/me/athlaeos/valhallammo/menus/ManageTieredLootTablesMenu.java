package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.loottables.TieredLootEntry;
import me.athlaeos.valhallammo.loottables.TieredLootTable;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class ManageTieredLootTablesMenu extends Menu {
    private final ItemStack nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
    private final ItemStack previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
    private final ItemStack returnToMenuButton = Utils.createItemStack(Material.WRITABLE_BOOK, Utils.chat("&7&lReturn to menu"), null);
    private final ItemStack weightButton = Utils.createItemStack(Material.PAPER, Utils.chat("&fWeight: 0"),
            Utils.separateStringIntoLines(Utils.chat("&7Determines the rarity (weighted) of the item drop." +
                            " The lower, the more rare. The drop chance will be equal to <weight>/<total combined " +
                            "weight of available drops> * 100%")
                    , 40));
    private final ItemStack tierButton = Utils.createItemStack(Material.GOLD_INGOT, Utils.chat("&fTier"),
            Utils.separateStringIntoLines(Utils.chat("&7The tier of the drop. Typically the higher the tier " +
                            "the more powerful/valuable the drops.")
                    , 40));
    private final ItemStack dynamicModifierButton = Utils.createItemStack(
            Material.BOOK,
            Utils.chat("&b&lDynamic Modifiers"), null);
    private final ItemStack biomeFilterButton = Utils.createItemStack(Material.GRASS_BLOCK, Utils.chat("&fBiome Filter"), null);
    private final ItemStack regionFilterButton = Utils.createItemStack(Material.NETHERRACK, Utils.chat("&fRegion Filter"), null);
    private final ItemStack newEntryButton = Utils.createItemStack(Material.GREEN_STAINED_GLASS_PANE, Utils.chat("&aAdd"), null);
    private final ItemStack saveButton = Utils.createItemStack(Material.STRUCTURE_VOID, Utils.chat("&aSave Changes"), null);
    private final ItemStack deleteButton = Utils.createItemStack(Material.BARRIER, Utils.chat("&aRemove Entry"), null);

    private final NamespacedKey entryNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "entry_identifier");
    private final NamespacedKey biomeNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "biome_identifier");
    private final NamespacedKey regionNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "region_identifier");

    private View view = View.VIEW_ENTRIES;
    private int currentPage = 0;
    private final TieredLootTable currentLootTable;
    private TieredLootEntry currentLootEntry = null;
    private ItemStack drop = Utils.createItemStack(Material.WHEAT_SEEDS, Utils.chat("&r&fPlace your own custom drop here :)"), null);
    private Set<String> regionFilter = new HashSet<>();
    private Set<Biome> biomeFilter = new HashSet<>();
    private int weight = 0;
    private int tier = 0;
    private List<DynamicItemModifier> currentModifiers = new ArrayList<>();

    public ManageTieredLootTablesMenu(PlayerMenuUtility playerMenuUtility, TieredLootTable table) {
        super(playerMenuUtility);
        this.currentLootTable = table;
        if (currentLootTable == null) {
            playerMenuUtility.getOwner().sendMessage(Utils.chat("&cLoot Table is null"));
            playerMenuUtility.getOwner().closeInventory();
        }
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7Tiered Loot Table: " + this.currentLootTable.getDisplayName());
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        ItemStack clickedItem = e.getCurrentItem();
        e.setCancelled(true);
        if (e.getClickedInventory() instanceof PlayerInventory){
            e.setCancelled(false);
        } else {
            if (clickedItem != null){
                ItemMeta meta = clickedItem.getItemMeta();
                assert meta != null;
                if (meta.getPersistentDataContainer().has(entryNameKey, PersistentDataType.STRING)){
                    String value = meta.getPersistentDataContainer().get(entryNameKey, PersistentDataType.STRING);
                    if (currentLootTable.getAllLootEntries().containsKey(value)){
                        TieredLootEntry entry = currentLootTable.getAllLootEntries().get(value);
                        if (entry == null){
                            playerMenuUtility.getOwner().sendMessage(Utils.chat("&cEntry has been removed"));
                        } else {
                            currentPage = 0;
                            currentLootEntry = entry;
                            view = View.VIEW_ENTRY;
                            this.weight = entry.getWeight();
                            this.tier = entry.getTier();
                            this.biomeFilter = entry.getBiomeFilter();
                            this.regionFilter = entry.getRegionFilter();
                            this.drop = entry.getLoot().clone();
                            this.currentModifiers = new ArrayList<>(entry.getModifiers());
                        }
                    } else {
                        playerMenuUtility.getOwner().sendMessage(Utils.chat("&cEntry has been removed"));
                    }
                } else if (meta.getPersistentDataContainer().has(biomeNameKey, PersistentDataType.STRING)){
                    String value = meta.getPersistentDataContainer().get(biomeNameKey, PersistentDataType.STRING);
                    try {
                        if (view == View.VIEW_BIOME_FILTER){
                            biomeFilter.remove(Biome.valueOf(value));
                        } else if (view == View.PICK_BIOME_FILTER){
                            biomeFilter.add(Biome.valueOf(value));
                        }
                    } catch (IllegalArgumentException ignored){
                    }
                } else if (meta.getPersistentDataContainer().has(regionNameKey, PersistentDataType.STRING)){
                    String value = meta.getPersistentDataContainer().get(regionNameKey, PersistentDataType.STRING);
                    if (view == View.VIEW_REGION_FILTER){
                        regionFilter.remove(value);
                    } else if (view == View.PICK_REGION_FILTER){
                        regionFilter.add(value);
                    }
                }
                if (clickedItem.equals(nextPageButton)){
                    currentPage++;
                } else if (clickedItem.equals(previousPageButton)){
                    currentPage--;
                } else if (clickedItem.equals(drop)) {
                    if (e.getCursor() != null){
                        drop = e.getCursor().clone();
                    }
                } else if (clickedItem.equals(returnToMenuButton)) {
                    switch (view){
                        case VIEW_ENTRIES:{
                            new ManageLootTableSelectionMenu(playerMenuUtility).open();
                            return;
                        }
                        case VIEW_ENTRY:{
                            view = View.VIEW_ENTRIES;
                            break;
                        }
                        case PICK_BIOME_FILTER:{
                            view = View.VIEW_BIOME_FILTER;
                            break;
                        }
                        case VIEW_BIOME_FILTER:
                        case VIEW_REGION_FILTER: {
                            view = View.VIEW_ENTRY;
                            break;
                        }
                        case PICK_REGION_FILTER:{
                            view = View.VIEW_REGION_FILTER;
                            break;
                        }
                    }
                } else if (clickedItem.equals(newEntryButton)) {
                    switch (view){
                        case VIEW_ENTRIES:{
                            view = View.VIEW_ENTRY;
                            String name = UUID.randomUUID().toString();
                            for (int i = 0; i < 10; i++){
                                if (currentLootTable.getAllLootEntries().containsKey(name)){
                                    name = UUID.randomUUID().toString();
                                } else {
                                    break;
                                }
                            }
                            currentLootEntry = new TieredLootEntry(name, 0, new ItemStack(Material.STICK),
                                    10, new HashSet<>(), new HashSet<>(), new HashSet<>());
                            this.weight = currentLootEntry.getWeight();
                            this.tier = currentLootEntry.getTier();
                            this.biomeFilter = currentLootEntry.getBiomeFilter();
                            this.regionFilter = currentLootEntry.getRegionFilter();
                            this.drop = currentLootEntry.getLoot().clone();
                            this.currentModifiers = new ArrayList<>(currentLootEntry.getModifiers());
                            break;
                        }
                        case VIEW_REGION_FILTER:{
                            view = View.PICK_REGION_FILTER;
                            break;
                        }
                        case VIEW_BIOME_FILTER:{
                            view = View.PICK_BIOME_FILTER;
                            break;
                        }
                    }
                } else if (clickedItem.equals(weightButton)){
                    handleWeightButton(e.getClick());
                } else if (clickedItem.equals(tierButton)){
                    handleTierButton(e.getClick());
                } else if (clickedItem.equals(dynamicModifierButton)){
                    playerMenuUtility.setPreviousMenu(this);
                    new DynamicModifierMenu(playerMenuUtility, this.currentModifiers).open();
                    return;
                } else if (clickedItem.equals(biomeFilterButton)){
                    currentPage = 0;
                    view = View.VIEW_BIOME_FILTER;
                } else if (clickedItem.equals(regionFilterButton)){
                    currentPage = 0;
                    view = View.VIEW_BIOME_FILTER;
                } else if (clickedItem.equals(saveButton)){
                    if (currentLootEntry != null){
                        if (drop == null || currentLootEntry.getName() == null){
                            playerMenuUtility.getOwner().sendMessage(Utils.chat("&cPlease enter a drop!"));
                        } else {
                            TieredLootEntry newEntry = new TieredLootEntry(currentLootEntry.getName(),
                                    tier, drop.clone(), weight, currentModifiers, biomeFilter, regionFilter);
                            currentLootTable.unRegisterEntry(currentLootEntry.getName());
                            currentLootTable.registerEntry(newEntry);
                            view = View.VIEW_ENTRIES;
                            playerMenuUtility.getOwner().sendMessage(Utils.chat("&aSaved Changes!"));
                        }
                    } else {
                        view = View.VIEW_ENTRIES;
                        playerMenuUtility.getOwner().sendMessage(Utils.chat("&cEntry has already been deleted."));
                    }
                } else if (clickedItem.equals(deleteButton)) {
                    if (currentLootEntry != null){
                        currentLootTable.unRegisterEntry(currentLootEntry.getName());
                        playerMenuUtility.getOwner().sendMessage(Utils.chat("&cEntry has been deleted"));
                    } else {
                        playerMenuUtility.getOwner().sendMessage(Utils.chat("&cEntry has already been deleted."));
                    }
                    view = View.VIEW_ENTRIES;
                }
            }
        }
        setMenuItems();
    }

    @Override
    public void handleMenu(InventoryDragEvent e) {
        setMenuItems();
    }

    @Override
    public void setMenuItems() {
        inventory.clear();
        for (int i = 0; i < getSlots(); i++){
            inventory.setItem(i, Utils.createItemStack(Material.GRAY_STAINED_GLASS_PANE, Utils.chat("&8 "), null));
        }
        switch (view){
            case VIEW_ENTRIES:{
                setViewEntriesMenu();
                break;
            }
            case VIEW_ENTRY: {
                setViewEntryMenu();
                break;
            }
            case VIEW_BIOME_FILTER:{
                setBiomeFilterMenu();
                break;
            }
            case PICK_BIOME_FILTER:{
                setPickBiomeFilterMenu();
                break;
            }
            case PICK_REGION_FILTER:{
                setPickRegionFilterMenu();
                break;
            }
            case VIEW_REGION_FILTER:{
                setRegionFilterMenu();
                break;
            }
        }
    }

    private void setPickRegionFilterMenu(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> regionIcons = new ArrayList<>();
        for (String r : Arrays.asList("Not Yet Implemented")){
            if (regionFilter.contains(r)) continue;
            ItemStack icon = new ItemStack(Material.PAPER);
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;
            meta.setDisplayName(Utils.chat("&r&f" + Utils.toPascalCase(r)));

            meta.getPersistentDataContainer().set(regionNameKey, PersistentDataType.STRING, r);

            icon.setItemMeta(meta);
            regionIcons.add(icon);
        }

        regionIcons.sort(Comparator.comparing(Utils::getItemName));
        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, regionIcons);

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

    private void setRegionFilterMenu(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> regionIcons = new ArrayList<>();
        for (String r : regionFilter){
            ItemStack icon = new ItemStack(Material.PAPER);
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;
            meta.setDisplayName(Utils.chat("&r&f" + Utils.toPascalCase(r)));

            meta.getPersistentDataContainer().set(regionNameKey, PersistentDataType.STRING, r);

            icon.setItemMeta(meta);
            regionIcons.add(icon);
        }

        regionIcons.sort(Comparator.comparing(Utils::getItemName));
//        regionIcons.add(newEntryButton);
        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, regionIcons);

        if (currentPage > pages.size()) currentPage = pages.size();
        if (currentPage < 1) currentPage = 1;

        if (pages.size() > 0){
            for (ItemStack i : pages.get(currentPage - 1)){
                inventory.addItem(i);
            }
        }

        inventory.setItem(22, Utils.createItemStack(Material.RED_STAINED_GLASS_PANE, Utils.chat("&cWorldGuard support not yet implemented"), null));
        inventory.setItem(45, previousPageButton);
        inventory.setItem(49, returnToMenuButton);
        inventory.setItem(53, nextPageButton);
    }

    private void setPickBiomeFilterMenu(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> biomeIcons = new ArrayList<>();
        for (Biome b : Biome.values()){
            if (biomeFilter.contains(b)) continue;
            ItemStack icon = new ItemStack(Material.PAPER);
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;
            meta.setLore(Collections.singletonList(Utils.chat("&8" + b.getKey().getNamespace() + ":" + b.getKey().getKey())));
            meta.setDisplayName(Utils.chat("&r&f" + Utils.toPascalCase(b.toString().replace("_", " "))));

            meta.getPersistentDataContainer().set(biomeNameKey, PersistentDataType.STRING, b.toString());

            icon.setItemMeta(meta);
            biomeIcons.add(icon);
        }

        biomeIcons.sort(Comparator.comparing(Utils::getItemName));
        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, biomeIcons);

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

    private void setBiomeFilterMenu(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> biomeIcons = new ArrayList<>();
        for (Biome b : biomeFilter){
            ItemStack icon = new ItemStack(Material.PAPER);
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;
            meta.setLore(Collections.singletonList(Utils.chat("&8" + b.getKey().getNamespace() + ":" + b.getKey().getKey())));
            meta.setDisplayName(Utils.chat("&r&f" + Utils.toPascalCase(b.toString().replace("_", " "))));

            meta.getPersistentDataContainer().set(biomeNameKey, PersistentDataType.STRING, b.toString());

            icon.setItemMeta(meta);
            biomeIcons.add(icon);
        }

        biomeIcons.sort(Comparator.comparing(Utils::getItemName));
        biomeIcons.add(newEntryButton);
        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, biomeIcons);

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

    private void setViewEntryMenu(){
        if (currentLootEntry == null){
            inventory.setItem(23, Utils.createItemStack(Material.RED_STAINED_GLASS_PANE,
                    Utils.chat("&cEntry was removed"),
                    null));
            inventory.setItem(49, returnToMenuButton);
        } else {
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

            List<String> biomeFilterLore = new ArrayList<>();
            for (Biome b : biomeFilter){
                biomeFilterLore.add(Utils.chat("&7- " + b));
            }
            ItemMeta biomeFilterMeta = biomeFilterButton.getItemMeta();
            assert biomeFilterMeta != null;
            biomeFilterMeta.setLore(biomeFilterLore);
            biomeFilterButton.setItemMeta(biomeFilterMeta);

            List<String> regionFilterLore = new ArrayList<>();
            for (String r : regionFilter){
                regionFilterLore.add(Utils.chat("&7- " + r));
            }
            ItemMeta regionFilterMeta = regionFilterButton.getItemMeta();
            assert regionFilterMeta != null;
            regionFilterMeta.setLore(regionFilterLore);
            regionFilterButton.setItemMeta(regionFilterMeta);

            ItemMeta weightMeta = weightButton.getItemMeta();
            assert weightMeta != null;
            weightMeta.setDisplayName(Utils.chat(String.format("&fWeight: &e%d", weight)));
            weightButton.setItemMeta(weightMeta);

            ItemMeta tierMeta = tierButton.getItemMeta();
            assert tierMeta != null;
            tierMeta.setDisplayName(Utils.chat(String.format("&fTier: &e%d", tier)));
            tierButton.setItemMeta(tierMeta);

            inventory.setItem(19, drop);
            inventory.setItem(11, tierButton);
            inventory.setItem(29, weightButton);
            inventory.setItem(22, dynamicModifierButton);
            inventory.setItem(16, biomeFilterButton);
            inventory.setItem(34, regionFilterButton);
            inventory.setItem(45, deleteButton);
            inventory.setItem(53, saveButton);
        }
    }

    private void setViewEntriesMenu(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> lootTableEntries = new ArrayList<>();
        List<TieredLootEntry> entries = new ArrayList<>(currentLootTable.getAllLootEntries().values());
        entries.sort(Comparator.comparing(TieredLootEntry::getTier));
        for (TieredLootEntry entry : entries){
            boolean unobtainable = false;
            ItemStack icon = currentLootTable.entryToItem(entry, false);
            if (icon == null) {
                icon = entry.getLoot();
                unobtainable = true;
            }
            if (icon == null) continue;
            icon = icon.clone();
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;
            List<String> lore = new ArrayList<>();
            if (unobtainable){
                lore.add(Utils.chat("&cWarning: Item is currently unobtainable."));
                lore.add(Utils.chat("&cItem is violating modifier conditions"));
            }
            lore.add(Utils.chat("&7Tier: &e" + entry.getTier()));
            lore.add(Utils.chat("&7Weight: &e" + entry.getWeight()));

            if (entry.getModifiers() != null){
                if (!entry.getModifiers().isEmpty()){
                    lore.add(Utils.chat("&8                                        "));
                    lore.add(Utils.chat("&7Executes modifiers:"));
                    List<DynamicItemModifier> modifiers = new ArrayList<>(entry.getModifiers());
                    modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));

                    for (DynamicItemModifier modifier : modifiers){
                        String craftDescription = modifier.getCraftDescription();
                        if (craftDescription != null){
                            if (!craftDescription.equals("")){
                                if (modifier instanceof TripleArgDynamicItemModifier){
                                    lore.add(Utils.chat("&7- &e" + craftDescription
                                            .replace("%strength%", "" + Utils.round(modifier.getStrength(), 2))
                                            .replace("%strength2%", "" + Utils.round(((TripleArgDynamicItemModifier) modifier).getStrength2(), 2))
                                            .replace("%strength3%", "" + Utils.round(((TripleArgDynamicItemModifier) modifier).getStrength3(), 2))));
                                } else if (modifier instanceof DuoArgDynamicItemModifier){
                                    lore.add(Utils.chat("&7- &e" + craftDescription
                                            .replace("%strength%", "" + Utils.round(modifier.getStrength(), 2))
                                            .replace("%strength2%", "" + Utils.round(((DuoArgDynamicItemModifier) modifier).getStrength2(), 2))));
                                } else {
                                    lore.add(Utils.chat("&7- &e" + craftDescription.replace("%strength%", "" + Utils.round(modifier.getStrength(), 2))));
                                }
                            }
                        }
                    }
                }
            }
            if (entry.getBiomeFilter() != null){
                if (!entry.getBiomeFilter().isEmpty()){
                    lore.add(Utils.chat("&8                                        "));
                    lore.add(Utils.chat("&7Only obtainable in biome(s):"));
                    for (Biome b : entry.getBiomeFilter()){
                        lore.add(Utils.chat("&7- &e" + Utils.toPascalCase(b.toString())));
                    }
                }
            }
            if (entry.getRegionFilter() != null){
                if (!entry.getRegionFilter().isEmpty()){
                    lore.add(Utils.chat("&8                                        "));
                    lore.add(Utils.chat("&7Only obtainable in region(s):"));
                    for (String r : entry.getRegionFilter()){
                        lore.add(Utils.chat("&7- &e" + r));
                    }
                }
            }
            meta.setLore(lore);

            meta.getPersistentDataContainer().set(entryNameKey, PersistentDataType.STRING, entry.getName());
            icon.setItemMeta(meta);
            lootTableEntries.add(icon);
        }
        lootTableEntries.add(newEntryButton);

        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, lootTableEntries);

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

    private enum View {
        VIEW_ENTRIES,
        VIEW_ENTRY,
        VIEW_BIOME_FILTER,
        VIEW_REGION_FILTER,
        PICK_BIOME_FILTER,
        PICK_REGION_FILTER
    }

    private void handleWeightButton(ClickType clickType){
        switch (clickType){
            case LEFT: this.weight += 1;
                break;
            case RIGHT: {
                if (this.weight - 1 < 0) this.weight = 0;
                else this.weight -= 1;
                break;
            }
            case SHIFT_LEFT: this.weight += 10;
                break;
            case SHIFT_RIGHT: {
                if (this.weight - 10 < 0) this.weight = 0;
                else this.weight -= 10;
            }
        }
    }

    private void handleTierButton(ClickType clickType){
        switch (clickType){
            case LEFT: this.tier += 1;
                break;
            case RIGHT: {
                if (this.tier - 1 < 0) this.tier = 0;
                else this.tier -= 1;
                break;
            }
            case SHIFT_LEFT: this.tier += 5;
                break;
            case SHIFT_RIGHT: {
                if (this.tier - 5 < 0) this.tier = 0;
                else this.tier -= 5;
            }
        }
    }
}
