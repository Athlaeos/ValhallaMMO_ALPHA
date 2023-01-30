package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class RequireCustomModifier extends DynamicItemModifier {

    public RequireCustomModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_CONDITIONALS;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Requires the item to have ValhallaMMO characteristics (it needs to be custom) for the recipe to proceed");
        this.displayName = Utils.chat("&7&lRequires Customized Item");
        this.icon = Material.NETHER_STAR;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        if (!SmithingItemTreatmentManager.getInstance().isItemCustom(outputItem)) return null;
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Requires the item to have custom characteristics.");
    }
}
