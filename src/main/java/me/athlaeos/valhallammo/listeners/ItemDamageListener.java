package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.managers.CustomDurabilityManager;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemDamageEvent;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class ItemDamageListener implements Listener {
    private static final Collection<UUID> excludeEquipmentDamage = new HashSet<>();

    @EventHandler(priority=EventPriority.NORMAL)
    public void onDurabilityChange(PlayerItemDamageEvent e){
        if (excludeEquipmentDamage.contains(e.getPlayer().getUniqueId())){
            e.setCancelled(true);
            return;
        }
        if (SmithingItemTreatmentManager.getInstance().isItemCustom(e.getItem())){
            CustomDurabilityManager.getInstance().damageItem(e.getItem(), e.getDamage());
            e.setCancelled(CustomDurabilityManager.getInstance().getDurability(e.getItem()) > 0);
        }
    }

    public static Collection<UUID> getExcludeEquipmentDamage() {
        return excludeEquipmentDamage;
    }
}
