package me.athlaeos.valhallammo.skills.smithing;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.MaterialClass;
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

public class SmithingProfile extends Profile implements Serializable {
    private static final NamespacedKey smithingProfileKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_smithing");

    private int craftingquality_all = 0;
    private int craftingquality_bow = 0;
    private int craftingquality_crossbow = 0;
    private int craftingquality_wood = 0;
    private int craftingquality_leather = 0;
    private int craftingquality_stone = 0;
    private int craftingquality_chain = 0;
    private int craftingquality_gold = 0;
    private int craftingquality_iron = 0;
    private int craftingquality_diamond = 0;
    private int craftingquality_netherite = 0;
    private int craftingquality_prismarine = 0;
    private int craftingquality_membrane = 0;
    private double craftingexpmultiplier_all = 100D;
    private double craftingexpmultiplier_bow = 100D;
    private double craftingexpmultiplier_crossbow = 100D;
    private double craftingexpmultiplier_wood = 100D;
    private double craftingexpmultiplier_leather = 100D;
    private double craftingexpmultiplier_stone = 100D;
    private double craftingexpmultiplier_chain = 100D;
    private double craftingexpmultiplier_gold = 100D;
    private double craftingexpmultiplier_iron = 100D;
    private double craftingexpmultiplier_diamond = 100D;
    private double craftingexpmultiplier_netherite = 100D;
    private double craftingexpmultiplier_prismarine = 100D;
    private double craftingexpmultiplier_membrane = 100D;
    private float craftingtimereduction = 0F;

    public SmithingProfile(Player owner){
        super(owner);
        if (owner == null) return;
        this.key = smithingProfileKey;
    }

    @Override
    public void setDefaultStats(Player player) {
        Skill skill = SkillProgressionManager.getInstance().getSkill("SMITHING");
        if (skill != null){
            if (skill instanceof SmithingSkill){
                SmithingSkill smithingSkill = (SmithingSkill) skill;
                for (PerkReward startingPerk : smithingSkill.getStartingPerks()){
                    startingPerk.execute(player);
                }
            }
        }
    }

    public float getCraftingTimeReduction() {
        return craftingtimereduction;
    }

    public void setCraftingTimeReduction(float craftingtimereduction) {
        this.craftingtimereduction = craftingtimereduction;
    }

    public int getCraftingQuality(MaterialClass type){
        if (type == null) return 0;
        switch (type){
            case CROSSBOW: return craftingquality_crossbow;
            case BOW: return craftingquality_bow;
            case WOOD: return craftingquality_wood;
            case LEATHER: return craftingquality_leather;
            case STONE: return craftingquality_stone;
            case CHAINMAIL: return craftingquality_chain;
            case GOLD: return craftingquality_gold;
            case IRON: return craftingquality_iron;
            case DIAMOND: return craftingquality_diamond;
            case NETHERITE: return craftingquality_netherite;
            case PRISMARINE: return craftingquality_prismarine;
            case MEMBRANE: return craftingquality_membrane;
            default: return 0;
        }
    }

    public void setMaterialCraftingQuality(MaterialClass type, int value){
        if (type == null) return;
        switch (type){
            case CROSSBOW: this.craftingquality_crossbow = value;
                break;
            case BOW: this.craftingquality_bow = value;
                break;
            case WOOD: this.craftingquality_wood = value;
                break;
            case LEATHER: this.craftingquality_leather = value;
                break;
            case STONE: this.craftingquality_stone = value;
                break;
            case CHAINMAIL: this.craftingquality_chain = value;
                break;
            case GOLD: this.craftingquality_gold = value;
                break;
            case IRON: this.craftingquality_iron = value;
                break;
            case DIAMOND: this.craftingquality_diamond = value;
                break;
            case NETHERITE: this.craftingquality_netherite = value;
                break;
            case PRISMARINE: this.craftingquality_prismarine = value;
                break;
            case MEMBRANE: this.craftingquality_membrane = value;
                break;
        }
    }

