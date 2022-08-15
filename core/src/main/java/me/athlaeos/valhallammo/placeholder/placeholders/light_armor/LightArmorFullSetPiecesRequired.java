package me.athlaeos.valhallammo.placeholder.placeholders.light_armor;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.lightarmor.LightArmorProfile;
import org.bukkit.entity.Player;

public class LightArmorFullSetPiecesRequired extends Placeholder {
    public LightArmorFullSetPiecesRequired(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "LIGHT_ARMOR");
        if (profile != null){
            if (profile instanceof LightArmorProfile){
                int required = ((LightArmorProfile) profile).getArmorPiecesForBonusses();
                if (required < 0 || required > 4){
                    return s.replace(this.placeholder, "-");
                }
                return s.replace(this.placeholder, "" + required);
            }
        }
        return s;
    }
}
