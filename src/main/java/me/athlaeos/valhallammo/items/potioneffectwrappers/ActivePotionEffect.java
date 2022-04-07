package me.athlaeos.valhallammo.items.potioneffectwrappers;

import org.bukkit.event.player.PlayerItemConsumeEvent;

public interface ActivePotionEffect {
    void onEvent(PlayerItemConsumeEvent e);
}
