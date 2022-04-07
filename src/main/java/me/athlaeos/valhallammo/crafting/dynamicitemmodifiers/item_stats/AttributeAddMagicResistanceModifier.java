package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.items.attributewrappers.CustomMagicResistanceWrapper;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class AttributeAddMagicResistanceModifier extends DynamicItemModifier implements Cloneable{

    public AttributeAddMagicResistanceModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS;

        this.bigStepDecrease = 0.1;
        this.bigStepIncrease = 0.1;
        this.smallStepDecrease = 0.01;
        this.smallStepIncrease = 0.01;
        this.defaultStrength = 1;
        this.minStrength = -1000D;
        this.maxStrength = 1000D;
        this.description = Utils.chat("&7Sets a default magic resistance modifier to the item. Magic resistance reduces " +
                "magic damage taken, such as damage taken from splash harming potions, dragon's breath, lightning, and thorns. " +
                "If negative, the target will take more projectile damage instead.");
        this.displayName = Utils.chat("&7&lAdd Stat: &e&lMagic Resistance");
        this.icon = Material.SPLASH_POTION;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemAttributesManager.getInstance().addDefaultStat(outputItem, new CustomMagicResistanceWrapper(
                strength,
                AttributeModifier.Operation.ADD_SCALAR,
                EquipmentSlot.HAND
        ));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's default magic resistance to &e%.1f.", (strength * 100)));
    }
}
