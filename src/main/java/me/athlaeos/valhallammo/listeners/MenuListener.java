package me.athlaeos.valhallammo.core_listeners;

import me.athlaeos.valhallammo.domain.Menu;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.PlayerInventory;

public class MenuListener implements Listener {

    @EventHandler
    public void onMenuClick(InventoryClickEvent e){
        if (e.getView().getTopInventory().getHolder() instanceof Menu && e.getView().getBottomInventory() instanceof PlayerInventory){
            Menu m = (Menu) e.getView().getTopInventory().getHolder();

            m.handleMenu(e);
        }
    }
}