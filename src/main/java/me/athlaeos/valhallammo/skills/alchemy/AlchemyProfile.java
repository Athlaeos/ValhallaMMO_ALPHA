package me.athlaeos.valhallammo.skills.alchemy;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.io.Serializable;
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
    public AlchemyProfile clone() throws CloneNotSupportedException {
        return (AlchemyProfile) super.clone();
    }
}
