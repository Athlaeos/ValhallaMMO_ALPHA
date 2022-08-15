package me.athlaeos.valhallammo.statsources.farming;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import me.athlaeos.valhallammo.utility.EntityUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class FarmingProfileAnimalDropMultiplierSource extends EvEAccumulativeStatSource {

    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        if (offender instanceof Player && EntityUtils.EntityClassification.isMatchingClassification(entity.getType(), EntityUtils.EntityClassification.ANIMAL)){
            Profile profile = ProfileManager.getManager().getProfile((Player) offender, "FARMING");
            if (profile == null) return 0;
            if (!(profile instanceof FarmingProfile)) return 0;
            FarmingProfile farmingProfile = (FarmingProfile) profile;
            return farmingProfile.getAnimalDropMultiplier() - 1;
        }
        return 0;
    }
}
