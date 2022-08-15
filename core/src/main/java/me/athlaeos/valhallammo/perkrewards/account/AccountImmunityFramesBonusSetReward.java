package me.athlaeos.valhallammo.perkrewards.account;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import org.bukkit.entity.Player;

public class AccountImmunityFramesBonusSetReward extends PerkReward {
    private int frames = 0;

    public AccountImmunityFramesBonusSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "ACCOUNT");
        if (profile == null) return;
        if (profile instanceof AccountProfile){
            AccountProfile accountProfile = (AccountProfile) profile;
            accountProfile.setImmunityFrameBonus(frames);
            ProfileManager.getManager().setProfile(player, accountProfile, "ACCOUNT");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                frames = (Integer) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                frames = (int) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
