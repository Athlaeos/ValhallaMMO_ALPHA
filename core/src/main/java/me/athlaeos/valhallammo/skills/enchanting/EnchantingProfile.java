package me.athlaeos.valhallammo.skills.enchanting;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.EnchantmentType;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.persistence.DatabaseConnection;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class EnchantingProfile extends Profile implements Serializable {
    private static final NamespacedKey enchantingProfileKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_enchanting");

    private float vanillaenchantmentamplifychance = 0F; // chance for vanilla enchantments to be amplified (or weakened)
    private int maxcustomenchantmentsallowed = 0; // amount of custom enchantments one is allowed to add to an item
    private float lapissavechance = 0F; // chance for lapis lazuli to be refunded to the player
    private float exprefundchance = 0F; // chance for experience to be refunded to the player
    private float exprefundfraction = 0F; // amount of experience refunded if chance procs, capped at 1.0
    private float vanillaexpgainmultiplier = 1F; // multiplier of the player's experience gained

    private int enchantingquality_general = 0; // general enchanting skill
    private int enchantingquality_vanilla = 0; // vanilla enchanting skill
    private int enchantingquality_custom = 0; // custom enchanting skill
    private int anvilquality = 0; // custom anvil enchantment combining skill (higher skill = higher max lv enchantment combining)

    private boolean cantransferenchantments = false; // if true, adding a plain book to a grindstone transfers the enchantments of the item to the book
    private float sharpnessbonus = 0F; // multiplier bonus for Sharpness' effectiveness, for example 0.5 will increase the damage of Sharpness I from 1 to 1.5
    private float smitebonus = 0F; // same for sharpness, but for Smite
    private float boabonus = 0F; // same for sharpness, but for Bane of Arthropods
    private float fortunebonus = 0F; // provides an additional drop multiplier bonus per level
    private float efficiencybonus = 0F; // when implemented, provides a mining speed bonus for Efficiency's effectiveness. Efficiency I for wooden pickaxes for example provides +150% mining speed, if this value is 0.5 it will be +225%
    private float knockbackbonus = 0F; // provides an additional bonus to the effectiveness of Knockback
    private float lootingbonus = 0F; // provides an additional mob drop multiplier bonus per level
    private float fireaspectbonus = 0F; // provides an additional fire tick duration multiplier bonus per level
    private float powerbonus = 0F; // same for sharpness, but for Power
    private float flamebonus = 0F; // same for fire aspect, but for Flame
    private float blastprotectionbonus = 0F; // provides an additional blast protection multiplier bonus
    private float featherfallingbonus = 0F; // provides an additional feather falling multiplier bonus
    private float fireprotectionbonus = 0F; // provides an additional fire protection multiplier bonus
    private float projectileprotectionbonus = 0F; // provides an additional projectile protection multiplier bonus
    private float protectionbonus = 0F; // provides an additional protection multiplier bonus
    private float soulspeedbonus = 0F; // provides an additional movement speed multiplier bonus for soul speed
    private float thornsbonus = 0F; // provides an additional reflect damage multiplier bonus
    private float lurebonus = 0F; // provides additional fishing time reduction per level
    private float lotsbonus = 0F; // provides additional fishing tier per level
    private float impalingbonus = 0F; // same for sharpness, but for Impaling
    private float swiftsneakbonus = 0F; // same for soul speed, but for swift sneak

    private float environmentalenchantmentbonus = 0F; // general bonus describing non-combat enchantments like Fortune, Efficiency, Looting, Swift Sneak, Soul Speed. Not used in the plugin natively, but can be used for external plugins
    private float protectiveenchantmentbonus = 0F; // general bonus describing protective enchantments like Projectile Protection, Feather Falling, Protection, etc.
    private float offensiveenchantmentbonus = 0F; // general bonus describing offensive enchantments like Sharpness, Smite, Power, etc.

    private double enchantingexpmultiplier_general = 100D;
    private double enchantingexpmultiplier_custom = 100D;
    private double enchantingexpmultiplier_vanilla = 100D;

    @Override
    public void createProfileTable(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS profiles_enchanting (" +
                "owner VARCHAR(40) PRIMARY KEY," +
                "level SMALLINT default 0," +
                "exp DOUBLE default 0," +
                "exp_total DOUBLE default 0," +
                "vanillaenchantmentamplifychance FLOAT DEFAULT 0," +
                "maxcustomenchantmentsallowed SMALLINT DEFAULT 0, " +
                "lapissavechance FLOAT DEFAULT 0," +
                "exprefundchance FLOAT DEFAULT 0," +
                "exprefundfraction FLOAT DEFAULT 0," +
                "vanillaexpgainmultiplier FLOAT DEFAULT 1," +
                "enchantingquality_general SMALLINT DEFAULT 0," +
                "enchantingquality_vanilla SMALLINT DEFAULT 0," +
                "enchantingquality_custom SMALLINT DEFAULT 0," +
                "anvilquality SMALLINT DEFAULT 0," +
                "enchantingexpmultiplier_general DOUBLE DEFAULT 100," +
                "enchantingexpmultiplier_custom DOUBLE DEFAULT 100," +
                "enchantingexpmultiplier_vanilla DOUBLE DEFAULT 100);");
        stmt.execute();

        conn.addColumnIfNotExists("profiles_enchanting", "anvilquality", "SMALLINT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "cantransferenchantments", "BOOL DEFAULT false");
        conn.addColumnIfNotExists("profiles_enchanting", "sharpnessbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "smitebonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "boabonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "fortunebonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "efficiencybonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "knockbackbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "lootingbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "fireaspectbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "powerbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "flamebonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "blastprotectionbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "featherfallingbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "fireprotectionbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "projectileprotectionbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "protectionbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "soulspeedbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "thornsbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "lurebonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "lotsbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "impalingbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "swiftsneakbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "environmentalenchantmentbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "protectiveenchantmentbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_enchanting", "offensiveenchantmentbonus", "FLOAT DEFAULT 0"); // TODO
    }

    @Override
    public void insertOrUpdateProfile(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement(
                "REPLACE INTO profiles_enchanting " +
                        "(owner, level, exp, exp_total, vanillaenchantmentamplifychance, maxcustomenchantmentsallowed, " +
                        "lapissavechance, exprefundchance, exprefundfraction, vanillaexpgainmultiplier, enchantingquality_general, " +
                        "enchantingquality_vanilla, enchantingquality_custom, anvilquality, cantransferenchantments, " +
                        "sharpnessbonus, smitebonus, boabonus, fortunebonus, efficiencybonus, knockbackbonus, " +
                        "lootingbonus, fireaspectbonus, powerbonus, flamebonus, blastprotectionbonus, featherfallingbonus, " +
                        "fireprotectionbonus, projectileprotectionbonus, protectionbonus, soulspeedbonus, thornsbonus, " +
                        "lurebonus, lotsbonus, impalingbonus, swiftsneakbonus, environmentalenchantmentbonus, " +
                        "protectiveenchantmentbonus, offensiveenchantmentbonus, enchantingexpmultiplier_general, " +
                        "enchantingexpmultiplier_custom, enchantingexpmultiplier_vanilla) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        stmt.setString(1, owner.toString());
        stmt.setInt(2, level);
        stmt.setDouble(3, exp);
        stmt.setDouble(4, lifetimeEXP);
        stmt.setFloat(5, vanillaenchantmentamplifychance);
        stmt.setInt(6, maxcustomenchantmentsallowed);
        stmt.setFloat(7, lapissavechance);
        stmt.setFloat(8, exprefundchance);
        stmt.setFloat(9, exprefundfraction);
        stmt.setFloat(10, vanillaexpgainmultiplier);
        stmt.setInt(11, enchantingquality_general);
        stmt.setInt(12, enchantingquality_vanilla);
        stmt.setInt(13, enchantingquality_custom);
        stmt.setInt(14, anvilquality);
        stmt.setBoolean(15, cantransferenchantments);
        stmt.setFloat(16, sharpnessbonus);
        stmt.setFloat(17, smitebonus);
        stmt.setFloat(18, boabonus);
        stmt.setFloat(19, fortunebonus);
        stmt.setFloat(20, efficiencybonus);
        stmt.setFloat(21, knockbackbonus);
        stmt.setFloat(22, lootingbonus);
        stmt.setFloat(23, fireaspectbonus);
        stmt.setFloat(24, powerbonus);
        stmt.setFloat(25, flamebonus);
        stmt.setFloat(26, blastprotectionbonus);
        stmt.setFloat(27, featherfallingbonus);
        stmt.setFloat(28, fireprotectionbonus);
        stmt.setFloat(29, projectileprotectionbonus);
        stmt.setFloat(30, protectionbonus);
        stmt.setFloat(31, soulspeedbonus);
        stmt.setFloat(32, thornsbonus);
        stmt.setFloat(33, lurebonus);
        stmt.setFloat(34, lotsbonus);
        stmt.setFloat(35, impalingbonus);
        stmt.setFloat(36, swiftsneakbonus);
        stmt.setFloat(37, environmentalenchantmentbonus);
        stmt.setFloat(38, protectiveenchantmentbonus);
        stmt.setFloat(39, offensiveenchantmentbonus);
        stmt.setDouble(40, enchantingexpmultiplier_general);
        stmt.setDouble(41, enchantingexpmultiplier_custom);
        stmt.setDouble(42, enchantingexpmultiplier_vanilla);
        stmt.execute();
    }

    @Override
    public Profile fetchProfile(Player p, DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement("SELECT * FROM profiles_enchanting WHERE owner = ?;");
        stmt.setString(1, p.getUniqueId().toString());
        ResultSet result = stmt.executeQuery();
        if (result.next()){
            EnchantingProfile profile = new EnchantingProfile(p);
            profile.setLevel(result.getInt("level"));
            profile.setExp(result.getDouble("exp"));
            profile.setLifetimeEXP(result.getDouble("exp_total"));
            profile.setVanillaEnchantmentAmplifyChance(result.getFloat("vanillaenchantmentamplifychance"));
            profile.setMaxCustomEnchantmentsAllowed(result.getInt("maxcustomenchantmentsallowed"));
            profile.setLapisSaveChance(result.getFloat("lapissavechance"));
            profile.setExpRefundChance(result.getFloat("exprefundchance"));
            profile.setExpRefundFraction(result.getFloat("exprefundfraction"));
            profile.setVanillaExpGainMultiplier(result.getFloat("vanillaexpgainmultiplier"));
            profile.setEnchantingSkill(null, result.getInt("enchantingquality_general"));
            profile.setEnchantingSkill(EnchantmentType.VANILLA, result.getInt("enchantingquality_vanilla"));
            profile.setEnchantingSkill(EnchantmentType.CUSTOM, result.getInt("enchantingquality_custom"));
            profile.setAnvilQuality(result.getInt("anvilquality"));
            profile.setCanTransferEnchantments(result.getBoolean("cantransferenchantments"));
            profile.setSharpnessBonus(result.getFloat("sharpnessbonus"));
            profile.setSmiteBonus(result.getFloat("smitebonus"));
            profile.setBoABonus(result.getFloat("boabonus"));
            profile.setFortuneBonus(result.getFloat("fortunebonus"));
            profile.setEfficiencyBonus(result.getFloat("efficiencybonus"));
            profile.setKnockbackBonus(result.getFloat("knockbackbonus"));
            profile.setLootingBonus(result.getFloat("lootingbonus"));
            profile.setFireAspectBonus(result.getFloat("fireaspectbonus"));
            profile.setPowerBonus(result.getFloat("powerbonus"));
            profile.setFlameBonus(result.getFloat("flamebonus"));
            profile.setBlastProtectionBonus(result.getFloat("blastprotectionbonus"));
            profile.setFeatherFallingBonus(result.getFloat("featherfallingbonus"));
            profile.setFireProtectionBonus(result.getFloat("fireprotectionbonus"));
            profile.setProjectileProtectionBonus(result.getFloat("projectileprotectionbonus"));
            profile.setProtectionBonus(result.getFloat("protectionbonus"));
            profile.setSoulSpeedBonus(result.getFloat("soulspeedbonus"));
            profile.setThornsBonus(result.getFloat("thornsbonus"));
            profile.setLureBonus(result.getFloat("lurebonus"));
            profile.setLotSBonus(result.getFloat("lotsbonus"));
            profile.setImpalingBonus(result.getFloat("impalingbonus"));
            profile.setSwiftSneakBonus(result.getFloat("swiftsneakbonus"));
            profile.setEnvironmentalEnchantmentBonus(result.getFloat("environmentalenchantmentbonus"));
            profile.setProtectiveEnchantmentBonus(result.getFloat("protectiveenchantmentbonus"));
            profile.setOffensiveEnchantmentBonus(result.getFloat("offensiveenchantmentbonus"));
            profile.setEnchantingExpMultiplier(null, result.getDouble("enchantingexpmultiplier_general"));
            profile.setEnchantingExpMultiplier(EnchantmentType.CUSTOM, result.getDouble("enchantingexpmultiplier_custom"));
            profile.setEnchantingExpMultiplier(EnchantmentType.VANILLA, result.getDouble("enchantingexpmultiplier_vanilla"));
            return profile;
        }
        return null;
    }

    public EnchantingProfile(Player owner){
        super(owner);
        if (owner == null) return;
        this.key = enchantingProfileKey;
    }

    @Override
    public void setDefaultStats(Player player) {
        Skill skill = SkillProgressionManager.getInstance().getSkill("ENCHANTING");
        if (skill != null){
            if (skill instanceof EnchantingSkill){
                EnchantingSkill enchantingSkill = (EnchantingSkill) skill;
                for (PerkReward startingPerk : enchantingSkill.getStartingPerks()){
                    startingPerk.execute(player);
                }
            }
        }
    }

    public float getOffensiveEnchantmentBonus() {
        return offensiveenchantmentbonus;
    }

    public void setOffensiveEnchantmentBonus(float offensiveenchantmentbonus) {
        this.offensiveenchantmentbonus = offensiveenchantmentbonus;
    }

    public float getProtectiveEnchantmentBonus() {
        return protectiveenchantmentbonus;
    }

    public void setProtectiveEnchantmentBonus(float protectiveenchantmentbonus) {
        this.protectiveenchantmentbonus = protectiveenchantmentbonus;
    }

    public void setEnvironmentalEnchantmentBonus(float environmentalenchantmentbonus) {
        this.environmentalenchantmentbonus = environmentalenchantmentbonus;
    }

    public float getEnvironmentalEnchantmentBonus() {
        return environmentalenchantmentbonus;
    }

    public void setAnvilQuality(int anvilquality) {
        this.anvilquality = anvilquality;
    }

    public int getAnvilQuality() {
        return anvilquality;
    }

    public float getLapisSaveChance() {
        return lapissavechance;
    }

    public void setLapisSaveChance(float value) {
        this.lapissavechance = value;
    }

    public float getExpRefundChance() {
        return exprefundchance;
    }

    public void setExpRefundChance(float value) {
        this.exprefundchance = value;
    }

    public float getVanillaExpGainMultiplier() {
        return vanillaexpgainmultiplier;
    }

    public void setVanillaExpGainMultiplier(float value) {
        this.vanillaexpgainmultiplier = value;
    }

    public float getExpRefundFraction() {
        return exprefundfraction;
    }

    public void setExpRefundFraction(float value) {
        this.exprefundfraction = value;
    }

    public double getEnchantingExpMultiplier(EnchantmentType type) {
        if (type == null) return enchantingexpmultiplier_general;
        switch (type){
            case CUSTOM:{
                return enchantingexpmultiplier_custom;
            }
            case VANILLA:{
                return enchantingexpmultiplier_vanilla;
            }
        }
        return enchantingexpmultiplier_general;
    }

    public void setEnchantingExpMultiplier(EnchantmentType type, double value) {
        if (type == null) {
            this.enchantingexpmultiplier_general = value;
            return;
        }
        switch (type){
            case CUSTOM:{
                this.enchantingexpmultiplier_custom = value;
                return;
            }
            case VANILLA:{
                this.enchantingexpmultiplier_vanilla = value;
                return;
            }
        }
        this.enchantingexpmultiplier_general = value;
    }

    public int getEnchantingSkill(EnchantmentType type) {
        if (type == null) return enchantingquality_general;
        switch (type){
            case CUSTOM:{
                return enchantingquality_custom;
            }
            case VANILLA:{
                return enchantingquality_vanilla;
            }
        }
        return enchantingquality_general;
    }

    public void setEnchantingSkill(EnchantmentType type, int value) {
        if (type == null) {
            this.enchantingquality_general = value;
            return;
        }
        switch (type){
            case CUSTOM:{
                this.enchantingquality_custom = value;
                return;
            }
            case VANILLA:{
                this.enchantingquality_vanilla = value;
                return;
            }
        }
        this.enchantingquality_general = value;
    }

    public float getVanillaEnchantmentAmplifyChance() {
        return vanillaenchantmentamplifychance;
    }

    public int getMaxCustomEnchantmentsAllowed() {
        return maxcustomenchantmentsallowed;
    }

    public void setVanillaEnchantmentAmplifyChance(float value) {
        this.vanillaenchantmentamplifychance = value;
    }

    public void setMaxCustomEnchantmentsAllowed(int value) {
        this.maxcustomenchantmentsallowed = value;
    }

    @Override
    public NamespacedKey getKey() {
        return enchantingProfileKey;
    }

    @Override
    public EnchantingProfile clone() throws CloneNotSupportedException {
        return (EnchantingProfile) super.clone();
    }

    public boolean canTransferEnchantments() {
        return cantransferenchantments;
    }

    public float getSharpnessBonus() {
        return sharpnessbonus;
    }

    public float getSmiteBonus() {
        return smitebonus;
    }

    public float getBoABonus() {
        return boabonus;
    }

    public float getFortuneBonus() {
        return fortunebonus;
    }

    public float getEfficiencyBonus() {
        return efficiencybonus;
    }

    public float getKnockbackBonus() {
        return knockbackbonus;
    }

    public float getLootingBonus() {
        return lootingbonus;
    }

    public float getFireAspectBonus() {
        return fireaspectbonus;
    }

    public float getPowerBonus() {
        return powerbonus;
    }

    public float getFlameBonus() {
        return flamebonus;
    }

    public float getBlastProtectionBonus() {
        return blastprotectionbonus;
    }

    public float getFeatherFallingBonus() {
        return featherfallingbonus;
    }

    public float getFireProtectionBonus() {
        return fireprotectionbonus;
    }

    public float getProjectileProtectionBonus() {
        return projectileprotectionbonus;
    }

    public float getProtectionBonus() {
        return protectionbonus;
    }

    public float getSoulSpeedBonus() {
        return soulspeedbonus;
    }

    public float getThornsBonus() {
        return thornsbonus;
    }

    public float getLureBonus() {
        return lurebonus;
    }

    public float getLotSBonus() {
        return lotsbonus;
    }

    public float getImpalingBonus() {
        return impalingbonus;
    }

    public float getSwiftSneakBonus() {
        return swiftsneakbonus;
    }

    public void setCanTransferEnchantments(boolean cantransferenchantments) {
        this.cantransferenchantments = cantransferenchantments;
    }

    public void setSharpnessBonus(float sharpnessbonus) {
        this.sharpnessbonus = sharpnessbonus;
    }

    public void setSmiteBonus(float smitebonus) {
        this.smitebonus = smitebonus;
    }

    public void setBoABonus(float boabonus) {
        this.boabonus = boabonus;
    }

    public void setFortuneBonus(float fortunebonus) {
        this.fortunebonus = fortunebonus;
    }

    public void setEfficiencyBonus(float efficiencybonus) {
        this.efficiencybonus = efficiencybonus;
    }

    public void setKnockbackBonus(float knockbackbonus) {
        this.knockbackbonus = knockbackbonus;
    }

    public void setLootingBonus(float lootingbonus) {
        this.lootingbonus = lootingbonus;
    }

    public void setFireAspectBonus(float fireaspectbonus) {
        this.fireaspectbonus = fireaspectbonus;
    }

    public void setPowerBonus(float powerbonus) {
        this.powerbonus = powerbonus;
    }

    public void setFlameBonus(float flamebonus) {
        this.flamebonus = flamebonus;
    }

    public void setBlastProtectionBonus(float blastprotectionbonus) {
        this.blastprotectionbonus = blastprotectionbonus;
    }

    public void setFeatherFallingBonus(float featherfallingbonus) {
        this.featherfallingbonus = featherfallingbonus;
    }

    public void setFireProtectionBonus(float fireprotectionbonus) {
        this.fireprotectionbonus = fireprotectionbonus;
    }

    public void setProjectileProtectionBonus(float projectileprotectionbonus) {
        this.projectileprotectionbonus = projectileprotectionbonus;
    }

    public void setProtectionBonus(float protectionbonus) {
        this.protectionbonus = protectionbonus;
    }

    public void setSoulSpeedBonus(float soulspeedbonus) {
        this.soulspeedbonus = soulspeedbonus;
    }

    public void setThornsBonus(float thornsbonus) {
        this.thornsbonus = thornsbonus;
    }

    public void setLureBonus(float lurebonus) {
        this.lurebonus = lurebonus;
    }

    public void setLotSBonus(float lotsbonus) {
        this.lotsbonus = lotsbonus;
    }

    public void setImpalingBonus(float impalingbonus) {
        this.impalingbonus = impalingbonus;
    }

    public void setSwiftSneakBonus(float swiftsneakbonus) {
        this.swiftsneakbonus = swiftsneakbonus;
    }
}
