package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class PotionSplashModifier extends DynamicItemModifier {
    public PotionSplashModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.POTION_STATS;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;

        this.description = Utils.chat("&7Converts the item into a splash potion. Recipe is cancelled if item " +
                "already is a splash potion");
        this.displayName = Utils.chat("&7&lSplash Potion");
        this.icon = Material.SPLASH_POTION;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;

        if (outputItem.getType() == Material.SPLASH_POTION) return null;
        outputItem.setType(Material.SPLASH_POTION);

        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Converts the item into a splash potion. Recipe is cancelled if already a splash potion.");
    }
}
