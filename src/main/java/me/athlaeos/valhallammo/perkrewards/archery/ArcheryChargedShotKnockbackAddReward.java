package me.athlaeos.valhallammo.perkrewards.archery;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.archery.ArcheryProfile;
import org.bukkit.entity.Player;

public class ArcheryChargedShotKnockbackAddReward extends PerkReward {
    private int knockback = 0;

    public ArcheryChargedShotKnockbackAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "ARCHERY");
        if (profile == null) return;
        if (profile instanceof ArcheryProfile){
            ArcheryProfile archeryProfile = (ArcheryProfile) profile;
            archeryProfile.setChargedShotKnockbackBonus(archeryProfile.getChargedShotKnockbackBonus() + knockback);
            ProfileManager.getManager().setProfile(player, archeryProfile, "ARCHERY");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                knockback = (int) argument;
            }
            if (argument instanceof Float){
                knockback = (int) (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                knockback = (int) (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
