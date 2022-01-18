package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionType;

public class PotionSetPotionTypeModifier extends DynamicItemModifier {
    private final PotionType type;
    private final String typeString;

    public PotionSetPotionTypeModifier(String name, double strength, ModifierPriority priority, PotionType type, Material icon) {
        super(name, strength, priority);
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
        this.description = Utils.chat("&7Sets the potion type to &e" + typeString + " &7. -nThe recipe is cancelled if" +
                " the potion already has this type. This can be used to add" +
                " conditions to following recipes.");
        this.displayName = Utils.chat("&7&lSet Potion Type: &e&l" + typeString);
        this.icon = icon;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (outputItem.getItemMeta() == null) return null;
        if (!(outputItem.getItemMeta() instanceof PotionMeta)) return null;
        if (!this.use) return outputItem;
        PotionMeta meta = (PotionMeta) outputItem.getItemMeta();
        if (meta.getBasePotionData().getType() == type) return null;

        meta.setBasePotionData(new PotionData(type, meta.getBasePotionData().isExtended(), meta.getBasePotionData().isUpgraded()));
        outputItem.setItemMeta(meta);

        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Sets the potion type to &e" + typeString + "&7. Recipe is cancelled if potion already has this property.");
    }
}
