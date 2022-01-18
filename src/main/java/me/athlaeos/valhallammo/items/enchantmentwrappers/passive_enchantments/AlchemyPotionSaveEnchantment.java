package me.athlaeos.valhallammo.items.enchantmentwrappers;

import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AlchemyPotionSaveEnchantment extends EnchantmentWrapper {
    private final String ingredientSaveTranslation = Utils.chat(TranslationManager.getInstance().getTranslation("enchantment_alchemy_potion_save"));
    private final String negativePrefix = TranslationManager.getInstance().getTranslation("enchantment_negative_prefix");
    private final String positivePrefix = TranslationManager.getInstance().getTranslation("enchantment_positive_prefix");

    public AlchemyPotionSaveEnchantment(double amount) {
        super(amount);
        this.attribute = "ALCHEMY_POTION_SAVE";
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
        double bow_strength = amount;

        if (!ingredientSaveTranslation.equals("")){
            String bowStrength = ((bow_strength < 0) ? "" : "+") + String.format("%d", (int) Math.floor(bow_strength));
            String prefix = ((bow_strength < 0) ? negativePrefix : positivePrefix);
            Utils.findAndReplaceLore(meta,
                    ChatColor.stripColor(Utils.chat(ingredientSaveTranslation)),
                    String.format(prefix + "%s %s%%", ingredientSaveTranslation, bowStrength));
        }
    }

    private void removeLore(ItemMeta meta){
        if (meta == null) return;
        if (!ingredientSaveTranslation.equals("")){
            Utils.removeIfLoreContains(meta,
                    ChatColor.stripColor(Utils.chat(ingredientSaveTranslation)));
        }
    }
}
