package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import org.bukkit.entity.Entity;

public class ArbitraryOffensivePotionAmplifierSource extends EvEAccumulativeStatSource {
    private final String potionEffect;
    private final boolean negative;

    public ArbitraryOffensivePotionAmplifierSource(String potionEffect, boolean negative){
        this.potionEffect = potionEffect;
        this.negative = negative;
    }

    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        PotionEffect activePotionEffect = PotionEffectManager.getInstance().getPotionEffect(offender, potionEffect);
        if (activePotionEffect == null) {
            return 0;
        }
        return negative ? -activePotionEffect.getAmplifier() : activePotionEffect.getAmplifier();
    }
}
