package me.athlaeos.valhallammo.statsources.heavy_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.heavyweapons.HeavyWeaponsProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class HeavyWeaponsProfileRareDropMultiplierSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) p, "HEAVY_WEAPONS");
            if (profile != null){
                if (profile instanceof HeavyWeaponsProfile){
                    return ((HeavyWeaponsProfile) profile).getRareDropMultiplier();
                }
            }
        }
        return 0;
    }
}
