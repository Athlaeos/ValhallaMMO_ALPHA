package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.dom.ArmorType;
import me.athlaeos.valhallammo.dom.EntityProperties;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.EntityEquipmentCacheManager;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.inventory.EntityEquipment;
import org.bukkit.inventory.ItemStack;

public class EquipmentAttributeArmorDamageBonusSource extends EvEAccumulativeStatSource {
    private final ArmorType type;
    public EquipmentAttributeArmorDamageBonusSource(ArmorType type){
        this.type = type;
    }

    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity p, Entity e, boolean use) {
        if (type == ArmorType.WEIGHTLESS) return 0;
        if (p instanceof LivingEntity){
            EntityProperties entityProperties = EntityEquipmentCacheManager.getInstance().getAndCacheEquipment((LivingEntity) p);
            int armorCount = type == ArmorType.LIGHT ? entityProperties.getLightArmorCount() : entityProperties.getHeavyArmorCount();
            if (armorCount == 0) return 0;
            if (e instanceof AbstractArrow){
                ItemStack ammo = ItemUtils.getArrowFromEntity((AbstractArrow) e);
                if (ammo == null) return 0;
                AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(ammo, "CUSTOM_" + type.toString() + "_ARMOR_DAMAGE");
                if (wrapper == null) return 0;
                return wrapper.getAmount() * armorCount;
            } else if (e instanceof LivingEntity){
                EntityEquipment equipment = ((LivingEntity) e).getEquipment();
                if (equipment == null) return 0;
                ItemStack weapon = equipment.getItemInMainHand();
                if (Utils.isItemEmptyOrNull(weapon)) return 0;
                AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(weapon, "CUSTOM_" + type.toString() + "_ARMOR_DAMAGE");
                if (wrapper == null) return 0;
                return wrapper.getAmount() * armorCount;
            }
        }
        return 0;
    }
}
