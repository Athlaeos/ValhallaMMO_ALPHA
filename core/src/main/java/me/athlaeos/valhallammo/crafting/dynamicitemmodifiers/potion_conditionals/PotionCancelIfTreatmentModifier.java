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

import java.util.Collections;
import java.util.List;

public class PotionCancelIfTreatmentModifier extends DynamicItemModifier {
    private final PotionTreatment treatment;
    private final String treatmentString;

    public PotionCancelIfTreatmentModifier(String name, PotionTreatment treatment, Material icon) {
        super(name, 0D, ModifierPriority.NEUTRAL);
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
        this.description = Utils.chat("&7Cancels the crafting recipe if the item has &e" + treatmentString + "&7." +
                " -nThis can be used to enforce conditions to recipes.");
        this.displayName = Utils.chat("&7&lCancel if item has &e&l" + treatmentString);
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
        if (!this.validate) return outputItem;
        if (AlchemyPotionTreatmentManager.getInstance().hasTreatment(outputItem, treatment)) return null;
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Cancels the recipe if the potion has the &e" + treatmentString + " &7treatment.");
    }
}
