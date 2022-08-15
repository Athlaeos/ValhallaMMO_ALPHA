package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.CustomDurabilityManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;

public class DynamicRepairModifier extends DynamicItemModifier {
    public DynamicRepairModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1000D;
        this.description = Utils.chat("&7Repairs the item's durabilty. The strength of the modifier " +
                "represents the % of the player's crafting skill points used in determining the fraction of the item's " +
                "durability restored. Example: " +
                "if a player has 150 crafting skill points, setting a strength of 50% will repair the " +
                "item as if the player had 75 crafting skill points, and 200% would represent 300.");
        this.displayName = Utils.chat("&7&lRepair item [SKILL BASED]");
        this.icon = Material.ANVIL;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<percentage_skill>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        Profile profile = ProfileManager.getManager().getProfile(crafter, "SMITHING");
        if (!(meta instanceof Damageable)) return null;
        if (profile == null) return null;
        MaterialClass materialClass = MaterialClass.getMatchingClass(outputItem);
        if (materialClass == null) return null;
        int itemDurabiity = CustomDurabilityManager.getInstance().getDurability(outputItem);
        if (itemDurabiity >= CustomDurabilityManager.getInstance().getMaxDurability(outputItem)) return null;
        double generalSkill = Math.round((strength/100D) * AccumulativeStatManager.getInstance().getEntityStatsIncludingCache("SMITHING_QUALITY_GENERAL", crafter, 2000, this.use));
        if (itemDurabiity > 0){
            // Item has custom durability
            int maxDurability = CustomDurabilityManager.getInstance().getMaxDurability(outputItem);
            double fractionToRepair = Utils.eval(SmithingItemTreatmentManager.getInstance().getRepairScaling().replace("%rating%", "" + generalSkill));
            int addDurability = (int) (fractionToRepair * (double) maxDurability);
            CustomDurabilityManager.getInstance().damageItem(outputItem, -addDurability);
        }
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Repairing the item with &e%s%%&7 efficiency to the player's crafting skill.", strength));
    }
}
