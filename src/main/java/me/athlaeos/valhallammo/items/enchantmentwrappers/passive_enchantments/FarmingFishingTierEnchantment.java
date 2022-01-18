package me.athlaeos.valhallammo.items.enchantmentwrappers.passive_enchantments;

import me.athlaeos.valhallammo.items.enchantmentwrappers.EnchantmentWrapper;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class FarmingExtraDropsEnchantment extends EnchantmentWrapper {
    private final String translation = Utils.chat(TranslationManager.getInstance().getTranslation("enchantment_farming_extra_drops"));
    private final String negativePrefix = TranslationManager.getInstance().getTranslation("enchantment_negative_prefix");
    private final String positivePrefix = TranslationManager.getInstance().getTranslation("enchantment_positive_prefix");

    public FarmingExtraDropsEnchantment(double amount) {
        super(amount);
        this.attribute = "FARMING_EXTRA_DROPS";
        this.minValue = -1000;
        this.maxValue = 1000;
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
        double bow_strength = amplifier;

        if (!translation.equals("")){
            String bowStrength = ((bow_strength < 0) ? "" : "+") + String.format("%d", (int) Math.floor(bow_strength*100));
            String prefix = ((bow_strength < 0) ? negativePrefix : positivePrefix);
            Utils.findAndReplaceLore(meta,
                    ChatColor.stripColor(Utils.chat(translation)),
                    String.format(prefix + "%s %s%%", translation, bowStrength));
        }
    }

    private void removeLore(ItemMeta meta){
        if (meta == null) return;
        if (!translation.equals("")){
            Utils.removeIfLoreContains(meta,
                    ChatColor.stripColor(Utils.chat(translation)));
        }
    }
}
