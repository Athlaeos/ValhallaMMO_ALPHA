package me.athlaeos.valhallammo.statsources.enchanting;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.enchanting.EnchantingProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EnchantingProfileLapisSaveChanceSource extends AccumulativeStatSource {
    public EnchantingProfileLapisSaveChanceSource(){
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileUtil.getProfile((Player) p, SkillType.ENCHANTING);
            if (profile == null) return 0;
            if (!(profile instanceof EnchantingProfile)) return 0;
            EnchantingProfile enchantingProfile = (EnchantingProfile) profile;
            return enchantingProfile.getLapisSaveChance();
        }
        return 0;
    }
}
