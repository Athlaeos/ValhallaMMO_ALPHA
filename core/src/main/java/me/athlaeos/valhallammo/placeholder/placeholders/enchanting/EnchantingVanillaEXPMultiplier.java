package me.athlaeos.valhallammo.placeholder.placeholders.enchanting;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.enchanting.EnchantingProfile;
import org.bukkit.entity.Player;

public class EnchantingVanillaEXPMultiplier extends Placeholder {

    public EnchantingVanillaEXPMultiplier(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "ENCHANTING");
        if (profile != null){
            if (profile instanceof EnchantingProfile){
                double chance = ((EnchantingProfile) profile).getVanillaExpGainMultiplier() * 100;
                return s.replace(this.placeholder, String.format("%,.1f", chance));
            }
        }
        return s;
    }
}
