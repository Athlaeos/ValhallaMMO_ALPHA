package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.dom.Transmutation;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.items.potioneffectwrappers.PotionEffectWrapper;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.skills.ItemConsumptionSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;

import java.util.Collection;
import java.util.Map;

public class ItemConsumeListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onItemConsume(PlayerItemConsumeEvent e){
        if (!e.isCancelled()){
            if (e.getItem().getType() == Material.POTION){
                double chance = AccumulativeStatManager.getInstance().getStats("ALCHEMY_POTION_SAVE", e.getPlayer(), true);
                if (Utils.getRandom().nextDouble() < chance){
                    e.getPlayer().getInventory().addItem(e.getItem());
                }

                Collection<PotionEffectWrapper> storedPotionEffects = PotionAttributesManager.getInstance().getCurrentStats(e.getItem());
                for (PotionEffectWrapper wrapper : storedPotionEffects){
                    if (wrapper.getPotionEffect().equals("MILK")){
                        PotionEffectManager.getInstance().removePotionEffects(e.getPlayer(), PotionEffect::isRemovable);
                        for (org.bukkit.potion.PotionEffect p : e.getPlayer().getActivePotionEffects()){
                            EntityPotionEffectEvent event = new EntityPotionEffectEvent(e.getPlayer(), p, null, EntityPotionEffectEvent.Cause.PLUGIN, EntityPotionEffectEvent.Action.REMOVED, true);
                            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
                            if (!event.isCancelled()){
                                e.getPlayer().removePotionEffect(p.getType());
                            }
                        }
                    } else if (wrapper.getPotionEffect().equals("CHOCOLATE_MILK")){
                        PotionEffectManager.getInstance().removePotionEffects(e.getPlayer(), potionEffect -> potionEffect.isRemovable() && potionEffect.getType() == PotionType.DEBUFF);
                        FoodLevelChangeEvent foodEvent = new FoodLevelChangeEvent(e.getPlayer(), 4, e.getItem());
                        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(foodEvent);
                        if (!foodEvent.isCancelled()){
                            e.getPlayer().setFoodLevel(Math.min(e.getPlayer().getFoodLevel() + foodEvent.getFoodLevel(), 20));
                            e.getPlayer().setSaturation(Math.min(e.getPlayer().getSaturation() + 6, 20));
                        }
                        for (org.bukkit.potion.PotionEffect p : e.getPlayer().getActivePotionEffects()){
                            if (PotionType.DEBUFF.getVanillaEffects().contains(p.getType())){
                                EntityPotionEffectEvent potionEvent = new EntityPotionEffectEvent(e.getPlayer(), p, null, EntityPotionEffectEvent.Cause.PLUGIN, EntityPotionEffectEvent.Action.REMOVED, true);
                                ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(potionEvent);
                                if (!potionEvent.isCancelled()){
                                    e.getPlayer().removePotionEffect(p.getType());
                                }
                            }
                        }
                    } else {
                        PotionEffect basePotionEffect = PotionEffectManager.getInstance().getBasePotionEffect(wrapper.getPotionEffect());
                        if (basePotionEffect != null){
                            // Potion effect is custom potion effect
                            PotionEffectManager.getInstance().addPotionEffect(e.getPlayer(), new PotionEffect(wrapper.getPotionEffect(), System.currentTimeMillis() + wrapper.getDuration(), wrapper.getAmplifier(), basePotionEffect.getType()), false);
                        } // otherwise stored potion does not exist
                    }
                }

                Map<Material, Transmutation> transmutations = TransmutationManager.getInstance().getStoredTransmutations(e.getItem());
                if (!transmutations.isEmpty()){
                    AttributeInstance a = e.getPlayer().getAttribute(Attribute.GENERIC_MAX_HEALTH);
                    if (a != null){
                        e.getPlayer().damage(0);
                        e.getPlayer().setHealth(1);
                    }
                }
            } else if (e.getItem().getType() == Material.MILK_BUCKET){
                for (PotionEffect effect : PotionEffectManager.getInstance().getActivePotionEffects(e.getPlayer()).values()){
                    effect.setEffectiveUntil(0);
                    PotionEffectManager.getInstance().addPotionEffect(e.getPlayer(), effect, true);
                }
            }

            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof ItemConsumptionSkill){
                        ((ItemConsumptionSkill) skill).onItemConsume(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onHungerChange(FoodLevelChangeEvent e){
        for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
            if (skill != null){
                if (skill instanceof ItemConsumptionSkill){
                    ((ItemConsumptionSkill) skill).onHungerChange(e);
                }
            }
        }
    }
}
