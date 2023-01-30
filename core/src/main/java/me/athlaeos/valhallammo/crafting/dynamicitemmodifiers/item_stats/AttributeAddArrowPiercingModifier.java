package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.attributewrappers.CustomArrowPiercingWrapper;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class AttributeAddArrowPiercingModifier extends DynamicItemModifier {

    public AttributeAddArrowPiercingModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_CUSTOM;

        this.bigStepDecrease = 3;
        this.bigStepIncrease = 3;
        this.smallStepDecrease = 1;
        this.smallStepIncrease = 1;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = Short.MAX_VALUE;
        this.description = Utils.chat("&7Sets a piercing level to the item. The strength of the modifier " +
                "represents the amount of targets the arrow can pierce through. The bigger " +
                "the value, the more targets an arrow can hit.");
        this.displayName = Utils.chat("&7&lAdd Stat: &e&lArrow Piercing");
        this.icon = Material.SPECTRAL_ARROW;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<amount>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        ItemAttributesManager.getInstance().addDefaultStat(outputItem, new CustomArrowPiercingWrapper(
                (int) strength,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND
        ));
        ItemAttributesManager.getInstance().setAttributeStrength(outputItem, "CUSTOM_ARROW_PIERCING", strength);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the arrow's piercing to &e%d.", (int) strength));
    }
}
