package me.athlaeos.valhallammo.dom;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.skills.account.AccountSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.SkillType;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class AccountProfile extends Profile implements Serializable{

    private static final NamespacedKey accountProfileKey = new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_account");

    private int spendableSkillPoints = 0;
    private Set<String> unlockedPerks = new HashSet<>();
    private Set<String> unlockedRecipes = new HashSet<>();

    public AccountProfile(Player owner){
        super(owner);
        if (owner == null) return;
        key = accountProfileKey;
    }

    @Override
    public void setDefaultStats(Player player) {
        Skill skill = SkillProgressionManager.getInstance().getSkill(SkillType.ACCOUNT);
        if (skill != null){
            if (skill instanceof AccountSkill){
                AccountSkill smithingSkill = (AccountSkill) skill;
                for (PerkReward startingPerk : smithingSkill.getStartingPerks()){
                    startingPerk.execute(player);
                }
            }
        }
    }

    public void setSpendableSkillPoints(int spendableSkillPoints) {
        this.spendableSkillPoints = spendableSkillPoints;
    }

    public void setUnlockedPerks(Set<String> unlockedPerks) {
        this.unlockedPerks = unlockedPerks;
    }

    public void setUnlockedRecipes(Set<String> unlockedRecipes) {
        this.unlockedRecipes = unlockedRecipes;
    }

    public int getSpendableSkillPoints() {
        return spendableSkillPoints;
    }

    public Set<String> getUnlockedPerks() {
        return unlockedPerks;
    }

    public Set<String> getUnlockedRecipes() {
        return unlockedRecipes;
    }

    @Override
    public NamespacedKey getKey() {
        return accountProfileKey;
    }
}
