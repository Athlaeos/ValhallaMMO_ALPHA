package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.metadata.FixedMetadataValue;

public class EntitySpawnListener implements Listener {

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent e){
        if (!e.isCancelled()){
            if (e.getSpawnReason() == CreatureSpawnEvent.SpawnReason.SPAWNER){
                e.getEntity().setMetadata("valhallammo_spawnreason", new FixedMetadataValue(ValhallaMMO.getPlugin(), e.getSpawnReason().toString()));
            }
        }
    }

    public static CreatureSpawnEvent.SpawnReason getSpawnReason(Entity e){
        if (e.hasMetadata("valhallammo_spawnreason") && !e.getMetadata("valhallammo_spawnreason").isEmpty()){
            try {
                return CreatureSpawnEvent.SpawnReason.valueOf(e.getMetadata("valhallammo_spawnreason").iterator().next().asString());
            } catch (IllegalArgumentException ignored) {}
        }
        return null;
    }
}
