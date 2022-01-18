package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats.DynamicItemModifier;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class UpgradeGoldModifier extends DynamicItemModifier {
    public UpgradeGoldModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;

        this.bigStepDecrease = 0D;
        this.bigStepIncrease = 0D;
        this.smallStepDecrease = 0D;
        this.smallStepIncrease = 0D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Changes the item's type to &eGold");
        this.displayName = Utils.chat("&7&lTransform to Gold");
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
                case CHESTPLATE: outputItem.setType(Material.GOLDEN_CHESTPLATE);
                return outputItem;
                case LEGGINGS: outputItem.setType(Material.GOLDEN_LEGGINGS);
                return outputItem;
                case HELMET: outputItem.setType(Material.GOLDEN_HELMET);
                return outputItem;
                case BOOTS: outputItem.setType(Material.GOLDEN_BOOTS);
                return outputItem;
                case SWORD: outputItem.setType(Material.GOLDEN_SWORD);
                return outputItem;
                case PICKAXE: outputItem.setType(Material.GOLDEN_PICKAXE);
                return outputItem;
                case SHOVEL: outputItem.setType(Material.GOLDEN_SHOVEL);
                return outputItem;
                case HOE: outputItem.setType(Material.GOLDEN_HOE);
                return outputItem;
                case AXE: outputItem.setType(Material.GOLDEN_AXE);
                return outputItem;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Changing the item's type to &eGold&7 if possible.");
    }
}
