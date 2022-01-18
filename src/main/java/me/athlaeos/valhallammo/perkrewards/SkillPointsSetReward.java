package me.athlaeos.valhallammo.perkrewards;

import me.athlaeos.valhallammo.domain.AccountProfile;
import me.athlaeos.valhallammo.domain.Profile;
import me.athlaeos.valhallammo.domain.SkillType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import org.bukkit.entity.Player;

public class GrantSkillPointsReward extends PerkReward{
    private int points = 0;

    /**
     * Constructor for GrantSkillPointsReward, which adds an amount of Skill Points to the player's account profile.
     * Players can use these skill points to unlock perks in different skill trees
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount to add to the player's experience multiplier. Must be Integer.
     */
    public GrantSkillPointsReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.ACCOUNT);
        if (profile == null) return;
        if (profile instanceof AccountProfile){
            AccountProfile accountProfile = (AccountProfile) profile;
            int currentPoints = accountProfile.getSpendableSkillPoints();
            int newAmount = currentPoints + points;
            if (newAmount < 0) newAmount = 0;
            accountProfile.setSpendableSkillPoints(newAmount);
            ProfileUtil.setProfile(player, accountProfile, SkillType.ACCOUNT);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Double){
                this.points = (int) Math.floor((Double) argument);
            }
            if (argument instanceof Integer){
                points = (Integer) argument;
            }
        }
    }
}
