package me.athlaeos.valhallammo.items.customarrowattributes;

import me.athlaeos.valhallammo.dom.CustomArrowAttribute;
import me.athlaeos.valhallammo.skills.archery.ArcherySkill;
import org.bukkit.Particle;
import org.bukkit.entity.LargeFireball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class LargeFireballArrow extends CustomArrowAttribute {
    /**
     * Replaces the arrow with a large fireball
     * The first double argument determines the radius of explosion
     * The second double argument determines if the explosion should light terrain on fire
     * If there are not enough arguments, the fireball does not occur
     */
    public LargeFireballArrow(String identifier) {
        super(identifier);
    }

    @Override
    public void onBowShoot(EntityShootBowEvent e, double... args) {
        if (args.length == 2){
            float radius = (float) args[0];
            boolean isIncendiary = ((int) args[1]) == 1;
            LargeFireball largeFireball = e.getEntity().launchProjectile(LargeFireball.class, e.getProjectile().getVelocity());
            largeFireball.setShooter(e.getEntity());
            largeFireball.setYield(radius);
            largeFireball.setIsIncendiary(isIncendiary);
            ArcherySkill.trailProjectile(largeFireball, Particle.FLAME);
            e.setProjectile(largeFireball);
        }
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent e, double... args) {

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
