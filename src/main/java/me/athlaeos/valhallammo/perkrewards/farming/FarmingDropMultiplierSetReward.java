package me.athlaeos.valhallammo.perkrewards.farming;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.farming.FarmingProfile;
import org.bukkit.entity.Player;

public class FarmingDropMultiplierAddReward extends PerkReward {
    private float chance = 0F;
    /**
     * Constructor for PotionSaveChanceAddReward, which adds to the player's chance to not consume the potion when drank
     * or thrown.
     * A negative value reduces the chance for a potion to be consumed, a positive value increases it.
     * A final value of 1 (or more) makes it so the player never consumes potions.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount additional save chance. Must be Float or Double. If Double, it's cast to float.
     */
    public FarmingDropMultiplierAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.FARMING);
        if (profile == null) return;
        if (profile instanceof FarmingProfile){
            FarmingProfile farmingProfile = (FarmingProfile) profile;
            farmingProfile.setDropMultiplier(farmingProfile.getDropMultiplier() + chance);
            ProfileUtil.setProfile(player, farmingProfile, SkillType.FARMING);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Float){
                chance = (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                chance = (float) temp;
            }
        }
    }
}
