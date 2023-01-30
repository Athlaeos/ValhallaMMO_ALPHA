package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;
import org.bukkit.event.inventory.InventoryMoveItemEvent;
import org.bukkit.inventory.BrewerInventory;

public class PotionBrewListener implements Listener {
    public PotionBrewListener(){

    }

    @EventHandler(priority= EventPriority.HIGHEST)
    public void onBrew(BrewEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getBlock().getWorld().getName())) return;
        if (CustomRecipeManager.getInstance().getBrewingRecipes().isEmpty()) return;
        e.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onBrewingStandHopperEvent(InventoryMoveItemEvent e){
        if (CustomRecipeManager.getInstance().getBrewingRecipes().isEmpty()) return;
        if (e.getSource() instanceof BrewerInventory){
            e.setCancelled(true);
        }
    }
}
