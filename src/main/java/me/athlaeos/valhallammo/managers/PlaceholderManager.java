package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.items.EnchantmentType;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.placeholder.placeholders.alchemy.*;
import me.athlaeos.valhallammo.placeholder.placeholders.enchanting.*;
import me.athlaeos.valhallammo.placeholder.placeholders.farming.*;
import me.athlaeos.valhallammo.placeholder.placeholders.landscaping.*;
import me.athlaeos.valhallammo.placeholder.placeholders.mining.*;
import me.athlaeos.valhallammo.placeholder.placeholders.smithing.SmithingEXPMultipliers;
import me.athlaeos.valhallammo.placeholder.placeholders.smithing.SmithingSkill;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.placeholder.Placeholder;
import me.athlaeos.valhallammo.placeholder.placeholders.*;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;

public class PlaceholderManager {
    private static PlaceholderManager manager = null;
    private final Map<String, Placeholder> placeholders = new HashMap<>();

    public static PlaceholderManager getInstance(){
        if (manager == null) manager = new PlaceholderManager();
        return manager;
    }

    public PlaceholderManager(){
        // GENERAL/EVERYTHING
        registerPlaceholder(new SkillEXPMultiplier("%exp_gain%"));

        for (String skill : SkillProgressionManager.getInstance().getAllSkills().keySet()){
            String name = skill.toLowerCase();
            registerPlaceholder(new EXPCurrent("%" + name + "_exp_current%", skill));
            registerPlaceholder(new EXPNextLevel("%" + name + "_exp_next%", skill));
            registerPlaceholder(new EXPPercentProgress("%" + name + "_exp_percent%", skill));
            registerPlaceholder(new EXPTotal("%" + name + "_exp_total%", skill));
            registerPlaceholder(new LevelCurrent("%" + name + "_level%", skill));
            registerPlaceholder(new LevelNext("%" + name + "_level_next%", skill));
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

        // ACCOUNT
        registerPlaceholder(new Skillpoints("%skillpoints%"));
    }

    public void registerPlaceholder(Placeholder p){
        placeholders.put(p.getPlaceholder(), p);
    }

    public Map<String, Placeholder> getPlaceholders() {
        return placeholders;
    }

    public static String parse(String stringToParse, Player p){
        for (Placeholder s : PlaceholderManager.getInstance().getPlaceholders().values()){
            if (stringToParse.contains(s.getPlaceholder())){
                stringToParse = s.parse(stringToParse, p);
            }
        }
        return stringToParse;
    }
}
