package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers;

import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.items.attributewrappers.CustomDrawStrengthWrapper;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class AttributeAddDrawStrengthModifier extends DynamicItemModifier implements Cloneable{

    public AttributeAddDrawStrengthModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;

        this.bigStepDecrease = 0.1;
        this.bigStepIncrease = 0.1;
        this.smallStepDecrease = 0.01;
        this.smallStepIncrease = 0.01;
        this.defaultStrength = 1;
        this.minStrength = 0;
        this.maxStrength = 1000D;
        this.description = Utils.chat("&7Sets a default custom draw strength to the item. The strength of the modifier " +
                "represents the default draw strength of the item. Draw strength directly influences the velocity" +
                " of arrows shot from these items. This only works on bows or crossbows.");
        this.displayName = Utils.chat("&7&lAdd Stat: &e&lDraw Strength");
        this.icon = Material.CROSSBOW;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemAttributesManager.getInstance().addDefaultStat(outputItem, new CustomDrawStrengthWrapper(
                strength,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND
        ));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's default draw strength to &e%.2f.", strength));
    }
}
