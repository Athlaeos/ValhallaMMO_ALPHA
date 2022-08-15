package me.athlaeos.valhallammo.placeholder.placeholders.heavy_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.heavyweapons.HeavyWeaponsProfile;
import org.bukkit.entity.Player;

public class HeavyWeaponsCrushingBlowChance extends Placeholder {
    public HeavyWeaponsCrushingBlowChance(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "HEAVY_WEAPONS");
        if (profile != null){
            if (profile instanceof HeavyWeaponsProfile){
                float bonus = ((HeavyWeaponsProfile) profile).getCrushingBlowChance();
                double chance = bonus * 100D;
                return s.replace(this.placeholder, String.format("%,.1f", chance));
            }
        }
        return s;
    }
}
