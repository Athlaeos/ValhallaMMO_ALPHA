package me.athlaeos.valhallammo.items.customarrowattributes;

import me.athlaeos.valhallammo.dom.CustomArrowAttribute;
import me.athlaeos.valhallammo.skills.archery.ArcherySkill;
import org.bukkit.Particle;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class LightningArrow extends CustomArrowAttribute {
    /**
     * Causes a lightning strike at the spot of impact.
     * The first double argument determines if the strike should only occur during rain (0 = no, 1 = yes)
     * If there are not enough arguments, the lightning strike does not occur
     */
    public LightningArrow(String identifier) {
        super(identifier);
    }

    @Override
    public void onBowShoot(EntityShootBowEvent e, double... args) {
        if (e.getProjectile() instanceof Projectile){
            ArcherySkill.trailProjectile((Projectile) e.getProjectile(), Particle.SOUL_FIRE_FLAME);
        }
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent e, double... args) {
        if (args.length == 1){
            boolean requiresRain = ((int) args[0]) == 1;
            if (requiresRain) {
                if (!(e.getEntity().getWorld().isThundering() || e.getEntity().getWorld().hasStorm())) return;
            }
            e.getEntity().getWorld().strikeLightning(getHitLocation(e));
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
