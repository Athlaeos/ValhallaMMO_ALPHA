package me.athlaeos.valhallammo.managers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.configs.ConfigManager;
import me.athlaeos.valhallammo.items.ItemTreatment;
import me.athlaeos.valhallammo.dom.Scaling;
import me.athlaeos.valhallammo.dom.ScalingMode;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;
import java.util.stream.Collectors;

public class ItemTreatmentManager {
    private static ItemTreatmentManager manager = null;

    private final NamespacedKey key_treatment = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_treatments");
    private final NamespacedKey key_quality = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_item_quality");

    private BiMap<ItemTreatment, String> treatmentTranslations;
    private Map<Integer, String> cosmeticQualityModifiers;

    private boolean hideTreatmentLore;

    // materialScalings defines the scalings for more specific materials. For example, a leather chestplate might
    // scale differently than a diamond chestplate depending on the quality, because these items have well-defined
    // material classes (LEATHER and DIAMOND respectively) and have vanilla attribute stats by default, GENERIC_ARMOR
    // in this case, and diamond also has GENERIC_ARMOR_TOUGHNESS. Global scalings are more meant for unconventional
    // attribute modifiers, such as GENERIC_MAX_HEALTH. In these cases no specific material distinction is made, because
    // these attributes HAVE to be customly applied through the recipes menu/commands and you already have full control
    // over how much each item should have in terms of stats. It's just not necessary. In practice, though, you do not
    // need to worry about the difference.
    // TL;DR, materialScalings are stat scalings specific per material, while globalScalings don't need to care for
    // the item's material.
    private Map<String, Map<MaterialClass, Scaling>> materialScalings;
    private Map<String, Scaling> globalScalings;
    /**
     * Registers an attribute's quality scalings for a material class. The attribute is a string as it can represent
     * vanilla attributes such as GENERIC_ARMOR or custom attributes such as CUSTOM_DRAW_STRENGTH. Duplicates will be
     * overwritten.
     * @param attribute the attribute to set its quality scalings for
     * @param mClass the material class to apply to the attribute
     * @param scaling the quality scaling formula for the material
     */
    public void registerScaling(String attribute, MaterialClass mClass, Scaling scaling){
        if (attribute == null || mClass == null || scaling == null) return;
        Map<MaterialClass, Scaling> newScalings;
        if (materialScalings.containsKey(attribute)) {
            newScalings = materialScalings.get(attribute);
        } else {
            newScalings = new HashMap<>();
        }
        newScalings.put(mClass, scaling);
        materialScalings.put(attribute, newScalings);
    }

    private Scaling getScalingFromPath(YamlConfiguration config, String path){
        String scaling = config.getString(path + ".scaling");
        if (scaling == null) return null;
        String modeString = config.getString(path + ".mode");
        if (modeString == null) return null;
        String lowerBoundString = config.getString(path + ".lower_bound");
        String upperBoundString = config.getString(path + ".upper_bound");
        double lowerBound = 0;
        double upperbound = 0;
        boolean ignoreLower = false;
        boolean ignoreUpper = false;
        ScalingMode mode = ScalingMode.MULTIPLIER;
        try {
            if (lowerBoundString == null) throw new IllegalArgumentException();
            lowerBound = Double.parseDouble(lowerBoundString);
        } catch (IllegalArgumentException ignored){
            ignoreLower = true;
        }
        try {
            if (upperBoundString == null) throw new IllegalArgumentException();
            upperbound = Double.parseDouble(upperBoundString);
        } catch (IllegalArgumentException ignored){
            ignoreUpper = true;
        }
        try {
            mode = ScalingMode.valueOf(modeString);
        } catch (IllegalArgumentException ignored){
        }
        return new Scaling(scaling, mode, lowerBound, upperbound, ignoreLower, ignoreUpper);
    }

    private String repairScaling;

    public ItemTreatmentManager(){
        loadConfig();
    }

