package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class BuffResistanceDamageResistanceSource extends AccumulativeStatSource {
    private final double resistancePerLevel;

    public BuffResistanceDamageResistanceSource(){
        resistancePerLevel = ConfigManager.getInstance().getConfig("config.yml").get().getDouble("buff_resistance_reduction");
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof LivingEntity){
            PotionEffect effect = ((LivingEntity) p).getPotionEffect(PotionEffectType.DAMAGE_RESISTANCE);
            if (effect != null){
                return (effect.getAmplifier() + 1) * resistancePerLevel;
            }
        }
        return 0;
    }
}
