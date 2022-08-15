package me.athlaeos.valhallammo.perkrewards.landscaping;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.BlockConversionManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.landscaping.LandscapingProfile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class LandscapingLockConversionsReward extends PerkReward {
    private final List<String> conversionsToLock = new ArrayList<>();
    /**
     * Constructor for LockTransmutationsReward, which locks a number of transmutations away from the player for usage
     * with transmutation liquid.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount of points to add to the player. Must be Integer or Double. If Double, it's cast to int.
     */
    public LandscapingLockConversionsReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "LANDSCAPING");
        if (profile == null) return;
        if (profile instanceof LandscapingProfile){
            LandscapingProfile landscapingProfile = (LandscapingProfile) profile;
            Collection<String> unlockedConversions = landscapingProfile.getUnlockedConversions();
            if (unlockedConversions == null) unlockedConversions = new HashSet<>();
            for (String r : conversionsToLock){
                unlockedConversions.remove(r);
            }
            landscapingProfile.setUnlockedConversions(unlockedConversions);
            ProfileManager.getManager().setProfile(player, landscapingProfile, "LANDSCAPING");
        }
    }

    @Override
    public List<String> getTabAutoComplete(String currentArg) {
        if (currentArg.endsWith(";") || currentArg.equals("")){
            return BlockConversionManager.getInstance().getAllConversions().keySet().stream().filter(s -> !currentArg.contains(s)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Collection){
                conversionsToLock.addAll((Collection<String>) argument);
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.STRING_LIST;
    }
}
