package me.athlaeos.valhallammo.core_listeners;

import me.athlaeos.valhallammo.Main;
import me.athlaeos.valhallammo.configs.ConfigManager;
import me.athlaeos.valhallammo.core_managers.CooldownManager;
import me.athlaeos.valhallammo.recipes.dom.AbstractCraftingRecipe;
import me.athlaeos.valhallammo.recipes.dom.ItemCraftingRecipe;
import me.athlaeos.valhallammo.core_managers.MaterialCosmeticManager;
import me.athlaeos.valhallammo.recipes.dom.ItemImprovementRecipe;
import me.athlaeos.valhallammo.recipes.managers.PlayerCraftChoiceManager;
import me.athlaeos.valhallammo.menus.PlayerMenuUtilManager;
import me.athlaeos.valhallammo.recipes.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.menus.SmithingRecipeMenu;
import me.athlaeos.valhallammo.recipes.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import java.util.*;

public class InteractListener implements Listener {
    private final Map<UUID, Material> itemsHeld = new HashMap<>();
    private final boolean spawnOnTopOfBlock;
    public InteractListener(){
        spawnOnTopOfBlock = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("craft_item_drop");
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        trackRightClicksHeldDuration(e);
    }

    private void trackRightClicksHeldDuration(PlayerInteractEvent e){
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (e.getClickedBlock() != null){
                // Tracking how long players held a mouse button
                CooldownManager cooldownManager = CooldownManager.getInstance();
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (cooldownManager.getTimerResult(e.getPlayer().getUniqueId(), "time_since_right_click") > 500
                            || !itemsHeld.containsKey(e.getPlayer().getUniqueId())) {
                        cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_held_right_click");
                        itemsHeld.put(e.getPlayer().getUniqueId(), e.getPlayer().getInventory().getItemInMainHand().getType());
                    }
                    if (itemsHeld.containsKey(e.getPlayer().getUniqueId())) {
                        if (e.getPlayer().getInventory().getItemInMainHand().getType() != itemsHeld.get(e.getPlayer().getUniqueId())) {
                            cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_held_right_click");
                            itemsHeld.put(e.getPlayer().getUniqueId(), e.getPlayer().getInventory().getItemInMainHand().getType());
                        }
                    }
                }

                if (e.getClickedBlock().getType().isInteractable()){
                    if (!cooldownManager.isCooldownPassed(e.getPlayer().getUniqueId(), "cancel_block_interactions")){
                        e.setCancelled(true);
                    }
                }

                // Opening a recipe picking menu
                if (e.getPlayer().isSneaking()){
                    if (CustomRecipeManager.getInstance().getCraftingStationRecipes().containsKey(e.getClickedBlock().getType())){
                        if (e.getPlayer().getInventory().getItemInMainHand().getType() == Material.AIR
                            && e.getPlayer().getInventory().getItemInOffHand().getType() == Material.AIR){
                            // player is sneak right clicking a block that has recipes with empty hands
                            SmithingRecipeMenu menu = new SmithingRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility(e.getPlayer()), e.getClickedBlock().getType());
                            if (menu.getUnlockedRecipes().size() == 1){
                                if (!e.getClickedBlock().getType().isInteractable()){
                                    if (cooldownManager.isCooldownPassed(e.getPlayer().getUniqueId(), "craft_cooldown")){
                                        ItemCraftingRecipe recipe = menu.getUnlockedRecipes().get(0);
                                        PlayerCraftChoiceManager.getInstance().setPlayerCurrentRecipe(e.getPlayer(), recipe);
                                        e.getPlayer().sendMessage(Utils.chat("&aNow crafting " + recipe.getName()));
                                        cooldownManager.setCooldown(e.getPlayer().getUniqueId(), 500, "craft_cooldown");
                                    }
                                } else {
                                    e.setCancelled(true);
                                    menu.open();
                                }
                            } else if (menu.getUnlockedRecipes().size() > 0){
                                e.setCancelled(true);
                                menu.open();
                            }
                        }
                    }
                }
                // Cancelling block interactions if player is trying to craft something
                ItemCraftingRecipe currentRecipe = PlayerCraftChoiceManager.getInstance().getPlayerCurrentRecipe(e.getPlayer());
                if (currentRecipe != null){
                    // Player is crafting an item
                    if (e.getClickedBlock().getType() == currentRecipe.getCraftingBlock()){
                        if (e.getClickedBlock().getType().isInteractable()){
                            e.setCancelled(true);
                        }
                        // Checks if the player can craft the recipe once on first click, and once at the end.
                        // If it's been more than 500ms since the players last right click, so if the player clicks a crafting
                        // station, or their first click, the player is notified they dont have the materials.
                        // If it has been shorter, so if the player has been right clicking the station for longer than 500ms,
                        // then it checks if the required crafting time right clicking has been held before crafting.
                        if (cooldownManager.getTimerResult(e.getPlayer().getUniqueId(), "time_since_right_click") > 500){
                            if (!canCraft(currentRecipe, e.getPlayer())){
                                e.getPlayer().sendMessage(Utils.chat("&cNope cant craft bucko"));
                                cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_held_right_click");
                                PlayerCraftChoiceManager.getInstance().setPlayerCurrentRecipe(e.getPlayer(), null);
                            } else {
                                if (cooldownManager.isCooldownPassed(e.getPlayer().getUniqueId(), "sound_craft")){
                                    Sound sound = MaterialCosmeticManager.getInstance().getCraftWorkSound(e.getClickedBlock().getType());
                                    if (sound != null){
                                        e.getPlayer().playSound(e.getPlayer().getLocation(), sound, .7F, 1F);
                                    }
                                    e.getPlayer().spawnParticle(Particle.BLOCK_DUST, e.getClickedBlock().getLocation().add(0.5, 1.2, 0.5), 3, e.getClickedBlock().getBlockData());
                                    cooldownManager.setCooldown(e.getPlayer().getUniqueId(), 500, "sound_craft");
                                }
                            }
                        } else if (cooldownManager.getTimerResult(e.getPlayer().getUniqueId(), "time_held_right_click") >= currentRecipe.getCraftingTime()){
                            if (canCraft(currentRecipe, e.getPlayer())){
                                boolean playerHasSpace = e.getPlayer().getInventory().firstEmpty() > -1;
                                if (playerHasSpace || spawnOnTopOfBlock){
                                    ItemStack result = currentRecipe.getResult().clone();
                                    // Modify item based on the recipe's improvement modifiers
                                    List<DynamicItemModifier> modifiers = new ArrayList<>(currentRecipe.getItemModifers());
                                    modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                                    for (DynamicItemModifier modifier : modifiers){
                                        if (result == null) break;
                                        result = modifier.processItem(e.getPlayer(), result);
                                    }
                                    if (result != null){
                                        if (currentRecipe.breakStation()){
                                            BlockBreakEvent event = new BlockBreakEvent(e.getClickedBlock(), e.getPlayer());
                                            Main.getPlugin().getServer().getPluginManager().callEvent(event);
                                            if (event.isCancelled()){
                                                return;
                                            } else {
                                                event.getBlock().breakNaturally();
                                            }
                                        }
                                        // player has inventory space and crafting requirements are met, ingredients are removed and result is added
                                        for (ItemStack ingredient : currentRecipe.getIngredients()){
                                            e.getPlayer().getInventory().removeItem(ingredient);
                                        }

                                        if (spawnOnTopOfBlock){
                                            Item itemDrop = (Item) e.getPlayer().getWorld().spawnEntity(e.getClickedBlock().getLocation().add(0.5, 1.2, 0.5), EntityType.DROPPED_ITEM);
                                            itemDrop.setItemStack(result);
                                            itemDrop.setPickupDelay(0);
                                            itemDrop.setOwner(e.getPlayer().getUniqueId());
                                            itemDrop.setThrower(e.getPlayer().getUniqueId());
                                        } else {
                                            e.getPlayer().getInventory().addItem(result);
                                        }
                                        Sound sound = MaterialCosmeticManager.getInstance().getCraftFinishSounds(e.getClickedBlock().getType());
                                        if (sound != null){
                                            e.getPlayer().playSound(e.getPlayer().getLocation(), sound, .7F, 1F);
                                        }
                                        e.getPlayer().spawnParticle(Particle.FIREWORKS_SPARK, e.getClickedBlock().getLocation().add(0.5, 1, 0.5), 15);
                                        PlayerCraftChoiceManager.getInstance().setPlayerCurrentRecipe(e.getPlayer(), null);
                                    } else {
                                        // The recipe is cancelled
                                        e.getPlayer().sendMessage(Utils.chat("&cCrafting failed"));
                                    }
                                    cooldownManager.setCooldown(e.getPlayer().getUniqueId(), 750, "cancel_block_interactions");
                                } else {
                                    e.getPlayer().sendMessage(Utils.chat("&cNo inventory space!"));
                                }
                            } else {
                                e.getPlayer().sendMessage(Utils.chat("&cNope cant craft bucko"));
                                PlayerCraftChoiceManager.getInstance().setPlayerCurrentRecipe(e.getPlayer(), null);
                            }
                            cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_held_right_click");
                        } else {
                            if (cooldownManager.isCooldownPassed(e.getPlayer().getUniqueId(), "sound_craft")){
                                Sound sound = MaterialCosmeticManager.getInstance().getCraftWorkSound(e.getClickedBlock().getType());
                                if (sound != null){
                                    e.getPlayer().playSound(e.getPlayer().getLocation(), sound, .7F, 1F);
                                }
                                cooldownManager.setCooldown(e.getPlayer().getUniqueId(), 500, "sound_craft");
                                e.getPlayer().spawnParticle(Particle.BLOCK_DUST, e.getClickedBlock().getLocation().add(0.5, 1.2, 0.5), 3, e.getClickedBlock().getBlockData());
                            }
                        }
                    } else {
                        PlayerCraftChoiceManager.getInstance().setPlayerCurrentRecipe(e.getPlayer(), null);
                    }
                } else {
                    // Check if player is trying to improve item
                    ItemStack heldItem = e.getPlayer().getInventory().getItemInMainHand();
                    if (heldItem.getType() != Material.AIR){
                        ItemImprovementRecipe improvementRecipe = CustomRecipeManager.getInstance().getItemImprovementRecipes().get(heldItem.getType());
                        if (improvementRecipe != null){
                            // Player might be trying to improve item
                            if (improvementRecipe.getCraftingBlock() == e.getClickedBlock().getType()){
                                if (e.getClickedBlock().getType().isInteractable()){
                                    e.setCancelled(true);
                                }
                                if (cooldownManager.getTimerResult(e.getPlayer().getUniqueId(), "time_since_right_click") > 500){
                                    if (!canCraft(improvementRecipe, e.getPlayer())){
                                        e.setCancelled(false);
                                        cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_held_right_click");
                                    } else {
                                        if (cooldownManager.isCooldownPassed(e.getPlayer().getUniqueId(), "sound_craft")){
                                            Sound sound = MaterialCosmeticManager.getInstance().getCraftWorkSound(e.getClickedBlock().getType());
                                            if (sound != null){
                                                e.getPlayer().playSound(e.getPlayer().getLocation(), sound, .7F, 1F);
                                            }
                                            e.getPlayer().spawnParticle(Particle.BLOCK_DUST, e.getClickedBlock().getLocation().add(0.5, 1.2, 0.5), 3, e.getClickedBlock().getBlockData());
                                            cooldownManager.setCooldown(e.getPlayer().getUniqueId(), 500, "sound_craft");
                                        }
                                    }
                                } else if (cooldownManager.getTimerResult(e.getPlayer().getUniqueId(), "time_held_right_click") >= improvementRecipe.getCraftingTime()){
                                    if (canCraft(improvementRecipe, e.getPlayer())){

                                        // Modify item based on the recipe's improvement modifiers
                                        List<DynamicItemModifier> modifiers = new ArrayList<>(improvementRecipe.getItemModifers());
                                        modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                                        ItemStack newItem = e.getPlayer().getInventory().getItemInMainHand().clone();
                                        for (DynamicItemModifier modifier : modifiers){
                                            if (newItem == null) break;
                                            newItem = modifier.processItem(e.getPlayer(), newItem);
                                        }

                                        if (newItem != null){
                                            if (improvementRecipe.breakStation()){
                                                BlockBreakEvent event = new BlockBreakEvent(e.getClickedBlock(), e.getPlayer());
                                                Main.getPlugin().getServer().getPluginManager().callEvent(event);
                                                if (event.isCancelled()){
                                                    return;
                                                } else {
                                                    event.getBlock().breakNaturally();
                                                }
                                            }
                                            // player has inventory space and crafting requirements are met, ingredients are removed and held item is updated
                                            for (ItemStack ingredient : improvementRecipe.getIngredients()){
                                                e.getPlayer().getInventory().removeItem(ingredient);
                                            }

                                            Sound sound = MaterialCosmeticManager.getInstance().getCraftFinishSounds(e.getClickedBlock().getType());
                                            if (sound != null){
                                                e.getPlayer().playSound(e.getPlayer().getLocation(), sound, .7F, 1F);
                                            }
                                            e.getPlayer().spawnParticle(Particle.FIREWORKS_SPARK, e.getClickedBlock().getLocation().add(0.5, 1, 0.5), 15);
                                            e.getPlayer().getInventory().setItemInMainHand(newItem);
                                        } else {
                                            e.getPlayer().sendMessage(Utils.chat("&cCrafting failed"));
                                        }
                                        cooldownManager.setCooldown(e.getPlayer().getUniqueId(), 750, "cancel_block_interactions");
                                    } else {
                                        e.getPlayer().sendMessage(Utils.chat("&cNope cant improve bucko"));
                                    }
                                    cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_held_right_click");
                                } else {
                                    if (cooldownManager.isCooldownPassed(e.getPlayer().getUniqueId(), "sound_craft")){
                                        Sound sound = MaterialCosmeticManager.getInstance().getCraftWorkSound(e.getClickedBlock().getType());
                                        if (sound != null){
                                            e.getPlayer().playSound(e.getPlayer().getLocation(), sound, .7F, 1F);
                                        }
                                        cooldownManager.setCooldown(e.getPlayer().getUniqueId(), 500, "sound_craft");
                                        e.getPlayer().spawnParticle(Particle.BLOCK_DUST, e.getClickedBlock().getLocation().add(0.5, 1.2, 0.5), 3, e.getClickedBlock().getBlockData());
                                    }
                                }
                            }
                        }
                    }
                }
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_since_right_click");
                }
            }
        }
    }

    private boolean canCraft(AbstractCraftingRecipe recipe, Player p){
        PlayerInventory inventory = p.getInventory();
        boolean canCraft = true;
        for (ItemStack i : recipe.getIngredients()){
            if (!inventory.containsAtLeast(i, i.getAmount())){
                canCraft = false;
                break;
            }
        }
        return canCraft;
    }
}