    public void loadConfig(){
        treatmentTranslations = HashBiMap.create();
        cosmeticQualityModifiers = new TreeMap<>();
        materialScalings = new HashMap<>();
        globalScalings = new HashMap<>();
        YamlConfiguration config = ConfigManager.getInstance().getConfig("skill_smithing.yml").get();

        hideTreatmentLore = config.getBoolean("hide_treatment_lore");

        registerScaling("CUSTOM_MAX_DURABILITY", MaterialClass.WOOD, getScalingFromPath(config, "scaling_durability.wood"));
        registerScaling("CUSTOM_MAX_DURABILITY", MaterialClass.LEATHER, getScalingFromPath(config, "scaling_durability.leather"));
        registerScaling("CUSTOM_MAX_DURABILITY", MaterialClass.STONE, getScalingFromPath(config, "scaling_durability.stone"));
        registerScaling("CUSTOM_MAX_DURABILITY", MaterialClass.CHAINMAIL, getScalingFromPath(config, "scaling_durability.chainmail"));
        registerScaling("CUSTOM_MAX_DURABILITY", MaterialClass.IRON, getScalingFromPath(config, "scaling_durability.iron"));
        registerScaling("CUSTOM_MAX_DURABILITY", MaterialClass.GOLD, getScalingFromPath(config, "scaling_durability.gold"));
        registerScaling("CUSTOM_MAX_DURABILITY", MaterialClass.DIAMOND, getScalingFromPath(config, "scaling_durability.diamond"));
        registerScaling("CUSTOM_MAX_DURABILITY", MaterialClass.NETHERITE, getScalingFromPath(config, "scaling_durability.netherite"));
        registerScaling("CUSTOM_MAX_DURABILITY", MaterialClass.BOW, getScalingFromPath(config, "scaling_durability.bow"));
        registerScaling("CUSTOM_MAX_DURABILITY", MaterialClass.CROSSBOW, getScalingFromPath(config, "scaling_durability.crossbow"));
        registerScaling("CUSTOM_MAX_DURABILITY", MaterialClass.PRISMARINE, getScalingFromPath(config, "scaling_durability.prismarine"));
        registerScaling("CUSTOM_MAX_DURABILITY", MaterialClass.MEMBRANE, getScalingFromPath(config, "scaling_durability.membrane"));
        registerScaling("CUSTOM_MAX_DURABILITY", MaterialClass.OTHER, getScalingFromPath(config, "scaling_durability.other"));

        registerScaling("GENERIC_ATTACK_DAMAGE", MaterialClass.WOOD, getScalingFromPath(config,"scaling_damage.wood"));
        registerScaling("GENERIC_ATTACK_DAMAGE", MaterialClass.STONE, getScalingFromPath(config,"scaling_damage.stone"));
        registerScaling("GENERIC_ATTACK_DAMAGE", MaterialClass.GOLD, getScalingFromPath(config,"scaling_damage.gold"));
        registerScaling("GENERIC_ATTACK_DAMAGE", MaterialClass.IRON, getScalingFromPath(config,"scaling_damage.iron"));
        registerScaling("GENERIC_ATTACK_DAMAGE", MaterialClass.DIAMOND, getScalingFromPath(config,"scaling_damage.diamond"));
        registerScaling("GENERIC_ATTACK_DAMAGE", MaterialClass.NETHERITE, getScalingFromPath(config,"scaling_damage.netherite"));
        registerScaling("GENERIC_ATTACK_DAMAGE", MaterialClass.PRISMARINE, getScalingFromPath(config,"scaling_damage.prismarine"));
        registerScaling("GENERIC_ATTACK_DAMAGE", MaterialClass.OTHER, getScalingFromPath(config,"scaling_damage.other"));

        registerScaling("CUSTOM_DRAW_STRENGTH", MaterialClass.BOW, getScalingFromPath(config,"scaling_damage.bow"));
        registerScaling("CUSTOM_DRAW_STRENGTH", MaterialClass.CROSSBOW, getScalingFromPath(config,"scaling_damage.crossbow"));

        registerScaling("GENERIC_ATTACK_SPEED", MaterialClass.WOOD, getScalingFromPath(config,"scaling_speed.wood"));
        registerScaling("GENERIC_ATTACK_SPEED", MaterialClass.STONE, getScalingFromPath(config,"scaling_speed.stone"));
        registerScaling("GENERIC_ATTACK_SPEED", MaterialClass.GOLD, getScalingFromPath(config,"scaling_speed.gold"));
        registerScaling("GENERIC_ATTACK_SPEED", MaterialClass.IRON, getScalingFromPath(config,"scaling_speed.iron"));
        registerScaling("GENERIC_ATTACK_SPEED", MaterialClass.DIAMOND, getScalingFromPath(config,"scaling_speed.diamond"));
        registerScaling("GENERIC_ATTACK_SPEED", MaterialClass.NETHERITE, getScalingFromPath(config,"scaling_speed.netherite"));
        registerScaling("GENERIC_ATTACK_SPEED", MaterialClass.PRISMARINE, getScalingFromPath(config,"scaling_speed.prismarine"));
        registerScaling("GENERIC_ATTACK_SPEED", MaterialClass.OTHER, getScalingFromPath(config,"scaling_speed.other"));

        registerScaling("GENERIC_ARMOR", MaterialClass.LEATHER, getScalingFromPath(config,"scaling_armor.leather"));
        registerScaling("GENERIC_ARMOR", MaterialClass.CHAINMAIL, getScalingFromPath(config,"scaling_armor.chainmail"));
        registerScaling("GENERIC_ARMOR", MaterialClass.GOLD, getScalingFromPath(config,"scaling_armor.gold"));
        registerScaling("GENERIC_ARMOR", MaterialClass.IRON, getScalingFromPath(config,"scaling_armor.iron"));
        registerScaling("GENERIC_ARMOR", MaterialClass.DIAMOND, getScalingFromPath(config,"scaling_armor.diamond"));
        registerScaling("GENERIC_ARMOR", MaterialClass.NETHERITE, getScalingFromPath(config,"scaling_armor.netherite"));
        registerScaling("GENERIC_ARMOR", MaterialClass.MEMBRANE, getScalingFromPath(config,"scaling_armor.membrane"));
        registerScaling("GENERIC_ARMOR", MaterialClass.OTHER, getScalingFromPath(config,"scaling_armor.other"));

        registerScaling("GENERIC_ARMOR_TOUGHNESS", MaterialClass.LEATHER, getScalingFromPath(config,"scaling_armor_toughness.leather"));
        registerScaling("GENERIC_ARMOR_TOUGHNESS", MaterialClass.CHAINMAIL, getScalingFromPath(config,"scaling_armor_toughness.chainmail"));
        registerScaling("GENERIC_ARMOR_TOUGHNESS", MaterialClass.GOLD, getScalingFromPath(config,"scaling_armor_toughness.gold"));
        registerScaling("GENERIC_ARMOR_TOUGHNESS", MaterialClass.IRON, getScalingFromPath(config,"scaling_armor_toughness.iron"));
        registerScaling("GENERIC_ARMOR_TOUGHNESS", MaterialClass.DIAMOND, getScalingFromPath(config,"scaling_armor_toughness.diamond"));
        registerScaling("GENERIC_ARMOR_TOUGHNESS", MaterialClass.NETHERITE, getScalingFromPath(config,"scaling_armor_toughness.netherite"));
        registerScaling("GENERIC_ARMOR_TOUGHNESS", MaterialClass.MEMBRANE, getScalingFromPath(config,"scaling_armor_toughness.membrane"));
        registerScaling("GENERIC_ARMOR_TOUGHNESS", MaterialClass.OTHER, getScalingFromPath(config,"scaling_armor_toughness.other"));

        registerScaling("GENERIC_KNOCKBACK_RESISTANCE", MaterialClass.LEATHER, getScalingFromPath(config,"scaling_armor_knockbackresist.leather"));
        registerScaling("GENERIC_KNOCKBACK_RESISTANCE", MaterialClass.CHAINMAIL, getScalingFromPath(config,"scaling_armor_knockbackresist.chainmail"));
        registerScaling("GENERIC_KNOCKBACK_RESISTANCE", MaterialClass.GOLD, getScalingFromPath(config,"scaling_armor_knockbackresist.gold"));
        registerScaling("GENERIC_KNOCKBACK_RESISTANCE", MaterialClass.IRON, getScalingFromPath(config,"scaling_armor_knockbackresist.iron"));
        registerScaling("GENERIC_KNOCKBACK_RESISTANCE", MaterialClass.DIAMOND, getScalingFromPath(config,"scaling_armor_knockbackresist.diamond"));
        registerScaling("GENERIC_KNOCKBACK_RESISTANCE", MaterialClass.NETHERITE, getScalingFromPath(config,"scaling_armor_knockbackresist.netherite"));
        registerScaling("GENERIC_KNOCKBACK_RESISTANCE", MaterialClass.MEMBRANE, getScalingFromPath(config,"scaling_armor_knockbackresist.membrane"));
        registerScaling("GENERIC_KNOCKBACK_RESISTANCE", MaterialClass.OTHER, getScalingFromPath(config,"scaling_armor_knockbackresist.other"));

        registerScaling("GENERIC_MAX_HEALTH", MaterialClass.WOOD, getScalingFromPath(config,"scaling_health.wood"));
        registerScaling("GENERIC_MAX_HEALTH", MaterialClass.LEATHER, getScalingFromPath(config,"scaling_health.leather"));
        registerScaling("GENERIC_MAX_HEALTH", MaterialClass.STONE, getScalingFromPath(config,"scaling_health.stone"));
        registerScaling("GENERIC_MAX_HEALTH", MaterialClass.CHAINMAIL, getScalingFromPath(config,"scaling_health.chainmail"));
        registerScaling("GENERIC_MAX_HEALTH", MaterialClass.GOLD, getScalingFromPath(config,"scaling_health.gold"));
        registerScaling("GENERIC_MAX_HEALTH", MaterialClass.IRON, getScalingFromPath(config,"scaling_health.iron"));
        registerScaling("GENERIC_MAX_HEALTH", MaterialClass.DIAMOND, getScalingFromPath(config,"scaling_health.diamond"));
        registerScaling("GENERIC_MAX_HEALTH", MaterialClass.NETHERITE, getScalingFromPath(config,"scaling_health.netherite"));
        registerScaling("GENERIC_MAX_HEALTH", MaterialClass.BOW, getScalingFromPath(config,"scaling_health.bow"));
        registerScaling("GENERIC_MAX_HEALTH", MaterialClass.CROSSBOW, getScalingFromPath(config,"scaling_health.crossbow"));
        registerScaling("GENERIC_MAX_HEALTH", MaterialClass.PRISMARINE, getScalingFromPath(config,"scaling_health.prismarine"));
        registerScaling("GENERIC_MAX_HEALTH", MaterialClass.MEMBRANE, getScalingFromPath(config,"scaling_health.membrane"));
        registerScaling("GENERIC_MAX_HEALTH", MaterialClass.OTHER, getScalingFromPath(config,"scaling_health.other"));

        registerScaling("GENERIC_MOVEMENT_SPEED", MaterialClass.WOOD, getScalingFromPath(config,"scaling_movement_speed.wood"));
        registerScaling("GENERIC_MOVEMENT_SPEED", MaterialClass.LEATHER, getScalingFromPath(config,"scaling_movement_speed.leather"));
        registerScaling("GENERIC_MOVEMENT_SPEED", MaterialClass.STONE, getScalingFromPath(config,"scaling_movement_speed.stone"));
        registerScaling("GENERIC_MOVEMENT_SPEED", MaterialClass.CHAINMAIL, getScalingFromPath(config,"scaling_movement_speed.chainmail"));
        registerScaling("GENERIC_MOVEMENT_SPEED", MaterialClass.GOLD, getScalingFromPath(config,"scaling_movement_speed.gold"));
        registerScaling("GENERIC_MOVEMENT_SPEED", MaterialClass.IRON, getScalingFromPath(config,"scaling_movement_speed.iron"));
        registerScaling("GENERIC_MOVEMENT_SPEED", MaterialClass.DIAMOND, getScalingFromPath(config,"scaling_movement_speed.diamond"));
        registerScaling("GENERIC_MOVEMENT_SPEED", MaterialClass.NETHERITE, getScalingFromPath(config,"scaling_movement_speed.netherite"));
        registerScaling("GENERIC_MOVEMENT_SPEED", MaterialClass.BOW, getScalingFromPath(config,"scaling_movement_speed.bow"));
        registerScaling("GENERIC_MOVEMENT_SPEED", MaterialClass.CROSSBOW, getScalingFromPath(config,"scaling_movement_speed.crossbow"));
        registerScaling("GENERIC_MOVEMENT_SPEED", MaterialClass.PRISMARINE, getScalingFromPath(config,"scaling_movement_speed.prismarine"));
        registerScaling("GENERIC_MOVEMENT_SPEED", MaterialClass.MEMBRANE, getScalingFromPath(config,"scaling_movement_speed.membrane"));
        registerScaling("GENERIC_MOVEMENT_SPEED", MaterialClass.OTHER, getScalingFromPath(config,"scaling_movement_speed.other"));

        registerScaling("GENERIC_ATTACK_KNOCKBACK", MaterialClass.WOOD, getScalingFromPath(config,"scaling_knockback.wood"));
        registerScaling("GENERIC_ATTACK_KNOCKBACK", MaterialClass.STONE, getScalingFromPath(config,"scaling_knockback.stone"));
        registerScaling("GENERIC_ATTACK_KNOCKBACK", MaterialClass.GOLD, getScalingFromPath(config,"scaling_knockback.gold"));
        registerScaling("GENERIC_ATTACK_KNOCKBACK", MaterialClass.IRON, getScalingFromPath(config,"scaling_knockback.iron"));
        registerScaling("GENERIC_ATTACK_KNOCKBACK", MaterialClass.DIAMOND, getScalingFromPath(config,"scaling_knockback.diamond"));
        registerScaling("GENERIC_ATTACK_KNOCKBACK", MaterialClass.NETHERITE, getScalingFromPath(config,"scaling_knockback.netherite"));
        registerScaling("GENERIC_ATTACK_KNOCKBACK", MaterialClass.PRISMARINE, getScalingFromPath(config,"scaling_knockback.prismarine"));
        registerScaling("GENERIC_ATTACK_KNOCKBACK", MaterialClass.OTHER, getScalingFromPath(config,"scaling_knockback.other"));

        registerScaling("CUSTOM_DAMAGE_RESISTANCE", MaterialClass.WOOD, getScalingFromPath(config,"scaling_damage_resistance.wood"));
        registerScaling("CUSTOM_DAMAGE_RESISTANCE", MaterialClass.LEATHER, getScalingFromPath(config,"scaling_damage_resistance.leather"));
        registerScaling("CUSTOM_DAMAGE_RESISTANCE", MaterialClass.STONE, getScalingFromPath(config,"scaling_damage_resistance.stone"));
        registerScaling("CUSTOM_DAMAGE_RESISTANCE", MaterialClass.CHAINMAIL, getScalingFromPath(config,"scaling_damage_resistance.chainmail"));
        registerScaling("CUSTOM_DAMAGE_RESISTANCE", MaterialClass.GOLD, getScalingFromPath(config,"scaling_damage_resistance.gold"));
        registerScaling("CUSTOM_DAMAGE_RESISTANCE", MaterialClass.IRON, getScalingFromPath(config,"scaling_damage_resistance.iron"));
        registerScaling("CUSTOM_DAMAGE_RESISTANCE", MaterialClass.DIAMOND, getScalingFromPath(config,"scaling_damage_resistance.diamond"));
        registerScaling("CUSTOM_DAMAGE_RESISTANCE", MaterialClass.NETHERITE, getScalingFromPath(config,"scaling_damage_resistance.netherite"));
        registerScaling("CUSTOM_DAMAGE_RESISTANCE", MaterialClass.BOW, getScalingFromPath(config,"scaling_damage_resistance.bow"));
        registerScaling("CUSTOM_DAMAGE_RESISTANCE", MaterialClass.CROSSBOW, getScalingFromPath(config,"scaling_damage_resistance.crossbow"));
        registerScaling("CUSTOM_DAMAGE_RESISTANCE", MaterialClass.PRISMARINE, getScalingFromPath(config,"scaling_damage_resistance.prismarine"));
        registerScaling("CUSTOM_DAMAGE_RESISTANCE", MaterialClass.MEMBRANE, getScalingFromPath(config,"scaling_damage_resistance.membrane"));
        registerScaling("CUSTOM_DAMAGE_RESISTANCE", MaterialClass.OTHER, getScalingFromPath(config,"scaling_damage_resistance.other"));

        registerScaling("CUSTOM_EXPLOSION_RESISTANCE", MaterialClass.WOOD, getScalingFromPath(config,"scaling_explosion_resistance.wood"));
        registerScaling("CUSTOM_EXPLOSION_RESISTANCE", MaterialClass.LEATHER, getScalingFromPath(config,"scaling_explosion_resistance.leather"));
        registerScaling("CUSTOM_EXPLOSION_RESISTANCE", MaterialClass.STONE, getScalingFromPath(config,"scaling_explosion_resistance.stone"));
        registerScaling("CUSTOM_EXPLOSION_RESISTANCE", MaterialClass.CHAINMAIL, getScalingFromPath(config,"scaling_explosion_resistance.chainmail"));
        registerScaling("CUSTOM_EXPLOSION_RESISTANCE", MaterialClass.GOLD, getScalingFromPath(config,"scaling_explosion_resistance.gold"));
        registerScaling("CUSTOM_EXPLOSION_RESISTANCE", MaterialClass.IRON, getScalingFromPath(config,"scaling_explosion_resistance.iron"));
        registerScaling("CUSTOM_EXPLOSION_RESISTANCE", MaterialClass.DIAMOND, getScalingFromPath(config,"scaling_explosion_resistance.diamond"));
        registerScaling("CUSTOM_EXPLOSION_RESISTANCE", MaterialClass.NETHERITE, getScalingFromPath(config,"scaling_explosion_resistance.netherite"));
        registerScaling("CUSTOM_EXPLOSION_RESISTANCE", MaterialClass.BOW, getScalingFromPath(config,"scaling_explosion_resistance.bow"));
        registerScaling("CUSTOM_EXPLOSION_RESISTANCE", MaterialClass.CROSSBOW, getScalingFromPath(config,"scaling_explosion_resistance.crossbow"));
        registerScaling("CUSTOM_EXPLOSION_RESISTANCE", MaterialClass.PRISMARINE, getScalingFromPath(config,"scaling_explosion_resistance.prismarine"));
        registerScaling("CUSTOM_EXPLOSION_RESISTANCE", MaterialClass.MEMBRANE, getScalingFromPath(config,"scaling_explosion_resistance.membrane"));
        registerScaling("CUSTOM_EXPLOSION_RESISTANCE", MaterialClass.OTHER, getScalingFromPath(config,"scaling_explosion_resistance.other"));

        registerScaling("CUSTOM_FIRE_RESISTANCE", MaterialClass.WOOD, getScalingFromPath(config,"scaling_fire_resistance.wood"));
        registerScaling("CUSTOM_FIRE_RESISTANCE", MaterialClass.LEATHER, getScalingFromPath(config,"scaling_fire_resistance.leather"));
        registerScaling("CUSTOM_FIRE_RESISTANCE", MaterialClass.STONE, getScalingFromPath(config,"scaling_fire_resistance.stone"));
        registerScaling("CUSTOM_FIRE_RESISTANCE", MaterialClass.CHAINMAIL, getScalingFromPath(config,"scaling_fire_resistance.chainmail"));
        registerScaling("CUSTOM_FIRE_RESISTANCE", MaterialClass.GOLD, getScalingFromPath(config,"scaling_fire_resistance.gold"));
        registerScaling("CUSTOM_FIRE_RESISTANCE", MaterialClass.IRON, getScalingFromPath(config,"scaling_fire_resistance.iron"));
        registerScaling("CUSTOM_FIRE_RESISTANCE", MaterialClass.DIAMOND, getScalingFromPath(config,"scaling_fire_resistance.diamond"));
        registerScaling("CUSTOM_FIRE_RESISTANCE", MaterialClass.NETHERITE, getScalingFromPath(config,"scaling_fire_resistance.netherite"));
        registerScaling("CUSTOM_FIRE_RESISTANCE", MaterialClass.BOW, getScalingFromPath(config,"scaling_fire_resistance.bow"));
        registerScaling("CUSTOM_FIRE_RESISTANCE", MaterialClass.CROSSBOW, getScalingFromPath(config,"scaling_fire_resistance.crossbow"));
        registerScaling("CUSTOM_FIRE_RESISTANCE", MaterialClass.PRISMARINE, getScalingFromPath(config,"scaling_fire_resistance.prismarine"));
        registerScaling("CUSTOM_FIRE_RESISTANCE", MaterialClass.MEMBRANE, getScalingFromPath(config,"scaling_fire_resistance.membrane"));
        registerScaling("CUSTOM_FIRE_RESISTANCE", MaterialClass.OTHER, getScalingFromPath(config,"scaling_fire_resistance.other"));

        registerScaling("CUSTOM_POISON_RESISTANCE", MaterialClass.WOOD, getScalingFromPath(config,"scaling_poison_resistance.wood"));
        registerScaling("CUSTOM_POISON_RESISTANCE", MaterialClass.LEATHER, getScalingFromPath(config,"scaling_poison_resistance.leather"));
        registerScaling("CUSTOM_POISON_RESISTANCE", MaterialClass.STONE, getScalingFromPath(config,"scaling_poison_resistance.stone"));
        registerScaling("CUSTOM_POISON_RESISTANCE", MaterialClass.CHAINMAIL, getScalingFromPath(config,"scaling_poison_resistance.chainmail"));
        registerScaling("CUSTOM_POISON_RESISTANCE", MaterialClass.GOLD, getScalingFromPath(config,"scaling_poison_resistance.gold"));
        registerScaling("CUSTOM_POISON_RESISTANCE", MaterialClass.IRON, getScalingFromPath(config,"scaling_poison_resistance.iron"));
        registerScaling("CUSTOM_POISON_RESISTANCE", MaterialClass.DIAMOND, getScalingFromPath(config,"scaling_poison_resistance.diamond"));
        registerScaling("CUSTOM_POISON_RESISTANCE", MaterialClass.NETHERITE, getScalingFromPath(config,"scaling_poison_resistance.netherite"));
        registerScaling("CUSTOM_POISON_RESISTANCE", MaterialClass.BOW, getScalingFromPath(config,"scaling_poison_resistance.bow"));
        registerScaling("CUSTOM_POISON_RESISTANCE", MaterialClass.CROSSBOW, getScalingFromPath(config,"scaling_poison_resistance.crossbow"));
        registerScaling("CUSTOM_POISON_RESISTANCE", MaterialClass.PRISMARINE, getScalingFromPath(config,"scaling_poison_resistance.prismarine"));
        registerScaling("CUSTOM_POISON_RESISTANCE", MaterialClass.MEMBRANE, getScalingFromPath(config,"scaling_poison_resistance.membrane"));
        registerScaling("CUSTOM_POISON_RESISTANCE", MaterialClass.OTHER, getScalingFromPath(config,"scaling_poison_resistance.other"));

        registerScaling("CUSTOM_MAGIC_RESISTANCE", MaterialClass.WOOD, getScalingFromPath(config,"scaling_magic_resistance.wood"));
        registerScaling("CUSTOM_MAGIC_RESISTANCE", MaterialClass.LEATHER, getScalingFromPath(config,"scaling_magic_resistance.leather"));
        registerScaling("CUSTOM_MAGIC_RESISTANCE", MaterialClass.STONE, getScalingFromPath(config,"scaling_magic_resistance.stone"));
        registerScaling("CUSTOM_MAGIC_RESISTANCE", MaterialClass.CHAINMAIL, getScalingFromPath(config,"scaling_magic_resistance.chainmail"));
        registerScaling("CUSTOM_MAGIC_RESISTANCE", MaterialClass.GOLD, getScalingFromPath(config,"scaling_magic_resistance.gold"));
        registerScaling("CUSTOM_MAGIC_RESISTANCE", MaterialClass.IRON, getScalingFromPath(config,"scaling_magic_resistance.iron"));
        registerScaling("CUSTOM_MAGIC_RESISTANCE", MaterialClass.DIAMOND, getScalingFromPath(config,"scaling_magic_resistance.diamond"));
        registerScaling("CUSTOM_MAGIC_RESISTANCE", MaterialClass.NETHERITE, getScalingFromPath(config,"scaling_magic_resistance.netherite"));
        registerScaling("CUSTOM_MAGIC_RESISTANCE", MaterialClass.BOW, getScalingFromPath(config,"scaling_magic_resistance.bow"));
        registerScaling("CUSTOM_MAGIC_RESISTANCE", MaterialClass.CROSSBOW, getScalingFromPath(config,"scaling_magic_resistance.crossbow"));
        registerScaling("CUSTOM_MAGIC_RESISTANCE", MaterialClass.PRISMARINE, getScalingFromPath(config,"scaling_magic_resistance.prismarine"));
        registerScaling("CUSTOM_MAGIC_RESISTANCE", MaterialClass.MEMBRANE, getScalingFromPath(config,"scaling_magic_resistance.membrane"));
        registerScaling("CUSTOM_MAGIC_RESISTANCE", MaterialClass.OTHER, getScalingFromPath(config,"scaling_magic_resistance.other"));

        registerScaling("CUSTOM_PROJECTILE_RESISTANCE", MaterialClass.WOOD, getScalingFromPath(config,"scaling_projectile_resistance.wood"));
        registerScaling("CUSTOM_PROJECTILE_RESISTANCE", MaterialClass.LEATHER, getScalingFromPath(config,"scaling_projectile_resistance.leather"));
        registerScaling("CUSTOM_PROJECTILE_RESISTANCE", MaterialClass.STONE, getScalingFromPath(config,"scaling_projectile_resistance.stone"));
        registerScaling("CUSTOM_PROJECTILE_RESISTANCE", MaterialClass.CHAINMAIL, getScalingFromPath(config,"scaling_projectile_resistance.chainmail"));
        registerScaling("CUSTOM_PROJECTILE_RESISTANCE", MaterialClass.GOLD, getScalingFromPath(config,"scaling_projectile_resistance.gold"));
        registerScaling("CUSTOM_PROJECTILE_RESISTANCE", MaterialClass.IRON, getScalingFromPath(config,"scaling_projectile_resistance.iron"));
        registerScaling("CUSTOM_PROJECTILE_RESISTANCE", MaterialClass.DIAMOND, getScalingFromPath(config,"scaling_projectile_resistance.diamond"));
        registerScaling("CUSTOM_PROJECTILE_RESISTANCE", MaterialClass.NETHERITE, getScalingFromPath(config,"scaling_projectile_resistance.netherite"));
        registerScaling("CUSTOM_PROJECTILE_RESISTANCE", MaterialClass.BOW, getScalingFromPath(config,"scaling_projectile_resistance.bow"));
        registerScaling("CUSTOM_PROJECTILE_RESISTANCE", MaterialClass.CROSSBOW, getScalingFromPath(config,"scaling_projectile_resistance.crossbow"));
        registerScaling("CUSTOM_PROJECTILE_RESISTANCE", MaterialClass.PRISMARINE, getScalingFromPath(config,"scaling_projectile_resistance.prismarine"));
        registerScaling("CUSTOM_PROJECTILE_RESISTANCE", MaterialClass.MEMBRANE, getScalingFromPath(config,"scaling_projectile_resistance.membrane"));
        registerScaling("CUSTOM_PROJECTILE_RESISTANCE", MaterialClass.OTHER, getScalingFromPath(config,"scaling_projectile_resistance.other"));

        repairScaling = config.getString("scaling_repair");

        ConfigurationSection sectionCosmetic = config.getConfigurationSection("quality_cosmetic");
        if (sectionCosmetic != null){
            for (String rating : sectionCosmetic.getKeys(false)){
                try {
                    int intRating = Integer.parseInt(rating);
                    String quality = config.getString("quality_cosmetic." + rating);
                    if (quality == null) throw new IllegalArgumentException();
                    cosmeticQualityModifiers.put(intRating, Utils.chat(quality));
                } catch (IllegalArgumentException ignored){
                }
            }
        }

        ConfigurationSection sectionTreatments = config.getConfigurationSection("treatment_lore");
        if (sectionTreatments != null){
            for (String t : sectionTreatments.getKeys(false)){
                try {
                    ItemTreatment treatment = ItemTreatment.valueOf(t);
                    String name = config.getString("treatment_lore." + t);
                    if (name == null) throw new IllegalArgumentException();
                    treatmentTranslations.put(treatment, Utils.chat(name));
                } catch (IllegalArgumentException ignored){
                }
            }
        }
    }

