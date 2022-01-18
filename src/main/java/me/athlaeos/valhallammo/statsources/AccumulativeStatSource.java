package me.athlaeos.valhallammo.crafting.statsources;

import org.bukkit.entity.Player;

public abstract class AccumulativeStatSource {
    public abstract double add(Player p, boolean use);
}
