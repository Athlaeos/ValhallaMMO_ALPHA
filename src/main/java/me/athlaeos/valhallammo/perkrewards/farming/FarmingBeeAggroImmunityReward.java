package me.athlaeos.valhallammo.perkrewards.farming;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import org.bukkit.entity.Player;

public class FarmingBeeAggroImmunityReward extends PerkReward {
    private boolean enable = false;

    public FarmingBeeAggroImmunityReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getProfile(player, "FARMING");
        if (profile == null) return;
        if (profile instanceof FarmingProfile){
            FarmingProfile farmingProfile = (FarmingProfile) profile;
            farmingProfile.setHiveBeeAggroImmunity(enable);
            ProfileManager.setProfile(player, farmingProfile, "FARMING");
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

    @Override
    public ObjectType getType() {
        return ObjectType.BOOLEAN;
    }
}
