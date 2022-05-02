package me.athlaeos.valhallammo.items.customarrowattributes;

import me.athlaeos.valhallammo.dom.CustomArrowAttribute;
import me.athlaeos.valhallammo.skills.archery.ArcherySkill;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class ExplodingArrow extends CustomArrowAttribute {
    /**
     * Causes an explosion at the spot of impact.
     * The first double argument determines the size of the explosion
     * The second double argument determines if it should damage terrain or not (0 = no, 1 = yes)
     * The third double argument determines if it should light terrain on fire or not (0 = no, 1 = yes)
     * If there are not enough arguments, the explosion does not occur
     */
    public ExplodingArrow(String identifier) {
        super(identifier);
    }

    @Override
    public void onBowShoot(EntityShootBowEvent e, double... args) {
        if (e.getProjectile() instanceof Projectile){
            ArcherySkill.trailProjectile((Projectile) e.getProjectile(), Particle.FLAME);
        }
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent e, double... args) {
        if (args.length == 3){
            boolean damageTerrain = ((int) args[1]) == 1;
            boolean setFire = ((int) args[2]) == 1;
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
            if (e.getEntity().getShooter() instanceof Entity){
                e.getEntity().getWorld().createExplosion(hit, (float) args[0], setFire, damageTerrain, (Entity) e.getEntity().getShooter());
            } else {
                e.getEntity().getWorld().createExplosion(hit, (float) args[0], setFire, damageTerrain, e.getEntity());
            }
            e.getEntity().remove();
        }
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e, double... args) {

    }

    @Override
    public void onProjectileDamage(EntityDamageByEntityEvent e, double... args) {

    }

    @Override
    public void onArrowPickup(PlayerPickupArrowEvent e, double... args) {

    }
}
