package me.athlaeos.valhallammo.perkrewards.mining;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.mining.MiningProfile;
import org.bukkit.entity.Player;

public class MiningMiningRareDropMultiplierSetReward extends PerkReward {
    private float multiplier = 0F;

    public MiningMiningRareDropMultiplierSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getProfile(player, "MINING");
        if (profile == null) return;
        if (profile instanceof MiningProfile){
            MiningProfile miningProfile = (MiningProfile) profile;
            miningProfile.setMiningRareDropRateMultiplier(multiplier);
            ProfileManager.setProfile(player, miningProfile, "MINING");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Float){
                multiplier = (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                multiplier = (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.DOUBLE;
    }
}
