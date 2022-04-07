package me.athlaeos.valhallammo.dom;

import me.athlaeos.valhallammo.items.PotionType;

public class PotionEffect {
    private final String name;
    private long effectiveUntil;
    private double amplifier;
    private final PotionType type;
    private boolean removable = true;

    public PotionEffect(String name, long duration, double amplifier, PotionType type) {
        this.name = name;
        this.effectiveUntil = duration;
        this.amplifier = amplifier;
        this.type = type;
    }

    public PotionEffect(String name, long duration, double amplifier, PotionType type, boolean removable) {
        this.name = name;
        this.effectiveUntil = duration;
        this.amplifier = amplifier;
        this.type = type;
        this.removable = removable;
    }

    public boolean isRemovable() {
        return removable;
    }

    public String getName() {
        return name;
    }

    public double getAmplifier() {
        return amplifier;
    }

    public long getEffectiveUntil() {
        return effectiveUntil;
    }

    public void setAmplifier(double amplifier) {
        this.amplifier = amplifier;
    }

    public PotionType getType() {
        return type;
    }

    public void setEffectiveUntil(long effectiveUntil) {
        this.effectiveUntil = effectiveUntil;
    }
}
