package me.athlaeos.valhallammo.perkrewards.mining;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.mining.MiningProfile;
import org.bukkit.entity.Player;

public class MiningBlastFortuneLevelAddReward extends PerkReward {
    private int level = 0;

    public MiningBlastFortuneLevelAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getProfile(player, "MINING");
        if (profile == null) return;
        if (profile instanceof MiningProfile){
            MiningProfile miningProfile = (MiningProfile) profile;
            miningProfile.setExplosionFortuneLevel(miningProfile.getExplosionFortuneLevel() + level);
            ProfileManager.setProfile(player, miningProfile, "MINING");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                level = (int) argument;
            }
            if (argument instanceof Float){
                level = (int) (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                level = (int) (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
