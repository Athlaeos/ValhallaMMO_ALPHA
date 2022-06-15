package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.InvisibleIfIncompatibleModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class WeaponIdRequirementModifier extends DynamicItemModifier implements InvisibleIfIncompatibleModifier {
    public WeaponIdRequirementModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_CONDITIONALS;

        this.bigStepDecrease = 3D;
        this.bigStepIncrease = 3D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = -1;
        this.maxStrength = 9999999;
        this.description = Utils.chat("&7Requires the item to have a specific Weapon ID. " +
                "If &e-1 &7is selected, the recipe will be cancelled if the item has any weapon id");
        this.displayName = Utils.chat("&7&lRequire Weapon ID");
        this.icon = Material.TRIDENT;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<model_data>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        if (strength < 0){
            if (SmithingItemTreatmentManager.getInstance().getWeaponId(outputItem) >= 0) return null;
        } else {
            if (SmithingItemTreatmentManager.getInstance().getWeaponId(outputItem) != (int) strength) return null;
        }
        return outputItem;
    }

    @Override
    public String toString() {
        if (strength < 0){
            return Utils.chat("&7Requires the item to have &eno&7 Weapon ID");
        } else {
            return Utils.chat("&7Requires the item to have &e" + ((int) strength) + "&7 as Weapon ID");
        }
    }
}
