package me.athlaeos.valhallammo.statsources.heavy_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.dom.WeaponType;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.heavyweapons.HeavyWeaponsProfile;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class HeavyWeaponsProfileAttackKnockbackBonusSource extends EvEAccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity p, boolean use) {
        if (p instanceof Player){
            ItemStack weapon = ((Player) p).getInventory().getItemInMainHand();
            if (Utils.isItemEmptyOrNull(weapon) || WeaponType.getWeaponType(weapon) != WeaponType.HEAVY) return 0;
            Profile profile = ProfileManager.getManager().getProfile((Player) p, "HEAVY_WEAPONS");
            if (profile != null){
                if (profile instanceof HeavyWeaponsProfile){
                    return ((HeavyWeaponsProfile) profile).getKnockbackBonus();
                }
            }
        }
        return 0;
    }
}
