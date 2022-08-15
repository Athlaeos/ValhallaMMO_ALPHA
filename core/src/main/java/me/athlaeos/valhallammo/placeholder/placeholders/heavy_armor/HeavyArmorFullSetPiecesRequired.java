package me.athlaeos.valhallammo.placeholder.placeholders.heavy_armor;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.heavyarmor.HeavyArmorProfile;
import org.bukkit.entity.Player;

public class HeavyArmorFullSetPiecesRequired extends Placeholder {
    public HeavyArmorFullSetPiecesRequired(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "HEAVY_ARMOR");
        if (profile != null){
            if (profile instanceof HeavyArmorProfile){
                int required = ((HeavyArmorProfile) profile).getArmorPiecesForBonusses();
                if (required < 0 || required > 4){
                    return s.replace(this.placeholder, "-");
                }
                return s.replace(this.placeholder, "" + required);
            }
        }
        return s;
    }
}
