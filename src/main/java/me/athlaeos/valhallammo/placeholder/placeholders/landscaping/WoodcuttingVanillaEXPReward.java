package me.athlaeos.valhallammo.placeholder.placeholders.landscaping;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.landscaping.LandscapingProfile;
import org.bukkit.entity.Player;

public class WoodcuttingVanillaEXPReward extends Placeholder {
    public WoodcuttingVanillaEXPReward(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getProfile(p, "LANDSCAPING");
        if (profile != null){
            if (profile instanceof LandscapingProfile){
                float reward = ((LandscapingProfile) profile).getWoodcuttingExperienceRate();
                return s.replace(this.placeholder, String.format("%,.2f", reward));
            }
        }
        return s;
    }
}
