package me.athlaeos.valhallammo.skills;

import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public interface InteractSkill {
    void onEntityInteract(PlayerInteractEntityEvent event);

    void onAtEntityInteract(PlayerInteractAtEntityEvent event);

    void onInteract(PlayerInteractEvent event);
}
