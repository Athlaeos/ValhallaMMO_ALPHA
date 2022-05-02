package me.athlaeos.valhallammo.items.customarrowattributes;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.CustomArrowAttribute;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class NoGravityArrow extends CustomArrowAttribute {
    /**
     * Removes the gravity off the projectile, causing it to fly straight
     */
    public NoGravityArrow(String identifier) {
        super(identifier);
    }

    @Override
    public void onBowShoot(EntityShootBowEvent e, double... args) {
        e.getProjectile().setGravity(false);
        new BukkitRunnable(){
            @Override
            public void run() {
                e.getProjectile().remove();
            }
        }.runTaskLater(ValhallaMMO.getPlugin(), 200);
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
