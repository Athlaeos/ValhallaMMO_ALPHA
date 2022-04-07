package me.athlaeos.valhallammo.perkrewards.landscaping;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.BlockConversionManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.landscaping.LandscapingProfile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class LandscapingUnlockAllConversionsReward extends PerkReward {
    private final List<String> conversionsToUnlock = new ArrayList<>();

    public LandscapingUnlockAllConversionsReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getProfile(player, "LANDSCAPING");
        if (profile == null) return;
        if (profile instanceof LandscapingProfile){
            LandscapingProfile landscapingProfile = (LandscapingProfile) profile;
            Collection<String> unlockedConversions = landscapingProfile.getUnlockedConversions();
            if (unlockedConversions == null) unlockedConversions = new HashSet<>();
            unlockedConversions.addAll(BlockConversionManager.getInstance().getAllConversions().keySet());
            landscapingProfile.setUnlockedConversions(unlockedConversions);
            ProfileManager.setProfile(player, landscapingProfile, "LANDSCAPING");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
    }

    @Override
    public ObjectType getType() {
        return ObjectType.NONE;
    }
}
