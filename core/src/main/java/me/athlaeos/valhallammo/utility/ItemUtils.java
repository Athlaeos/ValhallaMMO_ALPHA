package me.athlaeos.valhallammo.utility;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.recipetypes.AbstractCustomCraftingRecipe;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.items.enchantmentwrappers.EnchantmentWrapper;
import me.athlaeos.valhallammo.managers.CustomDurabilityManager;
import me.athlaeos.valhallammo.managers.CustomEnchantmentManager;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallatrinkets.TrinketsManager;
import org.bukkit.EntityEffect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.MetadataValue;

import java.util.*;

public class ItemUtils {
    private static final List<String> similarPlanks = Arrays.asList("OAK_PLANKS", "BIRCH_PLANKS",
            "CRIMSON_PLANKS", "DARK_OAK_PLANKS", "JUNGLE_PLANKS", "ACACIA_PLANKS",
            "SPRUCE_PLANKS", "WARPED_PLANKS");
    private static final List<String> similarLogs = Arrays.asList("OAK_LOG", "BIRCH_LOG", "DARK_OAK_LOG"
            , "JUNGLE_LOG", "ACACIA_LOG", "SPRUCE_LOG", "STRIPPED_ACACIA_LOG", "STRIPPED_BIRCH_LOG"
            , "STRIPPED_JUNGLE_LOG", "STRIPPED_SPRUCE_LOG", "STRIPPED_DARK_OAK_LOG", "STRIPPED_OAK_LOG",
            "STRIPPED_CRIMSON_STEM", "STRIPPED_WARPED_STEM", "CRIMSON_STEM", "WARPED_STEM");
    private static final List<String> similarStones = Arrays.asList("COBBLESTONE", "BLACKSTONE");
    private static final List<String> similarAnvils = Arrays.asList("ANVIL", "CHIPPED_ANVIL", "DAMAGED_ANVIL");
    private static final List<String> similarCauldrons = Arrays.asList("CAULDRON", "WATER_CAULDRON", "LAVA_CAULDRON", "POWDER_SNOW_CAULDRON");
    private static final List<String> similarWools = Arrays.asList("WHITE_WOOL", "BROWN_WOOL", "GRAY_WOOL", "LIGHT_GRAY_WOOL", "BLACK_WOOL",
            "BLUE_WOOL", "LIGHT_BLUE_WOOL", "CYAN_WOOL", "MAGENTA_WOOL", "PURPLE_WOOL", "PINK_WOOL", "RED_WOOL", "ORANGE_WOOL", "YELLOW_WOOL",
            "LIME_WOOL", "GREEN_WOOL");
    private static final List<String> similarSaplings = Arrays.asList("OAK_SAPLING", "BIRCH_SAPLING", "SPRUCE_SAPLING", "DARK_OAK_SAPLING", "ACACIA_SAPLING",
            "JUNGLE_SAPLING");
    private static final Collection<Collection<Material>> similarMaterialLists = new HashSet<>();
    private static final Collection<Material> totalSimilarItems = new HashSet<>();
    private static final Map<Material, Collection<Material>> similarItemsMap = new HashMap<>();

    private static final Collection<Material> filledBuckets = getMaterialList(Arrays.asList("MILK_BUCKET", "POWDER_SNOW_BUCKET", "LAVA_BUCKET", "WATER_BUCKET", "AXOLOTL_BUCKET", "COD_BUCKET", "PUFFERFISH_BUCKET", "SALMON_BUCKET", "TROPICAL_FISH_BUCKET"));

    public static void registerMaterials(){
        if (similarMaterialLists.isEmpty()){
            similarMaterialLists.add(getMaterialList(similarAnvils));
            similarMaterialLists.add(getMaterialList(similarStones));
            similarMaterialLists.add(getMaterialList(similarPlanks));
            similarMaterialLists.add(getMaterialList(similarLogs));
            similarMaterialLists.add(getMaterialList(similarCauldrons));
            similarMaterialLists.add(getMaterialList(similarWools));
            similarMaterialLists.add(getMaterialList(similarSaplings));
        }
        if (totalSimilarItems.isEmpty()){
            totalSimilarItems.addAll(getMaterialList(similarAnvils));
            totalSimilarItems.addAll(getMaterialList(similarStones));
            totalSimilarItems.addAll(getMaterialList(similarPlanks));
            totalSimilarItems.addAll(getMaterialList(similarLogs));
            totalSimilarItems.addAll(getMaterialList(similarCauldrons));
            totalSimilarItems.addAll(getMaterialList(similarWools));
            totalSimilarItems.addAll(getMaterialList(similarSaplings));
        }
        if (similarItemsMap.isEmpty()){
            similarItemsMap.put(Material.ANVIL, getMaterialList(similarAnvils));
            similarItemsMap.put(Material.COBBLESTONE, getMaterialList(similarStones));
            similarItemsMap.put(Material.OAK_PLANKS, getMaterialList(similarPlanks));
            similarItemsMap.put(Material.OAK_LOG, getMaterialList(similarLogs));
            similarItemsMap.put(Material.CAULDRON, getMaterialList(similarCauldrons));
            similarItemsMap.put(Material.WHITE_WOOL, getMaterialList(similarWools));
            similarItemsMap.put(Material.OAK_SAPLING, getMaterialList(similarSaplings));
        }
    }

    public static Collection<Material> getMaterialList(Collection<String> materials){
        Collection<Material> m = new HashSet<>();
        if (materials == null) return m;
        for (String s : materials){
            try {
                m.add(Material.valueOf(s));
            } catch (IllegalArgumentException ignored){
            }
        }
        return m;
    }

    public static Collection<Material> getSimilarMaterials(Material m){
        registerMaterials();
        if (totalSimilarItems.contains(m)){
            for (Collection<Material> similarMaterials : similarMaterialLists){
                if (similarMaterials.contains(m)){
                    return similarMaterials;
                }
            }
        }
        return null;
    }

    /**
     * If the item has several different variation of itself, such as anvils, this method will return the base version
     * of that variation. Example: CHIPPED_ANVIL will return ANVIL, SPRUCE_PLANKS will return OAK_PLANKS.
     * Base version of all anvils is ANVIL
     * Base version of all planks is OAK_PLANKS
     * Base version of all logs is OAK_LOG
     * Base version of all (cobble)stones is COBBLESTONE
     * Base version of all cauldrons is CAULDRON
     * @param m the material to get the base version of, if any
     * @return the base version of the material if it exists, or null otherwise
     */
    public static Material getBaseMaterial(Material m){
        registerMaterials();
        for (Material baseVersion : similarItemsMap.keySet()){
            if (similarItemsMap.get(baseVersion).contains(m)){
                return baseVersion;
            }
        }
        return null;
    }

