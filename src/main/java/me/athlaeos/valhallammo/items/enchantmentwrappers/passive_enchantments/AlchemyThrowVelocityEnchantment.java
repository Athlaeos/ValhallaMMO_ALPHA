package me.athlaeos.valhallammo.items.enchantmentwrappers.passive_enchantments;

import me.athlaeos.valhallammo.items.enchantmentwrappers.EnchantmentWrapper;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class AlchemyThrowVelocityEnchantment extends EnchantmentWrapper {
    private final String ingredientSaveTranslation = Utils.chat(TranslationManager.getInstance().getTranslation("enchantment_alchemy_throw_velocity"));

    public AlchemyThrowVelocityEnchantment(double amount) {
        super(amount);
        this.attribute = "ALCHEMY_THROW_VELOCITY";
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

        if (!ingredientSaveTranslation.equals("")){
            String bowStrength = ((bow_strength < 0) ? "" : "+") + String.format("%d", (int) Math.floor(bow_strength*100));
            String prefix = ((amplifier < 0) ? TranslationManager.getInstance().getTranslation("enchantment_negative_prefix") : TranslationManager.getInstance().getTranslation("enchantment_positive_prefix"));
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