    public Scaling getScaling(Material m, String type){
        MaterialClass materialClass = MaterialClass.getMatchingClass(m);
        if (materialScalings.containsKey(type)){
            if (materialClass != null){
                if (materialScalings.get(type).containsKey(materialClass)){
                    return materialScalings.get(type).get(materialClass);
                }
            }
        } else if (globalScalings.containsKey(type)){
            return globalScalings.get(type);
        }
        return null;
    }

    public String getRepairScaling() {
        return repairScaling;
    }

    public boolean hasTreatment(ItemStack i, ItemTreatment t){
        return getItemsTreatments(i).contains(t);
    }

    /**
     * Updates an ItemStack to apply all the lore of all ItemTreatments it has, if the ItemTreatment has lore associated
     * with it in treatmentTranslations.
     * @param i the ItemStack to update
     */
    public void setTreatmentLore(ItemStack i){
        if (i == null) return;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return;
        List<String> lore;
        List<String> finalLore = new ArrayList<>();
        if (meta.hasLore()){
            assert meta.getLore() != null;
            lore = new ArrayList<>(meta.getLore());
        } else {
            lore = new ArrayList<>();
        }
        //if the lore contains an enchantment it's not supposed to have, it is removed
        int firstTreatmentIndex = 0; //This'll track where in the lore the last enchant is located,
        // so that once enchants are added back to it no other lore has to be removed
        boolean treatmentAtLine0 = false;
        for (String l : lore){
            if (treatmentTranslations.inverse().get(l) != null){
                if (firstTreatmentIndex == 0) {
                    firstTreatmentIndex = lore.indexOf(l);
                    if (firstTreatmentIndex == 0) treatmentAtLine0 = true;
                }
            } else {
                finalLore.add(l);
            }
        }
        Collection<ItemTreatment> itemsTreatments = (hideTreatmentLore) ? new HashSet<>() : getItemsTreatments(i);
        if (finalLore.size() == 0){
            for (ItemTreatment treatment : itemsTreatments){
                String treatmentLore = treatmentTranslations.get(treatment);
                if (treatmentLore != null){
                    finalLore.add(treatmentLore);
                }
            }
        } else {
            if (firstTreatmentIndex >= finalLore.size()){
                for (ItemTreatment treatment : itemsTreatments){
                    String treatmentLore = treatmentTranslations.get(treatment);
                    if (treatmentLore != null){
                        finalLore.add(treatmentLore);
                    }
                }
            } else if (treatmentAtLine0 || firstTreatmentIndex != 0){
                for (ItemTreatment treatment : itemsTreatments){
                    String treatmentLore = treatmentTranslations.get(treatment);
                    if (treatmentLore != null){
                        finalLore.add(firstTreatmentIndex, treatmentLore);
                    }
                }
            } else {
                for (ItemTreatment treatment : itemsTreatments){
                    String treatmentLore = treatmentTranslations.get(treatment);
                    if (treatmentLore != null){
                        finalLore.add(treatmentLore);
                    }
                }
            }
        }
        meta.setLore(finalLore);
        i.setItemMeta(meta);
    }