    public void setGeneralCraftingExpMultiplier(double craftingexpmultiplier_all) {
        this.craftingexpmultiplier_all = craftingexpmultiplier_all;
    }

    public void setGeneralCraftingQuality(int craftingquality_all) {
        this.craftingquality_all = craftingquality_all;
    }

    public double getCraftingEXPMultiplier(MaterialClass type){
        if (type == null) return 0;
        switch (type){
            case CROSSBOW: return craftingexpmultiplier_crossbow;
            case BOW: return craftingexpmultiplier_bow;
            case WOOD: return craftingexpmultiplier_wood;
            case LEATHER: return craftingexpmultiplier_leather;
            case STONE: return craftingexpmultiplier_stone;
            case CHAINMAIL: return craftingexpmultiplier_chain;
            case GOLD: return craftingexpmultiplier_gold;
            case IRON: return craftingexpmultiplier_iron;
            case DIAMOND: return craftingexpmultiplier_diamond;
            case NETHERITE: return craftingexpmultiplier_netherite;
            case PRISMARINE: return craftingexpmultiplier_prismarine;
            case MEMBRANE: return craftingexpmultiplier_membrane;
            default: return 0;
        }
    }

    public void setCraftingEXPMultiplier(MaterialClass type, double value){
        if (type == null) return;
        switch (type){
            case CROSSBOW: this.craftingexpmultiplier_crossbow = value;
                break;
            case BOW: this.craftingexpmultiplier_bow = value;
                break;
            case WOOD: this.craftingexpmultiplier_wood = value;
                break;
            case LEATHER: this.craftingexpmultiplier_leather = value;
                break;
            case STONE: this.craftingexpmultiplier_stone = value;
                break;
            case CHAINMAIL: this.craftingexpmultiplier_chain = value;
                break;
            case GOLD: this.craftingexpmultiplier_gold = value;
                break;
            case IRON: this.craftingexpmultiplier_iron = value;
                break;
            case DIAMOND: this.craftingexpmultiplier_diamond = value;
                break;
            case NETHERITE: this.craftingexpmultiplier_netherite = value;
                break;
            case PRISMARINE: this.craftingexpmultiplier_prismarine = value;
                break;
            case MEMBRANE: this.craftingexpmultiplier_membrane = value;
                break;
        }
    }

    public double getGeneralCraftingExpMultiplier() {
        return craftingexpmultiplier_all;
    }

    public int getGeneralCraftingQuality() {
        return craftingquality_all;
    }

    @Override
    public void createProfileTable(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement("CREATE TABLE IF NOT EXISTS profiles_smithing (" +
                "owner VARCHAR(40) PRIMARY KEY," +
                "level SMALLINT default 0," +
                "exp DOUBLE default 0," +
                "exp_total DOUBLE default 0," +
                "craftingquality_all SMALLINT DEFAULT 0," +
                "craftingquality_bow SMALLINT DEFAULT 0," +
                "craftingquality_crossbow SMALLINT DEFAULT 0," +
                "craftingquality_wood SMALLINT DEFAULT 0," +
                "craftingquality_leather SMALLINT DEFAULT 0," +
                "craftingquality_stone SMALLINT DEFAULT 0," +
                "craftingquality_chain SMALLINT DEFAULT 0," +
                "craftingquality_gold SMALLINT DEFAULT 0," +
                "craftingquality_iron SMALLINT DEFAULT 0," +
                "craftingquality_diamond SMALLINT DEFAULT 0," +
                "craftingquality_netherite SMALLINT DEFAULT 0," +
                "craftingquality_prismarine SMALLINT DEFAULT 0," +
                "craftingquality_membrane SMALLINT DEFAULT 0," +
                "craftingexpmultiplier_all DOUBLE DEFAULT 100," +
                "craftingexpmultiplier_bow DOUBLE DEFAULT 100," +
                "craftingexpmultiplier_crossbow DOUBLE DEFAULT 100," +
                "craftingexpmultiplier_wood DOUBLE DEFAULT 100," +
                "craftingexpmultiplier_leather DOUBLE DEFAULT 100," +
                "craftingexpmultiplier_stone DOUBLE DEFAULT 100," +
                "craftingexpmultiplier_chain DOUBLE DEFAULT 100," +
                "craftingexpmultiplier_gold DOUBLE DEFAULT 100," +
                "craftingexpmultiplier_iron DOUBLE DEFAULT 100," +
                "craftingexpmultiplier_diamond DOUBLE DEFAULT 100," +
                "craftingexpmultiplier_netherite DOUBLE DEFAULT 100," +
                "craftingexpmultiplier_prismarine DOUBLE DEFAULT 100," +
                "craftingexpmultiplier_membrane DOUBLE DEFAULT 100);");
        stmt.execute();

        conn.addColumnIfNotExists("profiles_smithing", "craftingtimereduction", "FLOAT DEFAULT 0");
    }

