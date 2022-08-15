package me.athlaeos.valhallammo.statsources.landscaping;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.landscaping.LandscapingProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class LandscapingProfileWoodstrippingEXPSource extends AccumulativeStatSource {

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) p, "LANDSCAPING");
            if (profile == null) return 0;
            if (!(profile instanceof LandscapingProfile)) return 0;
            LandscapingProfile landscapingProfile = (LandscapingProfile) profile;
            return landscapingProfile.getWoodstrippingExpMultiplier();
        }
        return 0;
    }
}
