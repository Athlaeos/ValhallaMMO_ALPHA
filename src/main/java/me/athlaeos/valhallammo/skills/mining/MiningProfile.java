package me.athlaeos.valhallammo.skills.mining;

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

public class MiningProfile extends Profile implements Serializable {
    private static final NamespacedKey miningProfileKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_mining");

    private float miningraredropratemultiplier = 1F; // rare drop rate multiplier of mining
    private float blastminingraredropratemultiplier = 1F; // rare drop rate multiplier of blast mining
    private float miningdropmultiplier = 1F; // drop multiplier for mining
    private float blastminingdropmultiplier = 1F; // drop multiplier for blast mining
    private float blastradiusmultiplier = 1F; // blast radius multiplier
    private float tntexplosiondamagemultiplier = 1F; // damage multiplier of damage taken from tnt
    private int veinminingcooldown = -1; // cooldown of the vein mining ability, which harvests a large area of the same ore at once. if -1, ability is unusable
    private Collection<String> validveinminerblocks = new HashSet<>();
    private Collection<String> unbreakableblocks = new HashSet<>();
    private int quickminecooldown = 0; // cooldown of the quick mine ability, which allows blocks to be instantly mined at the cost of hunger and health.
    private int quickminehungerdrainspeed = -1; // amount of blocks the player can instantly mine before losing 1 saturation/hunger/health point. if <0 this ability is disabled
    // some blocks can be valued higher, which are defined in skill_mining.yml
    // this is to make it so blocks like obsidian can't be mined as frequently as any other block. undefined block values are valued as 1
    private float quickminedurabilitylossrate = 1F; // durability damage multiplier of tools while quickmine mode is enabled. 1 is equal to a normal rate, anything below 1 means a decreased rate, anything above 1 is an increased rate
    private float oreexperiencemultiplier = 1F;
    private float blockmineexperiencerate = 0F;
    private int explosionfortunelevel = 0; // the fortune level at which exploded blocks will be "mined". if 0 blocks will be broken as if they were mined with a plain iron pickaxe, if -1 they will be broken as if broken with a silk touch pickaxe
    private int tunnelfatiguelevel = -1; // mining fatigue level applied on the player when tunnel mode is activated, if <0 tunnel mode is locked, if 0 no fatigue is applied

    private double generalexpmultiplier = 100D;
    private double blastminingexpmultiplier = 100D;
    private double miningexpmultiplier = 100D;

    public MiningProfile(Player owner){
        super(owner);
        if (owner == null) return;
        this.key = miningProfileKey;
    }

    public float getMiningRareDropRateMultiplier() {
        return miningraredropratemultiplier;
    }

    public float getBlastMiningRareDropRateMultiplier() {
        return blastminingraredropratemultiplier;
    }

    public float getMiningDropMultiplier() {
        return miningdropmultiplier;
    }

    public float getBlastMiningDropMultiplier() {
        return blastminingdropmultiplier;
    }

    public float getBlastRadiusMultiplier() {
        return blastradiusmultiplier;
    }

    public float getTntExplosionDamageMultiplier() {
        return tntexplosiondamagemultiplier;
    }

    public int getVeinMiningCooldown() {
        return veinminingcooldown;
    }

    public float getQuickMineDurabilityLossRate() {
        return quickminedurabilitylossrate;
    }

    /**
     * currently has no purpose
     * @return how fatigued the player should be while in tunneling mode
     */
    public int getTunnelFatigueLevel() {
        return tunnelfatiguelevel;
    }

    /**
     * currently has no purpose
     * @param tunnelfatiguelevel how fatigued the player should be while in tunneling mode
     */
    public void setTunnelFatigueLevel(int tunnelfatiguelevel) {
        this.tunnelfatiguelevel = tunnelfatiguelevel;
    }

    public int getExplosionFortuneLevel() {
        return explosionfortunelevel;
    }

    public Collection<String> getValidVeinMinerBlocks() {
        return validveinminerblocks;
    }

    public int getQuickMineHungerDrainSpeed() {
        return quickminehungerdrainspeed;
    }

