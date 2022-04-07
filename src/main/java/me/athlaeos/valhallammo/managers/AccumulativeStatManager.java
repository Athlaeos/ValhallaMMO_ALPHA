package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.items.EnchantmentType;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import me.athlaeos.valhallammo.statsources.alchemy.*;
import me.athlaeos.valhallammo.statsources.archery.*;
import me.athlaeos.valhallammo.statsources.enchanting.*;
import me.athlaeos.valhallammo.statsources.farming.*;
import me.athlaeos.valhallammo.statsources.general.*;
import me.athlaeos.valhallammo.statsources.landscaping.*;
import me.athlaeos.valhallammo.statsources.mining.*;
import me.athlaeos.valhallammo.statsources.smithing.SmithingPotionQualitySingleUseSource;
import me.athlaeos.valhallammo.statsources.smithing.SmithingPotionQualitySource;
import me.athlaeos.valhallammo.statsources.smithing.SmithingProfileEXPSource;
import me.athlaeos.valhallammo.statsources.smithing.SmithingProfileQualitySource;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Entity;

import java.util.*;

public class AccumulativeStatManager {
    private static AccumulativeStatManager manager = null;
    private final Map<String, Collection<AccumulativeStatSource>> sources;

    public AccumulativeStatManager(){
        sources = new HashMap<>();

        register("GLOBAL_EXP_GAIN", new ProfileExpGainSource(), new BuffExpGainSource(), new EnchantmentSkillExpGainSource(), new PermissionExpGainSource(null), new ArbitraryGlobalEffectSource("general_experience"));
        register("DAMAGE_DEALT", new EnchantmentDamageDealtSource());
        register("DAMAGE_TAKEN", new EnchantmentDamageTakenSource());

        register("SMITHING_QUALITY_GENERAL", new SmithingProfileQualitySource(null), new SmithingPotionQualitySingleUseSource(), new SmithingPotionQualitySource(), new ArbitraryEnchantmentAmplifierSource("SMITHING_QUALITY"), new ArbitraryGlobalEffectSource("smithing_quality"));
        register("SMITHING_QUALITY_WOOD", new SmithingProfileQualitySource(MaterialClass.WOOD));
        register("SMITHING_QUALITY_LEATHER", new SmithingProfileQualitySource(MaterialClass.LEATHER));
        register("SMITHING_QUALITY_STONE", new SmithingProfileQualitySource(MaterialClass.STONE));
        register("SMITHING_QUALITY_CHAINMAIL", new SmithingProfileQualitySource(MaterialClass.CHAINMAIL));
        register("SMITHING_QUALITY_GOLD", new SmithingProfileQualitySource(MaterialClass.GOLD));
        register("SMITHING_QUALITY_IRON", new SmithingProfileQualitySource(MaterialClass.IRON));
        register("SMITHING_QUALITY_DIAMOND", new SmithingProfileQualitySource(MaterialClass.DIAMOND));
        register("SMITHING_QUALITY_NETHERITE", new SmithingProfileQualitySource(MaterialClass.NETHERITE));
        register("SMITHING_QUALITY_BOW", new SmithingProfileQualitySource(MaterialClass.BOW));
        register("SMITHING_QUALITY_CROSSBOW", new SmithingProfileQualitySource(MaterialClass.CROSSBOW));
        register("SMITHING_QUALITY_PRISMARINE", new SmithingProfileQualitySource(MaterialClass.PRISMARINE));
        register("SMITHING_QUALITY_MEMBRANE", new SmithingProfileQualitySource(MaterialClass.MEMBRANE));
        register("ATTRIBUTE_DAMAGE_RESISTANCE", new EquipmentAttributeSource("CUSTOM_DAMAGE_RESISTANCE"));
        register("ATTRIBUTE_EXPLOSION_RESISTANCE", new EquipmentAttributeSource("CUSTOM_EXPLOSION_RESISTANCE"));
        register("ATTRIBUTE_FIRE_RESISTANCE", new EquipmentAttributeSource("CUSTOM_FIRE_RESISTANCE"));
        register("ATTRIBUTE_MAGIC_RESISTANCE", new EquipmentAttributeSource("CUSTOM_MAGIC_RESISTANCE"));
        register("ATTRIBUTE_POISON_RESISTANCE", new EquipmentAttributeSource("CUSTOM_POISON_RESISTANCE"));
        register("ATTRIBUTE_PROJECTILE_RESISTANCE", new EquipmentAttributeSource("CUSTOM_PROJECTILE_RESISTANCE"));
        register("SMITHING_EXP_GAIN_GENERAL", new SmithingProfileEXPSource(null), new PermissionExpGainSource("SMITHING"), new ArbitraryGlobalEffectSource("smithing_experience"));
        register("SMITHING_EXP_GAIN_WOOD", new SmithingProfileEXPSource(MaterialClass.WOOD));
        register("SMITHING_EXP_GAIN_LEATHER", new SmithingProfileEXPSource(MaterialClass.LEATHER));
        register("SMITHING_EXP_GAIN_STONE", new SmithingProfileEXPSource(MaterialClass.STONE));
        register("SMITHING_EXP_GAIN_CHAINMAIL", new SmithingProfileEXPSource(MaterialClass.CHAINMAIL));
        register("SMITHING_EXP_GAIN_GOLD", new SmithingProfileEXPSource(MaterialClass.GOLD));
        register("SMITHING_EXP_GAIN_IRON", new SmithingProfileEXPSource(MaterialClass.IRON));
        register("SMITHING_EXP_GAIN_DIAMOND", new SmithingProfileEXPSource(MaterialClass.DIAMOND));
        register("SMITHING_EXP_GAIN_NETHERITE", new SmithingProfileEXPSource(MaterialClass.NETHERITE));
        register("SMITHING_EXP_GAIN_BOW", new SmithingProfileEXPSource(MaterialClass.BOW));
        register("SMITHING_EXP_GAIN_CROSSBOW", new SmithingProfileEXPSource(MaterialClass.CROSSBOW));
        register("SMITHING_EXP_GAIN_PRISMARINE", new SmithingProfileEXPSource(MaterialClass.PRISMARINE));
        register("SMITHING_EXP_GAIN_MEMBRANE", new SmithingProfileEXPSource(MaterialClass.MEMBRANE));

        register("ALCHEMY_QUALITY_GENERAL", new AlchemyQualityPlayerSource(null), new ArbitraryEnchantmentAmplifierSource("ALCHEMY_QUALITY"), new AlchemyPotionQualitySingleUseSource(), new ArbitraryGlobalEffectSource("alchemy_quality"));
        register("ALCHEMY_QUALITY_DEBUFF", new AlchemyQualityPlayerSource(PotionType.DEBUFF));
        register("ALCHEMY_QUALITY_BUFF", new AlchemyQualityPlayerSource(PotionType.BUFF));
        register("ALCHEMY_BREW_SPEED", new AlchemyProfileBrewSpeedSource(), new AlchemyPotionBrewSpeedSource(), new ArbitraryEnchantmentAmplifierSource("ALCHEMY_BREW_SPEED"), new ArbitraryGlobalEffectSource("alchemy_brewing_speed"));
        register("ALCHEMY_INGREDIENT_SAVE", new AlchemyProfileIngredientSaveSource(), new AlchemyPotionIngredientSaveSource(), new ArbitraryEnchantmentAmplifierSource("ALCHEMY_INGREDIENT_SAVE"), new ArbitraryGlobalEffectSource("alchemy_ingredient_save"));
        register("ALCHEMY_POTION_SAVE", new AlchemyProfilePotionSaveSource(), new AlchemyPotionPotionSaveSource(), new ArbitraryEnchantmentAmplifierSource("ALCHEMY_POTION_SAVE"), new ArbitraryGlobalEffectSource("alchemy_potion_save"));
        register("ALCHEMY_POTION_VELOCITY", new AlchemyProfileThrowVelocitySource(), new AlchemyPotionThrowVelocitySource(), new ArbitraryEnchantmentAmplifierSource("ALCHEMY_THROW_VELOCITY"), new ArbitraryGlobalEffectSource("alchemy_potion_velocity"));
        register("ALCHEMY_EXP_GAIN", new AlchemyProfileEXPSource(), new PermissionExpGainSource("ALCHEMY"), new ArbitraryGlobalEffectSource("alchemy_experience"));

        register("ENCHANTING_QUALITY_GENERAL", new EnchantingProfileQualitySource(null), new EnchantingPotionQualitySource(), new EnchantingPotionQualitySingleUseSource(), new ArbitraryGlobalEffectSource("enchanting_quality"));
        register("ENCHANTING_QUALITY_VANILLA", new EnchantingProfileQualitySource(EnchantmentType.VANILLA));
        register("ENCHANTING_QUALITY_CUSTOM", new EnchantingProfileQualitySource(EnchantmentType.CUSTOM));
        register("ENCHANTING_EXP_GAIN_GENERAL", new EnchantingProfileEXPSource(null), new PermissionExpGainSource("ENCHANTING"), new ArbitraryGlobalEffectSource("enchanting_experience"));
        register("ENCHANTING_EXP_GAIN_VANILLA", new EnchantingProfileEXPSource(EnchantmentType.VANILLA));
        register("ENCHANTING_EXP_GAIN_CUSTOM", new EnchantingProfileEXPSource(EnchantmentType.CUSTOM));
        register("ENCHANTING_VANILLA_EXP_GAIN", new EnchantingProfileVanillaEXPGainSource(), new EnchantingPotionVanillaEXPGainSource(), new EnchantmentVanillaExpGainSource());
        register("ENCHANTING_AMPLIFY_CHANCE", new EnchantingProfileAmplifyChanceSource(), new ArbitraryGlobalEffectSource("enchanting_amplify_chance"));
        register("ENCHANTING_MAX_CUSTOM_ALLOWED", new EnchantingProfileMaxCustomAllowedSource(), new ArbitraryGlobalEffectSource("enchanting_max_custom_allowed"));
        register("ENCHANTING_LAPIS_SAVE_CHANCE", new EnchantingProfileLapisSaveChanceSource(), new ArbitraryGlobalEffectSource("enchanting_lapis_save_chance"));
        register("ENCHANTING_REFUND_CHANCE", new EnchantingProfileRefundChanceSource(), new ArbitraryGlobalEffectSource("enchanting_exp_refund_chance"));
        register("ENCHANTING_REFUND_AMOUNT", new EnchantingProfileRefundFractionSource(), new ArbitraryGlobalEffectSource("enchanting_exp_refund_amount"));

        register("FARMING_BREEDING_AGE_REDUCTION", new FarmingProfileBabyAnimalAgeMultiplierSource(), new ArbitraryGlobalEffectSource("farming_animal_age_reduction"));
        register("FARMING_DAMAGE_ANIMAL_MULTIPLIER", new FarmingProfileAnimalDamageMultiplierSource());
        register("FARMING_ANIMAL_DROP_MULTIPLIER", new FarmingProfileAnimalDropMultiplierSource(), new ArbitraryGlobalEffectSource("farming_animal_drop_multiplier"));
        register("FARMING_ANIMAL_RARE_DROP_MULTIPLIER", new FarmingProfileAnimalRareDropChanceMultiplierSource(), new ArbitraryGlobalEffectSource("farming_animal_rare_drop_multiplier"));
        register("FARMING_BREEDING_VANILLA_EXP_MULTIPLIER", new FarmingProfileBreedingVanillaEXPMultiplierSource());
        register("FARMING_DROP_MULTIPLIER", new FarmingProfileDropMultiplierSource(), new ArbitraryEnchantmentAmplifierSource("FARMING_EXTRA_DROPS"), new FarmingPotionExtraDropsSource(), new ArbitraryGlobalEffectSource("farming_drop_multiplier"));
        register("FARMING_VANILLA_EXP_REWARD", new FarmingProfileFarmingVanillaEXPRewardSource(), new ArbitraryGlobalEffectSource("farming_vanilla_exp"));
        register("FARMING_FISHING_REWARD_TIER", new FarmingProfileFishingRewardTierSource(), new FarmingLOTSFishingRewardTierSource(), new FarmingLuckFishingRewardTierSource(), new ArbitraryEnchantmentAmplifierSource("FARMING_FISHING_TIER"), new ArbitraryGlobalEffectSource("farming_fishing_tier"));
        register("FARMING_FISHING_TIME_MULTIPLIER", new FarmingProfileFishingTimeMultiplierSource(), new ArbitraryGlobalEffectSource("farming_fishing_speed_multiplier"));
        register("FARMING_FISHING_VANILLA_EXP_MULTIPLIER", new FarmingProfileFishingVanillaEXPMultiplierSource(), new ArbitraryGlobalEffectSource("farming_fishing_vanilla_exp_multiplier"));
        register("FARMING_HONEY_SAVE_CHANCE", new FarmingProfileHiveHoneySaveChanceSource(), new ArbitraryGlobalEffectSource("farming_honey_save_chance"));
        register("FARMING_INSTANT_GROWTH_RATE", new FarmingProfileInstantGrowthRateSource(), new ArbitraryGlobalEffectSource("farming_instant_growth_rate"));
        register("FARMING_RARE_DROP_CHANCE_MULTIPLIER", new FarmingProfileRareDropChanceMultiplierSource(), new FarmingPotionRareDropsSource(), new ArbitraryEnchantmentAmplifierSource("FARMING_RARE_DROPS"), new ArbitraryGlobalEffectSource("farming_rare_drop_multiplier"));
        register("FARMING_HUNGER_MULTIPLIER_FISH", new FarmingProfileFoodMultiplierSource(FarmingProfileFoodMultiplierSource.FoodType.FISH));
        register("FARMING_HUNGER_MULTIPLIER_MEAT", new FarmingProfileFoodMultiplierSource(FarmingProfileFoodMultiplierSource.FoodType.MEAT));
        register("FARMING_HUNGER_MULTIPLIER_VEGETARIAN", new FarmingProfileFoodMultiplierSource(FarmingProfileFoodMultiplierSource.FoodType.VEG));
        register("FARMING_HUNGER_MULTIPLIER_GARBAGE", new FarmingProfileFoodMultiplierSource(FarmingProfileFoodMultiplierSource.FoodType.GARBAGE));
        register("FARMING_HUNGER_MULTIPLIER_MAGICAL", new FarmingProfileFoodMultiplierSource(FarmingProfileFoodMultiplierSource.FoodType.MAGICAL));
        register("FARMING_EXP_GAIN_GENERAL", new FarmingProfileGeneralEXPSource(), new PermissionExpGainSource("FARMING"), new ArbitraryGlobalEffectSource("farming_experience"));
        register("FARMING_EXP_GAIN_BREEDING", new FarmingProfileBreedingEXPSource());
        register("FARMING_EXP_GAIN_FARMING", new FarmingProfileFarmingEXPSource());
        register("FARMING_EXP_GAIN_FISHING", new FarmingProfileFishingEXPSource());

        register("MINING_BLAST_DROP_MULTIPLIER", new MiningProfileBlastDropMultiplierSource(), new ArbitraryGlobalEffectSource("blast_mining_drop_multiplier"));
        register("MINING_BLAST_EXPLOSION_DAMAGE_MULTIPLIER", new MiningProfileBlastExplosionDamageMultiplierSource(), new ArbitraryGlobalEffectSource("blast_mining_damage_taken_multiplier"));
        register("MINING_BLAST_RADIUS_MULTIPLIER", new MiningProfileBlastRadiusMultiplierSource(), new ArbitraryGlobalEffectSource("blast_mining_radius_multiplier"));
        register("MINING_BLAST_RARE_DROP_CHANCE_MULTIPLIER", new MiningProfileBlastRareDropChanceMultiplierSource(), new ArbitraryGlobalEffectSource("blast_mining_rare_drop_multiplier"));
        register("MINING_VANILLA_EXP_REWARD", new MiningProfileBlockExperienceRateSource(), new ArbitraryGlobalEffectSource("mining_vanilla_exp_reward"));
        register("MINING_MINING_DROP_MULTIPLIER", new MiningProfileMiningDropMultiplierSource(), new ArbitraryGlobalEffectSource("mining_drop_multiplier"));
        register("MINING_QUICK_MINE_DRAIN_RATE", new MiningProfileQuickMineDrainRateSource(), new ArbitraryGlobalEffectSource("mining_quick_mine_drain_rate"));
        register("MINING_ORE_EXPERIENCE_MULTIPLIER", new MiningProfileMiningOreExperienceMultiplierSource(), new ArbitraryGlobalEffectSource("mining_ore_experience_multiplier"));
        register("MINING_MINING_RARE_DROP_CHANCE_MULTIPLIER", new MiningProfileMiningRareDropChanceMultiplierSource(), new ArbitraryGlobalEffectSource("mining_rare_drop_multiplier"));
        register("MINING_EXP_GAIN_GENERAL", new MiningProfileGeneralEXPSource(), new ArbitraryGlobalEffectSource("mining_experience"));
        register("MINING_EXP_GAIN_MINING", new MiningProfileMiningEXPSource());
        register("MINING_EXP_GAIN_BLAST", new MiningProfileBlastEXPSource());

        register("LANDSCAPING_DIGGING_DROP_MULTIPLIER", new LandscapingProfileDiggingDropMultiplierSource(), new ArbitraryGlobalEffectSource("landscaping_digging_drop_multiplier"));
        register("LANDSCAPING_DIGGING_RARE_DROP_MULTIPLIER", new LandscapingProfileDiggingRareDropChanceMultiplierSource(), new ArbitraryGlobalEffectSource("landscaping_digging_rare_drop_multiplier"));
        register("LANDSCAPING_WOODCUTTING_DROP_MULTIPLIER", new LandscapingProfileWoodcuttingDropMultiplierSource(), new ArbitraryGlobalEffectSource("landscaping_woodcutting_drop_multiplier"));
        register("LANDSCAPING_WOODCUTTING_RARE_DROP_MULTIPLIER", new LandscapingProfileWoodcuttingRareDropChanceMultiplierSource(), new ArbitraryGlobalEffectSource("landscaping_woodcutting_rare_drop_multiplier"));
        register("LANDSCAPING_WOODSTRIPPING_RARE_DROP_MULTIPLIER", new LandscapingProfileWoodstrippingRareDropChanceMultiplierSource(), new ArbitraryGlobalEffectSource("landscaping_woodstripping_rare_drop_multiplier"));
        register("LANDSCAPING_INSTANT_GROWTH_RATE", new LandscapingProfileInstantGrowthRateSource(), new ArbitraryGlobalEffectSource("landscaping_instant_growth_rate"));
        register("LANDSCAPING_PLACEMENT_REACH_BONUS", new LandscapingProfilePlaceReachBonusSource(), new ArbitraryGlobalEffectSource("landscaping_placement_reach"));
        register("LANDSCAPING_EXP_GAIN_GENERAL", new LandscapingProfileGeneralEXPSource(), new ArbitraryGlobalEffectSource("landscaping_experience"));
        register("LANDSCAPING_WOODCUTTING_VANILLA_EXP_REWARD", new LandscapingProfileWoodcuttingExperienceRateSource(), new ArbitraryGlobalEffectSource("landscaping_woodcutting_vanilla_exp_reward"));
        register("LANDSCAPING_DIGGING_VANILLA_EXP_REWARD", new LandscapingProfileDiggingExperienceRateSource(), new ArbitraryGlobalEffectSource("landscaping_digging_vanilla_exp_reward"));
        register("LANDSCAPING_EXP_GAIN_WOODCUTTING", new LandscapingProfileWoodcuttingEXPSource());
        register("LANDSCAPING_EXP_GAIN_WOODSTRIPPING", new LandscapingProfileWoodstrippingEXPSource());
        register("LANDSCAPING_EXP_GAIN_DIGGING", new LandscapingProfileDiggingEXPSource());

        register("ARCHERY_DAMAGE", new ArbitraryEnchantmentAmplifierSource("ARCHERY_DAMAGE"));
        register("ARCHERY_BOW_DAMAGE_MULTIPLIER", new ArcheryProfileBowDamageMultiplierSource());
        register("ARCHERY_CROSSBOW_DAMAGE_MULTIPLIER", new ArcheryProfileCrossBowDamageMultiplierSource());
        register("ARCHERY_AMMO_SAVE_CHANCE", new ArcheryProfileAmmoSaveChanceSource());
        register("ARCHERY_BOW_CRIT_CHANCE", new ArcheryProfileBowCritChanceSource());
        register("ARCHERY_CROSSBOW_CRIT_CHANCE", new ArcheryProfileCrossBowCritChanceSource());
        register("ARCHERY_CRIT_DAMAGE_MULTIPLIER", new ArcheryProfileCritDamageMultiplierSource());
        register("ARCHERY_CHARGED_SHOT_COOLDOWN", new ArcheryProfileChargeShotCooldownSource());
        register("ARCHERY_CHARGED_SHOT_KNOCKBACK_BONUS", new ArcheryProfileChargeShotKnockbackBonusSource());
        register("ARCHERY_CHARGED_SHOT_DAMAGE_MULTIPLIER", new ArcheryProfileChargeShotDamageMultiplierSource());
        register("ARCHERY_STUN_CHANCE", new ArcheryProfileStunChanceSource());
        register("ARCHERY_STUN_DURATION", new ArcheryProfileStunDurationSource());
        register("ARCHERY_INFINITY_DAMAGE_MULTIPLIER", new ArcheryProfileInfinityDamageMultiplierSource());
        register("ARCHERY_INACCURACY", new ArcheryProfileInaccuracySource());
        register("ARCHERY_DISTANCE_DAMAGE_MULTIPLIER_BASE", new ArcheryProfileDistanceDamageBaseSource());
        register("ARCHERY_DISTANCE_DAMAGE_MULTIPLIER", new ArcheryProfileDistanceDamageMultiplierSource());


        register("ARCHERY_EXP_GAIN_BOW", new ArcheryProfileBowEXPSource());
        register("ARCHERY_EXP_GAIN_CROSSBOW", new ArcheryProfileCrossBowEXPSource());
        register("ARCHERY_EXP_GAIN_GENERAL", new ArcheryProfileGeneralEXPSource());
    }

