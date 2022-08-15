package me.athlaeos.valhallammo.events;

import me.athlaeos.valhallammo.dom.CombatType;
import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class ValhallaEntityStunEvent extends EntityEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled = false;
    private double durationMultiplier;
    private final CombatType typeCause;

    public ValhallaEntityStunEvent(Entity who, CombatType type, double durationMultiplier) {
        super(who);
        this.typeCause = type;
        this.durationMultiplier = durationMultiplier;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public CombatType getTypeCause() {
        return typeCause;
    }

    public double getDurationMultiplier() {
        return durationMultiplier;
    }

    public void setDurationMultiplier(double durationMultiplier) {
        this.durationMultiplier = durationMultiplier;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
