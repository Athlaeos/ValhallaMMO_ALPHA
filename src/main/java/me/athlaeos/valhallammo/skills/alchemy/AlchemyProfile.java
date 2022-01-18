package me.athlaeos.valhallammo.skills.smithing;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.materials.MaterialClass;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.SkillType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.io.Serializable;

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
    private double craftingexpmultiplier_all = 0D;
    private double craftingexpmultiplier_bow = 0D;
    private double craftingexpmultiplier_crossbow = 0D;
    private double craftingexpmultiplier_wood = 0D;
    private double craftingexpmultiplier_leather = 0D;
    private double craftingexpmultiplier_stone = 0D;
    private double craftingexpmultiplier_chain = 0D;
    private double craftingexpmultiplier_gold = 0D;
    private double craftingexpmultiplier_iron = 0D;
    private double craftingexpmultiplier_diamond = 0D;
    private double craftingexpmultiplier_netherite = 0D;
    private double craftingexpmultiplier_prismarine = 0D;
    private double craftingexpmultiplier_membrane = 0D;

    public SmithingProfile(Player owner){
        super(owner);
        if (owner == null) return;
        this.key = smithingProfileKey;
    }

    @Override
    public void setDefaultStats(Player player) {
        Skill skill = SkillProgressionManager.getInstance().getSkill(SkillType.SMITHING);
        if (skill != null){
            if (skill instanceof SmithingSkill){
                SmithingSkill smithingSkill = (SmithingSkill) skill;
                for (PerkReward startingPerk : smithingSkill.getStartingPerks()){
                    startingPerk.execute(player);
                }
            }
        }
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
    public NamespacedKey getKey() {
        return smithingProfileKey;
    }
}
