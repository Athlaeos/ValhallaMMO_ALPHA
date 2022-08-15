package me.athlaeos.valhallammo.items.customarrowattributes;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.CustomArrowAttribute;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.scheduler.BukkitRunnable;

public class RemoveImmunityFramesArrow extends CustomArrowAttribute {
    /**
     * Removes the hit entity's immunity frames when hit
     * No arguments are needed for this to work
     */
    public RemoveImmunityFramesArrow(String identifier) {
        super(identifier);
    }

    @Override
    public void onBowShoot(EntityShootBowEvent e, double... args) {

    }

    @Override
    public void onProjectileHit(ProjectileHitEvent e, double... args) {

    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e, double... args) {

    }

    @Override
    public void onProjectileDamage(EntityDamageByEntityEvent e, double... args) {
        if (e.getEntity() instanceof LivingEntity){
            new BukkitRunnable(){
                @Override
                public void run() {
                    if (((LivingEntity) e.getEntity()).getNoDamageTicks() > 0){
                        ((LivingEntity) e.getEntity()).setNoDamageTicks(0);
                    }
                }
            }.runTaskLater(ValhallaMMO.getPlugin(), 1L);
        }
    }

    @Override
    public void onArrowPickup(PlayerPickupArrowEvent e, double... args) {

    }
}
