package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.items.ItemTreatment;
import me.athlaeos.valhallammo.managers.ItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;

public class RemoveArmorFittingModifier extends DynamicItemModifier {
    public RemoveArmorFittingModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_CONDITIONALS;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Removes the treatment &eArmor Fitting &7from the item if present." +
                " -nThis can be used to apply conditions to previous recipes, but by default it's intended to be executed " +
                " on armor when players attempts to heat/repair the item.");
        this.displayName = Utils.chat("&7&lRemove treatment: &e&lArmor Fitting");
        this.icon = Material.NETHERITE_SCRAP;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        Collection<ItemTreatment> itemTreatments = ItemTreatmentManager.getInstance().getItemsTreatments(outputItem);
        itemTreatments.remove(ItemTreatment.ARMOR_FITTING);
        ItemTreatmentManager.getInstance().setItemsTreatments(outputItem, itemTreatments);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Removes the &eArmor Fitting &7treatment from an item if present.");
    }
}
