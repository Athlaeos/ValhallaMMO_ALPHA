package me.athlaeos.valhallammo.perkrewards.smithing;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.smithing.SmithingProfile;
import me.athlaeos.valhallammo.items.MaterialClass;
import org.bukkit.entity.Player;

public class ExpGainAddReward extends PerkReward {
    private double expGain = 0D;
    private final MaterialClass materialClass;

    /**
     * Constructor for ExpGainAddReward, which adds an amount to the player's experience multiplier (of a specific
     * MaterialClass if desired). If materialClass is left null, the amount will be added to the player's general
     * crafting experience multiplier instead.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount to add to the player's experience multiplier. Must be Integer or Double.
     * @param materialClass the MaterialClass to add to the player's experience multiplier. If null, the general crafting
     *                      experience multiplier is added on instead.
     */
    public ExpGainAddReward(String name, Object argument, MaterialClass materialClass) {
        super(name, argument);
        this.materialClass = materialClass;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.SMITHING);
        if (profile == null) return;
        if (profile instanceof SmithingProfile){
            SmithingProfile smithingProfile = (SmithingProfile) profile;
            if (materialClass == null){
                smithingProfile.setGeneralCraftingExpMultiplier(smithingProfile.getGeneralCraftingExpMultiplier() + expGain);
            } else {
                smithingProfile.setCraftingEXPMultiplier(materialClass, smithingProfile.getCraftingEXPMultiplier(materialClass) + expGain);
            }
            ProfileUtil.setProfile(player, smithingProfile, SkillType.SMITHING);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Double){
                this.expGain = (Double) argument;
            }
            if (argument instanceof Integer){
                expGain = (Integer) argument;
            }
        }
    }
}
