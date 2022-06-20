package me.athlaeos.valhallammo.placeholder.placeholders.light_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.lightweapons.LightWeaponsProfile;
import org.bukkit.entity.Player;

public class LightWeaponsCoatingSelfDurationMultiplier extends Placeholder {
    public LightWeaponsCoatingSelfDurationMultiplier(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "LIGHT_WEAPONS");
        if (profile != null){
            if (profile instanceof LightWeaponsProfile){
                float multiplier = ((LightWeaponsProfile) profile).getSelfPotionDurationMultiplier();
                return s.replace(this.placeholder, multiplier <= 0 ? String.format("%d", -(int)multiplier) : String.format("%,.2fx", multiplier));
            }
        }
        return s;
    }
}
