package me.athlaeos.valhallammo.skills;

import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public interface EntityTargetingSkill {
    void onEntityTargetEntity(EntityTargetLivingEntityEvent event);

    void onEntityTarget(EntityTargetEvent event);
}
