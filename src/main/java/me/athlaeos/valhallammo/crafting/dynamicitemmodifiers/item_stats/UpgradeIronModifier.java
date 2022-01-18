package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats.DynamicItemModifier;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class UpgradeIronModifier extends DynamicItemModifier {
    public UpgradeIronModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;

        this.bigStepDecrease = 0D;
        this.bigStepIncrease = 0D;
        this.smallStepDecrease = 0D;
        this.smallStepIncrease = 0D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Changes the item's type to &eIron");
        this.displayName = Utils.chat("&7&lTransform to Iron");
        this.icon = Material.SMITHING_TABLE;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        if (!(meta instanceof Damageable)) return null;
        EquipmentClass equipmentClass = EquipmentClass.getClass(outputItem.getType());
        if (equipmentClass != null){
            switch (equipmentClass){
                case CHESTPLATE: outputItem.setType(Material.IRON_CHESTPLATE);
                return outputItem;
                case LEGGINGS: outputItem.setType(Material.IRON_LEGGINGS);
                return outputItem;
                case HELMET: outputItem.setType(Material.IRON_HELMET);
                return outputItem;
                case BOOTS: outputItem.setType(Material.IRON_BOOTS);
                return outputItem;
                case SWORD: outputItem.setType(Material.IRON_SWORD);
                return outputItem;
                case PICKAXE: outputItem.setType(Material.IRON_PICKAXE);
                return outputItem;
                case SHOVEL: outputItem.setType(Material.IRON_SHOVEL);
                return outputItem;
                case HOE: outputItem.setType(Material.IRON_HOE);
                return outputItem;
                case AXE: outputItem.setType(Material.IRON_AXE);
                return outputItem;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Changing the item's type to &eIron&7 if possible.");
    }
}
