package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.enchantment_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.CustomEnchantmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class AddCustomEnchantCounterModifier extends DynamicItemModifier {
    public AddCustomEnchantCounterModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ENCHANTING_CONDITIONALS;

        this.bigStepDecrease = 1;
        this.bigStepIncrease = 1;
        this.smallStepDecrease = 1;
        this.smallStepIncrease = 1;
        this.defaultStrength = 1;
        this.minStrength = -16;
        this.maxStrength = 16;
        this.description = Utils.chat("&7Adds an amount to the item's custom enchantment counter. This can be used to " +
                "apply further conditions to the item");
        this.displayName = Utils.chat("&7&lAdd Enchantment Counter");
        this.icon = Material.KNOWLEDGE_BOOK;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<amount>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        int count = (int) strength;
        CustomEnchantmentManager.getInstance().setEnchantmentsCount(outputItem, CustomEnchantmentManager.getInstance().getEnchantmentsCount(outputItem) + count);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Adds " + ((int) strength) + " to the Enchantment Counter");
    }
}
