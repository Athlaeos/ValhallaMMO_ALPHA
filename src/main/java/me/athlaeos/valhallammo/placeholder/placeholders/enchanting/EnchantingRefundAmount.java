package me.athlaeos.valhallammo.placeholder.placeholders.enchanting;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.enchanting.EnchantingProfile;
import org.bukkit.entity.Player;

public class EnchantingRefundAmount extends Placeholder {

    public EnchantingRefundAmount(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getProfile(p, "ENCHANTING");
        if (profile != null){
            if (profile instanceof EnchantingProfile){
                double chance = ((EnchantingProfile) profile).getExpRefundFraction() * 100;
                return s.replace(this.placeholder, String.format("%,.1f", chance));
            }
        }
        return s;
    }
}
