package me.athlaeos.valhallammo.statsources.combinedstatsources;

import me.athlaeos.valhallammo.dom.*;
import me.athlaeos.valhallammo.managers.EntityEquipmentCacheManager;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.lightweapons.LightWeaponsProfile;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LightWeaponsDamageMultiplier extends EvEAccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        double amount = 1;
        if (offender instanceof LivingEntity){
            if (offender instanceof Player){
                ItemStack weapon = ((Player) offender).getInventory().getItemInMainHand();
                if (!Utils.isItemEmptyOrNull(weapon) && WeaponType.getWeaponType(weapon) == WeaponType.LIGHT) {
                    Profile profile = ProfileManager.getManager().getProfile((Player) offender, "LIGHT_WEAPONS");
                    if (profile != null){
                        if (profile instanceof LightWeaponsProfile){
                            amount += ((LightWeaponsProfile) profile).getDamageMultiplier() - 1;
                            EntityProperties equipment = EntityEquipmentCacheManager.getInstance().getAndCacheEquipment((LivingEntity) entity);
                            amount += equipment.getHeavyArmorCount() * ((LightWeaponsProfile) profile).getDamageBonusHeavyArmor();
                            amount += equipment.getLightArmorCount() * ((LightWeaponsProfile) profile).getDamageBonusLightArmor();
                        }
                    }
                }
            }

            PotionEffect activePotionEffect = PotionEffectManager.getInstance().getPotionEffect(offender, "WEAPONS_DAMAGE");
            if (activePotionEffect != null) amount += activePotionEffect.getAmplifier();

            amount += ItemUtils.combinedCustomEnchantAmplifier((LivingEntity) offender, "HEAVY_ARMOR_DAMAGE");
        }
        return amount;
    }
}
