package me.athlaeos.valhallammo.statsources.farming;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class FarmingLuckFishingRewardTierSource extends AccumulativeStatSource {
    private final double fishing_luck_potency;
    public FarmingLuckFishingRewardTierSource(){
        YamlConfiguration farmingConfig = ConfigManager.getInstance().getConfig("skill_farming.yml").get();
        fishing_luck_potency = farmingConfig.getDouble("fishing_luck_potency");
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof LivingEntity){
            LivingEntity e = (LivingEntity) p;
            PotionEffect level = e.getPotionEffect(PotionEffectType.LUCK);
            if (level != null){
                return fishing_luck_potency * (level.getAmplifier() + 1);
            }
        }
        return 0;
    }
}
