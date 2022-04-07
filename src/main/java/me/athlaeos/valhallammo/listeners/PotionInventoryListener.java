package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.crafting.CustomBrewingProcess;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicBrewingRecipe;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class PotionInventoryListener implements Listener {

    // i hate this

    @EventHandler(priority = EventPriority.HIGHEST)
    public void potionItemPlacer(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) {
            return;
        }
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.getView().getTopInventory().getType() == InventoryType.BREWING
                && e.getView().getBottomInventory().getType() == InventoryType.PLAYER){
            if (!Arrays.asList(
                    ClickType.LEFT,
                    ClickType.RIGHT,
                    ClickType.SHIFT_LEFT,
                    ClickType.SHIFT_RIGHT,
                    ClickType.MIDDLE,
                    ClickType.DOUBLE_CLICK
            ).contains(e.getClick())) {
                e.setCancelled(true);
                return;
            }
        }
        if (e.getClickedInventory().getType() != InventoryType.BREWING) {
            if (e.getView().getTopInventory().getType() == InventoryType.BREWING
            && e.getView().getBottomInventory().getType() == InventoryType.PLAYER){
                if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT){
                    // player shift clicking in own inventory while brewing inventory is pulled up,
                    // switching item on clicked slot to first available brewing slot
                    ItemStack clickedItem = (Utils.isItemEmptyOrNull(e.getCurrentItem())) ? null : e.getCurrentItem().clone();
                    if (clickedItem == null) return;
                    BrewerInventory brewerInventory = (BrewerInventory) e.getView().getTopInventory();
                    int firstEmpty = brewerInventory.firstEmpty();
                    if (firstEmpty < 0 || firstEmpty >= 4){
                        // If the inventory has no empty space, or the inventory's only available spot is the fuel spot,
                        // then nothing will happen.
                        if (firstEmpty == 4){
                            if (clickedItem.getType() != Material.BLAZE_POWDER){
                                return;
                            }
                        } else {
                            return;
                        }
                    }
                    boolean transferAll = firstEmpty >= 3 || clickedItem.getAmount() == 1;

                    if (transferAll){
                        e.setCurrentItem(null);
                        brewerInventory.setItem(firstEmpty, clickedItem);
                        clickedItem = null;
                    } else {
                        clickedItem.setAmount(clickedItem.getAmount() - 1);
                        ItemStack newItem = clickedItem.clone();
                        newItem.setAmount(1);
                        brewerInventory.setItem(firstEmpty, newItem);
                    }

                    CustomBrewingProcess.getPlayerUsageTracker().put(brewerInventory.getHolder(), (Player) e.getWhoClicked());
                    e.getClickedInventory().setItem(e.getSlot(), clickedItem);
                    e.setCancelled(true);
                    updateBrewingStand(e);
                    return;
                }
            }
            return;
        } else {
            if (e.getView().getTopInventory().getType() == InventoryType.BREWING
                    && e.getView().getBottomInventory().getType() == InventoryType.PLAYER){
                if (e.getClick() == ClickType.SHIFT_LEFT || e.getClick() == ClickType.SHIFT_RIGHT){
                    // player shift clicking in brewing inventory while brewing inventory is pulled up,
                    // switching item on clicked slot to first available brewing slot
                    ItemStack item = (Utils.isItemEmptyOrNull(e.getCurrentItem())) ? null : e.getCurrentItem().clone();
                    if (item == null) return;
                    PlayerInventory playerInventory = (PlayerInventory) e.getView().getBottomInventory();
                    int firstEmpty = playerInventory.firstEmpty();
                    if (firstEmpty < 0) return;
                    e.setCurrentItem(null);
                    playerInventory.setItem(firstEmpty, item);
                    e.setCancelled(true);
                    updateBrewingStand(e);
                    return;
                }
            }
        }

        ItemStack clickedItem = (Utils.isItemEmptyOrNull(e.getCurrentItem()) ? null : e.getCurrentItem().clone());
        ItemStack cursor = (Utils.isItemEmptyOrNull(e.getCursor()) ? null : e.getCursor().clone());
        if (Utils.isItemEmptyOrNull(clickedItem) && Utils.isItemEmptyOrNull(cursor)) return;
        e.setCancelled(true);
        CustomBrewingProcess.getPlayerUsageTracker().put((BrewingStand) e.getInventory().getHolder(), (Player) e.getWhoClicked());

        switch (e.getClick()){
            case LEFT:{
                if (Utils.isItemEmptyOrNull(clickedItem) || Utils.isItemEmptyOrNull(cursor)){
                    // one of cursor or clicked item is null, so they should be swapped
                    if (e.getSlot() >= 3){
                        ItemStack temp = (clickedItem == null) ? null : clickedItem.clone();
                        clickedItem = (cursor == null) ? null : cursor.clone();
                        cursor = temp;
                    } else {
                        // transferring to brewing slots, transfer 1 instead of swapping
                        if (Utils.isItemEmptyOrNull(clickedItem)){
                            if (!Utils.isItemEmptyOrNull(cursor)){
                                ItemStack newItem = cursor.clone();
                                newItem.setAmount(1);
                                if (cursor.getAmount() == 1) {
                                    cursor = null;
                                } else {
                                    cursor.setAmount(cursor.getAmount() - 1);
                                }
                                clickedItem = newItem;
                            }
                        } else {
                            // clicked item not null, swap items if cursor null or transfer if similar. do nothing if not similar
                            if (Utils.isItemEmptyOrNull(cursor)){
                                // swap instead
                                ItemStack temp = clickedItem.clone();
                                clickedItem = null;
                                cursor = temp;
                            } else {
                                if (cursor.isSimilar(clickedItem)) {
                                    if (cursor.getAmount() + clickedItem.getAmount() > cursor.getMaxStackSize()) break;
                                    cursor.setAmount(clickedItem.getAmount() + cursor.getAmount());
                                    clickedItem = null;
                                } else {
                                    break;
                                }
                            }
                        }
                    }
                } else if (clickedItem.isSimilar(cursor)){
                    // neither of the items are null, and they are similar, so should be combined as much as possible
                    if (e.getSlot() < 3){
                        if (cursor.getAmount() + clickedItem.getAmount() > cursor.getMaxStackSize()) break;
                        cursor.setAmount(clickedItem.getAmount() + cursor.getAmount());
                        clickedItem = null;
                        break;
                    }
                    int remainingCapacity = Math.max(0, clickedItem.getMaxStackSize() - clickedItem.getAmount());
                    if (remainingCapacity == 0) break;
                    int amountToTransfer = Math.min(cursor.getAmount(), remainingCapacity);
                    cursor.setAmount(cursor.getAmount() - amountToTransfer);
                    clickedItem.setAmount(clickedItem.getAmount() + amountToTransfer);
                } else {
                    // items are not similar and neither are null, so they should be swapped
                    if (e.getSlot() < 3){
                        if (clickedItem.getAmount() == 1 && cursor.getAmount() == 1){
                            ItemStack temp = clickedItem.clone();
                            clickedItem = cursor.clone();
                            cursor = temp;
                        } else if (clickedItem.getAmount() >= 1) break;
                    }
                    ItemStack temp = clickedItem.clone();
                    clickedItem = cursor.clone();
                    cursor = temp;
                }
                break;
            }
            case RIGHT:{
                if (clickedItem == null){
                    // Clicked item is null, 1 should be transferred from cursor to slot
                    ItemStack newItem = cursor.clone();
                    if (cursor.getAmount() == 1) {
                        cursor = null;
                    } else {
                        cursor.setAmount(cursor.getAmount() - 1);
                    }
                    newItem.setAmount(1);
                    clickedItem = newItem;
                } else if (cursor == null) {
                    // cursor is null, but main stack isn't. So should split the clicked stack in half
                    int amountToTransfer = (int) Math.ceil(clickedItem.getAmount() / 2D);
                    cursor = clickedItem.clone();
                    if (clickedItem.getAmount() == 1) {
                        clickedItem = null;
                    } else {
                        clickedItem.setAmount(clickedItem.getAmount() - amountToTransfer);
                        cursor.setAmount(amountToTransfer);
                    }
                } else if (cursor.isSimilar(clickedItem)){
                    // Neither items are null, both items are similar, so 1 should transfer from cursor to slot
                    // until slot is max capacity
                    if (e.getSlot() < 3){
                        if (clickedItem.getAmount() >= 1) break;
                    }
                    if (clickedItem.getAmount() >= clickedItem.getMaxStackSize()) break;
                    clickedItem.setAmount(clickedItem.getAmount() + 1);
                    if (cursor.getAmount() == 1) {
                        cursor = null;
                    } else {
                        cursor.setAmount(cursor.getAmount() - 1);
                    }
                } else {
                    // Neither items are null or similar, items should be swapped.
                    if (e.getSlot() < 3){
                        if (cursor.getAmount() == 1){
                            //swap
                            ItemStack temp = clickedItem.clone();
                            clickedItem = cursor.clone();
                            cursor = temp;
                        } else {
                            if (clickedItem.getAmount() >= 1) break;
                        }
                        break;
                    }
                    ItemStack temp = clickedItem.clone();
                    clickedItem = cursor.clone();
                    cursor = temp;
                }
                break;
            }
            default: {
                e.setCancelled(false);
                return;
            }
        }
        e.getClickedInventory().setItem(e.getSlot(), clickedItem);
        e.getWhoClicked().setItemOnCursor(cursor);
        ((Player)e.getWhoClicked()).updateInventory();

        updateBrewingStand(e);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void potionItemPlacer(InventoryDragEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        if (e.getView().getTopInventory().getType() != InventoryType.BREWING) {
            return;
        }
        for (Integer i : e.getRawSlots()){
            if (i > 4) return;
        }
        if (e.getRawSlots().size() != 1) {
            return;
        }

        int slot = new ArrayList<>(e.getRawSlots()).get(0);
        ItemStack clickedItem = e.getInventory().getItem(slot);
        ItemStack cursor = (Utils.isItemEmptyOrNull(e.getOldCursor()) ? null : e.getOldCursor().clone());

        if (clickedItem == null && cursor == null) return;
        e.setCancelled(true);
        e.setResult(Event.Result.ALLOW);
        CustomBrewingProcess.getPlayerUsageTracker().put((BrewingStand) e.getInventory().getHolder(), (Player) e.getWhoClicked());

        switch (e.getType()){
            case EVEN:{
                if (Utils.isItemEmptyOrNull(clickedItem) || Utils.isItemEmptyOrNull(cursor)){
                    // clicked item is null, so they should be swapped
                    ItemStack temp = (clickedItem == null) ? null : clickedItem.clone();
                    clickedItem = (cursor == null) ? null : cursor.clone();
                    cursor = temp;
//
//                    e.getInventory().setItem(e.getSlot(), cursor);
//                    e.setCursor(clickedItem);
                } else if (clickedItem.isSimilar(cursor)){
                    // neither of the items are null, and they are similar, so should be combined as much as possible
                    int remainingCapacity = Math.max(0, clickedItem.getMaxStackSize() - clickedItem.getAmount());
                    if (remainingCapacity == 0) break;
                    int amountToTransfer = Math.min(cursor.getAmount(), remainingCapacity);
                    cursor.setAmount(cursor.getAmount() - amountToTransfer);
                    clickedItem.setAmount(clickedItem.getAmount() + amountToTransfer);
                } else {
                    // items are not similar and neither are null, so they should be swapped
                    ItemStack temp = clickedItem.clone();
                    clickedItem = cursor.clone();
                    cursor = temp;
                }
                break;
            }
            case SINGLE:{
                if (clickedItem == null){
                    // Clicked item is null, 1 should be transferred from cursor to slot
                    // Both items can't be null, so cursor can't be null if this passes
                    ItemStack newItem = cursor.clone();
                    if (cursor.getAmount() == 1) {
                        cursor = null;
                    } else {
                        cursor.setAmount(cursor.getAmount() - 1);
                    }
                    newItem.setAmount(1);
                    clickedItem = newItem;
                } else if (cursor == null) {
                    // cursor is null, but main stack isn't. So should split the clicked stack in half
                    int amountToTransfer = (int) Math.ceil(clickedItem.getAmount() / 2D);
                    cursor = clickedItem.clone();
                    if (clickedItem.getAmount() == 1) {
                        clickedItem = null;
                    } else {
                        clickedItem.setAmount(clickedItem.getAmount() - amountToTransfer);
                        cursor.setAmount(amountToTransfer);
                    }
                } else if (cursor.isSimilar(clickedItem)){
                    // Neither items are null, both items are similar, so 1 should transfer from cursor to slot
                    // until slot is max capacity
                    if (clickedItem.getAmount() >= clickedItem.getMaxStackSize()) {
                        break;
                    }
                    clickedItem.setAmount(clickedItem.getAmount() + 1);
                    if (cursor.getAmount() == 1) {
                        cursor = null;
                    } else {
                        cursor.setAmount(cursor.getAmount() - 1);
                    }
                } else {
                    // Neither items are null or similar, items should be swapped.
                    ItemStack temp = clickedItem.clone();
                    clickedItem = cursor.clone();
                    cursor = temp;
                }
                break;
            }
            default: {
                e.setCancelled(false);
                return;
            }
        }

        e.getInventory().setItem(slot, clickedItem);
        ((Player)e.getWhoClicked()).updateInventory();
        e.setCursor(cursor);

        updateBrewingStand(e);
    }

    private void updateBrewingStand(InventoryClickEvent e){
        if (!(e.getWhoClicked() instanceof Player)) return;
        BrewerInventory topInventory = (BrewerInventory) e.getView().getTopInventory();
        final Map<Integer, DynamicBrewingRecipe> recipes = CustomRecipeManager.getInstance().getBrewingRecipes(topInventory, (Player) e.getWhoClicked());
        CustomBrewingProcess clock = CustomBrewingProcess.getActiveStands().get(topInventory);
        if (!recipes.isEmpty()){
            if (clock != null){
                clock.resetStand(topInventory, recipes);
            } else {
                double baseTime = 399;
                double multiplier = AccumulativeStatManager.getInstance().getStats("ALCHEMY_BREW_SPEED", e.getWhoClicked(), true);
                if (multiplier <= 0){
                    baseTime = 4000;
                } else {
                    baseTime = baseTime / multiplier;
                }

                new CustomBrewingProcess(recipes, topInventory, baseTime);
            }
        } else {
            if (clock != null){
                CustomBrewingProcess.getActiveStands().remove(topInventory);
                clock.cancel();
            }
        }
    }

    private void updateBrewingStand(InventoryDragEvent e){
        if (!(e.getWhoClicked() instanceof Player)) return;
        BrewerInventory topInventory = (BrewerInventory) e.getView().getTopInventory();
        final Map<Integer, DynamicBrewingRecipe> recipes = CustomRecipeManager.getInstance().getBrewingRecipes(topInventory, (Player) e.getWhoClicked());
        CustomBrewingProcess clock = CustomBrewingProcess.getActiveStands().get(topInventory);
        if (!recipes.isEmpty()){
            if (clock != null){
                clock.resetStand(topInventory, recipes);
            } else {
                double baseTime = 399;
                double multiplier = AccumulativeStatManager.getInstance().getStats("ALCHEMY_BREW_SPEED", e.getWhoClicked(), true);
                if (multiplier <= 0){
                    baseTime = 4000;
                } else {
                    baseTime = baseTime / multiplier;
                }

                new CustomBrewingProcess(recipes, topInventory, baseTime);
            }
        } else {
            if (clock != null){
                CustomBrewingProcess.getActiveStands().remove(topInventory);
                clock.cancel();
            }
        }
    }
}
