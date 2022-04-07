package me.athlaeos.valhallammo.items.attributewrappers;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VanillaAttackKnockbackWrapper extends AttributeWrapper{
    public VanillaAttackKnockbackWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        super(amount, operation, slot);
        this.attribute = "GENERIC_ATTACK_KNOCKBACK";
        this.minValue = 0;
        this.maxValue = 5;
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
