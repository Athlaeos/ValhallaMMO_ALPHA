package me.athlaeos.valhallammo.items.enchantmentwrappers.passive_enchantments;

import me.athlaeos.valhallammo.items.enchantmentwrappers.EnchantmentWrapper;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class BleedResistanceEnchantment extends EnchantmentWrapper {
    private final String display = Utils.chat(TranslationManager.getInstance().getTranslation("enchantment_bleedresistance"));

    public BleedResistanceEnchantment(double amount) {
        super(amount);
        this.attribute = "BLEED_RESISTANCE";
        this.minValue = Integer.MIN_VALUE;
        this.maxValue = Integer.MAX_VALUE;
    }

    @Override
    public boolean isCompatible(ItemStack i) {
        return true;
    }

    @Override
    public void onApply(ItemMeta i) {
        updateLore(i);
    }

    @Override
    public void onRemove(ItemMeta i) {
        removeLore(i);
    }

    private void updateLore(ItemMeta meta){
        if (meta == null) return;
        if (meta.getItemFlags().contains(ItemFlag.HIDE_ENCHANTS)) {
            removeLore(meta);
            return;
        }
        double strength = amplifier * 100;

        if (!display.equals("")){
            String displayStrength = ((strength < 0) ? "" : "+") + String.format("%.1f%%", strength);
            String prefix = ((amplifier < 0) ? TranslationManager.getInstance().getTranslation("enchantment_negative_prefix") : TranslationManager.getInstance().getTranslation("enchantment_positive_prefix"));
            Utils.findAndReplaceLore(meta,
                    ChatColor.stripColor(Utils.chat(display)),
                    String.format(prefix + "%s %s", display, displayStrength));
        }
    }

    private void removeLore(ItemMeta meta){
        if (meta == null) return;
        if (!display.equals("")){
            Utils.removeIfLoreContains(meta,
                    ChatColor.stripColor(Utils.chat(display)));
        }
    }
}
