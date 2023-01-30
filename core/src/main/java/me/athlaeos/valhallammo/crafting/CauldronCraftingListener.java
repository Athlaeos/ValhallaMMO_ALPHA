package me.athlaeos.valhallammo.crafting;

import com.jeff_media.customblockdata.CustomBlockData;
import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicCauldronRecipe;
import me.athlaeos.valhallammo.events.CauldronAbsorbItemEvent;
import me.athlaeos.valhallammo.events.CauldronCompleteRecipeEvent;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.Levelled;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CauldronCraftingListener implements Listener {
    private static final NamespacedKey cauldronStorageKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_custom_alchemy_cauldron");
    private static int cauldron_max_capacity = 0;
    private static int cauldron_item_checker_duration = 20;
    private final Map<UUID, CauldronItemChecker> runnableLimiter = new HashMap<>();

    public CauldronCraftingListener(){
        cauldron_max_capacity = ConfigManager.getInstance().getConfig("config.yml").get().getInt("cauldron_max_capacity");
        cauldron_item_checker_duration = ConfigManager.getInstance().getConfig("config.yml").get().getInt("cauldron_item_duration");
    }

    @EventHandler
    public void onItemThrow(PlayerDropItemEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getPlayer().getWorld().getName())) return;
        Player thrower = e.getPlayer();
        if (!runnableLimiter.containsKey(thrower.getUniqueId())){
            CauldronItemChecker runnable = new CauldronItemChecker(thrower, e.getItemDrop());
            runnableLimiter.put(thrower.getUniqueId(), runnable);
            runnable.runTaskTimer(ValhallaMMO.getPlugin(), 0L, 1L);
        } else {
            runnableLimiter.get(thrower.getUniqueId()).resetTicks();
        }
    }

    @EventHandler
    public void onCauldronClick(PlayerInteractEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getPlayer().getWorld().getName())) return;

        if (e.getClickedBlock() != null && Utils.isItemEmptyOrNull(e.getPlayer().getInventory().getItemInMainHand()) && e.getHand() == EquipmentSlot.HAND){
            if (e.getClickedBlock().getType().toString().contains("CAULDRON")){
                dumpCauldronContents(e.getClickedBlock());
            }
        }
    }

    public static void dumpCauldronContents(Block cauldron){
        List<ItemStack> items = getCauldronContents(cauldron);
        if (items == null) return;
        for (ItemStack i : items){
            cauldron.getWorld().dropItem(cauldron.getLocation().add(0.5, 1, 0.5), i);
        }
        cauldron.getWorld().playSound(cauldron.getLocation().add(0.5, 1, 0.5), Sound.ITEM_BUCKET_EMPTY, 0.3F, 1F);
        setCauldronInventory(cauldron, null);
    }

    private class CauldronItemChecker extends BukkitRunnable{
        private int ticksToCheck = cauldron_item_checker_duration;
        private final Player thrower;
        private final Item item;

        public CauldronItemChecker(Player thrower, Item item){
            this.thrower = thrower;
            this.item = item;
        }

        @Override
        public void run() {
            if (ticksToCheck > 0 && item.isValid()){
                Block b = item.getLocation().getBlock();
                if (!b.getType().toString().contains("CAULDRON")) b = item.getLocation().add(0, -0.5, 0).getBlock();
                if (b.getType().toString().contains("CAULDRON")){
                    if (b.getBlockData() instanceof Levelled){
                        Levelled cauldronBlockData = (Levelled) b.getBlockData();
                        if (cauldronBlockData.getLevel() <= 0){
                            runnableLimiter.remove(thrower.getUniqueId());
                            cancel();
                            return;
                        }
                    } else {
                        runnableLimiter.remove(thrower.getUniqueId());
                        cancel();
                        return;
                    }
                    // if the item is inside a cauldron, or it's 0.5 blocks ahove a cauldron try to activate a recipe or absorb the item into the cauldron
                    ItemStack tryResult = tryGetCauldronRecipeResult(thrower, b, item);
                    if (tryResult == null){
                        CauldronAbsorbItemEvent event = new CauldronAbsorbItemEvent(b, item, thrower);
                        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
                        if (!event.isCancelled()){
                            if (addItem(b, event.getAbsorbedItem().getItemStack().clone())){
                                event.getAbsorbedItem().remove();
                            }
                        }
                    } else {
                        b.getWorld().dropItem(b.getLocation().add(0.5, 1, 0.5), tryResult);
                        b.getWorld().playEffect(b.getLocation().add(0.5, 0.2, 0.5), Effect.EXTINGUISH, 0);
                        b.getWorld().spawnParticle(Particle.FIREWORKS_SPARK, b.getLocation().add(0.5, 0.8, 0.5), 15);
                    }
                    runnableLimiter.remove(thrower.getUniqueId());
                    cancel();
                }
            } else {
                runnableLimiter.remove(thrower.getUniqueId());
                cancel();
            }
            ticksToCheck--;
        }

        public void resetTicks(){
            ticksToCheck = cauldron_item_checker_duration;
        }
    }

    private final Collection<Material> legalFireBlocks = ItemUtils.getMaterialList(Arrays.asList("CAMPFIRE", "FIRE", "SOUL_CAMPFIRE", "SOUL_FIRE",
            "LAVA"));
    /**
     * Attempts to trigger a cauldron recipe
     * @param cauldron the cauldron in which the recipe is being attempted
     * @param catalyst the ItemStack currently being thrown into the cauldron, to be treated as catalyst
     * @return the resulting ItemStack if the recipe succeeds, or null otherwise
     */
    public ItemStack tryGetCauldronRecipeResult(Player thrower, Block cauldron, Item catalyst){
        List<ItemStack> cauldronContents = getCauldronContents(cauldron);
        for (DynamicCauldronRecipe recipe : CustomRecipeManager.getInstance().getCauldronRecipes().values()){
            if (!recipe.getIngredients().isEmpty() && cauldronContents == null) continue; // recipe has ingredients, but cauldron has none. therefore this recipe is not an option
            if (cauldronContents != null && recipe.getIngredients().size() > cauldronContents.size()) continue; // recipe contains more ingredients than cauldron has, so therefore cannot be an option
            boolean isAboveFire = legalFireBlocks.contains(cauldron.getLocation().add(0, -1, 0).getBlock().getType());
            if (recipe.isRequiresBoilingWater() && !isAboveFire) continue;
            if (EquipmentClass.getClass(catalyst.getItemStack()) != null && (recipe.isRequireCustomCatalyst() && !SmithingItemTreatmentManager.getInstance().isItemCustom(catalyst.getItemStack()))) continue;
            boolean consumeWater = false;
            if (cauldron.getBlockData() instanceof Levelled){
                Levelled cData = (Levelled) cauldron.getBlockData();
                if (cData.getLevel() > 0){
                    if (recipe.isConsumesWaterLevel()) consumeWater = true;
                } else return null;
            } else return null;

            if ((!recipe.isCatalystExactMeta() && recipe.getCatalyst().getType() == catalyst.getItemStack().getType()) || recipe.getCatalyst().isSimilar(catalyst.getItemStack())){
                // recipe has matching catalyst
                ItemStack result = recipe.isTinkerCatalyst() ? catalyst.getItemStack().clone() : recipe.getResult().clone();
                int timesCrafted;

                ItemStack[] arrayContents = null;
                if (cauldronContents != null) arrayContents = cauldronContents.toArray(new ItemStack[0]);
                if (arrayContents == null){
                    result.setAmount(catalyst.getItemStack().getAmount());
                    timesCrafted = result.getAmount();
                } else {
                    timesCrafted = ItemUtils.timesCraftable(recipe.getIngredients(), arrayContents, result.getAmount(), recipe.isIngredientsExactMeta());
                    result.setAmount(result.getAmount() * timesCrafted);
                }
                if (arrayContents == null || timesCrafted > 0){
                    List<ItemStack> newContents = new ArrayList<>();
                    if (arrayContents != null){
                        Inventory inventory = ValhallaMMO.getPlugin().getServer().createInventory(null, 54);
                        inventory.addItem(arrayContents);
                        ItemUtils.removeItems(inventory, recipe.getIngredients(), timesCrafted, recipe.isIngredientsExactMeta());
                        for (ItemStack i : inventory.getContents()){
                            if (Utils.isItemEmptyOrNull(i)) continue;
                            newContents.add(i);
                        }
                    }
                    result = DynamicItemModifier.modify(result, thrower, recipe.getItemModifiers(), false, true, true, timesCrafted);
                    if (result == null) continue;
                    setCauldronInventory(cauldron, newContents);
                    ItemStack originalCatalyst = catalyst.getItemStack().clone();
                    if (originalCatalyst.getAmount() <= timesCrafted) {
                        catalyst.remove();
                    } else {
                        originalCatalyst.setAmount(originalCatalyst.getAmount() - timesCrafted);
                        catalyst.setItemStack(originalCatalyst);
                    }

                    if (consumeWater){
                        Levelled cData = (Levelled) cauldron.getBlockData();
                        if (cData.getLevel() == 1){
                            cauldron.setType(Material.CAULDRON);
                        } else {
                            cData.setLevel(cData.getLevel() - 1);
                            cauldron.setBlockData(cData);
                        }
                    }

                    CauldronCompleteRecipeEvent event = new CauldronCompleteRecipeEvent(cauldron, recipe, thrower);
                    ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
                    if (!event.isCancelled()){
                        return result;
                    }
                }
            }
        }
        return null;
    }

    /**
     * Fetches all contents a cauldron has.
     * @param cauldron The block to fetch its contents from, this should be a cauldron
     * @return the collection of items stored in the cauldron, or null if the block is not a cauldron or does not possess items
     */
    public static List<ItemStack> getCauldronContents(Block cauldron){
        if (!cauldron.getType().toString().contains("CAULDRON")) return null;
        if (!CustomBlockData.hasCustomBlockData(cauldron, ValhallaMMO.getPlugin())) return null;

        PersistentDataContainer customBlockData = new CustomBlockData(cauldron, ValhallaMMO.getPlugin());
        if (!customBlockData.has(cauldronStorageKey, PersistentDataType.STRING)) return null;

        String rawContents = customBlockData.get(cauldronStorageKey, PersistentDataType.STRING);
        if (rawContents == null) return null;

        String[] items = rawContents.split("<itemsplitter>");
        List<ItemStack> inventory = new ArrayList<>();

        for (String itemSlot : items){
            ItemStack item = ItemUtils.deserializeItemStack(itemSlot);
            if (Utils.isItemEmptyOrNull(item)) continue;
            inventory.add(item);
        }

        return inventory;
    }

    /**
     * Sets the inventory of the cauldron, or wipes the cauldron of custom data if the contents are empty or null
     * @param cauldron the cauldron to store the items in
     * @param contents the contents to serialize to the cauldron
     */
    public static void setCauldronInventory(Block cauldron, List<ItemStack> contents){
        if (!cauldron.getType().toString().contains("CAULDRON")) return;

        PersistentDataContainer customBlockData = new CustomBlockData(cauldron, ValhallaMMO.getPlugin());
        if (contents == null || contents.isEmpty()){
            customBlockData.remove(cauldronStorageKey);
        } else {
            List<String> serializedItems = new ArrayList<>();
            contents.forEach(i -> serializedItems.add(ItemUtils.serializeItemStack(i)));
            customBlockData.set(cauldronStorageKey, PersistentDataType.STRING, String.join("<itemsplitter>", serializedItems));
        }
    }

    /**
     * Adds an item to a cauldron inventory
     * @param cauldron the cauldron to add the item to
     * @param i the item to add to the cauldron
     * @return false if the block is not a cauldron or if the item could not be added, true if the item was successfully added
     */
    public boolean addItem(Block cauldron, ItemStack i){
        if (!cauldron.getType().toString().contains("CAULDRON")) return false;
        List<ItemStack> contents = getCauldronContents(cauldron);
        if (contents == null) contents = new ArrayList<>();
        contents.add(i);

        Map<ItemStack, Integer> compactedContents = new HashMap<>();
        // this first compacts and then separates ItemStacks. For example, 60 ender pearls would be separated into 3 stacks of 16 plus 12
        for (ItemStack item : contents){
            if (item == null) continue;
            int itemAmount = item.getAmount();
            item.setAmount(1);
            if (compactedContents.containsKey(item)){
                compactedContents.put(item, compactedContents.get(item) + itemAmount);
            } else {
                compactedContents.put(item, itemAmount);
            }
        }

        List<ItemStack> newContents = new ArrayList<>();
        for (ItemStack item : compactedContents.keySet()){
            int amount = compactedContents.get(item);
            if (amount > item.getType().getMaxStackSize()){
                while (amount > 0) {
                    ItemStack clone = item.clone();
                    clone.setAmount(Math.min(item.getType().getMaxStackSize(), amount));
                    newContents.add(clone);
                    amount -= item.getType().getMaxStackSize();
                }
            } else {
                ItemStack clone = item.clone();
                clone.setAmount(amount);
                newContents.add(clone);
            }
        }

        if (newContents.size() > cauldron_max_capacity) return false;

        cauldron.getWorld().playSound(cauldron.getLocation().add(0.5, 0.5, 0.5), Sound.ITEM_BUCKET_FILL, .3F, 1F);
        for (int p = 0; p < 10; p++)
            cauldron.getWorld().spawnParticle(Particle.CAMPFIRE_COSY_SMOKE, cauldron.getLocation().add(0.5, 0.9, 0.5), 0, 0.3, 0.05, 0.3);
        setCauldronInventory(cauldron, newContents);
        return true;
    }

    /**
     * Checks if a block is a custom cauldron with inventory contents
     * @param cauldron the block to check
     * @return false if the block is not a cauldron or has no inventory contents
     */
    public static boolean isCustomCauldron(Block cauldron){
        if (!cauldron.getType().toString().contains("CAULDRON")) return false;
        if (!CustomBlockData.hasCustomBlockData(cauldron, ValhallaMMO.getPlugin())) return false;
        PersistentDataContainer customBlockData = new CustomBlockData(cauldron, ValhallaMMO.getPlugin());
        return customBlockData.has(cauldronStorageKey, PersistentDataType.STRING);
    }

    public static int getMaxCauldronCapacity() {
        return cauldron_max_capacity;
    }
}
