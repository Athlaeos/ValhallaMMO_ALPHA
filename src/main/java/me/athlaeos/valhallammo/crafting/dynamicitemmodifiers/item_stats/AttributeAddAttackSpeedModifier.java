package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.items.attributewrappers.VanillaAttackSpeedWrapper;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class AttributeAddAttackSpeedModifier extends DynamicItemModifier {
    public AttributeAddAttackSpeedModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS;

        this.bigStepDecrease = 0.5;
        this.bigStepIncrease = 0.5;
        this.smallStepDecrease = 0.05;
        this.smallStepIncrease = 0.05;
        this.defaultStrength = 0;
        this.minStrength = -4;
        this.maxStrength = 4;
        this.description = Utils.chat("&7Adds &eAttack Speed &7as a default attribute to the item." +
                " Since players have a base attack speed of 4.0, modified items will usually appear to have " +
                "negative attack speed. This is normal." +
                " When an item says it has -1.5 attack speed, it actually has 2.5.");
        this.displayName = Utils.chat("&7&lAdd Stat: &e&lAttack Speed");
        this.icon = Material.STONE_SWORD;
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
        ItemAttributesManager.getInstance().addDefaultStat(outputItem, new VanillaAttackSpeedWrapper(strength, AttributeModifier.Operation.ADD_NUMBER, ItemUtils.getEquipmentSlot(outputItem.getType())));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Gives the item &e%.2f &7attack speed. Recipe is cancelled if item already has attack speed.", strength));
    }
}
