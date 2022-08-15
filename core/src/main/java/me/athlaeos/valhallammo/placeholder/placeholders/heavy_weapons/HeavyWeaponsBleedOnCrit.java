package me.athlaeos.valhallammo.placeholder.placeholders.heavy_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.heavyweapons.HeavyWeaponsProfile;
import org.bukkit.entity.Player;

public class HeavyWeaponsBleedOnCrit extends Placeholder {
    private final String unlocked;
    private final String locked;

    public HeavyWeaponsBleedOnCrit(String placeholder) {
        super(placeholder);
        this.unlocked = TranslationManager.getInstance().getTranslation("translation_true");
        this.locked = TranslationManager.getInstance().getTranslation("translation_false");
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "HEAVY_WEAPONS");
        if (profile != null){
            if (profile instanceof HeavyWeaponsProfile){
                boolean enabled = ((HeavyWeaponsProfile) profile).isBleedOnCrit();
                if (enabled){
                    return s.replace(this.placeholder, this.unlocked);
                } else {
                    return s.replace(this.placeholder, this.locked);
                }
            }
        }
        return s;
    }
}
