package me.athlaeos.valhallammo.items.potioneffectwrappers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class PotionEffectWrapper implements Cloneable{
    protected String potionEffect;
    protected double amplifier;
    protected int duration;

    public PotionEffectWrapper(String potionEffect, double amplifier, int duration){
        this.amplifier = amplifier;
        this.potionEffect = potionEffect;
        this.duration = duration;
    }

    public abstract void onApply(ItemMeta potion);

    public abstract void onRemove(ItemMeta potion);

    public abstract boolean isCompatible(ItemStack i);

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public double getAmplifier() {
        return amplifier;
    }

    public void setAmplifier(double amplifier) {
        this.amplifier = amplifier;
    }

    public String getPotionEffect() {
        return potionEffect;
    }

    public void setPotionEffect(String potionEffect) {
        this.potionEffect = potionEffect;
    }

    @Override
    public PotionEffectWrapper clone() throws CloneNotSupportedException {
        return (PotionEffectWrapper) super.clone();
    }
}
