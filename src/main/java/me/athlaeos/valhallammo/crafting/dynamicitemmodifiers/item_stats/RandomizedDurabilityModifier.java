package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

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

public class RandomizedDurabilityModifier extends DynamicItemModifier implements Cloneable{

    public RandomizedDurabilityModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;

        this.description = Utils.chat("&7Randomly damages the item. If the item is a custom item it will be assigned" +
                " a random custom durability between 0 and its max durability. If the item is not custom, it will" +
                " instead be damaged the vanilla way. If the item is not damageable it is not modified.");
        this.displayName = Utils.chat("&b&lRandomize Durability");
        this.icon = Material.WOODEN_PICKAXE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (!this.use) return outputItem;
        if (!(outputItem.getItemMeta() instanceof Damageable)) return outputItem;
        if (SmithingItemTreatmentManager.getInstance().isItemCustom(outputItem)){
            int maxDurability = CustomDurabilityManager.getInstance().getMaxDurability(outputItem);
            int randomDurability = Utils.getRandom().nextInt(maxDurability) + 1;
            CustomDurabilityManager.getInstance().setDurability(outputItem, randomDurability, maxDurability);
        } else {
            Damageable meta = (Damageable) outputItem.getItemMeta();
            int maxDurability = outputItem.getType().getMaxDurability();
            int randomDurability = Utils.getRandom().nextInt(maxDurability) + 1;
            meta.setDamage(maxDurability - randomDurability);
            outputItem.setItemMeta(meta);
        }
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Randomizes the item's durability.");
    }
}
