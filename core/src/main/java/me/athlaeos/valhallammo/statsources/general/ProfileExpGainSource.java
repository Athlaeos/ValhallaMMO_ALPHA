package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ProfileExpGainSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) p, "ACCOUNT");
            if (profile == null) {
                return 0;
            }
            if (!(profile instanceof AccountProfile)) {
                return 0;
            }
            AccountProfile accountProfile = (AccountProfile) profile;
            return accountProfile.getAllSkillEXPGain();
        }
        return 0;
    }
}
