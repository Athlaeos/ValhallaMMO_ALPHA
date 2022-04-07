package me.athlaeos.valhallammo.perkrewards.landscaping;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.landscaping.LandscapingProfile;
import org.bukkit.entity.Player;

public class LandscapingWoodstrippingRareDropMultiplierAddReward extends PerkReward {
    private float multiplier = 0F;

    public LandscapingWoodstrippingRareDropMultiplierAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getProfile(player, "LANDSCAPING");
        if (profile == null) return;
        if (profile instanceof LandscapingProfile){
            LandscapingProfile landscapingProfile = (LandscapingProfile) profile;
            landscapingProfile.setWoodstrippingRareDropRateMultiplier(landscapingProfile.getWoodstrippingRareDropRateMultiplier() + multiplier);
            ProfileManager.setProfile(player, landscapingProfile, "LANDSCAPING");
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
