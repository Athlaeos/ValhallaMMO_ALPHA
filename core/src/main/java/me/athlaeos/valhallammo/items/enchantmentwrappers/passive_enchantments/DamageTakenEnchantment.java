package me.athlaeos.valhallammo.items.enchantmentwrappers.passive_enchantments;

import me.athlaeos.valhallammo.items.enchantmentwrappers.EnchantmentWrapper;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class DamageTakenEnchantment extends EnchantmentWrapper {
    private final String translation = Utils.chat(TranslationManager.getInstance().getTranslation("enchantment_damage_taken"));

    public DamageTakenEnchantment(double amount) {
        super(amount);
        this.attribute = "DAMAGE_TAKEN";
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
        double taken = amplifier;

        if (!translation.equals("")){
            String bowStrength = ((taken > 0) ? "+" : "") + String.format("%d", (int) Math.round(taken * 100));
            String prefix = ((amplifier < 0) ? TranslationManager.getInstance().getTranslation("enchantment_positive_prefix") : TranslationManager.getInstance().getTranslation("enchantment_negative_prefix"));
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
