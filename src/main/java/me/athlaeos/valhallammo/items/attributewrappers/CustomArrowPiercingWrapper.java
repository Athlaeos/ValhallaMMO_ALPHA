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

public class CustomArrowPiercingWrapper extends AttributeWrapper{
    private final String arrowPiercingTranslation = Utils.chat(TranslationManager.getInstance().getTranslation("translation_arrowpiercing"));

    public CustomArrowPiercingWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        super(amount, operation, slot);
        this.attribute = "CUSTOM_ARROW_PIERCING";
        this.minValue = 0;
        this.maxValue = Short.MAX_VALUE;
    }

    @Override
    public boolean isCompatible(ItemStack i) {
        return i.getType() == Material.ARROW || i.getType() == Material.TIPPED_ARROW || i.getType() == Material.SPECTRAL_ARROW;
    }

    @Override
    public void onApply(ItemMeta i) {
        updateArrowPiercing(i);
    }

    @Override
    public void onRemove(ItemMeta i) {
        removeLore(i);
    }

    private void updateArrowPiercing(ItemMeta meta){
        if (meta == null) return;
        if (meta.getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES)) {
            removeLore(meta);
            return;
        }
        int arrow_piercing = (int) amount;

        if (!arrowPiercingTranslation.equals("")){
            String arrowStrength = String.format((arrow_piercing >= 1 ? "+" : "") + "%d", arrow_piercing);
            String prefix = TranslationManager.getInstance().getTranslation("stat_positive_prefix");
            Utils.findAndReplaceLore(meta,
                    ChatColor.stripColor(Utils.chat(arrowPiercingTranslation)),
                    String.format(prefix + "%s " + arrowPiercingTranslation, arrowStrength));
        }
    }

    private void removeLore(ItemMeta meta){
        if (meta == null) return;
        if (!arrowPiercingTranslation.equals("")){
            Utils.removeIfLoreContains(meta,
                    ChatColor.stripColor(Utils.chat(arrowPiercingTranslation)));
        }
    }
}
