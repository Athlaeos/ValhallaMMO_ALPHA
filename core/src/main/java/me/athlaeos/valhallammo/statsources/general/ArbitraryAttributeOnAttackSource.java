package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class ArbitraryAttributeOnAttackSource extends EvEAccumulativeStatSource {
    private final String stat;
    private final boolean negative;

    /**
     * Returns the extra amount of this stat based on an attack. If the offender is an AbstractArrow, the stat attribute
     * is fetched from the arrow and returned. If the offender is a LivingEntity, the stat attribute is fetched from the
     * LivingEntity's main hand item (if any) and returned.
     */
    public ArbitraryAttributeOnAttackSource(String stat, boolean negative){
        this.stat = stat;
        this.negative = negative;
    }

    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        if (offender instanceof AbstractArrow){
            ItemStack ammo = ItemUtils.getArrowFromEntity((AbstractArrow) offender);
            if (ammo == null) return 0;
            AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(ammo, stat);
            if (wrapper == null) return 0;
            return negative ? -wrapper.getAmount() : wrapper.getAmount();
        } else if (offender instanceof LivingEntity){
            EntityEquipment equipment = ((LivingEntity) offender).getEquipment();
            if (equipment == null) return 0;
            ItemStack weapon = equipment.getItemInMainHand();
            if (Utils.isItemEmptyOrNull(weapon)) return 0;
            AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(weapon, stat);
            if (wrapper == null) return 0;
            return negative ? -wrapper.getAmount() : wrapper.getAmount();
        }
        return 0;
    }
}
