package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import org.bukkit.entity.Player;

public class Skillpoints extends Placeholder {

    public Skillpoints(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getProfile(p, "ACCOUNT");
        if (profile != null){
            if (profile instanceof AccountProfile){
                int points = ((AccountProfile) profile).getSpendableSkillPoints();

                return s.replace(this.placeholder, String.format("%,d", points));
            }
        }
        return s;
    }
}
