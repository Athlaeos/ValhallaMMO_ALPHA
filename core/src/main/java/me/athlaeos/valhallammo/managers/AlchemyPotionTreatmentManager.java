package me.athlaeos.valhallammo.managers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Scaling;
import me.athlaeos.valhallammo.dom.ScalingMode;
import me.athlaeos.valhallammo.items.PotionTreatment;
import me.athlaeos.valhallammo.items.potioneffectwrappers.PotionEffectWrapper;
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

public class AlchemyPotionTreatmentManager {
    private static AlchemyPotionTreatmentManager manager = null;

    private final NamespacedKey key_brewing_treatment = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_brewing_treatments");
    private final NamespacedKey key_quality_general = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_potion_quality");

    private BiMap<PotionTreatment, String> treatmentTranslations;
    private Map<Integer, String> cosmeticQualityModifiers;

    private boolean hideTreatmentLore;

    private Map<Type, Map<String, Scaling>> potionEffectScalings;

    /**
     * Registers a potion effect's quality scalings for a material class. The potion effect is a string as it can represent
     * vanilla potion effects such as FIRE_RESISTANCE or custom potion effects such as FORTIFY_ENCHANTING. Duplicates will be
     * overwritten.
     * @param potionEffect the potion effect to set its quality scalings for
     * @param scaling the quality scaling formula for the material
     */
    public void registerScaling(Type type, String potionEffect, Scaling scaling){
        if (potionEffect == null || scaling == null || type == null) return;
        Map<String, Scaling> existingScalings = new HashMap<>();
        if (potionEffectScalings.containsKey(type)){
            existingScalings = potionEffectScalings.get(type);
        }
        existingScalings.put(potionEffect, scaling);
        potionEffectScalings.put(type, existingScalings);
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

    public AlchemyPotionTreatmentManager(){
        loadConfig();
    }

    public void loadConfig(){
        treatmentTranslations = HashBiMap.create();
        cosmeticQualityModifiers = new TreeMap<>();
        potionEffectScalings = new HashMap<>();
        YamlConfiguration config = ConfigManager.getInstance().getConfig("skill_alchemy.yml").get();

        hideTreatmentLore = config.getBoolean("hide_treatment_lore");

        registerScaling(Type.DURATION, "REGENERATION", getScalingFromPath(config, "scaling_duration.regeneration"));
        registerScaling(Type.DURATION, "DAMAGE_RESISTANCE", getScalingFromPath(config, "scaling_duration.resistance"));
        registerScaling(Type.DURATION, "FAST_DIGGING", getScalingFromPath(config, "scaling_duration.haste"));
        registerScaling(Type.DURATION, "FIRE_RESISTANCE", getScalingFromPath(config, "scaling_duration.fire_resistance"));
        registerScaling(Type.DURATION, "HEALTH_BOOST", getScalingFromPath(config, "scaling_duration.health_boost"));
        registerScaling(Type.DURATION, "INCREASE_DAMAGE", getScalingFromPath(config, "scaling_duration.strength"));
        registerScaling(Type.DURATION, "JUMP", getScalingFromPath(config, "scaling_duration.jump_boost"));
        registerScaling(Type.DURATION, "LUCK", getScalingFromPath(config, "scaling_duration.luck"));
        registerScaling(Type.DURATION, "NIGHT_VISION", getScalingFromPath(config, "scaling_duration.night_vision"));
        registerScaling(Type.DURATION, "SATURATION", getScalingFromPath(config, "scaling_duration.saturation"));
        registerScaling(Type.DURATION, "SPEED", getScalingFromPath(config, "scaling_duration.speed_boost"));
        registerScaling(Type.DURATION, "WATER_BREATHING", getScalingFromPath(config, "scaling_duration.water_breathing"));
        registerScaling(Type.DURATION, "CONDUIT_POWER", getScalingFromPath(config, "scaling_duration.conduit_power"));
        registerScaling(Type.DURATION, "HERO_OF_THE_VILLAGE", getScalingFromPath(config, "scaling_duration.hero_of_the_village"));
        registerScaling(Type.DURATION, "WITHER", getScalingFromPath(config, "scaling_duration.wither"));
        registerScaling(Type.DURATION, "UNLUCK", getScalingFromPath(config, "scaling_duration.bad_luck"));
        registerScaling(Type.DURATION, "WEAKNESS", getScalingFromPath(config, "scaling_duration.weakness"));
        registerScaling(Type.DURATION, "SLOW_DIGGING", getScalingFromPath(config, "scaling_duration.mining_fatigue"));
        registerScaling(Type.DURATION, "SLOW", getScalingFromPath(config, "scaling_duration.slowness"));
        registerScaling(Type.DURATION, "HUNGER", getScalingFromPath(config, "scaling_duration.hunger"));
        registerScaling(Type.DURATION, "CONFUSION", getScalingFromPath(config, "scaling_duration.nausea"));
        registerScaling(Type.DURATION, "BLINDNESS", getScalingFromPath(config, "scaling_duration.blindness"));
        registerScaling(Type.DURATION, "POISON", getScalingFromPath(config, "scaling_duration.poison"));
        registerScaling(Type.DURATION, "BAD_OMEN", getScalingFromPath(config, "scaling_duration.bad_omen"));


        registerScaling(Type.DURATION, "ALCHEMY_INGREDIENT_SAVE", getScalingFromPath(config, "scaling_duration.alchemy_ingredient_save"));
        registerScaling(Type.DURATION, "ALCHEMY_BREW_SPEED", getScalingFromPath(config, "scaling_duration.alchemy_speed"));

        registerScaling(Type.DURATION, "FORTIFY_ENCHANTING", getScalingFromPath(config, "scaling_duration.fortify_enchanting"));
        registerScaling(Type.DURATION, "FORTIFY_SMITHING", getScalingFromPath(config, "scaling_duration.fortify_smithing"));
        registerScaling(Type.DURATION, "FORTIFY_MINING", getScalingFromPath(config, "scaling_duration.fortify_mining"));
        registerScaling(Type.DURATION, "FORTIFY_FARMING", getScalingFromPath(config, "scaling_duration.fortify_farming"));
        registerScaling(Type.DURATION, "FORTIFY_WOODCUTTING", getScalingFromPath(config, "scaling_duration.fortify_woodcutting"));
        registerScaling(Type.DURATION, "FORTIFY_ACROBATICS", getScalingFromPath(config, "scaling_duration.fortify_acrobatics"));
        registerScaling(Type.DURATION, "FORTIFY_TRADING", getScalingFromPath(config, "scaling_duration.fortify_trading"));

        registerScaling(Type.DURATION, "ARCHERY_ACCURACY", getScalingFromPath(config, "scaling_duration.archery_accuracy"));
        registerScaling(Type.DURATION, "ARCHERY_DAMAGE", getScalingFromPath(config, "scaling_duration.archery_damage"));
        registerScaling(Type.DURATION, "ARCHERY_AMMO_SAVE", getScalingFromPath(config, "scaling_duration.archery_ammo_save"));
        registerScaling(Type.DURATION, "DAMAGE_WEAPONS", getScalingFromPath(config, "scaling_duration.damage_weapons"));
        registerScaling(Type.DURATION, "DAMAGE_UNARMED", getScalingFromPath(config, "scaling_duration.damage_unarmed"));

        registerScaling(Type.DURATION, "INCREASE_EXP", getScalingFromPath(config, "scaling_duration.increase_exp_skill"));
        registerScaling(Type.DURATION, "INCREASE_VANILLA_EXP", getScalingFromPath(config, "scaling_duration.increase_exp_vanilla"));

        registerScaling(Type.DURATION, "POISON_ANTI_HEAL", getScalingFromPath(config, "scaling_duration.poison_anti_heal"));
        registerScaling(Type.DURATION, "POISON_VULNERABLE", getScalingFromPath(config, "scaling_duration.poison_vulnerable"));



        registerScaling(Type.AMPLIFIER, "HEAL", getScalingFromPath(config, "scaling_amplifier.instant_health"));
        registerScaling(Type.AMPLIFIER, "HARM", getScalingFromPath(config, "scaling_amplifier.instant_damage"));

        registerScaling(Type.AMPLIFIER, "REGENERATION", getScalingFromPath(config, "scaling_amplifier.regeneration"));
        registerScaling(Type.AMPLIFIER, "DAMAGE_RESISTANCE", getScalingFromPath(config, "scaling_amplifier.resistance"));
        registerScaling(Type.AMPLIFIER, "FAST_DIGGING", getScalingFromPath(config, "scaling_amplifier.haste"));
        registerScaling(Type.AMPLIFIER, "HEALTH_BOOST", getScalingFromPath(config, "scaling_amplifier.health_boost"));
        registerScaling(Type.AMPLIFIER, "INCREASE_DAMAGE", getScalingFromPath(config, "scaling_amplifier.strength"));
        registerScaling(Type.AMPLIFIER, "JUMP", getScalingFromPath(config, "scaling_amplifier.jump_boost"));
        registerScaling(Type.AMPLIFIER, "LUCK", getScalingFromPath(config, "scaling_amplifier.luck"));
        registerScaling(Type.AMPLIFIER, "SATURATION", getScalingFromPath(config, "scaling_amplifier.saturation"));
        registerScaling(Type.AMPLIFIER, "SPEED", getScalingFromPath(config, "scaling_amplifier.speed_boost"));
        registerScaling(Type.AMPLIFIER, "HERO_OF_THE_VILLAGE", getScalingFromPath(config, "scaling_amplifier.hero_of_the_village"));
        registerScaling(Type.AMPLIFIER, "WITHER", getScalingFromPath(config, "scaling_amplifier.wither"));
        registerScaling(Type.AMPLIFIER, "UNLUCK", getScalingFromPath(config, "scaling_amplifier.bad_luck"));
        registerScaling(Type.AMPLIFIER, "WEAKNESS", getScalingFromPath(config, "scaling_amplifier.weakness"));
        registerScaling(Type.AMPLIFIER, "SLOW_DIGGING", getScalingFromPath(config, "scaling_amplifier.mining_fatigue"));
        registerScaling(Type.AMPLIFIER, "SLOW", getScalingFromPath(config, "scaling_amplifier.slowness"));
        registerScaling(Type.AMPLIFIER, "HUNGER", getScalingFromPath(config, "scaling_amplifier.hunger"));
        registerScaling(Type.AMPLIFIER, "POISON", getScalingFromPath(config, "scaling_amplifier.poison"));
        registerScaling(Type.AMPLIFIER, "BAD_OMEN", getScalingFromPath(config, "scaling_amplifier.bad_omen"));


        registerScaling(Type.DURATION, "ALCHEMY_INGREDIENT_SAVE", getScalingFromPath(config, "scaling_duration.alchemy_ingredient_save"));
        registerScaling(Type.DURATION, "ALCHEMY_POTION_SAVE", getScalingFromPath(config, "scaling_duration.alchemy_potion_save"));
        registerScaling(Type.DURATION, "ALCHEMY_BREW_SPEED", getScalingFromPath(config, "scaling_duration.alchemy_speed"));
        registerScaling(Type.DURATION, "ALCHEMY_POTION_VELOCITY", getScalingFromPath(config, "scaling_duration.alchemy_potion_velocity"));

        registerScaling(Type.DURATION, "MASTERPIECE_SMITHING", getScalingFromPath(config, "scaling_duration.masterpiece_smithing"));
        registerScaling(Type.DURATION, "MASTERPIECE_ENCHANTING", getScalingFromPath(config, "scaling_duration.masterpiece_enchanting"));
        registerScaling(Type.DURATION, "MASTERPIECE_ALCHEMY", getScalingFromPath(config, "scaling_duration.masterpiece_alchemy"));
        registerScaling(Type.DURATION, "FORTIFY_ENCHANTING", getScalingFromPath(config, "scaling_duration.fortify_enchanting"));
        registerScaling(Type.DURATION, "FORTIFY_SMITHING", getScalingFromPath(config, "scaling_duration.fortify_smithing"));
        registerScaling(Type.DURATION, "MINING_EXTRA_DROPS", getScalingFromPath(config, "scaling_duration.mining_extra_drops"));
        registerScaling(Type.DURATION, "MINING_RARE_DROPS", getScalingFromPath(config, "scaling_duration.mining_rare_drops"));
        registerScaling(Type.DURATION, "FARMING_EXTRA_DROPS", getScalingFromPath(config, "scaling_duration.farming_extra_drops"));
        registerScaling(Type.DURATION, "FARMING_RARE_DROPS", getScalingFromPath(config, "scaling_duration.farming_rare_drops"));
        registerScaling(Type.DURATION, "FARMING_FISHING_TIER", getScalingFromPath(config, "scaling_duration.farming_fishing_tier"));
        registerScaling(Type.DURATION, "WOODCUTTING_EXTRA_DROPS", getScalingFromPath(config, "scaling_duration.woodcutting_extra_drops"));
        registerScaling(Type.DURATION, "WOODCUTTING_RARE_DROPS", getScalingFromPath(config, "scaling_duration.woodcutting_rare_drops"));
        registerScaling(Type.DURATION, "FORTIFY_ACROBATICS", getScalingFromPath(config, "scaling_duration.fortify_acrobatics"));
        registerScaling(Type.DURATION, "FORTIFY_TRADING", getScalingFromPath(config, "scaling_duration.fortify_trading"));

        registerScaling(Type.DURATION, "ARCHERY_ACCURACY", getScalingFromPath(config, "scaling_duration.archery_accuracy"));
        registerScaling(Type.DURATION, "ARCHERY_DAMAGE", getScalingFromPath(config, "scaling_duration.archery_damage"));
        registerScaling(Type.DURATION, "ARCHERY_AMMO_SAVE", getScalingFromPath(config, "scaling_duration.archery_ammo_save"));
        registerScaling(Type.DURATION, "WEAPONS_DAMAGE", getScalingFromPath(config, "scaling_duration.damage_weapons"));
        registerScaling(Type.DURATION, "UNARMED_DAMAGE", getScalingFromPath(config, "scaling_duration.damage_unarmed"));

        registerScaling(Type.DURATION, "INCREASE_EXP", getScalingFromPath(config, "scaling_duration.increase_exp_skill"));
        registerScaling(Type.DURATION, "INCREASE_VANILLA_EXP", getScalingFromPath(config, "scaling_duration.increase_exp_vanilla"));

        registerScaling(Type.DURATION, "POISON_ANTI_HEAL", getScalingFromPath(config, "scaling_duration.poison_anti_heal"));
        registerScaling(Type.DURATION, "POISON_VULNERABLE", getScalingFromPath(config, "scaling_duration.poison_vulnerable"));


        registerScaling(Type.AMPLIFIER, "ALCHEMY_INGREDIENT_SAVE", getScalingFromPath(config, "scaling_amplifier.alchemy_ingredient_save"));
        registerScaling(Type.AMPLIFIER, "ALCHEMY_POTION_SAVE", getScalingFromPath(config, "scaling_amplifier.alchemy_potion_save"));
        registerScaling(Type.AMPLIFIER, "ALCHEMY_BREW_SPEED", getScalingFromPath(config, "scaling_amplifier.alchemy_speed"));
        registerScaling(Type.AMPLIFIER, "ALCHEMY_POTION_VELOCITY", getScalingFromPath(config, "scaling_amplifier.alchemy_potion_velocity"));

        registerScaling(Type.AMPLIFIER, "MASTERPIECE_SMITHING", getScalingFromPath(config, "scaling_amplifier.masterpiece_smithing"));
        registerScaling(Type.AMPLIFIER, "MASTERPIECE_ENCHANTING", getScalingFromPath(config, "scaling_amplifier.masterpiece_enchanting"));
        registerScaling(Type.AMPLIFIER, "MASTERPIECE_ALCHEMY", getScalingFromPath(config, "scaling_amplifier.masterpiece_alchemy"));
        registerScaling(Type.AMPLIFIER, "FORTIFY_ENCHANTING", getScalingFromPath(config, "scaling_amplifier.fortify_enchanting"));
        registerScaling(Type.AMPLIFIER, "FORTIFY_SMITHING", getScalingFromPath(config, "scaling_amplifier.fortify_smithing"));
        registerScaling(Type.AMPLIFIER, "MINING_EXTRA_DROPS", getScalingFromPath(config, "scaling_amplifier.mining_extra_drops"));
        registerScaling(Type.AMPLIFIER, "MINING_RARE_DROPS", getScalingFromPath(config, "scaling_amplifier.mining_rare_drops"));
        registerScaling(Type.AMPLIFIER, "FARMING_EXTRA_DROPS", getScalingFromPath(config, "scaling_amplifier.farming_extra_drops"));
        registerScaling(Type.AMPLIFIER, "FARMING_RARE_DROPS", getScalingFromPath(config, "scaling_amplifier.farming_rare_drops"));
        registerScaling(Type.AMPLIFIER, "FARMING_FISHING_TIER", getScalingFromPath(config, "scaling_amplifier.farming_fishing_tier"));
        registerScaling(Type.AMPLIFIER, "WOODCUTTING_EXTRA_DROPS", getScalingFromPath(config, "scaling_amplifier.woodcutting_extra_drops"));
        registerScaling(Type.AMPLIFIER, "WOODCUTTING_RARE_DROPS", getScalingFromPath(config, "scaling_amplifier.woodcutting_rare_drops"));
        registerScaling(Type.AMPLIFIER, "FORTIFY_ACROBATICS", getScalingFromPath(config, "scaling_amplifier.fortify_acrobatics"));
        registerScaling(Type.AMPLIFIER, "FORTIFY_TRADING", getScalingFromPath(config, "scaling_amplifier.fortify_trading"));

        registerScaling(Type.AMPLIFIER, "ARCHERY_ACCURACY", getScalingFromPath(config, "scaling_amplifier.archery_accuracy"));
        registerScaling(Type.AMPLIFIER, "ARCHERY_DAMAGE", getScalingFromPath(config, "scaling_amplifier.archery_damage"));
        registerScaling(Type.AMPLIFIER, "ARCHERY_AMMO_SAVE", getScalingFromPath(config, "scaling_amplifier.archery_ammo_save"));
        registerScaling(Type.AMPLIFIER, "WEAPONS_DAMAGE", getScalingFromPath(config, "scaling_amplifier.damage_weapons"));
        registerScaling(Type.AMPLIFIER, "UNARMED_DAMAGE", getScalingFromPath(config, "scaling_amplifier.damage_unarmed"));

        registerScaling(Type.AMPLIFIER, "INCREASE_EXP", getScalingFromPath(config, "scaling_amplifier.increase_exp"));

        registerScaling(Type.AMPLIFIER, "POISON_ANTI_HEAL", getScalingFromPath(config, "scaling_amplifier.poison_anti_heal"));
        registerScaling(Type.AMPLIFIER, "POISON_VULNERABLE", getScalingFromPath(config, "scaling_amplifier.poison_vulnerable"));

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
                    PotionTreatment treatment = PotionTreatment.valueOf(t);
                    String name = config.getString("treatment_lore." + t);
                    if (name == null) throw new IllegalArgumentException();
                    treatmentTranslations.put(treatment, Utils.chat(name));
                } catch (IllegalArgumentException ignored){
                }
            }
        }
    }

    public void reload(){
        manager = null;
        getInstance();
    }

    public Scaling getScaling(Type type, String potionEffect){
        if (potionEffectScalings.containsKey(type)){
            return potionEffectScalings.get(type).get(potionEffect);
        } else {
            return null;
        }
    }

    public boolean hasTreatment(ItemStack i, PotionTreatment t){
        return getPotionTreatments(i).contains(t);
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
        Collection<PotionTreatment> itemsTreatments = (hideTreatmentLore) ? new HashSet<>() : getPotionTreatments(i);
        if (finalLore.size() == 0){
            for (PotionTreatment treatment : itemsTreatments){
                String treatmentLore = treatmentTranslations.get(treatment);
                if (treatmentLore != null){
                    finalLore.add(treatmentLore);
                }
            }
        } else {
            if (firstTreatmentIndex >= finalLore.size()){
                for (PotionTreatment treatment : itemsTreatments){
                    String treatmentLore = treatmentTranslations.get(treatment);
                    if (treatmentLore != null){
                        finalLore.add(treatmentLore);
                    }
                }
            } else if (treatmentAtLine0 || firstTreatmentIndex != 0){
                for (PotionTreatment treatment : itemsTreatments){
                    String treatmentLore = treatmentTranslations.get(treatment);
                    if (treatmentLore != null){
                        finalLore.add(firstTreatmentIndex, treatmentLore);
                    }
                }
            } else {
                for (PotionTreatment treatment : itemsTreatments){
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
        String qualityLore = getCosmeticQualityModifier(getPotionQuality(i));
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
    public Collection<PotionTreatment> getPotionTreatments(ItemStack i){
        Set<PotionTreatment> itemsTreatments = new HashSet<>();
        if (i == null) return itemsTreatments;
        if (i.getItemMeta() == null) return itemsTreatments;
        if (i.getItemMeta().getPersistentDataContainer().has(key_brewing_treatment, PersistentDataType.STRING)){
            String[] treatments = i.getItemMeta().getPersistentDataContainer().get(key_brewing_treatment, PersistentDataType.STRING).split(";");
            for (String treatment : treatments){
                try {
                    itemsTreatments.add(PotionTreatment.valueOf(treatment));
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
    public int getPotionQuality(ItemStack i){
        if (i == null) return 0;
        if (i.getItemMeta() == null) return 0;
        ItemMeta meta = i.getItemMeta();
        if (meta.getPersistentDataContainer().has(key_quality_general, PersistentDataType.INTEGER)){
            return meta.getPersistentDataContainer().get(key_quality_general, PersistentDataType.INTEGER);
        }
        return 0;
    }

    /**
     * Returns true if the item has any of the following keys in its PersistentDataContainer: key_quality, key_treatment,
     * key_durability, key_max_durability
     * @param i the ItemStack to check if it's custom
     * @return true if custom, false if essentially a vanilla item
     */
    public boolean isPotionCustom(ItemStack i){
        if (i == null) return false;
        if (i.getItemMeta() == null) return false;
        if (i.getItemMeta().getPersistentDataContainer().has(key_quality_general, PersistentDataType.INTEGER)){
            return true;
        }
        if (i.getItemMeta().getPersistentDataContainer().has(key_brewing_treatment, PersistentDataType.STRING)){
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
    public void setPotionTreatments(ItemStack i, Collection<PotionTreatment> treatments){
        if (i == null) return;
        if (i.getItemMeta() == null) return;
        if (treatments == null) {
            i.getItemMeta().getPersistentDataContainer().remove(key_brewing_treatment);
        } else {
            ItemMeta meta = i.getItemMeta();
            String treatmentString = treatments.stream().map(PotionTreatment::toString).collect(Collectors.joining(";"));
            meta.getPersistentDataContainer().set(key_brewing_treatment, PersistentDataType.STRING,
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
    public void setPotionQuality(ItemStack i, int quality){
        if (i == null) return;
        if (i.getItemMeta() == null) return;
        ItemMeta meta = i.getItemMeta();
        meta.getPersistentDataContainer().set(key_quality_general, PersistentDataType.INTEGER, quality);
        i.setItemMeta(meta);
        setQualityLore(i);
    }

    /**
     * Sets an item's attribute scaling, will not do anything if the item by default does not have the attribute.
     * @param i the ItemStack to update its attribute scaling strength
     * @param quality the amount of quality points to use with the material's attribute scaling
     * @param type the type of scaling you want to use, e.g. "DURABILITY" or "AMPLIFIER"
     * @param potionEffect the potion effect you want the item to have scaled
     */
    public void applyPotionEffectScaling(ItemStack i, int quality, Type type, String potionEffect, double minimumAmplifier, double minimumDuration){
        if (i == null) {
            return;
        }
        Scaling scaling = getScaling(type, potionEffect);
        if (scaling == null) {
            return;
        }
        if (i.getItemMeta() == null) {
            return;
        }
        PotionEffectWrapper defaultPotionEffect = PotionAttributesManager.getInstance().getDefaultPotionEffect(i, potionEffect);
        PotionEffectWrapper currentPotionEffect = PotionAttributesManager.getInstance().getPotionEffectWrapper(i, potionEffect);
        if (defaultPotionEffect != null){
            double defaultStat;
            double minimum = 0;
            if (type == Type.DURATION){
                defaultStat = defaultPotionEffect.getDuration();
                minimum = defaultStat * minimumDuration;
            } else if (type == Type.AMPLIFIER){
                defaultStat = defaultPotionEffect.getAmplifier();
                minimum = defaultStat * minimumAmplifier;
                if (!PotionEffectManager.getInstance().getRegisteredPotionEffects().containsKey(potionEffect)) {
                    // vanilla potion effects have amplifiers that start at 0 for amplifier I,
                    // multiplying by 0 still outputs 0, so 1 should be added before multiplication and then subtracted
                    minimum = ((1 + defaultStat) * minimumAmplifier) - 1;
                }
            } else {
                return;
            }
            try {
                double scalingResult = Utils.eval(scaling.getScaling().replace("%rating%", "" + quality));
                double finalResult = 0;
                if (scaling.getScalingType() == ScalingMode.MULTIPLIER){
                    finalResult = Utils.round(defaultStat * scalingResult, 4);
                } else if (scaling.getScalingType() == ScalingMode.ADD_ON_DEFAULT){
                    finalResult = Utils.round(defaultStat + scalingResult, 4);
                }
                if (!scaling.doIgnoreUpper()) if (scaling.getUpperBound() > finalResult) finalResult = scaling.getUpperBound();
                if (!scaling.doIgnoreLower()) if (finalResult < scaling.getLowerBound()) finalResult = scaling.getLowerBound();
                finalResult = Math.max(minimum, finalResult);
                if (type == Type.DURATION){
                    if (i.getType() == Material.LINGERING_POTION) finalResult /= 4;
                    if (i.getType() == Material.TIPPED_ARROW) finalResult /= 8;
                    if (currentPotionEffect != null){
                        PotionAttributesManager.getInstance().setPotionEffectStrength(i, potionEffect, currentPotionEffect.getAmplifier(), (int) Math.floor(finalResult));
                    } else {
                        PotionAttributesManager.getInstance().setPotionEffectStrength(i, potionEffect, defaultPotionEffect.getAmplifier(), (int) Math.floor(finalResult));
                    }
                } else {
                    if (currentPotionEffect != null){
                        PotionAttributesManager.getInstance().setPotionEffectStrength(i, potionEffect, finalResult, currentPotionEffect.getDuration());
                    } else {
                        PotionAttributesManager.getInstance().setPotionEffectStrength(i, potionEffect, finalResult, defaultPotionEffect.getDuration());
                    }
                }
            } catch (RuntimeException e){
                ValhallaMMO.getPlugin().getLogger().warning("Attempting to parse formula " + scaling + ", but something went wrong. ");
                e.printStackTrace();
            }
        }
    }

    public NamespacedKey getKey_quality_general() {
        return key_quality_general;
    }

    public NamespacedKey getKey_brewing_treatment() {
        return key_brewing_treatment;
    }

    public static AlchemyPotionTreatmentManager getInstance(){
        if (manager == null){
            manager = new AlchemyPotionTreatmentManager();
        }
        return manager;
    }

    public enum Type{
        DURATION,
        AMPLIFIER
    }
}
