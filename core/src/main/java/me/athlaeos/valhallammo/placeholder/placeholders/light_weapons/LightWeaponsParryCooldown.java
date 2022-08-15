package me.athlaeos.valhallammo.placeholder.placeholders.light_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.lightweapons.LightWeaponsProfile;
import org.bukkit.entity.Player;

public class LightWeaponsParryCooldown extends Placeholder {
    public LightWeaponsParryCooldown(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "LIGHT_WEAPONS");
        if (profile != null){
            if (profile instanceof LightWeaponsProfile){
                int frame = ((LightWeaponsProfile) profile).getParryCooldown();
                if (frame == -1){
                    return s.replace(this.placeholder, "0");
                }
                return s.replace(this.placeholder, String.format("%.1f", frame / 1000D));
            }
        }
        return s;
    }
}
