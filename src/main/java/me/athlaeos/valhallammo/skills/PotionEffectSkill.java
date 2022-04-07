package me.athlaeos.valhallammo.skills;

import org.bukkit.event.entity.AreaEffectCloudApplyEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.LingeringPotionSplashEvent;
import org.bukkit.event.entity.PotionSplashEvent;

public interface PotionEffectSkill {
    void onPotionEffect(EntityPotionEffectEvent event);

    void onPotionSplash(PotionSplashEvent event);

    void onPotionLingering(LingeringPotionSplashEvent event);

    void onLingerApply(AreaEffectCloudApplyEvent event);
}
