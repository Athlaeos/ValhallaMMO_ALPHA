package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.Collections;
import java.util.List;

public class FlagAddDuoHandAttributesModifier extends DynamicItemModifier {
    private static NamespacedKey dualHandsFlag = new NamespacedKey(ValhallaMMO.getPlugin(), "dual_hands_custom_flag");
    public FlagAddDuoHandAttributesModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Adds the DUAL_HANDS flag to the item. DUAL_HANDS is a custom flag that causes attributes applied " +
                "on held items to function for both main hand and off hand. This may look quite ugly though, so maybe you'll want to also apply the" +
                " HIDE_ATTRIBUTES modifier to hide them from the item.");
        this.displayName = Utils.chat("&7&lAdd Flag: &e&lDual Hand Attributes");
        this.icon = Material.PAPER;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        meta.getPersistentDataContainer().set(dualHandsFlag, PersistentDataType.INTEGER, 1);
        outputItem.setItemMeta(meta);
        return outputItem;
    }

    public static NamespacedKey getDualHandsFlag() {
        return dualHandsFlag;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Adds the &eDUAL_HANDS &7flag to the item. This causes attributes to apply for both hands instead of just main hand.");
    }
}
