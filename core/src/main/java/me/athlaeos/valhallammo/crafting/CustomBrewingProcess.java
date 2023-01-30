package me.athlaeos.valhallammo.crafting;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicBrewingRecipe;
import me.athlaeos.valhallammo.events.PlayerCustomBrewEvent;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.inventory.BrewerInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

public class CustomBrewingProcess extends BukkitRunnable
{
    private static final Map<BrewerInventory, CustomBrewingProcess> activeStands = new HashMap<>();
    private static final Map<BrewingStand, Player> playerUsageTracker = new HashMap<>();
    private static boolean save_ingredient_spawn_on_top_of_stand = ConfigManager.getInstance().getConfig("skill_alchemy.yml").get().getBoolean("save_ingredient_spawn_on_top_of_stand", true);

    private final BrewerInventory inventory;
    private final Map<Integer, DynamicBrewingRecipe> recipes;

    private final ItemStack ingredient;
    private final BrewingStand stand;
    private double baseTime = 399;
    private int time = 399;
    private double actualTime;
    private final double timeStep;

    public CustomBrewingProcess(Map<Integer, DynamicBrewingRecipe> recipes , BrewerInventory inventory) {
        this.recipes = recipes;
        this.inventory = inventory;
        this.ingredient = inventory.getIngredient();
        this.stand = inventory.getHolder();
        this.timeStep = 1D;
        actualTime = time;
        activeStands.put(inventory, this);
        runTaskTimer(ValhallaMMO.getPlugin(), 1L, 1L);
    }

    public CustomBrewingProcess(Map<Integer, DynamicBrewingRecipe> recipes , BrewerInventory inventory, double baseTime) {
        this.recipes = recipes;
        this.inventory = inventory;
        this.ingredient = inventory.getIngredient();
        this.stand = inventory.getHolder();
        this.baseTime = baseTime;
        if (baseTime < 1) baseTime = 1;
        if (baseTime == 400) baseTime = 399;
        this.timeStep = time / baseTime;
        actualTime = time;
        activeStands.put(inventory, this);
        runTaskTimer(ValhallaMMO.getPlugin(), 1L, 1L);
    }

