package me.athlaeos.valhallammo.items.customarrowattributes;

import me.athlaeos.valhallammo.dom.CustomArrowAttribute;
import me.athlaeos.valhallammo.skills.archery.ArcherySkill;
import org.bukkit.Particle;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class EnderArrow extends CustomArrowAttribute {
    /**
     * Summons an ender pearl when the arrow lands at the location where it landed
     */
    public EnderArrow(String identifier) {
        super(identifier);
    }

    @Override
    public void onBowShoot(EntityShootBowEvent e, double... args) {
        if (e.getProjectile() instanceof Projectile){
            ArcherySkill.trailProjectile((Projectile) e.getProjectile(), Particle.PORTAL);
        }
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent e, double... args) {
        if (e.getEntity().getShooter() != null){
            if (e.getEntity().getShooter() instanceof LivingEntity){
                EnderPearl pearl = e.getEntity().getShooter().launchProjectile(EnderPearl.class, e.getEntity().getVelocity().normalize());
                pearl.setShooter(e.getEntity().getShooter());
                pearl.teleport(e.getEntity().getLocation());
                e.getEntity().remove();
            }
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
