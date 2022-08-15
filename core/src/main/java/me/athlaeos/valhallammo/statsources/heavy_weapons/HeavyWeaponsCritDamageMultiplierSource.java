package me.athlaeos.valhallammo.statsources.heavy_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.heavyweapons.HeavyWeaponsProfile;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class HeavyWeaponsCritDamageMultiplierSource extends EvEAccumulativeStatSource {

    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        if (offender instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) offender, "HEAVY_WEAPONS");
            if (profile == null) return 0;
            if (!(profile instanceof HeavyWeaponsProfile)) return 0;
            HeavyWeaponsProfile heavyWeaponsProfile = (HeavyWeaponsProfile) profile;
            return heavyWeaponsProfile.getCritDamageMultiplier();
        }
        return 0;
    }
}
