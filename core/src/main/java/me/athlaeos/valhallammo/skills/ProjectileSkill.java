package me.athlaeos.valhallammo.skills;

import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;

public interface ProjectileSkill {
    void onProjectileLaunch(ProjectileLaunchEvent event);

    void onEntityShootBow(EntityShootBowEvent event);

    void onProjectileHit(ProjectileHitEvent event);

    void onArrowPickup(PlayerPickupArrowEvent event);
}
