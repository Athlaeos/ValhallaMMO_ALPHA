package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.potioneffectwrappers.VanillaPotionEffectWrapper;
import me.athlaeos.valhallammo.managers.PotionAttributesManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Collections;
import java.util.List;

public class PotionEffectAddCustomModifier extends DuoArgDynamicItemModifier {
    private final String effect;
    private int multiplyBy = 1;
    private String format = "%.1f";

    public PotionEffectAddCustomModifier(String name, String effect, Material icon) {
        super(name, 0D, 0D, ModifierPriority.NEUTRAL);
        this.effect = effect;

        this.name = name;
        this.category = ModifierCategory.POTION_CUSTOM;

        this.bigStepDecrease = 15000;
        this.bigStepIncrease = 15000;
        this.smallStepDecrease = 1000;
        this.smallStepIncrease = 1000;
        this.defaultStrength = 30000;
        this.minStrength = 1000;
        this.maxStrength = Integer.MAX_VALUE;

        this.bigStepDecrease2 = 10;
        this.bigStepIncrease2 = 10;
        this.smallStepDecrease2 = 1;
        this.smallStepIncrease2 = 1;
        this.defaultStrength2 = 1;
        this.minStrength2 = 0;
        this.maxStrength2 = 1000;

        this.description = Utils.chat("&7Adds &e" + effect + " &7as a default potion effect to the potion/tipped arrow. " +
                "-nThe recipe is cancelled if the item already has this potion effect. Can only be applied on potions and tipped arrows.");
        this.displayName = Utils.chat("&7&lAdd Base Effect: &e&l" + effect);
        this.icon = icon;
    }

    public PotionEffectAddCustomModifier(String name, String effect, Material icon, double smallStep, double bigStep, String format, int multiplyBy) {
        super(name, 0D, 0D, ModifierPriority.NEUTRAL);
        this.effect = effect;
        this.multiplyBy = multiplyBy;
        this.format = format;

        this.name = name;
        this.category = ModifierCategory.POTION_CUSTOM;

        this.bigStepDecrease = 15000;
        this.bigStepIncrease = 15000;
        this.smallStepDecrease = 1000;
        this.smallStepIncrease = 1000;
        this.defaultStrength = 30000;
        this.minStrength = 1000;
        this.maxStrength = Integer.MAX_VALUE;

        this.bigStepDecrease2 = bigStep;
        this.bigStepIncrease2 = bigStep;
        this.smallStepDecrease2 = smallStep;
        this.smallStepIncrease2 = smallStep;
        this.defaultStrength2 = 0;
        this.minStrength2 = 0;
        this.maxStrength2 = Integer.MAX_VALUE;

        this.description = Utils.chat("&7Adds &e" + effect + " &7as a default potion effect to the potion/tipped arrow. " +
                "-nThe recipe is cancelled if the item already has this potion effect. Can only be applied on potions and tipped arrows.");
        this.displayName = Utils.chat("&7&lAdd Base Effect: &e&l" + effect);
        this.icon = icon;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<duration_ms>");
    }

    @Override
    public List<String> tabAutoCompleteSecondArg() {
        return Collections.singletonList("<amplifier_base>");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        if (!(outputItem.getItemMeta() instanceof PotionMeta)) return null;
        PotionMeta meta = (PotionMeta) outputItem.getItemMeta();
        if (meta == null) return null;
        if (PotionAttributesManager.getInstance().getCurrentStats(outputItem).stream().anyMatch(potionEffectWrapper -> potionEffectWrapper.getPotionEffect().equals(effect))) return null;
        PotionAttributesManager.getInstance().addDefaultStat(outputItem, new VanillaPotionEffectWrapper(effect, strength2, (int) Math.floor(strength)));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Gives the potion &e%s %s for %s&7. Recipe is cancelled if item already has this potion effect.", effect, String.format(format, strength2 * multiplyBy), Utils.toTimeStamp((int) Math.floor(strength), 1000)));
    }
}
