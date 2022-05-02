package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.entity.Player;

public class EXPNextLevel extends Placeholder {
    private final String skillType;

    public EXPNextLevel(String placeholder, String skillType) {
        super(placeholder);
        this.skillType = skillType;
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, skillType);
        if (profile != null){
            Skill skill = SkillProgressionManager.getInstance().getSkill(skillType);
            if (skill != null){
                int nextLevelEXP = ((int) skill.expForlevel(profile.getLevel() + 1));
                String translation = String.format("%,d", nextLevelEXP);
                if (nextLevelEXP == -1) translation = TranslationManager.getInstance().getTranslation("max_level");
                return s.replace(this.placeholder, translation);
            }
        }
        return s;
    }
}
