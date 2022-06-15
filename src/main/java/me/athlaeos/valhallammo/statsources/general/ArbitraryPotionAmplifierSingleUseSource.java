package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public class ArbitraryPotionAmplifierSingleUseSource extends AccumulativeStatSource {
    private final String potionEffect;
    private final boolean negative;
    public ArbitraryPotionAmplifierSingleUseSource(String potionEffect, boolean negative){
        this.potionEffect = potionEffect;
        this.negative = negative;
    }

    @Override
    public double add(Entity p, boolean use) {
        PotionEffect effect = PotionEffectManager.getInstance().getPotionEffect(p, potionEffect);
        if (effect != null && p instanceof LivingEntity){
            if (use){
                effect.setEffectiveUntil(0);
                PotionEffectManager.getInstance().addPotionEffect((LivingEntity) p, effect, true, EntityPotionEffectEvent.Cause.PLUGIN, EntityPotionEffectEvent.Action.REMOVED);
            }
            return negative ? -effect.getAmplifier() : effect.getAmplifier();
        }
        return 0;
    }
}
