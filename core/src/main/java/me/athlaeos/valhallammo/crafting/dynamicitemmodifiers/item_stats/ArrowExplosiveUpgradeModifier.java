package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicEditable;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.managers.CustomArrowManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ArrowExplosiveUpgradeModifier extends TripleArgDynamicItemModifier implements DynamicEditable {
    public ArrowExplosiveUpgradeModifier(String name) {
        super(name, 0D, 0D, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_CUSTOM;

        this.bigStepDecrease = 1; // explosion radius
        this.bigStepIncrease = 1;
        this.smallStepDecrease = 0.1D;
        this.smallStepIncrease = 0.1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 127;

        this.bigStepDecrease2 = 1; // should the explosion damage terrain
        this.bigStepIncrease2 = 1;
        this.smallStepDecrease2 = 1;
        this.smallStepIncrease2 = 1;
        this.defaultStrength2 = 0;
        this.minStrength2 = 0;
        this.maxStrength2 = 1;

        this.bigStepDecrease3 = 1; // should the explosion light terrain on fire
        this.bigStepIncrease3 = 1;
        this.smallStepDecrease3 = 1;
        this.smallStepIncrease3 = 1;
        this.defaultStrength3 = 0;
        this.minStrength3 = 0;
        this.maxStrength3 = 1;

        this.description = Utils.chat("&7Causes the arrow to be explosive when hitting ground or entities." +
                " The arrow is despawned immediately upon explosion, so it can no longer be picked up and used" +
                " again.");
        this.displayName = Utils.chat("&7&lSpecial Arrow: &c&lExplosive");
        this.icon = Material.TNT;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<radius>");
    }

    @Override
    public List<String> tabAutoCompleteSecondArg() {
        return Arrays.asList("0-NoDamageTerrain", "1-DamageTerrain");
    }

    @Override
    public List<String> tabAutoCompleteThirdArg() {
        return Arrays.asList("0-NoFire", "1-Fire");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;

        strength2 = (int) strength2;
        strength3 = (int) strength3;
        CustomArrowManager.getInstance().addArrowAttribute(outputItem, "explosive_arrow", strength, strength2, strength3);

        return outputItem;
    }

    @Override
    public String toString() {
        boolean destroysTerrain = ((int) strength2) == 1;
        boolean causesFire = ((int) strength3) == 1;
        return Utils.chat(String.format("&7Explosion Radius: &6%.1f&7, Destroys Terrain: &6%s&7, Causes Fire: &6%s",
                strength, (destroysTerrain ? "Yes" : "No"), (causesFire ? "Yes" : "No")));
    }

    @Override
    public void editIcon(ItemStack icon, int buttonNumber) {
        ItemMeta meta = icon.getItemMeta();
        if (meta == null) return;
        switch (buttonNumber){
            case 1:
                meta.setDisplayName(Utils.chat("&6&lExplosion Radius"));
                break;
            case 2:
                meta.setDisplayName(Utils.chat("&6&lDestroys Terrain"));
                break;
            case 3:
                meta.setDisplayName(Utils.chat("&6&lCauses Fire"));
                break;
        }
        icon.setItemMeta(meta);
    }
}
