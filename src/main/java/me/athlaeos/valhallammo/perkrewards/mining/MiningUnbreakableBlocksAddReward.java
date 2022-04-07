package me.athlaeos.valhallammo.perkrewards.mining;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.mining.MiningProfile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class MiningUnbreakableBlocksAddReward extends PerkReward {
    private List<String> blocksToAdd = new ArrayList<>();
    public MiningUnbreakableBlocksAddReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getProfile(player, "MINING");
        if (profile == null) return;
        if (profile instanceof MiningProfile){
            MiningProfile miningProfile = (MiningProfile) profile;
            Collection<String> unbreakableBlocks = miningProfile.getUnbreakableBlocks();
            if (unbreakableBlocks == null) unbreakableBlocks = new HashSet<>();
            unbreakableBlocks.addAll(blocksToAdd);
            miningProfile.setUnbreakableBlocks(unbreakableBlocks);
            ProfileManager.setProfile(player, miningProfile, "MINING");
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
