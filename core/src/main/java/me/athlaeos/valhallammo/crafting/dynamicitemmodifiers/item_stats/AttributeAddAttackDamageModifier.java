package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.items.attributewrappers.VanillaAttackDamageWrapper;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class AttributeAddAttackDamageModifier extends DynamicItemModifier {
    public AttributeAddAttackDamageModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_VANILLA;

        this.bigStepDecrease = 1;
        this.bigStepIncrease = 1;
        this.smallStepDecrease = 0.1;
        this.smallStepIncrease = 0.1;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1000;
        this.description = Utils.chat("&7Adds &eAttack Damage &7as a default attribute to the item.");
        this.displayName = Utils.chat("&7&lAdd Stat: &e&lAttack Damage");
        this.icon = Material.STONE_AXE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<amount>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        if (ItemAttributesManager.getInstance().getCurrentStats(outputItem).isEmpty()){
            ItemAttributesManager.getInstance().applyVanillaStats(outputItem);
        }
        ItemAttributesManager.getInstance().addDefaultStat(outputItem, new VanillaAttackDamageWrapper(strength, AttributeModifier.Operation.ADD_NUMBER, ItemUtils.getEquipmentSlot(outputItem)));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Gives the item &e%.2f &7attack damage. Recipe is cancelled if item already has attack damage.", strength));
    }
}
