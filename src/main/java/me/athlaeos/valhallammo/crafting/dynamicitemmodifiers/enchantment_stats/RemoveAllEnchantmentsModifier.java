package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.enchantment_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.CustomEnchantmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class RemoveAllEnchantmentsModifier extends DynamicItemModifier {
    public RemoveAllEnchantmentsModifier(String name) {
        super(name, 0, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ENCHANTING_MISC;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Removes all enchantments from the item, including custom ones");
        this.displayName = Utils.chat("&7&lRemoves all enchantments");
        this.icon = Material.GRINDSTONE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        for (Enchantment e : outputItem.getEnchantments().keySet()){
            outputItem.removeEnchantment(e);
        }
        CustomEnchantmentManager.getInstance().setEnchantments(outputItem, new HashMap<>());
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Removes all enchantments from the item (custom and vanilla).");
    }
}
