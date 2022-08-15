package me.athlaeos.valhallammo.statsources.light_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.lightweapons.LightWeaponsProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LightWeaponsProfileRareDropMultiplierSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) p, "LIGHT_WEAPONS");
            if (profile != null){
                if (profile instanceof LightWeaponsProfile){
                    return ((LightWeaponsProfile) profile).getRareDropMultiplier();
                }
            }
        }
        return 0;
    }
}
