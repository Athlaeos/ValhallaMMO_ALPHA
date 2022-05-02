package me.athlaeos.valhallammo.skills.landscaping;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;

public class LandscapingProfile extends Profile implements Serializable {
    private static final NamespacedKey landscapingProfileKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_landscaping");

    private float woodcuttingraredropratemultiplier = 1F; // rare drop rate multiplier for cut logs
    private float woodcuttingdropmultiplier = 1F; // drop multiplier for cut logs
    private float diggingraredropratemultiplier = 1F; // rare drop rate multiplier for diggable blocks
    private float diggingdropmultiplier = 1F; // drop multiplier for diggable blocks
    private float woodstrippingraredropratemultiplier = 1F; // rare drop rate multiplier for stripping wood
    private int treecapitatorcooldown = -1; // cooldown of the tree capitator special ability, if <0 the ability is disabled
    private float instantgrowthrate = 0; // the amount of stages saplings grow immediately upon planting
    private boolean replacesaplings = false; // if true, saplings are automatically planted when breaking trees
    private float blockplacereachbonus = 0; // unused
    private Collection<String> unlockedconversions = new HashSet<>(); // the block conversion interactions the player has unlocked
    private Collection<String> validtreecapitatorblocks = new HashSet<>();
    private float woodcuttingexperiencerate = 0F;
    private float diggingexperiencerate = 0F;

    private double woodcuttingexpmultiplier = 100D;
    private double diggingexpmultiplier = 100D;
    private double woodstrippingexpmultiplier = 100D;
    private double generalexpmultiplier = 100D;

    public LandscapingProfile(Player owner){
        super(owner);
        if (owner == null) return;
        this.key = landscapingProfileKey;
    }

    public float getWoodcuttingRareDropRateMultiplier() {
        return woodcuttingraredropratemultiplier;
    }

    public float getDiggingExperienceRate() {
        return diggingexperiencerate;
    }

    public float getWoodcuttingExperienceRate() {
        return woodcuttingexperiencerate;
    }

    public void setDiggingExperienceRate(float diggingexperiencerate) {
        this.diggingexperiencerate = diggingexperiencerate;
    }

    public void setWoodcuttingExperienceRate(float woodcuttingexperiencerate) {
        this.woodcuttingexperiencerate = woodcuttingexperiencerate;
    }

    public void setWoodcuttingRareDropRateMultiplier(float woodcuttingraredropratemultiplier) {
        this.woodcuttingraredropratemultiplier = woodcuttingraredropratemultiplier;
    }

    public void setValidTreeCapitatorBlocks(Collection<String> validtreecapitatorblocks) {
        this.validtreecapitatorblocks = validtreecapitatorblocks;
    }

    public void setWoodcuttingDropMultiplier(float woodcuttingdropmultiplier) {
        this.woodcuttingdropmultiplier = woodcuttingdropmultiplier;
    }

    public void setReplaceSaplings(boolean replacesaplings) {
        this.replacesaplings = replacesaplings;
    }

    public boolean isReplaceSaplings() {
        return replacesaplings;
    }

    public void setDiggingRareDropRateMultiplier(float diggingraredropratemultiplier) {
        this.diggingraredropratemultiplier = diggingraredropratemultiplier;
    }

    public void setDiggingDropMultiplier(float diggingdropmultiplier) {
        this.diggingdropmultiplier = diggingdropmultiplier;
    }

    public void setWoodstrippingRareDropRateMultiplier(float woodstrippingraredropratemultiplier) {
        this.woodstrippingraredropratemultiplier = woodstrippingraredropratemultiplier;
    }

    public void setTreeCapitatorCooldown(int treecapitatorcooldown) {
        this.treecapitatorcooldown = treecapitatorcooldown;
    }

    public void setInstantGrowthRate(float instantgrowthrate) {
        this.instantgrowthrate = instantgrowthrate;
    }

    public void setBlockPlaceReachBonus(float blockplacereachbonus) {
        this.blockplacereachbonus = blockplacereachbonus;
    }

    public Collection<String> getValidTreeCapitatorBlocks() {
        return validtreecapitatorblocks;
    }

    public void setUnlockedConversions(Collection<String> unlockedconversions) {
        this.unlockedconversions = unlockedconversions;
    }

