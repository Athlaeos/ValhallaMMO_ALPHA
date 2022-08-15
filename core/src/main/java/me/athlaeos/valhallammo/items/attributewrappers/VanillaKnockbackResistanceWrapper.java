package me.athlaeos.valhallammo.items.attributewrappers;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VanillaKnockbackResistanceWrapper extends AttributeWrapper{
    public VanillaKnockbackResistanceWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        super(amount, operation, slot);
        this.attribute = "GENERIC_KNOCKBACK_RESISTANCE";
        this.minValue = 0;
        this.maxValue = 1;
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
