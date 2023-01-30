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
    }

    @Override
    public void insertOrUpdateProfile(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement(
                "REPLACE INTO profiles_enchanting " +
                        "(owner, level, exp, exp_total, vanillaenchantmentamplifychance, maxcustomenchantmentsallowed, " +
                        "lapissavechance, exprefundchance, exprefundfraction, vanillaexpgainmultiplier, enchantingquality_general, " +
                        "enchantingquality_vanilla, enchantingquality_custom, anvilquality, enchantingexpmultiplier_general, " +
                        "enchantingexpmultiplier_custom, enchantingexpmultiplier_vanilla) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
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
        stmt.setDouble(15, enchantingexpmultiplier_general);
        stmt.setDouble(16, enchantingexpmultiplier_custom);
        stmt.setDouble(17, enchantingexpmultiplier_vanilla);
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
}
