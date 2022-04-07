package me.athlaeos.valhallammo.placeholder.placeholders.farming;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import org.bukkit.entity.Player;

public class FarmingFishingEXPMultiplier extends Placeholder {
    public FarmingFishingEXPMultiplier(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getProfile(p, "FARMING");
        if (profile != null){
            if (profile instanceof FarmingProfile){
                double multiplier = ((FarmingProfile) profile).getFishingExpMultiplier();
                return s.replace(this.placeholder, String.format("%,.2f", multiplier));
            }
        }
        return s;
    }
}
