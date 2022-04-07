package me.athlaeos.valhallammo.placeholder.placeholders.landscaping;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.landscaping.LandscapingProfile;
import org.bukkit.entity.Player;

public class LandscapingSaplingReplaceUnlocked extends Placeholder {
    private final String unlocked;
    private final String locked;

    public LandscapingSaplingReplaceUnlocked(String placeholder) {
        super(placeholder);
        this.unlocked = TranslationManager.getInstance().getTranslation("translation_true");
        this.locked = TranslationManager.getInstance().getTranslation("translation_false");
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getProfile(p, "LANDSCAPING");
        if (profile != null){
            if (profile instanceof LandscapingProfile){
                boolean unlocked = ((LandscapingProfile) profile).isReplaceSaplings();
                if (unlocked){
                    return s.replace(this.placeholder, this.unlocked);
                } else {
                    return s.replace(this.placeholder, this.locked);
                }
            }
        }
        return s;
    }
}
