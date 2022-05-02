package me.athlaeos.valhallammo.placeholder.placeholders.mining;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.mining.MiningProfile;
import org.bukkit.entity.Player;

public class QuickMineHungerDrainSpeed extends Placeholder {
    public QuickMineHungerDrainSpeed(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "MINING");
        if (profile != null){
            if (profile instanceof MiningProfile){
                return s.replace(this.placeholder, ((MiningProfile) profile).getQuickMineHungerDrainSpeed() + "");
            }
        }
        return s;
    }
}
