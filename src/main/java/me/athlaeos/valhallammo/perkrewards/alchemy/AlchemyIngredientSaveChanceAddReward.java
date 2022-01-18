package me.athlaeos.valhallammo.perkrewards.alchemy;

import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import org.bukkit.entity.Player;

public class IngredientSaveChanceAddReward extends PerkReward {
    private float chance = 0F;
    /**
     * Constructor for IngredientSaveChanceAddReward, which adds to the player's chance to not consume ingredients
     * during brewing when executed.
     * A negative value reduces the chance for a player to save on an ingredient, a positive value increases it.
     * A final value of 1 (or more) makes it so the player uses no ingredients during brewing
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount additional ingredient save chance. Must be Float or Double. If Double, it's cast to float.
     */
    public IngredientSaveChanceAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.ALCHEMY);
        if (profile == null) return;
        if (profile instanceof AlchemyProfile){
            AlchemyProfile alchemyProfile = (AlchemyProfile) profile;
            alchemyProfile.setBrewingIngredientSaveChance(alchemyProfile.getBrewingIngredientSaveChance() + chance);
            ProfileUtil.setProfile(player, alchemyProfile, SkillType.ALCHEMY);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                chance = (float) (Integer) argument;
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
}
