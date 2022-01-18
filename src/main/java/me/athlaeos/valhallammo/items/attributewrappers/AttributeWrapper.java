package me.athlaeos.valhallammo.dom;

import org.bukkit.inventory.ItemStack;

public abstract class CustomAttribute {
    protected double minValue;
    protected double maxValue;
    protected String name;

    public abstract boolean isCompatible(ItemStack i);

    public abstract double getDefaultValue(ItemStack i);
}
