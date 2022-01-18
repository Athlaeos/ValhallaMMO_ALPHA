package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.enchantments;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.dom.ArtificialGlow;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class AddEnchantmentGlowModifier extends DynamicItemModifier {
    public AddEnchantmentGlowModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ENCHANTING_STATS;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Adds an enchantment glimmer to the item");
        this.displayName = Utils.chat("&7&lAdd Enchantment Glimmer");
        this.icon = Material.ENCHANTED_BOOK;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        outputItem.addUnsafeEnchantment(new ArtificialGlow(), 0);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Adds an enchantment glow to the item");
    }
}
