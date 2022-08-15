package me.athlaeos.valhallammo.placeholder.placeholders.light_weapons;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.lightweapons.LightWeaponsProfile;
import org.bukkit.entity.Player;

public class LightWeaponsLightArmorDamageBonus extends Placeholder {
    public LightWeaponsLightArmorDamageBonus(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "LIGHT_WEAPONS");
        if (profile != null){
            if (profile instanceof LightWeaponsProfile){
                float bonus = ((LightWeaponsProfile) profile).getDamageBonusLightArmor();
                double chance = bonus * 100D;
                return s.replace(this.placeholder, bonus > 0 ? "+" : "" + String.format("%,.1f", chance));
            }
        }
        return s;
    }
}
