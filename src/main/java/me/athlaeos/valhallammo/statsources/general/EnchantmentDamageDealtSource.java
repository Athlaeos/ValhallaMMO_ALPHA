package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import me.athlaeos.valhallammo.utility.ItemUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class EnchantmentDamageDealtSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof LivingEntity){
            return ItemUtils.combinedCustomEnchantAmplifier((LivingEntity) p, "DAMAGE_DEALT");
        }

        return 0;
    }
}
