package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityRegainHealthEvent;

public class HealingListener implements Listener {

    @EventHandler
    public void onEntityHeal(EntityRegainHealthEvent e){
        if (e.isCancelled()) return;
        double newAmount = e.getAmount() * (1 + AccumulativeStatManager.getInstance().getStats("HEALING_BONUS", e.getEntity(), true));
        e.setAmount(newAmount);
    }
}
