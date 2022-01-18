package me.athlaeos.valhallammo.perkrewards.mining;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.mining.MiningProfile;
import org.bukkit.entity.Player;

public class MiningBlockBreakEXPRewardSetReward extends PerkReward {
    private float exp = 0F;

    public MiningBlockBreakEXPRewardSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.MINING);
        if (profile == null) return;
        if (profile instanceof MiningProfile){
            MiningProfile farmingProfile = (MiningProfile) profile;
            farmingProfile.setBlockMineExperienceRate(exp);
            ProfileUtil.setProfile(player, farmingProfile, SkillType.MINING);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Float){
                exp = (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                exp = (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.DOUBLE;
    }
}
