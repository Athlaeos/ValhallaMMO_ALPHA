package me.athlaeos.valhallammo.perkrewards.smithing;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.smithing.SmithingProfile;
import org.bukkit.entity.Player;

public class SmithingCraftingTimeReductionSetReward extends PerkReward {
    private float reduction = 0F;

    public SmithingCraftingTimeReductionSetReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "SMITHING");
        if (profile == null) return;
        if (profile instanceof SmithingProfile){
            SmithingProfile smithingProfile = (SmithingProfile) profile;
            smithingProfile.setCraftingTimeReduction(reduction);
            ProfileManager.getManager().setProfile(player, smithingProfile, "SMITHING");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Integer){
                reduction = (float) (Integer) argument;
            }
            if (argument instanceof Float){
                reduction = (float) argument;
            }
            if (argument instanceof Double){
                double temp = (Double) argument;
                reduction = (float) temp;
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.DOUBLE;
    }
}
