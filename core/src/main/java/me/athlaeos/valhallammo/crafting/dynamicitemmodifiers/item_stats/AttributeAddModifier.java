package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class AttributeAddModifier extends DynamicItemModifier implements Cloneable{
    private final String attribute;
    private final String prettyAttribute;

    public AttributeAddModifier(String name, String attribute, Material icon, double def, double min, double max, double bigStep, double smallStep) {
        super(name, 0, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_CUSTOM;
        this.attribute = attribute;
        prettyAttribute = Utils.toPascalCase(attribute.replace("_", " "));

        this.bigStepDecrease = bigStep;
        this.bigStepIncrease = bigStep;
        this.smallStepDecrease = smallStep;
        this.smallStepIncrease = smallStep;
        this.defaultStrength = def;
        this.minStrength = min;
        this.maxStrength = max;
        this.description = Utils.chat("&7Sets a default " + prettyAttribute + " modifier to the item. ");
        this.displayName = Utils.chat("&7&lAdd Stat: &e&l" + prettyAttribute);
        this.icon = icon;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<amount>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        AttributeWrapper attributeWrapper = ItemAttributesManager.getInstance().getWrapperClone(attribute);
        if (attributeWrapper != null){
            attributeWrapper.setAmount(strength);
            ItemAttributesManager.getInstance().addDefaultStat(outputItem, attributeWrapper);
        }
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's default " + prettyAttribute + " to &e%.3f%%.", strength));
    }
}
