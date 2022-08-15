package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.EnchantmentApplicationSkill;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;

public class PlayerEnchantListener implements Listener {

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerEnchant(EnchantItemEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getEnchantBlock().getWorld().getName())) return;
        if (!e.isCancelled()){
            for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (s != null){
                    if (s instanceof EnchantmentApplicationSkill){
                        ((EnchantmentApplicationSkill) s).onEnchantItem(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPrepareEnchant(PrepareItemEnchantEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getEnchantBlock().getWorld().getName())) return;
        if (!e.isCancelled()){
            for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (s != null){
                    if (s instanceof EnchantmentApplicationSkill){
                        ((EnchantmentApplicationSkill) s).onPrepareEnchant(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPrepareEnchant(PrepareAnvilEvent e){
        if (e.getInventory().getLocation() != null){
            if (e.getInventory().getLocation().getWorld() != null){
                if (ValhallaMMO.isWorldBlacklisted(e.getInventory().getLocation().getWorld().getName())) return;
            }
        }
        for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
            if (s != null){
                if (s instanceof EnchantmentApplicationSkill){
                    ((EnchantmentApplicationSkill) s).onAnvilUsage(e);
                }
            }
        }
    }
}
