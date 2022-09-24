package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Scaling;
import me.athlaeos.valhallammo.dom.ScalingMode;
import me.athlaeos.valhallammo.items.enchantmentwrappers.EnchantmentWrapper;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
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

        registerScaling("ALCHEMY_QUALITY", getScalingFromPath(config, "scaling_amplifier.fortify_alchemy_quality"));
        registerScaling("ALCHEMY_BREW_SPEED", getScalingFromPath(config, "scaling_amplifier.fortify_alchemy_brew_speed"));
        registerScaling("ALCHEMY_INGREDIENT_SAVE", getScalingFromPath(config, "scaling_amplifier.fortify_alchemy_ingredient_save"));
        registerScaling("ALCHEMY_POTION_SAVE", getScalingFromPath(config, "scaling_amplifier.fortify_alchemy_potion_save"));
        registerScaling("SMITHING_QUALITY", getScalingFromPath(config, "scaling_amplifier.fortify_smithing"));
        registerScaling("MINING_EXTRA_DROPS", getScalingFromPath(config, "scaling_amplifier.fortify_mining_extra_drops"));
        registerScaling("MINING_RARE_DROPS", getScalingFromPath(config, "scaling_amplifier.fortify_mining_rare_drops"));
        registerScaling("WOODCUTTING_EXTRA_DROPS", getScalingFromPath(config, "scaling_amplifier.fortify_woodcutting_extra_drops"));
        registerScaling("WOODCUTTING_RARE_DROPS", getScalingFromPath(config, "scaling_amplifier.fortify_woodcutting_rare_drops"));
        registerScaling("FARMING_EXTRA_DROPS", getScalingFromPath(config, "scaling_amplifier.fortify_farming_extra_drops"));
        registerScaling("FARMING_RARE_DROPS", getScalingFromPath(config, "scaling_amplifier.fortify_farming_rare_drops"));
        registerScaling("FARMING_FISHING_TIER", getScalingFromPath(config, "scaling_amplifier.fortify_farming_fishing_tier"));
        registerScaling("ACROBATICS", getScalingFromPath(config, "scaling_amplifier.fortify_acrobatics"));
        registerScaling("WEAPONS_DAMAGE", getScalingFromPath(config, "scaling_amplifier.fortify_weapons_damage"));
        registerScaling("ARCHERY_DAMAGE", getScalingFromPath(config, "scaling_amplifier.fortify_archery_damage"));
        registerScaling("ARCHERY_ACCURACY", getScalingFromPath(config, "scaling_amplifier.fortify_archery_accuracy"));
        registerScaling("ARCHERY_AMMO_SAVE", getScalingFromPath(config, "scaling_amplifier.fortify_archery_ammo_save"));
        registerScaling("TRADING", getScalingFromPath(config, "scaling_amplifier.fortify_trading"));
        registerScaling("UNARMED_DAMAGE", getScalingFromPath(config, "scaling_amplifier.fortify_unarmed_damage"));
        registerScaling("EXP_GAIN_VANILLA", getScalingFromPath(config, "scaling_amplifier.exp_gained_vanilla"));
        registerScaling("EXP_GAIN_SKILL", getScalingFromPath(config, "scaling_amplifier.exp_gained_skill"));
        registerScaling("DAMAGE_TAKEN", getScalingFromPath(config, "scaling_amplifier.damage_taken"));
        registerScaling("DAMAGE_DEALT", getScalingFromPath(config, "scaling_amplifier.damage_dealt"));
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
                if (!scaling.doIgnoreUpper()) if (scaling.getUpperBound() > finalResult) finalResult = scaling.getUpperBound();
                if (!scaling.doIgnoreLower()) if (finalResult < scaling.getLowerBound()) finalResult = scaling.getLowerBound();
                CustomEnchantmentManager.getInstance().setEnchantmentStrength(i, enchantment, finalResult);
            } catch (RuntimeException e){
                ValhallaMMO.getPlugin().getLogger().severe("Attempting to parse formula " + scaling + ", but something went wrong. ");
                e.printStackTrace();
            }
        }
    }

    /**
     * Scales an item's custom enchantment according to the given skill points
     * @param i the ItemStack to update its attribute scaling strength
     * @param skill the amount of skill points to use with the item's enchantment scaling
     * @param enchantment the enchantment to scale
     */
    public Map<Enchantment, Integer> applyEnchantmentScaling(ItemStack i, int skill, Enchantment enchantment, int baseValue){
        Map<Enchantment, Integer> changedEnchantment = new HashMap<>();
        if (i == null) {
            return changedEnchantment;
        }
        Scaling scaling = getScaling(enchantment);
        if (scaling == null) {
            return changedEnchantment;
        }
        if (i.getItemMeta() == null) {
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
            if (!scaling.doIgnoreUpper()) if (scaling.getUpperBound() > finalResult) finalResult = scaling.getUpperBound();
            if (!scaling.doIgnoreLower()) if (finalResult < scaling.getLowerBound()) finalResult = scaling.getLowerBound();
//            if (i.getItemMeta() instanceof EnchantmentStorageMeta){
//                EnchantmentStorageMeta meta = (EnchantmentStorageMeta) i.getItemMeta();
//                meta.addStoredEnchant(enchantment, Math.max(1, (int) Math.floor(finalResult)), true);
//                i.setItemMeta(meta);
//            } else {
                changedEnchantment.put(enchantment, Math.max(1, (int) Math.floor(finalResult)));
//                i.addUnsafeEnchantment(enchantment, Math.max(1, (int) Math.floor(finalResult)));
//            }
        } catch (RuntimeException e){
            ValhallaMMO.getPlugin().getLogger().severe("Attempting to parse formula " + scaling + ", but something went wrong. ");
            e.printStackTrace();
        }
        return changedEnchantment;
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
