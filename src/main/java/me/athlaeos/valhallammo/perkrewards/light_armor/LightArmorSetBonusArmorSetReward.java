package me.athlaeos.valhallammo.perkrewards.light_armor;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.lightarmor.LightArmorProfile;
import org.bukkit.entity.Player;

public class LightArmorSetBonusArmorSetReward extends PerkReward {
    private float pieces = 0F;

    public LightArmorSetBonusArmorSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "LIGHT_ARMOR");
        if (profile == null) return;
        if (profile instanceof LightArmorProfile){
            LightArmorProfile lightArmorProfile = (LightArmorProfile) profile;
            lightArmorProfile.setFullArmorMultiplierBonus(pieces);
            ProfileManager.getManager().setProfile(player, lightArmorProfile, "LIGHT_ARMOR");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Float){
                pieces = (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                pieces = (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.DOUBLE;
    }
}
