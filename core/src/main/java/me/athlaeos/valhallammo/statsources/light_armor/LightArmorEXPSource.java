package me.athlaeos.valhallammo.statsources.light_armor;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.lightarmor.LightArmorProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LightArmorEXPSource extends AccumulativeStatSource {

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) p, "LIGHT_ARMOR");
            if (profile == null) return 0;
            if (!(profile instanceof LightArmorProfile)) return 0;
            LightArmorProfile lightArmorProfile = (LightArmorProfile) profile;
            return lightArmorProfile.getExpMultiplier();
        }
        return 0;
    }
}
