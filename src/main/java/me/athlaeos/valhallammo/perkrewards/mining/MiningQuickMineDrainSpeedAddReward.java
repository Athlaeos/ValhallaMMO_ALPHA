package me.athlaeos.valhallammo.perkrewards.farming;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import org.bukkit.entity.Player;

public class FarmingUltraHarvestCooldownAddReward extends PerkReward {
    private int cooldown = 0;

    public FarmingUltraHarvestCooldownAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.FARMING);
        if (profile == null) return;
        if (profile instanceof FarmingProfile){
            FarmingProfile farmingProfile = (FarmingProfile) profile;
            farmingProfile.setUltraHarvestingCooldown(farmingProfile.getUltraHarvestingCooldown() + cooldown);
            ProfileUtil.setProfile(player, farmingProfile, SkillType.FARMING);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                cooldown = (int) argument;
            }
            if (argument instanceof Float){
                cooldown = (int) (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                cooldown = (int) (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
