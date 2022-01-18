package me.athlaeos.valhallammo.statsources;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class ProfileExpGainSource extends AccumulativeStatSource {
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileUtil.getProfile((Player) p, SkillType.ACCOUNT);
            if (profile == null) {
                return 0;
            }
            if (!(profile instanceof AccountProfile)) {
                return 0;
            }
            AccountProfile alchemyProfile = (AccountProfile) profile;
            return alchemyProfile.getAllSkillEXPGain();
        }
        return 0;
    }
}
