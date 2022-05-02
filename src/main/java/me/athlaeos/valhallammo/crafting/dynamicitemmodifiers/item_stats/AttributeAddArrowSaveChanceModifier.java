package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.attributewrappers.CustomArrowSaveChanceWrapper;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class AttributeAddArrowSaveChanceModifier extends DynamicItemModifier implements Cloneable{

    public AttributeAddArrowSaveChanceModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS;

        this.bigStepDecrease = 0.1;
        this.bigStepIncrease = 0.1;
        this.smallStepDecrease = 0.01;
        this.smallStepIncrease = 0.01;
        this.defaultStrength = 0;
        this.minStrength = Short.MIN_VALUE;
        this.maxStrength = Short.MAX_VALUE;
        this.description = Utils.chat("&7Sets a default arrow save chance to the item. The strength of the modifier " +
                "represents the extra % chance the arrow will not be consumed when fired. The bigger " +
                "the value, the likelier the arrow won't be consumed. If the value is negative, the arrow will more likely be consumed.");
        this.displayName = Utils.chat("&7&lAdd Stat: &e&lArrow Save Chance");
        this.icon = Material.SPECTRAL_ARROW;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<save_chance>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemAttributesManager.getInstance().addDefaultStat(outputItem, new CustomArrowSaveChanceWrapper(
                strength,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND
        ));
        ItemAttributesManager.getInstance().setAttributeStrength(outputItem, "CUSTOM_ARROW_SAVE_CHANCE", strength);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the arrow's save chance to &e" + (strength >= 0 ? "+" : "") + "%.1f%%.", strength * 100));
    }
}
