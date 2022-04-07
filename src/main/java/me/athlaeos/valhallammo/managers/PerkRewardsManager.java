package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.items.EnchantmentType;
import me.athlaeos.valhallammo.perkrewards.*;
import me.athlaeos.valhallammo.perkrewards.account.*;
import me.athlaeos.valhallammo.perkrewards.alchemy.*;
import me.athlaeos.valhallammo.perkrewards.archery.*;
import me.athlaeos.valhallammo.perkrewards.enchanting.*;
import me.athlaeos.valhallammo.perkrewards.farming.*;
import me.athlaeos.valhallammo.perkrewards.landscaping.*;
import me.athlaeos.valhallammo.perkrewards.mining.*;
import me.athlaeos.valhallammo.perkrewards.smithing.*;
import me.athlaeos.valhallammo.items.MaterialClass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PerkRewardsManager {
    private static PerkRewardsManager manager = null;

    private final Map<String, PerkReward> perkRewards = new HashMap<>();

    public PerkRewardsManager(){
        // GENERAL
        register(new ExpGainAddReward("expgain_add", 0D));
        register(new ExpGainSetReward("expgain_set", 0D));

        register(new LockRecipesReward("lock_recipes", new ArrayList<>()));
        register(new LockAllCraftRecipesReward("lock_recipes_all_craft", new ArrayList<>()));
        register(new LockAllTinkerRecipesReward("lock_recipes_all_tinker", new ArrayList<>()));
        register(new LockAllShapedRecipesReward("lock_recipes_all_shaped", new ArrayList<>()));
        register(new LockAllBrewingRecipesReward("lock_recipes_all_brewing", new ArrayList<>()));
        register(new UnlockRecipesReward("unlock_recipes", new ArrayList<>()));
        register(new UnlockAllCraftRecipesReward("unlock_recipes_all_craft", new ArrayList<>()));
        register(new UnlockAllTinkerRecipesReward("unlock_recipes_all_tinker", new ArrayList<>()));
        register(new UnlockAllShapedRecipesReward("unlock_recipes_all_shaped", new ArrayList<>()));
        register(new UnlockAllBrewingRecipesReward("unlock_recipes_all_brewing", new ArrayList<>()));

        register(new RemovePerksReward("remove_perks", new ArrayList<>()));
        register(new AddPerksReward("add_perks", new ArrayList<>()));

        register(new SkillPointsAddReward("skillpoints_add", new ArrayList<>()));
        register(new SkillPointsSetReward("skillpoints_set", new ArrayList<>()));

        register(new PotionEffectAddReward("potion_effects_add", 0));

        // SMITHING
        register(new SmithingCraftingSkillAddReward("smithing_craftskill_general_add", 0, null));
        register(new SmithingCraftingSkillAddReward("smithing_craftskill_wood_add", 0, MaterialClass.WOOD));
        register(new SmithingCraftingSkillAddReward("smithing_craftskill_leather_add", 0, MaterialClass.LEATHER));
        register(new SmithingCraftingSkillAddReward("smithing_craftskill_stone_add", 0, MaterialClass.STONE));
        register(new SmithingCraftingSkillAddReward("smithing_craftskill_chainmail_add", 0, MaterialClass.CHAINMAIL));
        register(new SmithingCraftingSkillAddReward("smithing_craftskill_gold_add", 0, MaterialClass.GOLD));
        register(new SmithingCraftingSkillAddReward("smithing_craftskill_iron_add", 0, MaterialClass.IRON));
        register(new SmithingCraftingSkillAddReward("smithing_craftskill_diamond_add", 0, MaterialClass.DIAMOND));
        register(new SmithingCraftingSkillAddReward("smithing_craftskill_netherite_add", 0, MaterialClass.NETHERITE));
        register(new SmithingCraftingSkillAddReward("smithing_craftskill_bow_add", 0, MaterialClass.BOW));
        register(new SmithingCraftingSkillAddReward("smithing_craftskill_crossbow_add", 0, MaterialClass.CROSSBOW));
        register(new SmithingCraftingSkillAddReward("smithing_craftskill_membrane_add", 0, MaterialClass.MEMBRANE));
        register(new SmithingCraftingSkillAddReward("smithing_craftskill_prismarine_add", 0, MaterialClass.PRISMARINE));

        register(new SmithingCraftingSkillSetReward("smithing_craftskill_general_set", 0, null));
        register(new SmithingCraftingSkillSetReward("smithing_craftskill_wood_set", 0, MaterialClass.WOOD));
        register(new SmithingCraftingSkillSetReward("smithing_craftskill_leather_set", 0, MaterialClass.LEATHER));
        register(new SmithingCraftingSkillSetReward("smithing_craftskill_stone_set", 0, MaterialClass.STONE));
        register(new SmithingCraftingSkillSetReward("smithing_craftskill_chainmail_set", 0, MaterialClass.CHAINMAIL));
        register(new SmithingCraftingSkillSetReward("smithing_craftskill_gold_set", 0, MaterialClass.GOLD));
        register(new SmithingCraftingSkillSetReward("smithing_craftskill_iron_set", 0, MaterialClass.IRON));
        register(new SmithingCraftingSkillSetReward("smithing_craftskill_diamond_set", 0, MaterialClass.DIAMOND));
        register(new SmithingCraftingSkillSetReward("smithing_craftskill_netherite_set", 0, MaterialClass.NETHERITE));
        register(new SmithingCraftingSkillSetReward("smithing_craftskill_bow_set", 0, MaterialClass.BOW));
        register(new SmithingCraftingSkillSetReward("smithing_craftskill_crossbow_set", 0, MaterialClass.CROSSBOW));
        register(new SmithingCraftingSkillSetReward("smithing_craftskill_membrane_set", 0, MaterialClass.MEMBRANE));
        register(new SmithingCraftingSkillSetReward("smithing_craftskill_prismarine_set", 0, MaterialClass.PRISMARINE));

        register(new SmithingExpGainAddReward("smithing_expgain_general_add", 0, null));
        register(new SmithingExpGainAddReward("smithing_expgain_wood_add", 0, MaterialClass.WOOD));
        register(new SmithingExpGainAddReward("smithing_expgain_leather_add", 0, MaterialClass.LEATHER));
        register(new SmithingExpGainAddReward("smithing_expgain_stone_add", 0, MaterialClass.STONE));
        register(new SmithingExpGainAddReward("smithing_expgain_chainmail_add", 0, MaterialClass.CHAINMAIL));
        register(new SmithingExpGainAddReward("smithing_expgain_gold_add", 0, MaterialClass.GOLD));
        register(new SmithingExpGainAddReward("smithing_expgain_iron_add", 0, MaterialClass.IRON));
        register(new SmithingExpGainAddReward("smithing_expgain_diamond_add", 0, MaterialClass.DIAMOND));
        register(new SmithingExpGainAddReward("smithing_expgain_netherite_add", 0, MaterialClass.NETHERITE));
        register(new SmithingExpGainAddReward("smithing_expgain_bow_add", 0, MaterialClass.BOW));
        register(new SmithingExpGainAddReward("smithing_expgain_crossbow_add", 0, MaterialClass.CROSSBOW));
        register(new SmithingExpGainAddReward("smithing_expgain_membrane_add", 0, MaterialClass.MEMBRANE));
        register(new SmithingExpGainAddReward("smithing_expgain_prismarine_add", 0, MaterialClass.PRISMARINE));

        register(new SmithingExpGainSetReward("smithing_expgain_general_set", 0, null));
        register(new SmithingExpGainSetReward("smithing_expgain_wood_set", 0, MaterialClass.WOOD));
        register(new SmithingExpGainSetReward("smithing_expgain_leather_set", 0, MaterialClass.LEATHER));
        register(new SmithingExpGainSetReward("smithing_expgain_stone_set", 0, MaterialClass.STONE));
        register(new SmithingExpGainSetReward("smithing_expgain_chainmail_set", 0, MaterialClass.CHAINMAIL));
        register(new SmithingExpGainSetReward("smithing_expgain_gold_set", 0, MaterialClass.GOLD));
        register(new SmithingExpGainSetReward("smithing_expgain_iron_set", 0, MaterialClass.IRON));
        register(new SmithingExpGainSetReward("smithing_expgain_diamond_set", 0, MaterialClass.DIAMOND));
        register(new SmithingExpGainSetReward("smithing_expgain_netherite_set", 0, MaterialClass.NETHERITE));
        register(new SmithingExpGainSetReward("smithing_expgain_bow_set", 0, MaterialClass.BOW));
        register(new SmithingExpGainSetReward("smithing_expgain_crossbow_set", 0, MaterialClass.CROSSBOW));
        register(new SmithingExpGainSetReward("smithing_expgain_membrane_set", 0, MaterialClass.MEMBRANE));
        register(new SmithingExpGainSetReward("smithing_expgain_prismarine_set", 0, MaterialClass.PRISMARINE));

        // ALCHEMY
        register(new AlchemyBuffSkillAddReward("alchemy_quality_buff_add", 0));
        register(new AlchemyBuffSkillSetReward("alchemy_quality_buff_set", 0));
        register(new AlchemyDebuffSkillAddReward("alchemy_quality_debuff_add", 0));
        register(new AlchemyDebuffSkillSetReward("alchemy_quality_debuff_set", 0));
        register(new AlchemySkillAddReward("alchemy_quality_general_add", 0));
        register(new AlchemySkillSetReward("alchemy_quality_general_set", 0));
        register(new AlchemyExpGainAddReward("alchemy_expgain_add", 0));
        register(new AlchemyExpGainSetReward("alchemy_expgain_set", 0));
        register(new AlchemyBrewTimeMultiplierAddReward("alchemy_speed_multiplier_add", 0));
        register(new AlchemyBrewTimeMultiplierSetReward("alchemy_speed_multiplier_set", 0));
        register(new AlchemyIngredientSaveChanceAddReward("alchemy_ingredient_save_chance_add", 0));
        register(new AlchemyIngredientSaveChanceSetReward("alchemy_ingredient_save_chance_set", 0));
        register(new AlchemyPotionSaveChanceAddReward("alchemy_potion_save_chance_add", 0));
        register(new AlchemyPotionSaveChanceSetReward("alchemy_potion_save_chance_set", 0));
        register(new AlchemyPotionThrowVelocityAddReward("alchemy_throw_velocity_add", 0));
        register(new AlchemyPotionThrowVelocitySetReward("alchemy_throw_velocity_set", 0));

        register(new AlchemyUnlockTransmutationsReward("alchemy_unlock_transmutations", new ArrayList<>()));
        register(new AlchemyLockTransmutationsReward("alchemy_lock_transmutations", new ArrayList<>()));
        register(new AlchemyLockAllTransmutationsReward("alchemy_lock_all_transmutations", 0));
        register(new AlchemyUnlockAllTransmutationsReward("alchemy_unlock_all_transmutations", 0));

        // ENCHANTING
        register(new EnchantingAllowedEnchantmentsAddReward("enchanting_max_custom_allowed_add", 0));
        register(new EnchantingAllowedEnchantmentsSetReward("enchanting_max_custom_allowed_set", 0));
        register(new EnchantingAmplifyChanceAddReward("enchanting_amplify_chance_add", 0F));
        register(new EnchantingAmplifyChanceSetReward("enchanting_amplify_chance_set", 0F));
        register(new EnchantingRefundAmountAddReward("enchanting_exprefund_amount_add", 0F));
        register(new EnchantingRefundAmountSetReward("enchanting_exprefund_amount_set", 0F));
        register(new EnchantingRefundChanceAddReward("enchanting_exprefund_chance_add", 0F));
        register(new EnchantingRefundChanceSetReward("enchanting_exprefund_chance_set", 0F));
        register(new EnchantingLapisSaveChanceAddReward("enchanting_lapis_save_chance_add", 0F));
        register(new EnchantingLapisSaveChanceSetReward("enchanting_lapis_save_chance_set", 0F));
        register(new EnchantingSkillAddReward("enchanting_quality_general_add", 0F, null));
        register(new EnchantingSkillAddReward("enchanting_quality_vanilla_add", 0F, EnchantmentType.VANILLA));
        register(new EnchantingSkillAddReward("enchanting_quality_custom_add", 0F, EnchantmentType.CUSTOM));
        register(new EnchantingSkillSetReward("enchanting_quality_general_set", 0F, null));
        register(new EnchantingSkillSetReward("enchanting_quality_vanilla_set", 0F, EnchantmentType.VANILLA));
        register(new EnchantingSkillSetReward("enchanting_quality_custom_set", 0F, EnchantmentType.CUSTOM));
        register(new EnchantingSkillExpGainAddReward("enchanting_skill_expgain_general_add", 0F, null));
        register(new EnchantingSkillExpGainAddReward("enchanting_skill_expgain_vanilla_add", 0F, EnchantmentType.VANILLA));
        register(new EnchantingSkillExpGainAddReward("enchanting_skill_expgain_custom_add", 0F, EnchantmentType.CUSTOM));
        register(new EnchantingSkillExpGainSetReward("enchanting_skill_expgain_general_set", 0F, null));
        register(new EnchantingSkillExpGainSetReward("enchanting_skill_expgain_vanilla_set", 0F,EnchantmentType.VANILLA));
        register(new EnchantingSkillExpGainSetReward("enchanting_skill_expgain_custom_set", 0F, EnchantmentType.CUSTOM));
        register(new EnchantingVanillaExpGainAddReward("enchanting_vanilla_expgain_add", 0F));
        register(new EnchantingVanillaExpGainSetReward("enchanting_vanilla_expgain_set", 0F));

        // FARMING
        register(new FarmingAnimalAgeMultiplierAddReward("farming_baby_animal_age_multiplier_add", 0F));
        register(new FarmingAnimalAgeMultiplierSetReward("farming_baby_animal_age_multiplier_set", 0F));
        register(new FarmingAnimalDamageMultiplierAddReward("farming_animal_damage_multiplier_add", 0F));
        register(new FarmingAnimalDamageMultiplierSetReward("farming_animal_damage_multiplier_set", 0F));
        register(new FarmingAnimalDropMultiplierAddReward("farming_animal_drop_multiplier_add", 0F));
        register(new FarmingAnimalDropMultiplierSetReward("farming_animal_drop_multiplier_set", 0F));
        register(new FarmingAnimalRareDropRateAddReward("farming_animal_rare_drop_multiplier_add", 0F));
        register(new FarmingAnimalRareDropRateSetReward("farming_animal_rare_drop_multiplier_set", 0F));
        register(new FarmingBeeAggroImmunityReward("farming_bee_aggro_immunity", false));
        register(new FarmingBadFoodImmunityReward("farming_bad_food_immunity", false));
        register(new FarmingBreedingEXPMultiplierAddReward("farming_breeding_exp_multiplier_add", 0F));
        register(new FarmingBreedingEXPMultiplierSetReward("farming_breeding_exp_multiplier_set", 0F));
        register(new FarmingBreedingVanillaEXPMultiplierAddReward("farming_breeding_vanilla_exp_multiplier_add", 0F));
        register(new FarmingBreedingVanillaEXPMultiplierSetReward("farming_breeding_vanilla_exp_multiplier_set", 0F));
        register(new FarmingDropMultiplierAddReward("farming_drop_multiplier_add", 0F));
        register(new FarmingDropMultiplierSetReward("farming_drop_multiplier_set", 0F));
        register(new FarmingFarmingEXPMultiplierAddReward("farming_farming_exp_multiplier_add", 0F));
        register(new FarmingFarmingEXPMultiplierSetReward("farming_farming_exp_multiplier_set", 0F));
        register(new FarmingFarmingEXPRewardAddReward("farming_vanilla_exp_reward_add", 0F));
        register(new FarmingFarmingEXPRewardSetReward("farming_vanilla_exp_reward_set", 0F));
        register(new FarmingFishingEXPMultiplierAddReward("farming_fishing_exp_multiplier_add", 0F));
        register(new FarmingFishingEXPMultiplierSetReward("farming_fishing_exp_multiplier_set", 0F));
        register(new FarmingFishingRewardTierAddReward("farming_fishing_reward_tier_add", 0F));
        register(new FarmingFishingRewardTierSetReward("farming_fishing_reward_tier_set", 0F));
        register(new FarmingFishingTimeMultiplierAddReward("farming_fishing_time_multiplier_add", 0F));
        register(new FarmingFishingTimeMultiplierSetReward("farming_fishing_time_multiplier_set", 0F));
        register(new FarmingFishingVanillaEXPMultiplierAddReward("farming_fishing_vanilla_experience_multiplier_add", 0F));
        register(new FarmingFishingVanillaEXPMultiplierSetReward("farming_fishing_vanilla_experience_multiplier_set", 0F));
        register(new FarmingGeneralEXPMultiplierAddReward("farming_general_exp_multiplier_add", 0F));
        register(new FarmingGeneralEXPMultiplierSetReward("farming_general_exp_multiplier_set", 0F));
        register(new FarmingHiveHoneySaveChanceAddReward("farming_hive_honey_not_consume_chance_add", 0F));
        register(new FarmingHiveHoneySaveChanceSetReward("farming_hive_honey_not_consume_chance_set", 0F));
        register(new FarmingInstantDropSetReward("farming_instant_harvesting", 0F));
        register(new FarmingInstantGrowthRateAddReward("farming_instant_growth_rate_add", 0F));
        register(new FarmingInstantGrowthRateSetReward("farming_instant_growth_rate_set", 0F));
        register(new FarmingRareDropRateAddReward("farming_rare_drop_rate_multiplier_add", 0F));
        register(new FarmingRareDropRateSetReward("farming_rare_drop_rate_multiplier_set", 0F));
        register(new FarmingUltraHarvestCooldownAddReward("farming_ultra_harvest_cooldown_add", 0));
        register(new FarmingUltraHarvestCooldownSetReward("farming_ultra_harvest_cooldown_set", 0));
        register(new FarmingFoodCarnivorousMultiplierAddReward("farming_carnivorous_food_multiplier_add", 0D));
        register(new FarmingFoodCarnivorousMultiplierSetReward("farming_carnivorous_food_multiplier_set", 0D));
        register(new FarmingFoodVegetarianMultiplierAddReward("farming_vegetarian_food_multiplier_add", 0D));
        register(new FarmingFoodVegetarianMultiplierSetReward("farming_vegetarian_food_multiplier_set", 0D));
        register(new FarmingFoodPescotarianMultiplierAddReward("farming_pescotarian_food_multiplier_add", 0D));
        register(new FarmingFoodPescotarianMultiplierSetReward("farming_pescotarian_food_multiplier_set", 0D));
        register(new FarmingFoodGarbageMultiplierAddReward("farming_garbage_food_multiplier_add", 0D));
        register(new FarmingFoodGarbageMultiplierSetReward("farming_garbage_food_multiplier_set", 0D));
        register(new FarmingFoodMagicalMultiplierAddReward("farming_magical_food_multiplier_add", 0D));
        register(new FarmingFoodMagicalMultiplierSetReward("farming_magical_food_multiplier_set", 0D));

        // MINING
        register(new MiningBlastDamageMultiplierAddReward("blast_mining_tnt_damage_multiplier_add", 0F));
        register(new MiningBlastDamageMultiplierSetReward("blast_mining_tnt_damage_multiplier_set", 0F));
        register(new MiningBlastDropMultiplierAddReward("blast_mining_drop_multiplier_add", 0F));
        register(new MiningBlastDropMultiplierSetReward("blast_mining_drop_multiplier_set", 0F));
        register(new MiningBlastEXPMultiplierAddReward("mining_expgain_blast_add", 0F));
        register(new MiningBlastEXPMultiplierSetReward("mining_expgain_blast_set", 0F));
        register(new MiningBlastRadiusMultiplierAddReward("blast_mining_tnt_radius_multiplier_add", 0F));
        register(new MiningBlastRadiusMultiplierSetReward("blast_mining_tnt_radius_multiplier_set", 0F));
        register(new MiningBlastRareDropMultiplierAddReward("blast_mining_rare_drop_rate_multiplier_add", 0F));
        register(new MiningBlastRareDropMultiplierSetReward("blast_mining_rare_drop_rate_multiplier_set", 0F));
        register(new MiningBlockBreakEXPRewardAddReward("mining_block_experience_rate_add", 0F));
        register(new MiningBlockBreakEXPRewardSetReward("mining_block_experience_rate_set", 0F));
        register(new MiningGeneralEXPMultiplierAddReward("mining_expgain_general_add", 0F));
        register(new MiningGeneralEXPMultiplierSetReward("mining_expgain_general_set", 0F));
        register(new MiningMiningDropMultiplierAddReward("mining_drop_multiplier_add", 0F));
        register(new MiningMiningDropMultiplierSetReward("mining_drop_multiplier_set", 0F));
        register(new MiningMiningEXPMultiplierAddReward("mining_expgain_mining_add", 0F));
        register(new MiningMiningEXPMultiplierSetReward("mining_expgain_mining_set", 0F));
        register(new MiningMiningRareDropMultiplierAddReward("mining_rare_drop_rate_multiplier_add", 0F));
        register(new MiningMiningRareDropMultiplierSetReward("mining_rare_drop_rate_multiplier_set", 0F));
        register(new MiningOreExperienceMultiplierAddReward("mining_ore_experience_multiplier_add", 0F));
        register(new MiningOreExperienceMultiplierSetReward("mining_ore_experience_multiplier_set", 0F));
        register(new MiningQuickMineDrainSpeedAddReward("mining_quickmode_rate_add", 0F));
        register(new MiningQuickMineDrainSpeedSetReward("mining_quickmode_rate_set", 0F));
        register(new MiningQuickMineCooldownAddReward("mining_quickmode_cooldown_add", 0F));
        register(new MiningQuickMineCooldownSetReward("mining_quickmode_cooldown_set", 0F));
        register(new MiningUnbreakableBlocksAddReward("mining_unbreakable_blocks_add", new ArrayList<>()));
        register(new MiningUnbreakableBlocksRemoveReward("mining_unbreakable_blocks_remove", new ArrayList<>()));
        register(new MiningUnbreakableBlocksClearReward("mining_unbreakable_blocks_clear", null));
        register(new MiningVeinMineBlocksAddReward("mining_vein_mining_blocks_add", new ArrayList<>()));
        register(new MiningVeinMineBlocksRemoveReward("mining_vein_mining_blocks_remove", new ArrayList<>()));
        register(new MiningVeinMineBlocksClearReward("mining_vein_mining_blocks_clear", null));
        register(new MiningVeinMineCooldownAddReward("mining_vein_mining_cooldown_add", 0F));
        register(new MiningVeinMineCooldownSetReward("mining_vein_mining_cooldown_set", 0F));
        register(new MiningBlastFortuneLevelAddReward("blast_mining_fortune_level_add", 0));
        register(new MiningBlastFortuneLevelSetReward("blast_mining_fortune_level_set", 0));

        // LANDSCAPING
        //register(new LandscapingBlockPlaceRangeAddReward("landscaping_block_placement_range_add", 0));
        //register(new LandscapingBlockPlaceRangeSetReward("landscaping_block_placement_range_set", 0));
        register(new LandscapingDiggingDropMultiplierAddReward("digging_drop_multiplier_add", 0));
        register(new LandscapingDiggingDropMultiplierSetReward("digging_drop_multiplier_set", 0));
        register(new LandscapingDiggingEXPMultiplierAddReward("landscaping_expgain_digging_add", 0));
        register(new LandscapingDiggingEXPMultiplierSetReward("landscaping_expgain_digging_set", 0));
        register(new LandscapingDiggingRareDropMultiplierAddReward("digging_rare_drop_rate_multiplier_add", 0));
        register(new LandscapingDiggingRareDropMultiplierSetReward("digging_rare_drop_rate_multiplier_set", 0));
        register(new LandscapingGeneralEXPMultiplierAddReward("landscaping_expgain_general_add", 0));
        register(new LandscapingGeneralEXPMultiplierSetReward("landscaping_expgain_general_set", 0));
        register(new LandscapingInstantGrowthRateAddReward("landscaping_instant_growth_rate_add", 0));
        register(new LandscapingInstantGrowthRateSetReward("landscaping_instant_growth_rate_set", 0));
        register(new LandscapingLockConversionsReward("landscaping_lock_conversions", new ArrayList<>()));
        register(new LandscapingUnlockConversionsReward("landscaping_unlock_conversions", new ArrayList<>()));
        register(new LandscapingWoodcuttingDropMultiplierAddReward("woodcutting_drop_multiplier_add", 0));
        register(new LandscapingWoodcuttingDropMultiplierSetReward("woodcutting_drop_multiplier_set", 0));
        register(new LandscapingWoodcuttingEXPMultiplierAddReward("landscaping_expgain_woodcutting_add", 0));
        register(new LandscapingWoodcuttingEXPMultiplierSetReward("landscaping_expgain_woodcutting_set", 0));
        register(new LandscapingWoodcuttingRareDropMultiplierAddReward("woodcutting_rare_drop_rate_multiplier_add", 0));
        register(new LandscapingWoodcuttingRareDropMultiplierSetReward("woodcutting_rare_drop_rate_multiplier_set", 0));
        register(new LandscapingWoodstrippingRareDropMultiplierAddReward("woodstripping_rare_drop_rate_multiplier_add", 0));
        register(new LandscapingWoodstrippingRareDropMultiplierSetReward("woodstripping_rare_drop_rate_multiplier_set", 0));
        register(new LandscapingSaplingAutoReplaceReward("landscaping_sapling_auto_replace", false));
        register(new LandscapingTreeCapitatorCooldownAddReward("landscaping_tree_capitator_cooldown_add", 0));
        register(new LandscapingTreeCapitatorCooldownSetReward("landscaping_tree_capitator_cooldown_set", 0));
        register(new LandscapingUnlockAllConversionsReward("landscaping_unlock_all_conversions", null));
        register(new LandscapingLockAllConversionsReward("landscaping_lock_all_conversions", null));
        register(new LandscapingTreeCapitatorBlocksAddReward("landscaping_tree_capitator_blocks_add", new ArrayList<>()));
        register(new LandscapingTreeCapitatorBlocksRemoveReward("landscaping_tree_capitator_blocks_remove", new ArrayList<>()));
        register(new LandscapingTreeCapitatorBlocksClearReward("landscaping_tree_capitator_blocks_clear", new ArrayList<>()));
        register(new LandscapingDiggingEXPRewardAddReward("digging_experience_rate_add", 0));
        register(new LandscapingDiggingEXPRewardSetReward("digging_experience_rate_set", 0));
        register(new LandscapingWoodcuttingEXPRewardAddReward("woodcutting_experience_rate_add", 0));
        register(new LandscapingWoodcuttingEXPRewardSetReward("woodcutting_experience_rate_set", 0));

        register(new ArcheryAmmoSaveChanceAddReward("archery_ammo_save_chance_add", 0));
        register(new ArcheryAmmoSaveChanceSetReward("archery_ammo_save_chance_set", 0));
        register(new ArcheryBowCritChanceAddReward("archery_bow_crit_chance_add", 0));
        register(new ArcheryBowCritChanceSetReward("archery_bow_crit_chance_set", 0));
        register(new ArcheryBowDamageMultiplierAddReward("archery_bow_damage_multiplier_add", 0));
        register(new ArcheryBowDamageMultiplierSetReward("archery_bow_damage_multiplier_set", 0));
        register(new ArcheryChargedShotCooldownAddReward("archery_charged_shot_cooldown_add", 0));
        register(new ArcheryChargedShotCooldownSetReward("archery_charged_shot_cooldown_set", 0));
        register(new ArcheryChargedShotDamageMultiplierAddReward("archery_charged_shot_damage_multiplier_add", 0));
        register(new ArcheryChargedShotDamageMultiplierSetReward("archery_charged_shot_damage_multiplier_set", 0));
        register(new ArcheryChargedShotKnockbackAddReward("archery_charged_shot_knockback_add", 0));
        register(new ArcheryChargedShotKnockbackSetReward("archery_charged_shot_knockback_set", 0));
        register(new ArcheryCritDamageMultiplierAddReward("archery_crit_damage_multiplier_add", 0));
        register(new ArcheryCritDamageMultiplierSetReward("archery_crit_damage_multiplier_set", 0));
        register(new ArcheryCritOnFacingAwayReward("archery_crit_on_facing_away", false));
        register(new ArcheryCritOnStandingStillReward("archery_crit_on_standing_still", false));
        register(new ArcheryCritOnStealthReward("archery_crit_on_stealth", false));
        register(new ArcheryCrossBowCritChanceAddReward("archery_crossbow_crit_chance_add", 0));
        register(new ArcheryCrossBowCritChanceSetReward("archery_crossbow_crit_chance_set", 0));
        register(new ArcheryCrossBowDamageMultiplierAddReward("archery_crossbow_damage_multiplier_add", 0));
        register(new ArcheryCrossBowDamageMultiplierSetReward("archery_crossbow_damage_multiplier_set", 0));
        register(new ArcheryDamageDistanceBaseMultiplierAddReward("archery_damage_distance_base_multiplier_add", 0));
        register(new ArcheryDamageDistanceBaseMultiplierSetReward("archery_damage_distance_base_multiplier_set", 0));
        register(new ArcheryDamageDistanceMultiplierAddReward("archery_damage_distance_multiplier_add", 0));
        register(new ArcheryDamageDistanceMultiplierSetReward("archery_damage_distance_multiplier_set", 0));
        register(new ArcheryInaccuracyAddReward("archery_inaccuracy_add", 0));
        register(new ArcheryInaccuracySetReward("archery_inaccuracy_set", 0));
        register(new ArcheryInfinityDamageMultiplierAddReward("archery_infinity_damage_multiplier_add", 0));
        register(new ArcheryInfinityDamageMultiplierSetReward("archery_infinity_damage_multiplier_set", 0));
        register(new ArcheryStunChanceAddReward("archery_stun_chance_add", 0));
        register(new ArcheryStunChanceSetReward("archery_stun_chance_set", 0));
        register(new ArcheryStunDurationAddReward("archery_stun_duration_add", 0));
        register(new ArcheryStunDurationSetReward("archery_stun_duration_set", 0));
        register(new ArcheryStunOnCritReward("archery_stun_on_crit", 0));
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
            ValhallaMMO.getPlugin().getLogger().severe("[ValhallaMMO] Could not clone PerkReward, notify plugin author");
            return null;
        }
    }

    public Map<String, PerkReward> getPerkRewards() {
        return perkRewards;
    }
}
