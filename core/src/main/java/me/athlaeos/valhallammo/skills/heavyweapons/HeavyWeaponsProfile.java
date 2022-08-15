package me.athlaeos.valhallammo.skills.heavyweapons;

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

public class HeavyWeaponsProfile extends Profile implements Serializable {
    private static final NamespacedKey heavyWeaponsProfileKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_heavy_weapons");

    private float damagemultiplier = 1F;
    private float attackspeedbonus = 0F;
    private float knockbackbonus = 0F;
    private float damagebonuslightarmor = 0F;
    private float damagebonusheavyarmor = 0F;
    private float flatlightarmorignored = 0F;
    private float flatheavyarmorignored = 0F;
    private float flatarmorignored = 0F;
    private float fractionlightarmorignored = 0F;
    private float fractionheavyarmorignored = 0F;
    private float fractionarmorignored = 0F;
    private float immunityframereduction = 0F;
    private int parrytimeframe = 0;
    private int parryvulnerableframe = 0;
    private float parrydamagereduction = 0F;
    private int parrycooldown = -1;
    private int enemydebuffduration = 0;
    private int faileddebuffduration = 0;
    private boolean bleedoncrit = false;
    private float bleedchance = 0F;
    private float bleeddamage = 0F;
    private int bleedduration = 0;
    private float critchance = 0F;
    private boolean critonbleed = false;
    private float critdamagemultiplier = 1F;
    private boolean critonstealth = false;
    private boolean critonstun = false;
    private boolean unlockedweaponcoating = false; // determines if the player can coat their heavy weapons with potion effects
    private float selfpotiondurationmultiplier = 0F; // the multiplier of the original potion duration determining how long the weapon will stay coated for
    private float enemypotiondurationmultiplier = 0F; // the multiplier of the original potion duration determining the debuff inflicted on the enemy
    private float enemypotionamplifiermultiplier = 0F; // the multiplier of the original potion amplifier determining the debuff inflicted on the enemy
    private int crushingblowcooldown = -1;
    private float crushingblowradius = 0;
    private float crushingblowdamagemultiplier = 0;
    private boolean crushingblowoncrit = false;
    private boolean crushingblowonfalling = false;
    private float crushingblowchance = 0F;
    private float stunchance = 0F;
    private int stunduration = 0;
    private float dropbonus = 0F;
    private float raredropmultiplier = 0F;

    private double expmultiplier = 100D;

    public HeavyWeaponsProfile(Player owner){
        super(owner);
        if (owner == null) return;
        this.key = heavyWeaponsProfileKey;
    }

    @Override
    public void setDefaultStats(Player player) {
        Skill skill = SkillProgressionManager.getInstance().getSkill("HEAVY_WEAPONS");
        if (skill != null){
            if (skill instanceof HeavyWeaponsSkill){
                HeavyWeaponsSkill heavyWeaponsSkill = (HeavyWeaponsSkill) skill;
                for (PerkReward startingPerk : heavyWeaponsSkill.getStartingPerks()){
                    startingPerk.execute(player);
                }
            }
        }
    }

    @Override
    public void createProfileTable(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS profiles_heavy_weapons (" +
                "owner VARCHAR(40) PRIMARY KEY, " +
                "level SMALLINT default 0, " +
                "exp DOUBLE default 0, " +
                "exp_total DOUBLE default 0, " +
                "damagemultiplier FLOAT DEFAULT 1, " +
                "attackspeedbonus FLOAT DEFAULT 0, " +
                "knockbackbonus FLOAT DEFAULT 0, " +
                "damagebonuslightarmor FLOAT DEFAULT 0, " +
                "damagebonusheavyarmor FLOAT DEFAULT 0, " +
                "flatlightarmorignored FLOAT DEFAULT 0, " +
                "flatheavyarmorignored FLOAT DEFAULT 0, " +
                "flatarmorignored FLOAT DEFAULT 0, " +
                "fractionlightarmorignored FLOAT DEFAULT 0, " +
                "fractionheavyarmorignored FLOAT DEFAULT 0, " +
                "fractionarmorignored FLOAT DEFAULT 0, " +
                "immunityframereduction FLOAT DEFAULT 0, " +
                "parrytimeframe INT DEFAULT 0, " +
                "parryvulnerableframe INT DEFAULT 0, " +
                "parrydamagereduction FLOAT DEFAULT 0, " +
                "parrycooldown INT DEFAULT -1, " +
                "enemydebuffduration INT DEFAULT 0, " +
                "faileddebuffduration INT DEFAULT 0, " +
                "bleedoncrit BOOL DEFAULT false, " +
                "bleedchance FLOAT DEFAULT 0, " +
                "bleeddamage FLOAT DEFAULT 0, " +
                "bleedduration INT DEFAULT 0, " +
                "critchance FLOAT DEFAULT 0, " +
                "critonbleed BOOL DEFAULT false, " +
                "critdamagemultiplier FLOAT DEFAULT 1 ," +
                "critonstealth BOOL DEFAULT false, " +
                "critonstun BOOL DEFAULT false, " +
                "unlockedweaponcoating BOOL DEFAULT false, " +
                "selfpotiondurationmultiplier FLOAT DEFAULT 0, " +
                "enemypotiondurationmultiplier FLOAT DEFAULT 0, " +
                "enemypotionamplifiermultiplier FLOAT DEFAULT 0, " +
                "crushingblowcooldown INT DEFAULT -1, " +
                "crushingblowradius FLOAT DEFAULT 0, " +
                "crushingblowdamagemultiplier FLOAT DEFAULT 0, " +
                "crushingblowoncrit BOOL DEFAULT false, " +
                "crushingblowonfalling BOOL DEFAULT false, " +
                "crushingblowchance FLOAT DEFAULT 0, " +
                "stunchance FLOAT DEFAULT 0, " +
                "stunduration INT DEFAULT 0, " +
                "dropbonus FLOAT DEFAULT 0, " +
                "raredropmultiplier FLOAT DEFAULT 0, " +
                "expmultiplier DOUBLE DEFAULT 100);");
        stmt.execute();
    }

