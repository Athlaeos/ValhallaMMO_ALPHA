package me.athlaeos.valhallammo.perkrewards.light_weapons;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.lightweapons.LightWeaponsProfile;
import org.bukkit.entity.Player;

public class LightWeaponsUnlockedWeaponCoatingReward extends PerkReward {
    private boolean enable = false;

    public LightWeaponsUnlockedWeaponCoatingReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "LIGHT_WEAPONS");
        if (profile == null) return;
        if (profile instanceof LightWeaponsProfile){
            LightWeaponsProfile lightWeaponsProfile = (LightWeaponsProfile) profile;
            lightWeaponsProfile.setUnlockedWeaponCoating(enable);
            ProfileManager.getManager().setProfile(player, lightWeaponsProfile, "LIGHT_WEAPONS");
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
