package me.athlaeos.valhallammo.items.attributewrappers;

import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomMaxDurabilityWrapper extends AttributeWrapper{

    public CustomMaxDurabilityWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        super(amount, operation, slot);
        this.attribute = "CUSTOM_MAX_DURABILITY";
        this.minValue = 1;
        this.maxValue = Integer.MAX_VALUE;
        this.operation = null;
    }

    @Override
    public boolean isCompatible(ItemStack i) {
        if (i == null) return false;
        return i.getItemMeta() instanceof Damageable;
    }

    @Override
    public void onApply(ItemMeta i) {

    }

    @Override
    public void onRemove(ItemMeta i) {

    }
}
