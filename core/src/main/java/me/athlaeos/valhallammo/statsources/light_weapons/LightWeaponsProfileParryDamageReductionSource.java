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

public class LightWeaponsProfileParryDamageReductionSource extends EvEAccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        if (entity instanceof Player){
            // offender got parried, grab entity stats
            ItemStack weapon = ((Player) entity).getInventory().getItemInMainHand();
            if (Utils.isItemEmptyOrNull(weapon) || WeaponType.getWeaponType(weapon) != WeaponType.LIGHT) return 0;
            Profile profile = ProfileManager.getManager().getProfile((Player) entity, "LIGHT_WEAPONS");
            if (profile != null){
                if (profile instanceof LightWeaponsProfile){
                    return ((LightWeaponsProfile) profile).getParryDamageReduction();
                }
            }
        }
        return 0;
    }
}
