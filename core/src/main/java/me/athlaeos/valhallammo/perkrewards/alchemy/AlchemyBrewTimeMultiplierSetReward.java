package me.athlaeos.valhallammo.perkrewards.alchemy;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import org.bukkit.entity.Player;

public class AlchemyBrewTimeMultiplierSetReward extends PerkReward {
    private float multiplier = 0F;
    /**
     * Constructor for BrewTimeReductionSetReward, which sets the player's brewing time multiplier to a given amount when
     * executed. A negative value reduces the time it takes for a player to brew, a positive value extends it longer.
     * A final value of 0 (or less than) makes it so potions brew instantly
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount additional brewtime reduction. Must be Float or Double. If Double, it's cast to float.
     */
    public AlchemyBrewTimeMultiplierSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "ALCHEMY");
        if (profile == null) {
            return;
        }
        if (profile instanceof AlchemyProfile){
            AlchemyProfile alchemyProfile = (AlchemyProfile) profile;
            alchemyProfile.setBrewingTimeReduction(multiplier);
            ProfileManager.getManager().setProfile(player, alchemyProfile, "ALCHEMY");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                multiplier = (float) (Integer) argument;
            }
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
