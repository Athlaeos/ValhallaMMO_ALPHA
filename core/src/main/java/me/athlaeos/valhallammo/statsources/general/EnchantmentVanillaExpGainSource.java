package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import me.athlaeos.valhallammo.utility.ItemUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class EnchantmentVanillaExpGainSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof LivingEntity){
            return ItemUtils.combinedCustomEnchantAmplifier((LivingEntity) p, "EXP_GAIN_VANILLA");
        }

        return 0;
    }
}
