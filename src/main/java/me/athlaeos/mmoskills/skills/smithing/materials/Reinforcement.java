package me.athlaeos.mmoskills.skills.smithing.materials;

import me.athlaeos.mmoskills.skills.smithing.materials.perks.MaterialPerk;
import me.athlaeos.mmoskills.skills.smithing.materials.perks.Wood;

import java.util.ArrayList;
import java.util.List;

public enum Reinforcement {
    WOOD(new ArrayList<>()),
    STONE(new ArrayList<>()),
    GOLD(new ArrayList<>()),
    IRON(new ArrayList<>()),
    DIAMOND(new ArrayList<>()),
    NETHERITE(new ArrayList<>());

    private List<MaterialPerk> perks;
    Reinforcement(List<MaterialPerk> perks){
        this.perks = perks;
    }

    public List<MaterialPerk> getPerks() {
        return perks;
    }

    public void setPerks(List<MaterialPerk> perks) {
        this.perks = perks;
    }

    public void addPerk(MaterialPerk perk){
        this.perks.add(perk);
    }
}
