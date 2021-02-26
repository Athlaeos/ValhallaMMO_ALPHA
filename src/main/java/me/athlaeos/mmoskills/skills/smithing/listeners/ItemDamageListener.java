package me.athlaeos.mmoskills.skills.smithing.listeners;

import me.athlaeos.mmoskills.skills.smithing.managers.ItemDurabilityManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

public class ItemDamageListener implements Listener {

    @EventHandler (priority=EventPriority.LOWEST)
    public void onDurabilityChange(PlayerItemDamageEvent e){
        ItemDurabilityManager.getInstance().damageItem(e.getItem(), e.getDamage());
        if (ItemDurabilityManager.getInstance().getDurability(e.getItem()) > 0){
            e.setCancelled(true);
        }
    }
}
