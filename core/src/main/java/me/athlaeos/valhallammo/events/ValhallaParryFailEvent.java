package me.athlaeos.valhallammo.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class ValhallaParryFailEvent extends EntityEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled = false;
    private LivingEntity parrier;
    private final LivingEntity parried;
    private int debuffDuration;

    public ValhallaParryFailEvent(LivingEntity parrier, LivingEntity parried, int debuffDuration) {
        super(parrier);
        this.parried = parried;
        this.debuffDuration = debuffDuration;
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

    public LivingEntity getParried() {
        return parried;
    }

    public void setDebuffDuration(int debuffDuration) {
        this.debuffDuration = debuffDuration;
    }

    public int getDebuffDuration() {
        return debuffDuration;
    }

    public LivingEntity getParrier() {
        return parrier;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
