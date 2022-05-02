package me.athlaeos.valhallammo.perkrewards.smithing;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.smithing.SmithingProfile;
import org.bukkit.entity.Player;

public class SmithingExpGainSetReward extends PerkReward {
    private double expGain = 0D;
    private final MaterialClass materialClass;
    /**
     * Constructor for ExpGainSetReward, which sets the player's experience multiplier (of a specific
     * MaterialClass if desired) to the given amount. If materialClass is left null, the player's general
     * crafting experience multiplier is set instead.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount to set to the player's experience multiplier. Must be Integer or Double.
     * @param materialClass the MaterialClass to set the player's experience multiplier to. If null, the general crafting
     *                      experience multiplier is set instead.
     */
    public SmithingExpGainSetReward(String name, Object argument, MaterialClass materialClass) {
        super(name, argument);
        this.materialClass = materialClass;
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "SMITHING");
        if (profile == null) return;
        if (profile instanceof SmithingProfile){
            SmithingProfile smithingProfile = (SmithingProfile) profile;
            if (materialClass == null){
                smithingProfile.setGeneralCraftingExpMultiplier(expGain);
            } else {
                smithingProfile.setCraftingEXPMultiplier(materialClass, expGain);
            }
            ProfileManager.getManager().setProfile(player, smithingProfile, "SMITHING");
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

    @Override
    public ObjectType getType() {
        return ObjectType.INTEGER;
    }
}
