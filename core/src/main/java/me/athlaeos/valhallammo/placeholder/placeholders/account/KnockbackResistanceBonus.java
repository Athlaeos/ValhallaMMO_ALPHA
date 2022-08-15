package me.athlaeos.valhallammo.placeholder.placeholders.account;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import org.bukkit.entity.Player;

public class KnockbackResistanceBonus extends Placeholder {
    public KnockbackResistanceBonus(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileManager.getManager().getProfile(p, "ACCOUNT");
        if (profile != null){
            if (profile instanceof AccountProfile){
                float resistanceBonus = ((AccountProfile) profile).getKnockbackResistanceBonus();
                double percentage = resistanceBonus * 100D;
                return s.replace(this.placeholder, String.format("%,.1f", percentage));
            }
        }
        return s;
    }
}
