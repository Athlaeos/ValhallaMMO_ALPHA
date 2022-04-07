package me.athlaeos.valhallammo.perkrewards.landscaping;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.landscaping.LandscapingProfile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class LandscapingUnlockConversionsReward extends PerkReward {
    private final List<String> conversionsToUnlock = new ArrayList<>();
    /**
     * Constructor for LockTransmutationsReward, which unlocks a number of transmutations for the player to make use of
     * with transmutation liquid.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount of points to add to the player. Must be Integer or Double. If Double, it's cast to int.
     */
    public LandscapingUnlockConversionsReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getProfile(player, "LANDSCAPING");
        if (profile == null) return;
        if (profile instanceof LandscapingProfile){
            LandscapingProfile landscapingProfile = (LandscapingProfile) profile;
            Collection<String> unlockedConversions = landscapingProfile.getUnlockedConversions();
            if (unlockedConversions == null) unlockedConversions = new HashSet<>();
            unlockedConversions.addAll(conversionsToUnlock);
            landscapingProfile.setUnlockedConversions(unlockedConversions);
            ProfileManager.setProfile(player, landscapingProfile, "LANDSCAPING");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Collection){
                conversionsToUnlock.addAll((Collection<String>) argument);
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.STRING_LIST;
    }
}
