package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.advanced_modifiers;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.AdvancedDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.CustomDurabilityManager;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

import java.util.Collections;
import java.util.List;

public class DamageItemModifier extends DynamicItemModifier implements AdvancedDynamicItemModifier {

    public DamageItemModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 10;
        this.bigStepIncrease = 10;
        this.smallStepDecrease = 1;
        this.smallStepIncrease = 1;
        this.defaultStrength = 1;
        this.minStrength = Integer.MIN_VALUE;
        this.maxStrength = Integer.MAX_VALUE;

        this.description = Utils.chat("&7Damages this tool with the given amount. If the amount is negative, the item is repaired instead. " +
                "If the item cannot be damaged, the recipe is cancelled. If the tool does not have enough durability, the recipe is NOT cancelled.");
        this.displayName = Utils.chat("&b&lDamage Tool");
        this.icon = Material.WOODEN_PICKAXE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<durability_damage>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        return outputItem;
    }

    @Override
    public Pair<ItemStack, ItemStack> processItem(ItemStack i1, ItemStack i2, Player crafter, int timesExecuted) {
        if (i1 == null) return null;
        if (!this.use) return new Pair<>(i1, i2);
        if (!(i1.getItemMeta() instanceof Damageable)) return null;
        if (SmithingItemTreatmentManager.getInstance().isItemCustom(i1)){
            int maxDurability = CustomDurabilityManager.getInstance().getMaxDurability(i1);
            int currentDurability = CustomDurabilityManager.getInstance().getDurability(i1);
            CustomDurabilityManager.getInstance().setDurability(i1, Math.min(maxDurability, Math.max(0, currentDurability - ((int) strength))), maxDurability);
        } else {
            Damageable meta = (Damageable) i1.getItemMeta();
            int maxDurability = i1.getType().getMaxDurability();
            int currentDurability = meta.getDamage();
            meta.setDamage(Math.min(maxDurability, Math.max(0, currentDurability + ((int) strength))));
            i1.setItemMeta(meta);
        }
        return new Pair<>(i1, i2);
    }

    @Override
    public String toString() {
        if (strength > 0){
            return Utils.chat("&7Damages the item by &e" + ((int) strength) + " &7durability");
        } else {
            return Utils.chat("&7Repairs &e" + ((int) strength) + " &7durability to the item");
        }
    }
}
