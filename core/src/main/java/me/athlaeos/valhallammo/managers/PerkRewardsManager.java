package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.items.EnchantmentType;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.perkrewards.account.*;
import me.athlaeos.valhallammo.perkrewards.alchemy.*;
import me.athlaeos.valhallammo.perkrewards.archery.*;
import me.athlaeos.valhallammo.perkrewards.enchanting.*;
import me.athlaeos.valhallammo.perkrewards.farming.*;
import me.athlaeos.valhallammo.perkrewards.heavy_armor.*;
import me.athlaeos.valhallammo.perkrewards.heavy_weapons.*;
import me.athlaeos.valhallammo.perkrewards.landscaping.*;
import me.athlaeos.valhallammo.perkrewards.light_armor.*;
import me.athlaeos.valhallammo.perkrewards.light_weapons.*;
import me.athlaeos.valhallammo.perkrewards.mining.*;
import me.athlaeos.valhallammo.perkrewards.smithing.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PerkRewardsManager {
    private static PerkRewardsManager manager = null;

    private final Map<String, PerkReward> perkRewards = new HashMap<>();

    public PerkRewardsManager(){

        // ACCOUNT
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

        register(new AccountArmorBonusAddReward("player_base_bonus_armor_add", 0));
        register(new AccountArmorBonusSetReward("player_base_bonus_armor_set", 0));
        register(new AccountArmorMultiplierBonusAddReward("player_bonus_armor_multiplier_add", 0));
        register(new AccountArmorMultiplierBonusSetReward("player_bonus_armor_multiplier_set", 0));
        register(new AccountAttackDamageBonusAddReward("player_base_attack_damage_add", 0));
        register(new AccountAttackDamageBonusSetReward("player_base_attack_damage_set", 0));
        register(new AccountAttackSpeedBonusAddReward("player_base_attack_speed_add", 0));
        register(new AccountAttackSpeedBonusSetReward("player_base_attack_speed_set", 0));
        register(new AccountCooldownReductionAddReward("player_cooldown_reduction_add", 0));
        register(new AccountCooldownReductionSetReward("player_cooldown_reduction_set", 0));
        register(new AccountDamageResistanceAddReward("player_damage_resistance_add", 0));
        register(new AccountDamageResistanceSetReward("player_damage_resistance_set", 0));
        register(new AccountExplosionResistanceAddReward("player_explosion_resistance_add", 0));
        register(new AccountExplosionResistanceSetReward("player_explosion_resistance_set", 0));
        register(new AccountFallDamageResistanceAddReward("player_fall_damage_resistance_add", 0));
        register(new AccountFallDamageResistanceSetReward("player_fall_damage_resistance_set", 0));
        register(new AccountFireResistanceAddReward("player_fire_resistance_add", 0));
        register(new AccountFireResistanceSetReward("player_fire_resistance_set", 0));
        register(new AccountHealthBonusAddReward("player_base_bonus_health_add", 0));
        register(new AccountHealthBonusSetReward("player_base_bonus_health_set", 0));
        register(new AccountHealthRegenerationAddReward("player_health_regeneration_add", 0));
        register(new AccountHealthRegenerationSetReward("player_health_regeneration_set", 0));
        register(new AccountHungerSaveChanceAddReward("player_hunger_save_chance_add", 0));
        register(new AccountHungerSaveChanceSetReward("player_hunger_save_chance_set", 0));
        register(new AccountKnockbackResistanceAddReward("player_knockback_resistance_add", 0));
        register(new AccountKnockbackResistanceSetReward("player_knockback_resistance_set", 0));
        register(new AccountLuckBonusAddReward("player_base_luck_add", 0));
        register(new AccountLuckBonusSetReward("player_base_luck_set", 0));
        register(new AccountMagicResistanceAddReward("player_magic_resistance_add", 0));
        register(new AccountMagicResistanceSetReward("player_magic_resistance_set", 0));
        register(new AccountMeleeResistanceAddReward("player_melee_resistance_add", 0));
        register(new AccountMeleeResistanceSetReward("player_melee_resistance_set", 0));
        register(new AccountBleedResistanceAddReward("player_bleed_resistance_add", 0));
        register(new AccountBleedResistanceSetReward("player_bleed_resistance_set", 0));
        register(new AccountMovementSpeedBonusAddReward("player_movement_speed_bonus_add", 0));
        register(new AccountMovementSpeedBonusSetReward("player_movement_speed_bonus_set", 0));
        register(new AccountPoisonResistanceAddReward("player_poison_resistance_add", 0));
        register(new AccountPoisonResistanceSetReward("player_poison_resistance_set", 0));
        register(new AccountProjectileResistanceAddReward("player_projectile_resistance_add", 0));
        register(new AccountProjectileResistanceSetReward("player_projectile_resistance_set", 0));
        register(new AccountToughnessBonusAddReward("player_base_bonus_armor_toughness_add", 0));
        register(new AccountToughnessBonusSetReward("player_base_bonus_armor_toughness_set", 0));
        register(new AccountImmunityFramesBonusAddReward("player_immunity_frames_bonus_add", 0));
        register(new AccountImmunityFramesBonusSetReward("player_immunity_frames_bonus_set", 0));
        register(new AccountStunResistanceAddReward("player_stun_resistance_add", 0));
        register(new AccountStunResistanceSetReward("player_stun_resistance_set", 0));

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

        register(new SmithingCraftingTimeReductionAddReward("smithing_crafting_time_reduction_add", 0));
        register(new SmithingCraftingTimeReductionSetReward("smithing_crafting_time_reduction_set", 0));

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
        register(new EnchantingLapisSaveChanceAddReward("enchanting_lapis_refund_chance_add", 0F));
        register(new EnchantingLapisSaveChanceSetReward("enchanting_lapis_refund_chance_set", 0F));
        register(new EnchantingSkillAddReward("enchanting_quality_general_add", 0F, null));
        register(new EnchantingSkillAddReward("enchanting_quality_vanilla_add", 0F, EnchantmentType.VANILLA));
        register(new EnchantingSkillAddReward("enchanting_quality_custom_add", 0F, EnchantmentType.CUSTOM));
        register(new EnchantingAnvilSkillAddReward("enchanting_quality_anvil_add", 0F));
        register(new EnchantingSkillSetReward("enchanting_quality_general_set", 0F, null));
        register(new EnchantingSkillSetReward("enchanting_quality_vanilla_set", 0F, EnchantmentType.VANILLA));
        register(new EnchantingSkillSetReward("enchanting_quality_custom_set", 0F, EnchantmentType.CUSTOM));
        register(new EnchantingAnvilSkillSetReward("enchanting_quality_anvil_set", 0F));
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

        // ARCHERY
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
        register(new ArcheryChargedShotVelocityBonusAddReward("archery_charged_shot_velocity_bonus_add", 0));
        register(new ArcheryChargedShotVelocityBonusSetReward("archery_charged_shot_velocity_bonus_set", 0));
        register(new ArcheryChargedShotPiercingBonusAddReward("archery_charged_shot_piercing_bonus_add", 0));
        register(new ArcheryChargedShotPiercingBonusSetReward("archery_charged_shot_piercing_bonus_set", 0));
        register(new ArcheryChargedShotChargesAddReward("archery_charged_shot_charges_add", 0));
        register(new ArcheryChargedShotChargesSetReward("archery_charged_shot_charges_set", 0));
        register(new ArcheryChargedShotFullVelocityReward("archery_charged_shot_full_velocity", 0));

        // LIGHT ARMOR
        register(new LightArmorAdrenalineCooldownAddReward("light_armor_adrenaline_cooldown_add", 0));
        register(new LightArmorAdrenalineCooldownSetReward("light_armor_adrenaline_cooldown_set", 0));
        register(new LightArmorAdrenalineLevelAddReward("light_armor_adrenaline_level_add", 0));
        register(new LightArmorAdrenalineLevelSetReward("light_armor_adrenaline_level_set", 0));
        register(new LightArmorAdrenalineThresholdAddReward("light_armor_adrenaline_threshold_add", 0));
        register(new LightArmorAdrenalineThresholdSetReward("light_armor_adrenaline_threshold_set", 0));
        register(new LightArmorArmorMultiplierAddReward("light_armor_armor_multiplier_add", 0));
        register(new LightArmorArmorMultiplierSetReward("light_armor_armor_multiplier_set", 0));
        register(new LightArmorDamageResistanceAddReward("light_armor_damage_resistance_add", 0));
        register(new LightArmorDamageResistanceSetReward("light_armor_damage_resistance_set", 0));
        register(new LightArmorExplosionResistanceAddReward("light_armor_explosion_resistance_add", 0));
        register(new LightArmorExplosionResistanceSetReward("light_armor_explosion_resistance_set", 0));
        register(new LightArmorEXPMultiplierAddReward("light_armor_expgain_add", 0));
        register(new LightArmorEXPMultiplierSetReward("light_armor_expgain_set", 0));
        register(new LightArmorFallDamageResistanceAddReward("light_armor_fall_damage_resistance_add", 0));
        register(new LightArmorFallDamageResistanceSetReward("light_armor_fall_damage_resistance_set", 0));
        register(new LightArmorFireResistanceAddReward("light_armor_fire_resistance_add", 0));
        register(new LightArmorFireResistanceSetReward("light_armor_fire_resistance_set", 0));
        register(new LightArmorImmunePotionEffectsAddReward("light_armor_immune_potion_effects_add", new ArrayList<>()));
        register(new LightArmorImmunePotionEffectsRemoveReward("light_armor_immune_potion_effects_remove", new ArrayList<>()));
        register(new LightArmorImmunePotionEffectsClearReward("light_armor_immune_potion_effects_clear", null));
        register(new LightArmorKnockbackResistanceAddReward("light_armor_knockback_resistance_add", 0));
        register(new LightArmorKnockbackResistanceSetReward("light_armor_knockback_resistance_set", 0));
        register(new LightArmorBleedResistanceAddReward("light_armor_bleed_resistance_add", 0));
        register(new LightArmorBleedResistanceSetReward("light_armor_bleed_resistance_set", 0));
        register(new LightArmorMagicResistanceAddReward("light_armor_magic_resistance_add", 0));
        register(new LightArmorMagicResistanceSetReward("light_armor_magic_resistance_set", 0));
        register(new LightArmorMeleeResistanceAddReward("light_armor_melee_resistance_add", 0));
        register(new LightArmorMeleeResistanceSetReward("light_armor_melee_resistance_set", 0));
        register(new LightArmorMovementSpeedPenaltyAddReward("light_armor_movement_speed_penalty_add", 0));
        register(new LightArmorMovementSpeedPenaltySetReward("light_armor_movement_speed_penalty_set", 0));
        register(new LightArmorProjectileResistanceAddReward("light_armor_projectile_resistance_add", 0));
        register(new LightArmorProjectileResistanceSetReward("light_armor_projectile_resistance_set", 0));
        register(new LightArmorSetBonusArmorAddReward("light_armor_set_bonus_armor_add", 0));
        register(new LightArmorSetBonusArmorSetReward("light_armor_set_bonus_armor_set", 0));
        register(new LightArmorSetBonusBleedResistanceAddReward("light_armor_set_bonus_bleed_resistance_add", 0));
        register(new LightArmorSetBonusBleedResistanceSetReward("light_armor_set_bonus_bleed_resistance_set", 0));
        register(new LightArmorSetBonusDodgeChanceAddReward("light_armor_set_bonus_dodge_chance_add", 0));
        register(new LightArmorSetBonusDodgeChanceSetReward("light_armor_set_bonus_dodge_chance_set", 0));
        register(new LightArmorSetBonusHungerSaveChanceAddReward("light_armor_set_bonus_hunger_save_chance_add", 0));
        register(new LightArmorSetBonusHungerSaveChanceSetReward("light_armor_set_bonus_hunger_save_chance_set", 0));
        register(new LightArmorSetBonusHealingBonusAddReward("light_armor_set_bonus_healing_bonus_add", 0));
        register(new LightArmorSetBonusHealingBonusSetReward("light_armor_set_bonus_healing_bonus_set", 0));
        register(new LightArmorSetBonusPiecesRequiredAddReward("light_armor_set_bonus_pieces_required_add", 0));
        register(new LightArmorSetBonusPiecesRequiredSetReward("light_armor_set_bonus_pieces_required_set", 0));

        // HEAVY ARMOR
        register(new HeavyArmorRageCooldownAddReward("heavy_armor_rage_cooldown_add", 0));
        register(new HeavyArmorRageCooldownSetReward("heavy_armor_rage_cooldown_set", 0));
        register(new HeavyArmorRageLevelAddReward("heavy_armor_rage_level_add", 0));
        register(new HeavyArmorRageLevelSetReward("heavy_armor_rage_level_set", 0));
        register(new HeavyArmorRageThresholdAddReward("heavy_armor_rage_threshold_add", 0));
        register(new HeavyArmorRageThresholdSetReward("heavy_armor_rage_threshold_set", 0));
        register(new HeavyArmorArmorMultiplierAddReward("heavy_armor_armor_multiplier_add", 0));
        register(new HeavyArmorArmorMultiplierSetReward("heavy_armor_armor_multiplier_set", 0));
        register(new HeavyArmorDamageResistanceAddReward("heavy_armor_damage_resistance_add", 0));
        register(new HeavyArmorDamageResistanceSetReward("heavy_armor_damage_resistance_set", 0));
        register(new HeavyArmorExplosionResistanceAddReward("heavy_armor_explosion_resistance_add", 0));
        register(new HeavyArmorExplosionResistanceSetReward("heavy_armor_explosion_resistance_set", 0));
        register(new HeavyArmorEXPMultiplierAddReward("heavy_armor_expgain_add", 0));
        register(new HeavyArmorEXPMultiplierSetReward("heavy_armor_expgain_set", 0));
        register(new HeavyArmorFallDamageResistanceAddReward("heavy_armor_fall_damage_resistance_add", 0));
        register(new HeavyArmorFallDamageResistanceSetReward("heavy_armor_fall_damage_resistance_set", 0));
        register(new HeavyArmorFireResistanceAddReward("heavy_armor_fire_resistance_add", 0));
        register(new HeavyArmorFireResistanceSetReward("heavy_armor_fire_resistance_set", 0));
        register(new HeavyArmorImmunePotionEffectsAddReward("heavy_armor_immune_potion_effects_add", new ArrayList<>()));
        register(new HeavyArmorImmunePotionEffectsRemoveReward("heavy_armor_immune_potion_effects_remove", new ArrayList<>()));
        register(new HeavyArmorImmunePotionEffectsClearReward("heavy_armor_immune_potion_effects_clear", null));
        register(new HeavyArmorKnockbackResistanceAddReward("heavy_armor_knockback_resistance_add", 0));
        register(new HeavyArmorKnockbackResistanceSetReward("heavy_armor_knockback_resistance_set", 0));
        register(new HeavyArmorBleedResistanceAddReward("heavy_armor_bleed_resistance_add", 0));
        register(new HeavyArmorBleedResistanceSetReward("heavy_armor_bleed_resistance_set", 0));
        register(new HeavyArmorMagicResistanceAddReward("heavy_armor_magic_resistance_add", 0));
        register(new HeavyArmorMagicResistanceSetReward("heavy_armor_magic_resistance_set", 0));
        register(new HeavyArmorMeleeResistanceAddReward("heavy_armor_melee_resistance_add", 0));
        register(new HeavyArmorMeleeResistanceSetReward("heavy_armor_melee_resistance_set", 0));
        register(new HeavyArmorMovementSpeedPenaltyAddReward("heavy_armor_movement_speed_penalty_add", 0));
        register(new HeavyArmorMovementSpeedPenaltySetReward("heavy_armor_movement_speed_penalty_set", 0));
        register(new HeavyArmorProjectileResistanceAddReward("heavy_armor_projectile_resistance_add", 0));
        register(new HeavyArmorProjectileResistanceSetReward("heavy_armor_projectile_resistance_set", 0));
        register(new HeavyArmorSetBonusArmorAddReward("heavy_armor_set_bonus_armor_add", 0));
        register(new HeavyArmorSetBonusArmorSetReward("heavy_armor_set_bonus_armor_set", 0));
        register(new HeavyArmorSetBonusReflectChanceAddReward("heavy_armor_set_bonus_reflect_chance_add", 0));
        register(new HeavyArmorSetBonusReflectChanceSetReward("heavy_armor_set_bonus_reflect_chance_set", 0));
        register(new HeavyArmorSetBonusBleedResistanceAddReward("heavy_armor_set_bonus_bleed_resistance_add", 0));
        register(new HeavyArmorSetBonusBleedResistanceSetReward("heavy_armor_set_bonus_bleed_resistance_set", 0));
        register(new HeavyArmorReflectFractionAddReward("heavy_armor_reflect_fraction_add", 0));
        register(new HeavyArmorReflectFractionSetReward("heavy_armor_reflect_fraction_set", 0));
        register(new HeavyArmorSetBonusHungerSaveChanceAddReward("heavy_armor_set_bonus_hunger_save_chance_add", 0));
        register(new HeavyArmorSetBonusHungerSaveChanceSetReward("heavy_armor_set_bonus_hunger_save_chance_set", 0));
        register(new HeavyArmorSetBonusHealingBonusAddReward("heavy_armor_set_bonus_healing_bonus_add", 0));
        register(new HeavyArmorSetBonusHealingBonusSetReward("heavy_armor_set_bonus_healing_bonus_set", 0));
        register(new HeavyArmorSetBonusPiecesRequiredAddReward("heavy_armor_set_bonus_pieces_required_add", 0));
        register(new HeavyArmorSetBonusPiecesRequiredSetReward("heavy_armor_set_bonus_pieces_required_set", 0));

        // LIGHT WEAPONS
        register(new LightWeaponsAllArmorFlatIgnoredAddReward("light_weapons_flat_armor_ignored_add", 0));
        register(new LightWeaponsAllArmorFlatIgnoredSetReward("light_weapons_flat_armor_ignored_set", 0));
        register(new LightWeaponsAllArmorFractionIgnoredAddReward("light_weapons_fraction_armor_ignored_add", 0));
        register(new LightWeaponsAllArmorFractionIgnoredSetReward("light_weapons_fraction_armor_ignored_set", 0));
        register(new LightWeaponsAttackSpeedBonusAddReward("light_weapons_attack_speed_bonus_add", 0));
        register(new LightWeaponsAttackSpeedBonusSetReward("light_weapons_attack_speed_bonus_set", 0));
        register(new LightWeaponsBleedChanceAddReward("light_weapons_bleed_chance_add", 0));
        register(new LightWeaponsBleedChanceSetReward("light_weapons_bleed_chance_set", 0));
        register(new LightWeaponsBleedDamageAddReward("light_weapons_bleed_damage_add", 0));
        register(new LightWeaponsBleedDamageSetReward("light_weapons_bleed_damage_set", 0));
        register(new LightWeaponsBleedDurationAddReward("light_weapons_bleed_duration_add", 0));
        register(new LightWeaponsBleedDurationSetReward("light_weapons_bleed_duration_set", 0));
        register(new LightWeaponsBleedOnCritReward("light_weapons_bleed_on_crit", 0));
        register(new LightWeaponsCritChanceAddReward("light_weapons_crit_chance_add", 0));
        register(new LightWeaponsCritChanceSetReward("light_weapons_crit_chance_set", 0));
        register(new LightWeaponsCritDamageMultiplierAddReward("light_weapons_crit_damage_multiplier_add", 0));
        register(new LightWeaponsCritDamageMultiplierSetReward("light_weapons_crit_damage_multiplier_set", 0));
        register(new LightWeaponsCritOnBleedReward("light_weapons_crit_on_bleed", 0));
        register(new LightWeaponsCritOnStealthReward("light_weapons_crit_on_stealth", 0));
        register(new LightWeaponsCritOnStunReward("light_weapons_crit_on_stun", 0));
        register(new LightWeaponsDamageMultiplierAddReward("light_weapons_damage_multiplier_add", 0));
        register(new LightWeaponsDamageMultiplierSetReward("light_weapons_damage_multiplier_set", 0));
        register(new LightWeaponsEXPMultiplierAddReward("light_weapons_expgain_add", 0));
        register(new LightWeaponsEXPMultiplierSetReward("light_weapons_expgain_set", 0));
        register(new LightWeaponsHeavyArmorDamageBonusAddReward("light_weapons_heavy_armor_damage_bonus_add", 0));
        register(new LightWeaponsHeavyArmorDamageBonusSetReward("light_weapons_heavy_armor_damage_bonus_set", 0));
        register(new LightWeaponsHeavyArmorFlatIgnoredAddReward("light_weapons_flat_heavy_armor_ignored_add", 0));
        register(new LightWeaponsHeavyArmorFlatIgnoredSetReward("light_weapons_flat_heavy_armor_ignored_set", 0));
        register(new LightWeaponsHeavyArmorFractionIgnoredAddReward("light_weapons_fraction_heavy_armor_ignored_add", 0));
        register(new LightWeaponsHeavyArmorFractionIgnoredSetReward("light_weapons_fraction_heavy_armor_ignored_set", 0));
        register(new LightWeaponsImmunityFramereductionAddReward("light_weapons_immunity_frame_reduction_add", 0));
        register(new LightWeaponsImmunityFramereductionSetReward("light_weapons_immunity_frame_reduction_set", 0));
        register(new LightWeaponsKnockbackBonusAddReward("light_weapons_knockback_bonus_add", 0));
        register(new LightWeaponsKnockbackBonusSetReward("light_weapons_knockback_bonus_set", 0));
        register(new LightWeaponsLightArmorDamageBonusAddReward("light_weapons_light_armor_damage_bonus_add", 0));
        register(new LightWeaponsLightArmorDamageBonusSetReward("light_weapons_light_armor_damage_bonus_set", 0));
        register(new LightWeaponsLightArmorFlatIgnoredAddReward("light_weapons_flat_light_armor_ignored_add", 0));
        register(new LightWeaponsLightArmorFlatIgnoredSetReward("light_weapons_flat_light_armor_ignored_set", 0));
        register(new LightWeaponsLightArmorFractionIgnoredAddReward("light_weapons_fraction_light_armor_ignored_add", 0));
        register(new LightWeaponsLightArmorFractionIgnoredSetReward("light_weapons_fraction_light_armor_ignored_set", 0));
        register(new LightWeaponsParryCooldownAddReward("light_weapons_parry_cooldown_add", 0));
        register(new LightWeaponsParryCooldownSetReward("light_weapons_parry_cooldown_set", 0));
        register(new LightWeaponsParryDamageReductionAddReward("light_weapons_parry_damage_reduction_add", 0));
        register(new LightWeaponsParryDamageReductionSetReward("light_weapons_parry_damage_reduction_set", 0));
        register(new LightWeaponsParryEnemyDebuffDurationAddReward("light_weapons_parry_enemy_debuff_duration_add", 0));
        register(new LightWeaponsParryEnemyDebuffDurationSetReward("light_weapons_parry_enemy_debuff_duration_set", 0));
        register(new LightWeaponsParryFailedDebuffDurationAddReward("light_weapons_parry_fail_debuff_duration_add", 0));
        register(new LightWeaponsParryFailedDebuffDurationSetReward("light_weapons_parry_fail_debuff_duration_set", 0));
        register(new LightWeaponsParryTimeFrameAddReward("light_weapons_parry_time_frame_add", 0));
        register(new LightWeaponsParryTimeFrameSetReward ("light_weapons_parry_time_frame_set", 0));
        register(new LightWeaponsParryVulnerableFrameAddReward("light_weapons_parry_vulnerable_frame_add", 0));
        register(new LightWeaponsParryVulnerableFrameSetReward("light_weapons_parry_vulnerable_frame_set", 0));
        register(new LightWeaponsUnlockedWeaponCoatingReward("light_weapons_unlocked_weapon_coating", 0));
        register(new LightWeaponsCoatingEnemyAmplifierMultiplierAddReward("light_weapons_coating_enemy_amplifier_add", 0));
        register(new LightWeaponsCoatingEnemyAmplifierMultiplierSetReward("light_weapons_coating_enemy_amplifier_set", 0));
        register(new LightWeaponsCoatingEnemyDurationMultiplierAddReward("light_weapons_coating_enemy_duration_add", 0));
        register(new LightWeaponsCoatingEnemyDurationMultiplierSetReward("light_weapons_coating_enemy_duration_set", 0));
        register(new LightWeaponsCoatingSelfDurationMultiplierAddReward("light_weapons_coating_self_duration_add", 0));
        register(new LightWeaponsCoatingSelfDurationMultiplierSetReward("light_weapons_coating_self_duration_set", 0));
        register(new LightWeaponsStunChanceAddReward("light_weapons_stun_chance_add", 0));
        register(new LightWeaponsStunChanceSetReward("light_weapons_stun_chance_set", 0));
        register(new LightWeaponsStunDurationAddReward("light_weapons_stun_duration_add", 0));
        register(new LightWeaponsStunDurationSetReward("light_weapons_stun_duration_set", 0));
        register(new LightWeaponsDropsBonusAddReward("light_weapons_drops_bonus_add", 0));
        register(new LightWeaponsDropsBonusSetReward("light_weapons_drops_bonus_set", 0));
        register(new LightWeaponsRareDropsMultiplierAddReward("light_weapons_rare_drop_multiplier_add", 0));
        register(new LightWeaponsRareDropsMultiplierSetReward("light_weapons_rare_drop_multiplier_set", 0));

        // HEAVY WEAPONS
        register(new HeavyWeaponsAllArmorFlatIgnoredAddReward("heavy_weapons_flat_armor_ignored_add", 0));
        register(new HeavyWeaponsAllArmorFlatIgnoredSetReward("heavy_weapons_flat_armor_ignored_set", 0));
        register(new HeavyWeaponsAllArmorFractionIgnoredAddReward("heavy_weapons_fraction_armor_ignored_add", 0));
        register(new HeavyWeaponsAllArmorFractionIgnoredSetReward("heavy_weapons_fraction_armor_ignored_set", 0));
        register(new HeavyWeaponsAttackSpeedBonusAddReward("heavy_weapons_attack_speed_bonus_add", 0));
        register(new HeavyWeaponsAttackSpeedBonusSetReward("heavy_weapons_attack_speed_bonus_set", 0));
        register(new HeavyWeaponsBleedChanceAddReward("heavy_weapons_bleed_chance_add", 0));
        register(new HeavyWeaponsBleedChanceSetReward("heavy_weapons_bleed_chance_set", 0));
        register(new HeavyWeaponsBleedDamageAddReward("heavy_weapons_bleed_damage_add", 0));
        register(new HeavyWeaponsBleedDamageSetReward("heavy_weapons_bleed_damage_set", 0));
        register(new HeavyWeaponsBleedDurationAddReward("heavy_weapons_bleed_duration_add", 0));
        register(new HeavyWeaponsBleedDurationSetReward("heavy_weapons_bleed_duration_set", 0));
        register(new HeavyWeaponsBleedOnCritReward("heavy_weapons_bleed_on_crit", 0));
        register(new HeavyWeaponsCritChanceAddReward("heavy_weapons_crit_chance_add", 0));
        register(new HeavyWeaponsCritChanceSetReward("heavy_weapons_crit_chance_set", 0));
        register(new HeavyWeaponsCritDamageMultiplierAddReward("heavy_weapons_crit_damage_multiplier_add", 0));
        register(new HeavyWeaponsCritDamageMultiplierSetReward("heavy_weapons_crit_damage_multiplier_set", 0));
        register(new HeavyWeaponsCritOnBleedReward("heavy_weapons_crit_on_bleed", 0));
        register(new HeavyWeaponsCritOnStealthReward("heavy_weapons_crit_on_stealth", 0));
        register(new HeavyWeaponsCritOnStunReward("heavy_weapons_crit_on_stun", 0));
        register(new HeavyWeaponsDamageMultiplierAddReward("heavy_weapons_damage_multiplier_add", 0));
        register(new HeavyWeaponsDamageMultiplierSetReward("heavy_weapons_damage_multiplier_set", 0));
        register(new HeavyWeaponsEXPMultiplierAddReward("heavy_weapons_expgain_add", 0));
        register(new HeavyWeaponsEXPMultiplierSetReward("heavy_weapons_expgain_set", 0));
        register(new HeavyWeaponsHeavyArmorDamageBonusAddReward("heavy_weapons_heavy_armor_damage_bonus_add", 0));
        register(new HeavyWeaponsHeavyArmorDamageBonusSetReward("heavy_weapons_heavy_armor_damage_bonus_set", 0));
        register(new HeavyWeaponsHeavyArmorFlatIgnoredAddReward("heavy_weapons_flat_heavy_armor_ignored_add", 0));
        register(new HeavyWeaponsHeavyArmorFlatIgnoredSetReward("heavy_weapons_flat_heavy_armor_ignored_set", 0));
        register(new HeavyWeaponsHeavyArmorFractionIgnoredAddReward("heavy_weapons_fraction_heavy_armor_ignored_add", 0));
        register(new HeavyWeaponsHeavyArmorFractionIgnoredSetReward("heavy_weapons_fraction_heavy_armor_ignored_set", 0));
        register(new HeavyWeaponsImmunityFramereductionAddReward("heavy_weapons_immunity_frame_reduction_add", 0));
        register(new HeavyWeaponsImmunityFramereductionSetReward("heavy_weapons_immunity_frame_reduction_set", 0));
        register(new HeavyWeaponsKnockbackBonusAddReward("heavy_weapons_knockback_bonus_add", 0));
        register(new HeavyWeaponsKnockbackBonusSetReward("heavy_weapons_knockback_bonus_set", 0));
        register(new HeavyWeaponsLightArmorDamageBonusAddReward("heavy_weapons_light_armor_damage_bonus_add", 0));
        register(new HeavyWeaponsLightArmorDamageBonusSetReward("heavy_weapons_light_armor_damage_bonus_set", 0));
        register(new HeavyWeaponsLightArmorFlatIgnoredAddReward("heavy_weapons_flat_light_armor_ignored_add", 0));
        register(new HeavyWeaponsLightArmorFlatIgnoredSetReward("heavy_weapons_flat_light_armor_ignored_set", 0));
        register(new HeavyWeaponsLightArmorFractionIgnoredAddReward("heavy_weapons_fraction_light_armor_ignored_add", 0));
        register(new HeavyWeaponsLightArmorFractionIgnoredSetReward("heavy_weapons_fraction_light_armor_ignored_set", 0));
        register(new HeavyWeaponsParryCooldownAddReward("heavy_weapons_parry_cooldown_add", 0));
        register(new HeavyWeaponsParryCooldownSetReward("heavy_weapons_parry_cooldown_set", 0));
        register(new HeavyWeaponsParryDamageReductionAddReward("heavy_weapons_parry_damage_reduction_add", 0));
        register(new HeavyWeaponsParryDamageReductionSetReward("heavy_weapons_parry_damage_reduction_set", 0));
        register(new HeavyWeaponsParryEnemyDebuffDurationAddReward("heavy_weapons_parry_enemy_debuff_duration_add", 0));
        register(new HeavyWeaponsParryEnemyDebuffDurationSetReward("heavy_weapons_parry_enemy_debuff_duration_set", 0));
        register(new HeavyWeaponsParryFailedDebuffDurationAddReward("heavy_weapons_parry_fail_debuff_duration_add", 0));
        register(new HeavyWeaponsParryFailedDebuffDurationSetReward("heavy_weapons_parry_fail_debuff_duration_set", 0));
        register(new HeavyWeaponsParryTimeFrameAddReward("heavy_weapons_parry_time_frame_add", 0));
        register(new HeavyWeaponsParryTimeFrameSetReward ("heavy_weapons_parry_time_frame_set", 0));
        register(new HeavyWeaponsParryVulnerableFrameAddReward("heavy_weapons_parry_vulnerable_frame_add", 0));
        register(new HeavyWeaponsParryVulnerableFrameSetReward("heavy_weapons_parry_vulnerable_frame_set", 0));
        register(new HeavyWeaponsUnlockedWeaponCoatingReward("heavy_weapons_unlocked_weapon_coating", 0));
        register(new HeavyWeaponsCoatingEnemyAmplifierMultiplierAddReward("heavy_weapons_coating_enemy_amplifier_add", 0));
        register(new HeavyWeaponsCoatingEnemyAmplifierMultiplierSetReward("heavy_weapons_coating_enemy_amplifier_set", 0));
        register(new HeavyWeaponsCoatingEnemyDurationMultiplierAddReward("heavy_weapons_coating_enemy_duration_add", 0));
        register(new HeavyWeaponsCoatingEnemyDurationMultiplierSetReward("heavy_weapons_coating_enemy_duration_set", 0));
        register(new HeavyWeaponsCoatingSelfDurationMultiplierAddReward("heavy_weapons_coating_self_duration_add", 0));
        register(new HeavyWeaponsCoatingSelfDurationMultiplierSetReward("heavy_weapons_coating_self_duration_set", 0));
        register(new HeavyWeaponsCrushingBlowChanceAddReward("heavy_weapons_crushing_blow_chance_add", 0));
        register(new HeavyWeaponsCrushingBlowChanceSetReward("heavy_weapons_crushing_blow_chance_set", 0));
        register(new HeavyWeaponsCrushingBlowCooldownAddReward("heavy_weapons_crushing_blow_cooldown_add", 0));
        register(new HeavyWeaponsCrushingBlowCooldownSetReward("heavy_weapons_crushing_blow_cooldown_set", 0));
        register(new HeavyWeaponsCrushingBlowOnCritReward("heavy_weapons_crushing_blow_on_crit", false));
        register(new HeavyWeaponsCrushingBlowOnFallingReward("heavy_weapons_crushing_blow_on_falling", false));
        register(new HeavyWeaponsCrushingBlowDamageMultiplierAddReward("heavy_weapons_crushing_blow_damage_multiplier_add", 0));
        register(new HeavyWeaponsCrushingBlowDamageMultiplierSetReward("heavy_weapons_crushing_blow_damage_multiplier_set", 0));
        register(new HeavyWeaponsCrushingBlowRadiusAddReward("heavy_weapons_crushing_blow_radius_add", 0));
        register(new HeavyWeaponsCrushingBlowRadiusSetReward("heavy_weapons_crushing_blow_radius_set", 0));
        register(new HeavyWeaponsStunChanceAddReward("heavy_weapons_stun_chance_add", 0));
        register(new HeavyWeaponsStunChanceSetReward("heavy_weapons_stun_chance_set", 0));
        register(new HeavyWeaponsStunDurationAddReward("heavy_weapons_stun_duration_add", 0));
        register(new HeavyWeaponsStunDurationSetReward("heavy_weapons_stun_duration_set", 0));
        register(new HeavyWeaponsDropsBonusAddReward("heavy_weapons_drops_bonus_add", 0));
        register(new HeavyWeaponsDropsBonusAddReward("heavy_weapons_drops_bonus_set", 0));
        register(new HeavyWeaponsRareDropsMultiplierAddReward("heavy_weapons_rare_drop_multiplier_add", 0));
        register(new HeavyWeaponsRareDropsMultiplierSetReward("heavy_weapons_rare_drop_multiplier_set", 0));
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
            ValhallaMMO.getPlugin().getLogger().severe("Could not clone PerkReward, notify plugin author");
            return null;
        }
    }

    public Map<String, PerkReward> getPerkRewards() {
        return perkRewards;
    }
}
