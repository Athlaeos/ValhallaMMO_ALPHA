package me.athlaeos.valhallammo.statsources.combinedstatsources;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.EntityProperties;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.EntityEquipmentCacheManager;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.skills.heavyarmor.HeavyArmorProfile;
import me.athlaeos.valhallammo.skills.lightarmor.LightArmorProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class MovementSpeed extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        double amount = 0;
        if (p instanceof LivingEntity){

            if (p instanceof Player){
                Profile profile = ProfileManager.getManager().getProfile((Player) p, "ACCOUNT");
                if (profile != null){
                    if (profile instanceof AccountProfile){
                        amount += ((AccountProfile) profile).getMovementSpeedBonus();
                    }
                }

                EntityProperties equipment = EntityEquipmentCacheManager.getInstance().getAndCacheEquipment((LivingEntity) p);
                int lightArmorCount = equipment.getLightArmorCount();
                if (lightArmorCount > 0){
                    Profile lightArmorProfile = ProfileManager.getManager().getProfile((Player) p, "LIGHT_ARMOR");
                    if (lightArmorProfile != null){
                        if (lightArmorProfile instanceof LightArmorProfile){
                            double penaltyPerPiece = ((LightArmorProfile) lightArmorProfile).getMovementSpeedPenalty();
                            amount -= lightArmorCount * penaltyPerPiece;
                        }
                    }
                }
                int heavyArmorCount = equipment.getHeavyArmorCount();
                if (heavyArmorCount > 0){
                    Profile heavyArmorProfile = ProfileManager.getManager().getProfile((Player) p, "HEAVY_ARMOR");
                    if (heavyArmorProfile != null){
                        if (heavyArmorProfile instanceof HeavyArmorProfile){
                            double penaltyPerPiece = ((HeavyArmorProfile) heavyArmorProfile).getMovementSpeedPenalty();
                            amount -= heavyArmorCount * penaltyPerPiece;
                        }
                    }
                }
            }

            if (ValhallaMMO.isTrinketsHooked()){
                EntityProperties entityProperties = EntityEquipmentCacheManager.getInstance().getAndCacheEquipment((LivingEntity) p);
                for (ItemStack i : entityProperties.getIterable(false)){
                    if (EquipmentClass.getClass(i) == EquipmentClass.TRINKET) {
                        AttributeWrapper attributeWrapper = ItemAttributesManager.getInstance().getAnyAttributeWrapper(i, "GENERIC_MOVEMENT_SPEED");
                        if (attributeWrapper != null) amount += attributeWrapper.getAmount();
                    }
                }
            }
        }
        return amount;
    }
}
