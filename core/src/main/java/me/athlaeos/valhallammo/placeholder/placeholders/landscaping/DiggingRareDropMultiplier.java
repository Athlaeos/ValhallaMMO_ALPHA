package me.athlaeos.valhallammo.placeholder.placeholders.landscaping;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.landscaping.LandscapingProfile;
import org.bukkit.entity.Player;

public class DiggingRareDropMultiplier extends Placeholder {
    public DiggingRareDropMultiplier(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "LANDSCAPING");
        if (profile != null){
            if (profile instanceof LandscapingProfile){
                float multiplier = ((LandscapingProfile) profile).getDiggingRareDropRateMultiplier();
                return s.replace(this.placeholder, String.format("%,.2f", multiplier));
            }
        }
        return s;
    }
}
