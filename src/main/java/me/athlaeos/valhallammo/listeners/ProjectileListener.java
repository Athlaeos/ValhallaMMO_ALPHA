package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.ProjectileSkill;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;

public class ProjectileListener implements Listener {

    @EventHandler(priority= EventPriority.LOWEST)
    public void onProjectileShoot(ProjectileLaunchEvent e){
        if (!e.isCancelled()){
            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof ProjectileSkill){
                        ((ProjectileSkill) skill).onProjectileLaunch(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onBowShoot(EntityShootBowEvent e){
        if (!e.isCancelled()){
            if (e.getProjectile() instanceof AbstractArrow){
                LivingEntity shooter = e.getEntity();

                // apply custom bow draw strength attribute to arrow speed
                if (shooter.getEquipment() != null){
                    ItemStack shooterItem = e.getBow();
                    if (shooterItem != null){
                        AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(shooterItem, "CUSTOM_DRAW_STRENGTH");
                        if (wrapper != null){
                            double drawStrength = wrapper.getAmount();
                            if (drawStrength != 1){
                                e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(drawStrength));
                            }
                        }
                    }
                }
            }

            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof ProjectileSkill){
                        ((ProjectileSkill) skill).onEntityShootBow(e);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent e){
        if (!e.isCancelled()){
            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof ProjectileSkill){
                        ((ProjectileSkill) skill).onProjectileHit(e);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onArrowPickup(PlayerPickupArrowEvent e){
        if (!e.isCancelled()){
            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof ProjectileSkill){
                        ((ProjectileSkill) skill).onArrowPickup(e);
                    }
                }
            }
        }
    }
}