    /**
     * Updates an ItemStack to apply the lore appropriate to its quality, if the quality has lore associated
     * with it in cosmeticQualityModifiers.
     * @param i the ItemStack to update
     */
    public void setQualityLore(ItemStack i){
        if (i == null) return;
        ItemMeta meta = i.getItemMeta();
        if (meta == null) return;
        List<String> lore;
        List<String> finalLore = new ArrayList<>();
        if (meta.hasLore()){
            assert meta.getLore() != null;
            lore = new ArrayList<>(meta.getLore());
        } else {
            lore = new ArrayList<>();
        }
        //if the lore contains an enchantment it's not supposed to have, it is removed
        int qualityIndex = 0; //This'll track where in the lore the last enchant is located,
        // so that once enchants are added back to it no other lore has to be removed
        boolean qualityAtLine0 = false;
        for (String l : lore){
            if (cosmeticQualityModifiers.containsValue(l)){
                if (qualityIndex == 0) {
                    qualityIndex = lore.indexOf(l);
                    if (qualityIndex == 0) qualityAtLine0 = true;
                }
            } else {
                finalLore.add(l);
            }
        }
        String qualityLore = getCosmeticQualityModifier(getItemsQuality(i));
        if (qualityLore != null){
            if (finalLore.size() == 0){
                finalLore.add(qualityLore);
            } else {
                if (qualityIndex >= finalLore.size()){
                    finalLore.add(qualityLore);
                } else if (qualityAtLine0 || qualityIndex != 0){
                    finalLore.add(qualityIndex, qualityLore);
                } else {
                    finalLore.add(qualityLore);
                }
            }
        }
        meta.setLore(finalLore);
        i.setItemMeta(meta);
    }

