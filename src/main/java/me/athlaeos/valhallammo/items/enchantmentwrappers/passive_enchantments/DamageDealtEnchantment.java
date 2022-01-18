package me.athlaeos.valhallammo.items.enchantmentwrappers.passive_enchantments;

import me.athlaeos.valhallammo.items.enchantmentwrappers.EnchantmentWrapper;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class ArcheryDamageEnchantment extends EnchantmentWrapper {
    private final String rangedDamageTranslation = Utils.chat(TranslationManager.getInstance().getTranslation("enchantment_archery_damage"));
    private final String negativePrefix = TranslationManager.getInstance().getTranslation("enchantment_negative_prefix");
    private final String positivePrefix = TranslationManager.getInstance().getTranslation("enchantment_positive_prefix");

    public ArcheryDamageEnchantment(double amount) {
        super(amount);
        this.attribute = "ARCHERY_DAMAGE";
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
        double bow_strength = amount;

        if (!rangedDamageTranslation.equals("")){
            String bowStrength = ((bow_strength < 0) ? "" : "+") + String.format("%d", (int) Math.floor(bow_strength));
            String prefix = ((bow_strength < 0) ? negativePrefix : positivePrefix);
            Utils.findAndReplaceLore(meta,
                    ChatColor.stripColor(Utils.chat(rangedDamageTranslation)),
                    String.format(prefix + "%s %s%%", rangedDamageTranslation, bowStrength));
        }
    }

    private void removeLore(ItemMeta meta){
        if (meta == null) return;
        if (!rangedDamageTranslation.equals("")){
            Utils.removeIfLoreContains(meta,
                    ChatColor.stripColor(Utils.chat(rangedDamageTranslation)));
        }
    }
}
