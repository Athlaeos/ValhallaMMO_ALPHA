package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.InvisibleIfIncompatibleModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class CustomModelDataRequirementModifier extends DynamicItemModifier implements InvisibleIfIncompatibleModifier {
    public CustomModelDataRequirementModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_CONDITIONALS;

        this.bigStepDecrease = 25D;
        this.bigStepIncrease = 25D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 9999999;
        this.description = Utils.chat("&7Requires the item to have a specific Custom Model Data. " +
                "If &e0 &7is selected, the recipe will be cancelled if the item has any custom model data");
        this.displayName = Utils.chat("&7&lRequire Custom Model Data");
        this.icon = Material.GRASS_BLOCK;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<model_data>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        if (!meta.hasCustomModelData() && ((int) strength) > 0) return null;
        if ((int) strength == 0 && !meta.hasCustomModelData()) return outputItem;
        if (meta.getCustomModelData() != (int) strength) return null;
        return outputItem;
    }

    @Override
    public String toString() {
        if (strength == 0){
            return Utils.chat("&7Requires the item to have &eno&7 Custom Model Data");
        } else {
            return Utils.chat("&7Requires the item to have &e" + ((int) strength) + "&7 as Custom Model Data");
        }
    }
}
