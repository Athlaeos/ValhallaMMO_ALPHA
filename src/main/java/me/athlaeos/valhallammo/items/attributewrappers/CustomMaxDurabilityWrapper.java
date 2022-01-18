package me.athlaeos.valhallammo.items.attributewrappers;

import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class CustomBowStrengthWrapper extends AttributeWrapper{
    public CustomBowStrengthWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        super(amount, operation, slot);
        this.attribute = "CUSTOM_DRAW_STRENGTH";
        this.minValue = 0;
        this.maxValue = 16;
    }

    @Override
    public boolean isCompatible(ItemStack i) {
        return i.getType() == Material.BOW || i.getType() == Material.CROSSBOW;
    }
}
