package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.enchantment_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.CustomEnchantmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class EnchantCounterCancelIfExceededModifier extends DynamicItemModifier {
    public EnchantCounterCancelIfExceededModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ENCHANTING_CONDITIONALS;

        this.bigStepDecrease = 1;
        this.bigStepIncrease = 1;
        this.smallStepDecrease = 1;
        this.smallStepIncrease = 1;
        this.defaultStrength = 1;
        this.minStrength = 0;
        this.maxStrength = 16;
        this.description = Utils.chat("&7Cancels the recipe if the custom enchantment counter equals or exceeds what the player" +
                " is allowed to have.");
        this.displayName = Utils.chat("&7&lCancel if Enchantment Counter Equals or Exceeds");
        this.icon = Material.KNOWLEDGE_BOOK;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        int allowed = (int) AccumulativeStatManager.getInstance().getStats("ENCHANTING_MAX_CUSTOM_ALLOWED", crafter, this.use);
        if (CustomEnchantmentManager.getInstance().getEnchantmentsCount(outputItem) >= allowed && crafter.getGameMode() != GameMode.CREATIVE) return null;
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Cancel if Enchantment Counter equals or exceeds what's allowed for the player");
    }
}
