package me.athlaeos.valhallammo.items.attributewrappers;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class VanillaArmorToughnessWrapper extends AttributeWrapper{
    public VanillaArmorToughnessWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        super(amount, operation, slot);
        this.attribute = "GENERIC_ARMOR_TOUGHNESS";
        this.minValue = 0;
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
