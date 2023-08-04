package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.CustomDurabilityManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class NumbericRepairModifier extends DynamicItemModifier {
    public NumbericRepairModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 25D;
        this.bigStepIncrease = 25D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 100;
        this.minStrength = 0;
        this.maxStrength = Integer.MAX_VALUE;
        this.description = Utils.chat("&7Repairs the item's durabilty. The strength of the modifier " +
                "represents the durability of the item " +
                "to repair. Example: " +
                "A strength of 33 would repair a pickaxe with 1500 max durability by 33 points.");
        this.displayName = Utils.chat("&7&lRepair item [NUMBERIC]");
        this.icon = Material.ANVIL;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<amount_repaired>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        if (crafter == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        if (!(meta instanceof Damageable)) return null;
        int itemDurabiity = CustomDurabilityManager.getInstance().getDurability(outputItem);
        if (itemDurabiity > 0){
            // Item has custom durability
            int maxDurability = CustomDurabilityManager.getInstance().getMaxDurability(outputItem);
            if (strength > 0 && maxDurability <= CustomDurabilityManager.getInstance().getDurability(outputItem)) return null;
            CustomDurabilityManager.getInstance().damageItem(outputItem, (int) -strength);
            return outputItem;
        }
        return null;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Repairing the item by &e%d points&7.", (int) strength));
    }
}