    @Override
    public void insertOrUpdateProfile(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement(
                "REPLACE INTO profiles_heavy_weapons " +
                        "(owner, level, exp, exp_total, damagemultiplier, attackspeedbonus, knockbackbonus, " +
                        "damagebonuslightarmor, damagebonusheavyarmor, flatlightarmorignored, flatheavyarmorignored, " +
                        "flatarmorignored, fractionlightarmorignored, fractionheavyarmorignored, fractionarmorignored, " +
                        "immunityframereduction, parrytimeframe, parryvulnerableframe, parrydamagereduction, " +
                        "parrycooldown, enemydebuffduration, faileddebuffduration, bleedoncrit, bleedchance, bleeddamage, " +
                        "bleedduration, critchance, critonbleed, critdamagemultiplier, critonstealth, critonstun, " +
                        "unlockedweaponcoating, selfpotiondurationmultiplier, enemypotiondurationmultiplier, enemypotionamplifiermultiplier, " +
                        "crushingblowcooldown, crushingblowradius, crushingblowdamagemultiplier, crushingblowoncrit, " +
                        "crushingblowonfalling, crushingblowchance, stunchance, stunduration, dropbonus, " +
                        "raredropmultiplier, expmultiplier) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        stmt.setString(1, owner.toString());
        stmt.setInt(2, level);
        stmt.setDouble(3, exp);
        stmt.setDouble(4, lifetimeEXP);
        stmt.setFloat(5, damagemultiplier);
        stmt.setFloat(6, attackspeedbonus);
        stmt.setFloat(7, knockbackbonus);
        stmt.setFloat(8, damagebonuslightarmor);
        stmt.setFloat(9, damagebonusheavyarmor);
        stmt.setFloat(10, flatlightarmorignored);
        stmt.setFloat(11, flatheavyarmorignored);
        stmt.setFloat(12, flatarmorignored);
        stmt.setFloat(13, fractionlightarmorignored);
        stmt.setFloat(14, fractionheavyarmorignored);
        stmt.setFloat(15, fractionarmorignored);
        stmt.setFloat(16, immunityframereduction);
        stmt.setInt(17, parrytimeframe);
        stmt.setInt(18, parryvulnerableframe);
        stmt.setFloat(19, parrydamagereduction);
        stmt.setInt(20, parrycooldown);
        stmt.setInt(21, enemydebuffduration);
        stmt.setInt(22, faileddebuffduration);
        stmt.setBoolean(23, bleedoncrit);
        stmt.setFloat(24, bleedchance);
        stmt.setFloat(25, bleeddamage);
        stmt.setInt(26, bleedduration);
        stmt.setFloat(27, critchance);
        stmt.setBoolean(28, critonbleed);
        stmt.setFloat(29, critdamagemultiplier);
        stmt.setBoolean(30, critonstealth);
        stmt.setBoolean(31, critonstun);
        stmt.setBoolean(32, unlockedweaponcoating);
        stmt.setFloat(33, selfpotiondurationmultiplier);
        stmt.setFloat(34, enemypotiondurationmultiplier);
        stmt.setFloat(35, enemypotionamplifiermultiplier);
        stmt.setInt(36, crushingblowcooldown);
        stmt.setFloat(37, crushingblowradius);
        stmt.setFloat(38, crushingblowdamagemultiplier);
        stmt.setBoolean(39, crushingblowoncrit);
        stmt.setBoolean(40, crushingblowonfalling);
        stmt.setFloat(41, crushingblowchance);
        stmt.setFloat(42, stunchance);
        stmt.setInt(43, stunduration);
        stmt.setFloat(44, dropbonus);
        stmt.setFloat(45, raredropmultiplier);
        stmt.setDouble(46, expmultiplier);
        stmt.execute();
    }

    @Override
    public Profile fetchProfile(Player p, DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement("SELECT * FROM profiles_heavy_weapons WHERE owner = ?;");
        stmt.setString(1, p.getUniqueId().toString());
        ResultSet result = stmt.executeQuery();
        if (result.next()){
            HeavyWeaponsProfile profile = new HeavyWeaponsProfile(p);
            profile.setLevel(result.getInt("level"));
            profile.setExp(result.getDouble("exp"));
            profile.setLifetimeEXP(result.getDouble("exp_total"));
            profile.setDamageMultiplier(result.getFloat("damagemultiplier"));
            profile.setAttackSpeedBonus(result.getFloat("attackspeedbonus"));
            profile.setKnockbackBonus(result.getFloat("knockbackbonus"));
            profile.setDamageBonusLightArmor(result.getFloat("damagebonuslightarmor"));
            profile.setDamageBonusHeavyArmor(result.getFloat("damagebonusheavyarmor"));
            profile.setFlatLightArmorIgnored(result.getFloat("flatlightarmorignored"));
            profile.setFlatHeavyArmorIgnored(result.getFloat("flatheavyarmorignored"));
            profile.setFlatArmorIgnored(result.getFloat("flatarmorignored"));
            profile.setFractionLightArmorIgnored(result.getFloat("fractionlightarmorignored"));
            profile.setFractionHeavyArmorIgnored(result.getFloat("fractionheavyarmorignored"));
            profile.setFractionArmorIgnored(result.getFloat("fractionarmorignored"));
            profile.setImmunityFrameReduction(result.getFloat("immunityframereduction"));
            profile.setParryTimeFrame(result.getInt("parrytimeframe"));
            profile.setParryVulnerableFrame(result.getInt("parryvulnerableframe"));
            profile.setParryDamageReduction(result.getFloat("parrydamagereduction"));
            profile.setParryCooldown(result.getInt("parrycooldown"));
            profile.setEnemyDebuffDuration(result.getInt("enemydebuffduration"));
            profile.setFailedDebuffDuration(result.getInt("faileddebuffduration"));
            profile.setBleedOnCrit(result.getBoolean("bleedoncrit"));
            profile.setBleedChance(result.getFloat("bleedchance"));
            profile.setBleedDamage(result.getFloat("bleeddamage"));
            profile.setBleedDuration(result.getInt("bleedduration"));
            profile.setCritChance(result.getFloat("critchance"));
            profile.setCritOnBleed(result.getBoolean("critonbleed"));
            profile.setCritDamageMultiplier(result.getFloat("critdamagemultiplier"));
            profile.setCritOnStealth(result.getBoolean("critonstealth"));
            profile.setCritOnStun(result.getBoolean("critonstun"));
            profile.setUnlockedWeaponCoating(result.getBoolean("unlockedweaponcoating"));
            profile.setSelfPotionDurationMultiplier(result.getFloat("selfpotiondurationmultiplier"));
            profile.setEnemyPotionDurationMultiplier(result.getFloat("enemypotiondurationmultiplier"));
            profile.setEnemyPotionAmplifierMultiplier(result.getFloat("enemypotionamplifiermultiplier"));
            profile.setCrushingBlowCooldown(result.getInt("crushingblowcooldown"));
            profile.setCrushingBlowRadius(result.getFloat("crushingblowradius"));
            profile.setCrushingBlowDamageMultiplier(result.getFloat("crushingblowdamagemultiplier"));
            profile.setCrushingBlowOnCrit(result.getBoolean("crushingblowoncrit"));
            profile.setCrushingBlowOnFalling(result.getBoolean("crushingblowonfalling"));
            profile.setCrushingBlowChance(result.getFloat("crushingblowchance"));
            profile.setStunChance(result.getFloat("stunchance"));
            profile.setStunDuration(result.getInt("stunduration"));
            profile.setDropBonus(result.getFloat("dropbonus"));
            profile.setRareDropMultiplier(result.getFloat("raredropmultiplier"));
            profile.setExpMultiplier(result.getDouble("expmultiplier"));
            return profile;
        }
        return null;
    }

    public float getDropBonus() {
        return dropbonus;
    }

    public float getRareDropMultiplier() {
        return raredropmultiplier;
    }

    public void setDropBonus(float dropbonus) {
        this.dropbonus = dropbonus;
    }

    public void setRareDropMultiplier(float raredropmultiplier) {
        this.raredropmultiplier = raredropmultiplier;
    }

    public void setStunChance(float stunchance) {
        this.stunchance = stunchance;
    }

    public float getStunChance() {
        return stunchance;
    }

    public void setStunDuration(int stunduration) {
        this.stunduration = stunduration;
    }

    public int getStunDuration() {
        return stunduration;
    }

    public void setCrushingBlowChance(float crushingblowchance) {
        this.crushingblowchance = crushingblowchance;
    }

    public void setCrushingBlowCooldown(int crushingblowcooldown) {
        this.crushingblowcooldown = crushingblowcooldown;
    }

    public void setCrushingBlowDamageMultiplier(float crushingblowdamagemultiplier) {
        this.crushingblowdamagemultiplier = crushingblowdamagemultiplier;
    }

    public void setCrushingBlowOnCrit(boolean crushingblowoncrit) {
        this.crushingblowoncrit = crushingblowoncrit;
    }

    public void setCrushingBlowOnFalling(boolean crushingblowonfalling) {
        this.crushingblowonfalling = crushingblowonfalling;
    }

    public void setCrushingBlowRadius(float crushingblowradius) {
        this.crushingblowradius = crushingblowradius;
    }

    public float getCrushingBlowChance() {
        return crushingblowchance;
    }

    public float getCrushingBlowDamageMultiplier() {
        return crushingblowdamagemultiplier;
    }

    public float getCrushingBlowRadius() {
        return crushingblowradius;
    }

    public int getCrushingBlowCooldown() {
        return crushingblowcooldown;
    }

    public boolean isCrushingBlowOnCrit() {
        return crushingblowoncrit;
    }

    public boolean isCrushingBlowOnFalling() {
        return crushingblowonfalling;
    }

    public void setUnlockedWeaponCoating(boolean unlockedweaponcoating) {
        this.unlockedweaponcoating = unlockedweaponcoating;
    }

    public void setEnemyPotionDurationMultiplier(float enemypotiondurationmultiplier) {
        this.enemypotiondurationmultiplier = enemypotiondurationmultiplier;
    }

    public void setEnemyPotionAmplifierMultiplier(float enemypotionamplifiermultiplier) {
        this.enemypotionamplifiermultiplier = enemypotionamplifiermultiplier;
    }

    public void setSelfPotionDurationMultiplier(float selfpotiondurationmultiplier) {
        this.selfpotiondurationmultiplier = selfpotiondurationmultiplier;
    }

    public float getSelfPotionDurationMultiplier() {
        return selfpotiondurationmultiplier;
    }

    public float getEnemyPotionAmplifierMultiplier() {
        return enemypotionamplifiermultiplier;
    }

    public float getEnemyPotionDurationMultiplier() {
        return enemypotiondurationmultiplier;
    }

    public boolean isUnlockedWeaponCoating() {
        return unlockedweaponcoating;
    }

    public void setFailedDebuffDuration(int faileddebuffduration) {
        this.faileddebuffduration = faileddebuffduration;
    }

    public int getFailedDebuffDuration() {
        return faileddebuffduration;
    }

    public void setDamageMultiplier(float damagemultiplier) {
        this.damagemultiplier = damagemultiplier;
    }

    public void setAttackSpeedBonus(float attackspeedbonus) {
        this.attackspeedbonus = attackspeedbonus;
    }

    public void setKnockbackBonus(float knockbackbonus) {
        this.knockbackbonus = knockbackbonus;
    }

    public void setDamageBonusLightArmor(float damagebonuslightarmor) {
        this.damagebonuslightarmor = damagebonuslightarmor;
    }

    public void setDamageBonusHeavyArmor(float damagebonusheavyarmor) {
        this.damagebonusheavyarmor = damagebonusheavyarmor;
    }

    public void setFlatLightArmorIgnored(float flatlightarmorignored) {
        this.flatlightarmorignored = flatlightarmorignored;
    }

    public void setFlatHeavyArmorIgnored(float flatheavyarmorignored) {
        this.flatheavyarmorignored = flatheavyarmorignored;
    }

    public void setFlatArmorIgnored(float flatarmorignored) {
        this.flatarmorignored = flatarmorignored;
    }

    public void setFractionLightArmorIgnored(float fractionlightarmorignored) {
        this.fractionlightarmorignored = fractionlightarmorignored;
    }

    public void setFractionHeavyArmorIgnored(float fractionheavyarmorignored) {
        this.fractionheavyarmorignored = fractionheavyarmorignored;
    }

    public void setFractionArmorIgnored(float fractionarmorignored) {
        this.fractionarmorignored = fractionarmorignored;
    }

    public void setImmunityFrameReduction(float immunityframereduction) {
        this.immunityframereduction = immunityframereduction;
    }

    public void setParryTimeFrame(int parrytimeframe) {
        this.parrytimeframe = parrytimeframe;
    }

    public void setParryVulnerableFrame(int parryvulnerableframe) {
        this.parryvulnerableframe = parryvulnerableframe;
    }

    public void setParryDamageReduction(float parrydamagereduction) {
        this.parrydamagereduction = parrydamagereduction;
    }

    public void setParryCooldown(int parrycooldown) {
        this.parrycooldown = parrycooldown;
    }

    public void setEnemyDebuffDuration(int enemydebuffduration) {
        this.enemydebuffduration = enemydebuffduration;
    }

    public void setBleedOnCrit(boolean bleedoncrit) {
        this.bleedoncrit = bleedoncrit;
    }

    public void setBleedChance(float bleedchance) {
        this.bleedchance = bleedchance;
    }

    public void setBleedDamage(float bleeddamage) {
        this.bleeddamage = bleeddamage;
    }

    public void setBleedDuration(int bleedduration) {
        this.bleedduration = bleedduration;
    }

    public void setCritChance(float critchance) {
        this.critchance = critchance;
    }

    public void setCritOnBleed(boolean critonbleed) {
        this.critonbleed = critonbleed;
    }

    public void setCritDamageMultiplier(float critdamagemultiplier) {
        this.critdamagemultiplier = critdamagemultiplier;
    }

    public void setCritOnStealth(boolean critonstealth) {
        this.critonstealth = critonstealth;
    }

    public void setCritOnStun(boolean critonstun) {
        this.critonstun = critonstun;
    }

    public void setExpMultiplier(double expmultiplier) {
        this.expmultiplier = expmultiplier;
    }

    public float getDamageMultiplier() {
        return damagemultiplier;
    }

    public float getAttackSpeedBonus() {
        return attackspeedbonus;
    }

    public float getKnockbackBonus() {
        return knockbackbonus;
    }

    public float getDamageBonusLightArmor() {
        return damagebonuslightarmor;
    }

    public float getDamageBonusHeavyArmor() {
        return damagebonusheavyarmor;
    }

    public float getFlatLightArmorIgnored() {
        return flatlightarmorignored;
    }

    public float getFlatHeavyArmorIgnored() {
        return flatheavyarmorignored;
    }

    public float getFlatArmorIgnored() {
        return flatarmorignored;
    }

    public float getFractionLightArmorIgnored() {
        return fractionlightarmorignored;
    }

    public float getFractionHeavyArmorIgnored() {
        return fractionheavyarmorignored;
    }

    public float getFractionArmorIgnored() {
        return fractionarmorignored;
    }

    public float getImmunityFrameReduction() {
        return immunityframereduction;
    }

    public int getParryTimeFrame() {
        return parrytimeframe;
    }

    public int getParryVulnerableFrame() {
        return parryvulnerableframe;
    }

    public float getParryDamageReduction() {
        return parrydamagereduction;
    }

    public int getParryCooldown() {
        return parrycooldown;
    }

    public int getEnemyDebuffDuration() {
        return enemydebuffduration;
    }

    public boolean isBleedOnCrit() {
        return bleedoncrit;
    }

    public float getBleedChance() {
        return bleedchance;
    }

    public float getBleedDamage() {
        return bleeddamage;
    }

    public int getBleedDuration() {
        return bleedduration;
    }

    public float getCritChance() {
        return critchance;
    }

    public boolean isCritOnBleed() {
        return critonbleed;
    }

    public float getCritDamageMultiplier() {
        return critdamagemultiplier;
    }

    public boolean isCritOnStealth() {
        return critonstealth;
    }

    public boolean isCritOnStun() {
        return critonstun;
    }

    public double getExpMultiplier() {
        return expmultiplier;
    }

    @Override
    public NamespacedKey getKey() {
        return heavyWeaponsProfileKey;
    }

    @Override
    public HeavyWeaponsProfile clone() throws CloneNotSupportedException {
        return (HeavyWeaponsProfile) super.clone();
    }
}