    /**
     * Get the string lore related to an amount of quality points
     * @param i the integer amount of quality points
     * @return the string lore quality modifier matching the range of quality points
     */
    public String getCosmeticQualityModifier(int i){
        String currentLore = null;
        for (int q : cosmeticQualityModifiers.keySet()){
            if (q <= i) currentLore = cosmeticQualityModifiers.get(q);
        }
        return currentLore;
    }

    /**
     * Get all the ItemTreatments of an ItemStack
     * @param i the ItemStack to get ItemTreatments from
     * @return a collection of ItemTreatments
     */
    public Collection<ItemTreatment> getItemsTreatments(ItemStack i){
        Set<ItemTreatment> itemsTreatments = new HashSet<>();
        if (i == null) return itemsTreatments;
        if (i.getItemMeta() == null) return itemsTreatments;
        if (i.getItemMeta().getPersistentDataContainer().has(key_treatment, PersistentDataType.STRING)){
            String[] treatments = i.getItemMeta().getPersistentDataContainer().get(key_treatment, PersistentDataType.STRING).split(";");
            for (String treatment : treatments){
                try {
                    itemsTreatments.add(ItemTreatment.valueOf(treatment));
                } catch (IllegalArgumentException ignored){
                }
            }
        }
        return itemsTreatments;
    }