    /**
     * Checks if material m is a similar variant to material compareTo
     * Example: if m is CHIPPED_ANVIL and compareTo is ANVIL, it will return true
     * If the material has no similar materials to it to compare to, such as GLASS, it return true
     * @param m the material to compare to compareTo
     * @param compareTo the material to compare m to
     * @return true if compareTo and m are similar OR if either have no similar materials to compare themselves to.
     * False if one of the items isn't similar to the other
     */
    public static boolean isSimilarTo(Material m, Material compareTo){
        registerMaterials();
        if (totalSimilarItems.contains(m) || totalSimilarItems.contains(compareTo)){
            Collection<Material> possibleMatches = new ArrayList<>();
            for (Collection<Material> similarMaterialList : similarMaterialLists){
                if (similarMaterialList.contains(m)){
                    possibleMatches = similarMaterialList;
                    break;
                }
            }
            return possibleMatches.contains(compareTo);
        }
        return m == compareTo;
    }

    /**
     * Checks if a player has the items required to craft a certain AbstractCraftingRecipe
     * @param recipe the recipe to check if the player can craft it
     * @param p the player crafting the recipe
     * @return true if the player has the materials to craft, false otherwise
     */
    public static boolean canCraft(AbstractCustomCraftingRecipe recipe, Player p, boolean useMeta){
        return canCraft(recipe.getIngredients(), p, useMeta);
    }

    public static boolean canCraft(Collection<ItemStack> ingredients, Player p, boolean useMeta){
        if (p.getGameMode() == GameMode.CREATIVE) return true;
        PlayerInventory inventory = p.getInventory();
        return canCraft(ingredients, inventory, useMeta);
    }

    public static boolean canCraft(Collection<ItemStack> ingredients, ItemStack[] inventoryItems, boolean useMeta){
        Inventory inventory = ValhallaMMO.getPlugin().getServer().createInventory(null, 54);
        inventory.addItem(inventoryItems);
        return canCraft(ingredients, inventory, useMeta);
    }

    public static int timesCraftable(Collection<ItemStack> ingredients, ItemStack[] inventoryItems, int maxAmount, boolean useMeta){
        Inventory inventory = ValhallaMMO.getPlugin().getServer().createInventory(null, 54);
        inventory.addItem(inventoryItems);
        return timesCraftable(ingredients, inventory, maxAmount, useMeta);
    }

    public static int timesCraftable(Collection<ItemStack> ingredients, Inventory inventory, int maxAmount, boolean useMeta){
        registerMaterials();
        if (maxAmount <= 0) return 0;
        Collection<Integer> timesCraftableCollection = new HashSet<>();
        mainIngredientLoop:
        for (ItemStack item : ingredients){
            ItemStack compareItem = item.clone();
            boolean has = false;
            if (useMeta){
                for (int i = maxAmount; i > 0; i--){
                    has = ValhallaMMO.isSpigot() ? containsAtLeast(Arrays.asList(inventory.getContents()), item, i * item.getAmount()) : inventory.containsAtLeast(item, i * item.getAmount());
                    if (has) {
                        timesCraftableCollection.add(i);
                        break;
                    }
                }
            } else {
                for (int i = maxAmount; i > 0; i--){
                    has = inventory.contains(item.getType(), i * item.getAmount());
                    if (has) {
                        timesCraftableCollection.add(i);
                        break;
                    }
                }
            }
            if (!has){
                if (totalSimilarItems.contains(item.getType())){
                    Collection<Material> possibleMatches = new ArrayList<>();
                    for (Collection<Material> similarMaterialList : similarMaterialLists){
                        if (similarMaterialList.contains(item.getType())){
                            possibleMatches = similarMaterialList;
                            break;
                        }
                    }
                    for (int i = maxAmount; i > 0; i--){
                        for (Material m : possibleMatches){
                            compareItem.setType(m);
                            if (inventory.containsAtLeast(compareItem, i * item.getAmount())){
                                timesCraftableCollection.add(i);
                                continue mainIngredientLoop;
                            }
                        }

                    }
                } else {
                    return 0;
                }
            }
        }
        return Math.max(0, timesCraftableCollection.isEmpty() ? 0 : Collections.min(timesCraftableCollection));
    }

    public static boolean canCraft(Collection<ItemStack> ingredients, Inventory inventory, boolean useMeta){
        registerMaterials();
        boolean canCraft = true;
        mainIngredientLoop:
        for (ItemStack i : ingredients){
            ItemStack compareItem = i.clone();
            boolean has;
            if (useMeta){
                has = ValhallaMMO.isSpigot() ? containsAtLeast(Arrays.asList(inventory.getContents()), i, i.getAmount()) : inventory.containsAtLeast(i, i.getAmount());
            } else {
                has = inventory.contains(i.getType(), i.getAmount());
            }
            if (!has){
                canCraft = false;
                if (totalSimilarItems.contains(i.getType())){
                    Collection<Material> possibleMatches = new ArrayList<>();
                    for (Collection<Material> similarMaterialList : similarMaterialLists){
                        if (similarMaterialList.contains(i.getType())){
                            possibleMatches = similarMaterialList;
                            break;
                        }
                    }
                    for (Material m : possibleMatches){
                        compareItem.setType(m);
                        if (inventory.containsAtLeast(compareItem, i.getAmount())){
                            canCraft = true;
                            continue mainIngredientLoop;
                        }
                    }
                } else {
                    break;
                }
            }
        }
        return canCraft;
    }

    public static boolean containsAtLeast(Collection<ItemStack> inventory, ItemStack item, int amount){
        if (Utils.isItemEmptyOrNull(item)) return false;
        Map<String, Integer> totalContents = getItemTotals(inventory);
        ItemStack itemClone = item.clone();
        itemClone.setAmount(1);
        int count = totalContents.getOrDefault(itemClone.toString(), 0);
        return count >= amount;
    }

