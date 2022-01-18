package me.athlaeos.valhallammo.statsources.enchanting;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.EnchantmentType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class EnchantingQualityPlayerSource extends AccumulativeStatSource {
    private final EnchantmentType enchantmentType;

    public EnchantingQualityPlayerSource(EnchantmentType potionType){
        this.enchantmentType = potionType;
    }

    @Override
    public double add(Entity p, boolean use) {
        if (p instanceof Player){
            Profile profile = ProfileUtil.getProfile((Player) p, SkillType.ALCHEMY);
            if (profile == null) return 0;
            if (!(profile instanceof AlchemyProfile)) return 0;
            AlchemyProfile alchemyProfile = (AlchemyProfile) profile;
            if (enchantmentType == null){
                return alchemyProfile.getGeneralBrewingQuality();
            } else {
                return alchemyProfile.getBrewingQuality(enchantmentType);
            }
        }
        return 0;
    }
}
