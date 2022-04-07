package me.athlaeos.valhallammo.skills.landscaping;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.io.Serializable;
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
    public NamespacedKey getKey() {
        return landscapingProfileKey;
    }

    @Override
    public LandscapingProfile clone() throws CloneNotSupportedException {
        return (LandscapingProfile) super.clone();
    }
}
