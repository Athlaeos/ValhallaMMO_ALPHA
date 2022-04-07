package me.athlaeos.valhallammo.skills.farming;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.io.Serializable;

public class FarmingProfile extends Profile implements Serializable {
    private static final NamespacedKey farmingProfileKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_farming");

    private float raredropratemultiplier = 1F; // rare drop rate multiplier for crops
    private float dropmultiplier = 1F; // drop multiplier for crops
    private float animaldropmultiplier = 1F; // drop multiplier for animals
    private float animalraredropratemultiplier = 1F; // rare drop rate multiplier for killed animals
    private boolean instantharvesting = false; // if true, players can right click crops to harvest them and immediately replant them
    private float instantgrowthrate = 0F; // the amount of stages crops grow immediately upon planting
    private float fishingtimemultiplier = 1F; // the duration multiplier of fishing hooks until they catch a fish
    private float fishingrewardtier = 0F; // the reward tier of rewards the player fishes up
    private float farmingvanillaexpreward = 0F; // (vanilla) exp rewarded for harvesting fully grown crops
    private float breedingvanillaexpmultiplier = 1F; // (vanilla) experience multiplier for experience gained by breeding animals
    private float fishingvanillaexpmultiplier = 1F; // (vanilla) experience multiplier for experience gained by fishing
    private float babyanimalagemultiplier = 1F; // multiplier of baby animal age when born, causing them to mature faster or slower
    private float hivehoneysavechance = 0F; // chance for hives to not consume honey when harvested (either with shears or bottles)
    private boolean hivebeeaggroimmunity = false;
    private int ultraharvestingcooldown = -1; // cooldown of the ultra harvesting ability, which harvests a large area of crops at once (similar to veinminer except for crops). if -1, ability is unusable
    private float animaldamagemultiplier = 1F; // damage multiplier against livestock animals (cows, chickens, pigs, sheep, etc. NOT neutral animals like wolves or hoglins)

    private boolean isbadfoodimmune = false; // determines if the player is immune to the negative effects of foods like rotten flesh or pufferfish
    private float carnivoroushungermultiplier = 1F; // food/saturation multipliers of eating meat
    private float pescotarianhungermultiplier = 1F; // food/saturation multipliers of eating fish
    private float vegetarianhungermultiplier = 1F; // food/saturation multipliers of eating non-meat foods (plant-based, but including things like honey/cake)
    private float garbagehungermultiplier = 1F; // food/saturation multipliers of eating garbage (things that are usually not meant to be eaten like spider eyes and rotten flesh)
    private float magicalhungermultiplier = 1F; // food/saturation multipliers of eating magical foods like golden apples or golden carrots

    private double generalexpmultiplier = 100D;
    private double farmingexpmultiplier = 100D;
    private double breedingexpmultiplier = 100D;
    private double fishingexpmultiplier = 100D;

    public FarmingProfile(Player owner){
        super(owner);
        if (owner == null) return;
        this.key = farmingProfileKey;
    }

    public boolean isHiveBeeAggroImmune() {
        return hivebeeaggroimmunity;
    }

    public void setHiveBeeAggroImmunity(boolean hivebeeaggroimmunity) {
        this.hivebeeaggroimmunity = hivebeeaggroimmunity;
    }

    public double getGeneralExpMultiplier() {
        return generalexpmultiplier;
    }

    public void setGeneralExpMultiplier(double generalexpmultiplier) {
        this.generalexpmultiplier = generalexpmultiplier;
    }

    public int getUltraHarvestingCooldown() {
        return ultraharvestingcooldown;
    }

    public float getAnimalDamageMultiplier() {
        return animaldamagemultiplier;
    }

    public void setAnimalDamageMultiplier(float animaldamagemultiplier) {
        this.animaldamagemultiplier = animaldamagemultiplier;
    }

    public float getAnimalDropMultiplier() {
        return animaldropmultiplier;
    }

    public void setAnimalDropMultiplier(float animaldropmultiplier) {
        this.animaldropmultiplier = animaldropmultiplier;
    }

    public void setUltraHarvestingCooldown(int ultraharvestingcooldown) {
        this.ultraharvestingcooldown = ultraharvestingcooldown;
    }

    public float getFarmingVanillaExpReward() {
        return farmingvanillaexpreward;
    }

    public float getFishingVanillaExpMultiplier() {
        return fishingvanillaexpmultiplier;
    }

    public double getFishingExpMultiplier() {
        return fishingexpmultiplier;
    }

    public double getBreedingExpMultiplier() {
        return breedingexpmultiplier;
    }

    public void setFarmingVanillaExpReward(float farmingVanillaExpReward) {
        this.farmingvanillaexpreward = farmingVanillaExpReward;
    }

    public void setFishingVanillaExpMultiplier(float fishingvanillaexpmultiplier) {
        this.fishingvanillaexpmultiplier = fishingvanillaexpmultiplier;
    }

    public void setBreedingExpMultiplier(double breedingexpmultiplier) {
        this.breedingexpmultiplier = breedingexpmultiplier;
    }

