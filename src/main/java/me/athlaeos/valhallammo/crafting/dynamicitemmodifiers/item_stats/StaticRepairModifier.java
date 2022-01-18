package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats.DynamicItemModifier;
import me.athlaeos.valhallammo.managers.CustomDurabilityManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class StaticRepairModifier extends DynamicItemModifier {
    public StaticRepairModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 30;
        this.minStrength = 0;
        this.maxStrength = 100D;
        this.description = Utils.chat("&7Repairs the item's durabilty. The strength of the modifier " +
                "represents the % of the item's " +
                "durability to repair. Example: " +
                "A strength of 33% would repair a pickaxe with 1500 max durability by about 500 points.");
        this.displayName = Utils.chat("&7&lRepair item [STATIC]");
        this.icon = Material.ANVIL;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        if (!(meta instanceof Damageable)) return null;
        int itemDurabiity = CustomDurabilityManager.getInstance().getDurability(outputItem);
        if (itemDurabiity > 0){
            // Item has custom durability
            int maxDurability = CustomDurabilityManager.getInstance().getMaxDurability(outputItem);
            if (maxDurability <= CustomDurabilityManager.getInstance().getDurability(outputItem)) return null;
            int addDurability = (int) ((strength / 100D) * (double) maxDurability);
            PlayerItemDamageEvent event = new PlayerItemDamageEvent(crafter, outputItem, -addDurability);
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
            return outputItem;
        }
        return null;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Repairing the item by &e%s%%&7.", strength));
    }
}
