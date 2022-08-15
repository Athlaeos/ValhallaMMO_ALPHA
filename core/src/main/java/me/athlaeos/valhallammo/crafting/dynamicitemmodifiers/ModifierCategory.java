package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers;

import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.Collections;

public enum ModifierCategory {
    CUSTOM_ENCHANTMENTS(Utils.createItemStack(Material.WRITABLE_BOOK, Utils.chat("&b&lCustom Enchantments"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with custom enchantments.")
            ))),
    VANILLA_ENCHANTMENTS(Utils.createItemStack(Material.ENCHANTED_BOOK, Utils.chat("&b&lVanilla Enchantments"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with vanilla enchantments.")
            ))),
    ENCHANTING_MISC(Utils.createItemStack(Material.BOOK, Utils.chat("&b&lEnchantment Misc."),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers that modify"),
                    Utils.chat("&7items in other enchantment-related ways")
            ))),
    ENCHANTING_CONDITIONALS(Utils.createItemStack(Material.KNOWLEDGE_BOOK, Utils.chat("&b&lEnchantment Conditionals"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with enchantment conditions")
            ))),
    POTION_CUSTOM(Utils.createItemStack(Material.LINGERING_POTION, Utils.chat("&5&lCustom Potion Effects"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with custom potion effects")
            ))),
    POTION_VANILLA(Utils.createItemStack(Material.SPLASH_POTION, Utils.chat("&5&lVanilla Potion Effects"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with vanilla potion effects")
            ))),
    POTION_MISC(Utils.createItemStack(Material.SPLASH_POTION, Utils.chat("&5&lPotion Misc."),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers that modify"),
                    Utils.chat("&7items in other potion-related ways")
            ))),
    POTION_CONDITIONALS(Utils.createItemStack(Material.POTION, Utils.chat("&5&lPotion Conditionals"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with a potion's treatments and"),
                    Utils.chat("&7conditional modifiers.")
            ))),
    ITEM_STATS_VANILLA(Utils.createItemStack(Material.IRON_CHESTPLATE, Utils.chat("&5&lVanilla Attributes"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with an item's vanilla attributes.")
            ))),
    ITEM_STATS_CUSTOM(Utils.createItemStack(Material.IRON_CHESTPLATE, Utils.chat("&5&lCustom Attributes"),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers to do"),
                    Utils.chat("&7with an item's custom attributes.")
            ))),
    ITEM_STATS_MISC(Utils.createItemStack(Material.IRON_CHESTPLATE, Utils.chat("&5&lItem Misc."),
            Arrays.asList(
                    Utils.chat("&7Contains all modifiers that modify"),
                    Utils.chat("&7items in other potion-related ways")
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
