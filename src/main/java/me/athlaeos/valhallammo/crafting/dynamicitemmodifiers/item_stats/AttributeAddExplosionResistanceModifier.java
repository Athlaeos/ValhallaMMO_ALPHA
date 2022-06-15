package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.items.attributewrappers.CustomExplosionResistanceWrapper;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class AttributeAddExplosionResistanceModifier extends DynamicItemModifier implements Cloneable{

    public AttributeAddExplosionResistanceModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_CUSTOM;

        this.bigStepDecrease = 0.1;
        this.bigStepIncrease = 0.1;
        this.smallStepDecrease = 0.01;
        this.smallStepIncrease = 0.01;
        this.defaultStrength = 1;
        this.minStrength = -1000D;
        this.maxStrength = 1000D;
        this.description = Utils.chat("&7Sets a default explosion resistance modifier to the item. Explosion resistance reduces " +
                "explosion damage taken. If negative, the target will take more explosion damage instead.");
        this.displayName = Utils.chat("&7&lAdd Stat: &e&lExplosion Resistance");
        this.icon = Material.TNT;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<amount>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemAttributesManager.getInstance().addDefaultStat(outputItem, new CustomExplosionResistanceWrapper(
                strength,
                AttributeModifier.Operation.ADD_SCALAR,
                EquipmentSlot.HAND
        ));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's default explosion resistance to &e%.1f%%.", (strength * 100)));
    }
}
