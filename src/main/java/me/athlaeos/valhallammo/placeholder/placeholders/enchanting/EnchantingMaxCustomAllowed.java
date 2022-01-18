package me.athlaeos.valhallammo.placeholder.placeholders;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.enchanting.EnchantingProfile;
import org.bukkit.entity.Player;

public class EnchantingMaxCustomAllowed extends Placeholder {

    public EnchantingMaxCustomAllowed(String placeholder) {
        super(placeholder);
    }

    @Override
    public String parse(String s, Player p) {
        Profile profile = ProfileUtil.getProfile(p, SkillType.ENCHANTING);
        if (profile != null){
            if (profile instanceof EnchantingProfile){
                int maxAllowed;
                maxAllowed = ((EnchantingProfile) profile).getMaxCustomEnchantmentsAllowed();
                return s.replace(this.placeholder, String.format("%,d", maxAllowed));
            }
        }
        return s;
    }
}
