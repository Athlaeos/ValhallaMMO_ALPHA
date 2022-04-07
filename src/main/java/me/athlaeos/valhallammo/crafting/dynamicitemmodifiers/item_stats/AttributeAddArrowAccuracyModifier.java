package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.attributewrappers.CustomArrowAccuracyWrapper;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class AttributeAddArrowAccuracyModifier extends DynamicItemModifier implements Cloneable{

    public AttributeAddArrowAccuracyModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS;

        this.bigStepDecrease = 1;
        this.bigStepIncrease = 1;
        this.smallStepDecrease = 0.1;
        this.smallStepIncrease = 0.1;
        this.defaultStrength = 1;
        this.minStrength = Short.MIN_VALUE;
        this.maxStrength = Short.MAX_VALUE;
        this.description = Utils.chat("&7Sets a default custom arrow inaccuracy to the item. The strength of the modifier " +
                "represents the change in inaccuracy the arrow will be shot at. The bigger the value, the less accurate." +
                " If the value is negative, the arrow will be shot more accurately.");
        this.displayName = Utils.chat("&7&lAdd Stat: &e&lArrow Inaccuracy");
        this.icon = Material.SPECTRAL_ARROW;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemAttributesManager.getInstance().addDefaultStat(outputItem, new CustomArrowAccuracyWrapper(
                strength,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND
        ));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the arrow's default accuracy to &e%.1f.", -strength));
    }
}
