package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class PotionCancelIfPotionMaterialModifier extends DynamicItemModifier {
    private final Material requiredType;
    private final String typeString;

    public PotionCancelIfPotionMaterialModifier(String name, Material type, Material icon) {
        super(name, 0D, ModifierPriority.NEUTRAL);
        this.requiredType = type;
        typeString = Utils.toPascalCase(type.toString().replace("_", " "));

        this.name = name;

        this.category = ModifierCategory.POTION_CONDITIONALS;
        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Cancels the recipe if the potion material is &e" + typeString + " &7. " +
                "This can be used to add conditions to following recipes.");
        this.displayName = Utils.chat("&7&lCancel if Potion Material: &e&l" + typeString);
        this.icon = icon;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        if (outputItem.getItemMeta() == null) return null;

        if (outputItem.getType() == requiredType) return null;

        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Cancels the recipe is the potion type is &e" + typeString + "&7.");
    }
}
