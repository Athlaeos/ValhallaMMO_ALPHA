package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.enchantment_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RandomlyEnchantItemModifier extends DuoArgDynamicItemModifier {
    public RandomlyEnchantItemModifier(String name) {
        super(name, 0D, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ENCHANTING_MISC;

        this.bigStepDecrease = 10;
        this.bigStepIncrease = 10;
        this.smallStepDecrease = 1;
        this.smallStepIncrease = 1;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 10000;

        this.bigStepDecrease2 = 1;
        this.bigStepIncrease2 = 1;
        this.smallStepDecrease2 = 1;
        this.smallStepIncrease2 = 1;
        this.defaultStrength2 = 0;
        this.minStrength2 = 0;
        this.maxStrength2 = 1;

        this.description = Utils.chat("&7Randomly enchants the item given an enchantment level." +
                " The enchantment distribution is the same as vanilla enchantment distribution. Recipe" +
                " is cancelled if item already has (non-custom) enchantments. Recipe is cancelled if " +
                "item after enchanting still has no enchantments");
        this.displayName = Utils.chat("&7&lEnchant Item : RANDOM");
        this.icon = Material.ENCHANTING_TABLE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<enchantment_strength>");
    }

    @Override
    public List<String> tabAutoCompleteSecondArg() {
        return Arrays.asList("0", "1");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        if (!this.use) return outputItem;
        if (!outputItem.getEnchantments().isEmpty()) return null;
        boolean treasure = (int) Math.round(strength2) == 1;
        int level = (int) Math.round(strength);

        ItemUtils.enchantItem(outputItem, level, treasure);
        if (outputItem.getEnchantments().isEmpty()) return null;
        return outputItem;
    }

    @Override
    public String toString() {
        if ((int) Math.round(strength2) == 0){
            return Utils.chat(String.format("&7Randomly enchants the item with an enchantment level of &e%d&7, &eexcluding&7 treasure enchantments.", (int) strength));
        } else if ((int) Math.round(strength2) == 1){
            return Utils.chat(String.format("&7Randomly enchants the item with an enchantment level of &e%d&7, &eincluding&7 treasure enchantments.", (int) strength));
        } else {
            return "invalid argument";
        }
    }
}
