package me.athlaeos.valhallammo.statsources.archery;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.archery.ArcheryProfile;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import me.athlaeos.valhallammo.utility.EntityUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ArcheryProfileStunChanceSource extends EvEAccumulativeStatSource {

    @Override
    public double add(Entity p, boolean use) {
        return 0;
    }

    @Override
    public double add(Entity entity, Entity offender, boolean use) {
        Player p = EntityUtils.getPlayerFromEntity(offender);
        if (p != null){
            Profile profile = ProfileManager.getManager().getProfile(p, "ARCHERY");
            if (profile == null) return 0;
            if (!(profile instanceof ArcheryProfile)) return 0;
            ArcheryProfile archeryProfile = (ArcheryProfile) profile;
            return archeryProfile.getStunChance();
        }
        return 0;
    }
}