    @Override
    public void run() {
        if (stand.getFuelLevel() <= 0) {
            stand.setBrewingTime(400);
            activeStands.remove(inventory);
            cancel();
            return;
        }
        if (stand.getLocation().getBlock().getType() != Material.BREWING_STAND){
            //assuming block is broken
            activeStands.remove(inventory);
            cancel();
            return;
        }
        if (!activeStands.containsKey(inventory)){
            stand.setBrewingTime(400);
            cancel();
            return;
        }
        if(time <= 0)
        {
            Player brewer = playerUsageTracker.get(stand);
            if (brewer == null){
                brewer = (inventory.getViewers().size() > 0) ? (Player) inventory.getViewers().get(0) : null;
            }

            boolean[] success = new boolean[] {false, false, false};
            boolean cancelled = false;
            for(int i = 0; i < 3 ; i ++)
            {
                if(Utils.isItemEmptyOrNull(inventory.getItem(i)))
                    continue;
                if (recipes.get(i) != null){
                    ItemStack result = inventory.getItem(i);
                    assert result != null;
                    ItemStack backup = result.clone();
                    result = result.clone();

                    if (brewer != null){
                        result = DynamicItemModifier.modify(result, brewer, recipes.get(i).getItemModifiers(), false, true, true);

                        //List<DynamicItemModifier> modifiers = new ArrayList<>(recipes.get(i).getItemModifiers());
                        //modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                        //for (DynamicItemModifier modifier : modifiers){
                        //    if (result == null) break;
                        //    result = modifier.processItem(brewer, result);
                        //}

                        PotionEffectManager.renamePotion(result, true);
                        PlayerCustomBrewEvent event = new PlayerCustomBrewEvent(brewer, recipes.get(i), stand, result != null);
                        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
                        cancelled = event.isCancelled();
                    }
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
                    boolean consume = true;
                    if (brewer != null){
                        double chance = AccumulativeStatManager.getInstance().getStats("ALCHEMY_INGREDIENT_SAVE", brewer, true);
                        if (Utils.getRandom().nextDouble() < chance) consume = false;
                    }
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
                                stand.getWorld().dropItem(stand.getLocation().add(0.5, 0.8, 0.5), ingredient);
                            }
                        }
                    }
                    stand.setFuelLevel(stand.getFuelLevel() - 1);
                    stand.getWorld().playEffect(stand.getLocation(), Effect.BREWING_STAND_BREW, 1);
                } else {
                    stand.getWorld().playEffect(stand.getLocation(), Effect.EXTINGUISH, 1);
                }
            }
            stand.setBrewingTime(400);
            activeStands.remove(inventory);
            cancel();
            return;
        }
        if (!Utils.isItemEmptyOrNull(inventory.getIngredient())){
            if(!inventory.getIngredient().isSimilar(ingredient))
            {
                stand.setBrewingTime(400);
                activeStands.remove(inventory);
                cancel();
                return;
            }
        } else {
            stand.setBrewingTime(400);
            activeStands.remove(inventory);
            cancel();
        }
        boolean isEmpty = true;
        for (int i = 0; i < 3; i++){
            if (!Utils.isItemEmptyOrNull(stand.getInventory().getItem(i))) {
                isEmpty = false;
                break;
            }
        }
        if (isEmpty){
            stand.setBrewingTime(400);
            activeStands.remove(inventory);
            cancel();
            return;
        }

        if (time == 400){
            // checking at the start of the brewing process if the recipes will even work at all
            Player brewer = playerUsageTracker.get(stand);
            if (brewer == null){
                brewer = (inventory.getViewers().size() > 0) ? (Player) inventory.getViewers().get(0) : null;
            }

            boolean[] success = new boolean[] {false, false, false};
            for(int i = 0; i < 3 ; i ++)
            {
                if(Utils.isItemEmptyOrNull(inventory.getItem(i)))
                    continue;
                if (recipes.get(i) != null){
                    ItemStack theoreticalResult = inventory.getItem(i);
                    assert theoreticalResult != null;
                    ItemStack backup = theoreticalResult.clone();
                    theoreticalResult = theoreticalResult.clone();

                    if (brewer != null){
                        backup = DynamicItemModifier.modify(backup, brewer, recipes.get(i).getItemModifiers(), false, true, true);

                        //List<DynamicItemModifier> modifiers = new ArrayList<>(recipes.get(i).getItemModifiers());
                        //modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                        //for (DynamicItemModifier modifier : modifiers){
                        //    if (backup == null) break;
                        //    backup = modifier.processItem(brewer, theoreticalResult);
                        //}
                    }
                    if (backup != null){
                        success[i] = true;
                    }
                }
            }

            if (!(success[0] || success[1] || success[2])) {
                //cancel recipe if at the start there are no successful recipes executable
                stand.setBrewingTime(400);
                activeStands.remove(inventory);
                cancel();
                return;
            }
        }

        actualTime -= timeStep;
        time = Math.max(0, (int) actualTime);
        stand.setBrewingTime(time);
        stand.update();
    }

    public void resetStand(BrewerInventory inventory, Map<Integer, DynamicBrewingRecipe> recipes){
        cancel();
        CustomBrewingProcess newProcess = new CustomBrewingProcess(recipes, inventory, this.baseTime);
        activeStands.put(inventory, newProcess);
        newProcess.setActualTime(this.actualTime);
        newProcess.setTime(this.time);
    }

    public void setActualTime(double actualTime) {
        this.actualTime = actualTime;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public static Map<BrewerInventory, CustomBrewingProcess> getActiveStands() {
        return activeStands;
    }

    public static Map<BrewingStand, Player> getPlayerUsageTracker() {
        return playerUsageTracker;
    }

    public Map<Integer, DynamicBrewingRecipe> getRecipes() {
        return recipes;
    }

    public BrewingStand getStand() {
        return stand;
    }
}