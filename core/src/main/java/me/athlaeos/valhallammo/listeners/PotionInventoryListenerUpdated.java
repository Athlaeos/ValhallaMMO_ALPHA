package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicBrewingRecipe;
import me.athlaeos.valhallammo.events.PlayerCustomBrewEvent;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class PotionInventoryListenerUpdated implements Listener {
    private final Map<Location, Map.Entry<BrewingTask, Map<Integer, DynamicBrewingRecipe>>> activeBrewingStands = new HashMap<>();
    private static final boolean save_ingredient_spawn_on_top_of_stand = ConfigManager.getInstance().getConfig("skill_alchemy.yml").get().getBoolean("save_ingredient_spawn_on_top_of_stand", true);

    private final Map<Material, Integer[]> preferredMaterialSlots = new HashMap<>();
    public PotionInventoryListenerUpdated(){
        preferredMaterialSlots.put(Material.BLAZE_POWDER, new Integer[]{ 4 });
        preferredMaterialSlots.put(Material.GLASS_BOTTLE, new Integer[]{ 0, 1, 2 });
        preferredMaterialSlots.put(Material.POTION, new Integer[]{ 0, 1, 2 });
        preferredMaterialSlots.put(Material.SPLASH_POTION, new Integer[]{ 0, 1, 2 });
        preferredMaterialSlots.put(Material.LINGERING_POTION, new Integer[]{ 0, 1, 2 });
    }

    @EventHandler
    public void onInv(InventoryClickEvent e) {
        if (e.getClickedInventory() == null) return;
        if (ValhallaMMO.isWorldBlacklisted(e.getWhoClicked().getWorld().getName())) return;
        if (e.getView().getTopInventory() instanceof BrewerInventory){
            if (CustomRecipeManager.getInstance().getBrewingRecipes().isEmpty()) return;
            BrewerInventory inventory = (BrewerInventory) e.getView().getTopInventory();
            Location location = inventory.getLocation();
            Player player = (Player) e.getWhoClicked();
            if (e.getClickedInventory() instanceof BrewerInventory) {
                if (location == null) return;
                if (e.getClickedInventory().getType() != InventoryType.BREWING) return;

                if (e.getSlot() <= 3 && e.getSlot() >= 0) {
                    //Make it possible to place in everything into the ingredient slot
                    if (e.isRightClick()) {
                        //Dropping one item or pick up half
                        if (e.getAction().equals(InventoryAction.PICKUP_HALF) || e.getAction().equals(InventoryAction.PICKUP_SOME)) {
                            ValhallaMMO.getPlugin().getServer().getScheduler().runTask(ValhallaMMO.getPlugin(), () -> {
                                if (Utils.isItemEmptyOrNull(inventory.getItem(3))) {
                                    activeBrewingStands.remove(location);
                                }
                            });
                            return;
                        }
                    } else {
                        if (e.getAction().equals(InventoryAction.PICKUP_ALL) || Utils.isItemEmptyOrNull(e.getCursor()) || e.getAction().equals(InventoryAction.COLLECT_TO_CURSOR)) {
                            //Make sure cursor contains item and the item isn't picked up
                            ValhallaMMO.getPlugin().getServer().getScheduler().runTask(ValhallaMMO.getPlugin(), () -> {
                                if (Utils.isItemEmptyOrNull(inventory.getItem(3))) {
                                    activeBrewingStands.remove(location);
                                }
                            });
                            return;
                        }
                    }
                    ItemUtils.calculateClickedSlot(e);
                }
            } else if (e.getClickedInventory() instanceof PlayerInventory && e.getClick().isShiftClick() && !Utils.isItemEmptyOrNull(e.getCurrentItem())){
                int inputSlot = Utils.isItemEmptyOrNull(inventory.getItem(3)) ? 3 : inventory.firstEmpty();
                if (inputSlot >= 0) {
                    if (!(e.getCurrentItem().getType() != Material.BLAZE_POWDER && inputSlot == 4)){
                        // item material is a special case where it prefers to be in a different item slot (such as blaze powder in the fuel slot)
                        if (preferredMaterialSlots.containsKey(e.getCurrentItem().getType())){
                            for (int slot : preferredMaterialSlots.get(e.getCurrentItem().getType())){
                                if (Utils.isItemEmptyOrNull(inventory.getItem(slot))) {
                                    inputSlot = slot;
                                    break;
                                }
                            }
                        }
                        inventory.setItem(inputSlot, e.getCurrentItem().clone());
                        e.setCurrentItem(null);
                        e.setCancelled(true);
                    }
                }
            }

            updateBrewingStand(inventory, location, player);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void potionItemPlacer(InventoryDragEvent e) {
        if (ValhallaMMO.isWorldBlacklisted(e.getWhoClicked().getWorld().getName())) return;
        if (!e.isCancelled()){
            if (!(e.getWhoClicked() instanceof Player)) return;
            Player player = (Player) e.getWhoClicked();
            if (e.getView().getTopInventory().getType() != InventoryType.BREWING) {
                return;
            }
            for (Integer i : e.getRawSlots()){
                // if one of the dragged slots is a player inventory slot
                if (i > 4) return;
            }
            if (e.getRawSlots().size() != 1) {
                return;
            }

            int slot = new ArrayList<>(e.getRawSlots()).get(0);

            e.setCancelled(true);
            e.setResult(Event.Result.ALLOW);

            switch (e.getType()){
                case EVEN:{
                    ItemUtils.calculateClickedSlot(new InventoryClickEvent(e.getView(), InventoryType.SlotType.CONTAINER, slot, ClickType.LEFT, InventoryAction.DROP_ALL_SLOT));
                    break;
                }
                case SINGLE:{
                    ItemUtils.calculateClickedSlot(new InventoryClickEvent(e.getView(), InventoryType.SlotType.CONTAINER, slot, ClickType.RIGHT, InventoryAction.DROP_ALL_SLOT));
                    break;
                }
                default: {
                    e.setCancelled(false);
                    return;
                }
            }

            BrewerInventory inventory = (BrewerInventory) e.getView().getTopInventory();
            Location location = inventory.getLocation();

            updateBrewingStand(inventory, location, player);
        }
    }

    private void updateBrewingStand(BrewerInventory inventory, Location location, Player player){
        ValhallaMMO.getPlugin().getServer().getScheduler().runTaskLater(ValhallaMMO.getPlugin(), () -> {
            final ItemStack ingredient = inventory.getItem(3);
            if (Utils.isItemEmptyOrNull(ingredient)) {
                return;
            }
            BrewingStand brewingStand = inventory.getHolder();
            if (brewingStand != null) {
                Map<Integer, DynamicBrewingRecipe> brewingRecipeList = CustomRecipeManager.getInstance().getBrewingRecipes(inventory, player);
                if (brewingRecipeList.isEmpty()) {
                    if (activeBrewingStands.containsKey(location)) {
                        //Cancel current running tasks and removing the brewing operation from the location
                        activeBrewingStands.get(location).getKey().cancel();
                        activeBrewingStands.remove(location);
                        brewingStand.setBrewingTime(0);
                    }
                } else if (!activeBrewingStands.containsKey(location)) {
                    brewingStand.setBrewingTime(400);
                    brewingStand.setFuelLevel(brewingStand.getFuelLevel() - 1);

                    if (brewingStand.getFuelLevel() >= 0) {
                        double speed = AccumulativeStatManager.getInstance().getStats("ALCHEMY_BREW_SPEED", player, true);
                        double baseTime = Math.max(1, speed <= 0 ? 4000 : 400 / speed);
                        BrewingTask runnable = new BrewingTask(player, inventory, baseTime);
                        runnable.runTaskTimer(ValhallaMMO.getPlugin(), 2, 1);
                        activeBrewingStands.put(location, new Map.Entry<BrewingTask, Map<Integer, DynamicBrewingRecipe>>() {
                            @Override
                            public BrewingTask getKey() { return runnable; }
                            @Override
                            public Map<Integer, DynamicBrewingRecipe> getValue() { return brewingRecipeList; }
                            @Override
                            public Map<Integer, DynamicBrewingRecipe> setValue(Map<Integer, DynamicBrewingRecipe> value) { return null; }
                        });
                    }
                } else {
                    //Put new brewing recipes to map, but keep current active task
                    Map.Entry<BrewingTask, Map<Integer, DynamicBrewingRecipe>> currentEntry = activeBrewingStands.get(location);
                    currentEntry.getKey().updateTask(inventory);
                    activeBrewingStands.put(location, new Map.Entry<BrewingTask, Map<Integer, DynamicBrewingRecipe>>() {
                        @Override
                        public BrewingTask getKey() { return currentEntry.getKey(); }
                        @Override
                        public Map<Integer, DynamicBrewingRecipe> getValue() { return brewingRecipeList; }
                        @Override
                        public Map<Integer, DynamicBrewingRecipe> setValue(Map<Integer, DynamicBrewingRecipe> value) { return null; }
                    });
                }
            }
        }, 2);
    }

    private class BrewingTask extends BukkitRunnable{
        private BrewerInventory inventory;
        private final Player brewer;
        private int tick = 400;
        private double actualTime = 400D;
        private final double timeStep;

        public BrewingTask(Player brewer, BrewerInventory inventory, double duration){
            this.inventory = inventory;
            this.brewer = brewer;
            this.timeStep = 400D / duration;
        }

        public void updateTask(BrewerInventory inventory){
            this.inventory = inventory;
        }

        @Override
        public void run() {
            Location location = inventory.getLocation();
            BrewingStand brewingStand = inventory.getHolder();
            if (location == null || brewingStand == null){
                cancel();
                return;
            }
            if (!activeBrewingStands.containsKey(location)) {
                cancel();
                return;
            }
            if (location.getBlock().getType() != Material.BREWING_STAND){
                //assuming block is broken
                activeBrewingStands.remove(location);
                cancel();
                return;
            }
            if (tick > 0) {
                actualTime -= timeStep;
                tick = Math.max(0, (int) actualTime);
                if (!location.getBlock().getType().equals(Material.BREWING_STAND)) {
                    activeBrewingStands.remove(location);
                    cancel();
                    return;
                }
                brewingStand.setBrewingTime(tick);
                brewingStand.update();
                return;
            }
            Map<Integer, DynamicBrewingRecipe> rS = activeBrewingStands.get(location).getValue();
            for (Integer recipeSlot : rS.keySet()) {
                DynamicBrewingRecipe recipe = rS.get(recipeSlot);

                boolean[] success = new boolean[] {false, false, false};
                boolean cancelled = false;
                for (int i = 0; i < 3; i++) {
                    if(Utils.isItemEmptyOrNull(inventory.getItem(i)))
                        continue;
                    if (rS.get(i) != null){
                        ItemStack result = inventory.getItem(i);
                        assert result != null;
                        ItemStack backup = result.clone();
                        result = result.clone();

                        List<DynamicItemModifier> modifiers = new ArrayList<>(recipe.getItemModifiers());
                        modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                        for (DynamicItemModifier modifier : modifiers){
                            if (result == null) break;
                            result = modifier.processItem(brewer, result);
                        }

                        PotionEffectManager.renamePotion(result, true);
                        PlayerCustomBrewEvent event = new PlayerCustomBrewEvent(brewer, recipe, brewingStand, result != null);
                        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
                        cancelled = event.isCancelled();
                        if (!cancelled){
                            if (result == null){
                                // don't want to delete the item if the modifier conditions fail, so instead it's not touched at all
                                inventory.setItem(i, backup);
                            } else {
                                success[i] = true;
                                inventory.setItem(i, result);
                            }
                        }
                    }
                }

                if (!cancelled){
                    if (success[0] || success[1] || success[2]) {
                        boolean consume = !(Utils.getRandom().nextDouble() < AccumulativeStatManager.getInstance().getStats("ALCHEMY_INGREDIENT_SAVE", brewer, true));
                        if (consume){
                            if (!Utils.isItemEmptyOrNull(inventory.getIngredient())){
                                if (inventory.getIngredient().getAmount() <= 1){
                                    if (ItemUtils.getFilledBuckets().contains(inventory.getIngredient().getType())){
                                        inventory.setIngredient(new ItemStack(Material.BUCKET));
                                    } else {
                                        inventory.setIngredient(null);
                                    }
                                } else {
                                    inventory.getIngredient().setAmount(inventory.getIngredient().getAmount() - 1);
                                }
                            }
                        } else {
                            if (save_ingredient_spawn_on_top_of_stand){
                                if (!Utils.isItemEmptyOrNull(inventory.getIngredient())){
                                    ItemStack ingredient = inventory.getIngredient().clone();
                                    if (inventory.getIngredient().getAmount() <= 1){
                                        if (ItemUtils.getFilledBuckets().contains(inventory.getIngredient().getType())){
                                            inventory.setIngredient(new ItemStack(Material.BUCKET));
                                        } else {
                                            inventory.setIngredient(null);
                                        }
                                    } else {
                                        inventory.getIngredient().setAmount(inventory.getIngredient().getAmount() - 1);
                                    }

                                    ingredient.setAmount(1);
                                    if (location.getWorld() != null){
                                        location.getWorld().dropItem(location.add(0.5, 0.8, 0.5), ingredient);
                                    }
                                }
                            }
                        }
                    }
                }
            }
            activeBrewingStands.remove(location);
            cancel();
        }
    }
}