    public double getGeneralExpMultiplier() {
        return generalexpmultiplier;
    }

    public double getBlastMiningExpMultiplier() {
        return blastminingexpmultiplier;
    }

    public double getMiningExpMultiplier() {
        return miningexpmultiplier;
    }

    public float getBlockMineExperienceRate() {
        return blockmineexperiencerate;
    }

    public float getOreExperienceMultiplier() {
        return oreexperiencemultiplier;
    }

    public void setMiningRareDropRateMultiplier(float miningraredropratemultiplier) {
        this.miningraredropratemultiplier = miningraredropratemultiplier;
    }

    public void setBlastMiningRareDropRateMultiplier(float blastminingraredropratemultiplier) {
        this.blastminingraredropratemultiplier = blastminingraredropratemultiplier;
    }

    public void setMiningDropMultiplier(float miningdropmultiplier) {
        this.miningdropmultiplier = miningdropmultiplier;
    }

    public void setBlastMiningDropMultiplier(float blastminingdropmultiplier) {
        this.blastminingdropmultiplier = blastminingdropmultiplier;
    }

    public void setBlastRadiusMultiplier(float blastradiusmultiplier) {
        this.blastradiusmultiplier = blastradiusmultiplier;
    }

    public void setTntExplosionDamageMultiplier(float tntexplosiondamagemultiplier) {
        this.tntexplosiondamagemultiplier = tntexplosiondamagemultiplier;
    }

    public void setVeinMiningCooldown(int veinminingcooldown) {
        this.veinminingcooldown = veinminingcooldown;
    }

    public void setValidVeinMinerBlocks(Collection<String> validveinminerblocks) {
        this.validveinminerblocks = validveinminerblocks;
    }

    public void setQuickMineHungerDrainSpeed(int quickMineHungerDrainSpeed) {
        this.quickminehungerdrainspeed = quickMineHungerDrainSpeed;
    }

    public void setQuickMineDurabilityLossRate(float quickminedurabilitylossrate) {
        this.quickminedurabilitylossrate = quickminedurabilitylossrate;
    }

    public void setGeneralExpMultiplier(double generalexpmultiplier) {
        this.generalexpmultiplier = generalexpmultiplier;
    }

    public void setBlastMiningExpMultiplier(double blastminingexpmultiplier) {
        this.blastminingexpmultiplier = blastminingexpmultiplier;
    }

    public void setMiningExpMultiplier(double miningexpmultiplier) {
        this.miningexpmultiplier = miningexpmultiplier;
    }

    public void setBlockMineExperienceRate(float blockmineexperiencerate) {
        this.blockmineexperiencerate = blockmineexperiencerate;
    }

    public void setOreExperienceMultiplier(float oreexperiencemultiplier) {
        this.oreexperiencemultiplier = oreexperiencemultiplier;
    }

    public Collection<String> getUnbreakableBlocks() {
        return unbreakableblocks;
    }

    public void setUnbreakableBlocks(
            Collection<String> unbreakableblocks) {
        this.unbreakableblocks = unbreakableblocks;
    }

    public int getQuickMineCooldown() {
        return quickminecooldown;
    }

    public void setQuickMineCooldown(int quickminecooldown) {
        this.quickminecooldown = quickminecooldown;
    }

    public void setExplosionFortuneLevel(int explosionfortunelevel) {
        this.explosionfortunelevel = explosionfortunelevel;
    }

    @Override
    public void setDefaultStats(Player player) {
        Skill skill = SkillProgressionManager.getInstance().getSkill("MINING");
        if (skill != null){
            if (skill instanceof MiningSkill){
                MiningSkill farmingSkill = (MiningSkill) skill;
                for (PerkReward startingPerk : farmingSkill.getStartingPerks()){
                    startingPerk.execute(player);
                }
            }
        }
    }

    @Override
    public NamespacedKey getKey() {
        return miningProfileKey;
    }

    @Override
    public MiningProfile clone() throws CloneNotSupportedException {
        return (MiningProfile) super.clone();
    }
}
