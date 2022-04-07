package me.athlaeos.valhallammo.skills;

import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.ExplosionPrimeEvent;

public interface ExplosionSkill {
    void onEntityExplode(EntityExplodeEvent event);

    void onExplosionPrime(ExplosionPrimeEvent event);

    void onBlockExplode(BlockExplodeEvent event);
}
