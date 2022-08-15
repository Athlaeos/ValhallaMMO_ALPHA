package me.athlaeos.valhallammo.placeholder.placeholders.light_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.lightweapons.LightWeaponsProfile;
import org.bukkit.entity.Player;

public class LightWeaponsFlatHeavyArmorIgnored extends Placeholder {
    public LightWeaponsFlatHeavyArmorIgnored(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "LIGHT_WEAPONS");
        if (profile != null){
            if (profile instanceof LightWeaponsProfile){
                float ignored = ((LightWeaponsProfile) profile).getFlatHeavyArmorIgnored();
                return s.replace(this.placeholder, String.format("%,.1f", ignored));
            }
        }
        return s;
    }
}
