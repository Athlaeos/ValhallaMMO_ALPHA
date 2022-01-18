package me.athlaeos.valhallammo.items.enchantmentwrappers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class EnchantmentWrapper implements Cloneable{
    protected double minValue;
    protected double maxValue;
    protected String attribute;
    protected double amount;

    public EnchantmentWrapper(double amount){
        this.amount = amount;
    }

    public abstract boolean isCompatible(ItemStack i);

    public abstract void onApply(ItemMeta i);

    public abstract void onRemove(ItemMeta i);

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getAttribute() {
        return attribute;
    }

    @Override
    public EnchantmentWrapper clone() throws CloneNotSupportedException {
        return (EnchantmentWrapper) super.clone();
    }
}
