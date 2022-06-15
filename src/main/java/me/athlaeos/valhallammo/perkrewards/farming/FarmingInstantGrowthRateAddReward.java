package me.athlaeos.valhallammo.perkrewards.farming;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import org.bukkit.entity.Player;

public class FarmingInstantGrowthRateAddReward extends PerkReward {
    private float rate = 0F;

    public FarmingInstantGrowthRateAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "FARMING");
        if (profile == null) return;
        if (profile instanceof FarmingProfile){
            FarmingProfile farmingProfile = (FarmingProfile) profile;
            farmingProfile.setInstantGrowthRate(farmingProfile.getInstantGrowthRate() + rate);
            ProfileManager.getManager().setProfile(player, farmingProfile, "FARMING");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Float){
                rate = (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                rate = (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.DOUBLE;
    }
}
