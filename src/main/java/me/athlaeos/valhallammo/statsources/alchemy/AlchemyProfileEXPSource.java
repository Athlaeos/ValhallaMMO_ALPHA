package me.athlaeos.valhallammo.statsources.alchemy;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AlchemyProfileEXPSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getProfile((Player) p, "ALCHEMY");
            if (profile == null) return 0;
            if (!(profile instanceof AlchemyProfile)) return 0;
            AlchemyProfile alchemyProfile = (AlchemyProfile) profile;
            return alchemyProfile.getBrewingEXPMultiplier();
        }
        return 0;
    }
}
