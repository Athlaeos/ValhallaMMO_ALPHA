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

public class CustomArrowSpeedWrapper extends AttributeWrapper{
    private final String arrowSpeedTranslation = Utils.chat(TranslationManager.getInstance().getTranslation("translation_arrowspeed"));

    public CustomArrowSpeedWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        super(amount, operation, slot);
        this.attribute = "CUSTOM_ARROW_SPEED";
        this.minValue = Short.MIN_VALUE;
        this.maxValue = Short.MAX_VALUE;
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
        double arrow_speed = amount * 100;

        if (!arrowSpeedTranslation.equals("")){
            String arrowSpeed = String.format((arrow_speed >= 0 ? "+" : "") + "%.1f%%", arrow_speed);
            String prefix = arrow_speed >= 0 ? TranslationManager.getInstance().getTranslation("stat_positive_prefix") : TranslationManager.getInstance().getTranslation("stat_negative_prefix");
            Utils.findAndReplaceLore(meta,
                    ChatColor.stripColor(Utils.chat(arrowSpeedTranslation)),
                    String.format(prefix + "%s " + arrowSpeedTranslation, arrowSpeed));
        }
    }

    private void removeLore(ItemMeta meta){
        if (meta == null) return;
        if (!arrowSpeedTranslation.equals("")){
            Utils.removeIfLoreContains(meta,
                    ChatColor.stripColor(Utils.chat(arrowSpeedTranslation)));
        }
    }
}