    /**
     * @param i the ItemStack to get its quality from
     * @return the amount of quality points the given ItemStack has, or 0 if the item or its meta is null, or if the
     * item doesn't have the appropriate NamespacedKey
     */
    public int getItemsQuality(ItemStack i){
        if (i == null) return 0;
        if (i.getItemMeta() == null) return 0;
        ItemMeta meta = i.getItemMeta();
        if (meta.getPersistentDataContainer().has(key_quality, PersistentDataType.INTEGER)){
            return meta.getPersistentDataContainer().get(key_quality, PersistentDataType.INTEGER);
        }
        return 0;
    }

    /**
     * Returns true if the item has any of the following keys in its PersistentDataContainer: key_quality, key_treatment,
     * key_durability, key_max_durability
     * @param i the ItemStack to check if it's custom
     * @return true if custom, false if essentially a vanilla item
     */
    public boolean isItemCustom(ItemStack i){
        if (i == null) return false;
        if (i.getItemMeta() == null) return false;
        if (i.getItemMeta().getPersistentDataContainer().has(key_quality, PersistentDataType.INTEGER)){
            return true;
        }
        if (i.getItemMeta().getPersistentDataContainer().has(key_treatment, PersistentDataType.STRING)){
            return true;
        }
        if (i.getItemMeta().getPersistentDataContainer().has(CustomDurabilityManager.getInstance().getKey_durability(), PersistentDataType.INTEGER)){
            return true;
        }
        if (i.getItemMeta().getPersistentDataContainer().has(PotionTreatmentManager.getInstance().getKey_brewing_treatment(), PersistentDataType.STRING)){
            return true;
        }
        if (i.getItemMeta().getPersistentDataContainer().has(PotionTreatmentManager.getInstance().getKey_quality_general(), PersistentDataType.INTEGER)){
            return true;
        }
        return i.getItemMeta().getPersistentDataContainer().has(CustomDurabilityManager.getInstance().getKey_max_durability(), PersistentDataType.INTEGER);
    }

