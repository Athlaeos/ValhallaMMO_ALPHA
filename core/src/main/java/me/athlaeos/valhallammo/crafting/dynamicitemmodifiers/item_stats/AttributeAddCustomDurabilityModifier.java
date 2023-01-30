package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.CustomDurabilityManager;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.items.attributewrappers.CustomMaxDurabilityWrapper;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Collections;
import java.util.List;

public class AttributeAddCustomDurabilityModifier extends DynamicItemModifier {

    public AttributeAddCustomDurabilityModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_CUSTOM;

        this.bigStepDecrease = 25D;
        this.bigStepIncrease = 25D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 100;
        this.minStrength = 0;
        this.maxStrength = Integer.MAX_VALUE;
        this.description = Utils.chat("&7Sets a default custom durability to the item. The strength of the modifier " +
                "represents the default max durability of the item. This only works on items that can be damaged.");
        this.displayName = Utils.chat("&7&lAdd Stat: &e&lMax Durability");
        this.icon = Material.IRON_PICKAXE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<amount>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        if (!(outputItem.getItemMeta() instanceof Damageable)) return null;
        ItemAttributesManager.getInstance().addDefaultStat(outputItem, new CustomMaxDurabilityWrapper(
                strength,
                AttributeModifier.Operation.ADD_NUMBER,
                EquipmentSlot.HAND
        ));
        CustomDurabilityManager.getInstance().setDurability(outputItem, (int) strength, (int) strength);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's default max durability to &e%s.", "" + strength));
    }
}
