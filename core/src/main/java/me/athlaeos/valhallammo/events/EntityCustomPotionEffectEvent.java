package me.athlaeos.valhallammo.events;

import me.athlaeos.valhallammo.dom.PotionEffect;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public class EntityCustomPotionEffectEvent extends EntityEvent implements Cancellable {

    private static final HandlerList handlers = new HandlerList();
    private boolean cancel;
    private final PotionEffect oldEffect;
    private final PotionEffect newEffect;
    private final EntityPotionEffectEvent.Cause cause;
    private final EntityPotionEffectEvent.Action action;
    private boolean override;

    public EntityCustomPotionEffectEvent(LivingEntity livingEntity, PotionEffect oldEffect, PotionEffect newEffect, EntityPotionEffectEvent.Cause cause, EntityPotionEffectEvent.Action action, boolean override) {
        super(livingEntity);
        this.oldEffect = oldEffect;
        this.newEffect = newEffect;
        this.cause = cause;
        this.action = action;
        this.override = override;
    }

    /**
     * Gets the old potion effect of the changed type, which will be removed.
     *
     * @return The old potion effect or null if the entity did not have the
     * changed effect type.
     */
    public PotionEffect getOldEffect() {
        return oldEffect;
    }

    /**
     * Gets new potion effect of the changed type to be applied.
     *
     * @return The new potion effect or null if the effect of the changed type
     * will be removed.
     */
    public PotionEffect getNewEffect() {
        return newEffect;
    }

    /**
     * Gets the cause why the effect has changed.
     *
     * @return A Cause value why the effect has changed.
     */
    
    public EntityPotionEffectEvent.Cause getCause() {
        return cause;
    }

    /**
     * Gets the action which will be performed on the potion effect type.
     *
     * @return An action to be performed on the potion effect type.
     */
    
    public EntityPotionEffectEvent.Action getAction() {
        return action;
    }

    /**
     * Gets the modified potion effect type.
     *
     * @return The effect type which will be modified on the entity.
     */
    
    public String getModifiedType() {
        return (oldEffect == null) ? ((newEffect == null) ? null : newEffect.getName()) : oldEffect.getName();
    }

    /**
     * Returns if the new potion effect will override the old potion effect
     * (Only applicable for the CHANGED Action).
     *
     * @return If the new effect will override the old one.
     */
    public boolean isOverride() {
        return override;
    }

    /**
     * Sets if the new potion effect will override the old potion effect (Only
     * applicable for the CHANGED action).
     *
     * @param override If the new effect will override the old one.
     */
    public void setOverride(boolean override) {
        this.override = override;
    }

    @Override
    public boolean isCancelled() {
        return cancel;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancel = cancel;
    }
    
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
