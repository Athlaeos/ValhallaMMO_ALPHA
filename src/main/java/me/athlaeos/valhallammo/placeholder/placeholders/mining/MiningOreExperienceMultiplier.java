package me.athlaeos.valhallammo.placeholder.placeholders.mining;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.mining.MiningProfile;
import org.bukkit.entity.Player;

public class MiningOreExperienceMultiplier extends Placeholder {
    public MiningOreExperienceMultiplier(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getProfile(p, "MINING");
        if (profile != null){
            if (profile instanceof MiningProfile){
                float multiplier = ((MiningProfile) profile).getOreExperienceMultiplier();
                return s.replace(this.placeholder, String.format("%,.2f", multiplier));
            }
        }
        return s;
    }
}
