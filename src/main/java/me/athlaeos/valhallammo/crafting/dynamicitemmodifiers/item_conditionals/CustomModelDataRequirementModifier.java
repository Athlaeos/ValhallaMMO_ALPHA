package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomModelDataRequirementModifier extends DynamicItemModifier {
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
        this.description = Utils.chat("&7Requires the item to have a specific Custom Model Data");
        this.displayName = Utils.chat("&7&lRequire Custom Model Data");
        this.icon = Material.GRASS_BLOCK;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        if (!meta.hasCustomModelData()) return null;
        if (meta.getCustomModelData() != (int) strength) return null;
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Requires the item to have &e" + ((int) strength) + "&7 as Custom Model Data");
    }
}
