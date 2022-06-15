package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class WeaponIdAddModifier extends DynamicItemModifier {
    public WeaponIdAddModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 3D;
        this.bigStepIncrease = 3D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = -1;
        this.maxStrength = 9999999;
        this.description = Utils.chat("&7Sets the item's weapon ID to a specific value. This can later be used " +
                "to add conditions to tinkering recipes, and should be used in creating custom weapon types. If this value" +
                " is -1, the weapon id is removed instead");
        this.displayName = Utils.chat("&7&lSet Weapon Type");
        this.icon = Material.TRIDENT;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<id>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        SmithingItemTreatmentManager.getInstance().setWeaponId(outputItem, (int) strength);
        return outputItem;
    }

    @Override
    public String toString() {
        if (strength < 0){
            return Utils.chat("&7Removing weapon id");
        } else {
            return Utils.chat("&7Setting the item's weapon to &e" + ((int) strength) + "&7.");
        }
    }
}
