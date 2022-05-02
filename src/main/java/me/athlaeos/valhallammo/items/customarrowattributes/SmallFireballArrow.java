package me.athlaeos.valhallammo.items.customarrowattributes;

import me.athlaeos.valhallammo.dom.CustomArrowAttribute;
import me.athlaeos.valhallammo.skills.archery.ArcherySkill;
import org.bukkit.Particle;
import org.bukkit.entity.SmallFireball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class SmallFireballArrow extends CustomArrowAttribute {
    /**
     * Replaces the arrow with a small fireball
     * The first double argument determines the radius of explosion
     * The second double argument determines if the explosion should light terrain on fire
     * If there are not enough arguments, the fireball does not occur
     */
    public SmallFireballArrow(String identifier) {
        super(identifier);
    }

    @Override
    public void onBowShoot(EntityShootBowEvent e, double... args) {
        if (args.length == 2){
            float radius = (float) args[0];
            boolean isIncendiary = ((int) args[1]) == 1;
            SmallFireball smallFireball = e.getEntity().launchProjectile(SmallFireball.class, e.getProjectile().getVelocity());
            smallFireball.setShooter(e.getEntity());
            smallFireball.setYield(radius);
            smallFireball.setIsIncendiary(isIncendiary);
            ArcherySkill.trailProjectile(smallFireball, Particle.FLAME);
            e.setProjectile(smallFireball);
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
