package me.athlaeos.valhallammo.skills.enchanting;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.EnchantmentType;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.io.Serializable;

public class EnchantingProfile extends Profile implements Serializable {
    private static final NamespacedKey enchantingProfileKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_enchanting");

    private float vanillaenchantmentamplifychance = 0F; // chance for vanilla enchantments to be amplified (or weakened)
    private int maxcustomenchantmentsallowed = 0; // amount of custom enchantments one is allowed to add to an item
    private float lapissavechance = 0F; // chance for lapis lazuli to be refunded to the player
    private float exprefundchance = 0F; // chance for experience to be refunded to the player
    private float exprefundfraction = 0F; // amount of experience refunded if chance procs, capped at 1.0
    private float vanillaexpgainmultiplier = 1F; // multiplier of the player's experience gained

    private int enchantingquality_general = 0; // general enchanting skill
    private int enchantingquality_vanilla = 0; // vanilla enchanting skill
    private int enchantingquality_custom = 0; // custom enchanting skill

    private double enchantingexpmultiplier_general = 100D;
    private double enchantingexpmultiplier_custom = 100D;
    private double enchantingexpmultiplier_vanilla = 100D;

    public EnchantingProfile(Player owner){
        super(owner);
        if (owner == null) return;
        this.key = enchantingProfileKey;
    }

    @Override
    public void setDefaultStats(Player player) {
        Skill skill = SkillProgressionManager.getInstance().getSkill("ENCHANTING");
        if (skill != null){
            if (skill instanceof EnchantingSkill){
                EnchantingSkill enchantingSkill = (EnchantingSkill) skill;
                for (PerkReward startingPerk : enchantingSkill.getStartingPerks()){
                    startingPerk.execute(player);
                }
            }
        }
    }

    public float getLapisSaveChance() {
        return lapissavechance;
    }

    public void setLapisSaveChance(float value) {
        this.lapissavechance = value;
    }

    public float getExpRefundChance() {
        return exprefundchance;
    }

    public void setExpRefundChance(float value) {
        this.exprefundchance = value;
    }

    public float getVanillaExpGainMultiplier() {
        return vanillaexpgainmultiplier;
    }

    public void setVanillaExpGainMultiplier(float value) {
        this.vanillaexpgainmultiplier = value;
    }

    public float getExpRefundFraction() {
        return exprefundfraction;
    }

    public void setExpRefundFraction(float value) {
        this.exprefundfraction = value;
    }

    public double getEnchantingExpMultiplier(EnchantmentType type) {
        if (type == null) return enchantingexpmultiplier_general;
        switch (type){
            case CUSTOM:{
                return enchantingexpmultiplier_custom;
            }
            case VANILLA:{
                return enchantingexpmultiplier_vanilla;
            }
        }
        return enchantingexpmultiplier_general;
    }

    public void setEnchantingExpMultiplier(EnchantmentType type, double value) {
        if (type == null) {
            this.enchantingexpmultiplier_general = value;
            return;
        }
        switch (type){
            case CUSTOM:{
                this.enchantingexpmultiplier_custom = value;
                return;
            }
            case VANILLA:{
                this.enchantingexpmultiplier_vanilla = value;
                return;
            }
        }
        this.enchantingexpmultiplier_general = value;
    }

    public int getEnchantingSkill(EnchantmentType type) {
        if (type == null) return enchantingquality_general;
        switch (type){
            case CUSTOM:{
                return enchantingquality_custom;
            }
            case VANILLA:{
                return enchantingquality_vanilla;
            }
        }
        return enchantingquality_general;
    }

    public void setEnchantingSkill(EnchantmentType type, int value) {
        if (type == null) {
            this.enchantingquality_general = value;
            return;
        }
        switch (type){
            case CUSTOM:{
                this.enchantingquality_custom = value;
                return;
            }
            case VANILLA:{
                this.enchantingquality_vanilla = value;
                return;
            }
        }
        this.enchantingquality_general = value;
    }

    public float getVanillaEnchantmentAmplifyChance() {
        return vanillaenchantmentamplifychance;
    }

    public int getMaxCustomEnchantmentsAllowed() {
        return maxcustomenchantmentsallowed;
    }

    public void setVanillaEnchantmentAmplifyChance(float value) {
        this.vanillaenchantmentamplifychance = value;
    }

    public void setMaxCustomEnchantmentsAllowed(int value) {
        this.maxcustomenchantmentsallowed = value;
    }

    @Override
    public NamespacedKey getKey() {
        return enchantingProfileKey;
    }

    @Override
    public EnchantingProfile clone() throws CloneNotSupportedException {
        return (EnchantingProfile) super.clone();
    }
}
