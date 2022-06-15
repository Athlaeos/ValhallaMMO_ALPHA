package me.athlaeos.valhallammo.skills.alchemy;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.PotionType;
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
import java.util.Collection;
import java.util.HashSet;

public class AlchemyProfile extends Profile implements Serializable {
    private static final NamespacedKey alchemyProfileKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_alchemy");

    private float brewingtimereduction = 1F; // reduction in brewing time to brew anything
    private float brewingingredientsavechance = 0F; // chance for ingredient to not be consumed
    private float potionvelocity = 1F; // velocity multiplier of thrown potions
    private float potionsavechance = 0F; // chance for thrown/drank potion to not be consumed

    private int brewingquality_general = 0;
    private int brewingquality_buffs = 0;
    private int brewingquality_debuffs = 0;

    private double brewingexpmultiplier = 100D;

    private Collection<String> unlockedTransmutations = new HashSet<>();

    public AlchemyProfile(Player owner){
        super(owner);
        if (owner == null) return;
        this.key = alchemyProfileKey;
    }

    public Collection<String> getUnlockedTransmutations() {
        return unlockedTransmutations;
    }

    public void setUnlockedTransmutations(Collection<String> unlockedTransmutations) {
        this.unlockedTransmutations = unlockedTransmutations;
    }

    public int getBrewingQuality(PotionType type){
        switch (type){
            case BUFF: return brewingquality_buffs;
            case DEBUFF: return brewingquality_debuffs;
            default: return 0;
        }
    }

    public void setPotionSaveChance(float potionreusage) {
        this.potionsavechance = potionreusage;
    }

    public void setPotionVelocity(float potionvelocity) {
        this.potionvelocity = potionvelocity;
    }

    public float getPotionSaveChance() {
        return potionsavechance;
    }

    public float getPotionVelocity() {
        return potionvelocity;
    }

    public void setBrewingQuality(PotionType type, int quality) {
        switch (type){
            case BUFF: this.brewingquality_buffs = quality;
            break;
            case DEBUFF: this.brewingquality_debuffs = quality;
            break;
        }
    }

    public float getBrewingTimeMultiplier() {
        return brewingtimereduction;
    }

    public void setBrewingTimeReduction(float brewingtimereduction) {
        this.brewingtimereduction = brewingtimereduction;
    }

    public void setBrewingIngredientSaveChance(float brewingingredientsavechance) {
        this.brewingingredientsavechance = brewingingredientsavechance;
    }

    public float getBrewingIngredientSaveChance() {
        return brewingingredientsavechance;
    }

    public double getBrewingEXPMultiplier() {
        return brewingexpmultiplier;
    }

    public int getGeneralBrewingQuality() {
        return brewingquality_general;
    }

    public void setBrewingEXPMultiplier(double brewingexpmultiplier) {
        this.brewingexpmultiplier = brewingexpmultiplier;
    }

    public void setGeneralBrewingQuality(int quality) {
        this.brewingquality_general = quality;
    }

    @Override
    public void setDefaultStats(Player player) {
        Skill skill = SkillProgressionManager.getInstance().getSkill("ALCHEMY");
        if (skill != null){
            if (skill instanceof AlchemySkill){
                AlchemySkill alchemySkill = (AlchemySkill) skill;
                for (PerkReward startingPerk : alchemySkill.getStartingPerks()){
                    startingPerk.execute(player);
                }
            }
        }
    }

    @Override
    public NamespacedKey getKey() {
        return alchemyProfileKey;
    }

    @Override
    public void createProfileTable(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS profiles_alchemy (" +
                "owner VARCHAR(40) PRIMARY KEY," +
                "level SMALLINT default 0," +
                "exp DOUBLE default 0," +
                "exp_total DOUBLE default 0," +
                "brewing_time_reduction FLOAT DEFAULT 0," +
                "brewing_ingredient_save_chance FLOAT DEFAULT 0, " +
                "potion_throw_speed FLOAT DEFAULT 1," +
                "potion_save_chance FLOAT DEFAULT 0," +
                "quality_general SMALLINT DEFAULT 0," +
                "quality_buffs SMALLINT DEFAULT 0," +
                "quality_debuffs SMALLINT DEFAULT 0," +
                "brewing_exp_multiplier DOUBLE DEFAULT 100);");
        stmt.execute();
    }

    @Override
    public void insertOrUpdateProfile(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement(
                "REPLACE INTO profiles_alchemy " +
                        "(owner, level, exp, exp_total, brewing_time_reduction, brewing_ingredient_save_chance, " +
                        "potion_throw_speed, potion_save_chance, quality_general, quality_buffs, quality_debuffs, " +
                        "brewing_exp_multiplier) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        stmt.setString(1, owner.toString());
        stmt.setInt(2, level);
        stmt.setDouble(3, exp);
        stmt.setDouble(4, lifetimeEXP);
        stmt.setFloat(5, brewingtimereduction);
        stmt.setFloat(6, brewingingredientsavechance);
        stmt.setFloat(7, potionvelocity);
        stmt.setFloat(8, potionsavechance);
        stmt.setInt(9, brewingquality_general);
        stmt.setInt(10, brewingquality_buffs);
        stmt.setInt(11, brewingquality_debuffs);
        stmt.setDouble(12, brewingexpmultiplier);
        stmt.execute();
    }

    @Override
    public Profile fetchProfile(Player p, DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement("SELECT * FROM profiles_alchemy WHERE owner = ?;");
        stmt.setString(1, p.getUniqueId().toString());
        ResultSet result = stmt.executeQuery();
        if (result.next()){
            AlchemyProfile profile = new AlchemyProfile(p);
            profile.setLevel(result.getInt("level"));
            profile.setExp(result.getDouble("exp"));
            profile.setLifetimeEXP(result.getDouble("exp_total"));
            profile.setBrewingTimeReduction(result.getFloat("brewing_time_reduction"));
            profile.setBrewingIngredientSaveChance(result.getFloat("brewing_ingredient_save_chance"));
            profile.setPotionVelocity(result.getFloat("potion_throw_speed"));
            profile.setPotionSaveChance(result.getFloat("potion_save_chance"));
            profile.setGeneralBrewingQuality(result.getInt("quality_general"));
            profile.setBrewingQuality(PotionType.BUFF, result.getInt("quality_buffs"));
            profile.setBrewingQuality(PotionType.DEBUFF, result.getInt("quality_debuffs"));
            profile.setBrewingEXPMultiplier(result.getDouble("brewing_exp_multiplier"));
            return profile;
        }
        return null;
    }

    @Override
    public AlchemyProfile clone() throws CloneNotSupportedException {
        return (AlchemyProfile) super.clone();
    }
}
