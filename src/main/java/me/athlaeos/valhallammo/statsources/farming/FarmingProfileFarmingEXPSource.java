package me.athlaeos.valhallammo.statsources.smithing;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.smithing.SmithingProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SmithingProfileEXPSource extends AccumulativeStatSource {
    private final MaterialClass materialClass;

    public SmithingProfileEXPSource(MaterialClass m){
        this.materialClass = m;
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileUtil.getProfile((Player) p, SkillType.SMITHING);
            if (profile == null) return 0;
            if (!(profile instanceof SmithingProfile)) return 0;
            SmithingProfile smithingProfile = (SmithingProfile) profile;
            if (materialClass == null){
                return smithingProfile.getGeneralCraftingExpMultiplier();
            } else {
                return smithingProfile.getCraftingEXPMultiplier(materialClass);
            }
        }
        return 0;
    }
}
