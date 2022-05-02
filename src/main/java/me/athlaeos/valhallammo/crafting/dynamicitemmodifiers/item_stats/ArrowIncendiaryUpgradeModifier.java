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

import java.util.Collections;
import java.util.List;

public class ArrowIncendiaryUpgradeModifier extends TripleArgDynamicItemModifier implements DynamicEditable {
    public ArrowIncendiaryUpgradeModifier(String name, double strength, double strength2, double strength3, ModifierPriority priority) {
        super(name, strength, strength2, strength3, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS;

        this.bigStepDecrease = 20; // entity fire duration
        this.bigStepIncrease = 20;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = Short.MAX_VALUE;

        this.bigStepDecrease2 = 1; // fire radius
        this.bigStepIncrease2 = 1;
        this.smallStepDecrease2 = 1;
        this.smallStepIncrease2 = 1;
        this.defaultStrength2 = 0;
        this.minStrength2 = 0;
        this.maxStrength2 = 32;

        this.bigStepDecrease3 = 0.1; // fire density
        this.bigStepIncrease3 = 0.1;
        this.smallStepDecrease3 = 0.01;
        this.smallStepIncrease3 = 0.01;
        this.defaultStrength3 = 1;
        this.minStrength3 = 0;
        this.maxStrength3 = 1;

        this.description = Utils.chat("&7Causes the arrow shot to light all blocks and entities around the location" +
                " of impact on fire.");
        this.displayName = Utils.chat("&7&lSpecial Arrow: &6&lIncendiary");
        this.icon = Material.FIRE_CHARGE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<entity_fire_duration>");
    }

    @Override
    public List<String> tabAutoCompleteSecondArg() {
        return Collections.singletonList("<radius>");
    }

    @Override
    public List<String> tabAutoCompleteThirdArg() {
        return Collections.singletonList("<density>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;

        CustomArrowManager.getInstance().addArrowAttribute(outputItem, "incendiary_arrow", strength, strength2, strength3);

        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Causes all blocks and entities in a &e%d &7block radius to be lit on fire." +
                " Entities are lit on fire for &e%.2f seconds &7and block fire has a density of &e%.1f%%&7.",
                (int) strength2,
                ((int) strength) * 0.05,
                strength3 * 100));
    }

    @Override
    public void editIcon(ItemStack icon, int buttonNumber) {
        ItemMeta meta = icon.getItemMeta();
        if (meta == null) return;
        switch (buttonNumber){
            case 1:
                meta.setDisplayName(Utils.chat("&6&lFire Duration (ticks)"));
                break;
            case 2:
                meta.setDisplayName(Utils.chat("&6&lRadius"));
                break;
            case 3:
                meta.setDisplayName(Utils.chat("&6&lFire Density"));
                break;
        }
        icon.setItemMeta(meta);
    }
}
