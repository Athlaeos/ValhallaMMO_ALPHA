package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.managers.CustomDurabilityManager;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.items.MaterialClass;
import me.athlaeos.valhallammo.managers.ItemTreatmentManager;
import me.athlaeos.valhallammo.skills.smithing.SmithingProfile;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerItemDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

public class DynamicRepairModifier extends DynamicItemModifier{
    public DynamicRepairModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;

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
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        Profile profile = ProfileUtil.getProfile(crafter, SkillType.SMITHING);
        if (!(meta instanceof Damageable)) return null;
        if (profile == null) return null;
        if (!(profile instanceof SmithingProfile)) return null;
        SmithingProfile smithingProfile = (SmithingProfile) profile;
        MaterialClass materialClass = MaterialClass.getMatchingClass(outputItem.getType());
        if (materialClass == null) return null;
        int itemDurabiity = CustomDurabilityManager.getInstance().getDurability(outputItem);
        if (itemDurabiity >= CustomDurabilityManager.getInstance().getMaxDurability(outputItem)) return null;
        int playerCraftingSkill = (int) Math.round((strength/100D) * (smithingProfile.getCraftingQuality(materialClass) + smithingProfile.getGeneralCraftingQuality()));
        if (itemDurabiity > 0){
            // Item has custom durability
            int maxDurability = CustomDurabilityManager.getInstance().getMaxDurability(outputItem);
            double fractionToRepair = Utils.eval(ItemTreatmentManager.getInstance().getRepairScaling().replace("%rating%", "" + playerCraftingSkill));
            int addDurability = (int) (fractionToRepair * (double) maxDurability);
            PlayerItemDamageEvent event = new PlayerItemDamageEvent(crafter, outputItem, -addDurability);
            ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
        }
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Repairing the item with &e%s%%&7 efficiency to the player's crafting skill.", strength));
    }
}
