package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class RandomizedAmountModifier extends DuoArgDynamicItemModifier {

    public RandomizedAmountModifier(String name) {
        super(name, 0D, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 10;
        this.bigStepIncrease = 10;
        this.smallStepDecrease = 1;
        this.smallStepIncrease = 1;
        this.defaultStrength = 0;
        this.minStrength = -64D;
        this.maxStrength = 64D;

        this.bigStepDecrease2 = 10;
        this.bigStepIncrease2 = 10;
        this.smallStepDecrease2 = 1;
        this.smallStepIncrease2 = 1;
        this.defaultStrength2 = 0;
        this.minStrength2 = -64D;
        this.maxStrength2 = 64D;
        this.description = Utils.chat("&7Changes the item's amount to be randomized between " +
                "the two given values. Respects max stack size.");
        this.displayName = Utils.chat("&b&lRandomize Amount");
        this.icon = Material.STICK;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<minimum_amount>");
    }

    @Override
    public List<String> tabAutoCompleteSecondArg() {
        return Collections.singletonList("<maximum_amount>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        if (!this.use) return outputItem;
        if (strength > strength2) {
            crafter.sendMessage(Utils.chat("&cThis recipe has been improperly configured, randomized lower bound " +
                    "is not allowed to exceed the upper bound. Notify server owner(s)/admin(s)"));
            return null;
        }
        int newAmount = Utils.getRandom().nextInt((int) strength2 + 1) + (int) strength;
        outputItem.setAmount(Math.min(Math.max(1, newAmount), outputItem.getMaxStackSize()));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's amount to a random value between &e%d &7and &e%d&7.", (int) strength, (int) strength2));
    }
}
