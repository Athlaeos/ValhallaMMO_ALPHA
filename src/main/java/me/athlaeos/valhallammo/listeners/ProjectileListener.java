package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats.AttributeAddArrowInfinityExploitableModifier;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.CustomArrowManager;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.ProjectileSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.ItemUtils;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Trident;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;

public class ProjectileListener implements Listener {

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onProjectileShoot(ProjectileLaunchEvent e){
        if (!e.isCancelled()){
            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof ProjectileSkill){
                        ((ProjectileSkill) skill).onProjectileLaunch(e);
                    }
                }
            }

            if (e.getEntity() instanceof AbstractArrow && !(e.getEntity() instanceof Trident)){
                ItemStack ammo = ItemUtils.getArrowFromEntity((AbstractArrow) e.getEntity());
                if (ammo == null) return;
                CustomArrowManager.getInstance().executeOnLaunch(ammo, e);
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onBowShoot(EntityShootBowEvent e){
        if (!e.isCancelled()){
            if (e.getProjectile() instanceof AbstractArrow){
                AbstractArrow arrow = (AbstractArrow) e.getProjectile();
                LivingEntity shooter = e.getEntity();
                double projectileSpeed = 1;

                // apply custom bow draw strength attribute to arrow speed
                if (shooter.getEquipment() != null){
                    ItemStack shooterItem = e.getBow();
                    if (shooterItem != null){
                        AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(shooterItem, "CUSTOM_DRAW_STRENGTH");
                        if (wrapper != null){
                            projectileSpeed = wrapper.getAmount();
                        }
                    }
                }
                if (e.getConsumable() != null){
                    AttributeWrapper speedWrapper = ItemAttributesManager.getInstance().getAttributeWrapper(e.getConsumable(), "CUSTOM_ARROW_SPEED");
                    boolean isInfinityCompatible = AttributeAddArrowInfinityExploitableModifier.isInfinityCompatible(e.getConsumable());
                    boolean hasInfinity = (e.getBow() != null && e.getBow().getEnchantments().containsKey(Enchantment.ARROW_INFINITE));
                    boolean shouldSave = isInfinityCompatible && hasInfinity;
                    if (speedWrapper != null){
                        e.setConsumeItem(!shouldSave);
                        projectileSpeed += speedWrapper.getAmount();
//                        arrow.setMetadata("valhallammo_arrow_speed", new FixedMetadataValue(ValhallaMMO.getPlugin(), speedWrapper.getAmount()));
                    }
                }
                projectileSpeed = Math.max(0, projectileSpeed);
                e.getProjectile().setVelocity(e.getProjectile().getVelocity().multiply(projectileSpeed));

                ItemStack singleAmmo = e.getConsumable().clone();
                singleAmmo.setAmount(1);
                arrow.setMetadata("arrow_data", new FixedMetadataValue(ValhallaMMO.getPlugin(), ItemUtils.serializeItemStack(singleAmmo)));
            }

            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof ProjectileSkill){
                        ((ProjectileSkill) skill).onEntityShootBow(e);
                    }
                }
            }

            CustomArrowManager.getInstance().executeOnShoot(e.getConsumable(), e);
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

            if (e.getEntity() instanceof AbstractArrow && !(e.getEntity() instanceof Trident)){
                ItemStack ammo = ItemUtils.getArrowFromEntity((AbstractArrow) e.getEntity());
                if (ammo == null) return;
                CustomArrowManager.getInstance().executeOnHit(ammo, e);
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

            if (!(e.getArrow() instanceof Trident)){
                Item i = e.getItem();
                ItemStack ammo = ItemUtils.getArrowFromEntity(e.getArrow());
                if (ammo == null) return;
                CustomArrowManager.getInstance().executeOnPickup(ammo, e);
                i.setItemStack(ammo);
            }
        }
    }
}
