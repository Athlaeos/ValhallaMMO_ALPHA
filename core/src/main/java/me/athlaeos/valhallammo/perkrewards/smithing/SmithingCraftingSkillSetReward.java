package me.athlaeos.valhallammo.perkrewards.smithing;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.smithing.SmithingProfile;
import org.bukkit.entity.Player;

public class SmithingCraftingSkillSetReward extends PerkReward {
    private int points = 0;
    private final MaterialClass materialClass;

    /**
     * Constructor for CraftingSkillSetReward, which sets the player's skill points (of a specific MaterialClass if desired)
     * to the player's profile when execute() runs. If materialClass is null, the player's general crafting skill is set
     * instead.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount of points to set to the player's crafting skill. Must be Integer or Double.
     *                 If Double, it's cast to int.
     * @param materialClass the MaterialClass to add to the player's skill. If null, general crafting skill is added on
     *                     instead.
     */
    public SmithingCraftingSkillSetReward(String name, Object argument, MaterialClass materialClass) {
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
                smithingProfile.setGeneralCraftingQuality(points);
            } else {
                smithingProfile.setMaterialCraftingQuality(materialClass, points);
            }
            ProfileManager.getManager().setProfile(player, smithingProfile, "SMITHING");
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
