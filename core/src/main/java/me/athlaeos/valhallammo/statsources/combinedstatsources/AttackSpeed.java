package me.athlaeos.valhallammo.statsources.combinedstatsources;

import me.athlaeos.valhallammo.dom.EntityProperties;
import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.dom.WeaponType;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.managers.EntityEquipmentCacheManager;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.skills.heavyweapons.HeavyWeaponsProfile;
import me.athlaeos.valhallammo.skills.lightweapons.LightWeaponsProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AttackSpeed extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        double amount = 0;
        if (p instanceof LivingEntity){
            EntityProperties entityProperties = EntityEquipmentCacheManager.getInstance().getAndCacheEquipment((LivingEntity) p);
            ItemStack hand = entityProperties.getMainHand();

            if (!Utils.isItemEmptyOrNull(hand)){
                if (p instanceof Player){
                    Profile profile = ProfileManager.getManager().getProfile((Player) p, "ACCOUNT");
                    if (profile != null){
                        if (profile instanceof AccountProfile){
                            amount += ((AccountProfile) profile).getAttackSpeedBonus();
                        }
                    }

                    if (WeaponType.getWeaponType(hand) == WeaponType.LIGHT) {
                        Profile lightWeaponsProfile = ProfileManager.getManager().getProfile((Player) p, "LIGHT_WEAPONS");
                        if (lightWeaponsProfile != null){
                            if (lightWeaponsProfile instanceof LightWeaponsProfile){
                                amount += ((LightWeaponsProfile) lightWeaponsProfile).getAttackSpeedBonus();
                            }
                        }
                    } else if (WeaponType.getWeaponType(hand) == WeaponType.HEAVY) {
                        Profile heavyWeaponsProfile = ProfileManager.getManager().getProfile((Player) p, "HEAVY_WEAPONS");
                        if (heavyWeaponsProfile != null){
                            if (heavyWeaponsProfile instanceof HeavyWeaponsProfile){
                                amount += ((HeavyWeaponsProfile) heavyWeaponsProfile).getAttackSpeedBonus();
                            }
                        }
                    }
                }
                if (EquipmentClass.getClass(hand) != EquipmentClass.TRINKET && !EquipmentClass.isArmor(hand)) {
                    AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(hand, "CUSTOM_KNOCKBACK");
                    if (wrapper != null) amount += wrapper.getAmount();
                }
            }

            for (ItemStack i : entityProperties.getIterable(false)){
                AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(i, "GENERIC_ATTACK_SPEED");
                if (wrapper != null) amount += wrapper.getAmount();
            }

            PotionEffect activePotionEffect = PotionEffectManager.getInstance().getPotionEffect(p, "KNOCKBACK_BONUS");
            if (activePotionEffect != null) amount += activePotionEffect.getAmplifier();
        }
        return amount;
    }
}
