package me.athlaeos.valhallammo.statsources.alchemy;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class AlchemyQualityPlayerSource extends AccumulativeStatSource {
    private final PotionType potionType;

    public AlchemyQualityPlayerSource(PotionType potionType){
        this.potionType = potionType;
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileUtil.getProfile((Player) p, SkillType.ALCHEMY);
            if (profile == null) return 0;
            if (!(profile instanceof AlchemyProfile)) return 0;
            AlchemyProfile alchemyProfile = (AlchemyProfile) profile;
            if (potionType == null){
                return alchemyProfile.getGeneralBrewingQuality();
            } else {
                return alchemyProfile.getBrewingQuality(potionType);
            }
        }
        return 0;
    }
}
