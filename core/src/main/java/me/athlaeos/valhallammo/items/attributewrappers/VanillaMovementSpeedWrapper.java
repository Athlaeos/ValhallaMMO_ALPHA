package me.athlaeos.valhallammo.items.attributewrappers;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VanillaMovementSpeedWrapper extends AttributeWrapper{
    public VanillaMovementSpeedWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        super(amount, operation, slot);
        this.attribute = "GENERIC_MOVEMENT_SPEED";
        this.minValue = -1024;
        this.maxValue = 1024;
    }

    @Override
    public boolean isCompatible(ItemStack i) {
        return true;
    }

    @Override
    public void onApply(ItemMeta i) {

    }

    @Override
    public void onRemove(ItemMeta i) {

    }
}
