package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.items.EnchantmentType;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.placeholder.placeholders.*;
import me.athlaeos.valhallammo.placeholder.placeholders.account.*;
import me.athlaeos.valhallammo.placeholder.placeholders.alchemy.*;
import me.athlaeos.valhallammo.placeholder.placeholders.archery.*;
import me.athlaeos.valhallammo.placeholder.placeholders.enchanting.*;
import me.athlaeos.valhallammo.placeholder.placeholders.farming.*;
import me.athlaeos.valhallammo.placeholder.placeholders.heavy_armor.*;
import me.athlaeos.valhallammo.placeholder.placeholders.heavy_weapons.*;
import me.athlaeos.valhallammo.placeholder.placeholders.landscaping.*;
import me.athlaeos.valhallammo.placeholder.placeholders.light_armor.*;
import me.athlaeos.valhallammo.placeholder.placeholders.light_weapons.*;
import me.athlaeos.valhallammo.placeholder.placeholders.mining.*;
import me.athlaeos.valhallammo.placeholder.placeholders.smithing.SmithingEXPMultipliers;
import me.athlaeos.valhallammo.placeholder.placeholders.smithing.SmithingSkill;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderManager {
    private static PlaceholderManager manager = null;
    private final Map<String, Placeholder> placeholders = new HashMap<>();

    public static PlaceholderManager getInstance() {
        if (manager == null) manager = new PlaceholderManager();
        return manager;
    }

    public PlaceholderManager() {
        // GENERAL/EVERYTHING
        registerPlaceholder(new SkillEXPMultiplier("%exp_gain%"));

        for (String skill : SkillProgressionManager.getInstance().getAllSkills().keySet()) {
            String name = skill.toLowerCase();
            registerPlaceholder(new EXPCurrent("%" + name + "_exp_current%", skill));
            registerPlaceholder(new EXPNextLevel("%" + name + "_exp_next%", skill));
            registerPlaceholder(new EXPPercentProgress("%" + name + "_exp_percent%", skill));
            registerPlaceholder(new EXPTotal("%" + name + "_exp_total%", skill));
            registerPlaceholder(new LevelCurrent("%" + name + "_level%", skill));
            registerPlaceholder(new LevelNext("%" + name + "_level_next%", skill));
        }

        for (String statSource : AccumulativeStatManager.getInstance().getSources().keySet()) {
            registerPlaceholder(new TotalStatPlaceholder("%stat_source_" + statSource.toLowerCase() + "p0%", statSource, 0));
            registerPlaceholder(new TotalStatPlaceholder("%stat_source_" + statSource.toLowerCase() + "p1%", statSource, 1));
            registerPlaceholder(new TotalStatPlaceholder("%stat_source_" + statSource.toLowerCase() + "p2%", statSource, 2));
            registerPlaceholder(new TotalStatPlaceholder("%stat_source_" + statSource.toLowerCase() + "p3%", statSource, 3));
            registerPlaceholder(new TotalStatPlaceholder("%stat_source_" + statSource.toLowerCase() + "p4%", statSource, 4));
        }

        registerPlaceholder(new GeneralPlayerName("%player_name%"));

        registerPlaceholder(new GeneralPlayerNickName("%player_nickname%"));

        // SMITHING
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_general%", null));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_wood%", MaterialClass.WOOD));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_leather%", MaterialClass.LEATHER));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_stone%", MaterialClass.STONE));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_chain%", MaterialClass.CHAINMAIL));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_iron%", MaterialClass.IRON));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_gold%", MaterialClass.GOLD));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_diamond%", MaterialClass.DIAMOND));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_netherite%", MaterialClass.NETHERITE));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_prismarine%", MaterialClass.PRISMARINE));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_membrane%", MaterialClass.MEMBRANE));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_bow%", MaterialClass.BOW));
        registerPlaceholder(new SmithingEXPMultipliers("%smithing_expmult_crossbow%", MaterialClass.CROSSBOW));

        registerPlaceholder(new SmithingSkill("%smithing_skill_general%", null));
        registerPlaceholder(new SmithingSkill("%smithing_skill_wood%", MaterialClass.WOOD));
        registerPlaceholder(new SmithingSkill("%smithing_skill_leather%", MaterialClass.LEATHER));
        registerPlaceholder(new SmithingSkill("%smithing_skill_stone%", MaterialClass.STONE));
        registerPlaceholder(new SmithingSkill("%smithing_skill_chain%", MaterialClass.CHAINMAIL));
        registerPlaceholder(new SmithingSkill("%smithing_skill_iron%", MaterialClass.IRON));
        registerPlaceholder(new SmithingSkill("%smithing_skill_gold%", MaterialClass.GOLD));
        registerPlaceholder(new SmithingSkill("%smithing_skill_diamond%", MaterialClass.DIAMOND));
        registerPlaceholder(new SmithingSkill("%smithing_skill_netherite%", MaterialClass.NETHERITE));
        registerPlaceholder(new SmithingSkill("%smithing_skill_prismarine%", MaterialClass.PRISMARINE));
        registerPlaceholder(new SmithingSkill("%smithing_skill_membrane%", MaterialClass.MEMBRANE));
        registerPlaceholder(new SmithingSkill("%smithing_skill_bow%", MaterialClass.BOW));
        registerPlaceholder(new SmithingSkill("%smithing_skill_crossbow%", MaterialClass.CROSSBOW));

        // ALCHEMY
        registerPlaceholder(new AlchemySkill("%alchemy_skill_general%", null));
        registerPlaceholder(new AlchemySkill("%alchemy_skill_debuff%", PotionType.DEBUFF));
        registerPlaceholder(new AlchemySkill("%alchemy_skill_buff%", PotionType.BUFF));
        registerPlaceholder(new AlchemyPotionVelocity("%alchemy_potion_velocity%"));
        registerPlaceholder(new AlchemyBrewingSpeed("%alchemy_brewing_speed%"));
        registerPlaceholder(new AlchemyIngredientSaveChance("%alchemy_ingredient_save_chance%"));
        registerPlaceholder(new AlchemyPotionSaveChance("%alchemy_potion_save_chance%"));

        // ENCHANTING
        registerPlaceholder(new EnchantingSkill("%enchanting_skill_general%", null));
        registerPlaceholder(new EnchantingSkill("%enchanting_skill_vanilla%", EnchantmentType.VANILLA));
        registerPlaceholder(new EnchantingSkill("%enchanting_skill_custom%", EnchantmentType.CUSTOM));
        registerPlaceholder(new EnchantingMaxCustomAllowed("%enchanting_max_custom_allowed%"));
        registerPlaceholder(new EnchantingLapisSaveChance("%enchanting_lapis_save_chance%"));
        registerPlaceholder(new EnchantingRefundChance("%enchanting_refund_chance%"));
        registerPlaceholder(new EnchantingRefundAmount("%enchanting_refund_amount%"));
        registerPlaceholder(new EnchantingVanillaEXPMultiplier("%enchanting_vanilla_exp_multiplier%"));
        registerPlaceholder(new EnchantingAmplifyChance("%enchanting_amplify_chance%"));

        // FARMING
        registerPlaceholder(new FarmingAnimalDamageMultiplier("%farming_animal_damage_multiplier%"));
        registerPlaceholder(new FarmingAnimalDropMultiplier("%farming_animal_drop_multiplier%"));
        registerPlaceholder(new FarmingBabyAnimalAgeMultiplier("%farming_animal_age_multiplier%"));
        registerPlaceholder(new FarmingBeeAggroImmune("%farming_bee_aggro_immune%"));
        registerPlaceholder(new FarmingBreedingEXPMultiplier("%farming_breeding_exp_multiplier%"));
        registerPlaceholder(new FarmingBreedingVanillaEXPMultiplier("%farming_breeding_vanilla_exp_multiplier%"));
        registerPlaceholder(new FarmingDropMultiplier("%farming_drop_multiplier%"));
        registerPlaceholder(new FarmingFarmingEXPMultiplier("%farming_farming_exp_multiplier%"));
        registerPlaceholder(new FarmingFarmingVanillaEXPReward("%farming_farming_exp_reward%"));
        registerPlaceholder(new FarmingFishingEXPMultiplier("%farming_fishing_exp_multiplier%"));
        registerPlaceholder(new FarmingFishingRewardTier("%farming_fishing_reward_tier%"));
        registerPlaceholder(new FarmingFishingTimeMultiplier("%farming_fishing_time_multiplier%"));
        registerPlaceholder(new FarmingFishingVanillaEXPMultiplier("%farming_fishing_vanilla_exp_multiplier%"));
        registerPlaceholder(new FarmingGeneralEXPMultiplier("%farming_general_exp_multiplier%"));
        registerPlaceholder(new FarmingHiveHoneySaveChance("%farming_honey_save_chance%"));
        registerPlaceholder(new FarmingInstantHarvestingUnlocked("%farming_instant_harvesting_unlocked%"));
        registerPlaceholder(new FarmingRareDropMultiplier("%farming_rare_drop_multiplier%"));
        registerPlaceholder(new FarmingUltraHarvestingCooldown("%farming_ultra_harvesting_cooldown_timestamp%"));

        // MINING
        registerPlaceholder(new BlastMiningDropMultiplier("%blast_drop_multiplier%"));
        registerPlaceholder(new BlastMiningEXPMultiplier("%blast_exp_multiplier%"));
        registerPlaceholder(new BlastMiningRareDropMultiplier("%blast_rare_drop_multiplier%"));
        registerPlaceholder(new BlastRadiusMultiplier("%blast_tnt_radius_multiplier%"));
        registerPlaceholder(new BlastTntFortuneLevel("%blast_fortune_level%"));
        registerPlaceholder(new ExplosionDamageMultiplier("%blast_explosion_damage_multiplier%"));
        registerPlaceholder(new MiningDropMultiplier("%mining_drop_multiplier%"));
        registerPlaceholder(new MiningExperienceDropRate("%mining_exp_reward%"));
        registerPlaceholder(new MiningEXPMultiplier("%mining_exp_multiplier%"));
        registerPlaceholder(new MiningGeneralEXPMultiplier("%mining_general_exp_multiplier%"));
        registerPlaceholder(new MiningOreExperienceMultiplier("%mining_ore_exp_multiplier%"));
        registerPlaceholder(new MiningRareDropMultiplier("%mining_rare_drop_multiplier%"));
        registerPlaceholder(new QuickMineDurabilityDamageMultiplier("%mining_quick_mine_durability_multiplier%"));
        registerPlaceholder(new QuickMineHungerDrainSpeed("%mining_quick_mine_hunger_decay_capacity%"));

        // LANDSCAPING
        registerPlaceholder(new DiggingDropMultiplier("%digging_drop_multiplier%"));
        registerPlaceholder(new DiggingRareDropMultiplier("%digging_rare_drop_multiplier%"));
        registerPlaceholder(new DiggingVanillaEXPReward("%digging_exp_reward%"));
        registerPlaceholder(new LandscapingDiggingEXPMultiplier("%landscaping_digging_exp_multiplier%"));
        registerPlaceholder(new LandscapingGeneralEXPMultiplier("%landscaping_general_exp_multiplier%"));
        registerPlaceholder(new LandscapingWoodcuttingEXPMultiplier("%landscaping_woodcutting_exp_multiplier%"));
        registerPlaceholder(new LandscapingWoodstrippingEXPMultiplier("%landscaping_woodstripping_exp_multiplier%"));
        registerPlaceholder(new LandscapingSaplingReplaceUnlocked("%landscaping_sapling_replace_unlocked%"));
        registerPlaceholder(new WoodcuttingDropMultiplier("%woodcutting_drop_multiplier%"));
        registerPlaceholder(new WoodcuttingRareDropMultiplier("%woodcutting_rare_drop_multiplier%"));
        registerPlaceholder(new WoodstrippingRareDropMultiplier("%woodstripping_drop_multiplier%"));
        registerPlaceholder(new WoodcuttingVanillaEXPReward("%woodcutting_exp_reward%"));

        // ARCHERY
        registerPlaceholder(new ArcheryAmmoSaveChance("%archery_ammo_save_chance%"));
        registerPlaceholder(new ArcheryBowCritChance("%archery_bow_crit_chance%"));
        registerPlaceholder(new ArcheryBowDamageMultiplier("%archery_bow_damage_multiplier%"));
        registerPlaceholder(new ArcheryBowEXPMultiplier("%archery_bow_exp_multiplier%"));
        registerPlaceholder(new ArcheryChargedShotCooldown("%archery_charged_shot_cooldown%"));
        registerPlaceholder(new ArcheryChargedShotDamageMultiplier("%archery_charged_shot_damage_multiplier%"));
        registerPlaceholder(new ArcheryChargedShotKnockbackBonus("%archery_charged_shot_knockback_bonus%"));
        registerPlaceholder(new ArcheryCritDamageMultiplier("%archery_crit_damage_multiplier%"));
        registerPlaceholder(new ArcheryCritOnFacingAway("%archery_crit_on_facing_away%"));
        registerPlaceholder(new ArcheryCritOnStandingStill("%archery_crit_on_standing_still%"));
        registerPlaceholder(new ArcheryCritOnStandingStill("%archery_crit_on_stealth%"));
        registerPlaceholder(new ArcheryCrossbowCritChance("%archery_crossbow_crit_chance%"));
        registerPlaceholder(new ArcheryCritDamageMultiplier("%archery_crossbow_damage_multiplier%"));
        registerPlaceholder(new ArcheryCrossbowEXPMultiplier("%archery_crossbow_exp_multiplier%"));
        registerPlaceholder(new ArcheryDamageDistanceBaseMultiplier("%archery_damage_distance_base_multiplier%"));
        registerPlaceholder(new ArcheryDamageDistanceMultiplier("%archery_damage_distance_multiplier%"));
        registerPlaceholder(new ArcheryGeneralEXPMultiplier("%archery_general_exp_multiplier%"));
        registerPlaceholder(new ArcheryInaccuracy("%archery_inaccuracy%"));
        registerPlaceholder(new ArcheryInfinityDamageMultiplier("%archery_infinity_damage_multiplier%"));
        registerPlaceholder(new ArcheryStunChance("%archery_stun_chance%"));
        registerPlaceholder(new ArcheryStunDuration("%archery_stun_duration%"));
        registerPlaceholder(new ArcheryStunOnCrit("%archery_stun_on_crit%"));

        // LIGHT ARMOR
        registerPlaceholder(new LightArmorAdrenalineCooldown("%light_armor_adrenaline_cooldown%"));
        registerPlaceholder(new LightArmorAdrenalineLevel("%light_armor_adrenaline_level%"));
        registerPlaceholder(new LightArmorAdrenalineThreshold("%light_armor_adrenaline_threshold%"));
        registerPlaceholder(new LightArmorArmorMultiplier("%light_armor_armor_multiplier%"));
        registerPlaceholder(new LightArmorDamageResistance("%light_armor_damage_resistance%"));
        registerPlaceholder(new LightArmorExplosionResistance("%light_armor_explosion_resistance%"));
        registerPlaceholder(new LightArmorEXPMultiplier("%light_armor_exp_multiplier%"));
        registerPlaceholder(new LightArmorFallDamageResistance("%light_armor_fall_damage_resistance%"));
        registerPlaceholder(new LightArmorFireResistance("%light_armor_fire_resistance%"));
        registerPlaceholder(new LightArmorFullSetArmorBonus("%light_armor_full_set_armor_bonus%"));
        registerPlaceholder(new LightArmorFullSetDodgeChance("%light_armor_full_set_dodge_chance%"));
        registerPlaceholder(new LightArmorFullSetHungerSaveChance("%light_armor_full_set_hunger_save_chance%"));
        registerPlaceholder(new LightArmorFullSetHealingBonus("%light_armor_full_set_healing_bonus%"));
        registerPlaceholder(new LightArmorFullSetPiecesRequired("%light_armor_full_set_pieces_required%"));
        registerPlaceholder(new LightArmorKnockbackResistance("%light_armor_knockback_resistance%"));
        registerPlaceholder(new LightArmorMagicResistance("%light_armor_magic_resistance%"));
        registerPlaceholder(new LightArmorMeleeResistance("%light_armor_melee_resistance%"));
        registerPlaceholder(new LightArmorMovementSpeedPenalty("%light_armor_movement_speed_penalty%"));
        registerPlaceholder(new LightArmorPoisonResistance("%light_armor_poison_resistance%"));
        registerPlaceholder(new LightArmorProjectileResistance("%light_armor_projectile_resistance%"));

        // HEAVY ARMOR
        registerPlaceholder(new HeavyArmorRageCooldown("%heavy_armor_rage_cooldown%"));
        registerPlaceholder(new HeavyArmorRageLevel("%heavy_armor_rage_level%"));
        registerPlaceholder(new HeavyArmorRageThreshold("%heavy_armor_rage_threshold%"));
        registerPlaceholder(new HeavyArmorArmorMultiplier("%heavy_armor_armor_multiplier%"));
        registerPlaceholder(new HeavyArmorDamageResistance("%heavy_armor_damage_resistance%"));
        registerPlaceholder(new HeavyArmorExplosionResistance("%heavy_armor_explosion_resistance%"));
        registerPlaceholder(new HeavyArmorEXPMultiplier("%heavy_armor_exp_multiplier%"));
        registerPlaceholder(new HeavyArmorFallDamageResistance("%heavy_armor_fall_damage_resistance%"));
        registerPlaceholder(new HeavyArmorFireResistance("%heavy_armor_fire_resistance%"));
        registerPlaceholder(new HeavyArmorFullSetArmorBonus("%heavy_armor_full_set_armor_bonus%"));
        registerPlaceholder(new HeavyArmorReflectFraction("%heavy_armor_reflect_fraction%"));
        registerPlaceholder(new HeavyArmorFullSetReflectChance("%heavy_armor_full_set_reflect_chance%"));
        registerPlaceholder(new HeavyArmorFullSetHungerSaveChance("%heavy_armor_full_set_hunger_save_chance%"));
        registerPlaceholder(new HeavyArmorFullSetHealingBonus("%heavy_armor_full_set_healing_bonus%"));
        registerPlaceholder(new HeavyArmorFullSetPiecesRequired("%heavy_armor_full_set_pieces_required%"));
        registerPlaceholder(new HeavyArmorKnockbackResistance("%heavy_armor_knockback_resistance%"));
        registerPlaceholder(new HeavyArmorMagicResistance("%heavy_armor_magic_resistance%"));
        registerPlaceholder(new HeavyArmorMeleeResistance("%heavy_armor_melee_resistance%"));
        registerPlaceholder(new HeavyArmorMovementSpeedPenalty("%heavy_armor_movement_speed_penalty%"));
        registerPlaceholder(new HeavyArmorPoisonResistance("%heavy_armor_poison_resistance%"));
        registerPlaceholder(new HeavyArmorProjectileResistance("%heavy_armor_projectile_resistance%"));

        // LIGHT WEAPONS
        registerPlaceholder(new LightWeaponsAttackSpeedBonus("%light_weapons_attack_speed_bonus%"));
        registerPlaceholder(new LightWeaponsBleedChance("%light_weapons_bleed_chance%"));
        registerPlaceholder(new LightWeaponsBleedDamage("%light_weapons_bleed_damage%"));
        registerPlaceholder(new LightWeaponsBleedDuration("%light_weapons_bleed_duration%"));
        registerPlaceholder(new LightWeaponsBleedOnCrit("%light_weapons_bleed_on_crit%"));
        registerPlaceholder(new LightWeaponsCoatingEnemyAmplifierMultiplier("%light_weapons_coating_enemy_amplifier_multiplier%"));
        registerPlaceholder(new LightWeaponsCoatingEnemyDurationMultiplier("%light_weapons_coating_enemy_duration_multiplier%"));
        registerPlaceholder(new LightWeaponsCoatingSelfDurationMultiplier("%light_weapons_self_duration_multiplier%"));
        registerPlaceholder(new LightWeaponsCritChance("%light_weapons_crit_chance%"));
        registerPlaceholder(new LightWeaponsCritDamageMultiplier("%light_weapons_crit_damage_multiplier%"));
        registerPlaceholder(new LightWeaponsCritOnBleed("%light_weapons_crit_on_bleed%"));
        registerPlaceholder(new LightWeaponsCritOnStealth("%light_weapons_crit_on_stealth%"));
        registerPlaceholder(new LightWeaponsCritOnStun("%light_weapons_crit_on_stun%"));
        registerPlaceholder(new LightWeaponsDamageMultiplier("%light_weapons_damage_multiplier%"));
        registerPlaceholder(new LightWeaponsDropBonus("%light_weapons_drops_bonus%"));
        registerPlaceholder(new LightWeaponsFlatArmorIgnored("%light_weapons_flat_armor_penetration%"));
        registerPlaceholder(new LightWeaponsFlatLightArmorIgnored("%light_weapons_flat_heavy_armor_penetration%"));
        registerPlaceholder(new LightWeaponsFlatLightArmorIgnored("%light_weapons_flat_light_armor_penetration%"));
        registerPlaceholder(new LightWeaponsFractionArmorIgnored("%light_weapons_fraction_armor_penetration%"));
        registerPlaceholder(new LightWeaponsFractionLightArmorIgnored("%light_weapons_fraction_heavy_armor_penetration%"));
        registerPlaceholder(new LightWeaponsFractionLightArmorIgnored("%light_weapons_fraction_light_armor_penetration%"));
        registerPlaceholder(new LightWeaponsLightArmorDamageBonus("%light_weapons_heavy_armor_damage_bonus%"));
        registerPlaceholder(new LightWeaponsImmunityFrameReduction("%light_weapons_immunity_frame_reduction%"));
        registerPlaceholder(new LightWeaponsKnockbackBonus("%light_weapons_knockback_bonus%"));
        registerPlaceholder(new LightWeaponsLightArmorDamageBonus("%light_weapons_light_armor_damage_bonus%"));
        registerPlaceholder(new LightWeaponsParryCooldown("%light_weapons_parry_cooldown%"));
        registerPlaceholder(new LightWeaponsParryDamageReduction("%light_weapons_parry_damage_reduction%"));
        registerPlaceholder(new LightWeaponsParryEnemyDebuffDuration("%light_weapons_parry_enemy_debuff_duration%"));
        registerPlaceholder(new LightWeaponsParryFailedDebuffDuration("%light_weapons_parry_failed_debuff_duration%"));
        registerPlaceholder(new LightWeaponsParryTimeFrame("%light_weapons_parry_time_frame%"));
        registerPlaceholder(new LightWeaponsParryVulnerableTimeFrame("%light_weapons_parry_vulnerable_frame%"));
        registerPlaceholder(new LightWeaponsRareDropMultiplier("%light_weapons_rare_drops_bonus%"));
        registerPlaceholder(new LightWeaponsStunChance("%light_weapons_stun_chance%"));
        registerPlaceholder(new LightWeaponsStunDuration("%light_weapons_stun_duration%"));
        registerPlaceholder(new LightWeaponsUnlockedWeaponCoating("%light_weapons_unlocked_weapon_coating%"));

        // HEAVY WEAPONS
        registerPlaceholder(new HeavyWeaponsAttackSpeedBonus("%heavy_weapons_attack_speed_bonus%"));
        registerPlaceholder(new HeavyWeaponsBleedChance("%heavy_weapons_bleed_chance%"));
        registerPlaceholder(new HeavyWeaponsBleedDamage("%heavy_weapons_bleed_damage%"));
        registerPlaceholder(new HeavyWeaponsBleedDuration("%heavy_weapons_bleed_duration%"));
        registerPlaceholder(new HeavyWeaponsBleedOnCrit("%heavy_weapons_bleed_on_crit%"));
        registerPlaceholder(new HeavyWeaponsCoatingEnemyAmplifierMultiplier("%heavy_weapons_coating_enemy_amplifier_multiplier%"));
        registerPlaceholder(new HeavyWeaponsCoatingEnemyDurationMultiplier("%heavy_weapons_coating_enemy_duration_multiplier%"));
        registerPlaceholder(new HeavyWeaponsCoatingSelfDurationMultiplier("%heavy_weapons_self_duration_multiplier%"));
        registerPlaceholder(new HeavyWeaponsCritChance("%heavy_weapons_crit_chance%"));
        registerPlaceholder(new HeavyWeaponsCritDamageMultiplier("%heavy_weapons_crit_damage_multiplier%"));
        registerPlaceholder(new HeavyWeaponsCritOnBleed("%heavy_weapons_crit_on_bleed%"));
        registerPlaceholder(new HeavyWeaponsCritOnStealth("%heavy_weapons_crit_on_stealth%"));
        registerPlaceholder(new HeavyWeaponsCritOnStun("%heavy_weapons_crit_on_stun%"));
        registerPlaceholder(new HeavyWeaponsCrushingBlowChance("%heavy_weapons_crushing_blow_chance%"));
        registerPlaceholder(new HeavyWeaponsCrushingBlowCooldown("%heavy_weapons_crushing_blow_cooldown%"));
        registerPlaceholder(new HeavyWeaponsCrushingBlowDamageMultiplier("%heavy_weapons_crushing_blow_damage_multiplier%"));
        registerPlaceholder(new HeavyWeaponsCrushingBlowOnCrit("%heavy_weapons_crushing_blow_on_crit%"));
        registerPlaceholder(new HeavyWeaponsCrushingBlowOnFalling("%heavy_weapons_crushing_blow_on_falling%"));
        registerPlaceholder(new HeavyWeaponsCrushingBlowRadius("%heavy_weapons_crushing_blow_radius%"));
        registerPlaceholder(new HeavyWeaponsDamageMultiplier("%heavy_weapons_damage_multiplier%"));
        registerPlaceholder(new HeavyWeaponsDropBonus("%heavy_weapons_drops_bonus%"));
        registerPlaceholder(new HeavyWeaponsFlatArmorIgnored("%heavy_weapons_flat_armor_penetration%"));
        registerPlaceholder(new HeavyWeaponsFlatHeavyArmorIgnored("%heavy_weapons_flat_heavy_armor_penetration%"));
        registerPlaceholder(new HeavyWeaponsFlatLightArmorIgnored("%heavy_weapons_flat_light_armor_penetration%"));
        registerPlaceholder(new HeavyWeaponsFractionArmorIgnored("%heavy_weapons_fraction_armor_penetration%"));
        registerPlaceholder(new HeavyWeaponsFractionHeavyArmorIgnored("%heavy_weapons_fraction_heavy_armor_penetration%"));
        registerPlaceholder(new HeavyWeaponsFractionLightArmorIgnored("%heavy_weapons_fraction_light_armor_penetration%"));
        registerPlaceholder(new HeavyWeaponsHeavyArmorDamageBonus("%heavy_weapons_heavy_armor_damage_bonus%"));
        registerPlaceholder(new HeavyWeaponsImmunityFrameReduction("%heavy_weapons_immunity_frame_reduction%"));
        registerPlaceholder(new HeavyWeaponsKnockbackBonus("%heavy_weapons_knockback_bonus%"));
        registerPlaceholder(new HeavyWeaponsLightArmorDamageBonus("%heavy_weapons_light_armor_damage_bonus%"));
        registerPlaceholder(new HeavyWeaponsParryCooldown("%heavy_weapons_parry_cooldown%"));
        registerPlaceholder(new HeavyWeaponsParryDamageReduction("%heavy_weapons_parry_damage_reduction%"));
        registerPlaceholder(new HeavyWeaponsParryEnemyDebuffDuration("%heavy_weapons_parry_enemy_debuff_duration%"));
        registerPlaceholder(new HeavyWeaponsParryFailedDebuffDuration("%heavy_weapons_parry_failed_debuff_duration%"));
        registerPlaceholder(new HeavyWeaponsParryTimeFrame("%heavy_weapons_parry_time_frame%"));
        registerPlaceholder(new HeavyWeaponsParryVulnerableTimeFrame("%heavy_weapons_parry_vulnerable_frame%"));
        registerPlaceholder(new HeavyWeaponsRareDropMultiplier("%heavy_weapons_rare_drops_bonus%"));
        registerPlaceholder(new HeavyWeaponsStunChance("%heavy_weapons_stun_chance%"));
        registerPlaceholder(new HeavyWeaponsStunDuration("%heavy_weapons_stun_duration%"));
        registerPlaceholder(new HeavyWeaponsUnlockedWeaponCoating("%heavy_weapons_unlocked_weapon_coating%"));

        // ACCOUNT
        registerPlaceholder(new Skillpoints("%skillpoints%"));

        registerPlaceholder(new ArmorBonus("%player_armor_bonus%"));
        registerPlaceholder(new AttackDamageBonus("%player_attack_damage_bonus%"));
        registerPlaceholder(new AttackSpeedBonus("%player_attack_speed_bonus%"));
        registerPlaceholder(new CooldownReduction("%player_ability_cooldown_reduction%"));
        registerPlaceholder(new DamageResistance("%player_damage_resistance%"));
        registerPlaceholder(new ExplosionResistance("%player_explosion_resistance%"));
        registerPlaceholder(new FallDamageResistance("%player_fall_damage_resistance%"));
        registerPlaceholder(new FireResistance("%player_fire_resistance%"));
        registerPlaceholder(new HealthBonus("%player_health_bonus%"));
        registerPlaceholder(new HealthRegenerationBonus("%player_health_regeneration_bonus%"));
        registerPlaceholder(new HungerSaveChance("%player_hunger_save_chance%"));
        registerPlaceholder(new KnockbackResistanceBonus("%player_knockback_resistance%"));
        registerPlaceholder(new LuckBonus("%player_luck_bonus%"));
        registerPlaceholder(new MagicResistance("%player_magic_resistance%"));
        registerPlaceholder(new MeleeResistance("%player_melee_resistance%"));
        registerPlaceholder(new MovementSpeedBonus("%player_movement_speed_bonus%"));
        registerPlaceholder(new PoisonResistance("%player_poison_resistance%"));
        registerPlaceholder(new ProjectileResistance("%player_projectile_resistance%"));
        registerPlaceholder(new ToughnessBonus("%player_toughness_bonus%"));
        registerPlaceholder(new StunResistance("%player_stun_resistance%"));
        registerPlaceholder(new ImmunityFrameBonus("%player_immunity_frame_bonus%"));
    }

    public void registerPlaceholder(Placeholder p) {
        placeholders.put(p.getPlaceholder(), p);
    }

    public Map<String, Placeholder> getPlaceholders() {
        return placeholders;
    }

    public static String parse(String stringToParse, Player p) {
        for (Placeholder s : PlaceholderManager.getInstance().getPlaceholders().values()) {
            if (stringToParse.contains(s.getPlaceholder())) {
                stringToParse = s.parse(stringToParse, p);
            }
        }
        return stringToParse;
    }
}
