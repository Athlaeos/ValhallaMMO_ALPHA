package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import me.athlaeos.valhallammo.utility.ItemUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class ArbitraryOffensiveEnchantmentAmplifierSource extends EvEAccumulativeStatSource {
    private final String enchantment;
    public ArbitraryOffensiveEnchantmentAmplifierSource(String enchantment){
        this.enchantment = enchantment;
    }

    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        if (offender instanceof LivingEntity){
            return ItemUtils.combinedCustomEnchantAmplifier((LivingEntity) offender, enchantment);
        }
        return 0;
    }
}
