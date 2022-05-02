package me.athlaeos.valhallammo.perkrewards.light_armor;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.lightarmor.LightArmorProfile;
import org.bukkit.entity.Player;

public class LightArmorSetBonusHealingBonusAddReward extends PerkReward {
    private float bonus = 0F;

    public LightArmorSetBonusHealingBonusAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "LIGHT_ARMOR");
        if (profile == null) return;
        if (profile instanceof LightArmorProfile){
            LightArmorProfile lightArmorProfile = (LightArmorProfile) profile;
            lightArmorProfile.setFullArmorHealingBonus(lightArmorProfile.getFullArmorHealingBonus() + bonus);
            ProfileManager.getManager().setProfile(player, lightArmorProfile, "LIGHT_ARMOR");
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
