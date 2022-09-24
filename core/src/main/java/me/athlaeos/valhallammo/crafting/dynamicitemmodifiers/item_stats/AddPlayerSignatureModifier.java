package me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.item_stats;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierCategory;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddPlayerSignatureModifier extends DynamicItemModifier implements Cloneable{
    private final String format_signature;

    public AddPlayerSignatureModifier(String name, double strength, ModifierPriority priority) {
        super(name, strength, priority);
        this.format_signature = TranslationManager.getInstance().getTranslation("format_signature");

        this.name = name;
        this.category = ModifierCategory.ITEM_STATS_MISC;

        this.bigStepDecrease = 1D;
        this.bigStepIncrease = 1D;
        this.smallStepDecrease = 1D;
        this.smallStepIncrease = 1D;
        this.defaultStrength = 0;
        this.minStrength = 0;
        this.maxStrength = 1;
        this.description = Utils.chat("&7Adds a signature of the crafter to the item's lore. ");
        this.displayName = Utils.chat("&7&lPlayer Signature");
        this.icon = Material.WRITTEN_BOOK;
    }

    @Override
    public List<String> tabAutoCompleteFirstArg() {
        return Arrays.asList("0-signatureonfirstline", "1-signatureonlastline");
    }

    @Override
    public ItemStack processItem(Player crafter, ItemStack outputItem) {
        if (outputItem == null) return null;
        ItemMeta outputMeta = outputItem.getItemMeta();
        if (outputMeta == null) return null;
        List<String> lore = outputMeta.hasLore() ? outputMeta.getLore() : new ArrayList<>();
        if (lore == null) lore = new ArrayList<>();
        if (lore.isEmpty()){
            lore.add(Utils.chat(format_signature.replace("%player%", crafter.getName())));
        } else {
            if (strength > 0){
                lore.add(Utils.chat(format_signature.replace("%player%", crafter.getName())));
            } else {
                lore.set(0, Utils.chat(format_signature.replace("%player%", crafter.getName())));
            }
        }
        outputMeta.setLore(lore);
        outputItem.setItemMeta(outputMeta);
        return outputItem;
    }

    @Override
    public String toString() {
        if (strength > 0){
            return Utils.chat("&7Adds a player signature on the last line of lore");
        } else {
            return Utils.chat("&7Adds a player signature on the first line of lore");
        }
    }
}
