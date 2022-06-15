package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;

public class ArbitraryPotionAmplifierSource extends AccumulativeStatSource {
    private final String potionEffect;
    private final boolean negative;

    public ArbitraryPotionAmplifierSource(String potionEffect, boolean negative){
        this.potionEffect = potionEffect;
        this.negative = negative;
    }

    @Override
    public double add(Entity p, boolean use) {
        PotionEffect activePotionEffect = PotionEffectManager.getInstance().getPotionEffect(p, potionEffect);
        if (activePotionEffect == null) {
            return 0;
        }
        return negative ? -activePotionEffect.getAmplifier() : activePotionEffect.getAmplifier();
    }
}
