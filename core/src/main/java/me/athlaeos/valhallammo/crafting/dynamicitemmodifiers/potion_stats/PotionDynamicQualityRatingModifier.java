package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.AlchemyPotionTreatmentManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class PotionDynamicQualityRatingModifier extends DynamicItemModifier implements Cloneable{
    private final PotionType type;

    public PotionDynamicQualityRatingModifier(String name, double strength, ModifierPriority priority, PotionType type) {
        super(name, strength, priority);

        this.type = type;
        this.name = name;
        this.category = ModifierCategory.POTION_MISC;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1000D;
        if (type != null){
            this.description = Utils.chat("&7Updates the potion's quality rating assuming the potion is a &e" + type + "&7. " +
                    "The strength of the modifier represents the % of the player's alchemy skill points used in determining " +
                    "the item's new quality. Example: if a player has 150 alchemy skill points, setting a strength of 50% " +
                    "will update the item's quality rating to a quality rating of 75, and 200% results in a rating of 300.");
        } else {
            this.description = Utils.chat("&7Updates the potion's quality rating without any debuff/buff potion benefits. " +
                    "The strength of the modifier represents the % of the player's alchemy skill points used in determining " +
                    "the item's new quality. Example: if a player has 150 alchemy skill points, setting a strength of 50% " +
                    "will update the item's quality rating to a quality rating of 75, and 200% results in a rating of 300.");
        }
        this.displayName = Utils.chat("&b&lUpdate Quality : DYNAMIC");
        this.icon = Material.POTION;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("percentage_skill");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (crafter == null) return null;
        Profile profile = ProfileManager.getManager().getProfile(crafter, "ALCHEMY");
        if (profile == null) return null;
        if (!(profile instanceof AlchemyProfile)) return null;
        double generalSkill = AccumulativeStatManager.getInstance().getStats("ALCHEMY_QUALITY_GENERAL", crafter, this.validate);
        double specificSkill = 0;
        if (type != null){
            specificSkill = AccumulativeStatManager.getInstance().getStats("ALCHEMY_QUALITY_" + type, crafter, this.validate);
        }

        AlchemyPotionTreatmentManager.getInstance().setPotionQuality(outputItem, (int) (specificSkill + generalSkill));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the potion's %s quality rating to be &e%.1f%%&7 efficient with the player's alchemy skill.", (type == null) ? "GENERAL" : type, strength));
    }
}
