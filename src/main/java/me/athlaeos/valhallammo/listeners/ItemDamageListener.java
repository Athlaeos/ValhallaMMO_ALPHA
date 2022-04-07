package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.managers.CustomDurabilityManager;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ItemDamageListener implements Listener {

    @EventHandler(priority=EventPriority.LOWEST)
    public void onDurabilityChange(PlayerItemDamageEvent e){
        if (SmithingItemTreatmentManager.getInstance().isItemCustom(e.getItem())){
            CustomDurabilityManager.getInstance().damageItem(e.getItem(), e.getDamage());
            e.setCancelled(CustomDurabilityManager.getInstance().getDurability(e.getItem()) > 0);
        }
    }
}
