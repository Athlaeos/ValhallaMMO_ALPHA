package me.athlaeos.valhallammo.perkrewards.enchanting;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.enchanting.EnchantingProfile;
import org.bukkit.entity.Player;

public class EnchantingAnvilSkillSetReward extends PerkReward {
    private int points = 0;

    public EnchantingAnvilSkillSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "ENCHANTING");
        if (profile == null) return;
        if (profile instanceof EnchantingProfile){
            EnchantingProfile enchantingProfile = (EnchantingProfile) profile;
            enchantingProfile.setAnvilQuality(points);
            ProfileManager.getManager().setProfile(player, enchantingProfile, "ENCHANTING");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                points = (Integer) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                points = (int) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
