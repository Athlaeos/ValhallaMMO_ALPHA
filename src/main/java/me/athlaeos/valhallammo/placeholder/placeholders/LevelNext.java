package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.dom.Skill;
import me.athlaeos.valhallammo.dom.SkillType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import org.bukkit.entity.Player;

public class LevelNext extends Placeholder {
    private final SkillType skillType;

    public LevelNext(String placeholder, SkillType skillType) {
        super(placeholder);
        this.skillType = skillType;
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileUtil.getProfile(p, skillType);
        if (profile != null){
            Skill skill = SkillProgressionManager.getInstance().getSkill(skillType);
            if (skill != null){
                int nextLevelEXP = ((int) skill.expForNextlevel(profile.getLevel() + 1));
                return s.replace(this.placeholder, String.format("%,d", nextLevelEXP));
            }
        }
        return s;
    }
}
