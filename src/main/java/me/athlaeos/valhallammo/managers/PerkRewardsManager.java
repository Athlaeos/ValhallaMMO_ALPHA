package me.athlaeos.valhallammo.perkrewards;

import me.athlaeos.valhallammo.perkrewards.smithing.CraftingSkillAddReward;
import me.athlaeos.valhallammo.perkrewards.smithing.CraftingSkillSetReward;
import me.athlaeos.valhallammo.perkrewards.smithing.ExpGainAddReward;
import me.athlaeos.valhallammo.perkrewards.smithing.ExpGainSetReward;
import me.athlaeos.valhallammo.items.MaterialClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PerkRewardsManager {
    private static PerkRewardsManager manager = null;

    private final Map<String, PerkReward> perkRewards = new HashMap<>();

    public PerkRewardsManager(){
        register(new CraftingSkillAddReward("smithing_craftskill_general_add", 0, null));
        register(new CraftingSkillAddReward("smithing_craftskill_wood_add", 0, MaterialClass.WOOD));
        register(new CraftingSkillAddReward("smithing_craftskill_leather_add", 0, MaterialClass.LEATHER));
        register(new CraftingSkillAddReward("smithing_craftskill_stone_add", 0, MaterialClass.STONE));
        register(new CraftingSkillAddReward("smithing_craftskill_chainmail_add", 0, MaterialClass.CHAINMAIL));
        register(new CraftingSkillAddReward("smithing_craftskill_gold_add", 0, MaterialClass.GOLD));
        register(new CraftingSkillAddReward("smithing_craftskill_iron_add", 0, MaterialClass.IRON));
        register(new CraftingSkillAddReward("smithing_craftskill_diamond_add", 0, MaterialClass.DIAMOND));
        register(new CraftingSkillAddReward("smithing_craftskill_netherite_add", 0, MaterialClass.NETHERITE));
        register(new CraftingSkillAddReward("smithing_craftskill_bow_add", 0, MaterialClass.BOW));
        register(new CraftingSkillAddReward("smithing_craftskill_crossbow_add", 0, MaterialClass.CROSSBOW));
        register(new CraftingSkillAddReward("smithing_craftskill_membrane_add", 0, MaterialClass.MEMBRANE));
        register(new CraftingSkillAddReward("smithing_craftskill_prismarine_add", 0, MaterialClass.PRISMARINE));

        register(new CraftingSkillSetReward("smithing_craftskill_general_set", 0, null));
        register(new CraftingSkillSetReward("smithing_craftskill_wood_set", 0, MaterialClass.WOOD));
        register(new CraftingSkillSetReward("smithing_craftskill_leather_set", 0, MaterialClass.LEATHER));
        register(new CraftingSkillSetReward("smithing_craftskill_stone_set", 0, MaterialClass.STONE));
        register(new CraftingSkillSetReward("smithing_craftskill_chainmail_set", 0, MaterialClass.CHAINMAIL));
        register(new CraftingSkillSetReward("smithing_craftskill_gold_set", 0, MaterialClass.GOLD));
        register(new CraftingSkillSetReward("smithing_craftskill_iron_set", 0, MaterialClass.IRON));
        register(new CraftingSkillSetReward("smithing_craftskill_diamond_set", 0, MaterialClass.DIAMOND));
        register(new CraftingSkillSetReward("smithing_craftskill_netherite_set", 0, MaterialClass.NETHERITE));
        register(new CraftingSkillSetReward("smithing_craftskill_bow_set", 0, MaterialClass.BOW));
        register(new CraftingSkillSetReward("smithing_craftskill_crossbow_set", 0, MaterialClass.CROSSBOW));
        register(new CraftingSkillSetReward("smithing_craftskill_membrane_set", 0, MaterialClass.MEMBRANE));
        register(new CraftingSkillSetReward("smithing_craftskill_prismarine_set", 0, MaterialClass.PRISMARINE));

        register(new ExpGainAddReward("smithing_expgain_general_add", 0, null));
        register(new ExpGainAddReward("smithing_expgain_wood_add", 0, MaterialClass.WOOD));
        register(new ExpGainAddReward("smithing_expgain_leather_add", 0, MaterialClass.LEATHER));
        register(new ExpGainAddReward("smithing_expgain_stone_add", 0, MaterialClass.STONE));
        register(new ExpGainAddReward("smithing_expgain_chainmail_add", 0, MaterialClass.CHAINMAIL));
        register(new ExpGainAddReward("smithing_expgain_gold_add", 0, MaterialClass.GOLD));
        register(new ExpGainAddReward("smithing_expgain_iron_add", 0, MaterialClass.IRON));
        register(new ExpGainAddReward("smithing_expgain_diamond_add", 0, MaterialClass.DIAMOND));
        register(new ExpGainAddReward("smithing_expgain_netherite_add", 0, MaterialClass.NETHERITE));
        register(new ExpGainAddReward("smithing_expgain_bow_add", 0, MaterialClass.BOW));
        register(new ExpGainAddReward("smithing_expgain_crossbow_add", 0, MaterialClass.CROSSBOW));
        register(new ExpGainAddReward("smithing_expgain_membrane_add", 0, MaterialClass.MEMBRANE));
        register(new ExpGainAddReward("smithing_expgain_prismarine_add", 0, MaterialClass.PRISMARINE));

        register(new ExpGainSetReward("smithing_expgain_general_set", 0, null));
        register(new ExpGainSetReward("smithing_expgain_wood_set", 0, MaterialClass.WOOD));
        register(new ExpGainSetReward("smithing_expgain_leather_set", 0, MaterialClass.LEATHER));
        register(new ExpGainSetReward("smithing_expgain_stone_set", 0, MaterialClass.STONE));
        register(new ExpGainSetReward("smithing_expgain_chainmail_set", 0, MaterialClass.CHAINMAIL));
        register(new ExpGainSetReward("smithing_expgain_gold_set", 0, MaterialClass.GOLD));
        register(new ExpGainSetReward("smithing_expgain_iron_set", 0, MaterialClass.IRON));
        register(new ExpGainSetReward("smithing_expgain_diamond_set", 0, MaterialClass.DIAMOND));
        register(new ExpGainSetReward("smithing_expgain_netherite_set", 0, MaterialClass.NETHERITE));
        register(new ExpGainSetReward("smithing_expgain_bow_set", 0, MaterialClass.BOW));
        register(new ExpGainSetReward("smithing_expgain_crossbow_set", 0, MaterialClass.CROSSBOW));
        register(new ExpGainSetReward("smithing_expgain_membrane_set", 0, MaterialClass.MEMBRANE));
        register(new ExpGainSetReward("smithing_expgain_prismarine_set", 0, MaterialClass.PRISMARINE));

        register(new LockRecipesReward("lock_recipe", new ArrayList<>()));
        register(new LockAllCraftRecipesReward("lock_recipes_all_craft", new ArrayList<>()));
        register(new LockAllTinkerRecipesReward("lock_recipes_all_tinker", new ArrayList<>()));
        register(new LockAllShapedRecipesReward("lock_recipes_all_shaped", new ArrayList<>()));
        register(new UnlockRecipesReward("unlock_recipe", new ArrayList<>()));
        register(new UnlockAllCraftRecipesReward("unlock_recipes_all_craft", new ArrayList<>()));
        register(new UnlockAllTinkerRecipesReward("unlock_recipes_all_tinker", new ArrayList<>()));
        register(new UnlockAllShapedRecipesReward("unlock_recipes_all_shaped", new ArrayList<>()));

        register(new SkillPointsAddReward("skillpoints_add", new ArrayList<>()));
        register(new SkillPointsSetReward("skillpoints_set", new ArrayList<>()));

        register(new PotionEffectAddReward("potion_effects_add", 0));
    }

    public static PerkRewardsManager getInstance(){
        if (manager == null) manager = new PerkRewardsManager();
        return manager;
    }

    public boolean register(PerkReward modifier){
        if (perkRewards.containsKey(modifier.getName())) return false;
        perkRewards.put(modifier.getName(), modifier);
        return true;
    }

    /**
     * Creates an instance of the appropriate DynamicItemModifier given its name.
     * This method is used in loading the configs, because the type of modifier is only accessible by a string name
     * and requires a double strength property to be given.
     * @param name the name of the modifier type as registered in getModifiers()
     * @param argument the double strength given to the modifier
     * @return an instance of DynamicModifier used to tinker with output ItemStacks of custom recipes.
     */
    public PerkReward createReward(String name, Object argument){
        try {
            if (perkRewards.get(name) == null) return null;
            PerkReward modifier = perkRewards.get(name).clone();
            modifier.setArgument(argument);
            return modifier;
        } catch (CloneNotSupportedException ignored){
            return null;
        }
    }

    public Map<String, PerkReward> getPerkRewards() {
        return perkRewards;
    }
}
