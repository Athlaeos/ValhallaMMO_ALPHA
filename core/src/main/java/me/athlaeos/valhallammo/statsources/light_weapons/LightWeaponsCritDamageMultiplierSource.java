package me.athlaeos.valhallammo.statsources.light_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.lightweapons.LightWeaponsProfile;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LightWeaponsCritDamageMultiplierSource extends EvEAccumulativeStatSource {

    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        if (offender instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) offender, "LIGHT_WEAPONS");
            if (profile == null) return 0;
            if (!(profile instanceof LightWeaponsProfile)) return 0;
            LightWeaponsProfile lightWeaponsProfile = (LightWeaponsProfile) profile;
            return lightWeaponsProfile.getCritDamageMultiplier();
        }
        return 0;
    }
}