    /**
     * Registers a stat name to be used and collected stats from
     * @param stat the stat name to register
     */
    public void register(String stat){
        sources.put(stat, new HashSet<>());
    }

    /**
     * Registers a new stat source to a stat name. If the stat does not yet exist, it is registered also.
     * This source will be used to conditionally collect a specific stat from given a player.
     * @param stat the stat to register a new source to
     * @param sources the source to register
     */
    public void register(String stat, AccumulativeStatSource... sources){
        Collection<AccumulativeStatSource> existingSources = this.sources.get(stat);
        if (existingSources == null) existingSources = new HashSet<>();
        existingSources.addAll(Arrays.asList(sources));
        this.sources.put(stat, existingSources);
    }

    /**
     * Collects all the stats of the given stat name. For example, if 'SMITHING_SKILL_ALL' is given, it will return the
     * given player's total skill level in general smithing, which would be the accumulation of actual skill, potion
     * effects, global boosters, etc.
     * @param stat the stat to gather its total from
     * @param p the player to gether their stats from
     * @param use if true, it will be assumed the stat is actually being used in practice rather than being a visual
     *            for show. Example: if a player were to craft an item and had some potion effect to boost their
     *            crafting quality, use would be true. If the player were to only look at the item in the crafting menu,
     *            so not actually having crafted it just yet, use would be false.
     * @return the collective stat number
     * @throws IllegalArgumentException if the given stat was not registered
     */
    public double getStats(String stat, Entity p, boolean use) throws IllegalArgumentException {
        if (!sources.containsKey(stat)) return 0;
        Collection<AccumulativeStatSource> existingSources = sources.get(stat);
        double value = 0;
        for (AccumulativeStatSource s : existingSources){
            value += s.add(p, use);
        }
        return Utils.round(value, 6);
    }

    public static AccumulativeStatManager getInstance(){
        if (manager == null) manager = new AccumulativeStatManager();
        return manager;
    }
}
