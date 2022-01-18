package me.athlaeos.valhallammo.statsources.mining;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.mining.MiningProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class MiningProfileBlastExplosionDamageMultiplierSource extends AccumulativeStatSource {

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileUtil.getProfile((Player) p, SkillType.MINING);
            if (profile == null) return 0;
            if (!(profile instanceof MiningProfile)) return 0;
            MiningProfile miningProfile = (MiningProfile) profile;
            return miningProfile.getTntExplosionDamageMultiplier();
        }
        return 0;
    }
}
