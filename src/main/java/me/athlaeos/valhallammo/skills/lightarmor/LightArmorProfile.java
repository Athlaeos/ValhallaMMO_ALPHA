package me.athlaeos.valhallammo.skills.lightarmor;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class LightArmorProfile extends Profile implements Serializable {
    private static final NamespacedKey lightArmorProfileKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_light_armor");

    private float movementspeedpenalty = 0F; // movement speed penalty per piece of light armor
    private float damageresistance = 0F; // damage resistance per piece of light armor
    private float meleedamageresistance = 0F; // melee damage resistance per piece of light armor
    private float projectiledamageresistance = 0F; // projectile damage resistance per piece of light armor
    private float fireresistance = 0F; // fire damage resistance per piece of light armor
    private float magicresistance = 0F; // magic damage resistance per piece of light armor
    private float explosionresistance = 0F; // explosion damage resistance per piece of light armor
    private float falldamageresistance = 0F; // fall damage resistance per piece of light armor
    private float poisonresistance = 0F; // poison/wither damage resistance per piece of light armor
    private float knockbackresistance = 0F; // knockback reduction per piece of light armor
    private float bleedresistance = 0F; // bleed resistance per piece of light armor
    private float lightarmormultiplier = 1F; // armor multiplier for worn light armor pieces
    private float fullarmormultiplierbonus = 0F; // armor multiplier when player wearing full light armor
    private float fullarmorhungersavechance = 0F; // chance to not consume hunger points when wearing full light armor
    private float fullarmordodgechance = 0F; // chance to avoid damage from an entity when wearing full light armor
    private float fullarmorhealingbonus = 0F; // additional healing when wearing full light armor
    private float fullarmorbleedresistance = 0F; // additional bleeding resistance when wearing full light armor
    private int armorpiecesforbonusses = 4; // amount of armor pieces the player needs to wear to benefit from
    // "full set" bonusses
    private Collection<String> immunepotioneffects = new HashSet<>(); // potion effect immunity types
    private float adrenalinethreshold = 0F; // fraction of health the player must reach to activate adrenaline
    private int adrenalinecooldown = -1; // cooldown of adrenaline after activation
    private int adrenalinelevel = 0; // level of adrenaline, impact defined in skill config

    private double expmultiplier = 100D;

    public LightArmorProfile(Player owner){
        super(owner);
        if (owner == null) return;
        this.key = lightArmorProfileKey;
    }

    @Override
    public void setDefaultStats(Player player) {
        Skill skill = SkillProgressionManager.getInstance().getSkill("LIGHT_ARMOR");
        if (skill != null){
            if (skill instanceof LightArmorSkill){
                LightArmorSkill lightArmorSkill = (LightArmorSkill) skill;
                for (PerkReward startingPerk : lightArmorSkill.getStartingPerks()){
                    startingPerk.execute(player);
                }
            }
        }
    }

    @Override
    public void createProfileTable(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS profiles_light_armor (" +
                "owner VARCHAR(40) PRIMARY KEY," +
                "level SMALLINT default 0," +
                "exp DOUBLE default 0," +
                "exp_total DOUBLE default 0," +
                "movementspeedpenalty FLOAT DEFAULT 0," +
                "damageresistance FLOAT DEFAULT 0," +
                "meleeresistance FLOAT DEFAULT 0," +
                "projectileresistance FLOAT DEFAULT 0," +
                "fireresistance FLOAT DEFAULT 0," +
                "magicresistance FLOAT DEFAULT 0," +
                "explosionresistance FLOAT DEFAULT 0," +
                "falldamageresistance FLOAT DEFAULT 0, " +
                "poisonresistance FLOAT DEFAULT 0, " +
                "knockbackresistance FLOAT DEFAULT 0," +
                "lightarmormultiplier FLOAT DEFAULT 1," +
                "fullarmormultiplierbonus FLOAT DEFAULT 0," +
                "fullarmorhungersavechance FLOAT DEFAULT 0," +
                "fullarmordodgechance FLOAT DEFAULT 0," +
                "fullarmorhealingbonus FLOAT DEFAULT 0," +
                "armorpiecesforbonusses TINYINT DEFAULT 4," +
                "immunepotioneffects TEXT default ''," +
                "adrenalinethreshold FLOAT DEFAULT 0," +
                "adrenalinecooldown INT DEFAULT -1," +
                "adrenalinelevel SMALLINT DEFAULT 0," +
                "expmultiplier DOUBLE DEFAULT 100);");
        stmt.execute();

        conn.addColumnIfNotExists("profiles_light_armor", "bleedresistance", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_light_armor", "fullarmorbleedresistance", "FLOAT DEFAULT 0");
    }

    @Override
    public void insertOrUpdateProfile(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement(
                "REPLACE INTO profiles_light_armor " +
                        "(owner, level, exp, exp_total, movementspeedpenalty, damageresistance, meleeresistance, " +
                        "projectileresistance, fireresistance, magicresistance, explosionresistance, falldamageresistance, " +
                        "poisonresistance, knockbackresistance, bleedresistance, lightarmormultiplier, " +
                        "fullarmormultiplierbonus, fullarmorhungersavechance, fullarmordodgechance, fullarmorhealingbonus, " +
                        "fullarmorbleedresistance, armorpiecesforbonusses, immunepotioneffects, adrenalinethreshold, " +
                        "adrenalinecooldown, adrenalinelevel, expmultiplier) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        stmt.setString(1, owner.toString());
        stmt.setInt(2, level);
        stmt.setDouble(3, exp);
        stmt.setDouble(4, lifetimeEXP);
        stmt.setFloat(5, movementspeedpenalty);
        stmt.setFloat(6, damageresistance);
        stmt.setFloat(7, meleedamageresistance);
        stmt.setFloat(8, projectiledamageresistance);
        stmt.setFloat(9, fireresistance);
        stmt.setFloat(10, magicresistance);
        stmt.setFloat(11, explosionresistance);
        stmt.setFloat(12, falldamageresistance);
        stmt.setFloat(13, poisonresistance);
        stmt.setFloat(14, knockbackresistance);
        stmt.setFloat(15, bleedresistance);
        stmt.setFloat(16, lightarmormultiplier);
        stmt.setFloat(17, fullarmormultiplierbonus);
        stmt.setFloat(18, fullarmorhungersavechance);
        stmt.setFloat(19, fullarmordodgechance);
        stmt.setFloat(20, fullarmorhealingbonus);
        stmt.setFloat(21, fullarmorbleedresistance);
        stmt.setInt(22, armorpiecesforbonusses);
        stmt.setString(23, String.join("<>", immunepotioneffects));
        stmt.setFloat(24, adrenalinethreshold);
        stmt.setInt(25, adrenalinecooldown);
        stmt.setInt(26, adrenalinelevel);
        stmt.setDouble(27, expmultiplier);
        stmt.execute();
    }

    @Override
    public Profile fetchProfile(Player p, DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement("SELECT * FROM profiles_light_armor WHERE owner = ?;");
        stmt.setString(1, p.getUniqueId().toString());
        ResultSet result = stmt.executeQuery();
        if (result.next()){
            LightArmorProfile profile = new LightArmorProfile(p);
            profile.setLevel(result.getInt("level"));
            profile.setExp(result.getDouble("exp"));
            profile.setLifetimeEXP(result.getDouble("exp_total"));
            profile.setMovementSpeedPenalty(result.getFloat("movementspeedpenalty"));
            profile.setDamageResistance(result.getFloat("damageresistance"));
            profile.setMeleeDamageResistance(result.getFloat("meleeresistance"));
            profile.setProjectileDamageResistance(result.getFloat("projectileresistance"));
            profile.setFireResistance(result.getFloat("fireresistance"));
            profile.setMagicResistance(result.getFloat("magicresistance"));
            profile.setExplosionResistance(result.getFloat("explosionresistance"));
            profile.setFallDamageResistance(result.getFloat("falldamageresistance"));
            profile.setPoisonResistance(result.getFloat("poisonresistance"));
            profile.setKnockbackResistance(result.getFloat("knockbackresistance"));
            profile.setBleedResistance(result.getFloat("bleedresistance"));
            profile.setLightArmorMultiplier(result.getFloat("lightarmormultiplier"));
            profile.setFullArmorMultiplierBonus(result.getFloat("fullarmormultiplierbonus"));
            profile.setFullArmorHungerSaveChance(result.getFloat("fullarmorhungersavechance"));
            profile.setFullArmorDodgeChance(result.getFloat("fullarmordodgechance"));
            profile.setFullArmorHealingBonus(result.getFloat("fullarmorhealingbonus"));
            profile.setFullArmorBleedResistance(result.getFloat("fullarmorbleedresistance"));
            profile.setArmorPiecesForBonusses(result.getInt("armorpiecesforbonusses"));
            profile.setImmunePotionEffects(new HashSet<>(Arrays.asList(result.getString("immunepotioneffects").split("<>"))));
            profile.setAdrenalineThreshold(result.getFloat("adrenalinethreshold"));
            profile.setAdrenalineCooldown(result.getInt("adrenalinecooldown"));
            profile.setAdrenalineLevel(result.getInt("adrenalinelevel"));
            profile.setExpMultiplier(result.getDouble("expmultiplier"));
            return profile;
        }
        return null;
    }

    public float getBleedResistance() {
        return bleedresistance;
    }

    public void setBleedResistance(float bleedresistance) {
        this.bleedresistance = bleedresistance;
    }

    public float getFullArmorBleedResistance() {
        return fullarmorbleedresistance;
    }

    public void setFullArmorBleedResistance(float fullarmorbleedresistance) {
        this.fullarmorbleedresistance = fullarmorbleedresistance;
    }

    public void setFullArmorHealingBonus(float fullarmorhealingbonus) {
        this.fullarmorhealingbonus = fullarmorhealingbonus;
    }

    public float getFullArmorHealingBonus() {
        return fullarmorhealingbonus;
    }

    public void setFallDamageResistance(float falldamageresistance) {
        this.falldamageresistance = falldamageresistance;
    }

    public float getFallDamageResistance() {
        return falldamageresistance;
    }

    public void setPoisonResistance(float poisonresistance) {
        this.poisonresistance = poisonresistance;
    }

    public float getPoisonResistance() {
        return poisonresistance;
    }

    public void setExplosionResistance(float explosionresistance) {
        this.explosionresistance = explosionresistance;
    }

    public float getExplosionResistance() {
        return explosionresistance;
    }

    public void setMovementSpeedPenalty(float movementspeedpenalty) {
        this.movementspeedpenalty = movementspeedpenalty;
    }

    public void setDamageResistance(float damageresistance) {
        this.damageresistance = damageresistance;
    }

    public void setMeleeDamageResistance(float meleedamageresistance) {
        this.meleedamageresistance = meleedamageresistance;
    }

    public void setProjectileDamageResistance(float projectiledamageresistance) {
        this.projectiledamageresistance = projectiledamageresistance;
    }

    public void setFireResistance(float fireresistance) {
        this.fireresistance = fireresistance;
    }

    public void setMagicResistance(float magicresistance) {
        this.magicresistance = magicresistance;
    }

    public void setKnockbackResistance(float knockbackresistance) {
        this.knockbackresistance = knockbackresistance;
    }

    public void setLightArmorMultiplier(float lightarmormultiplier) {
        this.lightarmormultiplier = lightarmormultiplier;
    }

    public void setFullArmorMultiplierBonus(float fullarmormultiplierbonus) {
        this.fullarmormultiplierbonus = fullarmormultiplierbonus;
    }

    public void setFullArmorHungerSaveChance(float fullarmorhungersavechance) {
        this.fullarmorhungersavechance = fullarmorhungersavechance;
    }

    public void setFullArmorDodgeChance(float fullarmordodgechance) {
        this.fullarmordodgechance = fullarmordodgechance;
    }

    public void setArmorPiecesForBonusses(int armorpiecesforbonusses) {
        this.armorpiecesforbonusses = armorpiecesforbonusses;
    }

    public void setImmunePotionEffects(Collection<String> immunepotioneffects) {
        this.immunepotioneffects = immunepotioneffects;
    }

    public void setAdrenalineThreshold(float adrenalinethreshold) {
        this.adrenalinethreshold = adrenalinethreshold;
    }

    public void setAdrenalineCooldown(int adrenalinecooldown) {
        this.adrenalinecooldown = adrenalinecooldown;
    }

    public void setAdrenalineLevel(int adrenalinelevel) {
        this.adrenalinelevel = adrenalinelevel;
    }

    public void setExpMultiplier(double expmultiplier) {
        this.expmultiplier = expmultiplier;
    }

    public float getMovementSpeedPenalty() {
        return movementspeedpenalty;
    }

    public float getDamageResistance() {
        return damageresistance;
    }

    public float getMeleeDamageResistance() {
        return meleedamageresistance;
    }

    public float getProjectileDamageResistance() {
        return projectiledamageresistance;
    }

    public float getFireResistance() {
        return fireresistance;
    }

    public float getMagicResistance() {
        return magicresistance;
    }

    public float getKnockbackResistance() {
        return knockbackresistance;
    }

    public float getLightArmorMultiplier() {
        return lightarmormultiplier;
    }

    public float getFullArmorMultiplierBonus() {
        return fullarmormultiplierbonus;
    }

    public float getFullArmorHungerSaveChance() {
        return fullarmorhungersavechance;
    }

    public float getFullArmorDodgeChance() {
        return fullarmordodgechance;
    }

    public int getArmorPiecesForBonusses() {
        return armorpiecesforbonusses;
    }

    public Collection<String> getImmunePotionEffects() {
        return immunepotioneffects;
    }

    public float getAdrenalineThreshold() {
        return adrenalinethreshold;
    }

    public int getAdrenalineCooldown() {
        return adrenalinecooldown;
    }

    public int getAdrenalineLevel() {
        return adrenalinelevel;
    }

    public double getExpMultiplier() {
        return expmultiplier;
    }

    @Override
    public NamespacedKey getKey() {
        return lightArmorProfileKey;
    }

    @Override
    public LightArmorProfile clone() throws CloneNotSupportedException {
        return (LightArmorProfile) super.clone();
    }
}
