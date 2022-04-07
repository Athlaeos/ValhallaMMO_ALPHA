package me.athlaeos.valhallammo.skills.archery;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.io.Serializable;

public class ArcheryProfile extends Profile implements Serializable {
    private static final NamespacedKey archeryProfileKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_archery");

    private float bowdamagemultiplier = 1f; // done
    private float crossbowdamagemultiplier = 1f; // done
    private float bowcritchance = 0f; // done
    private float crossbowcritchance = 0f; // done
    private float ammosavechance = 0f; // done
    private boolean critonfacingaway = false; // done
    private boolean critonstandingstill = false; // done
    private boolean critonstealth = false; // done
    private float critdamagemultiplier = 1.5f; // done
    private float inaccuracy = 0f; // done
    private float damagedistancebasemultiplier = 1f; // done
    private float damagedistancemultiplier = 0f; // done
    private float stunchance = 0f; // done
    private int stunduration = 0; // done
    private boolean stunoncrit = false; // done
    private float infinitydamagemultiplier = 1f; // done
    private int chargedshotcooldown = -1;
    private int chargedshotknockbackbonus = 0;
    private float chargedshotdamagemultiplier = 1f;

    private double bowexpmultiplier = 100D;
    private double crossbowexpmultiplier = 100D;
    private double generalexpmultiplier = 100D;

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

    public void setBowexpmultiplier(double bowexpmultiplier) {
        this.bowexpmultiplier = bowexpmultiplier;
    }

    public void setCrossbowexpmultiplier(double crossbowexpmultiplier) {
        this.crossbowexpmultiplier = crossbowexpmultiplier;
    }

    public void setGeneralexpmultiplier(double generalexpmultiplier) {
        this.generalexpmultiplier = generalexpmultiplier;
    }

    public ArcheryProfile(Player owner){
        super(owner);
        if (owner == null) return;
        this.key = archeryProfileKey;
    }

    @Override
    public void setDefaultStats(Player player) {
        Skill skill = SkillProgressionManager.getInstance().getSkill("ARCHERY");
        if (skill != null){
            if (skill instanceof ArcherySkill){
                ArcherySkill archerySkill = (ArcherySkill) skill;
                for (PerkReward startingPerk : archerySkill.getStartingPerks()){
                    startingPerk.execute(player);
                }
            }
        }
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