    public void setFishingExpMultiplier(double fishingexpmultiplier) {
        this.fishingexpmultiplier = fishingexpmultiplier;
    }

    public void setRareDropRateMultiplier(float raredropratemultiplier) {
        this.raredropratemultiplier = raredropratemultiplier;
    }

    public void setDropMultiplier(float dropmultiplier) {
        this.dropmultiplier = dropmultiplier;
    }

    public void setInstantHarvesting(boolean instantharvesting) {
        this.instantharvesting = instantharvesting;
    }

    public void setInstantGrowthRate(float instantgrowthrate) {
        this.instantgrowthrate = instantgrowthrate;
    }

    public void setFishingTimeMultiplier(float fishingtimemultiplier) {
        this.fishingtimemultiplier = fishingtimemultiplier;
    }

    public void setFishingRewardTier(float fishingrewardtier) {
        this.fishingrewardtier = fishingrewardtier;
    }

    public void setBreedingVanillaExpMultiplier(float breedingexpmultiplier) {
        this.breedingvanillaexpmultiplier = breedingexpmultiplier;
    }

    public void setBabyAnimalAgeMultiplier(float babyanimalagemultiplier) {
        this.babyanimalagemultiplier = babyanimalagemultiplier;
    }

    public void setFarmingExpMultiplier(double farmingexpmultiplier) {
        this.farmingexpmultiplier = farmingexpmultiplier;
    }

    public void setHiveHoneySaveChance(float hivehoneysavechange) {
        this.hivehoneysavechance = hivehoneysavechange;
    }

    public static NamespacedKey getFarmingProfileKey() {
        return farmingProfileKey;
    }

    public float getRareDropRateMultiplier() {
        return raredropratemultiplier;
    }

    public float getDropMultiplier() {
        return dropmultiplier;
    }

    public boolean isInstantHarvestingUnlocked() {
        return instantharvesting;
    }

    public float getInstantGrowthRate() {
        return instantgrowthrate;
    }

    public float getFishingTimeMultiplier() {
        return fishingtimemultiplier;
    }

    public float getFishingRewardTier() {
        return fishingrewardtier;
    }

    public float getBreedingVanillaExpMultiplier() {
        return breedingvanillaexpmultiplier;
    }

    public float getBabyAnimalAgeMultiplier() {
        return babyanimalagemultiplier;
    }

    public double getFarmingExpMultiplier() {
        return farmingexpmultiplier;
    }

    public float getHiveHoneySaveChance() {
        return hivehoneysavechance;
    }

    public float getCarnivorousHungerMultiplier() {
        return carnivoroushungermultiplier;
    }

    public float getVegetarianHungerMultiplier() {
        return vegetarianhungermultiplier;
    }

    public float getPescotarianHungerMultiplier() {
        return pescotarianhungermultiplier;
    }

    public float getMagicalHungerMultiplier() {
        return magicalhungermultiplier;
    }

    public float getGarbageHungerMultiplier() {
        return garbagehungermultiplier;
    }

    public void setCarnivorousHungerMultiplier(float carnivoroushungermultiplier) {
        this.carnivoroushungermultiplier = carnivoroushungermultiplier;
    }

    public void setVegetarianHungerMultiplier(float vegetarianhungermultiplier) {
        this.vegetarianhungermultiplier = vegetarianhungermultiplier;
    }

    public void setPescotarianHungerMultiplier(float pescotarianhungermultiplier) {
        this.pescotarianhungermultiplier = pescotarianhungermultiplier;
    }

    public void setGarbageHungerMultiplier(float garbagehungermultiplier) {
        this.garbagehungermultiplier = garbagehungermultiplier;
    }

    public void setMagicalHungerMultiplier(float magicalhungermultiplier) {
        this.magicalhungermultiplier = magicalhungermultiplier;
    }

    public boolean isBadFoodImmune() {
        return isbadfoodimmune;
    }

    public void setBadFoodImmune(boolean isbadfoodimmune) {
        this.isbadfoodimmune = isbadfoodimmune;
    }

    public float getAnimalRareDropRateMultiplier() {
        return animalraredropratemultiplier;
    }

    public void setAnimalRareDropRateMultiplier(float animalraredropratemultiplier) {
        this.animalraredropratemultiplier = animalraredropratemultiplier;
    }

    @Override
    public void setDefaultStats(Player player) {
        Skill skill = SkillProgressionManager.getInstance().getSkill("FARMING");
        if (skill != null){
            if (skill instanceof FarmingSkill){
                FarmingSkill farmingSkill = (FarmingSkill) skill;
                for (PerkReward startingPerk : farmingSkill.getStartingPerks()){
                    startingPerk.execute(player);
                }
            }
        }
    }

    @Override
    public NamespacedKey getKey() {
        return farmingProfileKey;
    }

    @Override
    public FarmingProfile clone() throws CloneNotSupportedException {
        return (FarmingProfile) super.clone();
    }
}
