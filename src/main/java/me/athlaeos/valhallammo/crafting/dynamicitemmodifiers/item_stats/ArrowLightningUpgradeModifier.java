package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.CustomArrowManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class ArrowLightningUpgradeModifier extends DynamicItemModifier {
    public ArrowLightningUpgradeModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_CUSTOM;

        this.bigStepDecrease = 1;
        this.bigStepIncrease = 1;
        this.smallStepDecrease = 1;
        this.smallStepIncrease = 1;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1;

        this.description = Utils.chat("&7Summons lightning wherever the arrow lands");
        this.displayName = Utils.chat("&7&lSpecial Arrow: &b&lLightning Arrow");
        this.icon = Material.END_ROD;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Arrays.asList("0-NoStormNeeded", "1-StormNeeded");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;

        CustomArrowManager.getInstance().addArrowAttribute(outputItem, "lightning_arrow", strength);

        return outputItem;
    }

    @Override
    public String toString() {
        boolean needsRain = ((int) strength) == 1;
        return Utils.chat("&7Summons lightning wherever the arrow lands. " + (needsRain ? "&eRequires rain/thunder to summon lightning" : ""));
    }
}
