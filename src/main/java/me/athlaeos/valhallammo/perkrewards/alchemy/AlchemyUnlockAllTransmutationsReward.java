package me.athlaeos.valhallammo.perkrewards.alchemy;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.managers.TransmutationManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class UnlockAllTransmutationsReward extends PerkReward {
    private final List<String> transmutationsToUnlock = new ArrayList<>();
    /**
     * Constructor for UnlockAllTransmutationsReward, which unlocks a number of transmutations for the player to make use of
     * with transmutation liquid.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount of points to add to the player. Must be Integer or Double. If Double, it's cast to int.
     */
    public UnlockAllTransmutationsReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.ALCHEMY);
        if (profile == null) return;
        if (profile instanceof AlchemyProfile){
            AlchemyProfile accountProfile = (AlchemyProfile) profile;
            Collection<String> unlockedTransmutations = accountProfile.getUnlockedTransmutations();
            unlockedTransmutations.addAll(TransmutationManager.getInstance().getTransmutations().keySet());
            accountProfile.setUnlockedTransmutations(unlockedTransmutations);
            ProfileUtil.setProfile(player, accountProfile, SkillType.ALCHEMY);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
    }
}