    /**
     * Sets an item's ItemTreatments and updates its lore accordingly
     * Treatments are intended to only be applicable once, unless later removed.
     * @param i the ItemStack to set its ItemTreatments
     * @param treatments the ItemTreatments to assign to the item, if null it removes all treatments.
     */
    public void setItemsTreatments(ItemStack i, Collection<ItemTreatment> treatments){
        if (i == null) return;
        if (i.getItemMeta() == null) return;
        if (treatments == null) {
            i.getItemMeta().getPersistentDataContainer().remove(key_treatment);
        } else {
            ItemMeta meta = i.getItemMeta();
            String treatmentString = treatments.stream().map(ItemTreatment::toString).collect(Collectors.joining(";"));
            meta.getPersistentDataContainer().set(key_treatment, PersistentDataType.STRING,
                    treatmentString);
            i.setItemMeta(meta);
        }
        setTreatmentLore(i);
    }

    /**
     * Sets an item's crafting quality
     * @param i the ItemStack to change its quality
     * @param quality the amount of quality points to assign to the ItemStack
     */
    public void setItemsQuality(ItemStack i, int quality){
        if (i == null) return;
        if (i.getItemMeta() == null) return;
        ItemMeta meta = i.getItemMeta();
        meta.getPersistentDataContainer().set(key_quality, PersistentDataType.INTEGER, quality);
        i.setItemMeta(meta);
        setQualityLore(i);
    }

