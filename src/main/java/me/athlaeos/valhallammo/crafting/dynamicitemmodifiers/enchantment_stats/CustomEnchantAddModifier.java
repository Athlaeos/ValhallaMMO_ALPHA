package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.enchantment_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.enchantmentwrappers.EnchantmentWrapper;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.CustomEnchantmentManager;
import me.athlaeos.valhallammo.managers.EnchantingItemEnchantmentsManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class CustomEnchantAddModifier extends DuoArgDynamicItemModifier {
    private final String enchantment;

    public CustomEnchantAddModifier(String name, Material icon, String enchantment, double big, double small, double min, double max) {
        super(name, 0, 0, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.CUSTOM_ENCHANTMENTS;

        this.bigStepDecrease = big;
        this.bigStepIncrease = big;
        this.smallStepDecrease = small;
        this.smallStepIncrease = small;
        this.defaultStrength = 0;
        this.minStrength = min;
        this.maxStrength = max;

        this.bigStepDecrease2 = 10;
        this.bigStepIncrease2 = 10;
        this.smallStepDecrease2 = 1;
        this.smallStepIncrease2 = 1;
        this.defaultStrength2 = 0;
        this.minStrength2 = -1;
        this.maxStrength2 = 100000;

        this.enchantment = enchantment;
        this.description = Utils.chat("&7Adds the &e" + Utils.toPascalCase(enchantment.replace("_", " ")) + " &7enchantment to the item. " +
                "Enchantment is cancelled if item already has this enchantment. This enchantment scales with a percentage of the player's skill" +
                " unless this percentage is -1. ");
        this.displayName = Utils.chat("&7&lAdd Enchantment: &d&l" + Utils.toPascalCase(enchantment.replace("_", " ")));
        this.icon = icon;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<base_strength>");
    }

    @Override
    public List<String> tabAutoCompleteSecondArg() {
        return Collections.singletonList("<percentage_skill>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (CustomEnchantmentManager.getInstance().getCustomEnchant(outputItem, enchantment) != null) return null;
        EnchantmentWrapper wrapper = CustomEnchantmentManager.getInstance().getRegisteredEnchantments().get(enchantment);
        if (wrapper == null) {
            if (crafter == null) return null;
            crafter.sendMessage(Utils.chat("&cInvalid enchantment " + enchantment + " was called, but it did not exist. Notify server owner/developer(s)"));
            return null;
        }
        try {
            wrapper = wrapper.clone();
        } catch (CloneNotSupportedException ignored){
            return null;
        }
        wrapper.setAmplifier(strength);
        CustomEnchantmentManager.getInstance().addEnchantment(outputItem, wrapper);
        if (strength2 > 0){
            if (crafter == null) return null;
            int enchantingSkill = (int) ((strength2 / 100) * Math.floor(AccumulativeStatManager.getInstance().getStats("ENCHANTING_QUALITY_GENERAL", crafter, this.use) + AccumulativeStatManager.getInstance().getStats("ENCHANTING_QUALITY_CUSTOM", crafter, this.use)));
            EnchantingItemEnchantmentsManager.getInstance().applyEnchantmentScaling(outputItem, enchantingSkill, enchantment, strength);
        }
        return outputItem;
    }

    @Override
    public String toString() {
        if (strength2 >= 0){
            return Utils.chat(String.format("&7Gives the item &e%s %.2f&7 scaling with &e%,.1f%%&7 of the player's skill. Recipe is cancelled if item already has this enchantment.", Utils.toPascalCase(enchantment.replace("_", " ")), strength, strength2));
        } else {
            return Utils.chat(String.format("&7Gives the item &e%s %.2f&7 that doesn't scale with the player's skill. Recipe is cancelled if item already has this enchantment.", Utils.toPascalCase(enchantment.replace("_", " ")), strength));
        }
    }
}
