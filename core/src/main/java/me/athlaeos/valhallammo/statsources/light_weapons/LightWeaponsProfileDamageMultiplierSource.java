package me.athlaeos.valhallammo.statsources.light_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.lightweapons.LightWeaponsProfile;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LightWeaponsProfileDamageMultiplierSource extends EvEAccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        return 1;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        if (offender instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) offender, "LIGHT_WEAPONS");
            if (profile != null){
                if (profile instanceof LightWeaponsProfile){
                    return ((LightWeaponsProfile) profile).getDamageMultiplier();
                }
            }
        }
        return 1;
    }
}
