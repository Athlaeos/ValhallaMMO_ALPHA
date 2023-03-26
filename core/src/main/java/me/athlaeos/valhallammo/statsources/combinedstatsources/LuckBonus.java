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
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LuckBonus extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        double amount = 0;
        if (p instanceof LivingEntity){

            if (p instanceof Player){
                Profile profile = ProfileManager.getManager().getProfile((Player) p, "ACCOUNT");
                if (profile != null){
                    if (profile instanceof AccountProfile){
                        amount += ((AccountProfile) profile).getLuckBonus();
                    }
                }
            }

            if (ValhallaMMO.isTrinketsHooked()){
                EntityProperties entityProperties = EntityEquipmentCacheManager.getInstance().getAndCacheEquipment((LivingEntity) p);
                for (ItemStack i : entityProperties.getIterable(false)){
                    if (EquipmentClass.getClass(i) == EquipmentClass.TRINKET) {
                        AttributeWrapper attributeWrapper = ItemAttributesManager.getInstance().getAnyAttributeWrapper(i, "GENERIC_LUCK");
                        if (attributeWrapper != null) amount += attributeWrapper.getAmount();
                    }
                }
            }
        }
        return amount;
    }
}
