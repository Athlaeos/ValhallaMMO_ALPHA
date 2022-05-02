package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.enchantment_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.EnchantingItemEnchantmentsManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class VanillaEnchantAddModifier extends DuoArgDynamicItemModifier {
    private final Enchantment enchantment;
    private final String enchantName;

    public VanillaEnchantAddModifier(String name, Material icon, Enchantment enchantment, int max) {
        super(name, 0, 0, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ENCHANTING_STATS;

        this.bigStepDecrease = 1;
        this.bigStepIncrease = 1;
        this.smallStepDecrease = 1;
        this.smallStepIncrease = 1;
        this.defaultStrength = 1;
        this.minStrength = 0;
        this.maxStrength = max;

        this.bigStepDecrease2 = 10;
        this.bigStepIncrease2 = 10;
        this.smallStepDecrease2 = 1;
        this.smallStepIncrease2 = 1;
        this.defaultStrength2 = 0;
        this.minStrength2 = -1;
        this.maxStrength2 = 100000;

        this.enchantment = enchantment;
        this.enchantName = enchantment.getKey().getKey();
        this.description = Utils.chat("&7Adds the &e" + Utils.toPascalCase(enchantName.replace("_", " ")) + " &7enchantment to the item. " +
                "Enchantment is cancelled if item already has this enchantment. If strength is 0, enchantment is " +
                "removed instead. And if the item does not have the enchantment you want removed, the recipe is cancelled." +
                " Enchantment scales with a percentage of the player's skill, unless this percentage is -1.");
        this.displayName = Utils.chat("&7&lAdd Enchantment: &b&l" + Utils.toPascalCase(enchantName.replace("_", " ")));
        this.icon = icon;
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta meta = outputItem.getItemMeta();
        if (meta == null) return null;
        boolean remove = strength == 0;
        if (remove){
            outputItem.removeEnchantment(enchantment);
        } else {
            if (outputItem.getEnchantments().containsKey(enchantment)) return null;
            if (strength2 > 0){
                if (crafter == null) return null;
                int enchantingSkill = (int) ((strength2 / 100) * Math.floor(AccumulativeStatManager.getInstance().getStats("ENCHANTING_QUALITY_GENERAL", crafter, this.use) + AccumulativeStatManager.getInstance().getStats("ENCHANTING_QUALITY_VANILLA", crafter, this.use)));
                Map<Enchantment, Integer> enchant = EnchantingItemEnchantmentsManager.getInstance().applyEnchantmentScaling(outputItem, enchantingSkill, enchantment, (int) Math.floor(strength));
                Enchantment enchantment = null;
                int level = 0;
                for (Enchantment e : enchant.keySet()){
                    enchantment = e;
                    level = enchant.get(e);
                    break;
                }
                if (enchantment != null){
                    outputItem.addUnsafeEnchantment(enchantment, level);
                } else {
                    return null;
                }
            } else {
                outputItem.addUnsafeEnchantment(enchantment, (int) Math.floor(strength));
            }
        }
        return outputItem;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Collections.singletonList("<enchantment_strength>");
    }

    @Override
    public List<String> tabAutoCompleteSecondArg() {
        return Collections.singletonList("<percentage_skill>");
    }

    @Override
    public String toString() {
        if (strength == 0){
            return Utils.chat(String.format("&7Removes &e%s&7 from the item. Recipe is cancelled if item already has this enchantment.", Utils.toPascalCase(enchantName.replace("_", " "))));
        } else {
            if (strength2 >= 0){
                return Utils.chat(String.format("&7Gives the item &e%s %s&7 scaling with &e%,.1f%% &7of the player's skill. Recipe is cancelled if item already has this enchantment.", Utils.toPascalCase(enchantName.replace("_", " ")), Utils.toRoman((int) strength), strength2));
            } else {
                return Utils.chat(String.format("&7Gives the item &e%s %s&7. Recipe is cancelled if item already has this enchantment.", Utils.toPascalCase(enchantName.replace("_", " ")), Utils.toRoman((int) strength)));
            }
        }
    }
}
