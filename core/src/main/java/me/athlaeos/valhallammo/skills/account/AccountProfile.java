package me.athlaeos.valhallammo.skills.account;

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
import java.util.HashSet;
import java.util.Set;

public class AccountProfile extends Profile implements Serializable{

    private static final NamespacedKey accountProfileKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_account");

    private int spendableSkillPoints = 0;
    private int redeemableLevelTokens = 0;
    private double allSkillEXPGain = 100;
    private Set<String> unlockedPerks = new HashSet<>();
    private Set<String> unlockedRecipes = new HashSet<>();
    private float healthBonus = 0F;
    private float movementSpeedBonus = 0F;
    private float knockbackResistanceBonus = 0F;
    private float armorBonus = 0F;
    private float armorMultiplierBonus = 0F;
    private float toughnessBonus = 0F;
    private float attackDamageBonus = 0F;
    private float attackSpeedBonus = 0F;
    private float luckBonus = 0F;
    private float healthRegenerationBonus = 0F;
    private float hungerSaveChance = 0F;
    private float damageResistance = 0F;
    private float meleeResistance = 0F;
    private float projectileResistance = 0F;
    private float fireResistance = 0F;
    private float explosionResistance = 0F;
    private float magicResistance = 0F;
    private float poisonResistance = 0F;
    private float fallDamageResistance = 0F;
    private float cooldownReduction = 0F;
    private int immunityframebonus = 0;
    private float stunresistance = 0F;
    private float bleedresistance = 0F;

    @Override
    public void createProfileTable(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS profiles_account (" +
                "owner VARCHAR(40) PRIMARY KEY," +
                "level SMALLINT default 0," +
                "exp DOUBLE default 0," +
                "exp_total DOUBLE default 0," +
                "skill_points VARCHAR(40) DEFAULT 0," +
                "exp_gain_multiplier DOUBLE DEFAULT 100.0, " +
                "unlocked_perks TEXT," +
                "unlocked_recipes TEXT," +
                "healthbonus FLOAT DEFAULT 0," +
                "movementspeedbonus FLOAT DEFAULT 0," +
                "knockbackresistancebonus FLOAT DEFAULT 0," +
                "armorbonus FLOAT DEFAULT 0," +
                "toughnessbonus FLOAT DEFAULT 0," +
                "attackdamagebonus FLOAT DEFAULT 0," +
                "attackspeedbonus FLOAT DEFAULT 0," +
                "luckbonus FLOAT DEFAULT 0," +
                "healthregenerationbonus FLOAT DEFAULT 0," +
                "hungersavechance FLOAT DEFAULT 0," +
                "damageresistance FLOAT DEFAULT 0," +
                "meleeresistance FLOAT DEFAULT 0," +
                "projectileresistance FLOAT DEFAULT 0," +
                "fireresistance FLOAT DEFAULT 0," +
                "explosionresistance FLOAT DEFAULT 0," +
                "magicresistance FLOAT DEFAULT 0," +
                "poisonresistance FLOAT DEFAULT 0," +
                "falldamageresistance FLOAT DEFAULT 0," +
                "cooldownreduction FLOAT DEFAULT 0);");
        stmt.execute();

        conn.addColumnIfNotExists("profiles_account", "immunityframebonus", "SMALLINT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_account", "stunresistance", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_account", "armormultiplierbonus", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_account", "bleedresistance", "FLOAT DEFAULT 0");
        conn.addColumnIfNotExists("profiles_account", "redeemableleveltokens", "SMALLINT DEFAULT 0"); // TODO
    }

