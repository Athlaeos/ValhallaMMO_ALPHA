package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.EntityTargetingSkill;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetEvent;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

public class EntityTargetingListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityTargetEntity(EntityTargetLivingEntityEvent e){
        if (!e.isCancelled()){
            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof EntityTargetingSkill){
                        ((EntityTargetingSkill) skill).onEntityTargetEntity(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityTarget(EntityTargetEvent e){
        if (!e.isCancelled()){
            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof EntityTargetingSkill){
                        ((EntityTargetingSkill) skill).onEntityTarget(e);
                    }
                }
            }
        }
    }
}
