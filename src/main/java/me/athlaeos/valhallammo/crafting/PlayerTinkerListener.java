package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.Main;
import me.athlaeos.valhallammo.configs.ConfigManager;
import me.athlaeos.valhallammo.events.DynamicModifierApplicationEvent;
import me.athlaeos.valhallammo.events.PlayerItemTinkerEvent;
import me.athlaeos.valhallammo.managers.CooldownManager;
import me.athlaeos.valhallammo.managers.MaterialCosmeticManager;
import me.athlaeos.valhallammo.skills.smithing.recipes.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayerTinkerListener implements Listener {
    CooldownManager cooldownManager = CooldownManager.getInstance();
    private final String errorNoSpace;
    private final String errorTinkeringFailed;

    public PlayerTinkerListener(){
        errorNoSpace = ConfigManager.getInstance().getConfig("messages.yml").get().getString("error_no_space");
        errorTinkeringFailed = ConfigManager.getInstance().getConfig("messages.yml").get().getString("error_conditions_failed");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCustomCraft(PlayerItemTinkerEvent e){
        if (!e.isCancelled()){
            ItemStack playerHand = e.getPlayer().getInventory().getItemInMainHand().clone();
            boolean playerHasSpace = e.getPlayer().getInventory().firstEmpty() > -1;

            if (playerHand.getAmount() > 1){
                if (!playerHasSpace){
                    if (!errorNoSpace.equals("")){
                        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(errorNoSpace)));
                    }
                }
            }
            // Modify item based on the recipe's improvement modifiers
            List<DynamicItemModifier> modifiers = new ArrayList<>(e.getRecipe().getItemModifers());
            modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
            ItemStack newItem = e.getPlayer().getInventory().getItemInMainHand().clone();
            for (DynamicItemModifier modifier : modifiers){
                if (newItem == null) break;
                if (newItem.getType() == Material.AIR) break;
                DynamicModifierApplicationEvent event = new DynamicModifierApplicationEvent(e.getPlayer(), modifier, newItem);
                Main.getPlugin().getServer().getPluginManager().callEvent(event);
                newItem = event.getAppliedOn();
            }

            if (newItem != null){
                if (e.getRecipe().breakStation()){
                    BlockBreakEvent event = new BlockBreakEvent(e.getCraftingStation(), e.getPlayer());
                    Main.getPlugin().getServer().getPluginManager().callEvent(event);
                    if (event.isCancelled()){
                        return;
                    } else {
                        event.getBlock().breakNaturally();
                    }
                }
                // player has inventory space and crafting requirements are met, ingredients are removed and held item is updated
                for (ItemStack ingredient : e.getRecipe().getIngredients()){
                    e.getPlayer().getInventory().removeItem(ingredient);
                }

                Sound sound = MaterialCosmeticManager.getInstance().getCraftFinishSounds(e.getCraftingStation().getType());
                if (sound != null){
                    e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), sound, .7F, 1F);
                }
                e.getPlayer().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, e.getCraftingStation().getLocation().add(0.5, 1, 0.5), 15);
                if (playerHand.getAmount() > 1){
                    playerHand.setAmount(playerHand.getAmount() - 1);
                    e.getPlayer().getInventory().setItemInMainHand(playerHand);
                    e.getPlayer().getInventory().addItem(newItem);
                } else {
                    e.getPlayer().getInventory().setItemInMainHand(newItem);
                }
                cooldownManager.setCooldown(e.getPlayer().getUniqueId(), 750, "cancel_block_interactions");
            } else {
                if (!errorTinkeringFailed.equals("")){
                    e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(errorTinkeringFailed)));
                }
            }
        }
    }
}
