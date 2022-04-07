package me.athlaeos.valhallammo.statsources.farming;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class FarmingProfileAnimalDropMultiplierSource extends AccumulativeStatSource {

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getProfile((Player) p, "FARMING");
            if (profile == null) return 0;
            if (!(profile instanceof FarmingProfile)) return 0;
            FarmingProfile farmingProfile = (FarmingProfile) profile;
            return farmingProfile.getAnimalDropMultiplier();
        }
        return 0;
    }
}
