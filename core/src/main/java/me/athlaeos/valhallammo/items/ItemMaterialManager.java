package me.athlaeos.valhallammo.items;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class ItemMaterialManager {
    private static ItemMaterialManager manager = null;
    private final List<Material> pickaxes = new ArrayList<>();
    private final List<Material> axes = new ArrayList<>();
    private final List<Material> shovels = new ArrayList<>();
    private final List<Material> hoes = new ArrayList<>();
    private final List<Material> swords = new ArrayList<>();
    private final List<Material> boots = new ArrayList<>();
    private final List<Material> leggings = new ArrayList<>();
    private final List<Material> chestPlates = new ArrayList<>();
    private final List<Material> helmets = new ArrayList<>();
    private final Material shears;
    private final Material flintAndSteel;
    private final Material fishingRod;
    private final Material shield;
    private Material elytra;
    private Material bow;
    private Material crossbow;
    private Material trident;

    public ItemMaterialManager(){
        for (Material m : Material.values()){
            if (m.toString().contains("_PICKAXE")){
                pickaxes.add(m);
            } else if (m.toString().contains("_AXE")){
                axes.add(m);
            } else if (m.toString().contains("_SHOVEL") || m.toString().contains("_SPADE")){
                shovels.add(m);
            } else if (m.toString().contains("_HOE")){
                hoes.add(m);
            } else if (m.toString().contains("_SWORD")){
                swords.add(m);
            } else if (m.toString().contains("_BOOTS")){
                boots.add(m);
            } else if (m.toString().contains("_LEGGINGS")){
                leggings.add(m);
            } else if (m.toString().contains("_CHESTPLATE")){
                chestPlates.add(m);
            } else if (m.toString().contains("_HELMET")){
                helmets.add(m);
            }
        }
        shield = Material.SHIELD;
        shears = Material.SHEARS;
        flintAndSteel = Material.FLINT_AND_STEEL;
        fishingRod = Material.FISHING_ROD;
        elytra = Material.ELYTRA;
        bow = Material.BOW;
        trident = Material.TRIDENT;
        try{
            crossbow = Material.valueOf("CROSSBOW");
        } catch (IllegalArgumentException e){
            crossbow = null;
        }
    }

    public List<Material> getMaterialsFromType(EquipmentClass t){
        List<Material> materials = new ArrayList<>();

        switch (t){
            case AXE: materials.addAll(axes);
                break;
            case PICKAXE: materials.addAll(pickaxes);
                break;
            case SHOVEL: materials.addAll(shovels);
                break;
            case HOE: materials.addAll(hoes);
                break;
            case SWORD: materials.addAll(swords);
                break;
            case BOW: materials.add(bow);
                break;
            case CROSSBOW: if (crossbow != null) materials.add(crossbow);
                break;
            case TRIDENT: materials.add(trident);
                break;
            case HELMET: materials.addAll(helmets);
                break;
            case CHESTPLATE: materials.addAll(chestPlates);
                break;
            case LEGGINGS: materials.addAll(leggings);
                break;
            case BOOTS: materials.addAll(boots);
                break;
            case ELYTRA: materials.add(elytra);
                break;
            case SHEARS: materials.add(shears);
                break;
            case FISHING_ROD: materials.add(fishingRod);
                break;
            case FLINT_AND_STEEL: materials.add(flintAndSteel);
                break;
            case SHIELD: materials.add(shield);
                break;
        }

        return materials;
    }

    public static ItemMaterialManager getInstance(){
        if (manager == null){
            manager = new ItemMaterialManager();
        }
        return manager;
    }

    public List<Material> getAxes() {
        return axes;
    }

    public Material getTrident() {
        return trident;
    }

    public List<Material> getBoots() {
        return boots;
    }

    public List<Material> getChestPlates() {
        return chestPlates;
    }

    public List<Material> getHelmets() {
        return helmets;
    }

    public List<Material> getHoes() {
        return hoes;
    }

    public List<Material> getLeggings() {
        return leggings;
    }

    public List<Material> getPickaxes() {
        return pickaxes;
    }

    public List<Material> getShovels() {
        return shovels;
    }

    public List<Material> getSwords() {
        return swords;
    }

    public Material getShears() {
        return shears;
    }

    public Material getElytra() {
        return elytra;
    }

    public Material getFlintAndSteel() {
        return flintAndSteel;
    }

    public Material getFishingRod() {
        return fishingRod;
    }

    public Material getBow() {
        return bow;
    }

    public Material getCrossbow() {
        return crossbow;
    }

    public Material getShield() {
        return shield;
    }
}
