package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers;

import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AttributeRemoveAttackSpeedModifier extends DynamicItemModifier{
    public AttributeRemoveAttackSpeedModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Removes &eAttack Speed &7as a default attribute from the item if present. ");
        this.displayName = Utils.chat("&7&lRemove Stat: &e&lAttack Speed");
        this.icon = Material.STONE_SWORD;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        ItemAttributesManager.getInstance().removeDefaultStat(outputItem, "GENERIC_ATTACK_SPEED");
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Removes &eattack speed&7 from the item if present.");
    }
}
