package me.athlaeos.valhallammo.domain;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.util.UUID;

public abstract class Profile {
    protected NamespacedKey key;
    protected UUID owner;
    protected int level;
    protected double exp;
    protected double lifetimeEXP;

    public Profile(Player owner){
        if (owner == null) return;
        this.owner = owner.getUniqueId();
    }

    public abstract void setDefaultStats(Player player);

    public int getLevel() {
        return level;
    }

    public double getExp() {
        return exp;
    }

    public void setExp(double exp) {
        this.exp = exp;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public NamespacedKey getKey() {
        return key;
    }

    public UUID getOwner() {
        return owner;
    }

    public double getLifetimeEXP() {
        return lifetimeEXP;
    }

    public void setLifetimeEXP(double lifetimeEXP) {
        this.lifetimeEXP = lifetimeEXP;
    }
}
