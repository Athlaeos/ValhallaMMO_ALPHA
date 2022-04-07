package me.athlaeos.valhallammo.perkrewards.landscaping;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.landscaping.LandscapingProfile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class LandscapingTreeCapitatorBlocksAddReward extends PerkReward {
    private List<String> blocksToAdd = new ArrayList<>();
    public LandscapingTreeCapitatorBlocksAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getProfile(player, "LANDSCAPING");
        if (profile == null) return;
        if (profile instanceof LandscapingProfile){
            LandscapingProfile landscapingProfile = (LandscapingProfile) profile;
            Collection<String> unlockedBlocks = landscapingProfile.getValidTreeCapitatorBlocks();
            if (unlockedBlocks == null) unlockedBlocks = new HashSet<>();
            unlockedBlocks.addAll(blocksToAdd);
            landscapingProfile.setValidTreeCapitatorBlocks(unlockedBlocks);
            ProfileManager.setProfile(player, landscapingProfile, "LANDSCAPING");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Collection){
                blocksToAdd = new ArrayList<>(((Collection<String>) this.argument));
            }
        }
    }

    @Override
    public ObjectType getType() {
        return ObjectType.STRING_LIST;
    }
}
