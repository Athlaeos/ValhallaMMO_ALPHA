package me.athlaeos.valhallammo.statsources.heavy_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.heavyweapons.HeavyWeaponsProfile;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class HeavyWeaponsProfileDamageMultiplierSource extends EvEAccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        return 1;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        if (offender instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) offender, "HEAVY_WEAPONS");
            if (profile != null){
                if (profile instanceof HeavyWeaponsProfile){
                    return ((HeavyWeaponsProfile) profile).getDamageMultiplier();
                }
            }
        }
        return 1;
    }
}
