package me.athlaeos.valhallammo.crafting;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.advanced_modifiers.*;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.enchantment_conditionals.*;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.enchantment_stats.*;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.experience.ExpLevelCostModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.experience.ExpPointsCostModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.experience.SkillEXPModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_conditionals.*;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats.*;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_conditionals.*;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_stats.*;
import me.athlaeos.valhallammo.dom.MinecraftVersion;
import me.athlaeos.valhallammo.events.ValhallaLoadModifiersEvent;
import me.athlaeos.valhallammo.items.ItemTreatment;
import me.athlaeos.valhallammo.items.PotionTreatment;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.managers.MinecraftVersionManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.potion.PotionEffectType;

import java.util.HashMap;
import java.util.Map;

public class DynamicItemModifierManager {
    private static DynamicItemModifierManager manager = null;

    private final Map<String, DynamicItemModifier> modifiers = new HashMap<>();

    public DynamicItemModifierManager(){
        ValhallaLoadModifiersEvent event = new ValhallaLoadModifiersEvent();
        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
        if (event.isCancelled()) return;
        event.getModifiersToRegister().forEach(this::register);

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
        register(new DynamicDurabilityModifier("dynamic_durability"));
        register(new DynamicBowStrengthModifier("dynamic_bow_strength"));
        register(new DynamicDamageModifier("dynamic_damage"));
        register(new DynamicAttackSpeedModifier("dynamic_attack_speed"));
        register(new DynamicArmorModifier("dynamic_armor"));
        register(new DynamicArmorToughnessModifier("dynamic_armor_toughness"));
        register(new DynamicKnockbackResistanceModifier("dynamic_knockback_resistance"));
        register(new DynamicMovementSpeedModifier("dynamic_movement_speed"));
//        register(new DynamicKnockbackModifier("dynamic_knockback"));
        register(new DynamicHealthBoostModifier("dynamic_health_boost"));

        register(new AddTreatmentModifier("treatment_add_tempering", ItemTreatment.TEMPERING, Material.IRON_INGOT));
        register(new AddTreatmentModifier("treatment_add_quenching", ItemTreatment.QUENCHING, Material.NETHERITE_INGOT));
        register(new AddTreatmentModifier("treatment_add_sharpening_rough", ItemTreatment.SHARPENING_ROUGH, Material.GRINDSTONE));
        register(new AddTreatmentModifier("treatment_add_sharpening_fine", ItemTreatment.SHARPENING_FINE, Material.POLISHED_ANDESITE_SLAB));
        register(new AddTreatmentModifier("treatment_add_leather_binding", ItemTreatment.LEATHER_BINDING, Material.LEATHER));
        register(new AddTreatmentModifier("treatment_add_wax_coating", ItemTreatment.WAX_COATING, Material.HONEYCOMB));
        register(new AddTreatmentModifier("treatment_add_armor_fitting", ItemTreatment.ARMOR_FITTING, Material.NETHERITE_SCRAP));
        register(new AddTreatmentModifier("treatment_add_polishing", ItemTreatment.POLISHING, Material.CLAY_BALL));
        register(new AddTreatmentModifier("treatment_add_engraving", ItemTreatment.ENGRAVING, Material.QUARTZ));
        register(new AddTreatmentModifier("treatment_add_heating", ItemTreatment.HEATING, Material.CAMPFIRE));
        register(new AddTreatmentModifier("treatment_add_super_heating", ItemTreatment.SUPERHEATING, Material.SOUL_CAMPFIRE));
        register(new AddTreatmentModifier("treatment_add_studding", ItemTreatment.STUDDING, Material.IRON_NUGGET));
        register(new AddTreatmentModifier("treatment_add_generic_improvement", ItemTreatment.GENERIC_IMPROVEMENT, Material.GOLD_INGOT));
        register(new AddTreatmentModifier("treatment_add_unenchantable", ItemTreatment.UNENCHANTABLE, Material.BOOK));
        register(new AddTreatmentModifier("treatment_add_unmendable", ItemTreatment.UNMENDABLE, Material.EXPERIENCE_BOTTLE));

        register(new RemoveTreatmentModifier("treatment_remove_tempering", ItemTreatment.TEMPERING, Material.IRON_INGOT));
        register(new RemoveTreatmentModifier("treatment_remove_quenching", ItemTreatment.QUENCHING, Material.NETHERITE_INGOT));
        register(new RemoveTreatmentModifier("treatment_remove_sharpening_rough", ItemTreatment.SHARPENING_ROUGH, Material.GRINDSTONE));
        register(new RemoveTreatmentModifier("treatment_remove_sharpening_fine", ItemTreatment.SHARPENING_FINE, Material.POLISHED_ANDESITE_SLAB));
        register(new RemoveTreatmentModifier("treatment_remove_leather_binding", ItemTreatment.LEATHER_BINDING, Material.LEATHER));
        register(new RemoveTreatmentModifier("treatment_remove_wax_coating", ItemTreatment.WAX_COATING, Material.HONEYCOMB));
        register(new RemoveTreatmentModifier("treatment_remove_armor_fitting", ItemTreatment.ARMOR_FITTING, Material.NETHERITE_SCRAP));
        register(new RemoveTreatmentModifier("treatment_remove_polishing", ItemTreatment.POLISHING, Material.CLAY_BALL));
        register(new RemoveTreatmentModifier("treatment_remove_engraving", ItemTreatment.ENGRAVING, Material.QUARTZ));
        register(new RemoveTreatmentModifier("treatment_remove_heating", ItemTreatment.HEATING, Material.CAMPFIRE));
        register(new RemoveTreatmentModifier("treatment_remove_super_heating", ItemTreatment.SUPERHEATING, Material.SOUL_CAMPFIRE));
        register(new RemoveTreatmentModifier("treatment_remove_studding", ItemTreatment.STUDDING, Material.IRON_NUGGET));
        register(new RemoveTreatmentModifier("treatment_remove_generic_improvement", ItemTreatment.GENERIC_IMPROVEMENT, Material.GOLD_INGOT));
        register(new RemoveTreatmentModifier("treatment_remove_unenchantable", ItemTreatment.UNENCHANTABLE, Material.BOOK));
        register(new RemoveTreatmentModifier("treatment_remove_unmendable", ItemTreatment.UNENCHANTABLE, Material.EXPERIENCE_BOTTLE));

        register(new RemoveAllTreatmentsModifier("treatment_remove_all"));

        register(new RequireTreatmentModifier("treatment_requirement_tempering", ItemTreatment.TEMPERING, Material.IRON_INGOT));
        register(new RequireTreatmentModifier("treatment_requirement_quenching", ItemTreatment.QUENCHING, Material.NETHERITE_INGOT));
        register(new RequireTreatmentModifier("treatment_requirement_sharpening_rough", ItemTreatment.SHARPENING_ROUGH, Material.GRINDSTONE));
        register(new RequireTreatmentModifier("treatment_requirement_sharpening_fine", ItemTreatment.SHARPENING_FINE, Material.POLISHED_ANDESITE_SLAB));
        register(new RequireTreatmentModifier("treatment_requirement_leather_binding", ItemTreatment.LEATHER_BINDING, Material.LEATHER));
        register(new RequireTreatmentModifier("treatment_requirement_wax_coating", ItemTreatment.WAX_COATING, Material.HONEYCOMB));
        register(new RequireTreatmentModifier("treatment_requirement_armor_fitting", ItemTreatment.ARMOR_FITTING, Material.NETHERITE_SCRAP));
        register(new RequireTreatmentModifier("treatment_requirement_polishing", ItemTreatment.POLISHING, Material.CLAY_BALL));
        register(new RequireTreatmentModifier("treatment_requirement_engraving", ItemTreatment.ENGRAVING, Material.QUARTZ));
        register(new RequireTreatmentModifier("treatment_requirement_heating", ItemTreatment.HEATING, Material.CAMPFIRE));
        register(new RequireTreatmentModifier("treatment_requirement_super_heating", ItemTreatment.SUPERHEATING, Material.SOUL_CAMPFIRE));
        register(new RequireTreatmentModifier("treatment_requirement_studding", ItemTreatment.STUDDING, Material.IRON_NUGGET));
        register(new RequireTreatmentModifier("treatment_requirement_generic_improvement", ItemTreatment.GENERIC_IMPROVEMENT, Material.GOLD_INGOT));
        register(new RequireTreatmentModifier("treatment_requirement_unenchantable", ItemTreatment.UNENCHANTABLE, Material.BOOK));
        register(new RequireTreatmentModifier("treatment_requirement_unmendable", ItemTreatment.UNENCHANTABLE, Material.EXPERIENCE_BOTTLE));

        register(new CancelIfTreatmentModifier("treatment_cancel_if_tempering", ItemTreatment.TEMPERING, Material.IRON_INGOT));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_quenching", ItemTreatment.QUENCHING, Material.NETHERITE_INGOT));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_sharpening_rough", ItemTreatment.SHARPENING_ROUGH, Material.GRINDSTONE));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_sharpening_fine", ItemTreatment.SHARPENING_FINE, Material.POLISHED_ANDESITE_SLAB));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_leather_binding", ItemTreatment.LEATHER_BINDING, Material.LEATHER));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_wax_coating", ItemTreatment.WAX_COATING, Material.HONEYCOMB));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_armor_fitting", ItemTreatment.ARMOR_FITTING, Material.NETHERITE_SCRAP));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_polishing", ItemTreatment.POLISHING, Material.CLAY_BALL));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_engraving", ItemTreatment.ENGRAVING, Material.QUARTZ));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_heating", ItemTreatment.HEATING, Material.CAMPFIRE));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_super_heating", ItemTreatment.SUPERHEATING, Material.SOUL_CAMPFIRE));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_studding", ItemTreatment.STUDDING, Material.IRON_NUGGET));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_generic_improvement", ItemTreatment.GENERIC_IMPROVEMENT, Material.GOLD_INGOT));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_unenchantable", ItemTreatment.UNENCHANTABLE, Material.BOOK));
        register(new CancelIfTreatmentModifier("treatment_cancel_if_unmendable", ItemTreatment.UNENCHANTABLE, Material.EXPERIENCE_BOTTLE));

        register(new UpgradeNetheriteModifier("upgrade_equipment_netherite"));
        register(new UpgradeDiamondModifier("upgrade_equipment_diamond"));
        register(new UpgradeIronModifier("upgrade_equipment_iron"));
        register(new UpgradeGoldModifier("upgrade_equipment_gold"));
        register(new UpgradeStoneChainModifier("upgrade_equipment_stone_chain"));
        register(new UpgradeWoodLeatherModifier("upgrade_equipment_wood_leather"));

        register(new ArmorHeavyModifier("armor_weight_class_heavy"));
        register(new ArmorLightModifier("armor_weight_class_light"));
        register(new ArmorWeightlessModifier("armor_weight_class_weightless"));
        register(new WeaponHeavyModifier("weapon_weight_class_heavy"));
        register(new WeaponLightModifier("weapon_weight_class_light"));
        register(new WeaponWeightlessModifier("weapon_weight_class_weightless"));

        register(new SkillEXPModifier("exp_bonus_smithing", "SMITHING"));
        register(new SkillEXPModifier("exp_bonus_alchemy", "ALCHEMY"));
        register(new SkillEXPModifier("exp_bonus_enchanting", "ENCHANTING"));
        register(new SkillEXPModifier("exp_bonus_farming", "FARMING"));

        register(new ExpLevelCostModifier("exp_cost_level"));
        register(new ExpPointsCostModifier("exp_cost_points"));

        register(new LeatherArmorColorModifier("leather_armor_color"));

        register(new AttributeAddArmorKBResistModifier("default_attribute_knockback_resist_add"));
        register(new AttributeAddArmorModifier("default_attribute_armor_add"));
        register(new AttributeAddArmorToughnessModifier("default_attribute_armor_toughness_add"));
        register(new AttributeAddAttackDamageModifier("default_attribute_attack_damage_add"));
        register(new AttributeAddAttackSpeedModifier("default_attribute_attack_speed_add"));
        register(new AttributeAddMovementSpeedModifier("default_attribute_movement_speed_add"));
        register(new AttributeAddHealthModifier("default_attribute_max_health_add"));
        register(new AttributeAddDamageResistanceModifier("default_attribute_damage_resistance_add"));
        register(new AttributeAddFireResistanceModifier("default_attribute_fire_resistance_add"));
        register(new AttributeAddExplosionResistanceModifier("default_attribute_explosion_resistance_add"));
        register(new AttributeAddMagicResistanceModifier("default_attribute_magic_resistance_add"));
        register(new AttributeAddPoisonResistanceModifier("default_attribute_poison_resistance_add"));
        register(new AttributeAddProjectileResistanceModifier("default_attribute_projectile_resistance_add"));
        register(new AttributeAddMeleeResistanceModifier("default_attribute_melee_resistance_add"));
        register(new AttributeAddFallingResistanceModifier("default_attribute_falling_resistance_add"));
        register(new AttributeAddDrawStrengthModifier("draw_strength_static"));
        register(new AttributeAddCustomDurabilityModifier("durability_static"));
        register(new AttributeAddArrowStrengthModifier("arrow_damage"));
        register(new AttributeAddArrowAccuracyModifier("arrow_accuracy"));
        register(new AttributeAddArrowSaveChanceModifier("arrow_save_chance"));
        register(new AttributeAddArrowSpeedModifier("arrow_speed"));
        register(new AttributeAddArrowPiercingModifier("arrow_piercing"));
        register(new AttributeAddArrowInfinityExploitableModifier("arrow_infinity_compatible"));
        register(new AttributeAddModifier("custom_knockback", "CUSTOM_KNOCKBACK", Material.PISTON, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
        register(new AttributeAddModifier("custom_stun_chance", "CUSTOM_STUN_CHANCE", Material.ANVIL, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
        register(new AttributeAddModifier("custom_bleed_chance", "CUSTOM_BLEED_CHANCE", Material.BEETROOT_SOUP, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));
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
        register(new AttributeAddModifier("custom_dismount_chance", "CUSTOM_DISMOUNT_CHANCE", Material.SADDLE, 0, Integer.MIN_VALUE, Integer.MAX_VALUE, 0.1, 0.01));

        register(new ArrowExplosiveUpgradeModifier("arrow_explosive_upgrade"));
        register(new ArrowIncendiaryUpgradeModifier("arrow_incendiary_upgrade"));
        register(new ArrowEnderUpgradeModifier("arrow_ender_upgrade"));
        register(new ArrowRemoveIFramesUpgradeModifier("arrow_no_iframes_upgrade"));
        register(new ArrowLightningUpgradeModifier("arrow_lightning_upgrade"));
        register(new ArrowSFireballUpgradeModifier("arrow_small_fireball_upgrade"));
        register(new ArrowLFireballUpgradeModifier("arrow_large_fireball_upgrade"));
        register(new ArrowDFireballUpgradeModifier("arrow_dragon_fireball_upgrade"));
        register(new ArrowRemoveGravityUpgradeModifier("arrow_remove_gravity_upgrade"));

        register(new AttributeRemoveArmorKBResistModifier("default_attribute_knockback_resist_remove"));
        register(new AttributeRemoveArmorModifier("default_attribute_armor_remove"));
        register(new AttributeRemoveArmorToughnessModifier("default_attribute_armor_toughness_remove"));
        register(new AttributeRemoveAttackDamageModifier("default_attribute_attack_damage_remove"));
        register(new AttributeRemoveAttackSpeedModifier("default_attribute_attack_speed_remove"));
        register(new AttributeRemoveMovementSpeedModifier("default_attribute_movement_speed_remove"));
        register(new AttributeRemoveHealthModifier("default_attribute_max_health_remove"));
        register(new AttributeRemoveKnockbackModifier("default_attribute_knockback_remove"));
        register(new AttributeRemoveDamageResistanceModifier("default_attribute_damage_resistance_remove"));
        register(new AttributeRemoveFireResistanceModifier("default_attribute_fire_resistance_remove"));
        register(new AttributeRemoveExplosionResistanceModifier("default_attribute_explosion_resistance_remove"));
        register(new AttributeRemoveMagicResistanceModifier("default_attribute_magic_resistance_remove"));
        register(new AttributeRemovePoisonResistanceModifier("default_attribute_poison_resistance_remove"));
        register(new AttributeRemoveProjectileResistanceModifier("default_attribute_projectile_resistance_remove"));


        register(new SetAmountModifier("set_amount"));
        register(new RandomizedAmountModifier("random_amount"));
        register(new DynamicAmountModifier("dynamic_amount"));

        register(new SetToolIdModifier("tool_id"));
        register(new WeaponIdAddModifier("weapon_id"));
        register(new WeaponIdRequirementModifier("require_weapon_id"));
        register(new MaterialTypeChangeModifier("material_type_set"));
        register(new EquipmentTypeChangeModifier("equipment_type_set"));
        register(new ChangeItemToDictionaryItemModifier("change_item"));
        register(new ChangeItemToDictionaryItemKeepingAmountModifier("change_item_keeping_amount"));
        register(new ChangeItemNameToDictionaryItemNameModifier("change_item_name"));
        register(new ChangeItemTypeToDictionaryItemTypeModifier("change_item_type"));
        register(new ColorCodeItemLoreModifier("color_code_lore"));
        register(new ColorCodeItemNameModifier("color_code_name"));

        register(new StaticRepairModifier("repair_static"));
        register(new DynamicRepairModifier("repair_dynamic"));
        register(new NumbericRepairModifier("repair_numberic"));

        register(new DynamicQualityRatingModifier("dynamic_quality"));
        register(new StaticQualityRatingModifier("quality_static"));
        register(new RandomizedQualityRatingModifier("random_quality"));
        register(new AddQualityRatingModifier("add_quality"));
        register(new MultiplyQualityRatingModifier("multiply_quality"));

        register(new RandomizedDurabilityModifier("random_durability"));

        register(new CustomModelDataAddModifier("custom_model_data_add"));
        register(new CustomModelDataRequirementModifier("custom_model_data_require"));

        register(new AddPlayerSignatureModifier("add_signature"));

        register(new FlagAddHideAttributeModifier("flag_add_hide_attributes"));
        register(new FlagAddHidePotionEffectsModifier("flag_add_hide_potion_effects"));
        register(new FlagAddHideUnbreakableModifier("flag_add_hide_unbreakable"));
        register(new FlagAddHideEnchantmentsModifier("flag_add_hide_enchantments"));
        register(new FlagRemoveHideAttributeModifier("flag_remove_hide_attributes"));
        register(new FlagRemoveHidePotionEffectsModifier("flag_remove_hide_potion_effects"));
        register(new FlagRemoveHideUnbreakableModifier("flag_remove_hide_unbreakable"));
        register(new FlagRemoveHideEnchantmentsModifier("flag_remove_hide_enchantments"));
        register(new FlagAddDuoHandAttributesModifier("flag_add_duo_hand_attributes"));

        register(new UnbreakableSetModifier("unbreakable_set"));

        register(new RequireCustomModifier("requires_customized"));

        // ALCHEMY
        register(new DynamicPotionAmplifierModifier("potions_dynamic_amplifier"));
        register(new DynamicPotionDurationModifier("potions_dynamic_duration"));
        register(new PotionColorModifier("potions_color"));
        register(new PotionInvertEffectsModifier("potions_invert"));

        register(new PotionEffectAddVanillaModifier("potions_effect_add_instant_health", PotionEffectType.HEAL, Material.GLISTERING_MELON_SLICE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_absorption", PotionEffectType.ABSORPTION, Material.GOLDEN_APPLE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_bad_omen", PotionEffectType.BAD_OMEN, Material.IRON_AXE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_blindness", PotionEffectType.BLINDNESS, Material.BLACK_DYE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_conduit_power", PotionEffectType.CONDUIT_POWER, Material.NAUTILUS_SHELL));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_nausea", PotionEffectType.CONFUSION, Material.ANVIL));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_resistance", PotionEffectType.DAMAGE_RESISTANCE, Material.IRON_BLOCK));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_haste", PotionEffectType.FAST_DIGGING, Material.GOLDEN_PICKAXE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_fire_resistance", PotionEffectType.FIRE_RESISTANCE, Material.MAGMA_CREAM));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_glowing", PotionEffectType.GLOWING, Material.GLOWSTONE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_instant_damage", PotionEffectType.HARM, Material.IRON_SWORD));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_health_boost", PotionEffectType.HEALTH_BOOST, Material.APPLE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_hero_of_the_village", PotionEffectType.HERO_OF_THE_VILLAGE, Material.EMERALD));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_hunger", PotionEffectType.HUNGER, Material.ROTTEN_FLESH));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_strength", PotionEffectType.INCREASE_DAMAGE, Material.BLAZE_POWDER));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_invisibility", PotionEffectType.INVISIBILITY, Material.GLASS));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_jump_boost", PotionEffectType.JUMP, Material.RABBIT_FOOT));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_levitation", PotionEffectType.LEVITATION, Material.SHULKER_SHELL));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_luck", PotionEffectType.LUCK, Material.GOLD_NUGGET));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_night_vision", PotionEffectType.NIGHT_VISION, Material.GOLDEN_CARROT));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_poison", PotionEffectType.POISON, Material.SPIDER_EYE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_regeneration", PotionEffectType.REGENERATION, Material.GHAST_TEAR));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_saturation", PotionEffectType.SATURATION, Material.MUSHROOM_STEW));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_slow", PotionEffectType.SLOW, Material.SNOWBALL));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_fatigue", PotionEffectType.SLOW_DIGGING, Material.WOODEN_PICKAXE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_slow_falling", PotionEffectType.SLOW_FALLING, Material.PHANTOM_MEMBRANE));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_speed", PotionEffectType.SPEED, Material.SUGAR));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_bad_luck", PotionEffectType.UNLUCK, Material.IRON_NUGGET));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_water_breathing", PotionEffectType.WATER_BREATHING, Material.PUFFERFISH));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_weakness", PotionEffectType.WEAKNESS, Material.WOODEN_SWORD));
        register(new PotionEffectAddVanillaModifier("potions_effect_add_wither", PotionEffectType.WITHER, Material.WITHER_SKELETON_SKULL));

        register(new PotionTransmutationAssignmentModifier("potion_transmutations_assign"));

        register(new AddEnchantmentGlowModifier("enchantment_glimmer_add"));
        register(new RemoveAllEnchantmentsModifier("enchantment_remove_all"));

        register(new PotionEffectAddCustomModifier("potions_effect_add_masterpiece_smithing", "MASTERPIECE_SMITHING", Material.DAMAGED_ANVIL, 1, 10, "%.0f", 1));
        register(new PotionEffectAddCustomModifier("potions_effect_add_fortify_enchanting", "FORTIFY_ENCHANTING", Material.ENCHANTING_TABLE, 1, 10, "%.0f", 1));
        register(new PotionEffectAddCustomModifier("potions_effect_add_fortify_anvil_combining", "FORTIFY_ANVIL_COMBINING", Material.ANVIL, 1, 10, "%.0f", 1));
        register(new PotionEffectAddCustomModifier("potions_effect_add_fortify_smithing", "FORTIFY_SMITHING", Material.ANVIL, 1, 10, "%.0f", 1));
        register(new PotionEffectAddCustomModifier("potions_effect_add_farming_extra_drops", "FARMING_EXTRA_DROPS", Material.WHEAT, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_farming_rare_drops", "FARMING_RARE_DROPS", Material.BEETROOT, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_farming_fishing_tier", "FARMING_FISHING_TIER", Material.FISHING_ROD, 0.1, 1, "%.1f", 1));
        register(new PotionEffectAddCustomModifier("potions_effect_add_mining_extra_drops", "MINING_EXTRA_DROPS", Material.IRON_PICKAXE, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_mining_rare_drops", "MINING_RARE_DROPS", Material.GOLDEN_PICKAXE, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_woodcutting_extra_drops", "WOODCUTTING_EXTRA_DROPS", Material.IRON_AXE, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_woodcutting_rare_drops", "WOODCUTTING_RARE_DROPS", Material.GOLDEN_AXE, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_digging_extra_drops", "DIGGING_EXTRA_DROPS", Material.IRON_SHOVEL, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_digging_rare_drops", "DIGGING_RARE_DROPS", Material.GOLDEN_SHOVEL, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_alchemy_brew_speed", "ALCHEMY_BREW_SPEED", Material.BLAZE_ROD, 1, 10, "%.0f%%", 1));
        register(new PotionEffectAddCustomModifier("potions_effect_add_alchemy_ingredient_save", "ALCHEMY_INGREDIENT_SAVE", Material.GLASS_BOTTLE, 1, 10, "%.0f%%", 1));
        register(new PotionEffectAddCustomModifier("potions_effect_add_alchemy_potion_save", "ALCHEMY_POTION_SAVE", Material.SPLASH_POTION, 1, 10, "%.0f%%", 1));
        register(new PotionEffectAddCustomModifier("potions_effect_add_alchemy_potion_velocity", "ALCHEMY_POTION_VELOCITY", Material.SPLASH_POTION, 1, 10, "%.0f%%", 1));
        register(new PotionEffectAddCustomModifier("potions_effect_add_archery_accuracy", "ARCHERY_ACCURACY", Material.TARGET, 0.1, 1, "%.1f", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_archery_damage", "ARCHERY_DAMAGE", Material.BOW, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_archery_ammo_save", "ARCHERY_AMMO_SAVE", Material.ARROW, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_weapons_damage", "WEAPONS_DAMAGE", Material.IRON_SWORD, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_increase_exp", "INCREASE_EXP", Material.EXPERIENCE_BOTTLE, 1, 10, "%.0f%%", 1));
        register(new PotionEffectAddCustomModifier("potions_effect_add_milk", "MILK", Material.MILK_BUCKET));
        register(new PotionEffectAddCustomModifier("potions_effect_add_armor_flat_bonus", "ARMOR_FLAT_BONUS", Material.CHAINMAIL_CHESTPLATE, 0.1, 1, "%.1f", 1));
        register(new PotionEffectAddCustomModifier("potions_effect_add_light_armor_flat_bonus", "LIGHT_ARMOR_FLAT_BONUS", Material.LEATHER_CHESTPLATE, 0.1, 1, "%.1f", 1));
        register(new PotionEffectAddCustomModifier("potions_effect_add_heavy_armor_flat_bonus", "HEAVY_ARMOR_FLAT_BONUS", Material.GOLDEN_CHESTPLATE, 0.1, 1, "%.1f", 1));
        register(new PotionEffectAddCustomModifier("potions_effect_add_armor_fraction_bonus", "ARMOR_FRACTION_BONUS", Material.IRON_CHESTPLATE, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_light_armor_fraction_bonus", "LIGHT_ARMOR_FRACTION_BONUS", Material.DIAMOND_CHESTPLATE, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_heavy_armor_fraction_bonus", "HEAVY_ARMOR_FRACTION_BONUS", Material.NETHERITE_CHESTPLATE, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_damage_resistance", "CUSTOM_DAMAGE_RESISTANCE", Material.ENCHANTED_GOLDEN_APPLE, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_explosion_resistance", "EXPLOSION_RESISTANCE", Material.TNT, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_custom_fire_resistance", "CUSTOM_FIRE_RESISTANCE", Material.MAGMA_CREAM, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_magic_resistance", "MAGIC_RESISTANCE", Material.DRAGON_EGG, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_poison_resistance", "POISON_RESISTANCE", Material.SPIDER_EYE, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_projectile_resistance", "PROJECTILE_RESISTANCE", Material.SHIELD, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_melee_resistance", "MELEE_RESISTANCE", Material.IRON_SWORD, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_falling_resistance", "FALLING_RESISTANCE", Material.FEATHER, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_knockback_resistance", "KNOCKBACK_RESISTANCE", Material.NETHERITE_LEGGINGS, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_bleed_resistance", "BLEED_RESISTANCE", Material.BEETROOT_SOUP, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_crafting_time_reduction", "CRAFTING_TIME_REDUCTION", Material.CRAFTING_TABLE, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_hunger_save_chance", "HUNGER_SAVE_CHANCE", Material.BREAD, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_dodge_chance", "DODGE_CHANCE", Material.LEATHER_BOOTS, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_knockback_bonus", "KNOCKBACK_BONUS", Material.STONE_AXE, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_cooldown_reduction", "COOLDOWN_REDUCTION", Material.CLOCK, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_immunity_frame_bonus", "IMMUNITY_FRAME_BONUS", Material.NETHER_STAR, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_immunity_frame_multiplier", "IMMUNITY_FRAME_MULTIPLIER", Material.NETHER_STAR, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_healing_bonus", "HEALING_BONUS", Material.GLISTERING_MELON_SLICE, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_reflect_chance", "REFLECT_CHANCE", Material.CACTUS, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_reflect_fraction", "REFLECT_FRACTION", Material.CACTUS, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_poison_anti_heal", "POISON_ANTI_HEAL", Material.WITHER_SKELETON_SKULL, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_poison_vulnerable", "POISON_VULNERABLE", Material.SKELETON_SKULL, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_fraction_armor_reduction", "FRACTION_ARMOR_REDUCTION", Material.NETHERITE_AXE, 0.01, 0.1, "%.0f%%", 100));
        register(new PotionEffectAddCustomModifier("potions_effect_add_flat_armor_reduction", "FLAT_ARMOR_REDUCTION", Material.DIAMOND_AXE, 0.1, 1, "%.1f", 1));
        register(new MilkToChocolateMilkModifier("potions_chocolate_milk"));

        register(new PotionLingeringModifier("potions_conversion_lingering"));
        register(new PotionSplashModifier("potions_conversion_splash"));
        register(new PotionTippedArrowModifier("potions_conversion_tipped"));
        register(new PotionPotionModifier("potions_conversion_potion"));

        register(new PotionCancelIfPotionMaterialModifier("potions_cancel_if_lingering", Material.LINGERING_POTION, Material.LINGERING_POTION));
        register(new PotionCancelIfPotionMaterialModifier("potions_cancel_if_splash", Material.SPLASH_POTION, Material.SPLASH_POTION));
        register(new PotionCancelIfPotionMaterialModifier("potions_cancel_if_tipped", Material.TIPPED_ARROW, Material.TIPPED_ARROW));
        register(new PotionCancelIfPotionMaterialModifier("potions_cancel_if_potion", Material.POTION, Material.POTION));

        register(new PotionRequirePotionMaterialModifier("potions_require_lingering", Material.LINGERING_POTION, Material.LINGERING_POTION));
        register(new PotionRequirePotionMaterialModifier("potions_require_splash", Material.SPLASH_POTION, Material.SPLASH_POTION));
        register(new PotionRequirePotionMaterialModifier("potions_require_tipped", Material.TIPPED_ARROW, Material.TIPPED_ARROW));
        register(new PotionRequirePotionMaterialModifier("potions_require_potion", Material.POTION, Material.POTION));

        register(new PotionDynamicQualityRatingModifier("potions_quality_dynamic_general", null));
        register(new PotionDynamicQualityRatingModifier("potions_quality_dynamic_debuff", PotionType.DEBUFF));
        register(new PotionDynamicQualityRatingModifier("potions_quality_dynamic_buff", PotionType.BUFF));
        register(new RandomizedPotionQualityRatingModifier("potions_quality_random"));
        register(new PotionStaticQualityRatingModifier("potions_quality_static"));

        register(new PotionColorConditionModifier("potions_color_condition"));
        register(new PotionAddTreatmentModifier("potions_treatment_add_filtered", PotionTreatment.FILTERED, Material.COBWEB));
        register(new PotionAddTreatmentModifier("potions_treatment_add_diluted", PotionTreatment.DILUTED, Material.REDSTONE));
        register(new PotionAddTreatmentModifier("potions_treatment_add_concentrated", PotionTreatment.CONCENTRATED, Material.GLOWSTONE_DUST));
        register(new PotionAddTreatmentModifier("potions_treatment_add_empowered", PotionTreatment.EMPOWERED, Material.LAPIS_LAZULI));
        register(new PotionAddTreatmentModifier("potions_treatment_add_imbued", PotionTreatment.IMBUED, Material.BREWING_STAND));
        register(new PotionAddTreatmentModifier("potions_treatment_add_enchanted", PotionTreatment.ENCHANTED, Material.ENCHANTING_TABLE));
        register(new PotionAddTreatmentModifier("potions_treatment_add_polluted", PotionTreatment.POLLUTED, Material.DIRT));
        register(new PotionAddTreatmentModifier("potions_treatment_add_transmutation", PotionTreatment.TRANSMUTATION, Material.GOLDEN_APPLE));

        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_filtered", PotionTreatment.FILTERED, Material.COBWEB));
        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_diluted", PotionTreatment.DILUTED, Material.REDSTONE));
        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_concentrated", PotionTreatment.CONCENTRATED, Material.GLOWSTONE_DUST));
        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_empowered", PotionTreatment.EMPOWERED, Material.LAPIS_LAZULI));
        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_imbued", PotionTreatment.IMBUED, Material.BREWING_STAND));
        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_enchanted", PotionTreatment.ENCHANTED, Material.ENCHANTING_TABLE));
        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_polluted", PotionTreatment.POLLUTED, Material.DIRT));
        register(new PotionCancelIfTreatmentModifier("potions_treatment_cancel_if_transmutation", PotionTreatment.TRANSMUTATION, Material.GOLDEN_APPLE));

        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_filtered", PotionTreatment.FILTERED, Material.COBWEB));
        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_diluted", PotionTreatment.DILUTED, Material.REDSTONE));
        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_concentrated", PotionTreatment.CONCENTRATED, Material.GLOWSTONE_DUST));
        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_empowered", PotionTreatment.EMPOWERED, Material.LAPIS_LAZULI));
        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_imbued", PotionTreatment.IMBUED, Material.BREWING_STAND));
        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_enchanted", PotionTreatment.ENCHANTED, Material.ENCHANTING_TABLE));
        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_polluted", PotionTreatment.POLLUTED, Material.DIRT));
        register(new PotionRemoveTreatmentModifier("potions_treatment_remove_transmutation", PotionTreatment.TRANSMUTATION, Material.GOLDEN_APPLE));

        register(new PotionRequireTreatmentModifier("potions_treatment_require_filtered", PotionTreatment.FILTERED, Material.COBWEB));
        register(new PotionRequireTreatmentModifier("potions_treatment_require_diluted", PotionTreatment.DILUTED, Material.REDSTONE));
        register(new PotionRequireTreatmentModifier("potions_treatment_require_concentrated", PotionTreatment.CONCENTRATED, Material.GLOWSTONE_DUST));
        register(new PotionRequireTreatmentModifier("potions_treatment_require_empowered", PotionTreatment.EMPOWERED, Material.LAPIS_LAZULI));
        register(new PotionRequireTreatmentModifier("potions_treatment_require_imbued", PotionTreatment.IMBUED, Material.BREWING_STAND));
        register(new PotionRequireTreatmentModifier("potions_treatment_require_enchanted", PotionTreatment.ENCHANTED, Material.ENCHANTING_TABLE));
        register(new PotionRequireTreatmentModifier("potions_treatment_require_polluted", PotionTreatment.POLLUTED, Material.DIRT));
        register(new PotionRequireTreatmentModifier("potions_treatment_require_transmutation", PotionTreatment.TRANSMUTATION, Material.GOLDEN_APPLE));

        register(new PotionSetPotionTypeModifier("potions_type_set_awkward", org.bukkit.potion.PotionType.AWKWARD, Material.NETHER_WART));
        register(new PotionSetPotionTypeModifier("potions_type_set_thick", org.bukkit.potion.PotionType.THICK, Material.GLOWSTONE_DUST));
        register(new PotionSetPotionTypeModifier("potions_type_set_mundane", org.bukkit.potion.PotionType.MUNDANE, Material.REDSTONE));
        register(new PotionSetPotionTypeModifier("potions_type_set_water", org.bukkit.potion.PotionType.WATER, Material.WATER_BUCKET));
        register(new PotionSetPotionTypeModifier("potions_type_set_uncraftable", org.bukkit.potion.PotionType.UNCRAFTABLE, Material.BARRIER));

        register(new PotionRequirePotionTypeModifier("potions_type_require_awkward", org.bukkit.potion.PotionType.AWKWARD, Material.NETHER_WART));
        register(new PotionRequirePotionTypeModifier("potions_type_require_thick", org.bukkit.potion.PotionType.THICK, Material.GLOWSTONE_DUST));
        register(new PotionRequirePotionTypeModifier("potions_type_require_mundane", org.bukkit.potion.PotionType.MUNDANE, Material.REDSTONE));
        register(new PotionRequirePotionTypeModifier("potions_type_require_water", org.bukkit.potion.PotionType.WATER, Material.WATER_BUCKET));
        register(new PotionRequirePotionTypeModifier("potions_type_require_uncraftable", org.bukkit.potion.PotionType.UNCRAFTABLE, Material.BARRIER));

        register(new PotionCancelIfPotionTypeModifier("potions_type_cancel_if_awkward", org.bukkit.potion.PotionType.AWKWARD, Material.NETHER_WART));
        register(new PotionCancelIfPotionTypeModifier("potions_type_cancel_if_thick", org.bukkit.potion.PotionType.THICK, Material.GLOWSTONE_DUST));
        register(new PotionCancelIfPotionTypeModifier("potions_type_cancel_if_mundane",  org.bukkit.potion.PotionType.MUNDANE, Material.REDSTONE));
        register(new PotionCancelIfPotionTypeModifier("potions_type_cancel_if_water",  org.bukkit.potion.PotionType.WATER, Material.WATER_BUCKET));
        register(new PotionCancelIfPotionTypeModifier("potions_type_cancel_if_uncraftable", org.bukkit.potion.PotionType.UNCRAFTABLE, Material.BARRIER));

        register(new PotionUpdateNameModifier("potions_update_name"));

        // ENCHANTING
//        register(new CustomEnchantAddModifier("enchantment_add_fortify_acrobatics", Material.BLAZE_ROD, "ALCHEMY_BREW_SPEED", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
//        register(new CustomEnchantAddModifier("enchantment_add_fortify_acrobatics", Material.FEATHER, "ACROBATICS", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_alchemy_brew_speed", Material.BLAZE_ROD, "ALCHEMY_BREW_SPEED", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_alchemy_ingredient_save", Material.CHORUS_FRUIT, "ALCHEMY_INGREDIENT_SAVE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_alchemy_potion_save", Material.SPLASH_POTION, "ALCHEMY_POTION_SAVE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_alchemy_quality", Material.BREWING_STAND, "ALCHEMY_QUALITY", 10, 1, -100000, 100000));
        register(new CustomEnchantAddModifier("enchantment_add_alchemy_throw_velocity", Material.EGG, "ALCHEMY_THROW_VELOCITY", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_archery_accuracy", Material.TARGET, "ARCHERY_ACCURACY", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_archery_ammo_save", Material.ARROW, "ARCHERY_AMMO_SAVE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_archery_damage", Material.CROSSBOW, "ARCHERY_DAMAGE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_damage_dealt", Material.DIAMOND_SWORD, "DAMAGE_DEALT", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_damage_taken", Material.DIAMOND_CHESTPLATE, "DAMAGE_TAKEN", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_exp_gain_skill", Material.WRITABLE_BOOK, "EXP_GAIN_SKILL", 10, 1, -100000, 100000));
        register(new CustomEnchantAddModifier("enchantment_add_exp_gain_vanilla", Material.EXPERIENCE_BOTTLE, "EXP_GAIN_VANILLA", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_farming_extra_drops", Material.DIAMOND_HOE, "FARMING_EXTRA_DROPS", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_farming_rare_drops", Material.GOLDEN_HOE, "FARMING_RARE_DROPS", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_farming_fishing_tier", Material.FISHING_ROD, "FARMING_FISHING_TIER", 0.25, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_mining_extra_drops", Material.DIAMOND_PICKAXE, "MINING_EXTRA_DROPS", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_mining_rare_drops", Material.GOLDEN_PICKAXE, "MINING_RARE_DROPS", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_woodcutting_extra_drops", Material.DIAMOND_AXE, "WOODCUTTING_EXTRA_DROPS", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_woodcutting_rare_drops", Material.GOLDEN_AXE, "WOODCUTTING_RARE_DROPS", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_digging_extra_drops", Material.DIAMOND_SHOVEL, "DIGGING_EXTRA_DROPS", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_digging_rare_drops", Material.GOLDEN_SHOVEL, "DIGGING_RARE_DROPS", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_smithing_quality", Material.ANVIL, "SMITHING_QUALITY", 10, 1, -100000, 100000));
//        register(new CustomEnchantAddModifier("enchantment_add_weapons_damage", Material.DIAMOND_SWORD, "WEAPONS_DAMAGE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
//        register(new CustomEnchantAddModifier("enchantment_add_unarmed_damage", Material.BLAZE_ROD, "UNARMED_DAMAGE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_dodge_chance", Material.FEATHER, "DODGE_CHANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_armor_multiplier", Material.NETHERITE_CHESTPLATE, "ARMOR_MULTIPLIER", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_health_multiplier", Material.ENCHANTED_GOLDEN_APPLE, "HEALTH_MULTIPLIER", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_bleed_resistance", Material.BEETROOT_SOUP, "BLEED_RESISTANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_cooldown_reduction", Material.CLOCK, "COOLDOWN_REDUCTION", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_crafting_speed", Material.CRAFTING_TABLE, "CRAFTING_SPEED", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_healing_bonus", Material.GLISTERING_MELON_SLICE, "HEALING_BONUS", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_hunger_save_chance", Material.BREAD, "HUNGER_SAVE_CHANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_explosion_power", Material.TNT, "EXPLOSION_POWER", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_stun_resistance", Material.ANVIL, "STUN_RESISTANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));

        register(new CustomEnchantAddModifier("enchantment_add_stun_chance", Material.ANVIL, "STUN_CHANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_bleed_chance", Material.BEETROOT_SOUP, "BLEED_CHANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_bleed_damage", Material.BEETROOT_SOUP, "BLEED_DAMAGE", 1, 0.1, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_bleed_duration", Material.BEETROOT_SOUP, "BLEED_DURATION", 1000, 100, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_crit_chance", Material.GOLDEN_SWORD, "CRIT_CHANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_crit_damage", Material.IRON_SWORD, "CRIT_DAMAGE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_flat_armor_penetration", Material.ARROW, "FLAT_ARMOR_PENETRATION", 3, 0.1, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_flat_light_armor_penetration", Material.CHAINMAIL_CHESTPLATE, "FLAT_LIGHT_ARMOR_PENETRATION", 3, 0.1, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_flat_heavy_armor_penetration", Material.IRON_CHESTPLATE, "FLAT_HEAVY_ARMOR_PENETRATION", 3, 0.1, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_fraction_armor_penetration", Material.SPECTRAL_ARROW, "FRACTION_ARMOR_PENETRATION", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_fraction_light_armor_penetration", Material.DIAMOND_CHESTPLATE, "FRACTION_LIGHT_ARMOR_PENETRATION", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_fraction_heavy_armor_penetration", Material.NETHERITE_CHESTPLATE, "FRACTION_HEAVY_ARMOR_PENETRATION", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_light_armor_damage_bonus", Material.LEATHER_CHESTPLATE, "LIGHT_ARMOR_DAMAGE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_heavy_armor_damage_bonus", Material.GOLDEN_CHESTPLATE, "HEAVY_ARMOR_DAMAGE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_immunity_frame_bonus", Material.DRAGON_EGG, "IMMUNITY_FRAME_BONUS", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_immunity_frame_bonus_flat", Material.NETHER_STAR, "IMMUNITY_FRAME_FLAT_BONUS", 5, 1, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_immunity_frame_reduction", Material.TOTEM_OF_UNDYING, "IMMUNITY_FRAME_REDUCTION", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_attack_reach_bonus", Material.TRIDENT, "WEAPON_REACH_BONUS", 0.25, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_damage_resistance", Material.ENCHANTED_GOLDEN_APPLE, "DAMAGE_RESISTANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_fall_damage_resistance", Material.FEATHER, "FALL_DAMAGE_RESISTANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_fire_damage_resistance", Material.MAGMA_CREAM, "FIRE_DAMAGE_RESISTANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_projectile_damage_resistance", Material.SHIELD, "PROJECTILE_DAMAGE_RESISTANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_melee_damage_resistance", Material.NETHERITE_SWORD, "MELEE_DAMAGE_RESISTANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_magic_damage_resistance", Material.DRAGON_EGG, "MAGIC_DAMAGE_RESISTANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_explosion_damage_resistance", Material.TNT, "EXPLOSION_DAMAGE_RESISTANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));
        register(new CustomEnchantAddModifier("enchantment_add_poison_damage_resistance", Material.SPIDER_EYE, "POISON_DAMAGE_RESISTANCE", 0.1, 0.01, Integer.MIN_VALUE, Integer.MAX_VALUE));

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
        if (MinecraftVersionManager.getInstance().currentVersionNewerThan(MinecraftVersion.MINECRAFT_1_19)){
            register(new VanillaEnchantAddModifier("enchantments_add_swift_sneak", Material.FEATHER, Enchantment.SWIFT_SNEAK, Short.MAX_VALUE));
        }

        register(new AddCustomEnchantCounterModifier("enchantment_counter_increment"));
        register(new EnchantCounterCancelIfExceededModifier("enchantment_cancel_if_counter_increment_exceeds"));

        register(new RandomlyEnchantItemModifier("enchant_item_randomly"));
        
        // ADVANCED MODIFIERS
        register(new EnchantmentTransferModifier("transfer_enchantment"));
        register(new DamageItemModifier("damage_item_durability"));
        register(new ItemEqualizerModifier("equalize_items"));
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
            if (modifiers.get(name) == null) {
                ValhallaMMO.getPlugin().getServer().getLogger().warning("Modifier " + name + " was referenced, but it does not exist!");
                return null;
            }
            DynamicItemModifier modifier = modifiers.get(name).clone();
            modifier.setStrength(strength);
            modifier.setPriority(priority);
            if (modifier instanceof TripleArgDynamicItemModifier){
                ((TripleArgDynamicItemModifier) modifier).setStrength2(0);
                ((TripleArgDynamicItemModifier) modifier).setStrength3(0);
            } else if (modifier instanceof DuoArgDynamicItemModifier){
                ((DuoArgDynamicItemModifier) modifier).setStrength2(0);
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
    public DynamicItemModifier createModifier(String name, double strength, double strength2, ModifierPriority priority){
        try {
            if (modifiers.get(name) == null) {
                ValhallaMMO.getPlugin().getServer().getLogger().warning("Modifier " + name + " was referenced, but it does not exist!");
                return null;
            }
            DynamicItemModifier modifier = modifiers.get(name).clone();
            modifier.setStrength(strength);
            modifier.setPriority(priority);

            if (modifier instanceof TripleArgDynamicItemModifier){
                ((TripleArgDynamicItemModifier) modifier).setStrength2(strength2);
                ((TripleArgDynamicItemModifier) modifier).setStrength3(0);
            } else if (modifier instanceof DuoArgDynamicItemModifier){
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
            if (modifiers.get(name) == null) {
                ValhallaMMO.getPlugin().getServer().getLogger().warning("Modifier " + name + " was referenced, but it does not exist!");
                return null;
            }
            DynamicItemModifier modifier = modifiers.get(name).clone();
            modifier.setStrength(strength);
            modifier.setPriority(priority);
            if (modifier instanceof TripleArgDynamicItemModifier){
                ((TripleArgDynamicItemModifier) modifier).setStrength2(strength2);
                ((TripleArgDynamicItemModifier) modifier).setStrength3(strength3);
            } else if (modifier instanceof DuoArgDynamicItemModifier){
                ((DuoArgDynamicItemModifier) modifier).setStrength2(strength2);
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
