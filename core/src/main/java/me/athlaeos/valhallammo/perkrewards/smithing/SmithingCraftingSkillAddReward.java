package me.athlaeos.valhallammo.perkrewards.smithing;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.smithing.SmithingProfile;
import org.bukkit.entity.Player;

public class SmithingCraftingSkillAddReward extends PerkReward {
    private int points = 0;
    private final MaterialClass materialClass;
    /**
     * Constructor for CraftingSkillAddReward, which adds a number of skill points (of a specific MaterialClass if desired)
     * to the player's profile when execute() runs. If materialClass is null, the amount is added to the player's general
     * crafting skill instead.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount of points to add to the player. Must be Integer or Double. If Double, it's cast to int.
     * @param materialClass the MaterialClass to set the player's skill. If null, general crafting skill is set instead.
     */
    public SmithingCraftingSkillAddReward(String name, Object argument, MaterialClass materialClass) {
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
                smithingProfile.setGeneralCraftingQuality(smithingProfile.getGeneralCraftingQuality() + points);
            } else {
                smithingProfile.setMaterialCraftingQuality(materialClass, smithingProfile.getCraftingQuality(materialClass) + points);
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
