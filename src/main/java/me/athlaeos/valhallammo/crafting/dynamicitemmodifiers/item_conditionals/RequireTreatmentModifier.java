package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_conditionals;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.items.ItemTreatment;
import me.athlaeos.valhallammo.managers.ItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class RequirementWaxCoatingModifier extends DynamicItemModifier {
    public RequirementWaxCoatingModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_CONDITIONALS;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Requires the treatment &eWax Coating &7to craft." +
                " -nThis can be used to enforce conditions to recipes.");
        this.displayName = Utils.chat("&7&lRequire treatment: &e&lWax Coating");
        this.icon = Material.HONEYCOMB;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        if (!this.use) return outputItem;
        if (!ItemTreatmentManager.getInstance().hasTreatment(outputItem, ItemTreatment.WAX_COATING)) {
            return null;
        }
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Requires the item to have the &eWax Coating &7treatment to craft.");
    }
}
