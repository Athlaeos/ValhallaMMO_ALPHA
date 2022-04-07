package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers;

import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public enum ModifierCategory {
    ENCHANTING_STATS(Utils.createItemStack(Material.ENCHANTED_BOOK, Utils.chat("&b&lEnchantments"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with vanilla enchantments and"),
                    Utils.chat("&7custom enchantments.")
            ))),
    ENCHANTING_CONDITIONALS(Utils.createItemStack(Material.ENCHANTED_BOOK, Utils.chat("&b&lEnchantment Conditionals"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with enchantment conditions")
            ))),
    POTION_STATS(Utils.createItemStack(Material.SPLASH_POTION, Utils.chat("&5&lPotion Stats"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with a custom potion's stats and"),
                    Utils.chat("&7quality.")
            ))),
    POTION_CONDITIONALS(Utils.createItemStack(Material.POTION, Utils.chat("&5&lPotion Conditionals"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with a potion's treatments and"),
                    Utils.chat("&7conditional modifiers.")
            ))),
    ITEM_STATS(Utils.createItemStack(Material.IRON_CHESTPLATE, Utils.chat("&5&lItem Stats"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with an item's attributes,"),
                    Utils.chat("&7stats, and quality.")
            ))),
    ITEM_CONDITIONALS(Utils.createItemStack(Material.GOLDEN_CHESTPLATE, Utils.chat("&5&lItem Conditionals"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with an item's treatments and"),
                    Utils.chat("&7conditional modifiers.")
            ))),
    EXPERIENCE(Utils.createItemStack(Material.EXPERIENCE_BOTTLE, Utils.chat("&5&lExperience Modifiers"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with an additional exp rewards"),
                    Utils.chat("&7and costs.")
            ))),
    OTHER(Utils.createItemStack(Material.EXPERIENCE_BOTTLE, Utils.chat("&5&lOther"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers without"),
                    Utils.chat("&7a specific category.")
            ))),
    ALL(Utils.createItemStack(Material.NETHER_STAR, Utils.chat("&5&lAll"),
            Collections.singletonList(
                    Utils.chat("&7All modifiers.")
            )));

    private final ItemStack icon;
    ModifierCategory(ItemStack icon){
        this.icon = icon;
    }

    public ItemStack getIcon() {
        return icon;
    }
}
