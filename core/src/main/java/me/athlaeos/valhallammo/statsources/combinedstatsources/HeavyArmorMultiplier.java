package me.athlaeos.valhallammo.statsources.combinedstatsources;

import me.athlaeos.valhallammo.dom.EntityProperties;
import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.EntityEquipmentCacheManager;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.heavyarmor.HeavyArmorProfile;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class HeavyArmorMultiplier extends EvEAccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        return add(p, null, use);
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        double amount = 1;
        if (entity instanceof LivingEntity){
            if (entity instanceof Player){
                Profile profile = ProfileManager.getManager().getProfile((Player) entity, "HEAVY_ARMOR");
                if (profile != null){
                    if (profile instanceof HeavyArmorProfile){
                        amount += ((HeavyArmorProfile) profile).getHeavyArmorMultiplier() - 1;

                        EntityProperties equipment = EntityEquipmentCacheManager.getInstance().getAndCacheEquipment((Player) entity);
                        int count = equipment.getHeavyArmorCount();
                        if (count >= ((HeavyArmorProfile) profile).getArmorPiecesForBonusses())
                            amount += ((HeavyArmorProfile) profile).getFullArmorMultiplierBonus();
                    }
                }
            }

            PotionEffect activePotionEffect = PotionEffectManager.getInstance().getPotionEffect(entity, "HEAVY_ARMOR_FRACTION_BONUS");
            if (activePotionEffect != null) amount += activePotionEffect.getAmplifier();
        }
        return amount;
    }
}
