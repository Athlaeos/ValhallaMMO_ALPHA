package me.athlaeos.valhallammo.placeholder.placeholders.heavy_armor;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.heavyarmor.HeavyArmorProfile;
import org.bukkit.entity.Player;

public class HeavyArmorFallDamageResistance extends Placeholder {
    public HeavyArmorFallDamageResistance(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "HEAVY_ARMOR");
        if (profile != null){
            if (profile instanceof HeavyArmorProfile){
                float resistance = ((HeavyArmorProfile) profile).getFallDamageResistance();
                double chance = resistance * 100D;
                return s.replace(this.placeholder, String.format("%,.1f", chance));
            }
        }
        return s;
    }
}