    public static void removeItem(Inventory inventory, ItemStack item){
        if (Utils.isItemEmptyOrNull(item)) return;
        int required = item.getAmount();
        for (int i = 0; i < inventory.getContents().length; i++){
            ItemStack indexItem = inventory.getItem(i);
            if (Utils.isItemEmptyOrNull(indexItem)) continue;
            if (isSimilar(item, indexItem)){
                if (indexItem.getAmount() < required){
                    required -= indexItem.getAmount();
                    inventory.setItem(i, null);
                } else if (indexItem.getAmount() == required){
                    inventory.setItem(i, null);
                    return;
                } else {
                    indexItem.setAmount(indexItem.getAmount() - required);
                    inventory.setItem(i, indexItem);
                    return;
                }
            }
        }
    }

    public static boolean isSimilar(ItemStack i1, ItemStack i2){
        if (Utils.isItemEmptyOrNull(i1) || Utils.isItemEmptyOrNull(i2)) return false;
        ItemStack item1 = i1.clone();
        ItemStack item2 = i2.clone();
        item1.setAmount(1);
        item2.setAmount(1);
        return item1.toString().equalsIgnoreCase(item2.toString());
    }

    public static Map<String, Integer> getItemTotals(Collection<ItemStack> items){
        Map<String, Integer> totals = new HashMap<>();
        for (ItemStack i : items){
            if (Utils.isItemEmptyOrNull(i)) continue;
            ItemStack clone = i.clone();
            int itemAmount = i.getAmount();
            clone.setAmount(1);
            int existingAmount = totals.getOrDefault(clone.toString(), 0);
            totals.put(clone.toString(), existingAmount + itemAmount);
        }
        return totals;
    }

    public static void removeItems(Player p, Collection<ItemStack> items, boolean perfectMeta){
        registerMaterials();
        if (p.getGameMode() == GameMode.CREATIVE) return;
        removeItems(p.getInventory(), items, perfectMeta);
    }
    public static void removeItems(Inventory inventory, Collection<ItemStack> items, boolean perfectMeta){
        registerMaterials();
        mainIngredientLoop:
        for (ItemStack ingredient : items){
            boolean contains = (perfectMeta) ? (ValhallaMMO.isSpigot() ? containsAtLeast(Arrays.asList(inventory.getContents()), ingredient, ingredient.getAmount()) : inventory.containsAtLeast(ingredient, ingredient.getAmount())) : inventory.contains(ingredient.getType());
            if (contains){
                if (perfectMeta){
                    if (ValhallaMMO.isSpigot()){
                        removeItem(inventory, ingredient);
                    } else {
                        inventory.removeItem(ingredient);
                    }
                    if (filledBuckets.contains(ingredient.getType())){
                        inventory.addItem(new ItemStack(Material.BUCKET, ingredient.getAmount()));
                    }
                } else {
                    removeMaterials(inventory, ingredient.getType(), ingredient.getAmount());
                }
            } else {
                if (totalSimilarItems.contains(ingredient.getType())){
                    ItemStack compareItem = ingredient.clone();
                    Collection<Material> possibleMatches = new ArrayList<>();
                    for (Collection<Material> similarMaterialList : similarMaterialLists){
                        if (similarMaterialList.contains(ingredient.getType())){
                            possibleMatches = similarMaterialList;
                            break;
                        }
                    }

                    for (Material m : possibleMatches){
                        compareItem.setType(m);
                        boolean contains2 = (perfectMeta) ? inventory.containsAtLeast(compareItem, compareItem.getAmount()) : inventory.contains(compareItem.getType());
                        if (contains2){
                            if (perfectMeta){
                                if (ValhallaMMO.isSpigot()){
                                    removeItem(inventory, compareItem);
                                } else {
                                    inventory.removeItem(compareItem);
                                }
                                if (filledBuckets.contains(ingredient.getType())){
                                    inventory.addItem(new ItemStack(Material.BUCKET, ingredient.getAmount()));
                                }
                            } else {
                                removeMaterials(inventory, compareItem.getType(), compareItem.getAmount());
                            }
                            continue mainIngredientLoop;
                        }
                    }
                }
            }
        }
    }
    public static void removeItems(Inventory inventory, Collection<ItemStack> items, int multiplyBy, boolean perfectMeta){
        registerMaterials();
        mainIngredientLoop:
        for (ItemStack ingredient : items){
            boolean contains = (perfectMeta) ? (ValhallaMMO.isSpigot() ? containsAtLeast(Arrays.asList(inventory.getContents()), ingredient, ingredient.getAmount() * multiplyBy) : inventory.containsAtLeast(ingredient, ingredient.getAmount() * multiplyBy)) : inventory.contains(ingredient.getType());
            if (contains){
                if (perfectMeta){
                    if (ValhallaMMO.isSpigot()){
                        removeItem(inventory, ingredient);
                    } else {
                        inventory.removeItem(ingredient);
                    }
                    if (filledBuckets.contains(ingredient.getType())){
                        inventory.addItem(new ItemStack(Material.BUCKET, ingredient.getAmount() * multiplyBy));
                    }
                } else {
                    removeMaterials(inventory, ingredient.getType(), ingredient.getAmount() * multiplyBy);
                }
            } else {
                if (totalSimilarItems.contains(ingredient.getType())){
                    ItemStack compareItem = ingredient.clone();
                    Collection<Material> possibleMatches = new ArrayList<>();
                    for (Collection<Material> similarMaterialList : similarMaterialLists){
                        if (similarMaterialList.contains(ingredient.getType())){
                            possibleMatches = similarMaterialList;
                            break;
                        }
                    }

                    for (Material m : possibleMatches){
                        compareItem.setType(m);
                        boolean contains2 = (perfectMeta) ? inventory.containsAtLeast(compareItem, compareItem.getAmount() * multiplyBy) : inventory.contains(compareItem.getType());
                        if (contains2){
                            if (perfectMeta){
                                if (ValhallaMMO.isSpigot()){
                                    removeItem(inventory, compareItem);
                                } else {
                                    inventory.removeItem(compareItem);
                                }
                                if (filledBuckets.contains(ingredient.getType())){
                                    inventory.addItem(new ItemStack(Material.BUCKET, ingredient.getAmount() * multiplyBy));
                                }
                            } else {
                                removeMaterials(inventory, compareItem.getType(), compareItem.getAmount() * multiplyBy);
                            }
                            continue mainIngredientLoop;
                        }
                    }
                }
            }
        }
    }

