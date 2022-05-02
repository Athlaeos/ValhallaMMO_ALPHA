package me.athlaeos.valhallammo.perkrewards.archery;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.archery.ArcheryProfile;
import org.bukkit.entity.Player;

public class ArcheryCritOnStandingStillReward extends PerkReward {
    private boolean enable = false;

    public ArcheryCritOnStandingStillReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "ARCHERY");
        if (profile == null) return;
        if (profile instanceof ArcheryProfile){
            ArcheryProfile archeryProfile = (ArcheryProfile) profile;
            archeryProfile.setCritOnStandingStill(enable);
            ProfileManager.getManager().setProfile(player, archeryProfile, "ARCHERY");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Boolean){
                enable = (boolean) argument;
            }
            if (argument instanceof String){
                try {
                    enable = Boolean.parseBoolean((String) argument);
                } catch (IllegalArgumentException ignored){
                }
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.BOOLEAN;
    }
}
