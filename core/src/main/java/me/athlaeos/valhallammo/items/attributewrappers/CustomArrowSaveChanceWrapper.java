package me.athlaeos.valhallammo.items.attributewrappers;

import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomArrowSaveChanceWrapper extends AttributeWrapper{
    private final String arrowConsumeChanceTranslation = Utils.chat(TranslationManager.getInstance().getTranslation("translation_consumechance"));

    public CustomArrowSaveChanceWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        super(amount, operation, slot);
        this.attribute = "CUSTOM_ARROW_SAVE_CHANCE";
        this.minValue = -100;
        this.maxValue = 100;
    }

    @Override
    public boolean isCompatible(ItemStack i) {
        return i.getType() == Material.ARROW || i.getType() == Material.TIPPED_ARROW || i.getType() == Material.SPECTRAL_ARROW;
    }

    @Override
    public void onApply(ItemMeta i) {
        updateArrowAccuracy(i);
    }

    @Override
    public void onRemove(ItemMeta i) {
        removeLore(i);
    }

    private void updateArrowAccuracy(ItemMeta meta){
        if (meta == null) return;
        if (meta.getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES)) {
            removeLore(meta);
            return;
        }
        double consume_chance = -amount * 100;

        if (!arrowConsumeChanceTranslation.equals("")){
            String arrowConsumeChance = String.format((consume_chance >= 0 ? "+" : "") + "%.1f%%", consume_chance);
            String prefix = consume_chance < 0 ? TranslationManager.getInstance().getTranslation("stat_positive_prefix") : TranslationManager.getInstance().getTranslation("stat_negative_prefix");
            Utils.findAndReplaceLore(meta,
                    ChatColor.stripColor(Utils.chat(arrowConsumeChanceTranslation)),
                    String.format(prefix + "%s " + arrowConsumeChanceTranslation, arrowConsumeChance));
        }
    }

    private void removeLore(ItemMeta meta){
        if (meta == null) return;
        if (!arrowConsumeChanceTranslation.equals("")){
            Utils.removeIfLoreContains(meta,
                    ChatColor.stripColor(Utils.chat(arrowConsumeChanceTranslation)));
        }
    }
}
