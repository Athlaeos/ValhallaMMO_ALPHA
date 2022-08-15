package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.entity.Player;

public class EXPPercentProgress extends Placeholder {
    private final String skillType;

    public EXPPercentProgress(String placeholder, String skillType) {
        super(placeholder);
        this.skillType = skillType;
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, skillType);
        if (profile != null){
            Skill skill = SkillProgressionManager.getInstance().getSkill(skillType);
            if (skill != null){
                double nextLevelEXP = skill.expForlevel(profile.getLevel() + 1);
                double fraction = profile.getExp() / nextLevelEXP;
                if (nextLevelEXP == -1) fraction = 1D;
                double percent = fraction * 100;
                if (percent > 100) percent = 100D;
                return s.replace(this.placeholder, String.format("%.1f", percent));
            }
        }
        return s;
    }
}
