package me.athlaeos.valhallammo.listeners;

import com.jeff_media.customblockdata.CustomBlockData;
import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.commands.valhalla_commands.RecipeRevealToggleCommand;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicCampfireRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.DynamicCookingRecipe;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.managers.CooldownManager;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.*;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

public class CookingListener implements Listener {
    private final NamespacedKey ownerKey = new NamespacedKey(ValhallaMMO.getPlugin(), "owner_cooking_block");
    private final Map<Block, Map<Integer, DynamicCampfireRecipe>> campfireRecipes = new HashMap<>();
    private final Map<Block, DynamicCookingRecipe<?>> activeFurnaceRecipes = new HashMap<>();

    @EventHandler
    public void furnaceUserTracker(InventoryClickEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getWhoClicked().getWorld().getName())) return;
        if (e.getView().getTopInventory() instanceof FurnaceInventory){
            FurnaceInventory inventory = (FurnaceInventory) e.getView().getTopInventory();
            if (inventory.getHolder() != null){
                Furnace furnace = inventory.getHolder();

                setOwner(furnace.getBlock(), e.getWhoClicked().getUniqueId());
            }
        }
    }

    private void setOwner(Block b, UUID owner){
        PersistentDataContainer customBlockData = new CustomBlockData(b, ValhallaMMO.getPlugin());
        customBlockData.set(ownerKey, PersistentDataType.STRING, owner.toString());
    }

    private Player getOwner(Block b){
        PersistentDataContainer customBlockData = new CustomBlockData(b, ValhallaMMO.getPlugin());
        String value = customBlockData.get(ownerKey, PersistentDataType.STRING);
        if (value != null) {
            return ValhallaMMO.getPlugin().getServer().getPlayer(UUID.fromString(value));
        }
        return null;
    }

    private boolean hasKey(Block b){
        PersistentDataContainer customBlockData = new CustomBlockData(b, ValhallaMMO.getPlugin());
        return customBlockData.has(ownerKey, PersistentDataType.STRING);
    }

    private void removeKey(Block b){
        PersistentDataContainer customBlockData = new CustomBlockData(b, ValhallaMMO.getPlugin());
        customBlockData.remove(ownerKey);
    }

    @EventHandler
    public void campfireUsageListener(PlayerInteractEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getPlayer().getWorld().getName())) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.CAMPFIRE || e.getClickedBlock().getType() == Material.SOUL_CAMPFIRE)){
                if (e.getClickedBlock().getState() instanceof Campfire){
                    if (!CooldownManager.getInstance().isCooldownPassed(e.getPlayer().getUniqueId(), "delay_dynamic_campfire_attempts")){
                        e.setCancelled(true);
                        return;
                    }
                    Campfire campfire = (Campfire) e.getClickedBlock().getState();

                    setOwner(campfire.getBlock(), e.getPlayer().getUniqueId());

                    int firstSlot = getFirstEmptyCampfireSlot(campfire);

                    if (firstSlot >= 0){
                        ItemStack handItem = e.getPlayer().getInventory().getItemInMainHand();
                        CampfireRecipe recipe = getCampfireRecipeAssociatedToItem(handItem);
                        if (recipe == null) {
                            handItem = e.getPlayer().getInventory().getItemInOffHand();
                            recipe = getCampfireRecipeAssociatedToItem(handItem);
                        }
                        if (recipe == null) return; // neither hands hold an item that is compatible with a campfire

                        Map<Integer, DynamicCampfireRecipe> campfireContents = campfireRecipes.getOrDefault(campfire.getBlock(), new HashMap<>());
                        DynamicCookingRecipe<?> dRecipe = CustomRecipeManager.getInstance().getCookingRecipesByKey().get(recipe.getKey());
                        if (dRecipe != null){
                            if (dRecipe instanceof DynamicCampfireRecipe) {
                                // dynamic campfire recipe found for this item
                                DynamicCampfireRecipe r = (DynamicCampfireRecipe) dRecipe;

                                if (e.getClickedBlock().getType() == Material.SOUL_CAMPFIRE && !r.worksForSoulCampfire()){
                                    e.setCancelled(true);
                                    return;
                                }
                                if (e.getClickedBlock().getType() == Material.CAMPFIRE && !r.worksForCampfire()){
                                    e.setCancelled(true);
                                    return;
                                }

                                if (!r.isUnlockedForEveryone()){
                                    if (!e.getPlayer().hasPermission("valhalla.allrecipes")){
                                        Profile profile = ProfileManager.getManager().getProfile(e.getPlayer(), "ACCOUNT");
                                        if (profile instanceof AccountProfile){
                                            if (!((AccountProfile) profile).getUnlockedRecipes().contains(r.getName())){
                                                // player does not have this recipe unlocked
                                                e.setCancelled(true);
                                                return;
                                            }
                                        }
                                    }
                                }

                                if (r.requiresCustomTool()){
                                    if (EquipmentClass.getClass(handItem) != null && !SmithingItemTreatmentManager.getInstance().isItemCustom(handItem)){
                                        // item needs to be custom, but isn't
                                        e.setCancelled(true);
                                        return;
                                    }
                                }

                                ItemStack result = r.getResult().clone();
                                if (r.isTinkerInput()) result = handItem.clone(); // it's a tinkering recipe, so the recipe result is a copy of the hand item before modifiers

                                result = DynamicItemModifier.modify(result, e.getPlayer(), r.getModifiers(), false, false, true);

                                if (result == null) {
                                    // the recipe failed, so the item cannot be put. apply a half second delay until the process can be attempted again
                                    // this delay is to make sure modifiers can't be spammed, which is known to cause some lag
                                    CooldownManager.getInstance().setCooldown(e.getPlayer().getUniqueId(), 500, "delay_dynamic_campfire_attempts");
                                    e.setCancelled(true);
                                    return;
                                }

                                campfireContents.put(firstSlot, r); // inserting the slot's DynamicCampfireRecipe to use when the recipe finishes
                                campfireRecipes.put(campfire.getBlock(), campfireContents);

                            } else e.setCancelled(true); // a recipe was found, but it's not a campfire recipe ((this should not occur))
                        } else {
                            if (RecipeRevealToggleCommand.getRevealRecipesForCollection().contains(e.getPlayer().getUniqueId())){
                                e.getPlayer().sendMessage("CAMPFIRE: " + recipe.getKey().getKey());
                            }
                            //if (CustomRecipeManager.getInstance().getDisabledRecipes().contains(recipe.getKey())) e.setCancelled(true);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onFurnaceBurn(FurnaceBurnEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getBlock().getWorld().getName())) return;
        Block b = e.getBlock();
        if (b.getBlockData() instanceof Furnace){
            Furnace furnace = (Furnace) b.getBlockData();

            Player who = getOwner(furnace.getBlock());
            if (who != null && CooldownManager.getInstance().isCooldownPassed(who.getUniqueId(), "delay_dynamic_furnace_attempts")){
                DynamicCookingRecipe<?> dynamicRecipe;
                CookingRecipe<?> vanillaRecipe;
                if (furnace instanceof BlastFurnace){
                    vanillaRecipe = getBlastingRecipeAssociatedToItem(furnace.getInventory().getItem(0));
                    dynamicRecipe = vanillaRecipe != null ? CustomRecipeManager.getInstance().getCookingRecipesByKey().get(vanillaRecipe.getKey()) : null;
                } else if (furnace instanceof Smoker){
                    vanillaRecipe = getSmokingRecipeAssociatedToItem(furnace.getInventory().getItem(0));
                    dynamicRecipe = vanillaRecipe != null ? CustomRecipeManager.getInstance().getCookingRecipesByKey().get(vanillaRecipe.getKey()) : null;
                } else {
                    vanillaRecipe = getFurnaceRecipeAssociatedToItem(furnace.getInventory().getItem(0));
                    dynamicRecipe = vanillaRecipe != null ? CustomRecipeManager.getInstance().getCookingRecipesByKey().get(vanillaRecipe.getKey()) : null;
                }
                if (vanillaRecipe != null){
                    if (RecipeRevealToggleCommand.getRevealRecipesForCollection().contains(who.getUniqueId())){
                        who.sendMessage("FURNACE: " + vanillaRecipe.getKey().getKey());
                    }
                }

                if (dynamicRecipe != null && !Utils.isItemEmptyOrNull(furnace.getInventory().getResult()) && hasKey(furnace.getBlock())){ // burn item should never be null during a burn event anyway
                    ItemStack result = dynamicRecipe.getResult().clone();
                    if (dynamicRecipe.isTinkerInput()) result = furnace.getInventory().getResult().clone(); // it's a tinkering recipe, so the recipe result is a copy of the burn item before modifiers

                    result = DynamicItemModifier.modify(result, who, dynamicRecipe.getModifiers(), false, false, true);

                    if (result == null){
                        // recipe failed, cancelling burn event
                        CooldownManager.getInstance().setCooldown(who.getUniqueId(), 500, "delay_dynamic_furnace_attempts");
                        e.setCancelled(true);
                    }
                }
            }
        }
    }

    // BURN -> START -> SMELT -> START -> SMELT
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceStart(FurnaceStartSmeltEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getBlock().getWorld().getName())) return;

        if (e.getBlock().getState() instanceof Furnace){
            DynamicCookingRecipe<?> recipe = CustomRecipeManager.getInstance().getCookingRecipesByKey().get(e.getRecipe().getKey());
            if (recipe != null) {
                activeFurnaceRecipes.put(e.getBlock(), recipe);
                //ValhallaMMO.getPlugin().getServer().getScheduler().runTaskLater(ValhallaMMO.getPlugin(), () -> , 1L);
            } else {
                activeFurnaceRecipes.remove(e.getBlock());
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onCook(BlockCookEvent e){
        if (!e.isCancelled()){
            if ((e.getBlock().getType() == Material.CAMPFIRE || e.getBlock().getType() == Material.SOUL_CAMPFIRE) && e.getBlock().getState() instanceof Campfire){
                Campfire campfire = (Campfire) e.getBlock().getState();

                int finishedSlot = getFinishedCampfireCook(campfire);
                if (finishedSlot >= 0){
                    Map<Integer, DynamicCampfireRecipe> campfireRecipes = this.campfireRecipes.getOrDefault(campfire.getBlock(), new HashMap<>());
                    if (campfireRecipes.containsKey(finishedSlot)){
                        ItemStack result = campfire.getItem(finishedSlot);
                        if (result == null) return; // this should never occur because the slot should never be empty upon completion
                        ItemStack original = result.clone();
                        result = result.clone();

                        Player p = getOwner(campfire.getBlock());
                        if (p != null && p.isOnline()){
                            DynamicCampfireRecipe recipe = campfireRecipes.get(finishedSlot);
                            if (!recipe.isTinkerInput()) result = recipe.getResult();

                            result = DynamicItemModifier.modify(result, p, recipe.getModifiers(), false, true, true);

                            if (result == null){
                                // recipe failed, meaning between placing the item on the campfire and it cooking fully player stats or other conditions
                                // have changed causing the recipe to fail anyway
                                e.setResult(original);
                            } else {
                                int expReward = Utils.excessChance(recipe.getExperience());
                                if (expReward > 0){
                                    Location dropLocation = campfire.getLocation().add(0.5, 0.5, 0.5);
                                    ExperienceOrb exp = campfire.getWorld().spawn(dropLocation, ExperienceOrb.class);
                                    exp.setExperience(expReward);
                                }
                                e.setResult(result);
                            }

                            campfireRecipes.remove(finishedSlot);
                            this.campfireRecipes.put(campfire.getBlock(), campfireRecipes);
                        } else {
                            // player is offline or not found, spitting out original item
                            e.setResult(result);
                        }
                    }
                }
            } else if (e.getBlock().getState() instanceof Furnace){
                Furnace furnace = (Furnace) e.getBlock().getState();

                DynamicCookingRecipe<?> recipe = this.activeFurnaceRecipes.get(furnace.getBlock());
                if (recipe != null){
                    ItemStack result = recipe.isTinkerInput() ? furnace.getInventory().getItem(0) : recipe.getResult();
                    if (result == null) return; // this should never occur because the slot should never be empty upon completion
                    if (recipe.isTinkerInput()) {
                        if (recipe.requiresCustomTool()){
                            if (EquipmentClass.getClass(result) != null && !SmithingItemTreatmentManager.getInstance().isItemCustom(result)){
                                // item needs to be custom, but isn't
                                e.setCancelled(true);
                                return;
                            }
                        }
                        result.setAmount(1);
                    }
                    ItemStack original = result.clone();
                    result = result.clone();

                    Player p = getOwner(furnace.getBlock());
                    if (p != null && p.isOnline()){
                        result = DynamicItemModifier.modify(result, p, recipe.getModifiers(), false, true, true);

                        if (result == null){
                            // recipe failed, meaning between placing the item on the campfire and it cooking fully player stats or other conditions
                            // have changed causing the recipe to fail anyway
                            e.setResult(original);
                        } else {
                            int expReward = Utils.excessChance(recipe.getExperience());
                            if (expReward > 0){
                                Location dropLocation = furnace.getLocation().add(0.5, 0.5, 0.5);
                                ExperienceOrb exp = furnace.getWorld().spawn(dropLocation, ExperienceOrb.class);
                                exp.setExperience(expReward);
                            }
                            e.setResult(result);
                        }

                        this.activeFurnaceRecipes.remove(furnace.getBlock());
                    } else {
                        // player is offline or not found, spitting out original item
                        e.setResult(result);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCampfireBreak(BlockBreakEvent e){
        if (!e.isCancelled()){
            campfireRecipes.remove(e.getBlock());
            activeFurnaceRecipes.remove(e.getBlock());
            removeKey(e.getBlock());
        }
    }

    private int getFinishedCampfireCook(Campfire campfire){
        for (int i = 0; i < 4; i++) if (campfire.getItem(i) != null && campfire.getCookTime(i) > 0 && campfire.getCookTime(i) == campfire.getCookTimeTotal(i)) return i;
        return -1;
    }

    private int getFirstEmptyCampfireSlot(Campfire campfire){
        if (Utils.isItemEmptyOrNull(campfire.getItem(0))) return 0;
        if (Utils.isItemEmptyOrNull(campfire.getItem(1))) return 1;
        if (Utils.isItemEmptyOrNull(campfire.getItem(2))) return 2;
        if (Utils.isItemEmptyOrNull(campfire.getItem(3))) return 3;
        return -1;
    }

    private CampfireRecipe getCampfireRecipeAssociatedToItem(ItemStack input){
        if (Utils.isItemEmptyOrNull(input)) return null;
        Iterator<Recipe> iterator = ValhallaMMO.getPlugin().getServer().recipeIterator();
        CampfireRecipe found = null;
        while (iterator.hasNext()){
            Recipe r = iterator.next();
            if (r instanceof CampfireRecipe){
                CampfireRecipe recipe = (CampfireRecipe) r;
                if (recipe.getInputChoice().test(input)) {
                    if (recipe.getInputChoice() instanceof RecipeChoice.ExactChoice) return recipe;
                    found = recipe;
                }
            }
        }
        return found;
    }

    private FurnaceRecipe getFurnaceRecipeAssociatedToItem(ItemStack input){
        if (Utils.isItemEmptyOrNull(input)) return null;
        Iterator<Recipe> iterator = ValhallaMMO.getPlugin().getServer().recipeIterator();
        FurnaceRecipe found = null;
        while (iterator.hasNext()){
            Recipe r = iterator.next();
            if (r instanceof FurnaceRecipe){
                FurnaceRecipe recipe = (FurnaceRecipe) r;
                if (recipe.getInputChoice().test(input)) {
                    if (recipe.getInputChoice() instanceof RecipeChoice.ExactChoice) return recipe;
                    found = recipe;
                }
            }
        }
        return found;
    }

    private BlastingRecipe getBlastingRecipeAssociatedToItem(ItemStack input){
        if (Utils.isItemEmptyOrNull(input)) return null;
        Iterator<Recipe> iterator = ValhallaMMO.getPlugin().getServer().recipeIterator();
        BlastingRecipe found = null;
        while (iterator.hasNext()){
            Recipe r = iterator.next();
            if (r instanceof BlastingRecipe){
                BlastingRecipe recipe = (BlastingRecipe) r;
                if (recipe.getInputChoice().test(input)) {
                    if (recipe.getInputChoice() instanceof RecipeChoice.ExactChoice) return recipe;
                    found = recipe;
                }
            }
        }
        return found;
    }

    private SmokingRecipe getSmokingRecipeAssociatedToItem(ItemStack input){
        if (Utils.isItemEmptyOrNull(input)) return null;
        Iterator<Recipe> iterator = ValhallaMMO.getPlugin().getServer().recipeIterator();
        SmokingRecipe found = null;
        while (iterator.hasNext()){
            Recipe r = iterator.next();
            if (r instanceof SmokingRecipe){
                SmokingRecipe recipe = (SmokingRecipe) r;
                if (recipe.getInputChoice().test(input)) {
                    if (recipe.getInputChoice() instanceof RecipeChoice.ExactChoice) return recipe;
                    found = recipe;
                }
            }
        }
        return found;
    }
}
