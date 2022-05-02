package me.athlaeos.valhallammo.dom;

import org.bukkit.Location;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public abstract class CustomArrowAttribute {

    private final String identifier;

    public CustomArrowAttribute(String identifier){
        this.identifier = identifier;
    }

    public abstract void onBowShoot(EntityShootBowEvent e, double... args);
    public abstract void onProjectileHit(ProjectileHitEvent e, double... args);
    public abstract void onProjectileLaunch(ProjectileLaunchEvent e, double... args);

    /**
     * Executed when an AbstractArrow damages an entity.
     * The shooter is asserted to be a LivingEntity
     */
    public abstract void onProjectileDamage(EntityDamageByEntityEvent e, double... args);
    public abstract void onArrowPickup(PlayerPickupArrowEvent e, double... args);

    public String getIdentifier() {
        return identifier;
    }

    protected Location getHitLocation(ProjectileHitEvent e){
        Location hit = e.getEntity().getLocation();
        if (e.getHitBlock() != null){
            if (e.getHitBlockFace() != null){
                hit = e.getHitBlock().getRelative(e.getHitBlockFace()).getLocation();
            } else {
                hit = e.getHitBlock().getLocation().add(0.5, 0.5, 0.5);
            }
        } else if (e.getHitEntity() != null){
            hit = e.getHitEntity().getLocation();
        }
        return hit;
    }
}
