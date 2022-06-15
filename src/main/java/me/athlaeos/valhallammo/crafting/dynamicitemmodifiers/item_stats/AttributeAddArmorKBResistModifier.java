package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.items.attributewrappers.VanillaKnockbackResistanceWrapper;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class AttributeAddArmorKBResistModifier extends DynamicItemModifier {
    public AttributeAddArmorKBResistModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_VANILLA;

        this.bigStepDecrease = 0.01;
        this.bigStepIncrease = 0.01;
        this.smallStepDecrease = 0.001;
        this.smallStepIncrease = 0.001;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0.1;
        this.description = Utils.chat("&7Adds &eKnockback Resistance &7as a default attribute to the item. ");
        this.displayName = Utils.chat("&7&lAdd Stat: &e&lKnockback Resistance");
        this.icon = Material.CHAINMAIL_CHESTPLATE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<amount>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        if (ItemAttributesManager.getInstance().getCurrentStats(outputItem).isEmpty()){
            ItemAttributesManager.getInstance().applyVanillaStats(outputItem);
        }
        ItemAttributesManager.getInstance().addDefaultStat(outputItem, new VanillaKnockbackResistanceWrapper(strength, AttributeModifier.Operation.ADD_NUMBER, ItemUtils.getEquipmentSlot(outputItem.getType())));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Gives the item &e%.2f%% &7knockback resistance. Recipe is cancelled if item already has armor toughness.", strength * 1000));
    }
}
