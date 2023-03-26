package me.athlaeos.valhallammo.statsources.combinedstatsources;

import me.athlaeos.valhallammo.dom.EntityProperties;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.dom.WeaponType;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.EntityEquipmentCacheManager;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.heavyweapons.HeavyWeaponsProfile;
import me.athlaeos.valhallammo.skills.lightweapons.LightWeaponsProfile;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class BleedDamage extends EvEAccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        double amount = 0;
        if (offender instanceof LivingEntity){
            EntityProperties entityProperties = EntityEquipmentCacheManager.getInstance().getAndCacheEquipment((LivingEntity) offender);
            ItemStack hand = entityProperties.getMainHand();

            if (!Utils.isItemEmptyOrNull(hand)){
                if (offender instanceof Player){
                    if (WeaponType.getWeaponType(hand) == WeaponType.LIGHT) {
                        Profile profile = ProfileManager.getManager().getProfile((Player) offender, "LIGHT_WEAPONS");
                        if (profile != null){
                            if (profile instanceof LightWeaponsProfile){
                                amount += ((LightWeaponsProfile) profile).getBleedDamage();
                            }
                        }
                    } else if (WeaponType.getWeaponType(hand) == WeaponType.HEAVY) {
                        Profile profile = ProfileManager.getManager().getProfile((Player) offender, "HEAVY_WEAPONS");
                        if (profile != null){
                            if (profile instanceof HeavyWeaponsProfile){
                                amount += ((HeavyWeaponsProfile) profile).getBleedDamage();
                            }
                        }
                    }
                }
                if (EquipmentClass.getClass(hand) != EquipmentClass.TRINKET && !EquipmentClass.isArmor(hand)) {
                    AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(hand, "CUSTOM_BLEED_DAMAGE");
                    if (wrapper != null) amount += wrapper.getAmount();
                }
            }
            for (ItemStack i : entityProperties.getIterable(false)){
                AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(i, "CUSTOM_BLEED_DAMAGE");
                if (wrapper != null) amount += wrapper.getAmount();
            }
            amount += ItemUtils.combinedCustomEnchantAmplifier((LivingEntity) offender, "BLEED_DAMAGE");
        }
        return amount;
    }
}