    @Override
    public void insertOrUpdateProfile(DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement(
                "REPLACE INTO profiles_smithing " +
                        "(owner, level, exp, exp_total, craftingquality_all, craftingquality_bow, craftingquality_crossbow, " +
                        "craftingquality_wood, craftingquality_leather, craftingquality_stone, craftingquality_chain, " +
                        "craftingquality_gold, craftingquality_iron, craftingquality_diamond, craftingquality_netherite, " +
                        "craftingquality_prismarine, craftingquality_membrane, craftingexpmultiplier_all, craftingexpmultiplier_bow, " +
                        "craftingexpmultiplier_crossbow, craftingexpmultiplier_wood, craftingexpmultiplier_leather, craftingexpmultiplier_stone, " +
                        "craftingexpmultiplier_chain, craftingexpmultiplier_gold, craftingexpmultiplier_iron, craftingexpmultiplier_diamond, " +
                        "craftingexpmultiplier_netherite, craftingexpmultiplier_prismarine, craftingexpmultiplier_membrane," +
                        "craftingtimereduction)" +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        stmt.setString(1, owner.toString());
        stmt.setInt(2, level);
        stmt.setDouble(3, exp);
        stmt.setDouble(4, lifetimeEXP);
        stmt.setInt(5, craftingquality_all);
        stmt.setInt(6, craftingquality_bow);
        stmt.setInt(7, craftingquality_crossbow);
        stmt.setInt(8, craftingquality_wood);
        stmt.setInt(9, craftingquality_leather);
        stmt.setInt(10, craftingquality_stone);
        stmt.setInt(11, craftingquality_chain);
        stmt.setInt(12, craftingquality_gold);
        stmt.setInt(13, craftingquality_iron);
        stmt.setInt(14, craftingquality_diamond);
        stmt.setInt(15, craftingquality_netherite);
        stmt.setInt(16, craftingquality_prismarine);
        stmt.setInt(17, craftingquality_membrane);
        stmt.setDouble(18, craftingexpmultiplier_all);
        stmt.setDouble(19, craftingexpmultiplier_bow);
        stmt.setDouble(20, craftingexpmultiplier_crossbow);
        stmt.setDouble(21, craftingexpmultiplier_wood);
        stmt.setDouble(22, craftingexpmultiplier_leather);
        stmt.setDouble(23, craftingexpmultiplier_stone);
        stmt.setDouble(24, craftingexpmultiplier_chain);
        stmt.setDouble(25, craftingexpmultiplier_gold);
        stmt.setDouble(26, craftingexpmultiplier_iron);
        stmt.setDouble(27, craftingexpmultiplier_diamond);
        stmt.setDouble(28, craftingexpmultiplier_netherite);
        stmt.setDouble(29, craftingexpmultiplier_prismarine);
        stmt.setDouble(30, craftingexpmultiplier_membrane);
        stmt.setFloat(31, craftingtimereduction);
        stmt.execute();
    }

    @Override
    public Profile fetchProfile(Player p, DatabaseConnection conn) throws SQLException {
        PreparedStatement stmt = conn.getConnection().prepareStatement("SELECT * FROM profiles_smithing WHERE owner = ?;");
        stmt.setString(1, p.getUniqueId().toString());
        ResultSet result = stmt.executeQuery();
        if (result.next()){
            SmithingProfile profile = new SmithingProfile(p);
            profile.setLevel(result.getInt("level"));
            profile.setExp(result.getDouble("exp"));
            profile.setLifetimeEXP(result.getDouble("exp_total"));
            profile.setGeneralCraftingQuality(result.getInt("craftingquality_all"));
            profile.setMaterialCraftingQuality(MaterialClass.BOW, result.getInt("craftingquality_bow"));
            profile.setMaterialCraftingQuality(MaterialClass.CROSSBOW, result.getInt("craftingquality_crossbow"));
            profile.setMaterialCraftingQuality(MaterialClass.WOOD, result.getInt("craftingquality_wood"));
            profile.setMaterialCraftingQuality(MaterialClass.LEATHER, result.getInt("craftingquality_leather"));
            profile.setMaterialCraftingQuality(MaterialClass.STONE, result.getInt("craftingquality_stone"));
            profile.setMaterialCraftingQuality(MaterialClass.CHAINMAIL, result.getInt("craftingquality_chain"));
            profile.setMaterialCraftingQuality(MaterialClass.GOLD, result.getInt("craftingquality_gold"));
            profile.setMaterialCraftingQuality(MaterialClass.IRON, result.getInt("craftingquality_iron"));
            profile.setMaterialCraftingQuality(MaterialClass.DIAMOND, result.getInt("craftingquality_diamond"));
            profile.setMaterialCraftingQuality(MaterialClass.NETHERITE, result.getInt("craftingquality_netherite"));
            profile.setMaterialCraftingQuality(MaterialClass.PRISMARINE, result.getInt("craftingquality_prismarine"));
            profile.setMaterialCraftingQuality(MaterialClass.MEMBRANE, result.getInt("craftingquality_membrane"));
            profile.setGeneralCraftingExpMultiplier(result.getDouble("craftingexpmultiplier_all"));
            profile.setCraftingEXPMultiplier(MaterialClass.BOW, result.getDouble("craftingexpmultiplier_bow"));
            profile.setCraftingEXPMultiplier(MaterialClass.CROSSBOW, result.getDouble("craftingexpmultiplier_crossbow"));
            profile.setCraftingEXPMultiplier(MaterialClass.WOOD, result.getDouble("craftingexpmultiplier_wood"));
            profile.setCraftingEXPMultiplier(MaterialClass.LEATHER, result.getDouble("craftingexpmultiplier_leather"));
            profile.setCraftingEXPMultiplier(MaterialClass.STONE,result.getDouble("craftingexpmultiplier_stone"));
            profile.setCraftingEXPMultiplier(MaterialClass.CHAINMAIL, result.getDouble("craftingexpmultiplier_chain"));
            profile.setCraftingEXPMultiplier(MaterialClass.GOLD, result.getDouble("craftingexpmultiplier_gold"));
            profile.setCraftingEXPMultiplier(MaterialClass.IRON, result.getDouble("craftingexpmultiplier_iron"));
            profile.setCraftingEXPMultiplier(MaterialClass.DIAMOND, result.getDouble("craftingexpmultiplier_diamond"));
            profile.setCraftingEXPMultiplier(MaterialClass.NETHERITE, result.getDouble("craftingexpmultiplier_netherite"));
            profile.setCraftingEXPMultiplier(MaterialClass.PRISMARINE, result.getDouble("craftingexpmultiplier_prismarine"));
            profile.setCraftingEXPMultiplier(MaterialClass.MEMBRANE, result.getDouble("craftingexpmultiplier_membrane"));
            profile.setCraftingTimeReduction(result.getFloat("craftingtimereduction"));
            return profile;
        }
        return null;
    }

    @Override
    public NamespacedKey getKey() {
        return smithingProfileKey;
    }
}
