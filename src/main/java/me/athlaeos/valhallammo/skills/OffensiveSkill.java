package me.athlaeos.valhallammo.skills;

import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;

public interface OffensiveSkill {
    void onEntityDamage(EntityDamageByEntityEvent event);

    void onEntityKilled(EntityDeathEvent event);
}
