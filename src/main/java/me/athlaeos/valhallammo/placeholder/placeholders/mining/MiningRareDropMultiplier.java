package me.athlaeos.valhallammo.placeholder.placeholders.mining;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.mining.MiningProfile;
import org.bukkit.entity.Player;

public class MiningRareDropMultiplier extends Placeholder {
    public MiningRareDropMultiplier(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getProfile(p, "MINING");
        if (profile != null){
            if (profile instanceof MiningProfile){
                float multiplier = ((MiningProfile) profile).getMiningRareDropRateMultiplier();
                return s.replace(this.placeholder, String.format("%,.2f", multiplier));
            }
        }
        return s;
    }
}
