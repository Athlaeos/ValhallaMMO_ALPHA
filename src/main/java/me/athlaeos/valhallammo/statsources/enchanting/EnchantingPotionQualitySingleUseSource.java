package me.athlaeos.valhallammo.statsources.enchanting;

import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityPotionEffectEvent;

public class EnchantingPotionQualitySingleUseSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        PotionEffect potionEffect = PotionEffectManager.getInstance().getPotionEffect(p, "MASTERPIECE_ENCHANTING");
        if (potionEffect != null && p instanceof LivingEntity){
            if (use){
                potionEffect.setEffectiveUntil(0);
                PotionEffectManager.getInstance().addPotionEffect((LivingEntity) p, potionEffect, true, EntityPotionEffectEvent.Cause.PLUGIN, EntityPotionEffectEvent.Action.REMOVED);
            }
            return potionEffect.getAmplifier();
        }
        return 0;
    }
}
