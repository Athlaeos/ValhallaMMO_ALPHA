package me.athlaeos.valhallammo.items.enchantmentwrappers;

import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TradingEnchantment extends EnchantmentWrapper {
    private final String translation = Utils.chat(TranslationManager.getInstance().getTranslation("enchantment_trading"));
    private final String negativePrefix = TranslationManager.getInstance().getTranslation("enchantment_negative_prefix");
    private final String positivePrefix = TranslationManager.getInstance().getTranslation("enchantment_positive_prefix");

    public TradingEnchantment(double amount) {
        super(amount);
        this.attribute = "TRADING";
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
        double amplifier = amount;

        if (!translation.equals("")){
            String strength = ((amplifier < 0) ? "" : "+") + String.format("%d", (int) Math.floor(amplifier));
            String prefix = ((amplifier < 0) ? negativePrefix : positivePrefix);
            Utils.findAndReplaceLore(meta,
                    ChatColor.stripColor(Utils.chat(translation)),
                    String.format(prefix + "%s %s", translation, strength));
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
