package me.athlaeos.valhallammo.skills.archery;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Profile;
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

public class ArcheryProfile extends Profile implements Serializable {
    private static final NamespacedKey archeryProfileKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_archery");

    private float bowdamagemultiplier = 1f;
    private float crossbowdamagemultiplier = 1f;
    private float bowcritchance = 0f;
    private float crossbowcritchance = 0f;
    private float ammosavechance = 0f;
    private boolean critonfacingaway = false;
    private boolean critonstandingstill = false;
    private boolean critonstealth = false;
    private float critdamagemultiplier = 1.5f;
    private float inaccuracy = 0f;
    private float damagedistancebasemultiplier = 1f;
    private float damagedistancemultiplier = 0f;
    private float stunchance = 0f;
    private int stunduration = 0;
    private boolean stunoncrit = false;
    private float infinitydamagemultiplier = 1f;
    private int chargedshotcooldown = -1;
    private int chargedshotknockbackbonus = 0;
    private float chargedshotdamagemultiplier = 1f;
    private boolean chargedshotfullvelocity = false;
    private float chargedshotvelocitybonus = 0f;
    private int chargedshotpiercingbonus = 0;
    private int chargedshotcharges = 0;

    private double bowexpmultiplier = 100D;
    private double crossbowexpmultiplier = 100D;
    private double generalexpmultiplier = 100D;

    public void setChargedShotCharges(int chargedshotcharges) {
        this.chargedshotcharges = chargedshotcharges;
    }

    public void setChargedShotFullVelocity(boolean chargedshotfullvelocity) {
        this.chargedshotfullvelocity = chargedshotfullvelocity;
    }

    public void setChargedShotPiercingBonus(int chargedshotpiercingbonus) {
        this.chargedshotpiercingbonus = chargedshotpiercingbonus;
    }

    public void setChargedShotVelocityBonus(float chargedshotvelocitybonus) {
        this.chargedshotvelocitybonus = chargedshotvelocitybonus;
    }

    public float getChargedShotVelocityBonus() {
        return chargedshotvelocitybonus;
    }

    public int getChargedShotCharges() {
        return chargedshotcharges;
    }

    public int getChargedShotPiercingBonus() {
        return chargedshotpiercingbonus;
    }

    public boolean isChargedShotFullVelocity() {
        return chargedshotfullvelocity;
    }

    public double getBowExpMultiplier() {
        return bowexpmultiplier;
    }

    public double getCrossBowExpMultiplier() {
        return crossbowexpmultiplier;
    }

    public double getGeneralExpMultiplier() {
        return generalexpmultiplier;
    }

    public float getBowDamageMultiplier() {
        return bowdamagemultiplier;
    }

    public float getCrossBowDamageMultiplier() {
        return crossbowdamagemultiplier;
    }

    public float getBowCritChance() {
        return bowcritchance;
    }

    public float getCrossBowCritChance() {
        return crossbowcritchance;
    }

    public float getAmmoSaveChance() {
        return ammosavechance;
    }

    public boolean isCritOnFacingAway() {
        return critonfacingaway;
    }

    public boolean isCritOnStandingStill() {
        return critonstandingstill;
    }

    public boolean isCritOnStealth() {
        return critonstealth;
    }

    public float getCritDamageMultiplier() {
        return critdamagemultiplier;
    }

    public float getInaccuracy() {
        return inaccuracy;
    }

    public float getDamageDistanceBaseMultiplier() {
        return damagedistancebasemultiplier;
    }

    public float getDamageDistanceMultiplier() {
        return damagedistancemultiplier;
    }

    public float getStunChance() {
        return stunchance;
    }

    public int getStunDuration() {
        return stunduration;
    }

    public boolean isStunoncrit() {
        return stunoncrit;
    }

    public float getInfinityDamageMultiplier() {
        return infinitydamagemultiplier;
    }

    public int getChargedShotCooldown() {
        return chargedshotcooldown;
    }

    public int getChargedShotKnockbackBonus() {
        return chargedshotknockbackbonus;
    }

    public float getChargedShotDamageMultiplier() {
        return chargedshotdamagemultiplier;
    }

    public void setBowDamageMultiplier(float bowdamagemultiplier) {
        this.bowdamagemultiplier = bowdamagemultiplier;
    }

    public void setCrossBowDamageMultiplier(float crossbowdamagemultiplier) {
        this.crossbowdamagemultiplier = crossbowdamagemultiplier;
    }

    public void setBowCritChance(float bowcritchance) {
        this.bowcritchance = bowcritchance;
    }

    public void setCrossBowCritChance(float crossbowcritchance) {
        this.crossbowcritchance = crossbowcritchance;
    }

    public void setAmmoSaveChance(float ammosavechance) {
        this.ammosavechance = ammosavechance;
    }

    public void setCritOnFacingAway(boolean critonfacingaway) {
        this.critonfacingaway = critonfacingaway;
    }

    public void setCritOnStandingStill(boolean critonstandingstill) {
        this.critonstandingstill = critonstandingstill;
    }

