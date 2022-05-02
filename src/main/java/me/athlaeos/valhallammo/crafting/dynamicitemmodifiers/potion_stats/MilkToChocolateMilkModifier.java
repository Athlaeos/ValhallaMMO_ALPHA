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
import org.bukkit.inventory.meta.PotionMeta;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MilkToChocolateMilkModifier extends DynamicItemModifier {
    public MilkToChocolateMilkModifier(String name) {
        super(name, 0, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.POTION_STATS;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;

        this.description = Utils.chat("&7turn milk to choco milk.");
        this.displayName = Utils.chat("&7&lchocolate milk");
        this.icon = Material.COCOA_BEANS;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("666");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        if (!(outputItem.getItemMeta() instanceof PotionMeta)) return null;
        PotionMeta meta = (PotionMeta) outputItem.getItemMeta();
        if (meta == null) return null;
        Collection<PotionEffectWrapper> defaultPotionEffects = PotionAttributesManager.getInstance().getDefaultPotionEffects(outputItem);
        defaultPotionEffects.removeIf(potionEffectWrapper -> potionEffectWrapper.getPotionEffect().equals("MILK"));

        Collection<PotionEffectWrapper> currentPotionEffects = PotionAttributesManager.getInstance().getCurrentStats(outputItem);
        currentPotionEffects.removeIf(potionEffectWrapper -> potionEffectWrapper.getPotionEffect().equals("MILK"));

        PotionEffectWrapper chocolateMilkWrapper = PotionAttributesManager.getInstance().getRegisteredPotionEffects().get("CHOCOLATE_MILK");
        if (chocolateMilkWrapper != null){
            try {
                chocolateMilkWrapper = chocolateMilkWrapper.clone();
            } catch (CloneNotSupportedException ignored){
                return null;
            }

            defaultPotionEffects.add(chocolateMilkWrapper);
            currentPotionEffects.add(chocolateMilkWrapper);
        } else {
            return null;
        }
        PotionAttributesManager.getInstance().setDefaultPotionEffects(outputItem, currentPotionEffects);
        PotionAttributesManager.getInstance().setStats(outputItem, currentPotionEffects);
        PotionEffectManager.renamePotion(outputItem, true);

        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7make chocolate milk");
    }
}
