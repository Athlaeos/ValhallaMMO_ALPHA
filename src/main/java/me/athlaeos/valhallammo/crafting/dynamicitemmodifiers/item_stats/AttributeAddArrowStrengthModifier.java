package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.attributewrappers.CustomArrowDamageWrapper;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class AttributeAddArrowStrengthModifier extends DynamicItemModifier implements Cloneable{

    public AttributeAddArrowStrengthModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS;

        this.bigStepDecrease = 1;
        this.bigStepIncrease = 1;
        this.smallStepDecrease = 0.1;
        this.smallStepIncrease = 0.1;
        this.defaultStrength = 1;
        this.minStrength = 0;
        this.maxStrength = Short.MAX_VALUE;
        this.description = Utils.chat("&7Sets a default custom arrow strength to the item. The strength of the modifier " +
                "represents the default damage of the arrow. Arrow damage paired with the bow's draw strength " +
                "can greatly increase damage potential, so it should be used sparingly");
        this.displayName = Utils.chat("&7&lAdd Stat: &e&lArrow Strength");
        this.icon = Material.ARROW;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemAttributesManager.getInstance().addDefaultStat(outputItem, new CustomArrowDamageWrapper(
                strength,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND
        ));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the arrow's default damage to &e%.1f.", strength));
    }
}
