package me.athlaeos.valhallammo.perkrewards.heavy_armor;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.heavyarmor.HeavyArmorProfile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class HeavyArmorImmunePotionEffectsRemoveReward extends PerkReward {
    private List<String> blocksToAdd = new ArrayList<>();
    public HeavyArmorImmunePotionEffectsRemoveReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "HEAVY_ARMOR");
        if (profile == null) return;
        if (profile instanceof HeavyArmorProfile){
            HeavyArmorProfile heavyArmorProfile = (HeavyArmorProfile) profile;
            Collection<String> unbreakableBlocks = heavyArmorProfile.getImmunePotionEffects();
            if (unbreakableBlocks == null) unbreakableBlocks = new HashSet<>();
            unbreakableBlocks.removeAll(blocksToAdd);
            heavyArmorProfile.setImmunePotionEffects(unbreakableBlocks);
            ProfileManager.getManager().setProfile(player, heavyArmorProfile, "HEAVY_ARMOR");
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
