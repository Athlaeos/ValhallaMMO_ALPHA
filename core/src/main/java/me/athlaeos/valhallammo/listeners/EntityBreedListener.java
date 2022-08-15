package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.farming.FarmingSkill;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;

public class EntityBreedListener implements Listener {
    private FarmingSkill farmingSkill = null;

    public EntityBreedListener(){
        Skill s = SkillProgressionManager.getInstance().getSkill("FARMING");
        if (s != null){
            if (s instanceof FarmingSkill){
                farmingSkill = (FarmingSkill) s;
            }
        }
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onEntityBreed(EntityBreedEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getEntity().getWorld().getName())) return;
        if (!e.isCancelled()){
            if (farmingSkill != null){
                farmingSkill.onAnimalBreeding(e);
            }
        }
    }
}
