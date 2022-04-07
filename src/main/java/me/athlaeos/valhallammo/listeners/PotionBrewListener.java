package me.athlaeos.valhallammo.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.BrewEvent;

public class PotionBrewListener implements Listener {
    public PotionBrewListener(){

    }

    @EventHandler(priority= EventPriority.HIGHEST)
    public void onBrew(BrewEvent e){
        e.setCancelled(true);
    }

}
