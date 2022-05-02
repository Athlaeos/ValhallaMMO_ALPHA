package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.CustomArrowManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ArrowRemoveIFramesUpgradeModifier extends DynamicItemModifier {
    public ArrowRemoveIFramesUpgradeModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;

        this.description = Utils.chat("&7Entities shot with an arrow with this upgrade will not receive immunity frames");
        this.displayName = Utils.chat("&7&lSpecial Arrow: &8&lNo I-Frames");
        this.icon = Material.SKELETON_SKULL;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;

        CustomArrowManager.getInstance().addArrowAttribute(outputItem, "no_iframes_arrow");

        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Entities shot with arrows with this upgrade will not receive immunity frames.");
    }
}
