package me.athlaeos.valhallammo.perkrewards.account;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import org.bukkit.entity.Player;

public class ExpGainAddReward extends PerkReward {
    private double expGain = 0D;

    /**
     * Constructor for ExpGainAddReward, which adds an amount to the player's experience multiplier (of a specific
     * MaterialClass if desired). If materialClass is left null, the amount will be added to the player's general
     * crafting experience multiplier instead.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount to add to the player's experience multiplier. Must be Integer or Double.
     */
    public ExpGainAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "ACCOUNT");
        if (profile == null) return;
        if (profile instanceof AccountProfile){
            AccountProfile accountProfile = (AccountProfile) profile;
            accountProfile.setAllSkillEXPGain(accountProfile.getAllSkillEXPGain() + expGain);
            ProfileManager.getManager().setProfile(player, accountProfile, "ACCOUNT");
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
        return ObjectType.DOUBLE;
    }
}
