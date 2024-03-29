package me.athlaeos.valhallammo.perkrewards.alchemy;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import org.bukkit.entity.Player;

public class AlchemyPotionSaveChanceSetReward extends PerkReward {
    private float chance = 0F;
    /**
     * Constructor for PotionSaveChanceSetReward, which sets the player's chance to not consume the potion when drank
     * or thrown.
     * A negative value reduces the chance for a potion to be consumed, a positive value increases it.
     * A final value of 1 (or more) makes it so the player never consumes potions.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount additional save chance. Must be Float or Double. If Double, it's cast to float.
     */
    public AlchemyPotionSaveChanceSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "ALCHEMY");
        if (profile == null) return;
        if (profile instanceof AlchemyProfile){
            AlchemyProfile alchemyProfile = (AlchemyProfile) profile;
            alchemyProfile.setPotionSaveChance(chance);
            ProfileManager.getManager().setProfile(player, alchemyProfile, "ALCHEMY");
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

    @Override
    public ObjectType getType() {
        return ObjectType.DOUBLE;
    }
}
