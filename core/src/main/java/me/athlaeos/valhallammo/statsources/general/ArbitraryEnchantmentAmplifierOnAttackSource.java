package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import me.athlaeos.valhallammo.utility.ItemUtils;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class ArbitraryEnchantmentAmplifierOnAttackSource extends EvEAccumulativeStatSource {
    private final String enchantment;
    private boolean negative = false;
    public ArbitraryEnchantmentAmplifierOnAttackSource(String enchantment){
        this.enchantment = enchantment;
    }

    public ArbitraryEnchantmentAmplifierOnAttackSource(String enchantment, boolean negative){
        this.enchantment = enchantment;
        this.negative = negative;
    }

    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        if (offender instanceof AbstractArrow && ((AbstractArrow) offender).getShooter() instanceof Entity){
            offender = (Entity) ((AbstractArrow) offender).getShooter();
        }
        if (offender instanceof LivingEntity){
            double value = negative ? -ItemUtils.combinedCustomEnchantAmplifier((LivingEntity) offender, enchantment) : ItemUtils.combinedCustomEnchantAmplifier((LivingEntity) offender, enchantment);
            return value;
        }
        return 0;
    }
}
