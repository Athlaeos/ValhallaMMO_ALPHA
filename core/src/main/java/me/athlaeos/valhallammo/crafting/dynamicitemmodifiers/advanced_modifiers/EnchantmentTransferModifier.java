package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.advanced_modifiers;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.AdvancedDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentTransferModifier extends DuoArgDynamicItemModifier implements AdvancedDynamicItemModifier {
    public EnchantmentTransferModifier(String name) {
        super(name, 0D, 0D, ModifierPriority.NEUTRAL);

        this.name = name;
        this.category = ModifierCategory.ENCHANTING_MISC;

        this.bigStepDecrease = 1;
        this.bigStepIncrease = 1;
        this.smallStepDecrease = 1;
        this.smallStepIncrease = 1;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1;

        this.bigStepDecrease2 = 1;
        this.bigStepIncrease2 = 1;
        this.smallStepDecrease2 = 1;
        this.smallStepIncrease2 = 1;
        this.defaultStrength2 = 0;
        this.minStrength2 = 0;
        this.maxStrength2 = 1;

        this.description = Utils.chat("&7Transfers or copies all enchantments from this item " +
                "to the other item. If this item does not have any enchantments, the recipe is cancelled. -n" +
                "The recipe can be cancelled if the other item is already enchanted-n " +
                "The other item's enchantments can be overwritten or the recipe can be cancelled if the other item already has enchantments");
        this.displayName = Utils.chat("&7&lTransfer Enchantments");
        this.icon = Material.ANVIL;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Arrays.asList("0-Transfer", "1-Copy");
    }

    @Override
    public Pair<ItemStack, ItemStack> processItem(ItemStack i1, ItemStack i2, Player crafter, int timesExecuted) {
        Map<Enchantment, Integer> enchantmentsToPut = new HashMap<>();
        if (i1.getItemMeta() instanceof EnchantmentStorageMeta){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) i1.getItemMeta();
            if (meta.getStoredEnchants().isEmpty()) {
                return null;
            }
            if (strength2 == 0){
                if (i2.getItemMeta() instanceof EnchantmentStorageMeta){
                    if (!((EnchantmentStorageMeta) i2.getItemMeta()).getStoredEnchants().isEmpty()) {
                        return null;
                    }
                } else {
                    if (!i2.getEnchantments().isEmpty()) {
                        return null;
                    }
                }
            }
            enchantmentsToPut.putAll(meta.getStoredEnchants());
            if (strength == 0)
                for (Enchantment e : meta.getStoredEnchants().keySet())
                    meta.removeStoredEnchant(e);
            i1.setItemMeta(meta);
        } else {
            // if the 1st item has no enchantments, the recipe is cancelled
            if (i1.getEnchantments().isEmpty()) {
                return null;
            }
            if (strength2 == 0){
                // if the 2nd item already has enchantments the recipe is cancelled
                if (i2.getItemMeta() instanceof EnchantmentStorageMeta){
                    if (!((EnchantmentStorageMeta) i2.getItemMeta()).getStoredEnchants().isEmpty()) {
                        return null;
                    }
                } else {
                    if (!i2.getEnchantments().isEmpty()) {
                        return null;
                    }
                }
            }
            enchantmentsToPut.putAll(i1.getEnchantments());
            if (strength == 0)
                for (Enchantment e : i1.getEnchantments().keySet())
                    i1.removeEnchantment(e);
        }

        if (i2.getType() == Material.BOOK) i2.setType(Material.ENCHANTED_BOOK);
        if (i2.getItemMeta() instanceof EnchantmentStorageMeta){
            EnchantmentStorageMeta meta = (EnchantmentStorageMeta) i2.getItemMeta();
            for (Enchantment e : meta.getStoredEnchants().keySet())
                meta.removeStoredEnchant(e);

            for (Enchantment e : enchantmentsToPut.keySet()){
                int level = enchantmentsToPut.get(e);
                meta.addStoredEnchant(e, level, true);
            }
            i2.setItemMeta(meta);
        } else {
            for (Enchantment e : i2.getEnchantments().keySet())
                i2.removeEnchantment(e);

            i2.addEnchantments(enchantmentsToPut);
        }
        return new Pair<>(i1, i2);
    }

    @Override
    public List<String> tabAutoCompleteSecondArg() {
        return Arrays.asList("0-CancelIfEnchanted", "1-OverwriteIfEnchanted");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem, int timesExecuted) {
        return outputItem;
    }

    @Override
    public String toString() {
        if ((int) Math.round(strength) == 0){
            if ((int) Math.round(strength2) == 0){
                return Utils.chat("&7Transfers all enchantments from this item to the other, removing them from this item. If the other item already has enchantments, the recipe is cancelled");
            } else if ((int) Math.round(strength2) == 1){
                return Utils.chat("&7Transfers all enchantments from this item to the other, removing them from this item. If the other item already has enchantments, they are overwritten");
            }
        } else if ((int) Math.round(strength) == 1){
            if ((int) Math.round(strength2) == 0){
                return Utils.chat("&7Transfers all enchantments from this item to the other, keeping them on this item. If the other item already has enchantments, the recipe is cancelled");
            } else if ((int) Math.round(strength2) == 1){
                return Utils.chat("&7Transfers all enchantments from this item to the other, keeping them on this item. If the other item already has enchantments, they are overwritten");
            }
        }
        return Utils.chat("&cWhy do you keep hurting me in this way? Invalid option");
    }
}
