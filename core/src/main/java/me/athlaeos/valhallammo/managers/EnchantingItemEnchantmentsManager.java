package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Scaling;
import me.athlaeos.valhallammo.dom.ScalingMode;
import me.athlaeos.valhallammo.items.enchantmentwrappers.EnchantmentWrapper;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentOffer;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public class EnchantingItemEnchantmentsManager {
    private static EnchantingItemEnchantmentsManager manager = null;

    private Map<String, Scaling> customEnchantmentScalings;
    private Map<Enchantment, Scaling> vanillaEnchantmentScalings;

    /**
     * Registers an enchantment scalings for a custom enchantment type.
     * @param enchantment the enchantment to set its amplifier scalings for
     * @param scaling the scaling formula for the material
     */
    public void registerScaling(String enchantment, Scaling scaling){
        if (enchantment == null || scaling == null) return;
        customEnchantmentScalings.put(enchantment, scaling);
    }

    /**
     * Registers an enchantment scalings for a vanilla enchantment type.
     * @param enchantment the enchantment to set its amplifier scalings for
     * @param scaling the scaling formula for the material
     */
    public void registerScaling(Enchantment enchantment, Scaling scaling){
        if (enchantment == null || scaling == null) return;
        vanillaEnchantmentScalings.put(enchantment, scaling);
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

    public void reload(){
        manager = null;
        getInstance();
    }

    public EnchantingItemEnchantmentsManager(){
        loadConfig();
    }

    public void loadConfig(){
        customEnchantmentScalings = new HashMap<>();
        vanillaEnchantmentScalings = new HashMap<>();
        YamlConfiguration config = ConfigManager.getInstance().getConfig("skill_enchanting.yml").get();

        registerScaling(Enchantment.ARROW_DAMAGE, getScalingFromPath(config, "scaling_amplifier.power"));
        registerScaling(Enchantment.ARROW_KNOCKBACK, getScalingFromPath(config, "scaling_amplifier.punch"));
        registerScaling(Enchantment.DAMAGE_ALL, getScalingFromPath(config, "scaling_amplifier.sharpness"));
        registerScaling(Enchantment.DAMAGE_ARTHROPODS, getScalingFromPath(config, "scaling_amplifier.bane_of_arthropods"));
        registerScaling(Enchantment.DAMAGE_UNDEAD, getScalingFromPath(config, "scaling_amplifier.smite"));
        registerScaling(Enchantment.DEPTH_STRIDER, getScalingFromPath(config, "scaling_amplifier.depth_strider"));
        registerScaling(Enchantment.DIG_SPEED, getScalingFromPath(config, "scaling_amplifier.efficiency"));
        registerScaling(Enchantment.DURABILITY, getScalingFromPath(config, "scaling_amplifier.unbreaking"));
        registerScaling(Enchantment.FIRE_ASPECT, getScalingFromPath(config, "scaling_amplifier.fire_aspect"));
        registerScaling(Enchantment.FROST_WALKER, getScalingFromPath(config, "scaling_amplifier.frost_walker"));
        registerScaling(Enchantment.IMPALING, getScalingFromPath(config, "scaling_amplifier.impaling"));
        registerScaling(Enchantment.KNOCKBACK, getScalingFromPath(config, "scaling_amplifier.knockback"));
        registerScaling(Enchantment.LOOT_BONUS_BLOCKS, getScalingFromPath(config, "scaling_amplifier.fortune"));
        registerScaling(Enchantment.LOOT_BONUS_MOBS, getScalingFromPath(config, "scaling_amplifier.looting"));
        registerScaling(Enchantment.LOYALTY, getScalingFromPath(config, "scaling_amplifier.loyalty"));
        registerScaling(Enchantment.LUCK, getScalingFromPath(config, "scaling_amplifier.luck"));
        registerScaling(Enchantment.LURE, getScalingFromPath(config, "scaling_amplifier.lure"));
        registerScaling(Enchantment.OXYGEN, getScalingFromPath(config, "scaling_amplifier.respiration"));
        registerScaling(Enchantment.PIERCING, getScalingFromPath(config, "scaling_amplifier.piercing"));
        registerScaling(Enchantment.PROTECTION_ENVIRONMENTAL, getScalingFromPath(config, "scaling_amplifier.protection"));
        registerScaling(Enchantment.PROTECTION_PROJECTILE, getScalingFromPath(config, "scaling_amplifier.projectile_protection"));
        registerScaling(Enchantment.PROTECTION_EXPLOSIONS, getScalingFromPath(config, "scaling_amplifier.blast_protection"));
        registerScaling(Enchantment.PROTECTION_FIRE, getScalingFromPath(config, "scaling_amplifier.fire_protection"));
        registerScaling(Enchantment.PROTECTION_FALL, getScalingFromPath(config, "scaling_amplifier.feather_falling"));
        registerScaling(Enchantment.QUICK_CHARGE, getScalingFromPath(config, "scaling_amplifier.quick_charge"));
        registerScaling(Enchantment.RIPTIDE, getScalingFromPath(config, "scaling_amplifier.riptide"));
        registerScaling(Enchantment.SOUL_SPEED, getScalingFromPath(config, "scaling_amplifier.soul_speed"));
        registerScaling(Enchantment.SWEEPING_EDGE, getScalingFromPath(config, "scaling_amplifier.sweeping_edge"));
        registerScaling(Enchantment.THORNS, getScalingFromPath(config, "scaling_amplifier.thorns"));
        registerScaling(Enchantment.getByKey(NamespacedKey.minecraft("swift_sneak")), getScalingFromPath(config, "scaling_amplifier.swift_sneak"));

        registerScaling("ALCHEMY_QUALITY", getScalingFromPath(config, "scaling_amplifier.fortify_alchemy_quality"));
        registerScaling("ALCHEMY_BREW_SPEED", getScalingFromPath(config, "scaling_amplifier.fortify_alchemy_brew_speed"));
        registerScaling("ALCHEMY_INGREDIENT_SAVE", getScalingFromPath(config, "scaling_amplifier.fortify_alchemy_ingredient_save"));
        registerScaling("ALCHEMY_POTION_SAVE", getScalingFromPath(config, "scaling_amplifier.fortify_alchemy_potion_save"));
        registerScaling("SMITHING_QUALITY", getScalingFromPath(config, "scaling_amplifier.fortify_smithing"));
        registerScaling("MINING_EXTRA_DROPS", getScalingFromPath(config, "scaling_amplifier.fortify_mining_extra_drops"));
        registerScaling("MINING_RARE_DROPS", getScalingFromPath(config, "scaling_amplifier.fortify_mining_rare_drops"));
        registerScaling("WOODCUTTING_EXTRA_DROPS", getScalingFromPath(config, "scaling_amplifier.fortify_woodcutting_extra_drops"));
        registerScaling("WOODCUTTING_RARE_DROPS", getScalingFromPath(config, "scaling_amplifier.fortify_woodcutting_rare_drops"));
        registerScaling("DIGGING_EXTRA_DROPS", getScalingFromPath(config, "scaling_amplifier.fortify_digging_extra_drops"));
        registerScaling("DIGGING_RARE_DROPS", getScalingFromPath(config, "scaling_amplifier.fortify_digging_rare_drops"));
        registerScaling("FARMING_EXTRA_DROPS", getScalingFromPath(config, "scaling_amplifier.fortify_farming_extra_drops"));
        registerScaling("FARMING_RARE_DROPS", getScalingFromPath(config, "scaling_amplifier.fortify_farming_rare_drops"));
        registerScaling("FARMING_FISHING_TIER", getScalingFromPath(config, "scaling_amplifier.fortify_farming_fishing_tier"));
        registerScaling("WEAPONS_DAMAGE", getScalingFromPath(config, "scaling_amplifier.fortify_weapons_damage"));
        registerScaling("ARCHERY_DAMAGE", getScalingFromPath(config, "scaling_amplifier.fortify_archery_damage"));
        registerScaling("ARCHERY_ACCURACY", getScalingFromPath(config, "scaling_amplifier.fortify_archery_accuracy"));
        registerScaling("ARCHERY_AMMO_SAVE", getScalingFromPath(config, "scaling_amplifier.fortify_archery_ammo_save"));
        registerScaling("EXP_GAIN_VANILLA", getScalingFromPath(config, "scaling_amplifier.exp_gained_vanilla"));
        registerScaling("EXP_GAIN_SKILL", getScalingFromPath(config, "scaling_amplifier.exp_gained_skill"));
        registerScaling("DAMAGE_TAKEN", getScalingFromPath(config, "scaling_amplifier.damage_taken"));
        registerScaling("DAMAGE_DEALT", getScalingFromPath(config, "scaling_amplifier.damage_dealt"));

        registerScaling("STUN_CHANCE", getScalingFromPath(config, "scaling_amplifier.stun_chance"));
        registerScaling("ARMOR_MULTIPLIER", getScalingFromPath(config, "scaling_amplifier.armor_multiplier"));
        registerScaling("BLEED_RESISTANCE", getScalingFromPath(config, "scaling_amplifier.bleed_resistance"));
        registerScaling("COOLDOWN_REDUCTION", getScalingFromPath(config, "scaling_amplifier.cooldown_reduction"));
        registerScaling("CRAFTING_SPEED", getScalingFromPath(config, "scaling_amplifier.crafting_speed"));
        registerScaling("HEALING_BONUS", getScalingFromPath(config, "scaling_amplifier.healing_bonus"));
        registerScaling("HUNGER_SAVE_CHANCE", getScalingFromPath(config, "scaling_amplifier.hunger_save_chance"));
        registerScaling("EXPLOSION_POWER", getScalingFromPath(config, "scaling_amplifier.mining_explosion_power"));
        registerScaling("STUN_RESISTANCE", getScalingFromPath(config, "scaling_amplifier.stun_resistance"));
        registerScaling("BLEED_CHANCE", getScalingFromPath(config, "scaling_amplifier.bleed_chance"));
        registerScaling("BLEED_DAMAGE", getScalingFromPath(config, "scaling_amplifier.bleed_damage"));
        registerScaling("BLEED_DURATION", getScalingFromPath(config, "scaling_amplifier.bleed_duration"));
        registerScaling("CRIT_CHANCE", getScalingFromPath(config, "scaling_amplifier.crit_chance"));
        registerScaling("CRIT_DAMAGE", getScalingFromPath(config, "scaling_amplifier.crit_damage"));
        registerScaling("FLAT_ARMOR_PENETRATION", getScalingFromPath(config, "scaling_amplifier.armor_flat_penetration"));
        registerScaling("FLAT_LIGHT_ARMOR_PENETRATION", getScalingFromPath(config, "scaling_amplifier.light_armor_flat_penetration"));
        registerScaling("FLAT_HEAVY_ARMOR_PENETRATION", getScalingFromPath(config, "scaling_amplifier.heavy_armor_flat_penetration"));
        registerScaling("FRACTION_ARMOR_PENETRATION", getScalingFromPath(config, "scaling_amplifier.armor_fraction_penetration"));
        registerScaling("FRACTION_LIGHT_ARMOR_PENETRATION", getScalingFromPath(config, "scaling_amplifier.light_armor_fraction_penetration"));
        registerScaling("FRACTION_HEAVY_ARMOR_PENETRATION", getScalingFromPath(config, "scaling_amplifier.heavy_armor_fraction_penetration"));
        registerScaling("LIGHT_ARMOR_DAMAGE", getScalingFromPath(config, "scaling_amplifier.light_armor_damage"));
        registerScaling("HEAVY_ARMOR_DAMAGE", getScalingFromPath(config, "scaling_amplifier.heavy_armor_damage"));
        registerScaling("IMMUNITY_FRAME_BONUS", getScalingFromPath(config, "scaling_amplifier.immunity_frame_bonus"));
        registerScaling("IMMUNITY_FRAME_FLAT_BONUS", getScalingFromPath(config, "scaling_amplifier.immunity_frame_flat_bonus"));
        registerScaling("IMMUNITY_FRAME_REDUCTION", getScalingFromPath(config, "scaling_amplifier.immunity_frame_reduction"));
        registerScaling("WEAPON_REACH_BONUS", getScalingFromPath(config, "scaling_amplifier.attack_reach"));
        registerScaling("DAMAGE_RESISTANCE", getScalingFromPath(config, "scaling_amplifier.damage_resistance"));
        registerScaling("FALL_DAMAGE_RESISTANCE", getScalingFromPath(config, "scaling_amplifier.fall_damage_resistance"));
        registerScaling("FIRE_DAMAGE_RESISTANCE", getScalingFromPath(config, "scaling_amplifier.fire_damage_resistance"));
        registerScaling("PROJECTILE_DAMAGE_RESISTANCE", getScalingFromPath(config, "scaling_amplifier.projectile_damage_resistance"));
        registerScaling("MELEE_DAMAGE_RESISTANCE", getScalingFromPath(config, "scaling_amplifier.melee_damage_resistance"));
        registerScaling("MAGIC_DAMAGE_RESISTANCE", getScalingFromPath(config, "scaling_amplifier.magic_damage_resistance"));
        registerScaling("POISON_DAMAGE_RESISTANCE", getScalingFromPath(config, "scaling_amplifier.poison_damage_resistance"));
        registerScaling("EXPLOSION_DAMAGE_RESISTANCE", getScalingFromPath(config, "scaling_amplifier.explosion_damage_resistance"));
    }

    public Scaling getScaling(String enchantment){
        return customEnchantmentScalings.get(enchantment);
    }

    public Scaling getScaling(Enchantment enchantment){
        return vanillaEnchantmentScalings.get(enchantment);
    }

    /**
     * Scales an item's custom enchantment according to the given skill points
     * @param i the ItemStack to update its attribute scaling strength
     * @param skill the amount of skill points to use with the item's enchantment scaling
     * @param enchantment the enchantment to scale
     */
    public void applyEnchantmentScaling(ItemStack i, int skill, String enchantment, double baseValue){
        if (i == null) {
            return;
        }
        Scaling scaling = getScaling(enchantment);
        if (scaling == null) {
            return;
        }
        if (i.getItemMeta() == null) {
            return;
        }
        EnchantmentWrapper a = CustomEnchantmentManager.getInstance().getRegisteredEnchantments().get(enchantment);
        if (a != null){
            try {
                double scalingResult = Utils.eval(scaling.getScaling().replace("%rating%", "" + skill));
                double finalResult = 0;
                if (scaling.getScalingType() == ScalingMode.MULTIPLIER){
                    finalResult = Utils.round(baseValue * scalingResult, 3);
                } else if (scaling.getScalingType() == ScalingMode.ADD_ON_DEFAULT){
                    finalResult = Utils.round(baseValue + scalingResult, 3);
                }
                if (!scaling.doIgnoreUpper()) if (finalResult > scaling.getUpperBound()) finalResult = scaling.getUpperBound();
                if (!scaling.doIgnoreLower()) if (finalResult < scaling.getLowerBound()) finalResult = scaling.getLowerBound();
                CustomEnchantmentManager.getInstance().setEnchantmentStrength(i, enchantment, finalResult);
            } catch (RuntimeException e){
                ValhallaMMO.getPlugin().getLogger().severe("Attempting to parse formula " + scaling + ", but something went wrong. ");
                e.printStackTrace();
            }
        }
    }

    /**
     * Scales an an enchantment according to the given skill points
     * @param skill the amount of skill points to use with the item's enchantment scaling
     * @param enchantment the enchantment to scale
     * @param baseValue the base level of the enchantment
     */
    public Map<Enchantment, Integer> applyEnchantmentScaling(int skill, Enchantment enchantment, int baseValue){
        Map<Enchantment, Integer> changedEnchantment = new HashMap<>();
        Scaling scaling = getScaling(enchantment);
        if (scaling == null) {
            return changedEnchantment;
        }

        try {
            double scalingResult = Utils.eval(scaling.getScaling().replace("%rating%", "" + skill));
            double finalResult = 0;
            if (scaling.getScalingType() == ScalingMode.MULTIPLIER){
                finalResult = Utils.round(baseValue * scalingResult, 3);
            } else if (scaling.getScalingType() == ScalingMode.ADD_ON_DEFAULT){
                finalResult = Utils.round(baseValue + scalingResult, 3);
            }
            if (!scaling.doIgnoreUpper()) if (finalResult > scaling.getUpperBound()) finalResult = scaling.getUpperBound();
            if (!scaling.doIgnoreLower()) if (finalResult < scaling.getLowerBound()) finalResult = scaling.getLowerBound();

            changedEnchantment.put(enchantment, Math.max(1, (int) Math.floor(finalResult)));
        } catch (RuntimeException e){
            ValhallaMMO.getPlugin().getLogger().severe("Attempting to parse formula " + scaling + ", but something went wrong. ");
            e.printStackTrace();
        }
        return changedEnchantment;
    }

    /**
     * Scales an enchanting session's enchantment offers according to the given skill points
     * @param skill the amount of skill points to use with the item's enchantment scaling
     * @param enchantments the enchantment offers to scale
     */
    public void applyEnchantmentOffersScaling(int skill, EnchantmentOffer[] enchantments, double chanceForApplication){
        for (EnchantmentOffer offer : enchantments) {
            if (offer == null) continue;
            if (Utils.getRandom().nextDouble() <= chanceForApplication){
                Scaling scaling = getScaling(offer.getEnchantment());
                if (scaling == null) {
                    continue;
                }

                try {
                    double scalingResult = Utils.eval(scaling.getScaling().replace("%rating%", "" + skill));
                    double finalResult = 0;
                    if (scaling.getScalingType() == ScalingMode.MULTIPLIER) {
                        finalResult = Utils.round(offer.getEnchantmentLevel() * scalingResult, 3);
                    } else if (scaling.getScalingType() == ScalingMode.ADD_ON_DEFAULT) {
                        finalResult = Utils.round(offer.getEnchantmentLevel() + scalingResult, 3);
                    }
                    if (!scaling.doIgnoreUpper())
                        if (scaling.getUpperBound() > finalResult) finalResult = scaling.getUpperBound();
                    if (!scaling.doIgnoreLower())
                        if (finalResult < scaling.getLowerBound()) finalResult = scaling.getLowerBound();

                    offer.setEnchantmentLevel(Math.max(1, (int) Math.floor(finalResult)));
                } catch (RuntimeException e) {
                    ValhallaMMO.getPlugin().getLogger().severe("Attempting to parse formula " + scaling + ", but something went wrong. ");
                    e.printStackTrace();
                }
            }
        }
    }

    public Map<Enchantment, Integer> getAnvilMaxLevels(int skill){
        Map<Enchantment, Integer> anvilMaxLevels = new HashMap<>();
        for (Enchantment enchantment : Enchantment.values()){
            Scaling scaling = getScaling(enchantment);
            if (scaling == null) {
                anvilMaxLevels.put(enchantment, enchantment.getMaxLevel());
                continue;
            }

            try {
                double scalingResult = Utils.eval(scaling.getScaling().replace("%rating%", "" + skill));
                double finalResult = 0;
                if (scaling.getScalingType() == ScalingMode.MULTIPLIER){
                    finalResult = Utils.round(enchantment.getMaxLevel() * scalingResult, 3);
                } else if (scaling.getScalingType() == ScalingMode.ADD_ON_DEFAULT){
                    finalResult = Utils.round(enchantment.getMaxLevel() + scalingResult, 3);
                }
                if (!scaling.doIgnoreUpper()) if (scaling.getUpperBound() > finalResult) finalResult = scaling.getUpperBound();
                if (!scaling.doIgnoreLower()) if (finalResult < scaling.getLowerBound()) finalResult = scaling.getLowerBound();
//            if (i.getItemMeta() instanceof EnchantmentStorageMeta){
//                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) i.getItemMeta();
//                meta.addStoredEnchant(enchantment, Math.max(1, (int) Math.floor(finalResult)), true);
//                i.setItemMeta(meta);
//            } else {
                anvilMaxLevels.put(enchantment, Math.max(1, (int) Math.floor(finalResult)));
//                i.addUnsafeEnchantment(enchantment, Math.max(1, (int) Math.floor(finalResult)));
//            }
            } catch (RuntimeException e){
                ValhallaMMO.getPlugin().getLogger().severe("Attempting to parse formula " + scaling + ", but something went wrong. ");
                e.printStackTrace();
            }
        }

        return anvilMaxLevels;
    }

    public static EnchantingItemEnchantmentsManager getInstance(){
        if (manager == null){
            manager = new EnchantingItemEnchantmentsManager();
        }
        return manager;
    }
}
