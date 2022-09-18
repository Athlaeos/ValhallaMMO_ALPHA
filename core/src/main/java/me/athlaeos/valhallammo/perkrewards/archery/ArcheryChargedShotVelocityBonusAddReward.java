package me.athlaeos.valhallammo.perkrewards.archery;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.archery.ArcheryProfile;
import org.bukkit.entity.Player;

public class ArcheryChargedShotVelocityBonusAddReward extends PerkReward {
    private float bonus = 0F;

    public ArcheryChargedShotVelocityBonusAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "ARCHERY");
        if (profile == null) return;
        if (profile instanceof ArcheryProfile){
            ArcheryProfile archeryProfile = (ArcheryProfile) profile;
            archeryProfile.setChargedShotVelocityBonus(archeryProfile.getChargedShotVelocityBonus() + bonus);
            ProfileManager.getManager().setProfile(player, archeryProfile, "ARCHERY");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                bonus = (float) (Integer) argument;
            }
            if (argument instanceof Float){
                bonus = (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                bonus = (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.DOUBLE;
    }
}
