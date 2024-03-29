package me.athlaeos.valhallammo.statsources.mining;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.mining.MiningProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MiningProfileMiningEXPSource extends AccumulativeStatSource {

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) p, "MINING");
            if (profile == null) return 0;
            if (!(profile instanceof MiningProfile)) return 0;
            MiningProfile miningProfile = (MiningProfile) profile;
            return miningProfile.getMiningExpMultiplier();
        }
        return 0;
    }
}
