package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.advanced_modifiers;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.AdvancedDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class ItemEqualizerModifier extends DynamicItemModifier implements AdvancedDynamicItemModifier {
    public ItemEqualizerModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ENCHANTING_MISC;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;

        this.description = Utils.chat("&7Replaces this item by an exact replica of the other item");
        this.displayName = Utils.chat("&7&lTransform Item");
        this.icon = Material.SLIME_BALL;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public Pair<ItemStack, ItemStack> processItem(ItemStack i1, ItemStack i2, Player crafter, int timesExecuted) {
        return new Pair<>(i2.clone(), i2);
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Replaces this item by an exact replica of the other item");
    }
}
