package me.athlaeos.valhallammo.items.attributewrappers;

import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomBleedDurationWrapper extends AttributeWrapper{
    private final String displayLore = Utils.chat(TranslationManager.getInstance().getTranslation("translation_bleedduration"));

    public CustomBleedDurationWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        super(amount, operation, slot);
        this.attribute = "CUSTOM_BLEED_DURATION";
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
        if (meta.getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES)) {
            removeLore(meta);
            return;
        }
        int value = (int) amount;

        if (!displayLore.equals("")){
            String stringValue = String.format((value >= 0 ? "+" : "") + "%.1fs", (value / 1000D));
            String prefix = value >= 0 ? TranslationManager.getInstance().getTranslation("stat_positive_prefix") : TranslationManager.getInstance().getTranslation("stat_negative_prefix");
            Utils.findAndReplaceLore(meta,
                    ChatColor.stripColor(Utils.chat(displayLore)),
                    String.format(prefix + "%s " + displayLore, stringValue));
        }
    }

    private void removeLore(ItemMeta meta){
        if (meta == null) return;
        if (!displayLore.equals("")){
            Utils.removeIfLoreContains(meta,
                    ChatColor.stripColor(Utils.chat(displayLore)));
        }
    }
}
