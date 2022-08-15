package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import me.athlaeos.valhallammo.utility.ItemUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class ArbitraryEnchantmentAmplifierSource extends AccumulativeStatSource {
    private final String enchantment;
    private boolean negative = false;
    public ArbitraryEnchantmentAmplifierSource(String enchantment){
        this.enchantment = enchantment;
    }

    public ArbitraryEnchantmentAmplifierSource(String enchantment, boolean negative){
        this.enchantment = enchantment;
        this.negative = negative;
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof LivingEntity){
            double value = negative ? -ItemUtils.combinedCustomEnchantAmplifier((LivingEntity) p, enchantment) : ItemUtils.combinedCustomEnchantAmplifier((LivingEntity) p, enchantment);
            return value;
        }

        return 0;
    }
}
