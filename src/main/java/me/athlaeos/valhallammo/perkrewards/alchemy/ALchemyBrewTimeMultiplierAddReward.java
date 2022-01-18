package me.athlaeos.valhallammo.perkrewards.alchemy;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import org.bukkit.entity.Player;

public class BrewTimeMultiplierAddReward extends PerkReward {
    private float multiplier = 0F;
    /**
     * Constructor for BrewTimeReductionAddReward, which adds to the player's brewing time multiplier when executed.
     * A negative value reduces the time it takes for a player to brew, a positive value extends it longer.
     * A final value of 0 (or less than) makes it so potions brew instantly
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount additional brewtime reduction. Must be Float or Double. If Double, it's cast to float.
     */
    public BrewTimeMultiplierAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.ALCHEMY);
        if (profile == null) return;
        if (profile instanceof AlchemyProfile){
            AlchemyProfile alchemyProfile = (AlchemyProfile) profile;
            alchemyProfile.setBrewingTimeReduction(alchemyProfile.getBrewingTimeMultiplier() + multiplier);
            ProfileUtil.setProfile(player, alchemyProfile, SkillType.ALCHEMY);
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
}
