package me.athlaeos.valhallammo.perkrewards.alchemy;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import org.bukkit.entity.Player;

public class PotionThrowVelocityAddReward extends PerkReward {
    private float velocity = 0F;
    /**
     * Constructor for PotionThrowVelocityAddReward, which adds to the player's potion throw velocity.
     * A negative value reduces the potion's velocity when thrown, a positive value increases it.
     * A final value of 0 (or less than) makes it so the potion does not go forward and just drops to the ground.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount additional velocity. Must be Float or Double. If Double, it's cast to float.
     */
    public PotionThrowVelocityAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.ALCHEMY);
        if (profile == null) return;
        if (profile instanceof AlchemyProfile){
            AlchemyProfile alchemyProfile = (AlchemyProfile) profile;
            alchemyProfile.setPotionVelocity(alchemyProfile.getPotionVelocity() + velocity);
            ProfileUtil.setProfile(player, alchemyProfile, SkillType.ALCHEMY);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                velocity = (float) (Integer) argument;
            }
            if (argument instanceof Float){
                velocity = (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                velocity = (float) temp;
            }
        }
    }
}
