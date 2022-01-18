package me.athlaeos.valhallammo.perkrewards.farming;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import org.bukkit.entity.Player;

public class FarmingInstantDropSetReward extends PerkReward {
    private boolean enable = false;

    public FarmingInstantDropSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.FARMING);
        if (profile == null) return;
        if (profile instanceof FarmingProfile){
            FarmingProfile farmingProfile = (FarmingProfile) profile;
            farmingProfile.setInstantHarvesting(enable);
            ProfileUtil.setProfile(player, farmingProfile, SkillType.FARMING);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Boolean){
                enable = (boolean) argument;
            }
            if (argument instanceof String){
                try {
                    enable = Boolean.parseBoolean((String) argument);
                } catch (IllegalArgumentException ignored){
                }
            }
        }
    }
}
