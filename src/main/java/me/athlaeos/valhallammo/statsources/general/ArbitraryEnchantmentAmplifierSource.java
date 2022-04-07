package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import me.athlaeos.valhallammo.utility.ItemUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class ArbitraryEnchantmentAmplifierSource extends AccumulativeStatSource {
    private final String enchantment;
    public ArbitraryEnchantmentAmplifierSource(String enchantment){
        this.enchantment = enchantment;
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof LivingEntity){
            return ItemUtils.combinedCustomEnchantAmplifier((LivingEntity) p, enchantment);
        }

        return 0;
    }
}
