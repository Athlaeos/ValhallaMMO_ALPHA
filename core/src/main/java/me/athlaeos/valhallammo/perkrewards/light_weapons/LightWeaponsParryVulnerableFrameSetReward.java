package me.athlaeos.valhallammo.perkrewards.light_weapons;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.lightweapons.LightWeaponsProfile;
import org.bukkit.entity.Player;

public class LightWeaponsParryVulnerableFrameSetReward extends PerkReward {
    private int timeFrame = 0;

    public LightWeaponsParryVulnerableFrameSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "LIGHT_WEAPONS");
        if (profile == null) return;
        if (profile instanceof LightWeaponsProfile){
            LightWeaponsProfile lightWeaponsProfile = (LightWeaponsProfile) profile;
            lightWeaponsProfile.setParryVulnerableFrame(timeFrame);
            ProfileManager.getManager().setProfile(player, lightWeaponsProfile, "LIGHT_WEAPONS");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                timeFrame = (int) argument;
            }
            if (argument instanceof Float){
                timeFrame = (int) (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                timeFrame = (int) (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