    public static void removeMaterials(Inventory inventory, Material type, int amount) {
        if (amount <= 0) return;
        int size = inventory.getSize();
        if (filledBuckets.contains(type)) {
            inventory.addItem(new ItemStack(Material.BUCKET, amount));
        }
        for (int slot = 0; slot < size; slot++) {
            ItemStack is = inventory.getItem(slot);
            if (is == null) continue;
            if (type == is.getType()) {
                int newAmount = is.getAmount() - amount;
                if (newAmount > 0) {
                    is.setAmount(newAmount);
                    break;
                } else {
                    inventory.clear(slot);
                    amount = -newAmount;
                    if (amount == 0) break;
                }
            }
        }
    }

    public static EquipmentSlot getEquipmentSlot(ItemStack m){
        if (ValhallaMMO.isTrinketsHooked()){
            if (TrinketsManager.getInstance().getTrinketType(m) != null) return EquipmentSlot.CHEST;
        }
        EquipmentClass damageableClass = EquipmentClass.getClass(m);
        if (damageableClass != null){
            switch (damageableClass) {
                case BOOTS: return EquipmentSlot.FEET;
                case LEGGINGS: return EquipmentSlot.LEGS;
                case CHESTPLATE:
                case ELYTRA: return EquipmentSlot.CHEST;
                case HELMET: return EquipmentSlot.HEAD;
                default: return EquipmentSlot.HAND;
            }
        }
        return EquipmentSlot.HAND;
    }
    public static void calculateClickedSlot(InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        if (!Utils.isItemEmptyOrNull(cursor)) {
            ItemStack clickedItem = event.getCurrentItem();
            if (event.getClick().equals(ClickType.LEFT)) {
                if (!Utils.isItemEmptyOrNull(clickedItem)) {
                    event.setCancelled(true);
                    if (clickedItem.isSimilar(cursor)) {
                        int possibleAmount = clickedItem.getMaxStackSize() - clickedItem.getAmount();
                        clickedItem.setAmount(clickedItem.getAmount() + Math.min(cursor.getAmount(), possibleAmount));
                        cursor.setAmount(cursor.getAmount() - possibleAmount);
                        event.setCurrentItem(clickedItem);
                        //event.getCursor().setAmount(0);
                    } else {
                        ItemStack cursorClone = cursor.clone();
                        event.getWhoClicked().setItemOnCursor(event.getCurrentItem());
                        event.setCurrentItem(cursorClone);
                        //event.getCursor().setAmount(0);
                    }
                } else if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    event.setCancelled(true);
                    event.setCurrentItem(cursor.clone());
                    event.getCursor().setAmount(0);
                }
            } else if (!Utils.isItemEmptyOrNull(clickedItem)) {
                if (clickedItem.isSimilar(cursor)) {
                    if (clickedItem.getAmount() < clickedItem.getMaxStackSize() && cursor.getAmount() > 0) {
                        event.setCancelled(true);
                        clickedItem.setAmount(clickedItem.getAmount() + 1);
                        cursor.setAmount(cursor.getAmount() - 1);
                    }
                } else {
                    event.setCancelled(true);
                    event.setCurrentItem(cursor.clone());
                    event.getCursor().setAmount(0);
                }
            } else {
                event.setCancelled(true);
                ItemStack itemStack = cursor.clone();
                cursor.setAmount(cursor.getAmount() - 1);
                itemStack.setAmount(1);
                event.setCurrentItem(itemStack);
            }

            if (event.getWhoClicked() instanceof Player) {
                ((Player)event.getWhoClicked()).updateInventory();
            }
        }
    }

    public static void calculateClickedSlotOnlyAllow1Placed(InventoryClickEvent event) {
        ItemStack cursor = event.getCursor();
        if (!Utils.isItemEmptyOrNull(cursor)) {
            ItemStack clickedItem = event.getCurrentItem();
            if (event.getClick().equals(ClickType.LEFT)) {
                if (!Utils.isItemEmptyOrNull(clickedItem)) {
                    event.setCancelled(true);
                    if (!clickedItem.isSimilar(cursor) && cursor.getAmount() == 1) {
                        // you may only swap if the cursor amount is 1
                        ItemStack cursorClone = cursor.clone();
                        event.getWhoClicked().setItemOnCursor(event.getCurrentItem());
                        event.setCurrentItem(cursorClone);
                    }
                    // else neither cursor or clicked items are empty, and both are similar to eachother, but since only 1
                    // should be transferrable anyway nothing should happen
                } else if (!event.getAction().equals(InventoryAction.PICKUP_ALL)) {
                    event.setCancelled(true);
                    ItemStack cursorclone = cursor.clone();
                    cursorclone.setAmount(1);
                    cursor.setAmount(cursor.getAmount() - 1);
                    event.setCurrentItem(cursorclone);
                }
            } else if (!Utils.isItemEmptyOrNull(clickedItem)) {
                if (!clickedItem.isSimilar(cursor) && cursor.getAmount() == 1) {
                    // you may only swap if the cursor amount is 1
                    event.setCancelled(true);
                    event.setCurrentItem(cursor.clone());
                    event.getCursor().setAmount(0);
                }
                // else neither cursor or clicked items are empty, and both are similar to eachother, but since only 1
                // should be transferrable anyway nothing should happen
            } else {
                event.setCancelled(true);
                ItemStack itemStack = cursor.clone();
                cursor.setAmount(cursor.getAmount() - 1);
                itemStack.setAmount(1);
                event.setCurrentItem(itemStack);
            }

            if (event.getWhoClicked() instanceof Player) {
                ((Player)event.getWhoClicked()).updateInventory();
            }
        }
    }

    public static Collection<Material> getFilledBuckets() {
        return filledBuckets;
    }


    /*
     * Due to the dangerous design of this utility as it is very prone to misuse, abuse, and can lead to a lot of damage
     * if in the wrong hands, I decided to not implement this into ValhallaMMO. Perhaps in another plugin, with a big
     * disclaimer/safety notice attached. We wouldn't want an untrustworthy admin or hacked op account getting their hands
     * on the ability to make items that just make everyone op if they use them. items can get kinda lost too so it'll
     * be hard to track them down if it ever happens. would kinda be like lord of the rings where sauron makes the one
     * ring but then hes a stupid idiot and loses it and then some small man finds it again and its just really not
     * cool for everyone involved and it leads to someone getting his finger bitten off and its bad
     */
