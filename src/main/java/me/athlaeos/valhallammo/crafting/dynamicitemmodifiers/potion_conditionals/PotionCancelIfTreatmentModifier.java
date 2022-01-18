package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.ItemTreatment;
import me.athlaeos.valhallammo.managers.ItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CancelIfTreatmentModifier extends DynamicItemModifier {
    private final ItemTreatment treatment;
    private final String treatmentString;

    public CancelIfTreatmentModifier(String name, double strength, ModifierPriority priority, ItemTreatment treatment, Material icon) {
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
        this.description = Utils.chat("&7Cancels the crafting recipe if the item has &e" + treatmentString + "&7." +
                " -nThis can be used to enforce conditions to recipes.");
        this.displayName = Utils.chat("&7&lCancel if item has &e&l" + treatmentString);
        this.icon = icon;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        if (!this.use) return outputItem;
        if (ItemTreatmentManager.getInstance().hasTreatment(outputItem, treatment)) return null;
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Cancels the recipe if the item has the &e" + treatmentString + " &7treatment.");
    }
}
