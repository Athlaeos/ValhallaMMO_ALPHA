package me.athlaeos.valhallammo.perkrewards.smithing;

import me.athlaeos.valhallammo.domain.AccountProfile;
import me.athlaeos.valhallammo.domain.Profile;
import me.athlaeos.valhallammo.domain.SkillType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.smithing.materials.MaterialClass;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class UnlockRecipesReward extends PerkReward {
    private List<String> recipesToUnlock = new ArrayList<>();
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
    public UnlockRecipesReward(String name, Object argument, MaterialClass materialClass) {
        super(name, argument);
        this.materialClass = materialClass;
        if (argument != null){
            if (argument instanceof Collection){
                recipesToUnlock.addAll((Collection<String>) argument);
            }
        }
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.ACCOUNT);
        if (profile == null) return;
        if (profile instanceof AccountProfile){
            AccountProfile accountProfile = (AccountProfile) profile;
            Set<String> unlockedRecipes = accountProfile.getUnlockedRecipes();
            unlockedRecipes.addAll(recipesToUnlock);
            accountProfile.setUnlockedRecipes(unlockedRecipes);
            ProfileUtil.setProfile(player, accountProfile, SkillType.ACCOUNT);
        }
    }
}
