package me.athlaeos.valhallammo.perkrewards.light_armor;

import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.lightarmor.LightArmorProfile;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class LightArmorImmunePotionEffectsRemoveReward extends PerkReward {
    private List<String> blocksToAdd = new ArrayList<>();
    public LightArmorImmunePotionEffectsRemoveReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "LIGHT_ARMOR");
        if (profile == null) return;
        if (profile instanceof LightArmorProfile){
            LightArmorProfile lightArmorProfile = (LightArmorProfile) profile;
            Collection<String> unbreakableBlocks = lightArmorProfile.getImmunePotionEffects();
            if (unbreakableBlocks == null) unbreakableBlocks = new HashSet<>();
            unbreakableBlocks.removeAll(blocksToAdd);
            lightArmorProfile.setImmunePotionEffects(unbreakableBlocks);
            ProfileManager.getManager().setProfile(player, lightArmorProfile, "LIGHT_ARMOR");
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
