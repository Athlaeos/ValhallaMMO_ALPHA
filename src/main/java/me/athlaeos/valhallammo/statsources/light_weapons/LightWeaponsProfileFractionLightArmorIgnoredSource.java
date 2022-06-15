package me.athlaeos.valhallammo.statsources.light_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.dom.WeaponType;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.lightweapons.LightWeaponsProfile;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LightWeaponsProfileFractionLightArmorIgnoredSource extends EvEAccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        if (offender instanceof Player){
            ItemStack weapon = ((Player) offender).getInventory().getItemInMainHand();
            if (Utils.isItemEmptyOrNull(weapon) || WeaponType.getWeaponType(weapon) != WeaponType.LIGHT) return 0;
            Profile profile = ProfileManager.getManager().getProfile((Player) offender, "LIGHT_WEAPONS");
            if (profile != null){
                if (profile instanceof LightWeaponsProfile){
                    return ((LightWeaponsProfile) profile).getFractionLightArmorIgnored();
                }
            }
        }
        return 0;
    }
}
