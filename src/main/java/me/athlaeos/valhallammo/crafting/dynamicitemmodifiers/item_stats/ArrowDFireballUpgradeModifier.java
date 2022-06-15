package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicEditable;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.CustomArrowManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class ArrowDFireballUpgradeModifier extends DuoArgDynamicItemModifier implements DynamicEditable {
    public ArrowDFireballUpgradeModifier(String name, double strength, double strength2, ModifierPriority priority) {
        super(name, strength, strength2, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_CUSTOM;

        this.bigStepDecrease = 1; // explosion radius
        this.bigStepIncrease = 1;
        this.smallStepDecrease = 0.1D;
        this.smallStepIncrease = 0.1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 127;

        this.bigStepDecrease2 = 1; // should the explosion cause fire
        this.bigStepIncrease2 = 1;
        this.smallStepDecrease2 = 1;
        this.smallStepIncrease2 = 1;
        this.defaultStrength2 = 0;
        this.minStrength2 = 0;
        this.maxStrength2 = 1;

        this.description = Utils.chat("&7Causes the arrow to be replaced by a dragon fireball projectile.");
        this.displayName = Utils.chat("&7&lSpecial Arrow: &5&lDragon Fireball");
        this.icon = Material.FIRE_CHARGE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<radius>");
    }

    @Override
    public List<String> tabAutoCompleteSecondArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;

        CustomArrowManager.getInstance().addArrowAttribute(outputItem, "dragon_fireball_arrow", strength, (int) strength2);

        return outputItem;
    }

    @Override
    public String toString() {
        boolean isIncendiary = ((int) strength2) == 1;
        return Utils.chat(String.format("&7Fireball Radius: &6%.1f&7, Causes Poison: &6%s",
                strength, (isIncendiary ? "Yes" : "No")));
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
                meta.setDisplayName(Utils.chat("&6&lCauses Poison"));
                break;
        }
        icon.setItemMeta(meta);
    }
}
