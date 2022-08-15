package me.athlaeos.valhallammo.placeholder.placeholders.heavy_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.heavyweapons.HeavyWeaponsProfile;
import org.bukkit.entity.Player;

public class HeavyWeaponsCoatingSelfDurationMultiplier extends Placeholder {
    public HeavyWeaponsCoatingSelfDurationMultiplier(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "HEAVY_WEAPONS");
        if (profile != null){
            if (profile instanceof HeavyWeaponsProfile){
                float multiplier = ((HeavyWeaponsProfile) profile).getSelfPotionDurationMultiplier();
                return s.replace(this.placeholder, multiplier <= 0 ? String.format("%d", -(int)multiplier) : String.format("%,.2fx", multiplier));
            }
        }
        return s;
    }
}
