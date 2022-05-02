package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.items.potioneffectwrappers.PotionEffectWrapper;
import me.athlaeos.valhallammo.managers.PotionAttributesManager;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class PotionLingeringModifier extends DynamicItemModifier {
    public PotionLingeringModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);

        this.name = name;
        this.category = ModifierCategory.POTION_STATS;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;

        this.description = Utils.chat("&7Converts the item into a lingering potion. Recipe is cancelled if item " +
                "already is a lingering potion. Lingering potions have their custom potion effect durations " +
                "quartered.");
        this.displayName = Utils.chat("&7&lLingering Potion");
        this.icon = Material.LINGERING_POTION;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;

        if (outputItem.getType() == Material.LINGERING_POTION) return null;
        outputItem.setType(Material.LINGERING_POTION);
        PotionEffectManager.renamePotion(outputItem, true);

        Collection<PotionEffectWrapper> potionEffects = PotionAttributesManager.getInstance().getCurrentStats(outputItem);
        if (!potionEffects.isEmpty()){
            for (PotionEffectWrapper potionEffect : potionEffects){
//                if (PotionEffectType.getByName(potionEffect.getPotionEffect()) == null) continue;
                potionEffect.setDuration((int) Math.floor(potionEffect.getDuration() / 4D));
            }
            PotionAttributesManager.getInstance().setStats(outputItem, potionEffects);
        }

        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Converts the item into a lingering potion. Recipe is cancelled if already a lingering potion.");
    }
}
