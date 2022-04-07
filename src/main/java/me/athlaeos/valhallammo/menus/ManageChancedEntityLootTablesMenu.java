package me.athlaeos.valhallammo.menus;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.loottables.ChancedEntityLootEntry;
import me.athlaeos.valhallammo.loottables.ChancedEntityLootTable;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Biome;
import org.bukkit.entity.EntityType;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

public class ManageChancedEntityLootTablesMenu extends Menu {
    private final ItemStack nextPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lNext page"), null);
    private final ItemStack previousPageButton = Utils.createItemStack(Material.ARROW, Utils.chat("&7&lPrevious page"), null);
    private final ItemStack returnToMenuButton = Utils.createItemStack(Material.WRITABLE_BOOK, Utils.chat("&7&lReturn to menu"), null);
    private final ItemStack dropChanceButton = Utils.createItemStack(Material.PAPER, Utils.chat("&fDrop Chance: &e0.0%"),
            Utils.separateStringIntoLines(Utils.chat("&7Determines the chance the item will drop." +
                            " The lower, the more rare. ")
                    , 40));
    private final ItemStack overwriteDropsButton = Utils.createItemStack(Material.GOLD_INGOT, Utils.chat("&fOverwrite drops: &eFalse"),
            Utils.separateStringIntoLines(Utils.chat("&7If true, the block's regular drops will no longer drop and are " +
                            "instead replaced by the custom drop.")
                    , 40));
    private final ItemStack dynamicModifierButton = Utils.createItemStack(
            Material.BOOK,
            Utils.chat("&b&lDynamic Modifiers"), null);
    private final ItemStack biomeFilterButton = Utils.createItemStack(Material.GRASS_BLOCK, Utils.chat("&fBiome Filter"), null);
    private final ItemStack regionFilterButton = Utils.createItemStack(Material.NETHERRACK, Utils.chat("&fRegion Filter"), null);
    private final ItemStack newEntryButton = Utils.createItemStack(Material.GREEN_STAINED_GLASS_PANE, Utils.chat("&aAdd"), null);
    private final ItemStack saveButton = Utils.createItemStack(Material.STRUCTURE_VOID, Utils.chat("&aSave Changes"), null);
    private final ItemStack deleteButton = Utils.createItemStack(Material.BARRIER, Utils.chat("&cRemove Entry"), null);
    private ItemStack entityButton = Utils.createItemStack(Material.BARRIER, Utils.chat("&r&fEntity to drop custom item"), null);