    @Override
    public void insertOrUpdateProfile(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement(
                "REPLACE INTO profiles_account " +
                        "(owner, level, exp, exp_total, skill_points, exp_gain_multiplier, unlocked_perks, " +
                        "unlocked_recipes, healthbonus, movementspeedbonus, knockbackresistancebonus, " +
                        "armorbonus, armormultiplierbonus, toughnessbonus, attackdamagebonus, attackspeedbonus, luckbonus, " +
                        "healthregenerationbonus, hungersavechance, damageresistance, meleeresistance, " +
                        "projectileresistance, fireresistance, explosionresistance, magicresistance, " +
                        "poisonresistance, falldamageresistance, cooldownreduction, immunityframebonus, " +
                        "stunresistance, bleedresistance, redeemableleveltokens) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        stmt.setString(1, owner.toString());
        stmt.setInt(2, level);
        stmt.setDouble(3, exp);
        stmt.setDouble(4, lifetimeEXP);
        stmt.setInt(5, spendableSkillPoints);
        stmt.setDouble(6, allSkillEXPGain);
        stmt.setString(7, String.join("<>", unlockedPerks));
        stmt.setString(8, String.join("<>", unlockedRecipes));
        stmt.setFloat(9, healthBonus);
        stmt.setFloat(10, movementSpeedBonus);
        stmt.setFloat(11, knockbackResistanceBonus);
        stmt.setFloat(12, armorBonus);
        stmt.setFloat(13, armorMultiplierBonus);
        stmt.setFloat(14, toughnessBonus);
        stmt.setFloat(15, attackDamageBonus);
        stmt.setFloat(16, attackSpeedBonus);
        stmt.setFloat(17, luckBonus);
        stmt.setFloat(18, healthRegenerationBonus);
        stmt.setFloat(19, hungerSaveChance);
        stmt.setFloat(20, damageResistance);
        stmt.setFloat(21, meleeResistance);
        stmt.setFloat(22, projectileResistance);
        stmt.setFloat(23, fireResistance);
        stmt.setFloat(24, explosionResistance);
        stmt.setFloat(25, magicResistance);
        stmt.setFloat(26, poisonResistance);
        stmt.setFloat(27, fallDamageResistance);
        stmt.setFloat(28, cooldownReduction);
        stmt.setFloat(29, immunityframebonus);
        stmt.setFloat(30, stunresistance);
        stmt.setFloat(31, bleedresistance);
        stmt.setInt(32, redeemableLevelTokens);
        stmt.execute();
    }

    @Override
    public Profile fetchProfile(Player p, DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement("SELECT * FROM profiles_account WHERE owner = ?;");
        stmt.setString(1, p.getUniqueId().toString());
        ResultSet result = stmt.executeQuery();
        if (result.next()){
            AccountProfile profile = new AccountProfile(p);
            profile.setOwner(p.getUniqueId());
            profile.setLevel(result.getInt("level"));
            profile.setExp(result.getDouble("exp"));
            profile.setLifetimeEXP(result.getDouble("exp_total"));
            profile.setSpendableSkillPoints(result.getInt("skill_points"));
            profile.setAllSkillEXPGain(result.getDouble("exp_gain_multiplier"));
            profile.setUnlockedPerks(new HashSet<>(Arrays.asList(result.getString("unlocked_perks").split("<>"))));
            profile.setUnlockedRecipes(new HashSet<>(Arrays.asList(result.getString("unlocked_recipes").split("<>"))));
            profile.setHealthBonus(result.getFloat("healthbonus"));
            profile.setMovementSpeedBonus(result.getFloat("movementspeedbonus"));
            profile.setKnockbackResistanceBonus(result.getFloat("knockbackresistancebonus"));
            profile.setArmorBonus(result.getFloat("armorbonus"));
            profile.setArmorMultiplierBonus(result.getFloat("armormultiplierbonus"));
            profile.setToughnessBonus(result.getFloat("toughnessbonus"));
            profile.setAttackDamageBonus(result.getFloat("attackdamagebonus"));
            profile.setAttackSpeedBonus(result.getFloat("attackspeedbonus"));
            profile.setLuckBonus(result.getFloat("luckbonus"));
            profile.setHealthRegenerationBonus(result.getFloat("healthregenerationbonus"));
            profile.setHungerSaveChance(result.getFloat("hungersavechance"));
            profile.setDamageResistance(result.getFloat("damageresistance"));
            profile.setMeleeResistance(result.getFloat("meleeresistance"));
            profile.setProjectileResistance(result.getFloat("projectileresistance"));
            profile.setFireResistance(result.getFloat("fireresistance"));
            profile.setExplosionResistance(result.getFloat("explosionresistance"));
            profile.setMagicResistance(result.getFloat("magicresistance"));
            profile.setPoisonResistance(result.getFloat("poisonresistance"));
            profile.setFallDamageResistance(result.getFloat("falldamageresistance"));
            profile.setCooldownReduction(result.getFloat("cooldownreduction"));
            profile.setImmunityFrameBonus(result.getInt("immunityframebonus"));
            profile.setStunResistance(result.getFloat("stunresistance"));
            profile.setBleedResistance(result.getFloat("bleedresistance"));
            profile.setRedeemableLevelTokens(result.getInt("redeemableleveltokens"));
            return profile;
        }
        return null;
    }

    public AccountProfile(Player owner){
        super(owner);
        if (owner == null) return;
        key = accountProfileKey;
    }

    public float getArmorMultiplierBonus() {
        return armorMultiplierBonus;
    }

    public void setArmorMultiplierBonus(float armorMultiplierBonus) {
        this.armorMultiplierBonus = armorMultiplierBonus;
    }

    public void setBleedResistance(float bleedresistance) {
        this.bleedresistance = bleedresistance;
    }

    public float getBleedResistance() {
        return bleedresistance;
    }

    @Override
    public void setDefaultStats(Player player) {
        Skill skill = SkillProgressionManager.getInstance().getSkill("ACCOUNT");
        if (skill != null){
            if (skill instanceof AccountSkill){
                AccountSkill accountSkill = (AccountSkill) skill;
                for (PerkReward startingPerk : accountSkill.getStartingPerks()){
                    startingPerk.execute(player);
                }
            }
        }
    }

    public float getStunResistance() {
        return stunresistance;
    }

    public int getRedeemableLevelTokens() {
        return redeemableLevelTokens;
    }

    public void setRedeemableLevelTokens(int redeemableLevelTokens) {
        this.redeemableLevelTokens = redeemableLevelTokens;
    }

    public void setStunResistance(float stunimmunity) {
        this.stunresistance = stunimmunity;
    }

    public int getImmunityFrameBonus() {
        return immunityframebonus;
    }

    public void setImmunityFrameBonus(int immunityframebonus) {
        this.immunityframebonus = immunityframebonus;
    }

    public float getHealthRegenerationBonus() {
        return healthRegenerationBonus;
    }

    public float getHungerSaveChance() {
        return hungerSaveChance;
    }

    public float getDamageResistance() {
        return damageResistance;
    }

    public float getMeleeResistance() {
        return meleeResistance;
    }

    public float getProjectileResistance() {
        return projectileResistance;
    }

    public float getFireResistance() {
        return fireResistance;
    }

    public float getExplosionResistance() {
        return explosionResistance;
    }

    public float getMagicResistance() {
        return magicResistance;
    }

    public float getPoisonResistance() {
        return poisonResistance;
    }

    public float getFallDamageResistance() {
        return fallDamageResistance;
    }

    public float getCooldownReduction() {
        return cooldownReduction;
    }

    public void setHealthRegenerationBonus(float healthRegenerationBonus) {
        this.healthRegenerationBonus = healthRegenerationBonus;
    }

    public void setHungerSaveChance(float hungerSaveChance) {
        this.hungerSaveChance = hungerSaveChance;
    }

    public void setDamageResistance(float damageResistance) {
        this.damageResistance = damageResistance;
    }

    public void setMeleeResistance(float meleeResistance) {
        this.meleeResistance = meleeResistance;
    }

    public void setProjectileResistance(float projectileResistance) {
        this.projectileResistance = projectileResistance;
    }

    public void setFireResistance(float fireResistance) {
        this.fireResistance = fireResistance;
    }

    public void setExplosionResistance(float explosionResistance) {
        this.explosionResistance = explosionResistance;
    }

    public void setMagicResistance(float magicResistance) {
        this.magicResistance = magicResistance;
    }

    public void setPoisonResistance(float poisonResistance) {
        this.poisonResistance = poisonResistance;
    }

    public void setFallDamageResistance(float fallDamageResistance) {
        this.fallDamageResistance = fallDamageResistance;
    }

    public void setCooldownReduction(float cooldownReduction) {
        this.cooldownReduction = cooldownReduction;
    }

    public void setHealthBonus(float healthBonus) {
        this.healthBonus = healthBonus;
    }

    public void setMovementSpeedBonus(float movementSpeedBonus) {
        this.movementSpeedBonus = movementSpeedBonus;
    }

    public void setKnockbackResistanceBonus(float knockbackResistanceBonus) {
        this.knockbackResistanceBonus = knockbackResistanceBonus;
    }

    public void setArmorBonus(float armorBonus) {
        this.armorBonus = armorBonus;
    }

    public void setToughnessBonus(float toughnessBonus) {
        this.toughnessBonus = toughnessBonus;
    }

    public void setAttackDamageBonus(float attackDamageBonus) {
        this.attackDamageBonus = attackDamageBonus;
    }

    public void setAttackSpeedBonus(float attackSpeedBonus) {
        this.attackSpeedBonus = attackSpeedBonus;
    }

    public void setLuckBonus(float luckBonus) {
        this.luckBonus = luckBonus;
    }

    public float getHealthBonus() {
        return healthBonus;
    }

    public float getMovementSpeedBonus() {
        return movementSpeedBonus;
    }

    public float getKnockbackResistanceBonus() {
        return knockbackResistanceBonus;
    }

    public float getArmorBonus() {
        return armorBonus;
    }

    public float getToughnessBonus() {
        return toughnessBonus;
    }

    public float getAttackDamageBonus() {
        return attackDamageBonus;
    }

    public float getAttackSpeedBonus() {
        return attackSpeedBonus;
    }

    public float getLuckBonus() {
        return luckBonus;
    }

    public void setSpendableSkillPoints(int spendableSkillPoints) {
        this.spendableSkillPoints = spendableSkillPoints;
    }

    public void setUnlockedPerks(Set<String> unlockedPerks) {
        this.unlockedPerks = unlockedPerks;
    }

    public void setUnlockedRecipes(Set<String> unlockedRecipes) {
        this.unlockedRecipes = unlockedRecipes;
    }

    public int getSpendableSkillPoints() {
        return spendableSkillPoints;
    }

    public Set<String> getUnlockedPerks() {
        return unlockedPerks;
    }

    public Set<String> getUnlockedRecipes() {
        return unlockedRecipes;
    }

    public double getAllSkillEXPGain() {
        return allSkillEXPGain;
    }

    public void setAllSkillEXPGain(double allSkillEXPGain) {
        this.allSkillEXPGain = allSkillEXPGain;
    }

    @Override
    public NamespacedKey getKey() {
        return accountProfileKey;
    }

    @Override
    public AccountProfile clone() throws CloneNotSupportedException {
        return (AccountProfile) super.clone();
    }
}
