package me.athlaeos.valhallammo.perkrewards.alchemy;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class AlchemyLockTransmutationsReward extends PerkReward {
    private final List<String> transmutationsToLock = new ArrayList<>();
    /**
     * Constructor for LockTransmutationsReward, which locks a number of transmutations away from the player for usage
     * with transmutation liquid.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount of points to add to the player. Must be Integer or Double. If Double, it's cast to int.
     */
    public AlchemyLockTransmutationsReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getProfile(player, "ALCHEMY");
        if (profile == null) return;
        if (profile instanceof AlchemyProfile){
            AlchemyProfile accountProfile = (AlchemyProfile) profile;
            Collection<String> unlockedTransmutations = accountProfile.getUnlockedTransmutations();
            for (String r : transmutationsToLock){
                unlockedTransmutations.remove(r);
            }
            accountProfile.setUnlockedTransmutations(unlockedTransmutations);
            ProfileManager.setProfile(player, accountProfile, "ALCHEMY");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Collection){
                transmutationsToLock.addAll((Collection<String>) argument);
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.STRING_LIST;
    }
}
