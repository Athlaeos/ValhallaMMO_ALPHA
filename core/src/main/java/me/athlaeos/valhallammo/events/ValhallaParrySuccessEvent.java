package me.athlaeos.valhallammo.events;

import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class ValhallaParrySuccessEvent extends EntityEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled = false;
    private LivingEntity parrier;
    private final LivingEntity parried;
    private double damageMultiplier;
    private int debuffDuration;

    public ValhallaParrySuccessEvent(LivingEntity parrier, LivingEntity parried, double damageMultiplier, int debuffDuration) {
        super(parrier);
        this.parried = parried;
        this.damageMultiplier = damageMultiplier;
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

    public double getDamageMultiplier() {
        return damageMultiplier;
    }

    public void setDamageMultiplier(double damageMultiplier) {
        this.damageMultiplier = damageMultiplier;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
