package me.athlaeos.valhallammo.placeholder.placeholders.light_armor;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.lightarmor.LightArmorProfile;
import org.bukkit.entity.Player;

public class LightArmorAdrenalineLevel extends Placeholder {
    public LightArmorAdrenalineLevel(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "LIGHT_ARMOR");
        if (profile != null){
            if (profile instanceof LightArmorProfile){
                int level = ((LightArmorProfile) profile).getAdrenalineLevel();
                if (level == -1){
                    return s.replace(this.placeholder, "-");
                }
                return s.replace(this.placeholder, "" + level);
            }
        }
        return s;
    }
}
