package me.athlaeos.valhallammo.perkrewards.heavy_weapons;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.heavyweapons.HeavyWeaponsProfile;
import org.bukkit.entity.Player;

public class HeavyWeaponsParryEnemyDebuffDurationSetReward extends PerkReward {
    private int duration = 0;

    public HeavyWeaponsParryEnemyDebuffDurationSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "HEAVY_WEAPONS");
        if (profile == null) return;
        if (profile instanceof HeavyWeaponsProfile){
            HeavyWeaponsProfile heavyWeaponsProfile = (HeavyWeaponsProfile) profile;
            heavyWeaponsProfile.setEnemyDebuffDuration(duration);
            ProfileManager.getManager().setProfile(player, heavyWeaponsProfile, "HEAVY_WEAPONS");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                duration = (int) argument;
            }
            if (argument instanceof Float){
                duration = (int) (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                duration = (int) (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