    public void setWoodcuttingExpMultiplier(double woodcuttingexpmultiplier) {
        this.woodcuttingexpmultiplier = woodcuttingexpmultiplier;
    }

    public void setDiggingExpMultiplier(double diggingexpmultiplier) {
        this.diggingexpmultiplier = diggingexpmultiplier;
    }

    public void setWoodstrippingExpMultiplier(double woodstrippingexpmultiplier) {
        this.woodstrippingexpmultiplier = woodstrippingexpmultiplier;
    }

    public void setGeneralExpMultiplier(double generalexpmultiplier) {
        this.generalexpmultiplier = generalexpmultiplier;
    }

    public float getWoodcuttingDropMultiplier() {
        return woodcuttingdropmultiplier;
    }

    public float getDiggingRareDropRateMultiplier() {
        return diggingraredropratemultiplier;
    }

    public float getDiggingDropMultiplier() {
        return diggingdropmultiplier;
    }

    public float getWoodstrippingRareDropRateMultiplier() {
        return woodstrippingraredropratemultiplier;
    }

    public int getTreeCapitatorCooldown() {
        return treecapitatorcooldown;
    }

    public float getInstantGrowthRate() {
        return instantgrowthrate;
    }

    public float getBlockPlaceReachBonus() {
        return blockplacereachbonus;
    }

    public Collection<String> getUnlockedConversions() {
        return unlockedconversions;
    }

    public double getWoodcuttingExpMultiplier() {
        return woodcuttingexpmultiplier;
    }

    public double getDiggingExpMultiplier() {
        return diggingexpmultiplier;
    }

    public double getWoodstrippingExpMultiplier() {
        return woodstrippingexpmultiplier;
    }

    public double getGeneralExpMultiplier() {
        return generalexpmultiplier;
    }

    @Override
    public void setDefaultStats(Player player) {
        Skill skill = SkillProgressionManager.getInstance().getSkill("LANDSCAPING");
        if (skill != null){
            if (skill instanceof LandscapingSkill){
                LandscapingSkill landscapingSkill = (LandscapingSkill) skill;
                for (PerkReward startingPerk : landscapingSkill.getStartingPerks()){
                    startingPerk.execute(player);
                }
            }
        }
    }

    @Override
    public void createProfileTable(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS profiles_landscaping (" +
                "owner VARCHAR(40) PRIMARY KEY," +
                "level SMALLINT default 0," +
                "exp DOUBLE default 0," +
                "exp_total DOUBLE default 0," +
                "woodcuttingraredropratemultiplier FLOAT DEFAULT 1," +
                "woodcuttingdropmultiplier FLOAT DEFAULT 1, " +
                "diggingraredropratemultiplier FLOAT DEFAULT 1," +
                "diggingdropmultiplier FLOAT DEFAULT 1," +
                "woodstrippingraredropratemultiplier FLOAT DEFAULT 1," +
                "treecapitatorcooldown INT DEFAULT -1," +
                "instantgrowthrate FLOAT DEFAULT 0," +
                "replacesaplings BOOL DEFAULT false," +
                "blockplacereachbonus FLOAT DEFAULT 0," +
                "unlockedconversions VARCHAR(16384) default ''," +
                "validtreecapitatorblocks VARCHAR(16384) DEFAULT ''," +
                "woodcuttingexperiencerate FLOAT DEFAULT 0," +
                "diggingexperiencerate FLOAT DEFAULT 0," +
                "woodcuttingexpmultiplier DOUBLE DEFAULT 100," +
                "diggingexpmultiplier DOUBLE DEFAULT 100," +
                "woodstrippingexpmultiplier DOUBLE DEFAULT 100," +
                "generalexpmultiplier DOUBLE DEFAULT 100);");
        stmt.execute();
    }

