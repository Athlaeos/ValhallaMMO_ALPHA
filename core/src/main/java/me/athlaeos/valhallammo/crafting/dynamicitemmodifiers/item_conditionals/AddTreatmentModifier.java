package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.ItemTreatment;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class AddTreatmentModifier extends DynamicItemModifier {
    private final ItemTreatment treatment;
    private final String treatmentString;

    public AddTreatmentModifier(String name, double strength, ModifierPriority priority, ItemTreatment treatment, Material icon) {
        super(name, strength, priority);
        this.treatment = treatment;
        treatmentString = Utils.toPascalCase(treatment.toString().replace("_", " "));

        this.name = name;

        this.category = ModifierCategory.ITEM_CONDITIONALS;
        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Applies the treatment &e" + treatmentString + " &7to the item. -nThe recipe is cancelled if" +
                " the item already has this treatment. This can be used to add" +
                " conditions to following recipes.");
        this.displayName = Utils.chat("&7&lApply treatment: &e&l" + treatmentString);
        this.icon = icon;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        if (!this.validate) return outputItem;
        if (SmithingItemTreatmentManager.getInstance().hasTreatment(outputItem, treatment)) {
            return null;
        }
        Collection<ItemTreatment> itemTreatments = SmithingItemTreatmentManager.getInstance().getItemsTreatments(outputItem);
        itemTreatments.add(treatment);
        SmithingItemTreatmentManager.getInstance().setItemsTreatments(outputItem, itemTreatments);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Adds the &e" + treatmentString + " &7treatment to an item. Recipe is cancelled if item already has this property.");
    }
}
