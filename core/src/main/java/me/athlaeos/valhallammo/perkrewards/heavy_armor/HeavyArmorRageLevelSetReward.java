package me.athlaeos.valhallammo.perkrewards.heavy_armor;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.heavyarmor.HeavyArmorProfile;
import org.bukkit.entity.Player;

public class HeavyArmorRageLevelSetReward extends PerkReward {
    private int level = 0;

    public HeavyArmorRageLevelSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "HEAVY_ARMOR");
        if (profile == null) return;
        if (profile instanceof HeavyArmorProfile){
            HeavyArmorProfile heavyArmorProfile = (HeavyArmorProfile) profile;
            heavyArmorProfile.setRageLevel(level);
            ProfileManager.getManager().setProfile(player, heavyArmorProfile, "HEAVY_ARMOR");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                level = (int) argument;
            }
            if (argument instanceof Float){
                level = (int) (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                level = (int) (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
