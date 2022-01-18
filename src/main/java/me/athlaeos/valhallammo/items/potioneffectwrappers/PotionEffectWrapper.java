package me.athlaeos.valhallammo.items.attributewrappers;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class AttributeWrapper implements Cloneable{
    protected double minValue;
    protected double maxValue;
    protected String attribute;
    protected double amount;
    protected AttributeModifier.Operation operation;
    protected EquipmentSlot slot;

    public AttributeWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot){
        this.amount = amount;
        this.operation = operation;
        this.slot = slot;
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

    public AttributeModifier.Operation getOperation() {
        return operation;
    }

    public EquipmentSlot getSlot() {
        return slot;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setSlot(EquipmentSlot slot) {
        this.slot = slot;
    }

    public void setOperation(AttributeModifier.Operation operation) {
        this.operation = operation;
    }

    public String getAttribute() {
        return attribute;
    }

    @Override
    public AttributeWrapper clone() throws CloneNotSupportedException {
        return (AttributeWrapper) super.clone();
    }
}
