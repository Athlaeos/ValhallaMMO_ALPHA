package me.athlaeos.valhallammo.statsources.farming;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class FarmingProfileAnimalDamageMultiplierSource extends EvEAccumulativeStatSource {

    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) p, "FARMING");
            if (profile == null) return 1;
            if (!(profile instanceof FarmingProfile)) return 1;
            FarmingProfile farmingProfile = (FarmingProfile) profile;
            return farmingProfile.getAnimalDamageMultiplier();
        }
        return 1;
    }
}
