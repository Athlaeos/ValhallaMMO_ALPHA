package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class BuffExpGainSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            PotionEffect activePotionEffect = PotionEffectManager.getInstance().getPotionEffect(p, "INCREASE_EXP");

            if (activePotionEffect == null) {
                return 0;
            }
            return activePotionEffect.getAmplifier();
        }

        return 0;
    }
}
