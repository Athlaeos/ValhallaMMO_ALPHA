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

public class CustomArrowDamageWrapper extends AttributeWrapper{
    private final String arrowStrengthTranslation = Utils.chat(TranslationManager.getInstance().getTranslation("translation_arrowstrength"));

    public CustomArrowDamageWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        super(amount, operation, slot);
        this.attribute = "CUSTOM_ARROW_DAMAGE";
        this.minValue = 0;
        this.maxValue = Short.MAX_VALUE;
    }

    @Override
    public boolean isCompatible(ItemStack i) {
        return i.getType() == Material.ARROW || i.getType() == Material.TIPPED_ARROW || i.getType() == Material.SPECTRAL_ARROW;
    }

    @Override
    public void onApply(ItemMeta i) {
        updateArrowStrength(i);
    }

    @Override
    public void onRemove(ItemMeta i) {
        removeLore(i);
    }

    private void updateArrowStrength(ItemMeta meta){
        if (meta == null) return;
        if (meta.getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES)) {
            removeLore(meta);
            return;
        }
        double arrow_strength = amount;
        if (arrow_strength < 0) arrow_strength = 0;

        if (!arrowStrengthTranslation.equals("")){
            String arrowStrength = String.format("%.1f", arrow_strength);
            String prefix = TranslationManager.getInstance().getTranslation("stat_positive_prefix");
            Utils.findAndReplaceLore(meta,
                    ChatColor.stripColor(Utils.chat(arrowStrengthTranslation)),
                    String.format(prefix + "%s " + arrowStrengthTranslation, arrowStrength));
        }
    }

    private void removeLore(ItemMeta meta){
        if (meta == null) return;
        if (!arrowStrengthTranslation.equals("")){
            Utils.removeIfLoreContains(meta,
                    ChatColor.stripColor(Utils.chat(arrowStrengthTranslation)));
        }
    }
}
