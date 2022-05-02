package me.athlaeos.valhallammo.placeholder.placeholders.farming;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import org.bukkit.entity.Player;

public class FarmingInstantHarvestingUnlocked extends Placeholder {
    private final String unlocked;
    private final String locked;

    public FarmingInstantHarvestingUnlocked(String placeholder) {
        super(placeholder);
        this.unlocked = TranslationManager.getInstance().getTranslation("translation_true");
        this.locked = TranslationManager.getInstance().getTranslation("translation_false");
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "FARMING");
        if (profile != null){
            if (profile instanceof FarmingProfile){
                boolean unlocked = ((FarmingProfile) profile).isInstantHarvestingUnlocked();
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
