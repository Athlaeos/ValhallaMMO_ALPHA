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
import java.util.Collections;
import java.util.List;

public class PotionRemoveTreatmentModifier extends DynamicItemModifier {
    private final PotionTreatment treatment;
    private final String treatmentString;

    public PotionRemoveTreatmentModifier(String name, double strength, ModifierPriority priority, PotionTreatment treatment, Material icon) {
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
        this.description = Utils.chat("&7Removes the treatment &e" + treatmentString + " &7from the potion if present." +
                " -nThis can be used to apply conditions to recipes.");
        this.displayName = Utils.chat("&7&lRemove treatment: &e&l" + treatmentString);
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
        Collection<PotionTreatment> itemTreatments = AlchemyPotionTreatmentManager.getInstance().getPotionTreatments(outputItem);
        itemTreatments.remove(treatment);
        AlchemyPotionTreatmentManager.getInstance().setPotionTreatments(outputItem, itemTreatments);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Removes the &e" + treatmentString + " &7treatment from a potion if present.");
    }
}
