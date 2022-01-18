package me.athlaeos.valhallammo.placeholders;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.dom.SkillType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import org.bukkit.entity.Player;

public class EXPCurrent extends Placeholder{
    private final SkillType skillType;

    public EXPCurrent(String placeholder, SkillType skillType) {
        super(placeholder);
        this.skillType = skillType;
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileUtil.getProfile(p, skillType);
        if (profile != null){
            return s.replace(this.placeholder, String.format("%,d", (int) Math.floor(profile.getExp())));
        }
        return s;
    }
}
