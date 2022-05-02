package me.athlaeos.valhallammo.statsources;

import org.bukkit.entity.Entity;

public abstract class EvEAccumulativeStatSource {
    public abstract double add(Entity p, Entity e, boolean use);
}