    public void setCritOnStealth(boolean critonstealth) {
        this.critonstealth = critonstealth;
    }

    public void setCritDamageMultiplier(float critdamagemultiplier) {
        this.critdamagemultiplier = critdamagemultiplier;
    }

    public void setInaccuracy(float inaccuracy) {
        this.inaccuracy = inaccuracy;
    }

    public void setDamageDistanceBaseMultiplier(float damagedistancebasemultiplier) {
        this.damagedistancebasemultiplier = damagedistancebasemultiplier;
    }

    public void setDamageDistanceMultiplier(float damagedistancemultiplier) {
        this.damagedistancemultiplier = damagedistancemultiplier;
    }

    public void setStunChance(float stunchance) {
        this.stunchance = stunchance;
    }

    public void setStunDuration(int stunduration) {
        this.stunduration = stunduration;
    }

    public void setStunOnCrit(boolean stunoncrit) {
        this.stunoncrit = stunoncrit;
    }

    public void setInfinityDamageMultiplier(float infinitydamagemultiplier) {
        this.infinitydamagemultiplier = infinitydamagemultiplier;
    }

    public void setChargedShotCooldown(int chargedshotcooldown) {
        this.chargedshotcooldown = chargedshotcooldown;
    }

    public void setChargedShotKnockbackBonus(int chargedshotknockbackbonus) {
        this.chargedshotknockbackbonus = chargedshotknockbackbonus;
    }

    public void setChargedShotDamageMultiplier(float chargedshotdamagemultiplier) {
        this.chargedshotdamagemultiplier = chargedshotdamagemultiplier;
    }

    public void setBowExpMultiplier(double bowexpmultiplier) {
        this.bowexpmultiplier = bowexpmultiplier;
    }

    public void setCrossbowExpMultiplier(double crossbowexpmultiplier) {
        this.crossbowexpmultiplier = crossbowexpmultiplier;
    }

    public void setGeneralExpMultiplier(double generalexpmultiplier) {
        this.generalexpmultiplier = generalexpmultiplier;
    }

    public ArcheryProfile(Player owner) {
        super(owner);
        if (owner == null) return;
        this.key = archeryProfileKey;
    }

    @Override
    public void setDefaultStats(Player player) {
        Skill skill = SkillProgressionManager.getInstance().getSkill("ARCHERY");
        if (skill != null) {
            if (skill instanceof ArcherySkill) {
                ArcherySkill archerySkill = (ArcherySkill) skill;
                for (PerkReward startingPerk : archerySkill.getStartingPerks()) {
                    startingPerk.execute(player);
                }
            }
        }
    }

    @Override
    public void createProfileTable(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS profiles_archery (" +
                "owner VARCHAR(40) PRIMARY KEY," +
                "level SMALLINT default 0," +
                "exp DOUBLE default 0," +
                "exp_total DOUBLE default 0," +
                "bowdamagemultiplier FLOAT DEFAULT 1," +
                "crossbowdamagemultiplier FLOAT DEFAULT 1, " +
                "bowcritchance FLOAT DEFAULT 0," +
                "crossbowcritchance FLOAT DEFAULT 0," +
                "ammosavechance FLOAT DEFAULT 0," +
                "critonfacingaway BOOL DEFAULT false," +
                "critonstandingstill BOOL DEFAULT false," +
                "critonstealth BOOL DEFAULT false," +
                "critdamagemultiplier FLOAT DEFAULT 1.5," +
                "inaccuracy FLOAT DEFAULT 0," +
                "damagedistancebasemultiplier FLOAT DEFAULT 1," +
                "damagedistancemultiplier FLOAT DEFAULT 0," +
                "stunchance FLOAT DEFAULT 0," +
                "stunduration SMALLINT DEFAULT 0," +
                "stunoncrit BOOL DEFAULT false," +
                "infinitydamagemultiplier FLOAT DEFAULT 1," +
                "chargedshotcooldown INT DEFAULT -1," +
                "chargedshotknockbackbonus TINYINT DEFAULT 0," +
                "chargedshotdamagemultiplier FLOAT DEFAULT 1," +
                "chargedshotfullvelocity BOOL DEFAULT false," +
                "chargedshotvelocitybonus FLOAT DEFAULT 0," +
                "chargedshotpiercingbonus TINYINT DEFAULT 0," +
                "chargedshotcharges TINYINT DEFAULT 0," +
                "bowexpmultiplier DOUBLE DEFAULT 100," +
                "crossbowexpmultiplier DOUBLE DEFAULT 100," +
                "generalexpmultiplier DOUBLE DEFAULT 100);");
        stmt.execute();


    }

