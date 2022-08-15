package me.athlaeos.valhallammo.items.customarrowattributes;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.CustomArrowAttribute;
import me.athlaeos.valhallammo.skills.archery.ArcherySkill;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public class IncendiaryArrow extends CustomArrowAttribute {
    /**
     * Causes blocks and entities in the area to be lit on fire
     * The first double argument determines how long an entity should be lit on fire when hit (in ticks)
     * The second double argument determines the radius of fire
     * The third double argument determines the density of fire on blocks
     * If there are not enough arguments, no fire occurs
     */
    public IncendiaryArrow(String identifier) {
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
        if (args.length >= 2){
            int fireTicks = (int) args[0];
            int radius = (int) args[1];
            double density = 1;
            if (args.length == 3){
                density = args[2];
            }
            Location hit = e.getEntity().getLocation();
            if (hit.getWorld() == null) return;

            for (Block b : Utils.getBlocksTouchingAnything(hit.getBlock(), radius, radius, radius)){
                if (!b.getType().isAir()) continue;
                if (Utils.getRandom().nextDouble() < density){
                    BlockIgniteEvent igniteEvent = new BlockIgniteEvent(b, BlockIgniteEvent.IgniteCause.ARROW, e.getEntity());
                    ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(igniteEvent);
                    if (!igniteEvent.isCancelled()) {
                        b.setType(Material.FIRE);
                    }
                }
            }
            for (Entity entity : hit.getWorld().getNearbyEntities(hit, radius, radius, radius)){
                if (entity instanceof LivingEntity){
                    if (entity.getFireTicks() < fireTicks){
                        entity.setFireTicks(fireTicks);
                    }
                }
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
