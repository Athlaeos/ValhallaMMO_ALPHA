package me.athlaeos.valhallammo.perkrewards.account;

import me.athlaeos.valhallammo.crafting.recipetypes.DynamicCraftingTableRecipe;
import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.account.AccountProfile;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class UnlockRecipesReward extends PerkReward {
    private List<String> recipesToUnlock = new ArrayList<>();
    /**
     * Constructor for CraftingSkillAddReward, which adds a number of skill points (of a specific MaterialClass if desired)
     * to the player's profile when execute() runs. If materialClass is null, the amount is added to the player's general
     * crafting skill instead.
     * @param name the name of the reward. Must be unique to others rewards, or it will override them.
     *             This is also the name used to define the rewards in the configs.
     * @param argument the amount of points to add to the player. Must be Integer or Double. If Double, it's cast to int.
     */
    public UnlockRecipesReward(String name, Object argument) {
        super(name, argument);
    }

    @Override
    public void execute(Player player) {
        if (player == null) return;
        Profile profile = ProfileManager.getManager().getProfile(player, "ACCOUNT");
        if (profile == null) return;
        if (profile instanceof AccountProfile){
            AccountProfile accountProfile = (AccountProfile) profile;
            Set<String> unlockedRecipes = accountProfile.getUnlockedRecipes();
            unlockedRecipes.addAll(recipesToUnlock);
            accountProfile.setUnlockedRecipes(unlockedRecipes);

            for (String s : recipesToUnlock){
                DynamicCraftingTableRecipe recipe = CustomRecipeManager.getInstance().getDynamicShapedRecipe(s);
                if (recipe != null) player.discoverRecipe(recipe.getKey());
            }

            ProfileManager.getManager().setProfile(player, accountProfile, "ACCOUNT");
        }
    }

    @Override
    public void setArgument(Object argument) {
        super.setArgument(argument);
        if (argument != null){
            if (argument instanceof Collection){
                recipesToUnlock = new ArrayList<>(((Collection<String>) this.argument));
            }
        }
    }

    @Override
    public List<String> getTabAutoComplete(String currentArg) {
        if (currentArg.endsWith(";") || currentArg.equals("")){
            Set<String> allRecipes = new HashSet<>();
            allRecipes.addAll(CustomRecipeManager.getInstance().getBrewingRecipes().keySet());
            allRecipes.addAll(CustomRecipeManager.getInstance().getShapedRecipes().keySet());
            allRecipes.addAll(CustomRecipeManager.getInstance().getAllCustomRecipes().keySet());

            return allRecipes.stream().filter(s -> !currentArg.contains(s)).collect(Collectors.toList());
        }
        return new ArrayList<>();
    }

    @Override
    public ObjectType getType() {
        return ObjectType.STRING_LIST;
    }
}
