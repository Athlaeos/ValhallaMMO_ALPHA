package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.FishingSkill;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerFishEvent;

public class FishingListener implements Listener {
    @EventHandler(priority =EventPriority.HIGHEST)
    public void onPlayerFish(PlayerFishEvent e){
        if (!e.isCancelled()){
            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof FishingSkill){
                        ((FishingSkill) skill).onFishing(e);
                    }
                }
            }
        }
    }
}