    @Override
    public void insertOrUpdateProfile(Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(
                "REPLACE INTO profiles_landscaping " +
                        "(owner, level, exp, exp_total, woodcuttingraredropratemultiplier, woodcuttingdropmultiplier, " +
                        "diggingraredropratemultiplier, diggingdropmultiplier, woodstrippingraredropratemultiplier, " +
                        "treecapitatorcooldown, instantgrowthrate, replacesaplings, blockplacereachbonus, unlockedconversions, " +
                        "validtreecapitatorblocks, woodcuttingexperiencerate, diggingexperiencerate, woodcuttingexpmultiplier, " +
                        "diggingexpmultiplier, woodstrippingexpmultiplier, generalexpmultiplier) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
        stmt.setString(1, owner.toString());
        stmt.setInt(2, level);
        stmt.setDouble(3, exp);
        stmt.setDouble(4, lifetimeEXP);
        stmt.setFloat(5, woodcuttingraredropratemultiplier);
        stmt.setFloat(6, woodcuttingdropmultiplier);
        stmt.setFloat(7, diggingraredropratemultiplier);
        stmt.setFloat(8, diggingdropmultiplier);
        stmt.setFloat(9, woodstrippingraredropratemultiplier);
        stmt.setInt(10, treecapitatorcooldown);
        stmt.setFloat(11, instantgrowthrate);
        stmt.setBoolean(12, replacesaplings);
        stmt.setFloat(13, blockplacereachbonus);
        stmt.setString(14, String.join("<>", unlockedconversions));
        stmt.setString(15, String.join("<>", validtreecapitatorblocks));
        stmt.setFloat(16, woodcuttingexperiencerate);
        stmt.setFloat(17, diggingexperiencerate);
        stmt.setDouble(18, woodcuttingexpmultiplier);
        stmt.setDouble(19, diggingexpmultiplier);
        stmt.setDouble(20, woodstrippingexpmultiplier);
        stmt.setDouble(21, generalexpmultiplier);
        stmt.execute();
    }

    @Override
    public Profile fetchProfile(Player p, Connection conn) throws SQLException {
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM profiles_landscaping WHERE owner = ?;");
        stmt.setString(1, p.getUniqueId().toString());
        ResultSet result = stmt.executeQuery();
        if (result.next()){
            LandscapingProfile profile = new LandscapingProfile(p);
            profile.setLevel(result.getInt("level"));
            profile.setExp(result.getDouble("exp"));
            profile.setLifetimeEXP(result.getDouble("exp_total"));
            profile.setWoodcuttingRareDropRateMultiplier(result.getFloat("woodcuttingraredropratemultiplier"));
            profile.setWoodcuttingDropMultiplier(result.getFloat("woodcuttingdropmultiplier"));
            profile.setDiggingRareDropRateMultiplier(result.getFloat("diggingraredropratemultiplier"));
            profile.setDiggingDropMultiplier(result.getFloat("diggingdropmultiplier"));
            profile.setWoodstrippingRareDropRateMultiplier(result.getFloat("woodstrippingraredropratemultiplier"));
            profile.setTreeCapitatorCooldown(result.getInt("treecapitatorcooldown"));
            profile.setInstantGrowthRate(result.getFloat("instantgrowthrate"));
            profile.setReplaceSaplings(result.getBoolean("replacesaplings"));
            profile.setBlockPlaceReachBonus(result.getFloat("blockplacereachbonus"));
            profile.setUnlockedConversions(new HashSet<>(Arrays.asList(result.getString("unlockedconversions").split("<>"))));
            profile.setValidTreeCapitatorBlocks(new HashSet<>(Arrays.asList(result.getString("validtreecapitatorblocks").split("<>"))));
            profile.setWoodcuttingExperienceRate(result.getFloat("woodcuttingexperiencerate"));
            profile.setDiggingExperienceRate(result.getFloat("diggingexperiencerate"));
            profile.setWoodcuttingExpMultiplier(result.getDouble("woodcuttingexpmultiplier"));
            profile.setDiggingExpMultiplier(result.getDouble("diggingexpmultiplier"));
            profile.setWoodstrippingExpMultiplier(result.getDouble("woodstrippingexpmultiplier"));
            profile.setGeneralExpMultiplier(result.getDouble("generalexpmultiplier"));
            return profile;
        }
        return null;
    }

    @Override
    public NamespacedKey getKey() {
        return landscapingProfileKey;
    }

    @Override
    public LandscapingProfile clone() throws CloneNotSupportedException {
        return (LandscapingProfile) super.clone();
    }
}
