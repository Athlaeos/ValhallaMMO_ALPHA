package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.items.attributewrappers.VanillaMovementSpeedWrapper;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class AttributeAddMovementSpeedModifier extends DynamicItemModifier {
    public AttributeAddMovementSpeedModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_VANILLA;

        this.bigStepDecrease = 0.1;
        this.bigStepIncrease = 0.1;
        this.smallStepDecrease = 0.01;
        this.smallStepIncrease = 0.01;
        this.defaultStrength = 0;
        this.minStrength = -1;
        this.maxStrength = 100;
        this.description = Utils.chat("&7Adds &eMovement Speed &7as a default attribute to the item.");
        this.displayName = Utils.chat("&7&lAdd Stat: &e&lMovement Speed");
        this.icon = Material.CHAINMAIL_BOOTS;
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
        ItemAttributesManager.getInstance().addDefaultStat(outputItem, new VanillaMovementSpeedWrapper(strength, AttributeModifier.Operation.ADD_SCALAR, ItemUtils.getEquipmentSlot(outputItem)));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Gives the item &e%s%.1f%% &7movement speed.", ((strength < 0) ? "" : "+"), strength * 100));
    }
}
