package me.athlaeos.valhallammo.perkrewards.farming;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import org.bukkit.entity.Player;

public class FarmingBreedingVanillaEXPMultiplierAddReward extends PerkReward {
    private float exp = 0F;

    public FarmingBreedingVanillaEXPMultiplierAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getProfile(player, "FARMING");
        if (profile == null) return;
        if (profile instanceof FarmingProfile){
            FarmingProfile farmingProfile = (FarmingProfile) profile;
            farmingProfile.setBreedingVanillaExpMultiplier(farmingProfile.getBreedingVanillaExpMultiplier() + exp);
            ProfileManager.setProfile(player, farmingProfile, "FARMING");
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
