package me.athlaeos.valhallammo.events;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerResetEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final boolean hardReset;
    private final Player player;

    public PlayerResetEvent(Player player, boolean hardReset){
        this.player = player;
        this.hardReset = hardReset;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public boolean isHardReset() {
        return hardReset;
    }

    public Player getPlayer() {
        return player;
    }

}
