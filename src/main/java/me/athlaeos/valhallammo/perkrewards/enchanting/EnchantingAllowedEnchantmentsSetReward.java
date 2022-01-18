package me.athlaeos.valhallammo.perkrewards.enchanting;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.enchanting.EnchantingProfile;
import org.bukkit.entity.Player;

public class EnchantingAllowedEnchantmentsAddReward extends PerkReward {
    private int amount = 0;

    public EnchantingAllowedEnchantmentsAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.ENCHANTING);
        if (profile == null) return;
        if (profile instanceof EnchantingProfile){
            EnchantingProfile enchantingProfile = (EnchantingProfile) profile;
            enchantingProfile.setMaxCustomEnchantmentsAllowed(enchantingProfile.getMaxCustomEnchantmentsAllowed() + amount);
            ProfileUtil.setProfile(player, enchantingProfile, SkillType.ENCHANTING);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                amount = (Integer) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                amount = (int) temp;
            }
        }
    }
}
