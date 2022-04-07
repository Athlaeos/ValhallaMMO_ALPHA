package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.PotionTreatment;
import me.athlaeos.valhallammo.managers.AlchemyPotionTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collection;

public class PotionAddTreatmentModifier extends DynamicItemModifier {
    private final PotionTreatment treatment;
    private final String treatmentString;

    public PotionAddTreatmentModifier(String name, double strength, ModifierPriority priority, PotionTreatment treatment, Material icon) {
        super(name, strength, priority);
        this.treatment = treatment;
        treatmentString = Utils.toPascalCase(treatment.toString().replace("_", " "));

        this.name = name;

        this.category = ModifierCategory.POTION_CONDITIONALS;
        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Applies the treatment &e" + treatmentString + " &7to the potion. -nThe recipe is cancelled if" +
                " the item already has this treatment. This can be used to add" +
                " conditions to following recipes.");
        this.displayName = Utils.chat("&7&lApply treatment: &e&l" + treatmentString);
        this.icon = icon;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        if (!this.validate) return outputItem;
        if (AlchemyPotionTreatmentManager.getInstance().hasTreatment(outputItem, treatment)) return null;
        Collection<PotionTreatment> itemTreatments = AlchemyPotionTreatmentManager.getInstance().getPotionTreatments(outputItem);
        itemTreatments.add(treatment);
        AlchemyPotionTreatmentManager.getInstance().setPotionTreatments(outputItem, itemTreatments);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Adds the &e" + treatmentString + " &7treatment to a potion. Recipe is cancelled if potion already has this property.");
    }
}
