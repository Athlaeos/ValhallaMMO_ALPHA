package me.athlaeos.valhallammo.perkrewards.mining;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.mining.MiningProfile;
import org.bukkit.entity.Player;

public class MiningQuickMineDrainSpeedAddReward extends PerkReward {
    private int speed = 0;

    public MiningQuickMineDrainSpeedAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "MINING");
        if (profile == null) return;
        if (profile instanceof MiningProfile){
            MiningProfile miningProfile = (MiningProfile) profile;
            miningProfile.setQuickMineHungerDrainSpeed(miningProfile.getQuickMineHungerDrainSpeed() + speed);
            ProfileManager.getManager().setProfile(player, miningProfile, "MINING");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                speed = (int) argument;
            }
            if (argument instanceof Float){
                speed = (int) (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                speed = (int) (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
