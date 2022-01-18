package me.athlaeos.valhallammo.perkrewards.mining;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.mining.MiningProfile;
import org.bukkit.entity.Player;

public class MiningMiningEXPMultiplierAddReward extends PerkReward {
    private double multiplier = 0D;

    public MiningMiningEXPMultiplierAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.MINING);
        if (profile == null) return;
        if (profile instanceof MiningProfile){
            MiningProfile farmingProfile = (MiningProfile) profile;
            farmingProfile.setMiningExpMultiplier(farmingProfile.getMiningExpMultiplier() + multiplier);
            ProfileUtil.setProfile(player, farmingProfile, SkillType.MINING);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Float){
                multiplier = (double) (float) argument;
            }
            if (argument instanceof Double){
                multiplier = (Double) argument;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.DOUBLE;
    }
}
