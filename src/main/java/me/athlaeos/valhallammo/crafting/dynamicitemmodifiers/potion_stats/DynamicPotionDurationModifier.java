package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.potioneffectwrappers.PotionEffectWrapper;
import me.athlaeos.valhallammo.managers.PotionAttributesManager;
import me.athlaeos.valhallammo.managers.AlchemyPotionTreatmentManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DynamicPotionDurationModifier extends DynamicItemModifier {
    public DynamicPotionDurationModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.POTION_STATS;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1000D;
        this.description = Utils.chat("&7Updates the potion's effect durations. The strength of the modifier " +
                "represents the % of the potion's quality rating used in determining its amplifier. Example: " +
                "if an item has a quality rating of 150, setting a strength of 50% will update the " +
                "potion's effect durations to if it had a rating of 75, and 200% results in a rating of 300.");
        this.displayName = Utils.chat("&c&lUpdate Potion Effect Durations");
        this.icon = Material.REDSTONE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("percentage_quality");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (!(outputItem.getItemMeta() instanceof PotionMeta)) return null;
        PotionMeta meta = (PotionMeta) outputItem.getItemMeta();
        if (meta == null) return null;
        int quality = AlchemyPotionTreatmentManager.getInstance().getPotionQuality(outputItem);
        int finalQuality = (int) Math.round((strength / 100D) * quality);

        Collection<PotionEffectWrapper> wrappers = PotionAttributesManager.getInstance().getCurrentStats(outputItem);
        for (PotionEffectWrapper wrapper : wrappers){
            AlchemyPotionTreatmentManager.getInstance().applyPotionEffectScaling(outputItem, finalQuality, AlchemyPotionTreatmentManager.Type.DURATION, wrapper.getPotionEffect());
        }
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the potion's duration strength to be &e%.1f%%&7 efficient with its quality score.", strength));
    }
}
