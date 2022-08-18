package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.items.EnchantmentType;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import me.athlaeos.valhallammo.statsources.EvEAccumulativeStatSource;
import me.athlaeos.valhallammo.statsources.alchemy.*;
import me.athlaeos.valhallammo.statsources.archery.*;
import me.athlaeos.valhallammo.statsources.enchanting.*;
import me.athlaeos.valhallammo.statsources.farming.*;
import me.athlaeos.valhallammo.statsources.general.*;
import me.athlaeos.valhallammo.statsources.heavy_armor.*;
import me.athlaeos.valhallammo.statsources.heavy_weapons.*;
import me.athlaeos.valhallammo.statsources.landscaping.*;
import me.athlaeos.valhallammo.statsources.light_armor.*;
import me.athlaeos.valhallammo.statsources.light_weapons.*;
import me.athlaeos.valhallammo.statsources.mining.*;
import me.athlaeos.valhallammo.statsources.smithing.*;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Entity;

import java.util.*;

public class AccumulativeStatManager {
    private static AccumulativeStatManager manager = null;
    private final Map<String, Collection<AccumulativeStatSource>> sources;

    public Map<String, Collection<AccumulativeStatSource>> getSources() {
        return sources;
    }

    public AccumulativeStatManager(){
        sources = new HashMap<>();

        register("GLOBAL_EXP_GAIN", new ProfileExpGainSource(), new BuffExpGainSource(), new EnchantmentSkillExpGainSource(), new PermissionExpGainSource(null), new ArbitraryGlobalEffectSource("general_experience"));
        register("DAMAGE_DEALT", new EnchantmentDamageDealtSource());
        register("MELEE_DAMAGE_DEALT", new OverleveledEquipmentMeleeDamagePenaltySource(), new ArbitraryAttributeOnAttackSource("CUSTOM_MELEE_DAMAGE", false));

        register("DAMAGE_RESISTANCE", new ArbitraryPotionAmplifierSource("CUSTOM_DAMAGE_RESISTANCE", false), new ProfileDamageResistanceSource(), new HeavyArmorProfileEquipmentDamageResistanceSource(), new HeavyArmorDefaultEquipmentDamageResistanceSource(), new BuffResistanceDamageResistanceSource(), new ArbitraryEnchantmentAmplifierSource("DAMAGE_TAKEN", true), new EquipmentAttributeSource("CUSTOM_DAMAGE_RESISTANCE"), new LightArmorProfileEquipmentDamageResistanceSource(), new LightArmorDefaultEquipmentDamageResistanceSource(), new EquipmentProtectionEnchantmentDamageResistanceSource(), new ArbitraryPotionAmplifierSource("POISON_VULNERABLE", true));
        register("EXPLOSION_RESISTANCE", new ArbitraryPotionAmplifierSource("EXPLOSION_RESISTANCE", false), new ProfileExplosionResistanceSource(), new HeavyArmorProfileEquipmentExplosionResistanceSource(), new HeavyArmorDefaultEquipmentExplosionResistanceSource(), new EquipmentAttributeSource("CUSTOM_EXPLOSION_RESISTANCE"), new LightArmorProfileEquipmentExplosionResistanceSource(), new LightArmorDefaultEquipmentExplosionResistanceSource(), new EquipmentBlastProtectionEnchantmentExplosionResistanceSource());
        register("FIRE_RESISTANCE", new ArbitraryPotionAmplifierSource("CUSTOM_FIRE_RESISTANCE", false), new ProfileFireResistanceSource(), new HeavyArmorProfileEquipmentFireResistanceSource(), new HeavyArmorDefaultEquipmentFireResistanceSource(), new EquipmentAttributeSource("CUSTOM_FIRE_RESISTANCE"), new LightArmorProfileEquipmentFireResistanceSource(), new LightArmorDefaultEquipmentFireResistanceSource(), new EquipmentFireProtectionEnchantmentFireResistanceSource());
        register("MAGIC_RESISTANCE", new ArbitraryPotionAmplifierSource("MAGIC_RESISTANCE", false), new ProfileMagicResistanceSource(), new HeavyArmorProfileEquipmentMagicResistanceSource(), new HeavyArmorDefaultEquipmentMagicResistanceSource(), new EquipmentAttributeSource("CUSTOM_MAGIC_RESISTANCE"), new LightArmorProfileEquipmentMagicResistanceSource(), new LightArmorDefaultEquipmentMagicResistanceSource(), new EquipmentProtectionEnchantmentMagicDamageResistanceSource());
        register("POISON_RESISTANCE", new ArbitraryPotionAmplifierSource("POISON_RESISTANCE", false), new ProfilePoisonResistanceSource(), new HeavyArmorProfileEquipmentPoisonResistanceSource(), new HeavyArmorDefaultEquipmentPoisonResistanceSource(), new EquipmentAttributeSource("CUSTOM_POISON_RESISTANCE"), new LightArmorProfileEquipmentPoisonResistanceSource(), new LightArmorDefaultEquipmentPoisonResistanceSource(), new EquipmentProtectionEnchantmentPoisonDamageResistanceSource());
        register("PROJECTILE_RESISTANCE", new ArbitraryPotionAmplifierSource("PROJECTILE_RESISTANCE", false), new ProfileProjectileResistanceSource(), new HeavyArmorProfileEquipmentProjectileResistanceSource(), new HeavyArmorDefaultEquipmentProjectileResistanceSource(), new EquipmentAttributeSource("CUSTOM_PROJECTILE_RESISTANCE"), new LightArmorProfileEquipmentProjectileResistanceSource(), new LightArmorDefaultEquipmentProjectileResistanceSource(), new EquipmentProjectileProtectionEnchantmentProjectileResistanceSource());
        register("MELEE_RESISTANCE", new ArbitraryPotionAmplifierSource("MELEE_RESISTANCE", false), new ProfileMeleeResistanceSource(), new HeavyArmorProfileEquipmentMeleeResistanceSource(), new HeavyArmorDefaultEquipmentMeleeResistanceSource(), new EquipmentAttributeSource("CUSTOM_MELEE_RESISTANCE"), new LightArmorProfileEquipmentMeleeResistanceSource(), new LightArmorDefaultEquipmentMeleeResistanceSource());
        register("FALLING_RESISTANCE", new ArbitraryPotionAmplifierSource("FALLING_RESISTANCE", false), new ProfileFallDamageResistanceSource(), new HeavyArmorProfileEquipmentFallDamageResistanceSource(), new HeavyArmorDefaultEquipmentFallDamageResistanceSource(), new EquipmentAttributeSource("CUSTOM_FALLING_RESISTANCE"), new LightArmorProfileEquipmentFallDamageResistanceSource(), new LightArmorDefaultEquipmentFallDamageResistanceSource(), new EquipmentFeatherFallingEnchantmentFallDamageResistanceSource());
        register("KNOCKBACK_RESISTANCE", new ArbitraryPotionAmplifierSource("KNOCKBACK_RESISTANCE", false), new ProfileKnockbackResistanceSource(), new HeavyArmorProfileEquipmentKnockbackResistanceSource(), new HeavyArmorDefaultEquipmentKnockbackResistanceSource(), new LightArmorDefaultEquipmentKnockbackResistanceSource(), new LightArmorProfileEquipmentKnockbackResistanceSource());
        register("BLEED_RESISTANCE", new ArbitraryEnchantmentAmplifierSource("BLEED_RESISTANCE"), new ArbitraryPotionAmplifierSource("BLEED_RESISTANCE", false), new ProfileBleedResistanceSource(), new HeavyArmorProfileEquipmentBleedResistanceSource(), new HeavyArmorProfileFullArmorBleedResistanceSource(), new HeavyArmorDefaultEquipmentBleedResistanceSource(), new LightArmorProfileFullArmorBleedResistanceSource(), new LightArmorDefaultEquipmentBleedResistanceSource(), new LightArmorDefaultEquipmentBleedResistanceSource());
        register("STUN_RESISTANCE", new ArbitraryEnchantmentAmplifierSource("STUN_RESISTANCE"), new ProfileStunResistanceSource());

        register("CRAFTING_TIME_REDUCTION", new ArbitraryEnchantmentAmplifierSource("CRAFTING_SPEED"), new ArbitraryPotionAmplifierSource("CRAFTING_TIME_REDUCTION", false), new SmithingCraftingTimeReductionSource());

        //register("ARMOR", new ProfileBaseArmorBonusSource(), new EquipmentAttributeSource("GENERIC_ARMOR"), new EntityAttributeArmorRatingSource(), new ArbitraryPotionAmplifierSource("ARMOR_FLAT_BONUS", false));
        register("ARMOR_FLAT_IGNORED", new HeavyWeaponsProfileFlatArmorIgnoredSource(), new LightWeaponsProfileFlatArmorIgnoredSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_FLAT_ARMOR_PENETRATION", false), new ArbitraryPotionAmplifierSource("FLAT_ARMOR_REDUCTION", false));
        register("ARMOR_FRACTION_IGNORED", new HeavyWeaponsProfileFractionArmorIgnoredSource(), new LightWeaponsProfileFractionArmorIgnoredSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_FRACTION_ARMOR_PENETRATION", false), new ArbitraryPotionAmplifierSource("FRACTION_ARMOR_REDUCTION", false));
        register("TOUGHNESS", new EquipmentAttributeSource("GENERIC_ARMOR_TOUGHNESS"), new EntityAttributeArmorToughnessSource());
        register("LIGHT_ARMOR", new LightArmorEquipmentAttributeSource("GENERIC_ARMOR"), new ArbitraryPotionAmplifierSource("LIGHT_ARMOR_FLAT_BONUS", false));
        register("LIGHT_ARMOR_FLAT_IGNORED", new HeavyWeaponsProfileFlatLightArmorIgnoredSource(), new LightWeaponsProfileFlatLightArmorIgnoredSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_FLAT_LIGHT_ARMOR_PENETRATION", false));
        register("LIGHT_ARMOR_FRACTION_IGNORED", new HeavyWeaponsProfileFractionLightArmorIgnoredSource(), new LightWeaponsProfileFractionLightArmorIgnoredSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_FRACTION_LIGHT_ARMOR_PENETRATION", false));
        register("HEAVY_ARMOR", new HeavyArmorEquipmentAttributeSource("GENERIC_ARMOR"), new ArbitraryPotionAmplifierSource("HEAVY_ARMOR_FLAT_BONUS", false));
        register("HEAVY_ARMOR_FLAT_IGNORED", new HeavyWeaponsProfileFlatHeavyArmorIgnoredSource(), new LightWeaponsProfileFlatHeavyArmorIgnoredSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_FLAT_HEAVY_ARMOR_PENETRATION", false));
        register("HEAVY_ARMOR_FRACTION_IGNORED", new HeavyWeaponsProfileFractionHeavyArmorIgnoredSource(), new LightWeaponsProfileFractionHeavyArmorIgnoredSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_FRACTION_HEAVY_ARMOR_PENETRATION", false));
        // returns all armor that came from non-equipment sources, like attributes/potion effects
        register("NON_EQUIPMENT_ARMOR", new TrinketAttributeSource("GENERIC_ARMOR"), new WeightlessArmorEquipmentAttributeSource(), new EntityAttributeArmorRatingSource(), new ProfileBaseArmorBonusSource(), new ArbitraryPotionAmplifierSource("ARMOR_FLAT_BONUS", false));

        //register("ARMOR_BONUS", new ProfileBaseArmorBonusSource(), new ArbitraryPotionAmplifierSource("ARMOR_FLAT_BONUS", false));
        register("ARMOR_MULTIPLIER_BONUS", new ArbitraryEnchantmentAmplifierSource("ARMOR_MULTIPLIER"), new ProfileBaseArmorMultiplierBonusSource(), new ArbitraryPotionAmplifierSource("ARMOR_FRACTION_BONUS", false));
        register("HEALTH_MULTIPLIER_BONUS", new ArbitraryEnchantmentAmplifierSource("HEALTH_MULTIPLIER"));
        register("TOUGHNESS_BONUS", new TrinketAttributeSource("GENERIC_ARMOR_TOUGHNESS"), new ProfileBaseToughnessBonusSource());
        register("HUNGER_SAVE_CHANCE", new ArbitraryEnchantmentAmplifierSource("HUNGER_SAVE_CHANCE"), new ArbitraryPotionAmplifierSource("HUNGER_SAVE_CHANCE", false), new ProfileHungerSaveChanceBonusSource(), new LightArmorProfileFullArmorHungerSaveChanceBonusSource(), new HeavyArmorProfileFullArmorHungerSaveChanceSource());
        register("ATTACK_REACH_BONUS", new EquipmentAttributeSource("CUSTOM_WEAPON_REACH", true));
        register("ATTACK_DAMAGE_BONUS", new TrinketAttributeSource("GENERIC_ATTACK_DAMAGE"), new ProfileBaseAttackDamageBonusSource());
        register("ATTACK_SPEED_BONUS", new TrinketAttributeSource("GENERIC_ATTACK_SPEED"), new ProfileBaseAttackSpeedBonusSource(), new HeavyWeaponsProfileAttackSpeedBonusSource(), new LightWeaponsProfileAttackSpeedBonusSource());
        register("DODGE_CHANCE", new ArbitraryEnchantmentAmplifierSource("DODGE_CHANCE"), new ArbitraryPotionAmplifierSource("DODGE_CHANCE", false), new LightArmorProfileFullArmorDodgeChanceBonusEvESource());
        register("MOVEMENT_SPEED_BONUS", new TrinketAttributeSource("GENERIC_MOVEMENT_SPEED"), new ProfileMovementSpeedBonusSource(), new LightArmorProfileEquipmentMovementSpeedPenaltySource(), new HeavyArmorProfileEquipmentMovementSpeedPenaltySource());
        register("HEALTH_BONUS", new TrinketAttributeSource("GENERIC_MAX_HEALTH"), new ProfileHealthBonusSource());
        register("KNOCKBACK_BONUS", new ArbitraryAttributeOnAttackSource("CUSTOM_KNOCKBACK", false), new ArbitraryPotionAmplifierSource("KNOCKBACK_BONUS", false), new HeavyWeaponsProfileAttackKnockbackBonusSource(), new LightWeaponsProfileAttackKnockbackBonusSource());
        register("COOLDOWN_REDUCTION", new ArbitraryEnchantmentAmplifierSource("COOLDOWN_REDUCTION"), new ArbitraryPotionAmplifierSource("COOLDOWN_REDUCTION", false), new ProfileCooldownReductionSource());
        register("LUCK_BONUS", new TrinketAttributeSource("GENERIC_LUCK"), new ProfileBaseLuckBonusSource());
        register("IMMUNITY_FRAME_BONUS", new ArbitraryPotionAmplifierSource("IMMUNITY_FRAME_BONUS", false), new ProfileImmunityFramesBonusSource(), new EquipmentAttributeSource("CUSTOM_FLAT_IMMUNITY_FRAME_BONUS"));
        register("IMMUNITY_FRAME_MULTIPLIER", new ArbitraryPotionAmplifierSource("IMMUNITY_FRAME_MULTIPLIER", false), new LightWeaponsProfileImmunityFrameReductionSource(), new EquipmentAttributeSource("CUSTOM_FRACTION_IMMUNITY_FRAME_BONUS"), new ArbitraryAttributeOnAttackSource("CUSTOM_IMMUNITY_FRAME_REDUCTION", true));
        register("HEALING_BONUS", new ArbitraryEnchantmentAmplifierSource("HEALING_BONUS"), new ArbitraryPotionAmplifierSource("HEALING_BONUS", false), new ProfileHealthRegenerationBonusSource(), new LightArmorProfileFullArmorHealingBonusSource(), new HeavyArmorProfileFullArmorHealingBonusSource(), new ArbitraryPotionAmplifierSource("POISON_ANTI_HEAL", true));
        register("REFLECT_CHANCE", new ArbitraryPotionAmplifierSource("REFLECT_CHANCE", false), new HeavyArmorProfileFullArmorReflectChanceSource());
        register("REFLECT_FRACTION", new ArbitraryPotionAmplifierSource("REFLECT_FRACTION", false), new HeavyArmorProfileReflectFractionSource());
        register("BLEED_CHANCE", new HeavyWeaponsProfileBleedChanceSource(), new LightWeaponsProfileBleedChanceSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_BLEED_CHANCE", false));
        register("BLEED_DAMAGE", new HeavyWeaponsProfileBleedDamageSource(), new LightWeaponsProfileBleedDamageSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_BLEED_DAMAGE", false));
        register("BLEED_DURATION", new HeavyWeaponsProfileBleedDurationSource(), new LightWeaponsProfileBleedDurationSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_BLEED_DURATION", false));
        register("PARRY_ENEMY_DEBUFF_DURATION", new HeavyWeaponsProfileParryEnemyDebuffDurationSource(), new LightWeaponsProfileParryEnemyDebuffDurationSource());
        register("PARRY_DAMAGE_REDUCTION", new HeavyWeaponsProfileParryDamageReductionSource(), new LightWeaponsProfileParryDamageReductionSource());
        register("PARRY_DEBUFF_DURATION", new HeavyWeaponsProfileParryFailedDebuffDurationSource(), new LightWeaponsProfileParryFailedDebuffDurationSource());
        register("ENTITY_DROP_MULTIPLIER_BONUS", new PotionExtraDropsSource(), new ArbitraryGlobalEffectSource("entity_drops_multiplier"), new FarmingProfileAnimalDropMultiplierSource(), new LightWeaponsProfileDropsBonusSource(), new HeavyWeaponsProfileDropsBonusSource());
        register("DURABILITY_MULTIPLIER_BONUS");
        register("DISMOUNT_CHANCE", new ArbitraryAttributeOnAttackSource("CUSTOM_DISMOUNT_CHANCE", false));

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
        register("FARMING_ANIMAL_DROP_MULTIPLIER", new ArbitraryGlobalEffectSource("farming_animal_drop_multiplier"));
        register("FARMING_ANIMAL_RARE_DROP_MULTIPLIER", new FarmingProfileAnimalRareDropChanceMultiplierSource(), new ArbitraryGlobalEffectSource("farming_animal_rare_drop_multiplier"));
        register("FARMING_BREEDING_VANILLA_EXP_MULTIPLIER", new FarmingProfileBreedingVanillaEXPMultiplierSource());
        register("FARMING_DROP_MULTIPLIER", new FarmingProfileDropMultiplierSource(), new ArbitraryEnchantmentAmplifierSource("FARMING_EXTRA_DROPS"), new PotionExtraDropsSource(), new ArbitraryGlobalEffectSource("farming_drop_multiplier"));
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
        register("MINING_BLAST_RADIUS_MULTIPLIER", new ArbitraryEnchantmentAmplifierSource("EXPLOSION_POWER"), new MiningProfileBlastRadiusMultiplierSource(), new ArbitraryGlobalEffectSource("blast_mining_radius_multiplier"));
        register("MINING_BLAST_RARE_DROP_CHANCE_MULTIPLIER", new MiningProfileBlastRareDropChanceMultiplierSource(), new ArbitraryGlobalEffectSource("blast_mining_rare_drop_multiplier"));
        register("MINING_VANILLA_EXP_REWARD", new MiningProfileBlockExperienceRateSource(), new ArbitraryGlobalEffectSource("mining_vanilla_exp_reward"));
        register("MINING_MINING_DROP_MULTIPLIER", new MiningProfileMiningDropMultiplierSource(), new ArbitraryGlobalEffectSource("mining_drop_multiplier"));
        register("MINING_QUICK_MINE_DRAIN_RATE", new MiningProfileQuickMineDrainRateSource(), new ArbitraryGlobalEffectSource("mining_quick_mine_drain_rate"));
        register("MINING_ORE_EXPERIENCE_MULTIPLIER", new MiningProfileMiningOreExperienceMultiplierSource(), new ArbitraryGlobalEffectSource("mining_ore_experience_multiplier"));
        register("MINING_MINING_RARE_DROP_CHANCE_MULTIPLIER", new MiningProfileMiningRareDropChanceMultiplierSource(), new ArbitraryGlobalEffectSource("mining_rare_drop_multiplier"));
        register("MINING_EXP_GAIN_GENERAL", new MiningProfileGeneralEXPSource(), new ArbitraryGlobalEffectSource("mining_experience"));
        register("MINING_EXP_GAIN_MINING", new MiningProfileMiningEXPSource());
        register("MINING_EXP_GAIN_BLAST", new MiningProfileBlastEXPSource());

        register("LANDSCAPING_DIGGING_DROP_MULTIPLIER", new ArbitraryEnchantmentAmplifierSource("DIGGING_EXTRA_DROPS"), new LandscapingProfileDiggingDropMultiplierSource(), new ArbitraryGlobalEffectSource("landscaping_digging_drop_multiplier"));
        register("LANDSCAPING_DIGGING_RARE_DROP_MULTIPLIER", new ArbitraryEnchantmentAmplifierSource("DIGGING_RARE_DROPS"), new LandscapingProfileDiggingRareDropChanceMultiplierSource(), new ArbitraryGlobalEffectSource("landscaping_digging_rare_drop_multiplier"));
        register("LANDSCAPING_WOODCUTTING_DROP_MULTIPLIER", new ArbitraryEnchantmentAmplifierSource("WOODCUTTING_EXTRA_DROPS"), new LandscapingProfileWoodcuttingDropMultiplierSource(), new ArbitraryGlobalEffectSource("landscaping_woodcutting_drop_multiplier"));
        register("LANDSCAPING_WOODCUTTING_RARE_DROP_MULTIPLIER", new ArbitraryEnchantmentAmplifierSource("WOODCUTTING_RARE_DROPS"), new LandscapingProfileWoodcuttingRareDropChanceMultiplierSource(), new ArbitraryGlobalEffectSource("landscaping_woodcutting_rare_drop_multiplier"));
        register("LANDSCAPING_WOODSTRIPPING_RARE_DROP_MULTIPLIER", new LandscapingProfileWoodstrippingRareDropChanceMultiplierSource(), new ArbitraryGlobalEffectSource("landscaping_woodstripping_rare_drop_multiplier"));
        register("LANDSCAPING_INSTANT_GROWTH_RATE", new LandscapingProfileInstantGrowthRateSource(), new ArbitraryGlobalEffectSource("landscaping_instant_growth_rate"));
        register("LANDSCAPING_PLACEMENT_REACH_BONUS", new LandscapingProfilePlaceReachBonusSource(), new ArbitraryGlobalEffectSource("landscaping_placement_reach"));
        register("LANDSCAPING_EXP_GAIN_GENERAL", new LandscapingProfileGeneralEXPSource(), new ArbitraryGlobalEffectSource("landscaping_experience"));
        register("LANDSCAPING_WOODCUTTING_VANILLA_EXP_REWARD", new LandscapingProfileWoodcuttingExperienceRateSource(), new ArbitraryGlobalEffectSource("landscaping_woodcutting_vanilla_exp_reward"));
        register("LANDSCAPING_DIGGING_VANILLA_EXP_REWARD", new LandscapingProfileDiggingExperienceRateSource(), new ArbitraryGlobalEffectSource("landscaping_digging_vanilla_exp_reward"));
        register("LANDSCAPING_EXP_GAIN_WOODCUTTING", new LandscapingProfileWoodcuttingEXPSource());
        register("LANDSCAPING_EXP_GAIN_WOODSTRIPPING", new LandscapingProfileWoodstrippingEXPSource());
        register("LANDSCAPING_EXP_GAIN_DIGGING", new LandscapingProfileDiggingEXPSource());

        register("ARCHERY_DAMAGE", new OverleveledEquipmentArcheryDamagePenaltySource(), new ArbitraryOffensiveEnchantmentAmplifierSource("ARCHERY_DAMAGE"), new ArbitraryOffensivePotionAmplifierSource("ARCHERY_DAMAGE", false));
        register("ARCHERY_BOW_DAMAGE_MULTIPLIER", new ArcheryProfileBowDamageMultiplierSource());
        register("ARCHERY_CROSSBOW_DAMAGE_MULTIPLIER", new ArcheryProfileCrossBowDamageMultiplierSource());
        register("ARCHERY_AMMO_SAVE_CHANCE", new ArbitraryEnchantmentAmplifierSource("ARCHERY_AMMO_SAVE"), new ArbitraryPotionAmplifierSource("ARCHERY_AMMO_SAVE", false), new ArcheryProfileAmmoSaveChanceSource());
        register("ARCHERY_BOW_CRIT_CHANCE", new ArcheryProfileBowCritChanceSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_CRIT_CHANCE", false));
        register("ARCHERY_CROSSBOW_CRIT_CHANCE", new ArcheryProfileCrossBowCritChanceSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_CRIT_CHANCE", false));
        register("ARCHERY_CRIT_DAMAGE_MULTIPLIER", new ArcheryProfileCritDamageMultiplierSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_CRIT_DAMAGE", false));
        register("ARCHERY_CHARGED_SHOT_COOLDOWN", new ArcheryProfileChargeShotCooldownSource());
        register("ARCHERY_CHARGED_SHOT_KNOCKBACK_BONUS", new ArcheryProfileChargeShotKnockbackBonusSource());
        register("ARCHERY_CHARGED_SHOT_DAMAGE_MULTIPLIER", new ArcheryProfileChargeShotDamageMultiplierSource());
        register("ARCHERY_CHARGED_SHOT_PIERCING_BONUS", new ArcheryProfileChargeShotPiercingBonusSource());
        register("ARCHERY_CHARGED_SHOT_VELOCITY_BONUS", new ArcheryProfileChargeShotVelocityBonusSource());
        register("ARCHERY_CHARGED_SHOT_CHARGES", new ArcheryProfileChargeShotChargesSource());
        register("ARCHERY_STUN_CHANCE", new ArcheryProfileStunChanceSource());
        register("ARCHERY_STUN_DURATION", new ArcheryProfileStunDurationSource());
        register("ARCHERY_INFINITY_DAMAGE_MULTIPLIER", new ArcheryProfileInfinityDamageMultiplierSource());
        register("ARCHERY_INACCURACY", new ArbitraryEnchantmentAmplifierSource("ARCHERY_ACCURACY", true), new ArbitraryPotionAmplifierSource("ARCHERY_ACCURACY", true), new ArcheryProfileInaccuracySource());
        register("ARCHERY_DISTANCE_DAMAGE_MULTIPLIER_BASE", new ArcheryProfileDistanceDamageBaseSource());
        register("ARCHERY_DISTANCE_DAMAGE_MULTIPLIER", new ArcheryProfileDistanceDamageMultiplierSource());
        register("ARCHERY_EXP_GAIN_BOW", new ArcheryProfileBowEXPSource());
        register("ARCHERY_EXP_GAIN_CROSSBOW", new ArcheryProfileCrossBowEXPSource());
        register("ARCHERY_EXP_GAIN_GENERAL", new ArcheryProfileGeneralEXPSource());

        register("LIGHT_ARMOR_MULTIPLIER", new LightArmorProfileArmorValueMultiplierSource(), new LightArmorProfileFullArmorArmorValueBonusSource(), new ArbitraryPotionAmplifierSource("LIGHT_ARMOR_FRACTION_BONUS", false));
        register("LIGHT_ARMOR_EXP_GAIN", new LightArmorEXPSource());

        register("HEAVY_ARMOR_MULTIPLIER", new HeavyArmorProfileArmorValueMultiplierSource(), new HeavyArmorProfileFullArmorArmorValueBonusSource(), new ArbitraryPotionAmplifierSource("HEAVY_ARMOR_FRACTION_BONUS", false));
        register("HEAVY_ARMOR_EXP_GAIN", new HeavyArmorEXPSource());

        register("LIGHT_WEAPONS_CRIT_CHANCE", new LightWeaponsCritChanceSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_CRIT_CHANCE", false));
        register("LIGHT_WEAPONS_CRIT_DAMAGE", new LightWeaponsCritDamageMultiplierSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_CRIT_DAMAGE", false));
        register("LIGHT_WEAPONS_DAMAGE_MULTIPLIER", new VelocityDamageAttributeOnAttackSource(), new LightWeaponsProfileDamageMultiplierSource(), new LightWeaponsProfileHeavyArmorDamageBonusSource(), new LightWeaponsProfileLightArmorDamageBonusSource());
        register("LIGHT_WEAPONS_EXP_GAIN", new LightWeaponsEXPSource());
        register("LIGHT_WEAPONS_STUN_CHANCE", new LightWeaponsProfileStunChanceSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_STUN_CHANCE", false));
        register("LIGHT_WEAPONS_RARE_DROP_MULTIPLIER", new LightWeaponsProfileRareDropMultiplierSource());

        register("HEAVY_WEAPONS_CRIT_CHANCE", new HeavyWeaponsCritChanceSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_CRIT_CHANCE", false));
        register("HEAVY_WEAPONS_CRIT_DAMAGE", new HeavyWeaponsCritDamageMultiplierSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_CRIT_DAMAGE", false));
        register("HEAVY_WEAPONS_DAMAGE_MULTIPLIER", new VelocityDamageAttributeOnAttackSource(), new HeavyWeaponsProfileDamageMultiplierSource(), new HeavyWeaponsProfileHeavyArmorDamageBonusSource(), new HeavyWeaponsProfileLightArmorDamageBonusSource());
        register("HEAVY_WEAPONS_EXP_GAIN", new HeavyWeaponsEXPSource());
        register("HEAVY_WEAPONS_STUN_CHANCE", new HeavyWeaponsProfileStunChanceSource(), new ArbitraryAttributeOnAttackSource("CUSTOM_STUN_CHANCE", false));
        register("HEAVY_WEAPONS_RARE_DROP_MULTIPLIER", new HeavyWeaponsProfileRareDropMultiplierSource());
    }

    /**
     * Registers a stat name to be used and collected stats from
     * @param stat the stat name to register
     */
    public void registerGenericSource(String stat){
        sources.put(stat, new HashSet<>());
    }

    /**
     * Registers a new stat source to a stat name. If the stat does not yet exist, it is registered also.
     * This source will be used to conditionally collect a specific stat from a given entity.
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
     * This method should only be used in situations only involving a single entity (such as a player crafting, taking damage
     * from falling, or walking)
     * @param stat the stat to gather its total from
     * @param p the player to gether their stats from
     * @param use if true, it will be assumed the stat is actually being used in practice rather than being a visual
     *            for show. Example: if a player were to craft an item and had some potion effect to boost their
     *            crafting quality, use would be true. If the player were to only look at the item in the crafting menu,
     *            so not actually having crafted it just yet, use would be false.
     * @return the collective stat number
     */
    public double getStats(String stat, Entity p, boolean use) {
        if (!sources.containsKey(stat)) {
            return 0;
        }
        Collection<AccumulativeStatSource> existingSources = sources.get(stat);
        double value = 0;
        for (AccumulativeStatSource s : existingSources){
//            if (stat.equals("DAMAGE_RESISTANCE") && p instanceof Player && p.getName().equals("Athlaeos")){
//                System.out.println("damage resistance " + value + " added from " + s.getClass().getSimpleName());
//            }
            value += s.add(p, use);
        }
        return Utils.round(value, 6);
    }

    /**
     * Collects all the stats of the given stat name. For example, if 'LIGHT_GENERIC_ARMOR' is given, it will return the
     * given entity's total armor points influenced by the second entity given, which would be the accumulation of
     * actual skill, potion effects, global boosters, etc.
     * This method should be used in situations involving two entities, such as an entity being attacked by another
     * entity. Here, by convention, the FIRST entity is the entity being damaged, while the SECOND entity is the entity
     * damaging the first entity.
     * If the second entity is null, getStats(String, Entity, boolean) is being called instead.
     * If a stat source associated with the given stat is not an EvEAccumulativeStatSource, a regular AccumulativeStatSource
     * is being used instead where the second entity is not involved.
     * @param stat the stat to gather its total from
     * @param p the entity to gether their stats from
     * @param e the second entity involved in stat accumulation
     * @param use if true, it will be assumed the stat is actually being used in practice rather than being a visual
     *            for show. Example: if a player were to craft an item and had some potion effect to boost their
     *            crafting quality, use would be true. If the player were to only look at the item in the crafting menu,
     *            so not actually having crafted it just yet, use would be false.
     * @return the collective stat number
     */
    public double getStats(String stat, Entity p, Entity e, boolean use) {
        if (!sources.containsKey(stat)) {
            return 0;
        }
        if (e == null) {
            return getStats(stat, p, use);
        }
        Collection<AccumulativeStatSource> existingSources = sources.get(stat);
        double value = 0;
        for (AccumulativeStatSource s : existingSources){
            if (s instanceof EvEAccumulativeStatSource){
//                if (stat.equals("DAMAGE_RESISTANCE") && p instanceof Player && p.getName().equals("Athlaeos")){
//                    System.out.println("damage resistance " + value + " added from " + s.getClass().getSimpleName());
//                }
                value += ((EvEAccumulativeStatSource) s).add(p, e, use);
            } else {
//                if (stat.equals("DAMAGE_RESISTANCE") && p instanceof Player && p.getName().equals("Athlaeos")){
//                    System.out.println("damage resistance " + value + " added from " + s.getClass().getSimpleName());
//                }
                value += s.add(p, use);
            }
        }
        return Utils.round(value, 6);
    }

    public double getEntityStatsIncludingCache(String stat, Entity p, long refreshAfter, boolean use){
        if (isStatCached(p, stat)){
            return getCachedStat(p, stat);
        } else {
            double statValue = getStats(stat, p, use);
            cacheStat(p, stat, statValue, refreshAfter);
            return statValue;
        }
    }

    public double getEntityStatsIncludingCache(String stat, Entity p, Entity e, long refreshAfter, boolean use){
        if (isStatCached(p, stat)){
            return getCachedStat(p, stat);
        } else {
            double statValue = getStats(stat, p, e, use);
            cacheStat(p, stat, statValue, refreshAfter);
            return statValue;
        }
    }

    public double getAttackerStatsIncludingCache(String stat, Entity p, Entity e, long refreshAfter, boolean use){
        if (isStatCached(e, stat)){
            return getCachedStat(e, stat);
        } else {
            double statValue = getStats(stat, p, e, use);
            cacheStat(e, stat, statValue, refreshAfter);
            return statValue;
        }
    }

    public static AccumulativeStatManager getInstance(){
        if (manager == null) manager = new AccumulativeStatManager();
        return manager;
    }

    private final Map<Entity, Map<String, Map.Entry<Long, Double>>> statCache = new HashMap<>();
    private long lastMapCleanup = System.currentTimeMillis();

    /**
     * Attempts to get a cached stat from an entity
     * isStatCached() should be checked beforehand, if the stat is not cached or expired this method return 0
     * @param e the entity to check the cache for
     * @param stat the stat to get from the cache
     * @return the cached stat value, or 0 if the stat is not cached. This will still return the value even if the stat is expired
     */
    public double getCachedStat(Entity e, String stat){
        Map<String, Map.Entry<Long, Double>> currentCachedStats = statCache.getOrDefault(e, new HashMap<>());
        if (currentCachedStats.get(stat) != null){
            return currentCachedStats.get(stat).getValue();
        }
        return 0;
    }

    /**
     * Caches a stat for an entity for a specified amount of time
     * @param e the entity to cache for
     * @param stat the stat to cache
     * @param amount the value that will be cached
     * @param cacheFor the amount of time the cached value will be valid for, in milliseconds
     */
    public void cacheStat(Entity e, String stat, double amount, long cacheFor){
        Map<String, Map.Entry<Long, Double>> currentCachedStats = statCache.getOrDefault(e, new HashMap<>());
        currentCachedStats.put(stat, new Map.Entry<Long, Double>() {
            private final long time = System.currentTimeMillis() + cacheFor;
            @Override
            public Long getKey() { return time; }
            @Override
            public Double getValue() { return amount; }
            @Override
            public Double setValue(Double value) { return null; }
        });
        statCache.put(e, currentCachedStats);
    }

    /**
     * Checks if a certain stat is cached, and if so if the stat hasn't expired.
     * This method also cleans up the cache every 2 minutes, removing any entity where isValid() returns false
     * @param e the entity to check their cached stat
     * @param stat the stat to see if it's cached
     * @return true if the stat is cached and unexpired, false otherwise
     */
    public boolean isStatCached(Entity e, String stat){
        if (lastMapCleanup + 120000 < System.currentTimeMillis()){
            // cleaning up map every 2 minutes
            Map<Entity, Map<String, Map.Entry<Long, Double>>> clone = new HashMap<>(statCache);
            for (Entity entity : clone.keySet()){
                if (!entity.isValid()) statCache.remove(entity); // remove invalid entities from the map
            }
            lastMapCleanup = System.currentTimeMillis();
        }
        if (statCache.containsKey(e)){
            if (statCache.getOrDefault(e, new HashMap<>()).get(stat) != null){
                return statCache.get(e).get(stat).getKey() > System.currentTimeMillis();
            }
        }
        return false;
    }
}
