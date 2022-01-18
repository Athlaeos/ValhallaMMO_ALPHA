package me.athlaeos.valhallammo.skills;

import org.bukkit.event.player.PlayerItemConsumeEvent;

public interface ItemConsumptionSkill {
    void onItemConsume(PlayerItemConsumeEvent event);
}