    /**
     * Sets an item's attribute scaling, will not do anything if the item by default does not have the attribute.
     * @param i the ItemStack to update its attribute scaling strength
     * @param quality the amount of quality points to use with the material's attribute scaling
     */
    public void applyAttributeScaling(ItemStack i, int quality, String attribute){
        if (i == null) {
            return;
        }
        Scaling scaling = getScaling(i.getType(), attribute);
        if (scaling == null) {
            return;
        }
        if (i.getItemMeta() == null) {
            return;
        }
        if (ItemAttributesManager.getInstance().getCurrentStats(i).isEmpty()){
            ItemAttributesManager.getInstance().applyVanillaStats(i);
        }
        AttributeWrapper a = ItemAttributesManager.getInstance().getDefaultStat(i, attribute);
        if (a != null){
            double defaultAttributeStat = a.getAmount();
            try {
                double scalingResult = Utils.eval(scaling.getScaling().replace("%rating%", "" + quality));
                double finalResult = 0;
                if (scaling.getScalingType() == ScalingMode.MULTIPLIER){
                    finalResult = Utils.round(defaultAttributeStat * scalingResult, 3);
                } else if (scaling.getScalingType() == ScalingMode.ADD_ON_DEFAULT){
                    finalResult = Utils.round(defaultAttributeStat + scalingResult, 3);
                }
                ItemAttributesManager.getInstance().setAttributeStrength(i, attribute, finalResult);
            } catch (RuntimeException e){
                System.out.println("Attempting to parse formula " + scaling + ", but something went wrong. ");
                e.printStackTrace();
            }
        }
    }

    public static ItemTreatmentManager getInstance(){
        if (manager == null){
            manager = new ItemTreatmentManager();
        }
        return manager;
    }
}
