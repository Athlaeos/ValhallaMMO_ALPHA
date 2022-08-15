package me.athlaeos.valhallammo.statsources.farming;

import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class FarmingPotionRareDropsSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            PotionEffect activePotionEffect = PotionEffectManager.getInstance().getPotionEffect(p, "FARMING_RARE_DROPS");

            if (activePotionEffect == null) {
                return 0;
            }
            return activePotionEffect.getAmplifier() / 100D;
        }
        return 0;
    }
}
