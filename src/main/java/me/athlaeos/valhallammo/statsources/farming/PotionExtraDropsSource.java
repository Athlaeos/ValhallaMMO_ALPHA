package me.athlaeos.valhallammo.statsources.farming;

import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class PotionExtraDropsSource extends EvEAccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        if (offender instanceof LivingEntity){
            PotionEffect activePotionEffect = PotionEffectManager.getInstance().getPotionEffect(offender, "ENTITY_EXTRA_DROPS");
            if (activePotionEffect == null) {
                return 0;
            }
            return activePotionEffect.getAmplifier() / 100D;
        }
        return 0;
    }
}
