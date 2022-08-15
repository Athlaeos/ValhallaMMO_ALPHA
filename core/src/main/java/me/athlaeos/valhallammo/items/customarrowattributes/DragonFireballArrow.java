package me.athlaeos.valhallammo.items.customarrowattributes;

import me.athlaeos.valhallammo.dom.CustomArrowAttribute;
import me.athlaeos.valhallammo.skills.archery.ArcherySkill;
import org.bukkit.Particle;
import org.bukkit.entity.DragonFireball;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class DragonFireballArrow extends CustomArrowAttribute {
    /**
     * Replaces the arrow with a dragon fireball
     * The first double argument determines the radius of explosion/toxicity
     * The second double argument determines if the explosion should leave toxic damage stuff
     * If there are not enough arguments, the fireball does not occur
     */
    public DragonFireballArrow(String identifier) {
        super(identifier);
    }

    @Override
    public void onBowShoot(EntityShootBowEvent e, double... args) {
        if (args.length == 2){
            float radius = (float) args[0];
            boolean isIncendiary = ((int) args[1]) == 1;
            DragonFireball dragonFireball = e.getEntity().launchProjectile(DragonFireball.class, e.getProjectile().getVelocity());
            dragonFireball.setShooter(e.getEntity());
            dragonFireball.setYield(radius);
            dragonFireball.setIsIncendiary(isIncendiary);
            ArcherySkill.trailProjectile(dragonFireball, Particle.DRAGON_BREATH);
            e.setProjectile(dragonFireball);
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
