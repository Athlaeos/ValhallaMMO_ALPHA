package me.athlaeos.valhallammo.skills;

import org.bukkit.event.player.PlayerMoveEvent;

public interface MovementSkill {
    void onPlayerMove(PlayerMoveEvent event);
}
