package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.*;
import me.athlaeos.valhallammo.skills.SkillType;

import java.util.*;

public class DynamicItemModifierManager {
    private static DynamicItemModifierManager manager = null;

    private final Map<String, DynamicItemModifier> modifiers = new HashMap<>();

    public DynamicItemModifierManager(){
        register(new DynamicDurabilityModifier("dynamic_durability", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicBowStrengthModifier("dynamic_bow_strength", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicQualityRatingModifier("dynamic_quality", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicDamageModifier("dynamic_damage", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicAttackSpeedModifier("dynamic_attack_speed", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicArmorModifier("dynamic_armor", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicArmorToughnessModifier("dynamic_armor_toughness", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicKnockbackResistanceModifier("dynamic_knockback_resistance", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicMovementSpeedModifier("dynamic_movement_speed", 0D, ModifierPriority.NEUTRAL));
//        register(new DynamicKnockbackModifier("dynamic_knockback", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicHealthBoostModifier("dynamic_health_boost", 0D, ModifierPriority.NEUTRAL));
        register(new DynamicRepairModifier("repair_dynamic", 0D, ModifierPriority.NEUTRAL));

        register(new ApplyTemperModifier("treatment_add_tempering", 0D, ModifierPriority.NEUTRAL));
        register(new ApplyQuenchModifier("treatment_add_quenching", 0D, ModifierPriority.NEUTRAL));
        register(new ApplyRoughSharpenModifier("treatment_add_sharpening_rough", 0D, ModifierPriority.NEUTRAL));
        register(new ApplyFineSharpenModifier("treatment_add_sharpening_fine", 0D, ModifierPriority.NEUTRAL));
        register(new ApplyLeatherBindingModifier("treatment_add_leather_binding", 0D, ModifierPriority.NEUTRAL));
        register(new ApplyWaxCoatingModifier("treatment_add_wax_coating", 0D, ModifierPriority.NEUTRAL));
        register(new ApplyArmorFittingModifier("treatment_add_armor_fitting", 0D, ModifierPriority.NEUTRAL));
        register(new ApplyPolishingModifier("treatment_add_polishing", 0D, ModifierPriority.NEUTRAL));
        register(new ApplyEngravingModifier("treatment_add_engraving", 0D, ModifierPriority.NEUTRAL));
        register(new ApplyHeatingModifier("treatment_add_heating", 0D, ModifierPriority.NEUTRAL));
        register(new ApplySuperHeatingModifier("treatment_add_super_heating", 0D, ModifierPriority.NEUTRAL));
        register(new ApplyStuddingModifier("treatment_add_studding", 0D, ModifierPriority.NEUTRAL));
        register(new ApplyGenericImprovementModifier("treatment_add_generic_improvement", 0D, ModifierPriority.NEUTRAL));

        register(new RemoveTemperModifier("treatment_remove_tempering", 0D, ModifierPriority.NEUTRAL));
        register(new RemoveQuenchModifier("treatment_remove_quenching", 0D, ModifierPriority.NEUTRAL));
        register(new RemoveRoughSharpeningModifier("treatment_remove_sharpening_rough", 0D, ModifierPriority.NEUTRAL));
        register(new RemoveFineSharpeningModifier("treatment_remove_sharpening_fine", 0D, ModifierPriority.NEUTRAL));
        register(new RemoveLeatherBindingModifier("treatment_remove_leather_binding", 0D, ModifierPriority.NEUTRAL));
        register(new RemoveWaxCoatingModifier("treatment_remove_wax_coating", 0D, ModifierPriority.NEUTRAL));
        register(new RemoveArmorFittingModifier("treatment_remove_armor_fitting", 0D, ModifierPriority.NEUTRAL));
        register(new RemovePolishingModifier("treatment_remove_polishing", 0D, ModifierPriority.NEUTRAL));
        register(new RemoveEngravingModifier("treatment_remove_engraving", 0D, ModifierPriority.NEUTRAL));
        register(new RemoveHeatingModifier("treatment_remove_heating", 0D, ModifierPriority.NEUTRAL));
        register(new RemoveSuperHeatingModifier("treatment_remove_super_heating", 0D, ModifierPriority.NEUTRAL));
        register(new RemoveStuddingModifier("treatment_remove_studding", 0D, ModifierPriority.NEUTRAL));
        register(new RemoveGenericImprovementModifier("treatment_remove_generic_improvement", 0D, ModifierPriority.NEUTRAL));

        register(new RemoveAllTreatmentsModifier("treatment_remove_all", 0D, ModifierPriority.NEUTRAL));

        register(new RequirementTemperModifier("treatment_requirement_tempering", 0D, ModifierPriority.NEUTRAL));
        register(new RequirementQuenchModifier("treatment_requirement_quenching", 0D, ModifierPriority.NEUTRAL));
        register(new RequirementRoughSharpeningModifier("treatment_requirement_sharpening_rough", 0D, ModifierPriority.NEUTRAL));
        register(new RequirementFineSharpeningModifier("treatment_requirement_sharpening_fine", 0D, ModifierPriority.NEUTRAL));
        register(new RequirementLeatherBindingModifier("treatment_requirement_leather_binding", 0D, ModifierPriority.NEUTRAL));
        register(new RequirementWaxCoatingModifier("treatment_requirement_wax_coating", 0D, ModifierPriority.NEUTRAL));
        register(new RequirementArmorFittingModifier("treatment_requirement_armor_fitting", 0D, ModifierPriority.NEUTRAL));
        register(new RequirementPolishingModifier("treatment_requirement_polishing", 0D, ModifierPriority.NEUTRAL));
        register(new RequirementEngravingModifier("treatment_requirement_engraving", 0D, ModifierPriority.NEUTRAL));
        register(new RequirementHeatingModifier("treatment_requirement_heating", 0D, ModifierPriority.NEUTRAL));
        register(new RequirementSuperHeatingModifier("treatment_requirement_super_heating", 0D, ModifierPriority.NEUTRAL));
        register(new RequirementStuddingModifier("treatment_requirement_studding", 0D, ModifierPriority.NEUTRAL));
        register(new RequirementGenericImprovementModifier("treatment_requirement_generic_improvement", 0D, ModifierPriority.NEUTRAL));

        register(new UpgradeNetheriteModifier("upgrade_equipment_netherite", 0D, ModifierPriority.NEUTRAL));
        register(new UpgradeDiamondModifier("upgrade_equipment_diamond", 0D, ModifierPriority.NEUTRAL));
        register(new UpgradeIronModifier("upgrade_equipment_iron", 0D, ModifierPriority.NEUTRAL));
        register(new UpgradeGoldModifier("upgrade_equipment_gold", 0D, ModifierPriority.NEUTRAL));
        register(new UpgradeStoneChainModifier("upgrade_equipment_stone_chain", 0D, ModifierPriority.NEUTRAL));
        register(new UpgradeWoodLeatherModifier("upgrade_equipment_wood_leather", 0D, ModifierPriority.NEUTRAL));

        register(new EXPModifier("exp_bonus_smithing", 0D, ModifierPriority.NEUTRAL, SkillType.SMITHING));
        register(new EXPModifier("exp_bonus_alchemy", 0D, ModifierPriority.NEUTRAL, SkillType.ALCHEMY));
        register(new EXPModifier("exp_bonus_enchanting", 0D, ModifierPriority.NEUTRAL, SkillType.ENCHANTING));
        register(new ExpLevelCostModifier("exp_cost_level", 0D, ModifierPriority.NEUTRAL));
        register(new ExpPointsCostModifier("exp_cost_points", 0D, ModifierPriority.NEUTRAL));

        register(new AttributeAddArmorKBResistModifier("default_attribute_knockback_resist_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddArmorModifier("default_attribute_armor_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddArmorToughnessModifier("default_attribute_armor_toughness_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddAttackDamageModifier("default_attribute_attack_damage_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddAttackSpeedModifier("default_attribute_attack_speed_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddMovementSpeedModifier("default_attribute_movement_speed_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddHealthModifier("default_attribute_max_health_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddKnockbackModifier("default_attribute_attack_knockback_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddDamageResistanceModifier("default_attribute_damage_resistance_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddFireResistanceModifier("default_attribute_fire_resistance_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddExplosionResistanceModifier("default_attribute_explosion_resistance_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddMagicResistanceModifier("default_attribute_magic_resistance_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddPoisonResistanceModifier("default_attribute_poison_resistance_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddProjectileResistanceModifier("default_attribute_projectile_resistance_add", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddDrawStrengthModifier("draw_strength_static", 0D, ModifierPriority.NEUTRAL));
        register(new AttributeAddCustomDurabilityModifier("durability_static", 0D, ModifierPriority.NEUTRAL));

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

        register(new StaticRepairModifier("repair_static", 0D, ModifierPriority.NEUTRAL));
        register(new StaticQualityRatingModifier("quality_static", 0D, ModifierPriority.NEUTRAL));

        register(new CustomModelDataAddModifier("custom_model_data_add", 0D, ModifierPriority.NEUTRAL));
        register(new CustomModelDataRequirementModifier("custom_model_data_require", 0D, ModifierPriority.NEUTRAL));
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

    public Map<String, DynamicItemModifier> getModifiers() {
        return modifiers;
    }
}