    @Override
    public void insertOrUpdateProfile(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement(
                "REPLACE INTO profiles_archery " +
                        "(owner, level, exp, exp_total, bowdamagemultiplier, crossbowdamagemultiplier, bowcritchance, " +
                        "crossbowcritchance, ammosavechance, critonfacingaway, critonstandingstill, critonstealth," +
                        "critdamagemultiplier, inaccuracy, damagedistancebasemultiplier, damagedistancemultiplier, stunchance," +
                        "stunduration, stunoncrit, infinitydamagemultiplier, chargedshotcooldown, chargedshotknockbackbonus," +
                        "chargedshotdamagemultiplier, chargedshotfullvelocity, chargedshotvelocitybonus, chargedshotpiercingbonus, " +
                        "chargedshotcharges, bowexpmultiplier, crossbowexpmultiplier, generalexpmultiplier) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        stmt.setString(1, owner.toString());
        stmt.setInt(2, level);
        stmt.setDouble(3, exp);
        stmt.setDouble(4, lifetimeEXP);
        stmt.setFloat(5, bowdamagemultiplier);
        stmt.setFloat(6, crossbowdamagemultiplier);
        stmt.setFloat(7, bowcritchance);
        stmt.setFloat(8, crossbowcritchance);
        stmt.setFloat(9, ammosavechance);
        stmt.setBoolean(10, critonfacingaway);
        stmt.setBoolean(11, critonstandingstill);
        stmt.setBoolean(12, critonstealth);
        stmt.setFloat(13, critdamagemultiplier);
        stmt.setFloat(14, inaccuracy);
        stmt.setFloat(15, damagedistancebasemultiplier);
        stmt.setFloat(16, damagedistancemultiplier);
        stmt.setFloat(17, stunchance);
        stmt.setInt(18, stunduration);
        stmt.setBoolean(19, stunoncrit);
        stmt.setFloat(20, infinitydamagemultiplier);
        stmt.setInt(21, chargedshotcooldown);
        stmt.setInt(22, chargedshotknockbackbonus);
        stmt.setFloat(23, chargedshotdamagemultiplier);
        stmt.setBoolean(24, chargedshotfullvelocity);
        stmt.setFloat(25, chargedshotvelocitybonus);
        stmt.setInt(26, chargedshotpiercingbonus);
        stmt.setInt(27, chargedshotcharges);
        stmt.setDouble(28, bowexpmultiplier);
        stmt.setDouble(29, crossbowexpmultiplier);
        stmt.setDouble(30, generalexpmultiplier);
        stmt.execute();
    }

    @Override
    public Profile fetchProfile(Player p, DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement("SELECT * FROM profiles_archery WHERE owner = ?;");
        stmt.setString(1, p.getUniqueId().toString());
        ResultSet result = stmt.executeQuery();
        if (result.next()) {
            ArcheryProfile profile = new ArcheryProfile(p);
            profile.setLevel(result.getInt("level"));
            profile.setExp(result.getDouble("exp"));
            profile.setLifetimeEXP(result.getDouble("exp_total"));
            profile.setBowDamageMultiplier(result.getFloat("bowdamagemultiplier"));
            profile.setCrossBowDamageMultiplier(result.getFloat("crossbowdamagemultiplier"));
            profile.setBowCritChance(result.getFloat("bowcritchance"));
            profile.setCrossBowCritChance(result.getFloat("crossbowcritchance"));
            profile.setAmmoSaveChance(result.getFloat("ammosavechance"));
            profile.setCritOnFacingAway(result.getBoolean("critonfacingaway"));
            profile.setCritOnStandingStill(result.getBoolean("critonstandingstill"));
            profile.setCritOnStealth(result.getBoolean("critonstealth"));
            profile.setCritDamageMultiplier(result.getFloat("critdamagemultiplier"));
            profile.setInaccuracy(result.getFloat("inaccuracy"));
            profile.setDamageDistanceBaseMultiplier(result.getFloat("damagedistancebasemultiplier"));
            profile.setDamageDistanceMultiplier(result.getFloat("damagedistancemultiplier"));
            profile.setStunChance(result.getFloat("stunchance"));
            profile.setStunDuration(result.getInt("stunduration"));
            profile.setStunOnCrit(result.getBoolean("stunoncrit"));
            profile.setInfinityDamageMultiplier(result.getFloat("infinitydamagemultiplier"));
            profile.setChargedShotCooldown(result.getInt("chargedshotcooldown"));
            profile.setChargedShotKnockbackBonus(result.getInt("chargedshotknockbackbonus"));
            profile.setChargedShotDamageMultiplier(result.getFloat("chargedshotdamagemultiplier"));
            profile.setChargedShotFullVelocity(result.getBoolean("chargedshotfullvelocity"));
            profile.setChargedShotVelocityBonus(result.getFloat("chargedshotvelocitybonus"));
            profile.setChargedShotPiercingBonus(result.getInt("chargedshotpiercingbonus"));
            profile.setChargedShotCharges(result.getInt("chargedshotcharges"));
            profile.setBowExpMultiplier(result.getDouble("bowexpmultiplier"));
            profile.setCrossbowExpMultiplier(result.getDouble("crossbowexpmultiplier"));
            profile.setGeneralExpMultiplier(result.getDouble("generalexpmultiplier"));
            return profile;
        }
        return null;
    }

    @Override
    public NamespacedKey getKey() {
        return archeryProfileKey;
    }

    @Override
    public ArcheryProfile clone() throws CloneNotSupportedException {
        return (ArcheryProfile) super.clone();
    }
}
