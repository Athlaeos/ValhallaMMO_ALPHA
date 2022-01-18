package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.skills.smithing.ItemTreatmentManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ItemDamageListener implements Listener {

    @EventHandler (priority=EventPriority.LOWEST)
    public void onDurabilityChange(PlayerItemDamageEvent e){
        ItemTreatmentManager.getInstance().damageItem(e.getItem(), e.getDamage());
        e.setCancelled(ItemTreatmentManager.getInstance().getDurability(e.getItem()) > 0);
    }
}
