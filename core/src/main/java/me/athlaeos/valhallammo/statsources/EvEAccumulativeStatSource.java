package me.athlaeos.valhallammo.statsources;

import org.bukkit.entity.Entity;

public abstract class EvEAccumulativeStatSource extends AccumulativeStatSource{
    public abstract double add(Entity entity, Entity offender, boolean use);
}
