package me.athlaeos.valhallammo.perkrewards.heavy_weapons;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.heavyweapons.HeavyWeaponsProfile;
import org.bukkit.entity.Player;

public class HeavyWeaponsStunChanceSetReward extends PerkReward {
    private float chance = 0F;

    public HeavyWeaponsStunChanceSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "HEAVY_WEAPONS");
        if (profile == null) return;
        if (profile instanceof HeavyWeaponsProfile){
            HeavyWeaponsProfile heavyWeaponsProfile = (HeavyWeaponsProfile) profile;
            heavyWeaponsProfile.setStunChance(chance);
            ProfileManager.getManager().setProfile(player, heavyWeaponsProfile, "HEAVY_WEAPONS");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                chance = (float) argument;
            }
            if (argument instanceof Float){
                chance = (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                chance = (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.DOUBLE;
    }
}
