package me.athlaeos.valhallammo.perkrewards.enchanting;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.EnchantmentType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.enchanting.EnchantingProfile;
import org.bukkit.entity.Player;

public class EnchantingSkillExpGainAddReward extends PerkReward {
    private double expGain = 0D;
    private final EnchantmentType type;

    public EnchantingSkillExpGainAddReward(String name, Object argument, EnchantmentType type) {
        super(name, argument);
        this.type = type;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.ENCHANTING);
        if (profile == null) return;
        if (profile instanceof EnchantingProfile){
            EnchantingProfile enchantingProfile = (EnchantingProfile) profile;
            enchantingProfile.setEnchantingExpMultiplier(type, enchantingProfile.getEnchantingExpMultiplier(type) + expGain);
            ProfileUtil.setProfile(player, enchantingProfile, SkillType.ENCHANTING);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Double){
                this.expGain = (Double) argument;
            }
            if (argument instanceof Integer){
                expGain = (Integer) argument;
            }
        }
    }
}
