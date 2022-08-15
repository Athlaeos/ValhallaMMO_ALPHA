package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.List;

public class UnbreakableSetModifier extends DynamicItemModifier {
    public UnbreakableSetModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 1;
        this.bigStepIncrease = 1;
        this.smallStepDecrease = 1;
        this.smallStepIncrease = 1;
        this.defaultStrength = 1;
        this.minStrength = 0;
        this.maxStrength = 1;
        this.description = Utils.chat("&7Sets the unbreakable value of the item. 0 is breakable, 1 is unbreakable. " +
                "Being unbreakable makes it so the item does not break through durability damage, and custom " +
                "durability values are not displayed. ");
        this.displayName = Utils.chat("&7&lSet Property: &e&lUnbreakable");
        this.icon = Material.DIAMOND;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Arrays.asList("0-Breakable", "1-Unbreakable");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        meta.setUnbreakable(((int) Math.round(strength)) == 1);
        outputItem.setItemMeta(meta);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Makes the item &e" + ((((int) Math.round(strength)) == 0) ? "breakable" : "unbreakable"));
    }
}