    private final NamespacedKey entryNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "entry_identifier");
    private static final NamespacedKey entityNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "entity_identifier");
    private final NamespacedKey biomeNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "biome_identifier");
    private final NamespacedKey regionNameKey = new NamespacedKey(ValhallaMMO.getPlugin(), "region_identifier");

    private View view = View.VIEW_ENTRIES;
    private int currentPage = 0;
    private final ChancedEntityLootTable currentLootTable;
    private ChancedEntityLootEntry currentLootEntry = null;
    private ItemStack drop = Utils.createItemStack(Material.WHEAT_SEEDS, Utils.chat("&r&fPlace your own custom drop here :)"), null);
    private Set<String> regionFilter = new HashSet<>();
    private Set<Biome> biomeFilter = new HashSet<>();
    private double chance = 0;
    private boolean overwriteDrops = false;
    private EntityType entity = EntityType.ZOMBIE;
    private List<DynamicItemModifier> currentModifiers = new ArrayList<>();

    public ManageChancedEntityLootTablesMenu(PlayerMenuUtility playerMenuUtility, ChancedEntityLootTable table) {
        super(playerMenuUtility);
        this.currentLootTable = table;
        if (currentLootTable == null) {
            playerMenuUtility.getOwner().sendMessage(Utils.chat("&cLoot Table is null"));
            playerMenuUtility.getOwner().closeInventory();
        }
    }

    @Override
    public String getMenuName() {
        return Utils.chat("&7Chanced Loot Table: " + this.currentLootTable.getDisplayName());
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
                        ChancedEntityLootEntry entry = currentLootTable.getAllLootEntries().get(value);
                        if (entry == null){
                            playerMenuUtility.getOwner().sendMessage(Utils.chat("&cEntry has been removed"));
                        } else {
                            currentPage = 0;
                            currentLootEntry = entry;
                            view = View.VIEW_ENTRY;
                            this.chance = entry.getChance();
                            this.overwriteDrops = entry.isOverwriteNaturalDrops();
                            this.biomeFilter = entry.getBiomeFilter();
                            this.regionFilter = entry.getRegionFilter();
                            this.drop = entry.getLoot().clone();
                            this.entity = entry.getEntity();
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
                } else if (meta.getPersistentDataContainer().has(entityNameKey, PersistentDataType.STRING)){
                    if (view != View.VIEW_ENTRY){
                        String value = meta.getPersistentDataContainer().get(entityNameKey, PersistentDataType.STRING);
                        try {
                            entity = EntityType.valueOf(value);
                            if (getIcon(value) != null){
                                entityButton = getIcon(value);
                            }
                        } catch (IllegalArgumentException ignored){
                        }
                        view = View.VIEW_ENTRY;
                    } else {
                        view = View.PICK_ENTITY_TYPE;
                    }
                } else if (clickedItem.equals(nextPageButton)){
                    currentPage++;
                } else if (clickedItem.equals(drop)) {
                    if (!Utils.isItemEmptyOrNull(e.getCursor())){
                        drop = e.getCursor().clone();
                    }
                } else if (clickedItem.equals(entityButton)) {
                    view = View.PICK_ENTITY_TYPE;
                } else if (clickedItem.equals(previousPageButton)){
                    currentPage--;
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
                        case VIEW_REGION_FILTER:
                        case PICK_ENTITY_TYPE: {
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
                            currentLootEntry = new ChancedEntityLootEntry(name, EntityType.ZOMBIE, new ItemStack(Material.WHEAT_SEEDS),
                                    false, 0, new HashSet<>(), new HashSet<>(), new HashSet<>());
                            this.chance = currentLootEntry.getChance();
                            this.overwriteDrops = currentLootEntry.isOverwriteNaturalDrops();
                            this.biomeFilter = currentLootEntry.getBiomeFilter();
                            this.regionFilter = currentLootEntry.getRegionFilter();
                            this.drop = currentLootEntry.getLoot().clone();
                            this.entity = currentLootEntry.getEntity();
                            entityButton = getIcon(entity.toString());
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
                } else if (clickedItem.equals(dropChanceButton)){
                    handleChanceButton(e.getClick());
                } else if (clickedItem.equals(overwriteDropsButton)){
                    overwriteDrops = !overwriteDrops;
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
                        if (Utils.isItemEmptyOrNull(drop) || currentLootEntry.getName() == null){
                            playerMenuUtility.getOwner().sendMessage(Utils.chat("&cPlease enter a drop!"));
                        } else {
                            ChancedEntityLootEntry newEntry = new ChancedEntityLootEntry(currentLootEntry.getName(),
                                    entity, drop.clone(), overwriteDrops, chance, currentModifiers, biomeFilter, regionFilter);
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
            case PICK_ENTITY_TYPE:{
                setPickEntityTypeMenu();
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

    private void setPickEntityTypeMenu(){
        for (int i = 0; i < 45; i++){
            inventory.setItem(i, null);
        }
        List<ItemStack> entityIcons = new ArrayList<>();
        for (EntityType e : Arrays.stream(EntityType.values()).filter(EntityType::isAlive).collect(Collectors.toList())){
            if (entity == e) continue;
            if (currentLootTable.getCompatibleEntities().contains(e)){
                entityIcons.add(getIcon(e.toString()));
            }
        }

        Map<Integer, ArrayList<ItemStack>> pages = Utils.paginateItemStackList(45, entityIcons);

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

            ItemMeta dropChanceMeta = dropChanceButton.getItemMeta();
            assert dropChanceMeta != null;
            dropChanceMeta.setDisplayName(Utils.chat(String.format("&fDrop Chance: &e%.1f%%", chance * 100)));
            dropChanceButton.setItemMeta(dropChanceMeta);

            ItemMeta overwriteMeta = overwriteDropsButton.getItemMeta();
            assert overwriteMeta != null;
            overwriteMeta.setDisplayName(Utils.chat("&fOverwrite drops: &e" + (overwriteDrops ? "True" : "False")));
            overwriteDropsButton.setItemMeta(overwriteMeta);

            inventory.setItem(19, drop);
            inventory.setItem(20, entityButton);
            inventory.setItem(11, dropChanceButton);
            inventory.setItem(29, overwriteDropsButton);
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
        List<ChancedEntityLootEntry> entries = new ArrayList<>(currentLootTable.getAllLootEntries().values());
        entries.sort(Comparator.comparing(ChancedEntityLootEntry::getEntity));
        for (ChancedEntityLootEntry entry : entries){
            boolean unobtainable = false;

            ItemStack icon = currentLootTable.entryToItem(entry);
            if (icon == null) {
                icon = entry.getLoot();
                unobtainable = true;
            }
            if (Utils.isItemEmptyOrNull(icon)) {
                continue;
            }
            icon = icon.clone();
            ItemMeta meta = icon.getItemMeta();
            assert meta != null;
            List<String> lore = new ArrayList<>();
            if (unobtainable){
                lore.add(Utils.chat("&cWarning: Item is currently unobtainable."));
                lore.add(Utils.chat("&cItem is violating modifier conditions"));
            }
            lore.add(Utils.chat("&7Drops from: &e" + entry.getEntity()));
            lore.add(Utils.chat("&7Chance: &e" + Utils.round(entry.getChance() * 100, 2) + "%"));
            lore.add(Utils.chat("&7Overrides natural drops: &e" + (entry.isOverwriteNaturalDrops() ? "Yes" : "No")));
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
                        lore.add(Utils.chat("&7- &e" + Utils.toPascalCase(b.toString().replace("_", " "))));
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
        PICK_ENTITY_TYPE,
        PICK_REGION_FILTER
    }

    private void handleChanceButton(ClickType clickType){
        switch (clickType){
            case LEFT: {
                if (this.chance + 0.001 > 1) this.chance = 1;
                else this.chance += 0.001;
                break;
            }
            case RIGHT: {
                if (this.chance - 0.001 < 0) this.chance = 0;
                else this.chance -= 0.001;
                break;
            }
            case SHIFT_LEFT: {
                if (this.chance + 0.025 > 1) this.chance = 1;
                else this.chance += 0.025;
                break;
            }
            case SHIFT_RIGHT: {
                if (this.chance - 0.025 < 0) this.chance = 0;
                else this.chance -= 0.025;
            }
        }
    }

    private static final Map<String, ItemStack> entityIcons = loadEntityIcons();

    private static ItemStack getIcon(String icon){
        if (entityIcons.get(icon) != null) return entityIcons.get(icon);
        return createEntityIcon(icon, null);
    }

    private static Map<String, ItemStack> loadEntityIcons(){
        Map<String, ItemStack> icons = new HashMap<>();

        icons.put("AXOLOTL", validateAndReturnIcon("AXOLOTL", "AXOLOTL_BUCKET"));
        icons.put("BAT", validateAndReturnIcon("BAT", "BAT_SPAWN_EGG"));
        icons.put("BEE", validateAndReturnIcon("BEE", "HONEY_BOTTLE"));
        icons.put("BLAZE", validateAndReturnIcon("BLAZE", "BLAZE_ROD"));
        icons.put("CAT", validateAndReturnIcon("CAT", "STRING"));
        icons.put("CAVE_SPIDER", validateAndReturnIcon("CAVE_SPIDER", "SPIDER_EYE"));
        icons.put("CHICKEN", validateAndReturnIcon("CHICKEN", "FEATHER"));
        icons.put("COD", validateAndReturnIcon("COD", "COD"));
        icons.put("COW", validateAndReturnIcon("COW", "BEEF"));
        icons.put("CREEPER", validateAndReturnIcon("CREEPER", "GUNPOWDER"));
        icons.put("DOLPHIN", validateAndReturnIcon("DOLPHIN", "CONDUIT"));
        icons.put("DONKEY", validateAndReturnIcon("DONKEY", "CHEST"));
        icons.put("DROWNED", validateAndReturnIcon("DROWNED", "COPPER_INGOT"));
        icons.put("ELDER_GUARDIAN", validateAndReturnIcon("ELDER_GUARDIAN", "SPONGE"));
        icons.put("ENDER_DRAGON", validateAndReturnIcon("ENDER_DRAGON", "DRAGON_EGG"));
        icons.put("ENDERMAN", validateAndReturnIcon("ENDERMAN", "ENDER_EYE"));
        icons.put("ENDERMITE", validateAndReturnIcon("ENDERMITE", "ENDER_PEARL"));
        icons.put("EVOKER", validateAndReturnIcon("EVOKER", "TOTEM_OF_UNDYING"));
        icons.put("FOX", validateAndReturnIcon("FOX", "SWEET_BERRIES"));
        icons.put("GHAST", validateAndReturnIcon("GHAST", "GHAST_TEAR"));
        icons.put("GIANT", validateAndReturnIcon("GIANT", "BARRIER"));
        icons.put("GLOW_SQUID", validateAndReturnIcon("GLOW_SQUID", "GLOW_INK_SAC"));
        icons.put("GOAT", validateAndReturnIcon("GOAT", "GOAT_SPAWN_EGG"));
        icons.put("GUARDIAN", validateAndReturnIcon("GUARDIAN", "PRISMARINE_SHARD"));
        icons.put("HOGLIN", validateAndReturnIcon("HOGLIN", "CRIMSON_FUNGUS"));
        icons.put("HORSE", validateAndReturnIcon("HORSE", "SADDLE"));
        icons.put("HUSK", validateAndReturnIcon("HUST", "SAND"));
        icons.put("ILLUSIONER", validateAndReturnIcon("ILLUSIONER", "BARRIER"));
        icons.put("IRON_GOLEM", validateAndReturnIcon("IRON_GOLEM", "IRON_INGOT"));
        icons.put("LLAMA", validateAndReturnIcon("LLAMA", "WHITE_CARPET"));
        icons.put("MAGMA_CUBE", validateAndReturnIcon("MAGMA_CUBE", "MAGMA_CREAM"));
        icons.put("MULE", validateAndReturnIcon("MULE", "CHEST"));
        icons.put("MUSHROOM_COW", validateAndReturnIcon("MUSHROOM_COW", "RED_MUSHROOM"));
        icons.put("OCELOT", validateAndReturnIcon("OCELOT", "OCELOT_SPAWN_EGG"));
        icons.put("PANDA", validateAndReturnIcon("PANDA", "BAMBOO"));
        icons.put("PARROT", validateAndReturnIcon("PARROT", "WHEAT_SEEDS"));
        icons.put("PHANTOM", validateAndReturnIcon("PHANTOM", "PHANTOM_MEMBRANE"));
        icons.put("PIG", validateAndReturnIcon("PIG", "PORKCHOP"));
        icons.put("PIGLIN", validateAndReturnIcon("PIGLIN", "GOLD_INGOT"));
        icons.put("PIGLIN_BRUTE", validateAndReturnIcon("PIGLIN_BRUTE", "GOLDEN_AXE"));
        icons.put("PILLAGER", validateAndReturnIcon("PILLAGER", "CROSSBOW"));
        icons.put("PLAYER", validateAndReturnIcon("PLAYER", "PLAYER_HEAD"));
        icons.put("POLAR_BEAR", validateAndReturnIcon("POLAR_BEAR", "POLAR_BEAR_SPAWN_EGG"));
        icons.put("PUFFERFISH", validateAndReturnIcon("PUFFERFISH", "PUFFERFISH"));
        icons.put("RABBIT", validateAndReturnIcon("RABBIT", "RABBIT"));
        icons.put("RAVAGER", validateAndReturnIcon("RAVAGER", "RAVAGER_SPAWN_EGG"));
        icons.put("SALMON", validateAndReturnIcon("SALMON", "SALMON"));
        icons.put("SHEEP", validateAndReturnIcon("SHEEP", "WHITE_WOOL"));
        icons.put("SHULKER", validateAndReturnIcon("SHULKER", "SHULKER_SHELL"));
        icons.put("SILVERFISH", validateAndReturnIcon("SILVERFISH", "STONE_BRICKS"));
        icons.put("SKELETON", validateAndReturnIcon("SKELETON", "BOW"));
        icons.put("SKELETON_HORSE", validateAndReturnIcon("SKELETON_HORSE", "SKELETON_HORSE_SPAWN_EGG"));
        icons.put("SLIME", validateAndReturnIcon("SLIME", "SLIME_BALL"));
        icons.put("SNOWMAN", validateAndReturnIcon("SNOWMAN", "SNOWBALL"));
        icons.put("SPIDER", validateAndReturnIcon("SPIDER", "COBWEB"));
        icons.put("SQUID", validateAndReturnIcon("SQUID", "INK_SAC"));
        icons.put("STRAY", validateAndReturnIcon("STRAY", "TIPPED_ARROW"));
        icons.put("STRIDER", validateAndReturnIcon("STRIDER", "LAVA_BUCKET"));
        icons.put("TRADER_LLAMA", validateAndReturnIcon("TRADER_LLAMA", "TRADER_LLAMA_SPAWN_EGG"));
        icons.put("TROPICAL_FISH", validateAndReturnIcon("TROPICAL_FISH", "TROPICAL_FISH"));
        icons.put("TURTLE", validateAndReturnIcon("TURTLE", "SCUTE"));
        icons.put("VEX", validateAndReturnIcon("VEX", "GOLDEN_SWORD"));
        icons.put("VILLAGER", validateAndReturnIcon("VILLAGER", "EMERALD"));
        icons.put("VINDICATOR", validateAndReturnIcon("VINDICATOR", "IRON_AXE"));
        icons.put("WITCH", validateAndReturnIcon("WITCH", "SPLASH_POTION"));
        icons.put("WITHER", validateAndReturnIcon("WITHER", "NETHER_STAR"));
        icons.put("WITHER_SKELETON", validateAndReturnIcon("WITHER_SKELETON", "COAL"));
        icons.put("WOLF", validateAndReturnIcon("WOLF", "BONE"));
        icons.put("ZOGLIN", validateAndReturnIcon("ZOGLIN", "ZOGLIN_SPAWN_EGG"));
        icons.put("ZOMBIE", validateAndReturnIcon("ZOMBIE", "ROTTEN_FLESH"));
        icons.put("ZOMBIE_HORSE", validateAndReturnIcon("ZOMBIE_HORSE", "ZOMBIE_HORSE_SPAWN_EGG"));
        icons.put("ZOMBIE_VILLAGER", validateAndReturnIcon("ZOMBIE_VILLAGER", "ROTTEN_FLESH"));
        icons.put("ZOMBIFIED_PIGLIN", validateAndReturnIcon("ZOMBIFIED_PIGLIN", "GOLD_NUGGER"));

        return icons;
    }

    private static ItemStack validateAndReturnIcon(String type, String item){
        try {
            EntityType.valueOf(type);
            Material material = Material.valueOf(item);
            return createEntityIcon(type, material);
        } catch (IllegalArgumentException ignored){
        }
        return null;
    }

    private static ItemStack createEntityIcon(String type, Material icon){
        try {
            ItemStack i;
            EntityType entity = EntityType.valueOf(type);
            if (icon == null) {
                i = new ItemStack(Material.EXPERIENCE_BOTTLE);
            } else {
                i = new ItemStack(icon);
            }
            ItemMeta meta = i.getItemMeta();
            assert meta != null;
            meta.getPersistentDataContainer().set(entityNameKey, PersistentDataType.STRING, entity.toString());
            meta.setDisplayName(Utils.chat("&e" + Utils.toPascalCase(type.toString().replace("_", " "))));
            if (icon == null){
                meta.setLore(Arrays.asList(Utils.chat("&7" + type), Utils.chat("&cThis entity is not yet registered with this"), Utils.chat("&cplugin, please remind Athlaeos!"), Utils.chat("&cYou can keep using this entity")));
            } else {
                meta.setLore(Collections.singletonList(Utils.chat("&7" + type)));
            }
            i.setItemMeta(meta);
            return i;
        } catch (IllegalArgumentException ignored){
        }
        return null;
    }
}
