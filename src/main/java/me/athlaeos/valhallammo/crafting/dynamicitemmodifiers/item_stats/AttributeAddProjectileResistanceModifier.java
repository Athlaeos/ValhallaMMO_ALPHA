package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.items.attributewrappers.CustomProjectileResistanceWrapper;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

public class AttributeAddProjectileResistanceModifier extends DynamicItemModifier implements Cloneable{

    public AttributeAddProjectileResistanceModifier(String name, double strength, ModifierPriority priority) {
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
        this.description = Utils.chat("&7Sets a default projectile resistance modifier to the item. Projectile resistance reduces " +
                "projectile damage taken, such as damage taken from arrows. If negative, the target will " +
                "take more projectile damage instead.");
        this.displayName = Utils.chat("&7&lAdd Stat: &e&lProjectile Resistance");
        this.icon = Material.SPECTRAL_ARROW;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemAttributesManager.getInstance().addDefaultStat(outputItem, new CustomProjectileResistanceWrapper(
                strength,
                AttributeModifier.Operation.ADD_SCALAR,
                EquipmentSlot.HAND
        ));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's default projectile resistance to &e%.1f.", (strength * 100)));
    }
}
