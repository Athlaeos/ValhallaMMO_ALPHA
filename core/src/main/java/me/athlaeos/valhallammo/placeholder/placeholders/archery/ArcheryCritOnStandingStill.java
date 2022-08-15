package me.athlaeos.valhallammo.placeholder.placeholders.archery;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.archery.ArcheryProfile;
import org.bukkit.entity.Player;

public class ArcheryCritOnStandingStill extends Placeholder {
    private final String unlocked;
    private final String locked;

    public ArcheryCritOnStandingStill(String placeholder) {
        super(placeholder);
        this.unlocked = TranslationManager.getInstance().getTranslation("translation_true");
        this.locked = TranslationManager.getInstance().getTranslation("translation_false");
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "ARCHERY");
        if (profile != null){
            if (profile instanceof ArcheryProfile){
                boolean critOnStandingStill = ((ArcheryProfile) profile).isCritOnStandingStill();
                if (critOnStandingStill){
                    return s.replace(this.placeholder, this.unlocked);
                } else {
                    return s.replace(this.placeholder, this.locked);
                }
            }
        }
        return s;
    }
}
