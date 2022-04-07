package me.athlaeos.valhallammo.placeholder.placeholders.farming;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import org.bukkit.entity.Player;

public class FarmingFarmingVanillaEXPReward extends Placeholder {
    public FarmingFarmingVanillaEXPReward(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getProfile(p, "FARMING");
        if (profile != null){
            if (profile instanceof FarmingProfile){
                float reward = ((FarmingProfile) profile).getFarmingVanillaExpReward();
                return s.replace(this.placeholder, String.format("%,.2f", reward));
            }
        }
        return s;
    }
}
