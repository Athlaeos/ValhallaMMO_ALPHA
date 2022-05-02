package me.athlaeos.valhallammo.perkrewards.heavy_armor;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.heavyarmor.HeavyArmorProfile;
import org.bukkit.entity.Player;

public class HeavyArmorSetBonusPiecesRequiredSetReward extends PerkReward {
    private int pieces = 0;

    public HeavyArmorSetBonusPiecesRequiredSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "HEAVY_ARMOR");
        if (profile == null) return;
        if (profile instanceof HeavyArmorProfile){
            HeavyArmorProfile heavyArmorProfile = (HeavyArmorProfile) profile;
            heavyArmorProfile.setArmorPiecesForBonusses(pieces);
            ProfileManager.getManager().setProfile(player, heavyArmorProfile, "HEAVY_ARMOR");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                pieces = (int) argument;
            }
            if (argument instanceof Float){
                pieces = (int) (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                pieces = (int) (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
