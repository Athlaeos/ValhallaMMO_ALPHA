package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.potioneffectwrappers.PotionEffectWrapper;
import me.athlaeos.valhallammo.managers.AlchemyPotionTreatmentManager;
import me.athlaeos.valhallammo.managers.PotionAttributesManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class DynamicPotionDurationModifier extends DuoArgDynamicItemModifier {
    public DynamicPotionDurationModifier(String name, double strength, double strength2, ModifierPriority priority) {
        super(name, strength, strength2, priority);

        this.name = name;
        this.category = ModifierCategory.POTION_MISC;

        this.bigStepDecrease = 10D;
        this.bigStepIncrease = 10D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1000D;

        this.bigStepDecrease2 = .25D;
        this.bigStepIncrease2 = .25D;
        this.smallStepDecrease2 = .01D;
        this.smallStepIncrease2 = .01D;
        this.defaultStrength2 = 0;
        this.minStrength2 = 0;
        this.maxStrength2 = Integer.MAX_VALUE;
        this.description = Utils.chat("&7Updates the potion's effect durations. The strength of the modifier " +
                "represents the % of the potion's quality rating used in determining its amplifier. Example: " +
                "if an item has a quality rating of 150, setting a strength of 50% will update the " +
                "potion's effect durations to if it had a rating of 75, and 200% results in a rating of 300. " +
                "The second strength determines the minimum value of the duration compared to its default. If this value " +
                "is 2.0 for example, a default duration of 15s cannot be improved to anything below 30s and will always be 30s" +
                " or higher. This is to make sure low skill levels can still benefit from potion amplification with redstone");
        this.displayName = Utils.chat("&c&lUpdate Potion Effect Durations");
        this.icon = Material.REDSTONE;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("percentage_quality");
    }
    @Override
    public List<String> tabAutoCompleteSecondArg() {
        return Collections.singletonList("minimum_multiplier");
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
            AlchemyPotionTreatmentManager.getInstance().applyPotionEffectScaling(outputItem, finalQuality, AlchemyPotionTreatmentManager.Type.DURATION, wrapper.getPotionEffect(), 0, strength2);
        }
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Setting the potion's duration strength to be &e%.1f%%&7 efficient with its quality score." +
                "This duration will at least be &e%.2fx&7 the potion's default duration", strength, strength2));
    }
}
