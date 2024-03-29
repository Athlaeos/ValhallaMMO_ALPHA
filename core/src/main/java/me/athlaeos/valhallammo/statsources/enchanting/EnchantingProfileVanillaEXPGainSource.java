package me.athlaeos.valhallammo.statsources.enchanting;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.enchanting.EnchantingProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EnchantingProfileVanillaEXPGainSource extends AccumulativeStatSource {

    public EnchantingProfileVanillaEXPGainSource(){
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getManager().getProfile((Player) p, "ENCHANTING");
            if (profile == null) return 1;
            if (!(profile instanceof EnchantingProfile)) return 1;
            EnchantingProfile enchantingProfile = (EnchantingProfile) profile;
            return enchantingProfile.getVanillaExpGainMultiplier();
        }
        return 1;
    }
}
