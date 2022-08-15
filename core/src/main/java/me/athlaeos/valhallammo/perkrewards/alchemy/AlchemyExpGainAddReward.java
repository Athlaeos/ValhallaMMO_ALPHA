package me.athlaeos.valhallammo.perkrewards.alchemy;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import org.bukkit.entity.Player;

public class AlchemyExpGainAddReward extends PerkReward {
    private double expGain = 0D;

    /**
     * Constructor for BrewingExpGainAddReward, which adds an amount to the player's brewing experience multiplier.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount to add to the player's experience multiplier. Must be Integer or Double.
     */
    public AlchemyExpGainAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "ALCHEMY");
        if (profile == null) return;
        if (profile instanceof AlchemyProfile){
            AlchemyProfile alchemyProfile = (AlchemyProfile) profile;
            alchemyProfile.setBrewingEXPMultiplier(alchemyProfile.getBrewingEXPMultiplier() + expGain);
            ProfileManager.getManager().setProfile(player, alchemyProfile, "ALCHEMY");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Double){
                this.expGain = (Double) argument;
            }
            if (argument instanceof Integer){
                expGain = (Integer) argument;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
