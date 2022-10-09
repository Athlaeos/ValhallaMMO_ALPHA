package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.items.ItemTreatment;
import me.athlaeos.valhallammo.managers.CustomDurabilityManager;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemMendEvent;

public class ItemMendListener implements Listener {

    @EventHandler(priority=EventPriority.NORMAL)
    public void onItemMend(PlayerItemMendEvent e){
        if (e.isCancelled()) return;
        if (SmithingItemTreatmentManager.getInstance().hasTreatment(e.getItem(), ItemTreatment.UNMENDABLE)){
            e.setCancelled(true);
            return;
        }
        if (CustomDurabilityManager.getInstance().hasCustomDurability(e.getItem())){
            CustomDurabilityManager.getInstance().damageItem(e.getItem(), -e.getRepairAmount());
            e.setCancelled(true);
        }
    }
}
