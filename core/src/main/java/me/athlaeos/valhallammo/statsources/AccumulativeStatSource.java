package me.athlaeos.valhallammo.statsources;

import org.bukkit.entity.Entity;

public abstract class AccumulativeStatSource {
    public abstract double add(Entity p, boolean use);
}
