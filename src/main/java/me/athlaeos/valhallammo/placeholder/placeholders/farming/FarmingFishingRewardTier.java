package me.athlaeos.valhallammo.placeholder.placeholders.farming;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import org.bukkit.entity.Player;

public class FarmingFishingTimeMultiplier extends Placeholder {
    public FarmingFishingTimeMultiplier(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileUtil.getProfile(p, SkillType.FARMING);
        if (profile != null){
            if (profile instanceof FarmingProfile){
                float multiplier = ((FarmingProfile) profile).getFishingTimeMultiplier();
                return s.replace(this.placeholder, String.format("%,.1f", multiplier));
            }
        }
        return s;
    }
}
