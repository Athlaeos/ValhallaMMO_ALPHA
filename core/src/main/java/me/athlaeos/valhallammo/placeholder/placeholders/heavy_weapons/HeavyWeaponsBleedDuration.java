package me.athlaeos.valhallammo.placeholder.placeholders.heavy_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.heavyweapons.HeavyWeaponsProfile;
import org.bukkit.entity.Player;

public class HeavyWeaponsBleedDuration extends Placeholder {
    public HeavyWeaponsBleedDuration(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "HEAVY_WEAPONS");
        if (profile != null){
            if (profile instanceof HeavyWeaponsProfile){
                int frame = ((HeavyWeaponsProfile) profile).getBleedDuration();
                if (frame == -1){
                    return s.replace(this.placeholder, "0");
                }
                return s.replace(this.placeholder, String.format("%.1f", frame / 1000D));
            }
        }
        return s;
    }
}
