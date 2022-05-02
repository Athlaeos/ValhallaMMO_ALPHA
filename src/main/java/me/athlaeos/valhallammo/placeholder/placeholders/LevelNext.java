package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.entity.Player;

public class LevelNext extends Placeholder {
    private final String skillType;

    public LevelNext(String placeholder, String skillType) {
        super(placeholder);
        this.skillType = skillType;
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, skillType);
        if (profile != null){
            int nextLevel = profile.getLevel() + 1;
            Skill skill = SkillProgressionManager.getInstance().getSkill(skillType);
            if (skill != null){
                if (nextLevel > skill.getMax_level()) {
                    String translation = TranslationManager.getInstance().getTranslation("max_level");
                    if (translation == null) return s.replace(this.placeholder, "");
                    return s.replace(this.placeholder, translation);
                } else {
                    return s.replace(this.placeholder, String.format("%,d", nextLevel));
                }
            }
        }
        return s;
    }
}
