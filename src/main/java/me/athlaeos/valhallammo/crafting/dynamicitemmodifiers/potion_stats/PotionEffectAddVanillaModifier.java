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
import org.bukkit.potion.PotionEffectType;

public class PotionEffectAddVanillaModifier extends DuoArgDynamicItemModifier {
    private PotionEffectType effect;

    public PotionEffectAddVanillaModifier(String name, double duration_ticks, double amplifier, ModifierPriority priority, PotionEffectType effect, Material icon) {
        super(name, duration_ticks, amplifier, priority);
        this.effect = effect;

        this.name = name;
        this.category = ModifierCategory.POTION_STATS;

        this.bigStepDecrease = 300;
        this.bigStepIncrease = 300;
        this.smallStepDecrease = 20;
        this.smallStepIncrease = 20;
        this.defaultStrength = 600;
        this.minStrength = 20;
        this.maxStrength = Integer.MAX_VALUE;

        this.bigStepDecrease2 = 3;
        this.bigStepIncrease2 = 3;
        this.smallStepDecrease2 = 1;
        this.smallStepIncrease2 = 1;
        this.defaultStrength2 = 1;
        this.minStrength2 = 0;
        this.maxStrength2 = 1000;

        this.description = Utils.chat("&7Adds &e" + effect.getName() + " &7as a default potion effect to the potion/tipped arrow. " +
                "-nThe recipe is cancelled if the item already has this potion effect. Can only be applied on potions and tipped arrows.");
        this.displayName = Utils.chat("&7&lAdd Base Effect: &e&l" + effect.getName());
        this.icon = icon;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (!(outputItem.getItemMeta() instanceof PotionMeta)) return null;
        PotionMeta meta = (PotionMeta) outputItem.getItemMeta();
        if (meta == null) return null;
        if (meta.hasCustomEffect(effect)) return null;
        if (effect == PotionEffectType.HEAL || effect == PotionEffectType.HARM || effect == PotionEffectType.SATURATION){
            strength = 1;
        }
        PotionAttributesManager.getInstance().addDefaultStat(outputItem, new VanillaPotionEffectWrapper(effect.getName(), strength2, (int) Math.floor(strength)));
        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat(String.format("&7Gives the potion &e%s %s for %s&7. Recipe is cancelled if item already has this potion effect.", effect.getName(), Utils.toRoman(1 + (int) Math.floor(strength2)), Utils.toTimeStamp((int) Math.floor(strength), 20)));
    }
}