//    private static final NamespacedKey boundCommandsKey = new NamespacedKey(ValhallaMMO.getPlugin(), "command_item_bound_commands");
//    private static final NamespacedKey consumedKey = new NamespacedKey(ValhallaMMO.getPlugin(), "command_item_consumed");
//    private static final NamespacedKey limitedFor = new NamespacedKey(ValhallaMMO.getPlugin(), "command_item_limited_for");
//
//    public static void setConsumable(ItemStack item, boolean consumable){
//        if (item == null) return;
//        if (!item.hasItemMeta()) return;
//        ItemMeta meta = item.getItemMeta();
//        assert meta != null;
//        if (consumable){
//            meta.getPersistentDataContainer().set(consumedKey, PersistentDataType.BYTE, (byte) 0);
//        } else {
//            meta.getPersistentDataContainer().remove(consumedKey);
//        }
//        item.setItemMeta(meta);
//    }
//
//    public static void setLimitedFor(ItemStack item, Player player){
//        if (item == null) return;
//        if (!item.hasItemMeta()) return;
//        ItemMeta meta = item.getItemMeta();
//        assert meta != null;
//        if (player != null){
//            meta.getPersistentDataContainer().set(limitedFor, PersistentDataType.STRING, player.getUniqueId().toString());
//        } else {
//            meta.getPersistentDataContainer().remove(limitedFor);
//        }
//        item.setItemMeta(meta);
//    }
//
//    public static boolean isConsumable(ItemStack item){
//        if (item == null) return false;
//        if (!item.hasItemMeta()) return false;
//        ItemMeta meta = item.getItemMeta();
//        assert meta != null;
//        return meta.getPersistentDataContainer().has(consumedKey, PersistentDataType.BYTE);
//    }
//
//    public static boolean executesFor(ItemStack item, Player p){
//        if (item == null) return true;
//        if (!item.hasItemMeta()) return true;
//        ItemMeta meta = item.getItemMeta();
//        assert meta != null;
//        String stored = meta.getPersistentDataContainer().get(limitedFor, PersistentDataType.STRING);
//        if (stored == null) return true;
//        return stored.equals(p.getUniqueId().toString());
//    }
//
//    public static void addBoundCommand(ItemStack item, String command){
//        if (item == null || command == null) return;
//        if (!item.hasItemMeta()) return;
//        if (command.equals("")) return;
//        ItemMeta meta = item.getItemMeta();
//        assert meta != null;
//
//        Set<String> storedCommands = getBoundCommands(item);
//        storedCommands.add(command);
//        setBoundCommands(item, storedCommands);
//    }
//
//    public static Set<String> getBoundCommands(ItemStack item){
//        Set<String> boundCommands = new HashSet<>();
//        if (item == null) return boundCommands;
//        if (!item.hasItemMeta()) return boundCommands;
//        ItemMeta meta = item.getItemMeta();
//        assert meta != null;
//        String storedStringCommands = meta.getPersistentDataContainer().get(boundCommandsKey, PersistentDataType.STRING);
//        if (storedStringCommands == null) return boundCommands;
//        boundCommands.addAll(Arrays.asList(storedStringCommands.split("%s%")));
//        return boundCommands;
//    }
//
//    public static void setBoundCommands(ItemStack item, Set<String> commands){
//        if (item == null) return;
//        if (!item.hasItemMeta()) return;
//        ItemMeta meta = item.getItemMeta();
//        assert meta != null;
//        if (commands != null){
//            if (!commands.isEmpty()){
//                String storedStringCommands = String.join("%s%", commands);
//                meta.getPersistentDataContainer().set(boundCommandsKey, PersistentDataType.STRING, storedStringCommands);
//                item.setItemMeta(meta);
//                return;
//            }
//        }
//        meta.getPersistentDataContainer().remove(boundCommandsKey);
//        item.setItemMeta(meta);
//    }
//
//    public static void executeBoundCommands(ItemStack item, Player user){
//        if (!executesFor(item, user)) return;
//        Set<String> storedCommands = getBoundCommands(item);
//        for (String command : storedCommands){
//            ValhallaMMO.getPlugin().getServer().dispatchCommand(user, command.replace("%player%", user.getName()));
//        }
//        if (isConsumable(item)){
//            if (item.getAmount() == 1){
//                item.setType(Material.AIR);
//            } else {
//                item.setAmount(item.getAmount() - 1);
//            }
//        }
//    }

    // B = base enchantment level
    // E = enchantability item
    // R1 & R2 = randInt(0, E / 4)
    // enchantment level = B + R1 + R2 + 1
    // any division is rounded down
    // random = 1 + (randfloat() + randfloat() - 1) * 0.15
    // finallevel = Math.min(1, round(enchantmentlevel * random))

    private static final Map<Enchantment, E> enchantments = new HashMap<>();
    private static final Map<Material, Integer> itemEnchant_ability = new HashMap<>();

    public static void enchantItem(ItemStack i, int level, boolean treasure){
        if (enchantments.isEmpty()) registerEnchantments();
        if (itemEnchant_ability.isEmpty()) registerItemEnchant_ability();
        if (i == null || level == 0) return;
        boolean isBook = i.getType() == Material.ENCHANTED_BOOK || i.getType() == Material.BOOK;
        int modifiedEnchantmentLevel = getModifiedEnchantmentLevel(i, level);
        Map<Enchantment, Integer> possibleEnchantments = new HashMap<>();
        Map<Enchantment, Integer> chosenEnchantments = new HashMap<>();
        for (Enchantment e : enchantments.keySet()){
            if (!treasure && e.isTreasure()) {
                continue;
            }
            if (e.canEnchantItem(i) || isBook){
                int maxLevel = enchantments.get(e).getAmplifierForLevel(modifiedEnchantmentLevel);
                if (maxLevel > 0){
                    possibleEnchantments.put(e, maxLevel);
                }
            }
        }
        // list of possible enchantments now determined
        Map<Enchantment, E> availableEnchantments = new HashMap<>(enchantments);
        for (Enchantment e : enchantments.keySet()){
            if (!possibleEnchantments.containsKey(e)) {
                availableEnchantments.remove(e);
            }
        }
        Enchantment firstEnchantmentRoll = pickRandomEnchantment(availableEnchantments);
        if (firstEnchantmentRoll != null) {
            availableEnchantments.remove(firstEnchantmentRoll);
            int lv = possibleEnchantments.get(firstEnchantmentRoll);
            if (lv > 0){
                chosenEnchantments.put(firstEnchantmentRoll, lv);
            }
        }

        int limit = 10;
        while (Utils.getRandom().nextDouble() <= (modifiedEnchantmentLevel + 1) / 50D){
            if (limit <= 0) break;
            Enchantment additionalEnchantmentRoll = pickRandomEnchantment(availableEnchantments);
            if (additionalEnchantmentRoll == null) return;
            availableEnchantments.remove(additionalEnchantmentRoll);
            int additionalEnchantmentRollLv = possibleEnchantments.get(additionalEnchantmentRoll);
            if (additionalEnchantmentRollLv > 0){
                chosenEnchantments.put(additionalEnchantmentRoll, additionalEnchantmentRollLv);
            }

            modifiedEnchantmentLevel = (int) Math.floor(modifiedEnchantmentLevel / 2D);
            limit--;
        }

        if (chosenEnchantments.isEmpty()) return;

        if (i.getType() == Material.BOOK) i.setType(Material.ENCHANTED_BOOK);

        if (i.getItemMeta() instanceof EnchantmentStorageMeta){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) i.getItemMeta();
            for (Enchantment e : chosenEnchantments.keySet()){
                meta.addStoredEnchant(e, chosenEnchantments.get(e), false);
            }
            i.setItemMeta(meta);
        } else {
            i.addEnchantments(chosenEnchantments);
        }
    }

    /**
     * Damages the given item by an amount
     * @param who the player possessing the item
     * @param item the item to damage
     * @param damage the amount to damage it
     * @param breakEffect the effect to play should the item break
     * @return true if the item would break as a result of the damage dealt, false otherwise
     */
    public static boolean damageItem(Player who, ItemStack item, int damage, EntityEffect breakEffect){
        return damageItem(who, item, damage, breakEffect, false);
    }
    /**
     * Damages the given item by an amount. If "respectAttributes" is true, attributes such as the Unbreaking enchantment
     * the "UNBREAKABLE" item flag are respected.
     * If Unbreaking procs it will not always prevent damage, but it will multiply the damage by the chance for unbreaking items to take damage
     * This chance is equal to 1/(unbreakingLevel + 1), so 4 damage would be reduced to 1 with unbreaking 3.
     * @param who the player possessing the item
     * @param item the item to damage
     * @param damage the amount to damage it
     * @param breakEffect the effect to play should the item break
     * @return true if the item would break as a result of the damage dealt, false otherwise
     */
    public static boolean damageItem(Player who, ItemStack item, int damage, EntityEffect breakEffect, boolean respectAttributes){
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable){
            if (respectAttributes){
                if (item.getItemMeta().isUnbreakable()) return false;
                int unbreakableLevel = item.getEnchantmentLevel(Enchantment.DURABILITY);
                if (unbreakableLevel > 0){
                    double damageChance = 1D / (unbreakableLevel + 1D);
                    damage = Utils.excessChance(damage * damageChance);
                    if (damage == 0) return false;
                }
            }
            PlayerItemDamageEvent event = new PlayerItemDamageEvent(who, item, damage);
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()){
                if (!SmithingItemTreatmentManager.getInstance().isItemCustom(item)){
                    Damageable damageable = (Damageable) meta;
                    damageable.setDamage(damageable.getDamage() + damage);
                    if (damageable.getDamage() > item.getType().getMaxDurability()){
                        item.setType(Material.AIR);
                        who.playEffect(breakEffect);
                        return true;
                    } else {
                        item.setItemMeta(damageable);
                    }
                } else {
                    if (CustomDurabilityManager.getInstance().getDurability(item) <= 0){
                        item.setType(Material.AIR);
                        who.playEffect(breakEffect);
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public static boolean shouldItemBreak(ItemStack item){
        if (item == null) return false;
        ItemMeta meta = item.getItemMeta();
        if (meta instanceof Damageable && item.getType().getMaxDurability() > 0){
            Damageable damageable = (Damageable) meta;
            if (CustomDurabilityManager.getInstance().hasCustomDurability(item)){
                return CustomDurabilityManager.getInstance().getDurability(item) <= 0;
            } else {
                return damageable.getDamage() >= item.getType().getMaxDurability();
            }
        }
        return false;
    }

    private static Enchantment pickRandomEnchantment(Map<Enchantment, E> availableEnchantments){
        int combinedWeight = 0;
        for (E e : availableEnchantments.values()){
            combinedWeight += e.getWeight();
        }
        if (combinedWeight == 0) return null;
        int randInt = Utils.getRandom().nextInt(combinedWeight);
        for (Enchantment e : availableEnchantments.keySet()){
            randInt -= availableEnchantments.get(e).getWeight();
            if (randInt < 0) return e;
        }
        return null;
    }

    private static int getModifiedEnchantmentLevel(ItemStack i, int b){
        int e = 1;
        if (itemEnchant_ability.containsKey(i.getType())) {
            e = itemEnchant_ability.get(i.getType());
        }
        int r1 = (e >= 4) ? Utils.getRandom().nextInt((int) Math.floor(e / 4D)) : 1;
        int r2 = (e >= 4) ? Utils.getRandom().nextInt((int) Math.floor(e / 4D)) : 1;
        int enchantment_level = b + r1 + r2 + 1;

        double random = 1 + (Utils.getRandom().nextFloat() + Utils.getRandom().nextFloat() - 1) * 0.15;
        return Math.max(1, (int) Math.round(enchantment_level * random));
    }

    private static void registerItemEnchant_ability(){
        registerItem(15, Material.WOODEN_AXE, Material.WOODEN_PICKAXE, Material.WOODEN_SWORD, Material.WOODEN_HOE, Material.WOODEN_SHOVEL, Material.LEATHER_HELMET, Material.LEATHER_CHESTPLATE, Material.LEATHER_LEGGINGS, Material.LEATHER_BOOTS);
        registerItem(5, Material.STONE_AXE, Material.STICKY_PISTON, Material.STONE_SWORD, Material.STONE_HOE, Material.STONE_SHOVEL);
        registerItem(12, Material.CHAINMAIL_HELMET, Material.CHAINMAIL_CHESTPLATE, Material.CHAINMAIL_LEGGINGS, Material.CHAINMAIL_BOOTS);
        registerItem(9, Material.IRON_HELMET, Material.IRON_CHESTPLATE, Material.IRON_LEGGINGS, Material.IRON_BOOTS);
        registerItem(14, Material.IRON_AXE, Material.IRON_PICKAXE, Material.IRON_SWORD, Material.IRON_HOE, Material.IRON_SHOVEL);
        registerItem(25, Material.GOLDEN_HELMET, Material.GOLDEN_CHESTPLATE, Material.GOLDEN_LEGGINGS, Material.GOLDEN_BOOTS);
        registerItem(22, Material.GOLDEN_AXE, Material.GOLDEN_PICKAXE, Material.GOLDEN_SWORD, Material.GOLDEN_HOE, Material.GOLDEN_SHOVEL);
        registerItem(10, Material.DIAMOND_AXE, Material.DIAMOND_PICKAXE, Material.DIAMOND_SWORD, Material.DIAMOND_HOE, Material.DIAMOND_SHOVEL, Material.DIAMOND_HELMET, Material.DIAMOND_CHESTPLATE, Material.DIAMOND_LEGGINGS, Material.DIAMOND_BOOTS);
        registerItem(9, Material.TURTLE_HELMET);
        registerItem(15, Material.NETHERITE_AXE, Material.NETHERITE_PICKAXE, Material.NETHERITE_SWORD, Material.NETHERITE_HOE, Material.NETHERITE_SHOVEL, Material.NETHERITE_HELMET, Material.NETHERITE_CHESTPLATE, Material.NETHERITE_LEGGINGS, Material.NETHERITE_BOOTS);
    }

    private static void registerItem(int enchant_ability, Material... materials){
        for (Material m : materials){
            itemEnchant_ability.put(m, enchant_ability);
        }
    }

    private static void registerEnchantments(){
        enchantments.put(Enchantment.ARROW_DAMAGE, new E(10).range(1, 1, 16).range(2, 11, 26).range(3, 21, 36).range(4, 31, 46).range(5, 41, 56));
        enchantments.put(Enchantment.ARROW_FIRE, new E(2).range(1, 20, 50));
        enchantments.put(Enchantment.ARROW_INFINITE, new E(1).range(1, 20, 50));
        enchantments.put(Enchantment.ARROW_KNOCKBACK, new E(2).range(1, 12, 37).range(2, 32, 57));
        enchantments.put(Enchantment.BINDING_CURSE, new E(1).range(1, 25, 50));
        enchantments.put(Enchantment.CHANNELING, new E(1).range(1, 25, 50));
        enchantments.put(Enchantment.DAMAGE_ALL, new E(10).range(1, 1, 21).range(2, 12, 32).range(3, 23, 43).range(4, 34, 54).range(5, 45, 65));
        enchantments.put(Enchantment.DAMAGE_ARTHROPODS, new E(5).range(1, 5, 25).range(2, 13, 33).range(3, 21, 41).range(4, 29, 49).range(5, 37, 57));
        enchantments.put(Enchantment.DAMAGE_UNDEAD, new E(5).range(1, 5, 25).range(2, 13, 33).range(3, 21, 41).range(4, 29, 49).range(5, 37, 57));
        enchantments.put(Enchantment.DEPTH_STRIDER, new E(2).range(1, 10, 25).range(2, 20, 35).range(3, 30, 45));
        enchantments.put(Enchantment.DIG_SPEED, new E(10).range(1, 1, 51).range(2, 11, 61).range(3, 21, 71).range(4, 31, 81).range(5, 41, 91));
        enchantments.put(Enchantment.DURABILITY, new E(5).range(1, 5, 55).range(2, 13, 63).range(3, 21, 71));
        enchantments.put(Enchantment.FIRE_ASPECT, new E(2).range(1, 10, 60).range(2, 30, 80));
        enchantments.put(Enchantment.FROST_WALKER, new E(2).range(1, 10, 25).range(2, 20, 35));
        enchantments.put(Enchantment.IMPALING, new E(2).range(1, 1, 21).range(2, 9, 29).range(3, 17, 37).range(4, 25, 45).range(5, 33, 53));
        enchantments.put(Enchantment.KNOCKBACK, new E(5).range(1, 5, 55).range(2, 25, 75));
        enchantments.put(Enchantment.LOOT_BONUS_BLOCKS, new E(2).range(1, 15, 65).range(2, 24, 33).range(3, 33, 83));
        enchantments.put(Enchantment.LOOT_BONUS_MOBS, new E(2).range(1, 15, 24).range(2, 24, 74).range(3, 33, 83));
        enchantments.put(Enchantment.LOYALTY, new E(5).range(1, 17, 50).range(2, 24, 50).range(3, 31, 50));
        enchantments.put(Enchantment.LUCK, new E(2).range(1, 15, 65).range(2, 24, 74).range(3, 33, 83));
        enchantments.put(Enchantment.LURE, new E(2).range(1, 15, 65).range(2, 24, 74).range(3, 33, 83));
        enchantments.put(Enchantment.MENDING, new E(2).range(1, 25, 75));
        enchantments.put(Enchantment.MULTISHOT, new E(2).range(1, 20, 50));
        enchantments.put(Enchantment.OXYGEN, new E(2).range(1, 10, 40).range(2, 20, 50).range(3, 30, 60));
        enchantments.put(Enchantment.PIERCING, new E(10).range(1, 1, 50).range(2, 11, 50).range(3, 21, 50).range(4, 31, 50));
        enchantments.put(Enchantment.PROTECTION_ENVIRONMENTAL, new E(10).range(1, 1, 12).range(2, 12, 23).range(3, 23, 34).range(4, 34, 45));
        enchantments.put(Enchantment.PROTECTION_EXPLOSIONS, new E(2).range(1, 5, 13).range(2, 13, 21).range(3, 21, 29).range(4, 29, 37));
        enchantments.put(Enchantment.PROTECTION_FALL, new E(5).range(1, 5, 11).range(2, 11, 17).range(3, 17, 23).range(4, 34, 42));
        enchantments.put(Enchantment.PROTECTION_FIRE, new E(5).range(1, 10, 18).range(2, 18, 26).range(3, 26, 34).range(4, 34, 42));
        enchantments.put(Enchantment.PROTECTION_PROJECTILE, new E(5).range(1, 3, 9).range(2, 9, 15).range(3, 15, 21).range(4, 21, 27));
        enchantments.put(Enchantment.QUICK_CHARGE, new E(5).range(1, 1, 50).range(2, 11, 50).range(3, 21, 50).range(4, 31, 50));
        enchantments.put(Enchantment.RIPTIDE, new E(2).range(1, 17, 50).range(2, 24, 50).range(3, 31, 50));
        enchantments.put(Enchantment.SILK_TOUCH, new E(1).range(1, 15, 65));
        enchantments.put(Enchantment.SOUL_SPEED, new E(1).range(1, 10, 25).range(2, 20, 35).range(3, 30, 45));
        enchantments.put(Enchantment.SWEEPING_EDGE, new E(2).range(1, 5, 20).range(2, 14, 29).range(3, 23, 38));
        enchantments.put(Enchantment.THORNS, new E(1).range(1, 10, 60).range(2, 30, 70).range(3, 50, 80));
        enchantments.put(Enchantment.VANISHING_CURSE, new E(1).range(1, 25, 50));
        enchantments.put(Enchantment.WATER_WORKER, new E(2).range(1, 1, 41));
    }

    public static double combinedCustomEnchantAmplifier(LivingEntity entity, String enchantmentKey){
        double amplifier = 0D;
        EntityUtils.EntityEquipment equipment = EntityUtils.getEntityEquipment(entity);
        for (ItemStack i : equipment.getIterable(false)){
            EnchantmentWrapper enchantment = CustomEnchantmentManager.getInstance().getCustomEnchant(i, enchantmentKey);
            if (enchantment != null){
                amplifier += enchantment.getAmplifier();
            }
        }
        for (ItemStack i : equipment.getHands()){
            if (EquipmentClass.getClass(i) == EquipmentClass.TRINKET || EquipmentClass.isArmor(i)) continue;
            EnchantmentWrapper enchantment = CustomEnchantmentManager.getInstance().getCustomEnchant(i, enchantmentKey);
            if (enchantment != null){
                amplifier += enchantment.getAmplifier();
            }
        }
        return amplifier;
    }

    private static class E {
        private final int weight;
        private final TreeMap<Integer, Range> ranges = new TreeMap<>();

        public E(int weight){
            this.weight = weight;
        }

        public E range(int level, int min, int max){
            this.ranges.put(level, new Range(min, max));
            return this;
        }

        public int getWeight() {
            return weight;
        }

        public int getAmplifierForLevel(int score){
            int maxLevel = 0;
            for (int i : ranges.keySet()){
                if (ranges.get(i).isInRange(score)){
                    maxLevel = i;
                }
            }
//            if (maxLevel == 0){
//                if (score > ranges.lastEntry().getValue().getMax()){
//                    maxLevel = ranges.lastEntry().getKey();
//                }
//            }
            return maxLevel;
        }
    }

    public static void multiplyItems(List<Item> eventItems, List<Item> newItems, double multiplier, boolean forgiving, Collection<Material> filter){
        Location dropLocation = null;
        for (Item i : eventItems){
            if (dropLocation == null) dropLocation = i.getLocation();
            if (dropLocation.getWorld() == null) return;
            ItemStack item = i.getItemStack();
            if (filter.isEmpty() || filter.contains(item.getType())) {
                int newAmount = Utils.excessChance(item.getAmount() * multiplier);
                if (forgiving) newAmount = Math.max(1, newAmount);
                if (newAmount > item.getMaxStackSize()){
                    int limit = 4;
                    while(newAmount > item.getMaxStackSize()){
                        if (limit <= 0) break;
                        ItemStack drop = item.clone();
                        drop.setAmount(item.getMaxStackSize());
                        newItems.add(
                                dropLocation.getWorld().dropItem(dropLocation, drop)
                        );
                        newAmount -= item.getMaxStackSize();
                        limit--;
                    }
                }
                if (newAmount > 0){
                    item.setAmount(newAmount);
                    i.setItemStack(item);
                    newItems.add(i);
                }
            } else {
                newItems.add(i);
            }
        }
    }

    public static void multiplyItems(List<Item> eventItems, List<Item> newItems, double multiplier, boolean forgiving){
        multiplyItems(eventItems, newItems, multiplier, forgiving, new HashSet<>());
    }

    public static void multiplyItemStacks(List<ItemStack> eventItems, List<ItemStack> newItems, double multiplier, boolean forgiving){
        multiplyItemStacks(eventItems, newItems, multiplier, forgiving, new HashSet<>());
    }

    public static void multiplyItemStacks(List<ItemStack> eventItems, List<ItemStack> newItems, double multiplier, boolean forgiving, Collection<Material> filter){
        for (ItemStack i : eventItems){
            if (filter.isEmpty() || filter.contains(i.getType())){
                int newAmount = Utils.excessChance(i.getAmount() * multiplier);
                if (forgiving) newAmount = Math.max(1, newAmount);
                if (newAmount > i.getMaxStackSize()){
                    int limit = 4;
                    while(newAmount > i.getMaxStackSize()){
                        if (limit <= 0) break;
                        ItemStack drop = i.clone();
                        drop.setAmount(i.getMaxStackSize());
                        newItems.add(drop);
                        newAmount -= i.getMaxStackSize();
                        limit--;
                    }
                }
                if (newAmount > 0){
                    i.setAmount(newAmount);
                    newItems.add(i);
                }
            } else {
                newItems.add(i);
            }
        }
    }

    public static String serializeItemStack(ItemStack itemStack) {
        YamlConfiguration config = new YamlConfiguration();
        config.set("i", itemStack);
        return config.saveToString();
    }

    public static ItemStack deserializeItemStack(String json){
        YamlConfiguration config = new YamlConfiguration();
        try {
            config.loadFromString(json);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return config.getItemStack("i", null);
    }

    public static ItemStack getArrowFromEntity(AbstractArrow arrow){
        if (arrow.hasMetadata("arrow_data")){
            List<MetadataValue> metaData = arrow.getMetadata("arrow_data");
            if (!metaData.isEmpty()){
                ItemStack item = null;
                try {
                    item = ItemUtils.deserializeItemStack(metaData.get(0).asString());
                } catch (Exception ignored){
                    ValhallaMMO.getPlugin().getServer().getLogger().severe("Another plugin is using metadata key 'arrow_data' and not using the proper data type");
                }
                return item;
            }
        }
        return null;
    }

    private static class Range {
        private final int min;
        private final int max;

        public Range(int min, int max){
            this.min = min;
            this.max = max;
        }

        public int getMin() {
            return min;
        }

        public int getMax() {
            return max;
        }

        public boolean isInRange(int i){
            return i <= max && i >= min;
        }
    }
}
