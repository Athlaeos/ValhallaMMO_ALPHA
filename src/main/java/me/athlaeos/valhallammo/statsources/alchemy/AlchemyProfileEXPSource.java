package me.athlaeos.valhallammo.statsources;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AlchemyProfileEXPSource extends AccumulativeStatSource{
    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileUtil.getProfile((Player) p, SkillType.ALCHEMY);
            if (profile == null) return 0;
            if (!(profile instanceof AlchemyProfile)) return 0;
            AlchemyProfile alchemyProfile = (AlchemyProfile) profile;
            return alchemyProfile.getBrewingEXPMultiplier();
        }
        return 0;
    }
}
