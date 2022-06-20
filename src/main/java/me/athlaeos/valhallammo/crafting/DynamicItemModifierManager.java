package me.athlaeos.valhallammo.crafting;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.enchantment_conditionals.AddCustomEnchantCounterModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.enchantment_conditionals.EnchantCounterCancelIfExceededModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.enchantment_stats.*;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.experience.ExpLevelCostModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.experience.ExpPointsCostModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.experience.SkillEXPModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_conditionals.*;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats.*;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_conditionals.*;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_stats.*;
import me.athlaeos.valhallammo.items.ItemTreatment;
import me.athlaeos.valhallammo.items.PotionTreatment;
import me.athlaeos.valhallammo.items.PotionType;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class DynamicItemModifierManager {
    private static DynamicItemModifierManager manager = null;

    private final Map<String, DynamicItemModifier> modifiers = new HashMap<>();

    public DynamicItemModifierManager(){
        register(new SkillLevelRequirementAddModifier("level_requirement_smithing", "SMITHING", Material.ANVIL));
        register(new SkillLevelRequirementAddModifier("level_requirement_alchemy", "ALCHEMY", Material.BREWING_STAND));
        register(new SkillLevelRequirementAddModifier("level_requirement_enchanting", "ENCHANTING", Material.ENCHANTING_TABLE));
        register(new SkillLevelRequirementAddModifier("level_requirement_farming", "FARMING", Material.IRON_HOE));
        register(new SkillLevelRequirementAddModifier("level_requirement_mining", "MINING", Material.IRON_PICKAXE));
        register(new SkillLevelRequirementAddModifier("level_requirement_landscaping", "LANDSCAPING", Material.IRON_SHOVEL));
        register(new SkillLevelRequirementAddModifier("level_requirement_light_weapons", "LIGHT_WEAPONS", Material.IRON_SWORD));
        register(new SkillLevelRequirementAddModifier("level_requirement_heavy_weapons", "HEAVY_WEAPONS", Material.IRON_AXE));
        register(new SkillLevelRequirementAddModifier("level_requirement_light_armor", "LIGHT_ARMOR", Material.LEATHER_CHESTPLATE));
        register(new SkillLevelRequirementAddModifier("level_requirement_heavy_armor", "HEAVY_ARMOR", Material.IRON_CHESTPLATE));
        register(new SkillLevelRequirementAddModifier("level_requirement_archery", "ARCHERY", Material.BOW));
        register(new SkillLevelRequirementAddModifier("level_requirement_account", "ACCOUNT", Material.ARMOR_STAND));

        // SMITHING
        register(new DynamicDurabilityModifier("dynamic_durability", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicBowStrengthModifier("dynamic_bow_strength", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicDamageModifier("dynamic_damage", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicAttackSpeedModifier("dynamic_attack_speed", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicArmorModifier("dynamic_armor", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicArmorToughnessModifier("dynamic_armor_toughness", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicKnockbackResistanceModifier("dynamic_knockback_resistance", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicMovementSpeedModifier("dynamic_movement_speed", 0D, ModifierPriority.NEUTRAL));
//        register(new DynamicKnockbackModifier("dynamic_knockback", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicHealthBoostModifier("dynamic_health_boost", 0D, ModifierPriority.NEUTRAL));

        register(new AddTreatmentModifier("treatment_add_tempering", 0D, ModifierPriority.NEUTRAL, ItemTreatment.TEMPERING, Material.IRON_INGOT));
        register(new AddTreatmentModifier("treatment_add_quenching", 0D, ModifierPriority.NEUTRAL, ItemTreatment.QUENCHING, Material.NETHERITE_INGOT));
        register(new AddTreatmentModifier("treatment_add_sharpening_rough", 0D, ModifierPriority.NEUTRAL, ItemTreatment.SHARPENING_ROUGH, Material.GRINDSTONE));
        register(new AddTreatmentModifier("treatment_add_sharpening_fine", 0D, ModifierPriority.NEUTRAL, ItemTreatment.SHARPENING_FINE, Material.POLISHED_ANDESITE_SLAB));
        register(new AddTreatmentModifier("treatment_add_leather_binding", 0D, ModifierPriority.NEUTRAL, ItemTreatment.LEATHER_BINDING, Material.LEATHER));
        register(new AddTreatmentModifier("treatment_add_wax_coating", 0D, ModifierPriority.NEUTRAL, ItemTreatment.WAX_COATING, Material.HONEYCOMB));
        register(new AddTreatmentModifier("treatment_add_armor_fitting", 0D, ModifierPriority.NEUTRAL, ItemTreatment.ARMOR_FITTING, Material.NETHERITE_SCRAP));
        register(new AddTreatmentModifier("treatment_add_polishing", 0D, ModifierPriority.NEUTRAL, ItemTreatment.POLISHING, Material.CLAY_BALL));
        register(new AddTreatmentModifier("treatment_add_engraving", 0D, ModifierPriority.NEUTRAL, ItemTreatment.ENGRAVING, Material.QUARTZ));
        register(new AddTreatmentModifier("treatment_add_heating", 0D, ModifierPriority.NEUTRAL, ItemTreatment.HEATING, Material.CAMPFIRE));
        register(new AddTreatmentModifier("treatment_add_super_heating", 0D, ModifierPriority.NEUTRAL, ItemTreatment.SUPERHEATING, Material.SOUL_CAMPFIRE));
        register(new AddTreatmentModifier("treatment_add_studding", 0D, ModifierPriority.NEUTRAL, ItemTreatment.STUDDING, Material.IRON_NUGGET));
        register(new AddTreatmentModifier("treatment_add_generic_improvement", 0D, ModifierPriority.NEUTRAL, ItemTreatment.GENERIC_IMPROVEMENT, Material.GOLD_INGOT));
        register(new AddTreatmentModifier("treatment_add_unenchantable", 0D, ModifierPriority.NEUTRAL, ItemTreatment.UNENCHANTABLE, Material.BOOK));

        register(new RemoveTreatmentModifier("treatment_remove_tempering", 0D, ModifierPriority.NEUTRAL, ItemTreatment.TEMPERING, Material.IRON_INGOT));
        register(new RemoveTreatmentModifier("treatment_remove_quenching", 0D, ModifierPriority.NEUTRAL, ItemTreatment.QUENCHING, Material.NETHERITE_INGOT));
        register(new RemoveTreatmentModifier("treatment_remove_sharpening_rough", 0D, ModifierPriority.NEUTRAL, ItemTreatment.SHARPENING_ROUGH, Material.GRINDSTONE));
        register(new RemoveTreatmentModifier("treatment_remove_sharpening_fine", 0D, ModifierPriority.NEUTRAL, ItemTreatment.SHARPENING_FINE, Material.POLISHED_ANDESITE_SLAB));
        register(new RemoveTreatmentModifier("treatment_remove_leather_binding", 0D, ModifierPriority.NEUTRAL, ItemTreatment.LEATHER_BINDING, Material.LEATHER));
        register(new RemoveTreatmentModifier("treatment_remove_wax_coating", 0D, ModifierPriority.NEUTRAL, ItemTreatment.WAX_COATING, Material.HONEYCOMB));
        register(new RemoveTreatmentModifier("treatment_remove_armor_fitting", 0D, ModifierPriority.NEUTRAL, ItemTreatment.ARMOR_FITTING, Material.NETHERITE_SCRAP));
        register(new RemoveTreatmentModifier("treatment_remove_polishing", 0D, ModifierPriority.NEUTRAL, ItemTreatment.POLISHING, Material.CLAY_BALL));
        register(new RemoveTreatmentModifier("treatment_remove_engraving", 0D, ModifierPriority.NEUTRAL, ItemTreatment.ENGRAVING, Material.QUARTZ));
        register(new RemoveTreatmentModifier("treatment_remove_heating", 0D, ModifierPriority.NEUTRAL, ItemTreatment.HEATING, Material.CAMPFIRE));
        register(new RemoveTreatmentModifier("treatment_remove_super_heating", 0D, ModifierPriority.NEUTRAL, ItemTreatment.SUPERHEATING, Material.SOUL_CAMPFIRE));
        register(new RemoveTreatmentModifier("treatment_remove_studding", 0D, ModifierPriority.NEUTRAL, ItemTreatment.STUDDING, Material.IRON_NUGGET));
        register(new RemoveTreatmentModifier("treatment_remove_generic_improvement", 0D, ModifierPriority.NEUTRAL, ItemTreatment.GENERIC_IMPROVEMENT, Material.GOLD_INGOT));
        register(new RemoveTreatmentModifier("treatment_remove_unenchantable", 0D, ModifierPriority.NEUTRAL, ItemTreatment.UNENCHANTABLE, Material.BOOK));

        register(new RemoveAllTreatmentsModifier("treatment_remove_all", 0D, ModifierPriority.NEUTRAL));

        register(new RequireTreatmentModifier("treatment_requirement_tempering", 0D, ModifierPriority.NEUTRAL, ItemTreatment.TEMPERING, Material.IRON_INGOT));
        register(new RequireTreatmentModifier("treatment_requirement_quenching", 0D, ModifierPriority.NEUTRAL, ItemTreatment.QUENCHING, Material.NETHERITE_INGOT));
        register(new RequireTreatmentModifier("treatment_requirement_sharpening_rough", 0D, ModifierPriority.NEUTRAL, ItemTreatment.SHARPENING_ROUGH, Material.GRINDSTONE));
        register(new RequireTreatmentModifier("treatment_requirement_sharpening_fine", 0D, ModifierPriority.NEUTRAL, ItemTreatment.SHARPENING_FINE, Material.POLISHED_ANDESITE_SLAB));
        register(new RequireTreatmentModifier("treatment_requirement_leather_binding", 0D, ModifierPriority.NEUTRAL, ItemTreatment.LEATHER_BINDING, Material.LEATHER));
        register(new RequireTreatmentModifier("treatment_requirement_wax_coating", 0D, ModifierPriority.NEUTRAL, ItemTreatment.WAX_COATING, Material.HONEYCOMB));
        register(new RequireTreatmentModifier("treatment_requirement_armor_fitting", 0D, ModifierPriority.NEUTRAL, ItemTreatment.ARMOR_FITTING, Material.NETHERITE_SCRAP));
        register(new RequireTreatmentModifier("treatment_requirement_polishing", 0D, ModifierPriority.NEUTRAL, ItemTreatment.POLISHING, Material.CLAY_BALL));
        register(new RequireTreatmentModifier("treatment_requirement_engraving", 0D, ModifierPriority.NEUTRAL, ItemTreatment.ENGRAVING, Material.QUARTZ));
        register(new RequireTreatmentModifier("treatment_requirement_heating", 0D, ModifierPriority.NEUTRAL, ItemTreatment.HEATING, Material.CAMPFIRE));
        register(new RequireTreatmentModifier("treatment_requirement_super_heating", 0D, ModifierPriority.NEUTRAL, ItemTreatment.SUPERHEATING, Material.SOUL_CAMPFIRE));
        register(new RequireTreatmentModifier("treatment_requirement_studding", 0D, ModifierPriority.NEUTRAL, ItemTreatment.STUDDING, Material.IRON_NUGGET));
        register(new RequireTreatmentModifier("treatment_requirement_generic_improvement", 0D, ModifierPriority.NEUTRAL, ItemTreatment.GENERIC_IMPROVEMENT, Material.GOLD_INGOT));
        register(new RequireTreatmentModifier("treatment_requirement_unenchantable", 0D, ModifierPriority.NEUTRAL, ItemTreatment.UNENCHANTABLE, Material.BOOK));

        register(new CancelIfTreatmentModifier("treatment_cancel_if_tempering", 0D, ModifierPriority.NEUTRAL, ItemTreatment.TEMPERING, Material.IRON_INGOT));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_quenching", 0D, ModifierPriority.NEUTRAL, ItemTreatment.QUENCHING, Material.NETHERITE_INGOT));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_sharpening_rough", 0D, ModifierPriority.NEUTRAL, ItemTreatment.SHARPENING_ROUGH, Material.GRINDSTONE));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_sharpening_fine", 0D, ModifierPriority.NEUTRAL, ItemTreatment.SHARPENING_FINE, Material.POLISHED_ANDESITE_SLAB));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_leather_binding", 0D, ModifierPriority.NEUTRAL, ItemTreatment.LEATHER_BINDING, Material.LEATHER));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_wax_coating", 0D, ModifierPriority.NEUTRAL, ItemTreatment.WAX_COATING, Material.HONEYCOMB));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_armor_fitting", 0D, ModifierPriority.NEUTRAL, ItemTreatment.ARMOR_FITTING, Material.NETHERITE_SCRAP));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_polishing", 0D, ModifierPriority.NEUTRAL, ItemTreatment.POLISHING, Material.CLAY_BALL));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_engraving", 0D, ModifierPriority.NEUTRAL, ItemTreatment.ENGRAVING, Material.QUARTZ));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_heating", 0D, ModifierPriority.NEUTRAL, ItemTreatment.HEATING, Material.CAMPFIRE));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_super_heating", 0D, ModifierPriority.NEUTRAL, ItemTreatment.SUPERHEATING, Material.SOUL_CAMPFIRE));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_studding", 0D, ModifierPriority.NEUTRAL, ItemTreatment.STUDDING, Material.IRON_NUGGET));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_generic_improvement", 0D, ModifierPriority.NEUTRAL, ItemTreatment.GENERIC_IMPROVEMENT, Material.GOLD_INGOT));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_unenchantable", 0D, ModifierPriority.NEUTRAL, ItemTreatment.UNENCHANTABLE, Material.BOOK));

        register(new UpgradeNetheriteModifier("upgrade_equipment_netherite", 0D, ModifierPriority.NEUTRAL));
        register(new UpgradeDiamondModifier("upgrade_equipment_diamond", 0D, ModifierPriority.NEUTRAL));
        register(new UpgradeIronModifier("upgrade_equipment_iron", 0D, ModifierPriority.NEUTRAL));
        register(new UpgradeGoldModifier("upgrade_equipment_gold", 0D, ModifierPriority.NEUTRAL));
        register(new UpgradeStoneChainModifier("upgrade_equipment_stone_chain", 0D, ModifierPriority.NEUTRAL));
        register(new UpgradeWoodLeatherModifier("upgrade_equipment_wood_leather", 0D, ModifierPriority.NEUTRAL));

        register(new ArmorHeavyModifier("armor_weight_class_heavy", 0D, ModifierPriority.NEUTRAL));
        register(new ArmorLightModifier("armor_weight_class_light", 0D, ModifierPriority.NEUTRAL));
        register(new ArmorWeightlessModifier("armor_weight_class_weightless", 0D, ModifierPriority.NEUTRAL));
        register(new WeaponHeavyModifier("weapon_weight_class_heavy", 0D, ModifierPriority.NEUTRAL));
        register(new WeaponLightModifier("weapon_weight_class_light", 0D, ModifierPriority.NEUTRAL));
        register(new WeaponWeightlessModifier("weapon_weight_class_weightless", 0D, ModifierPriority.NEUTRAL));

        register(new SkillEXPModifier("exp_bonus_smithing", 0D, ModifierPriority.NEUTRAL, "SMITHING"));
        register(new SkillEXPModifier("exp_bonus_alchemy", 0D, ModifierPriority.NEUTRAL, "ALCHEMY"));
        register(new SkillEXPModifier("exp_bonus_enchanting", 0D, ModifierPriority.NEUTRAL, "ENCHANTING"));
        register(new SkillEXPModifier("exp_bonus_farming", 0D, ModifierPriority.NEUTRAL, "FARMING"));

        register(new ExpLevelCostModifier("exp_cost_level", 0D, ModifierPriority.NEUTRAL));
        register(new ExpPointsCostModifier("exp_cost_points", 0D, ModifierPriority.NEUTRAL));

        register(new AttributeAddArmorKBResistModifier("default_attribute_knockback_resist_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddArmorModifier("default_attribute_armor_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddArmorToughnessModifier("default_attribute_armor_toughness_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddAttackDamageModifier("default_attribute_attack_damage_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddAttackSpeedModifier("default_attribute_attack_speed_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddMovementSpeedModifier("default_attribute_movement_speed_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddHealthModifier("default_attribute_max_health_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddDamageResistanceModifier("default_attribute_damage_resistance_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddFireResistanceModifier("default_attribute_fire_resistance_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddExplosionResistanceModifier("default_attribute_explosion_resistance_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddMagicResistanceModifier("default_attribute_magic_resistance_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddPoisonResistanceModifier("default_attribute_poison_resistance_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddProjectileResistanceModifier("default_attribute_projectile_resistance_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddMeleeResistanceModifier("default_attribute_melee_resistance_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddDrawStrengthModifier("draw_strength_static", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddCustomDurabilityModifier("durability_static", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddArrowStrengthModifier("arrow_damage", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddArrowAccuracyModifier("arrow_accuracy", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddArrowSaveChanceModifier("arrow_save_chance", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddArrowSpeedModifier("arrow_speed", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddArrowPiercingModifier("arrow_piercing", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddArrowInfinityExploitableModifier("arrow_infinity_compatible", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddModifier("custom_knockback", "CUSTOM_KNOCKBACK", Material.PISTON, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
        register(new AttributeAddModifier("custom_stun_chance", "CUSTOM_STUN_CHANCE", Material.ANVIL, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
        register(new AttributeAddModifier("custom_bleed_chance", "CUSTOM_BLEED_CHANCE", Material.BEETROOT, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
        register(new AttributeAddModifier("custom_bleed_damage", "CUSTOM_BLEED_DAMAGE", Material.BEETROOT_SOUP, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, 0.1));
        register(new AttributeAddModifier("custom_bleed_duration", "CUSTOM_BLEED_DURATION", Material.BEETROOT_SOUP, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 10000, 1000));
        register(new AttributeAddModifier("custom_crit_chance", "CUSTOM_CRIT_CHANCE", Material.IRON_SWORD, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
        register(new AttributeAddModifier("custom_crit_damage", "CUSTOM_CRIT_DAMAGE", Material.GOLDEN_SWORD, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.25, 0.01));
        register(new AttributeAddModifier("custom_weapon_reach", "CUSTOM_WEAPON_REACH", Material.ENDER_PEARL, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 1, 0.1));
        register(new AttributeAddModifier("custom_flat_armor_piercing", "CUSTOM_FLAT_ARMOR_PENETRATION", Material.ARROW, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 5, 1));
        register(new AttributeAddModifier("custom_flat_heavy_armor_piercing", "CUSTOM_FLAT_HEAVY_ARMOR_PENETRATION", Material.IRON_CHESTPLATE, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 5, 1));
        register(new AttributeAddModifier("custom_flat_light_armor_piercing", "CUSTOM_FLAT_LIGHT_ARMOR_PENETRATION", Material.CHAINMAIL_CHESTPLATE, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 5, 1));
        register(new AttributeAddModifier("custom_flat_immunity_frame_bonus", "CUSTOM_FLAT_IMMUNITY_FRAME_BONUS", Material.NETHER_STAR, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 5, 1));
        register(new AttributeAddModifier("custom_fraction_armor_piercing", "CUSTOM_FRACTION_ARMOR_PENETRATION", Material.SPECTRAL_ARROW, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
        register(new AttributeAddModifier("custom_fraction_light_armor_piercing", "CUSTOM_FRACTION_LIGHT_ARMOR_PENETRATION", Material.DIAMOND_CHESTPLATE, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
        register(new AttributeAddModifier("custom_fraction_heavy_armor_piercing", "CUSTOM_FRACTION_HEAVY_ARMOR_PENETRATION", Material.NETHERITE_CHESTPLATE, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
        register(new AttributeAddModifier("custom_heavy_armor_damage", "CUSTOM_HEAVY_ARMOR_DAMAGE", Material.NETHERITE_CHESTPLATE, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
        register(new AttributeAddModifier("custom_light_armor_damage", "CUSTOM_LIGHT_ARMOR_DAMAGE", Material.LEATHER_CHESTPLATE, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
        register(new AttributeAddModifier("custom_fraction_immunity_frame_bonus", "CUSTOM_FRACTION_IMMUNITY_FRAME_BONUS", Material.DRAGON_EGG, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
        register(new AttributeAddModifier("custom_immunity_frame_reduction", "CUSTOM_IMMUNITY_FRAME_REDUCTION", Material.TOTEM_OF_UNDYING, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
        register(new AttributeAddModifier("custom_melee_damage", "CUSTOM_MELEE_DAMAGE", Material.STONE_AXE, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
        register(new AttributeAddModifier("custom_velocity_damage_bonus", "CUSTOM_VELOCITY_DAMAGE_BONUS", Material.TRIDENT, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));


        register(new ArrowExplosiveUpgradeModifier("arrow_explosive_upgrade", 0D, 0D, 0D, ModifierPriority.NEUTRAL));
        register(new ArrowIncendiaryUpgradeModifier("arrow_incendiary_upgrade", 0D, 0D, 0D, ModifierPriority.NEUTRAL));
        register(new ArrowEnderUpgradeModifier("arrow_ender_upgrade", 0D, ModifierPriority.NEUTRAL));
        register(new ArrowRemoveIFramesUpgradeModifier("arrow_no_iframes_upgrade", 0D, ModifierPriority.NEUTRAL));
        register(new ArrowLightningUpgradeModifier("arrow_lightning_upgrade", 0D, ModifierPriority.NEUTRAL));
        register(new ArrowSFireballUpgradeModifier("arrow_small_fireball_upgrade", 0D, 0D, ModifierPriority.NEUTRAL));
        register(new ArrowLFireballUpgradeModifier("arrow_large_fireball_upgrade", 0D, 0D, ModifierPriority.NEUTRAL));
        register(new ArrowDFireballUpgradeModifier("arrow_dragon_fireball_upgrade", 0D, 0D, ModifierPriority.NEUTRAL));
        register(new ArrowRemoveGravityUpgradeModifier("arrow_remove_gravity_upgrade", 0D, ModifierPriority.NEUTRAL));

        register(new AttributeRemoveArmorKBResistModifier("default_attribute_knockback_resist_remove", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeRemoveArmorModifier("default_attribute_armor_remove", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeRemoveArmorToughnessModifier("default_attribute_armor_toughness_remove", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeRemoveAttackDamageModifier("default_attribute_attack_damage_remove", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeRemoveAttackSpeedModifier("default_attribute_attack_speed_remove", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeRemoveMovementSpeedModifier("default_attribute_movement_speed_remove", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeRemoveHealthModifier("default_attribute_max_health_remove", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeRemoveKnockbackModifier("default_attribute_knockback_remove", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeRemoveDamageResistanceModifier("default_attribute_damage_resistance_remove", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeRemoveFireResistanceModifier("default_attribute_fire_resistance_remove", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeRemoveExplosionResistanceModifier("default_attribute_explosion_resistance_remove", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeRemoveMagicResistanceModifier("default_attribute_magic_resistance_remove", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeRemovePoisonResistanceModifier("default_attribute_poison_resistance_remove", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeRemoveProjectileResistanceModifier("default_attribute_projectile_resistance_remove", 0D, ModifierPriority.NEUTRAL));


        register(new SetAmountModifier("set_amount", 0D, ModifierPriority.NEUTRAL));
        register(new RandomizedAmountModifier("random_amount", 0D, 0D, ModifierPriority.NEUTRAL));
        register(new DynamicAmountModifier("dynamic_amount", 0D, ModifierPriority.NEUTRAL));

        register(new SetToolIdModifier("tool_id", 0D, ModifierPriority.NEUTRAL));
        register(new WeaponIdAddModifier("weapon_id", 0D, ModifierPriority.NEUTRAL));
        register(new WeaponIdRequirementModifier("require_weapon_id", 0D, ModifierPriority.NEUTRAL));

        register(new StaticRepairModifier("repair_static", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicRepairModifier("repair_dynamic", 0D, ModifierPriority.NEUTRAL));

        register(new DynamicQualityRatingModifier("dynamic_quality", 0D, ModifierPriority.NEUTRAL));
        register(new StaticQualityRatingModifier("quality_static", 0D, ModifierPriority.NEUTRAL));
        register(new RandomizedQualityRatingModifier("random_quality", 0D, 0D, ModifierPriority.NEUTRAL));
        register(new AddQualityRatingModifier("add_quality", 0D, ModifierPriority.NEUTRAL));
        register(new MultiplyQualityRatingModifier("multiply_quality", 0D, ModifierPriority.NEUTRAL));

        register(new RandomizedDurabilityModifier("random_durability", 0D, ModifierPriority.NEUTRAL));

        register(new CustomModelDataAddModifier("custom_model_data_add", 0D, ModifierPriority.NEUTRAL));
        register(new CustomModelDataRequirementModifier("custom_model_data_require", 0D, ModifierPriority.NEUTRAL));

        register(new FlagAddHideAttributeModifier("flag_add_hide_attributes", 0D, ModifierPriority.NEUTRAL));
        register(new FlagAddHidePotionEffectsModifier("flag_add_hide_potion_effects", 0D, ModifierPriority.NEUTRAL));
        register(new FlagAddHideUnbreakableModifier("flag_add_hide_unbreakable", 0D, ModifierPriority.NEUTRAL));
        register(new FlagRemoveHideAttributeModifier("flag_remove_hide_attributes", 0D, ModifierPriority.NEUTRAL));
        register(new FlagRemoveHidePotionEffectsModifier("flag_remove_hide_potion_effects", 0D, ModifierPriority.NEUTRAL));
        register(new FlagRemoveHideUnbreakableModifier("flag_remove_hide_unbreakable", 0D, ModifierPriority.NEUTRAL));

        register(new UnbreakableSetModifier("unbreakable_set", 0D, ModifierPriority.NEUTRAL));

        // ALCHEMY
        register(new DynamicPotionAmplifierModifier("potions_dynamic_amplifier", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicPotionDurationModifier("potions_dynamic_duration", 0D, ModifierPriority.NEUTRAL));
        register(new PotionColorModifier("potions_color", 0D, 0D, 0D, ModifierPriority.NEUTRAL));
        register(new PotionInvertEffectsModifier("potions_invert", 0D, ModifierPriority.NEUTRAL));

        register(new PotionEffectAddVanillaModifier("potions_effect_add_instant_health", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.HEAL, Material.GLISTERING_MELON_SLICE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_absorption", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.ABSORPTION, Material.GOLDEN_APPLE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_bad_omen", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.BAD_OMEN, Material.IRON_AXE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_blindness", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.BLINDNESS, Material.BLACK_DYE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_conduit_power", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.CONDUIT_POWER, Material.NAUTILUS_SHELL));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_nausea", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.CONFUSION, Material.ANVIL));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_resistance", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.DAMAGE_RESISTANCE, Material.IRON_BLOCK));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_haste", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.FAST_DIGGING, Material.GOLDEN_PICKAXE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_fire_resistance", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.FIRE_RESISTANCE, Material.MAGMA_CREAM));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_glowing", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.GLOWING, Material.GLOWSTONE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_instant_damage", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.HARM, Material.IRON_SWORD));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_health_boost", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.HEALTH_BOOST, Material.APPLE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_hero_of_the_village", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.HERO_OF_THE_VILLAGE, Material.EMERALD));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_hunger", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.HUNGER, Material.ROTTEN_FLESH));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_strength", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.INCREASE_DAMAGE, Material.BLAZE_POWDER));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_invisibility", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.INVISIBILITY, Material.GLASS));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_jump_boost", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.JUMP, Material.RABBIT_FOOT));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_levitation", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.LEVITATION, Material.SHULKER_SHELL));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_luck", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.LUCK, Material.GOLD_NUGGET));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_night_vision", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.NIGHT_VISION, Material.GOLDEN_CARROT));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_poison", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.POISON, Material.SPIDER_EYE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_regeneration", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.REGENERATION, Material.GHAST_TEAR));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_saturation", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.SATURATION, Material.MUSHROOM_STEW));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_slow", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.SLOW, Material.SNOWBALL));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_fatigue", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.SLOW_DIGGING, Material.WOODEN_PICKAXE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_slow_falling", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.SLOW_FALLING, Material.PHANTOM_MEMBRANE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_speed", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.SPEED, Material.SUGAR));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_bad_luck", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.UNLUCK, Material.IRON_NUGGET));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_water_breathing", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.WATER_BREATHING, Material.PUFFERFISH));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_weakness", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.WEAKNESS, Material.WOODEN_SWORD));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_wither", 0D, 0D, ModifierPriority.NEUTRAL, PotionEffectType.WITHER, Material.WITHER_SKELETON_SKULL));

        register(new PotionTransmutationAssignmentModifier("potion_transmutations_assign", 0D, ModifierPriority.NEUTRAL));

        register(new AddEnchantmentGlowModifier("enchantment_glimmer_add", 0D, ModifierPriority.NEUTRAL));
        register(new RemoveAllEnchantmentsModifier("enchantment_remove_all"));

        register(new PotionEffectAddCustomModifier("potions_effect_add_masterpiece_smithing", 0D, 0D, ModifierPriority.NEUTRAL, "MASTERPIECE_SMITHING", Material.DAMAGED_ANVIL));
        register(new PotionEffectAddCustomModifier("potions_effect_add_fortify_enchanting", 0D, 0D, ModifierPriority.NEUTRAL, "FORTIFY_ENCHANTING", Material.EXPERIENCE_BOTTLE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_fortify_smithing", 0D, 0D, ModifierPriority.NEUTRAL, "FORTIFY_SMITHING", Material.ANVIL));
        register(new PotionEffectAddCustomModifier("potions_effect_add_farming_extra_drops", 0D, 0D, ModifierPriority.NEUTRAL, "FARMING_EXTRA_DROPS", Material.WHEAT));
        register(new PotionEffectAddCustomModifier("potions_effect_add_farming_rare_drops", 0D, 0D, ModifierPriority.NEUTRAL, "FARMING_RARE_DROPS", Material.BEETROOT));
        register(new PotionEffectAddCustomModifier("potions_effect_add_farming_fishing_tier", 0D, 0D, ModifierPriority.NEUTRAL, "FARMING_FISHING_TIER", Material.FISHING_ROD));
//        register(new PotionEffectAddCustomModifier("potions_effect_add_fortify_mining", 0D, 0D, ModifierPriority.NEUTRAL, "FORTIFY_MINING", Material.IRON_PICKAXE));
//        register(new PotionEffectAddCustomModifier("potions_effect_add_fortify_woodcutting", 0D, 0D, ModifierPriority.NEUTRAL, "FORTIFY_WOODCUTTING", Material.IRON_AXE));
//        register(new PotionEffectAddCustomModifier("potions_effect_add_fortify_acrobatics", 0D, 0D, ModifierPriority.NEUTRAL, "FORTIFY_ACROBATICS", Material.LEATHER_BOOTS));
//        register(new PotionEffectAddCustomModifier("potions_effect_add_fortify_trading", 0D, 0D, ModifierPriority.NEUTRAL, "FORTIFY_TRADING", Material.EMERALD));
        register(new PotionEffectAddCustomModifier("potions_effect_add_alchemy_brew_speed", 0D, 0D, ModifierPriority.NEUTRAL, "ALCHEMY_BREW_SPEED", Material.BLAZE_ROD));
        register(new PotionEffectAddCustomModifier("potions_effect_add_alchemy_ingredient_save", 0D, 0D, ModifierPriority.NEUTRAL, "ALCHEMY_INGREDIENT_SAVE", Material.GLASS_BOTTLE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_alchemy_potion_save", 0D, 0D, ModifierPriority.NEUTRAL, "ALCHEMY_POTION_SAVE", Material.SPLASH_POTION));
        register(new PotionEffectAddCustomModifier("potions_effect_add_alchemy_potion_velocity", 0D, 0D, ModifierPriority.NEUTRAL, "ALCHEMY_POTION_VELOCITY", Material.SPLASH_POTION));
        register(new PotionEffectAddCustomModifier("potions_effect_add_archery_accuracy", 0D, 0D, ModifierPriority.NEUTRAL, "ARCHERY_ACCURACY", Material.TARGET));
        register(new PotionEffectAddCustomModifier("potions_effect_add_archery_damage", 0D, 0D, ModifierPriority.NEUTRAL, "ARCHERY_DAMAGE", Material.BOW));
        register(new PotionEffectAddCustomModifier("potions_effect_add_archery_ammo_save", 0D, 0D, ModifierPriority.NEUTRAL, "ARCHERY_AMMO_SAVE", Material.ARROW));
//        register(new PotionEffectAddCustomModifier("potions_effect_add_unarmed_damage", 0D, 0D, ModifierPriority.NEUTRAL, "UNARMED_DAMAGE", Material.BARRIER));
//        register(new PotionEffectAddCustomModifier("potions_effect_add_weapons_damage", 0D, 0D, ModifierPriority.NEUTRAL, "WEAPONS_DAMAGE", Material.IRON_SWORD));
        register(new PotionEffectAddCustomModifier("potions_effect_add_increase_exp", 0D, 0D, ModifierPriority.NEUTRAL, "INCREASE_EXP", Material.EXPERIENCE_BOTTLE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_milk", 0D, 0D, ModifierPriority.NEUTRAL, "MILK", Material.MILK_BUCKET));
        register(new PotionEffectAddCustomModifier("potions_effect_add_armor_flat_bonus", 0D, 0D, ModifierPriority.NEUTRAL, "ARMOR_FLAT_BONUS", Material.CHAINMAIL_CHESTPLATE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_light_armor_flat_bonus", 0D, 0D, ModifierPriority.NEUTRAL, "LIGHT_ARMOR_FLAT_BONUS", Material.LEATHER_CHESTPLATE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_heavy_armor_flat_bonus", 0D, 0D, ModifierPriority.NEUTRAL, "HEAVY_ARMOR_FLAT_BONUS", Material.GOLDEN_CHESTPLATE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_armor_fraction_bonus", 0D, 0D, ModifierPriority.NEUTRAL, "ARMOR_FRACTION_BONUS", Material.IRON_CHESTPLATE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_light_armor_fraction_bonus", 0D, 0D, ModifierPriority.NEUTRAL, "LIGHT_ARMOR_FRACTION_BONUS", Material.DIAMOND_CHESTPLATE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_heavy_armor_fraction_bonus", 0D, 0D, ModifierPriority.NEUTRAL, "HEAVY_ARMOR_FRACTION_BONUS", Material.NETHERITE_CHESTPLATE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_damage_resistance", 0D, 0D, ModifierPriority.NEUTRAL, "DAMAGE_RESISTANCE", Material.ENCHANTED_GOLDEN_APPLE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_explosion_resistance", 0D, 0D, ModifierPriority.NEUTRAL, "EXPLOSION_RESISTANCE", Material.TNT));
        register(new PotionEffectAddCustomModifier("potions_effect_add_fire_resistance", 0D, 0D, ModifierPriority.NEUTRAL, "FIRE_RESISTANCE", Material.MAGMA_CREAM));
        register(new PotionEffectAddCustomModifier("potions_effect_add_magic_resistance", 0D, 0D, ModifierPriority.NEUTRAL, "MAGIC_RESISTANCE", Material.DRAGON_EGG));
        register(new PotionEffectAddCustomModifier("potions_effect_add_poison_resistance", 0D, 0D, ModifierPriority.NEUTRAL, "POISON_RESISTANCE", Material.SPIDER_EYE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_projectile_resistance", 0D, 0D, ModifierPriority.NEUTRAL, "PROJECTILE_RESISTANCE", Material.SHIELD));
        register(new PotionEffectAddCustomModifier("potions_effect_add_melee_resistance", 0D, 0D, ModifierPriority.NEUTRAL, "MELEE_RESISTANCE", Material.IRON_SWORD));
        register(new PotionEffectAddCustomModifier("potions_effect_add_falling_resistance", 0D, 0D, ModifierPriority.NEUTRAL, "FALLING_RESISTANCE", Material.FEATHER));
        register(new PotionEffectAddCustomModifier("potions_effect_add_knockback_resistance", 0D, 0D, ModifierPriority.NEUTRAL, "KNOCKBACK_RESISTANCE", Material.NETHERITE_LEGGINGS));
        register(new PotionEffectAddCustomModifier("potions_effect_add_bleed_resistance", 0D, 0D, ModifierPriority.NEUTRAL, "BLEED_RESISTANCE", Material.BEETROOT_SOUP));
        register(new PotionEffectAddCustomModifier("potions_effect_add_crafting_time_reduction", 0D, 0D, ModifierPriority.NEUTRAL, "CRAFTING_TIME_REDUCTION", Material.CRAFTING_TABLE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_hunger_save_chance", 0D, 0D, ModifierPriority.NEUTRAL, "HUNGER_SAVE_CHANCE", Material.BREAD));
        register(new PotionEffectAddCustomModifier("potions_effect_add_dodge_chance", 0D, 0D, ModifierPriority.NEUTRAL, "DODGE_CHANCE", Material.LEATHER_BOOTS));
        register(new PotionEffectAddCustomModifier("potions_effect_add_knockback_bonus", 0D, 0D, ModifierPriority.NEUTRAL, "KNOCKBACK_BONUS", Material.STONE_AXE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_cooldown_reduction", 0D, 0D, ModifierPriority.NEUTRAL, "COOLDOWN_REDUCTION", Material.CLOCK));
        register(new PotionEffectAddCustomModifier("potions_effect_add_immunity_frame_bonus", 0D, 0D, ModifierPriority.NEUTRAL, "IMMUNITY_FRAME_BONUS", Material.NETHER_STAR));
        register(new PotionEffectAddCustomModifier("potions_effect_add_immunity_frame_multiplier", 0D, 0D, ModifierPriority.NEUTRAL, "IMMUNITY_FRAME_MULTIPLIER", Material.NETHER_STAR));
        register(new PotionEffectAddCustomModifier("potions_effect_add_healing_bonus", 0D, 0D, ModifierPriority.NEUTRAL, "HEALING_BONUS", Material.GLISTERING_MELON_SLICE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_reflect_chance", 0D, 0D, ModifierPriority.NEUTRAL, "REFLECT_CHANCE", Material.CACTUS));
        register(new PotionEffectAddCustomModifier("potions_effect_add_reflect_fraction", 0D, 0D, ModifierPriority.NEUTRAL, "REFLECT_FRACTION", Material.CACTUS));
        register(new PotionEffectAddCustomModifier("potions_effect_add_poison_anti_heal", 0D, 0D, ModifierPriority.NEUTRAL, "POISON_ANTI_HEAL", Material.WITHER_SKELETON_SKULL));
        register(new PotionEffectAddCustomModifier("potions_effect_add_poison_vulnerable", 0D, 0D, ModifierPriority.NEUTRAL, "POISON_VULNERABLE", Material.SKELETON_SKULL));
        register(new PotionEffectAddCustomModifier("potions_effect_add_fraction_armor_reduction", 0D, 0D, ModifierPriority.NEUTRAL, "FRACTION_ARMOR_REDUCTION", Material.NETHERITE_AXE));
        register(new PotionEffectAddCustomModifier("potions_effect_add_flat_armor_reduction", 0D, 0D, ModifierPriority.NEUTRAL, "FLAT_ARMOR_REDUCTION", Material.DIAMOND_AXE));
        register(new MilkToChocolateMilkModifier("potions_chocolate_milk"));

        register(new PotionLingeringModifier("potions_conversion_lingering", 0D, ModifierPriority.NEUTRAL));
        register(new PotionSplashModifier("potions_conversion_splash", 0D, ModifierPriority.NEUTRAL));
        register(new PotionTippedArrowModifier("potions_conversion_tipped", 0D, ModifierPriority.NEUTRAL));
        register(new PotionPotionModifier("potions_conversion_potion", 0D, ModifierPriority.NEUTRAL));

        register(new PotionCancelIfPotionMaterialModifier("potions_cancel_if_lingering", 0D, ModifierPriority.NEUTRAL, Material.LINGERING_POTION, Material.LINGERING_POTION));
        register(new PotionCancelIfPotionMaterialModifier("potions_cancel_if_splash", 0D, ModifierPriority.NEUTRAL, Material.SPLASH_POTION, Material.SPLASH_POTION));
        register(new PotionCancelIfPotionMaterialModifier("potions_cancel_if_tipped", 0D, ModifierPriority.NEUTRAL, Material.TIPPED_ARROW, Material.TIPPED_ARROW));
        register(new PotionCancelIfPotionMaterialModifier("potions_cancel_if_potion", 0D, ModifierPriority.NEUTRAL, Material.POTION, Material.POTION));

        register(new PotionRequirePotionMaterialModifier("potions_require_lingering", 0D, ModifierPriority.NEUTRAL, Material.LINGERING_POTION, Material.LINGERING_POTION));
        register(new PotionRequirePotionMaterialModifier("potions_require_splash", 0D, ModifierPriority.NEUTRAL, Material.SPLASH_POTION, Material.SPLASH_POTION));
        register(new PotionRequirePotionMaterialModifier("potions_require_tipped", 0D, ModifierPriority.NEUTRAL, Material.TIPPED_ARROW, Material.TIPPED_ARROW));
        register(new PotionRequirePotionMaterialModifier("potions_require_potion", 0D, ModifierPriority.NEUTRAL, Material.POTION, Material.POTION));

        register(new PotionDynamicQualityRatingModifier("potions_quality_dynamic_general", 0D, ModifierPriority.NEUTRAL, null));
        register(new PotionDynamicQualityRatingModifier("potions_quality_dynamic_debuff", 0D, ModifierPriority.NEUTRAL, PotionType.DEBUFF));
        register(new PotionDynamicQualityRatingModifier("potions_quality_dynamic_buff", 0D, ModifierPriority.NEUTRAL, PotionType.BUFF));
        register(new RandomizedPotionQualityRatingModifier("potions_quality_random", 0D, 0D, ModifierPriority.NEUTRAL));
        register(new PotionStaticQualityRatingModifier("potions_quality_static", 0D, ModifierPriority.NEUTRAL));

        register(new PotionColorConditionModifier("potions_color_condition", 0D, 0D, 0D, ModifierPriority.NEUTRAL));
        register(new PotionAddTreatmentModifier("potions_treatment_add_filtered", 0D, ModifierPriority.NEUTRAL, PotionTreatment.FILTERED, Material.COBWEB));
        register(new PotionAddTreatmentModifier("potions_treatment_add_diluted", 0D, ModifierPriority.NEUTRAL, PotionTreatment.DILUTED, Material.REDSTONE));
        register(new PotionAddTreatmentModifier("potions_treatment_add_concentrated", 0D, ModifierPriority.NEUTRAL, PotionTreatment.CONCENTRATED, Material.GLOWSTONE_DUST));
        register(new PotionAddTreatmentModifier("potions_treatment_add_empowered", 0D, ModifierPriority.NEUTRAL, PotionTreatment.EMPOWERED, Material.LAPIS_LAZULI));
        register(new PotionAddTreatmentModifier("potions_treatment_add_imbued", 0D, ModifierPriority.NEUTRAL, PotionTreatment.IMBUED, Material.BREWING_STAND));
        register(new PotionAddTreatmentModifier("potions_treatment_add_enchanted", 0D, ModifierPriority.NEUTRAL, PotionTreatment.ENCHANTED, Material.ENCHANTING_TABLE));
        register(new PotionAddTreatmentModifier("potions_treatment_add_polluted", 0D, ModifierPriority.NEUTRAL, PotionTreatment.POLLUTED, Material.DIRT));
        register(new PotionAddTreatmentModifier("potions_treatment_add_transmutation", 0D, ModifierPriority.NEUTRAL, PotionTreatment.TRANSMUTATION, Material.GOLDEN_APPLE));

        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_filtered", 0D, ModifierPriority.NEUTRAL, PotionTreatment.FILTERED, Material.COBWEB));
        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_diluted", 0D, ModifierPriority.NEUTRAL, PotionTreatment.DILUTED, Material.REDSTONE));
        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_concentrated", 0D, ModifierPriority.NEUTRAL, PotionTreatment.CONCENTRATED, Material.GLOWSTONE_DUST));
        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_empowered", 0D, ModifierPriority.NEUTRAL, PotionTreatment.EMPOWERED, Material.LAPIS_LAZULI));
        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_imbued", 0D, ModifierPriority.NEUTRAL, PotionTreatment.IMBUED, Material.BREWING_STAND));
        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_enchanted", 0D, ModifierPriority.NEUTRAL, PotionTreatment.ENCHANTED, Material.ENCHANTING_TABLE));
        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_polluted", 0D, ModifierPriority.NEUTRAL, PotionTreatment.POLLUTED, Material.DIRT));
        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_transmutation", 0D, ModifierPriority.NEUTRAL, PotionTreatment.TRANSMUTATION, Material.GOLDEN_APPLE));

        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_filtered", 0D, ModifierPriority.NEUTRAL, PotionTreatment.FILTERED, Material.COBWEB));
        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_diluted", 0D, ModifierPriority.NEUTRAL, PotionTreatment.DILUTED, Material.REDSTONE));
        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_concentrated", 0D, ModifierPriority.NEUTRAL, PotionTreatment.CONCENTRATED, Material.GLOWSTONE_DUST));
        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_empowered", 0D, ModifierPriority.NEUTRAL, PotionTreatment.EMPOWERED, Material.LAPIS_LAZULI));
        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_imbued", 0D, ModifierPriority.NEUTRAL, PotionTreatment.IMBUED, Material.BREWING_STAND));
        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_enchanted", 0D, ModifierPriority.NEUTRAL, PotionTreatment.ENCHANTED, Material.ENCHANTING_TABLE));
        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_polluted", 0D, ModifierPriority.NEUTRAL, PotionTreatment.POLLUTED, Material.DIRT));
        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_transmutation", 0D, ModifierPriority.NEUTRAL, PotionTreatment.TRANSMUTATION, Material.GOLDEN_APPLE));

        register(new PotionRequireTreatmentModifier("potions_treatment_require_filtered", 0D, ModifierPriority.NEUTRAL, PotionTreatment.FILTERED, Material.COBWEB));
        register(new PotionRequireTreatmentModifier("potions_treatment_require_diluted", 0D, ModifierPriority.NEUTRAL, PotionTreatment.DILUTED, Material.REDSTONE));
        register(new PotionRequireTreatmentModifier("potions_treatment_require_concentrated", 0D, ModifierPriority.NEUTRAL, PotionTreatment.CONCENTRATED, Material.GLOWSTONE_DUST));
        register(new PotionRequireTreatmentModifier("potions_treatment_require_empowered", 0D, ModifierPriority.NEUTRAL, PotionTreatment.EMPOWERED, Material.LAPIS_LAZULI));
        register(new PotionRequireTreatmentModifier("potions_treatment_require_imbued", 0D, ModifierPriority.NEUTRAL, PotionTreatment.IMBUED, Material.BREWING_STAND));
        register(new PotionRequireTreatmentModifier("potions_treatment_require_enchanted", 0D, ModifierPriority.NEUTRAL, PotionTreatment.ENCHANTED, Material.ENCHANTING_TABLE));
        register(new PotionRequireTreatmentModifier("potions_treatment_require_polluted", 0D, ModifierPriority.NEUTRAL, PotionTreatment.POLLUTED, Material.DIRT));
        register(new PotionRequireTreatmentModifier("potions_treatment_require_transmutation", 0D, ModifierPriority.NEUTRAL, PotionTreatment.TRANSMUTATION, Material.GOLDEN_APPLE));

        register(new PotionSetPotionTypeModifier("potions_type_set_awkward", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.AWKWARD, Material.NETHER_WART));
        register(new PotionSetPotionTypeModifier("potions_type_set_thick", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.THICK, Material.GLOWSTONE_DUST));
        register(new PotionSetPotionTypeModifier("potions_type_set_mundane", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.MUNDANE, Material.REDSTONE));
        register(new PotionSetPotionTypeModifier("potions_type_set_water", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.WATER, Material.WATER_BUCKET));
        register(new PotionSetPotionTypeModifier("potions_type_set_uncraftable", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.UNCRAFTABLE, Material.BARRIER));

        register(new PotionRequirePotionTypeModifier("potions_type_require_awkward", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.AWKWARD, Material.NETHER_WART));
        register(new PotionRequirePotionTypeModifier("potions_type_require_thick", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.THICK, Material.GLOWSTONE_DUST));
        register(new PotionRequirePotionTypeModifier("potions_type_require_mundane", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.MUNDANE, Material.REDSTONE));
        register(new PotionRequirePotionTypeModifier("potions_type_require_water", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.WATER, Material.WATER_BUCKET));
        register(new PotionRequirePotionTypeModifier("potions_type_require_uncraftable", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.UNCRAFTABLE, Material.BARRIER));

        register(new PotionCancelIfPotionTypeModifier("potions_type_cancel_if_awkward", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.AWKWARD, Material.NETHER_WART));
        register(new PotionCancelIfPotionTypeModifier("potions_type_cancel_if_thick", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.THICK, Material.GLOWSTONE_DUST));
        register(new PotionCancelIfPotionTypeModifier("potions_type_cancel_if_mundane", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.MUNDANE, Material.REDSTONE));
        register(new PotionCancelIfPotionTypeModifier("potions_type_cancel_if_water", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.WATER, Material.WATER_BUCKET));
        register(new PotionCancelIfPotionTypeModifier("potions_type_cancel_if_uncraftable", 0D, ModifierPriority.NEUTRAL, org.bukkit.potion.PotionType.UNCRAFTABLE, Material.BARRIER));

        register(new PotionUpdateNameModifier("potions_update_name", 0D , ModifierPriority.NEUTRAL));

        // ENCHANTING
//        register(new CustomEnchantAddModifier("enchantment_add_fortify_acrobatics", Material.BLAZE_ROD, "ALCHEMY_BREW_SPEED", 0.1, 0.01, -1000, 1000));
//        register(new CustomEnchantAddModifier("enchantment_add_fortify_acrobatics", Material.FEATHER, "ACROBATICS", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_alchemy_brew_speed", Material.BLAZE_ROD, "ALCHEMY_BREW_SPEED", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_alchemy_ingredient_save", Material.CHORUS_FRUIT, "ALCHEMY_INGREDIENT_SAVE", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_alchemy_potion_save", Material.SPLASH_POTION, "ALCHEMY_POTION_SAVE", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_alchemy_quality", Material.BREWING_STAND, "ALCHEMY_QUALITY", 10, 1, -100000, 100000));
        register(new CustomEnchantAddModifier("enchantment_add_alchemy_throw_velocity", Material.EGG, "ALCHEMY_THROW_VELOCITY", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_archery_accuracy", Material.TARGET, "ARCHERY_ACCURACY", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_archery_ammo_save", Material.ARROW, "ARCHERY_AMMO_SAVE", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_archery_damage", Material.CROSSBOW, "ARCHERY_DAMAGE", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_damage_dealt", Material.DIAMOND_SWORD, "DAMAGE_DEALT", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_damage_taken", Material.DIAMOND_CHESTPLATE, "DAMAGE_TAKEN", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_exp_gain_skill", Material.WRITABLE_BOOK, "EXP_GAIN_SKILL", 10, 1, -100000, 100000));
        register(new CustomEnchantAddModifier("enchantment_add_exp_gain_vanilla", Material.EXPERIENCE_BOTTLE, "EXP_GAIN_VANILLA", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_farming_extra_drops", Material.DIAMOND_HOE, "FARMING_EXTRA_DROPS", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_farming_rare_drops", Material.GOLDEN_HOE, "FARMING_RARE_DROPS", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_farming_fishing_tier", Material.FISHING_ROD, "FARMING_FISHING_TIER", 0.25, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_mining_extra_drops", Material.DIAMOND_PICKAXE, "MINING_EXTRA_DROPS", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_mining_rare_drops", Material.GOLDEN_PICKAXE, "MINING_RARE_DROPS", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_woodcutting_extra_drops", Material.DIAMOND_AXE, "WOODCUTTING_EXTRA_DROPS", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_woodcutting_rare_drops", Material.GOLDEN_AXE, "WOODCUTTING_RARE_DROPS", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_digging_extra_drops", Material.DIAMOND_SHOVEL, "DIGGING_EXTRA_DROPS", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_digging_rare_drops", Material.GOLDEN_SHOVEL, "DIGGING_RARE_DROPS", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_smithing_quality", Material.ANVIL, "SMITHING_QUALITY", 10, 1, -100000, 100000));
//        register(new CustomEnchantAddModifier("enchantment_add_weapons_damage", Material.DIAMOND_SWORD, "WEAPONS_DAMAGE", 0.1, 0.01, -1000, 1000));
//        register(new CustomEnchantAddModifier("enchantment_add_unarmed_damage", Material.BLAZE_ROD, "UNARMED_DAMAGE", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_dodge_chance", Material.FEATHER, "DODGE_CHANCE", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_armor_multiplier", Material.NETHERITE_CHESTPLATE, "ARMOR_MULTIPLIER", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_bleed_resistance", Material.BEETROOT_SOUP, "BLEED_RESISTANCE", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_cooldown_reduction", Material.CLOCK, "COOLDOWN_REDUCTION", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_crafting_speed", Material.CRAFTING_TABLE, "CRAFTING_SPEED", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_healing_bonus", Material.GLISTERING_MELON_SLICE, "HEALING_BONUS", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_hunger_save_chance", Material.BREAD, "HUNGER_SAVE_CHANCE", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_explosion_power", Material.TNT, "EXPLOSION_POWER", 0.1, 0.01, -1000, 1000));
        register(new CustomEnchantAddModifier("enchantment_add_stun_resistance", Material.ANVIL, "STUN_RESISTANCE", 0.1, 0.01, -1000, 1000));

        register(new VanillaEnchantAddModifier("enchantments_add_sharpness", Material.IRON_SWORD, Enchantment.DAMAGE_ALL, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_smite", Material.NETHERITE_SWORD, Enchantment.DAMAGE_UNDEAD, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_bane_of_arthropods", Material.WOODEN_SWORD, Enchantment.DAMAGE_ARTHROPODS, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_fortune", Material.GOLDEN_PICKAXE, Enchantment.LOOT_BONUS_BLOCKS, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_looting", Material.GOLDEN_SWORD, Enchantment.LOOT_BONUS_MOBS, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_power", Material.BOW, Enchantment.ARROW_DAMAGE, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_flame", Material.BLAZE_POWDER, Enchantment.ARROW_FIRE, 1));
        register(new VanillaEnchantAddModifier("enchantments_add_infinity", Material.ARROW, Enchantment.ARROW_INFINITE, 1));
        register(new VanillaEnchantAddModifier("enchantments_add_punch", Material.PISTON, Enchantment.ARROW_KNOCKBACK, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_curse_binding", Material.STRING, Enchantment.BINDING_CURSE, 1));
        register(new VanillaEnchantAddModifier("enchantments_add_channeling", Material.TRIDENT, Enchantment.CHANNELING, 1));
        register(new VanillaEnchantAddModifier("enchantments_add_depth_strider", Material.COD, Enchantment.DEPTH_STRIDER, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_efficiency", Material.IRON_PICKAXE, Enchantment.DIG_SPEED, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_unbreaking", Material.NETHERITE_PICKAXE, Enchantment.DURABILITY, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_fire_aspect", Material.BLAZE_POWDER, Enchantment.FIRE_ASPECT, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_frost_walker", Material.ICE, Enchantment.FROST_WALKER, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_impaling", Material.TRIDENT, Enchantment.IMPALING, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_knockback", Material.PISTON, Enchantment.KNOCKBACK, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_loyalty", Material.WOLF_SPAWN_EGG, Enchantment.LOYALTY, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_luck_of_the_sea", Material.GOLD_NUGGET, Enchantment.LUCK, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_lure", Material.IRON_NUGGET, Enchantment.LURE, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_mending", Material.ANVIL, Enchantment.MENDING, 1));
        register(new VanillaEnchantAddModifier("enchantments_add_multishot", Material.SPECTRAL_ARROW, Enchantment.MULTISHOT, 1));
        register(new VanillaEnchantAddModifier("enchantments_add_respiration", Material.NAUTILUS_SHELL, Enchantment.OXYGEN, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_piercing", Material.CROSSBOW, Enchantment.PIERCING, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_protection", Material.IRON_CHESTPLATE, Enchantment.PROTECTION_ENVIRONMENTAL, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_blast_protection", Material.GOLDEN_CHESTPLATE, Enchantment.PROTECTION_EXPLOSIONS, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_feather_falling", Material.LEATHER_BOOTS, Enchantment.PROTECTION_FALL, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_fire_protection", Material.NETHERITE_CHESTPLATE, Enchantment.PROTECTION_FIRE, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_projectile_protection", Material.DIAMOND_CHESTPLATE, Enchantment.PROTECTION_PROJECTILE, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_quick_charge", Material.STRING, Enchantment.QUICK_CHARGE, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_riptide", Material.SALMON, Enchantment.RIPTIDE, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_silk_touch", Material.WHITE_WOOL, Enchantment.SILK_TOUCH, 1));
        register(new VanillaEnchantAddModifier("enchantments_add_soul_speed", Material.SOUL_SAND, Enchantment.SOUL_SPEED, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_sweeping_edge", Material.STONE_SWORD, Enchantment.SWEEPING_EDGE, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_thorns", Material.CACTUS, Enchantment.THORNS, Short.MAX_VALUE));
        register(new VanillaEnchantAddModifier("enchantments_add_curse_vanishing", Material.BARRIER, Enchantment.VANISHING_CURSE, 1));
        register(new VanillaEnchantAddModifier("enchantments_add_aqua_affinity", Material.HEART_OF_THE_SEA, Enchantment.WATER_WORKER, 1));

        register(new AddCustomEnchantCounterModifier("enchantment_counter_increment", 0, ModifierPriority.NEUTRAL));
        register(new EnchantCounterCancelIfExceededModifier("enchantment_cancel_if_counter_increment_exceeds", 0, ModifierPriority.NEUTRAL));

        register(new RandomlyEnchantItemModifier("enchant_item_randomly", 0, ModifierPriority.NEUTRAL));
    }

    public static DynamicItemModifierManager getInstance(){
        if (manager == null) manager = new DynamicItemModifierManager();
        return manager;
    }

    public boolean register(DynamicItemModifier modifier){
        if (modifiers.containsKey(modifier.getName())) return false;
        modifiers.put(modifier.getName(), modifier);
        return true;
    }

    /**
     * Creates an instance of the appropriate DynamicItemModifier given its name.
     * This method is used in loading the configs, because the type of modifier is only accessible by a string name
     * and requires a double strength property to be given.
     * @param name the name of the modifier type as registered in getModifiers()
     * @param strength the double strength given to the modifier
     * @return an instance of DynamicModifier used to tinker with output ItemStacks of custom recipes.
     */
    public DynamicItemModifier createModifier(String name, double strength, ModifierPriority priority){
        try {
            if (modifiers.get(name) == null) return null;
            DynamicItemModifier modifier = modifiers.get(name).clone();
            modifier.setStrength(strength);
            modifier.setPriority(priority);
            return modifier;
        } catch (CloneNotSupportedException ignored){
            return null;
        }
    }

    /**
     * Creates an instance of the appropriate DynamicItemModifier given its name.
     * This method is used in loading the configs, because the type of modifier is only accessible by a string name
     * and requires a double strength property to be given.
     * @param name the name of the modifier type as registered in getModifiers()
     * @param strength the double strength given to the modifier
     * @return an instance of DynamicModifier used to tinker with output ItemStacks of custom recipes.
     */
    public DynamicItemModifier createModifier(String name, double strength, double strength2, ModifierPriority priority){
        try {
            if (modifiers.get(name) == null) return null;
            DynamicItemModifier modifier = modifiers.get(name).clone();
            modifier.setStrength(strength);
            modifier.setPriority(priority);

            if (modifier instanceof DuoArgDynamicItemModifier){
                ((DuoArgDynamicItemModifier) modifier).setStrength2(strength2);
            }
            return modifier;
        } catch (CloneNotSupportedException ignored){
            return null;
        }
    }

    /**
     * Creates an instance of the appropriate DynamicItemModifier given its name.
     * This method is used in loading the configs, because the type of modifier is only accessible by a string name
     * and requires a double strength property to be given.
     * @param name the name of the modifier type as registered in getModifiers()
     * @param strength the double strength given to the modifier
     * @return an instance of DynamicModifier used to tinker with output ItemStacks of custom recipes.
     */
    public DynamicItemModifier createModifier(String name, double strength, double strength2, double strength3, ModifierPriority priority){
        try {
            if (modifiers.get(name) == null) return null;
            DynamicItemModifier modifier = modifiers.get(name).clone();
            modifier.setStrength(strength);
            modifier.setPriority(priority);
            if (modifier instanceof TripleArgDynamicItemModifier){
                ((TripleArgDynamicItemModifier) modifier).setStrength2(strength2);
                ((TripleArgDynamicItemModifier) modifier).setStrength3(strength3);
            }
            return modifier;
        } catch (CloneNotSupportedException ignored){
            return null;
        }
    }

    public Map<String, DynamicItemModifier> getModifiers() {
        return modifiers;
    }
}
