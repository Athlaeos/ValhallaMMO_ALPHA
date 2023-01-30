package me.athlaeos.valhallammo.perkrewards.account;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import org.bukkit.entity.Player;

public class SkillPointsSetReward extends PerkReward {
    private int points = 0;

    /**
     * Constructor for GrantSkillPointsReward, which adds an amount of Skill Points to the player's account profile.
     * Players can use these skill points to unlock perks in different skill trees
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount to add to the player's experience multiplier. Must be Integer.
     */
    public SkillPointsSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "ACCOUNT");
        if (profile == null) return;
        if (profile instanceof AccountProfile){
            AccountProfile accountProfile = (AccountProfile) profile;
            if (points < 0) points = 0;
            accountProfile.setSpendableSkillPoints(points);
            ProfileManager.getManager().setProfile(player, accountProfile, "ACCOUNT");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Double){
                this.points = (int) Math.floor((Double) argument);
            }
            if (argument instanceof Float){
                points = (int) argument;
            }
            if (argument instanceof Integer){
                points = (Integer) argument;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
