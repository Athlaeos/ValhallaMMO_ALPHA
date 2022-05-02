package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class EntityAttributeArmorToughnessSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof LivingEntity){
            AttributeInstance instance = ((LivingEntity) p).getAttribute(Attribute.GENERIC_ARMOR_TOUGHNESS);
            if (instance != null){
                return instance.getBaseValue();
            }
        }
        return 0;
    }
}
