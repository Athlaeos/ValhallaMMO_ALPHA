package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ItemTreatmentManager;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.skills.SkillType;
import me.athlaeos.valhallammo.skills.smithing.SmithingProfile;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class StaticQualityRatingModifier extends DynamicItemModifier implements Cloneable{

    public StaticQualityRatingModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1000D;
        this.description = Utils.chat("&7Updates the item's quality rating. The strength of the modifier " +
                "represents the amount of quality points attributed to the item. This is unaffected by " +
                "potion effects or perks boosting the player's crafting skill.");
        this.displayName = Utils.chat("&b&lUpdate Quality : STATIC");
        this.icon = Material.NETHER_STAR;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (crafter == null) return null;
        Profile profile = ProfileUtil.getProfile(crafter, SkillType.SMITHING);
        if (profile == null) return null;
        if (!(profile instanceof SmithingProfile)) return null;
        ItemTreatmentManager.getInstance().setItemsQuality(outputItem, (int) strength);
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the item's quality rating to be &e%s%%&7 efficient with the player's crafting skill.", strength));
    }
}
