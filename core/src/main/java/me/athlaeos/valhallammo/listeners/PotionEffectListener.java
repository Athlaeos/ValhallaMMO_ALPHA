package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.dom.Transmutation;
import me.athlaeos.valhallammo.events.EntityCustomPotionEffectEvent;
import me.athlaeos.valhallammo.items.potioneffectwrappers.PotionEffectWrapper;
import me.athlaeos.valhallammo.managers.PotionAttributesManager;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.managers.TransmutationManager;
import me.athlaeos.valhallammo.skills.PotionEffectSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Particle;
import org.bukkit.block.Block;
import org.bukkit.entity.AreaEffectCloud;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.ThrownPotion;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffectType;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class PotionEffectListener implements Listener {

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPotionEffect(EntityPotionEffectEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getEntity().getWorld().getName())) return;
        if (!e.isCancelled()){
            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof PotionEffectSkill){
                        ((PotionEffectSkill) skill).onPotionEffect(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPotionEffect(EntityCustomPotionEffectEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getEntity().getWorld().getName())) return;
        if (!e.isCancelled()){
            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof PotionEffectSkill){
                        ((PotionEffectSkill) skill).onCustomPotionEffect(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPotionSplash(PotionSplashEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getEntity().getWorld().getName())) return;
        if (!e.isCancelled()){
            Collection<PotionEffectWrapper> storedPotionEffects = PotionAttributesManager.getInstance().getCurrentStats(e.getPotion().getItem());
            for (LivingEntity entity : e.getAffectedEntities()){
                double intensity = e.getIntensity(entity);
                for (PotionEffectWrapper wrapper : storedPotionEffects){
                    PotionEffect basePotionEffect = PotionEffectManager.getInstance().getBasePotionEffect(wrapper.getPotionEffect());
                    if (basePotionEffect != null){
                        // Potion effect is custom potion effect
                        PotionEffectManager.getInstance().addPotionEffect(entity, new PotionEffect(wrapper.getPotionEffect(), System.currentTimeMillis() + (int) Math.floor(intensity * wrapper.getDuration()), wrapper.getAmplifier(), basePotionEffect.getType()), false, EntityPotionEffectEvent.Cause.POTION_SPLASH, EntityPotionEffectEvent.Action.ADDED);
                    } // otherwise stored potion does not exist
                }
            }

            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof PotionEffectSkill){
                        ((PotionEffectSkill) skill).onPotionSplash(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onProjectileHitBlock(ProjectileHitEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getEntity().getWorld().getName())) return;
        if (e.getHitBlock() != null){
            if (e.getEntity() instanceof ThrownPotion){
                if (e.getEntity().getShooter() instanceof Player){
                    Map<Material, Transmutation> transmutations = TransmutationManager.getInstance().getStoredTransmutations(((ThrownPotion) e.getEntity()).getItem());
                    if (!transmutations.isEmpty()){
                        int radius = TransmutationManager.getInstance().getRadius();
                        Collection<Block> affectedBlocks = Utils.getBlocksTouching(e.getHitBlock(), radius, 1, radius, Material.AIR, Material.CAVE_AIR, Material.VOID_AIR);
                        for (Block b : affectedBlocks){
                            if (transmutations.containsKey(b.getType())){
                                BlockBreakEvent event = new BlockBreakEvent(b, ((Player) e.getEntity().getShooter()));
                                ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
                                if (!event.isCancelled()){
                                    b.setType(transmutations.get(b.getType()).getTo());
                                }
                            }
                        }
                        if (TransmutationManager.getInstance().isFlash()){
                            Location l = e.getEntity().getLocation();
                            if (l.getWorld() != null){
                                l.getWorld().spawnParticle(Particle.FLASH, l, 0);
                                if (TransmutationManager.getInstance().getSound() != null){
                                    l.getWorld().playSound(l, TransmutationManager.getInstance().getSound(), 1F, 1F);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private final NamespacedKey potionCloudKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_potion_cloud_custom_effects");

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPotionLinger(LingeringPotionSplashEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getEntity().getWorld().getName())) return;
        // Setting the potion effects to the cloud

        if (!e.isCancelled()){
            ThrownPotion thrownPotion = e.getEntity();
            ItemStack potion = thrownPotion.getItem();
            if (potion.getItemMeta() == null) return;
            if (potion.getItemMeta().getPersistentDataContainer().has(PotionAttributesManager.getInstance().getCustomPotionEffectsKey(), PersistentDataType.STRING)){
                String encodedPotionEffects = potion.getItemMeta().getPersistentDataContainer().get(PotionAttributesManager.getInstance().getCustomPotionEffectsKey(), PersistentDataType.STRING);
                if (encodedPotionEffects == null) {
                    return;
                }

                e.getAreaEffectCloud().getPersistentDataContainer().set(potionCloudKey, PersistentDataType.STRING, encodedPotionEffects);
                e.getAreaEffectCloud().addCustomEffect(new org.bukkit.potion.PotionEffect(PotionEffectType.BLINDNESS, 0, 0, false, false, false), false);
            }

            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof PotionEffectSkill){
                        ((PotionEffectSkill) skill).onPotionLingering(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onLingeringCloudHit(AreaEffectCloudApplyEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getEntity().getWorld().getName())) return;
        if (!e.isCancelled()){
            AreaEffectCloud cloud = e.getEntity();
            if (cloud.getPersistentDataContainer().has(potionCloudKey, PersistentDataType.STRING)){
                String cloudString = cloud.getPersistentDataContainer().get(potionCloudKey, PersistentDataType.STRING);
                if (cloudString == null) return;
                Collection<PotionEffectWrapper> potionEffects = getCustomPotionEffectsFromString(cloudString);

                for (LivingEntity entity : e.getAffectedEntities()){
                    for (PotionEffectWrapper wrapper : potionEffects){
                        PotionEffect basePotionEffect = PotionEffectManager.getInstance().getBasePotionEffect(wrapper.getPotionEffect());
                        if (basePotionEffect != null){
                            // Potion effect is custom potion effect
                            PotionEffectManager.getInstance().addPotionEffect(entity, new PotionEffect(wrapper.getPotionEffect(), System.currentTimeMillis() + wrapper.getDuration(), wrapper.getAmplifier(), basePotionEffect.getType()), false, EntityPotionEffectEvent.Cause.AREA_EFFECT_CLOUD, EntityPotionEffectEvent.Action.ADDED);
                        } // otherwise stored potion does not exist
                    }
                }
            }

            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof PotionEffectSkill){
                        ((PotionEffectSkill) skill).onLingerApply(e);
                    }
                }
            }
        }
    }

    private Collection<PotionEffectWrapper> getCustomPotionEffectsFromString(String effects){
        Collection<PotionEffectWrapper> customPotionEffects = new HashSet<>();
        if (effects != null){
            String[] customEffects = effects.split(";");
            for (String a : customEffects){
                String[] attributeProperties = a.split(":");
                if (attributeProperties.length >= 3){
                    String attribute = attributeProperties[0];
                    String amplifier = attributeProperties[1];
                    String duration = attributeProperties[2];
                    try {
                        if (PotionAttributesManager.getInstance().getRegisteredPotionEffects().containsKey(attribute)){
                            PotionEffectWrapper wrapper = PotionAttributesManager.getInstance().getRegisteredPotionEffects().get(attribute).clone();
                            if (PotionEffectType.getByName(wrapper.getPotionEffect()) != null) {
                                continue;
                            }
                            wrapper.setAmplifier(Double.parseDouble(amplifier));
                            wrapper.setDuration(Integer.parseInt(duration));
                            customPotionEffects.add(wrapper);
                        }
                    } catch (IllegalArgumentException | CloneNotSupportedException ignored){
                    }
                }
            }
        }

        return customPotionEffects;
    }
}
