package me.athlaeos.valhallammo.perkrewards.archery;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.archery.ArcheryProfile;
import org.bukkit.entity.Player;

public class ArcheryChargedShotCooldownAddReward extends PerkReward {
    private int cooldown = 0;

    public ArcheryChargedShotCooldownAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "ARCHERY");
        if (profile == null) return;
        if (profile instanceof ArcheryProfile){
            ArcheryProfile archeryProfile = (ArcheryProfile) profile;
            archeryProfile.setChargedShotCooldown(archeryProfile.getChargedShotCooldown() + cooldown);
            ProfileManager.getManager().setProfile(player, archeryProfile, "ARCHERY");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                cooldown = (int) argument;
            }
            if (argument instanceof Float){
                cooldown = (int) (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                cooldown = (int) (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
