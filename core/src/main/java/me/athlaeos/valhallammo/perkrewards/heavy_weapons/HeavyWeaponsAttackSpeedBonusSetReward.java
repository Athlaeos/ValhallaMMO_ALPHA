package me.athlaeos.valhallammo.perkrewards.heavy_weapons;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.heavyweapons.HeavyWeaponsProfile;
import org.bukkit.entity.Player;

public class HeavyWeaponsAttackSpeedBonusSetReward extends PerkReward {
    private float bonus = 0F;

    public HeavyWeaponsAttackSpeedBonusSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "HEAVY_WEAPONS");
        if (profile == null) return;
        if (profile instanceof HeavyWeaponsProfile){
            HeavyWeaponsProfile heavyWeaponsProfile = (HeavyWeaponsProfile) profile;
            heavyWeaponsProfile.setAttackSpeedBonus(bonus);
            ProfileManager.getManager().setProfile(player, heavyWeaponsProfile, "HEAVY_WEAPONS");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
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
