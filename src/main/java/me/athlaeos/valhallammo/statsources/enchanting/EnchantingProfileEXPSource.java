package me.athlaeos.valhallammo.statsources.enchanting;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.EnchantmentType;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.enchanting.EnchantingProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EnchantingProfileEXPSource extends AccumulativeStatSource {
    private final EnchantmentType enchantmentType;

    public EnchantingProfileEXPSource(EnchantmentType m){
        this.enchantmentType = m;
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileManager.getProfile((Player) p, "ENCHANTING");
            if (profile == null) return 0;
            if (!(profile instanceof EnchantingProfile)) return 0;
            EnchantingProfile enchantingProfile = (EnchantingProfile) profile;
            return enchantingProfile.getEnchantingExpMultiplier(enchantmentType);
        }
        return 0;
    }
}
