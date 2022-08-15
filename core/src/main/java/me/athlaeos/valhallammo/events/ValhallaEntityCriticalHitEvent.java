package me.athlaeos.valhallammo.events;

import me.athlaeos.valhallammo.dom.CombatType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;

public class ValhallaEntityCriticalHitEvent extends EntityEvent implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled = false;
    private double criticalHitDamageMultiplier;
    private double damageBeforeCrit;
    private final CombatType type;
    private final LivingEntity target;

    public ValhallaEntityCriticalHitEvent(LivingEntity critter, LivingEntity target, CombatType type, double damageBeforeCrit, double criticalHitDamageMultiplier) {
        super(critter);
        this.type = type;
        this.target = target;
        this.damageBeforeCrit = damageBeforeCrit;
        this.criticalHitDamageMultiplier = criticalHitDamageMultiplier;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public LivingEntity getTarget() {
        return target;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public double getCriticalHitDamageMultiplier() {
        return criticalHitDamageMultiplier;
    }

    public CombatType getType() {
        return type;
    }

    public double getDamageBeforeCrit() {
        return damageBeforeCrit;
    }

    public void setCriticalHitDamageMultiplier(double criticalHitDamageMultiplier) {
        this.criticalHitDamageMultiplier = criticalHitDamageMultiplier;
    }

    public void setDamageBeforeCrit(double damageBeforeCrit) {
        this.damageBeforeCrit = damageBeforeCrit;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
