package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionType;

import java.util.Collections;
import java.util.List;

public class PotionRequirePotionTypeModifier extends DynamicItemModifier {
    private final PotionType type;
    private final String typeString;

    public PotionRequirePotionTypeModifier(String name, PotionType type, Material icon) {
        super(name, 0D, ModifierPriority.NEUTRAL);
        this.type = type;
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
        this.description = Utils.chat("&7Requires the potion type to be &e" + typeString + " &7. -nThe recipe is cancelled if" +
                " the potion does not have this type. This can be used to add" +
                " conditions to following recipes.");
        this.displayName = Utils.chat("&7&lRequire Potion Type: &e&l" + typeString);
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
        if (!(outputItem.getItemMeta() instanceof PotionMeta)) return null;
        if (!this.validate) return outputItem;
        PotionMeta meta = (PotionMeta) outputItem.getItemMeta();
        if (meta.getBasePotionData().getType() != type) return null;

        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Requires the potion type to be &e" + typeString + "&7. Recipe is cancelled if potion does not have this property.");
    }
}
