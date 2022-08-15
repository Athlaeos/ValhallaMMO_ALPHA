package me.athlaeos.valhallammo.placeholder.placeholders.heavy_armor;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.heavyarmor.HeavyArmorProfile;
import org.bukkit.entity.Player;

public class HeavyArmorRageLevel extends Placeholder {
    public HeavyArmorRageLevel(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "HEAVY_ARMOR");
        if (profile != null){
            if (profile instanceof HeavyArmorProfile){
                int level = ((HeavyArmorProfile) profile).getRageLevel();
                if (level == -1){
                    return s.replace(this.placeholder, "-");
                }
                return s.replace(this.placeholder, "" + level);
            }
        }
        return s;
    }
}
