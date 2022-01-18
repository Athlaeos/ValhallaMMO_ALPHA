package me.athlaeos.valhallammo.perkrewards;

import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.crafting.recipetypes.AbstractCustomCraftingRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.ItemImprovementRecipe;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import org.bukkit.entity.Player;

import java.util.Set;
import java.util.stream.Collectors;

public class LockAllTinkerRecipesReward extends PerkReward {
    /**
     * Constructor for CraftingSkillAddReward, which adds a number of skill points (of a specific MaterialClass if desired)
     * to the player's profile when execute() runs. If materialClass is null, the amount is added to the player's general
     * crafting skill instead.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount of points to add to the player. Must be Integer or Double. If Double, it's cast to int.
     */
    public LockAllTinkerRecipesReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileUtil.getProfile(player, SkillType.ACCOUNT);
        if (profile == null) return;
        if (profile instanceof AccountProfile){
            AccountProfile accountProfile = (AccountProfile) profile;
            Set<String> unlockedRecipes = accountProfile.getUnlockedRecipes();

            unlockedRecipes.removeAll(CustomRecipeManager.getInstance().getAllCustomRecipes().values().stream().filter(
                    abstractCraftingRecipe -> abstractCraftingRecipe instanceof ItemImprovementRecipe
            ).map(AbstractCustomCraftingRecipe::getName).collect(Collectors.toList()));

            accountProfile.setUnlockedRecipes(unlockedRecipes);
            ProfileUtil.setProfile(player, accountProfile, SkillType.ACCOUNT);
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
    }
}
