package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.potion_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.TransmutationManager;
import me.athlaeos.valhallammo.skills.alchemy.AlchemyProfile;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class PotionTransmutationAssignmentModifier extends DynamicItemModifier{

    public PotionTransmutationAssignmentModifier(String name) {
        super(name, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.POTION_MISC;

        this.bigStepDecrease = 0;
        this.bigStepIncrease = 0;
        this.smallStepDecrease = 0;
        this.smallStepIncrease = 0;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 0;
        this.description = Utils.chat("&7Assigns all the crafter's unlocked transmutations to the potion.");

        this.displayName = Utils.chat("&e&lElixir of Transmutation");
        this.icon = Material.GOLD_BLOCK;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("0");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        if (outputItem == null) return null;
        if (crafter == null) return null;
        if (crafter.hasPermission("valhalla.allrecipes")) {
            TransmutationManager.getInstance().setStoredTransmutations(outputItem, TransmutationManager.getInstance().getTransmutations().keySet());
            PotionEffectManager.renamePotion(outputItem, true);
            return outputItem;
        }
        Profile profile = ProfileManager.getManager().getProfile(crafter, "ALCHEMY");
        if (profile == null) return null;
        if (!(profile instanceof AlchemyProfile)) return null;

        TransmutationManager.getInstance().setStoredTransmutations(outputItem, ((AlchemyProfile) profile).getUnlockedTransmutations());
        PotionEffectManager.renamePotion(outputItem, true);

        return outputItem;
    }

    @Override
    public String toString() {
        return Utils.chat("&7Assigns all the crafter's unlocked transmutations to the potion");
    }
}
