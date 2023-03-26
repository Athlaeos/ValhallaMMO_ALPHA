package me.athlaeos.valhallammo.statsources.combinedstatsources;

import me.athlaeos.valhallammo.dom.ArmorType;
import me.athlaeos.valhallammo.dom.EntityProperties;
import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.items.OverleveledEquipmentTool;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.EntityEquipmentCacheManager;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ArmorWeightlessTotal extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        double amount = 0;
        if (p instanceof LivingEntity){

            if (p instanceof Player){
                Profile profile = ProfileManager.getManager().getProfile((Player) p, "ACCOUNT");
                if (profile != null){
                    if (profile instanceof AccountProfile){
                        amount += ((AccountProfile) profile).getArmorBonus();
                    }
                }
            }

            AttributeInstance instance = ((LivingEntity) p).getAttribute(Attribute.GENERIC_ARMOR);
            if (instance != null) amount += instance.getBaseValue();

            EntityProperties entityProperties = EntityEquipmentCacheManager.getInstance().getAndCacheEquipment((LivingEntity) p);
            for (ItemStack i : entityProperties.getIterable(false)){
                if (EquipmentClass.getClass(i) == EquipmentClass.TRINKET || ArmorType.getArmorType(i) == ArmorType.WEIGHTLESS) {
                    AttributeWrapper attributeWrapper = ItemAttributesManager.getInstance().getAnyAttributeWrapper(i, "GENERIC_ARMOR");
                    if (attributeWrapper != null) {
                        double value = attributeWrapper.getAmount();
                        if (p instanceof Player){
                            double penalty = OverleveledEquipmentTool.getTool().getPenalty((Player) p, i, "armor");
                            value *= (1 + penalty);
                        }
                        amount += value;
                    }
                }
            }

            PotionEffect activePotionEffect = PotionEffectManager.getInstance().getPotionEffect(p, "ARMOR_FLAT_BONUS");
            if (activePotionEffect != null) amount += activePotionEffect.getAmplifier();
        }
        return amount;
    }
}
