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

public class RemoveTreatmentModifier extends DynamicItemModifier {
    private final ItemTreatment treatment;
    private final String treatmentString;

    public RemoveTreatmentModifier(String name, ItemTreatment treatment, Material icon) {
        super(name, 0D, ModifierPriority.NEUTRAL);
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
        this.description = Utils.chat("&7Removes the treatment &e" + treatmentString + " &7from the item if present." +
                " -nThis can be used to apply conditions to recipes.");
        this.displayName = Utils.chat("&7&lRemove treatment: &e&l" + treatmentString);
        this.icon = icon;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        Collection<ItemTreatment> itemTreatments = SmithingItemTreatmentManager.getInstance().getItemsTreatments(outputItem);
        itemTreatments.remove(treatment);
        SmithingItemTreatmentManager.getInstance().setItemsTreatments(outputItem, itemTreatments);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Removes the &e" + treatmentString + " &7treatment from an item if present.");
    }
}
