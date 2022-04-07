package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import org.bukkit.entity.Player;

public class EXPTotal extends Placeholder {
    private final String skillType;

    public EXPTotal(String placeholder, String skillType) {
        super(placeholder);
        this.skillType = skillType;
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getProfile(p, skillType);
        if (profile != null){
            return s.replace(this.placeholder, String.format("%,d", (int) Math.floor(profile.getLifetimeEXP())));
        }
        return s;
    }
}
