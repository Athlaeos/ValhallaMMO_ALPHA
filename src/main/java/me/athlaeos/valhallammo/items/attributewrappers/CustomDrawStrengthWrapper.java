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

public class CustomDrawStrengthWrapper extends AttributeWrapper{
    private final String bowStrengthTranslation = Utils.chat(TranslationManager.getInstance().getTranslation("translation_drawstrength"));

    public CustomDrawStrengthWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        super(amount, operation, slot);
        this.attribute = "CUSTOM_DRAW_STRENGTH";
        this.minValue = 0;
        this.maxValue = 16;
    }

    @Override
    public boolean isCompatible(ItemStack i) {
        return i.getType() == Material.BOW || i.getType() == Material.CROSSBOW;
    }

    @Override
    public void onApply(ItemMeta i) {
        updateBowStrength(i);
    }

    @Override
    public void onRemove(ItemMeta i) {
        removeLore(i);
    }

    private void updateBowStrength(ItemMeta meta){
        if (meta == null) return;
        if (meta.getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES)) {
            removeLore(meta);
            return;
        }
        double bow_strength = amount;
        if (bow_strength < 0) bow_strength = 0;

        if (!bowStrengthTranslation.equals("")){
            String bowStrength = String.format("%d", (int) Math.floor(bow_strength * 100));
            String prefix = ((bow_strength < 1) ? TranslationManager.getInstance().getTranslation("stat_negative_prefix") : TranslationManager.getInstance().getTranslation("stat_positive_prefix"));
            Utils.findAndReplaceLore(meta,
                    ChatColor.stripColor(Utils.chat(bowStrengthTranslation)),
                    String.format(prefix + "%s%% " + bowStrengthTranslation, bowStrength));
        }
    }

    private void removeLore(ItemMeta meta){
        if (meta == null) return;
        if (!bowStrengthTranslation.equals("")){
            Utils.removeIfLoreContains(meta,
                    ChatColor.stripColor(Utils.chat(bowStrengthTranslation)));
        }
    }

    /*
    public double getBowStrength(ItemStack item){
        if (item == null) return 1;
        if (item.getItemMeta() == null) return 1;
        ItemMeta meta = item.getItemMeta();
        if (meta.getPersistentDataContainer().has(key_bow_drawstrength, PersistentDataType.DOUBLE)){
            double strength = meta.getPersistentDataContainer().get(key_bow_drawstrength, PersistentDataType.DOUBLE);
            if (strength < 0) strength = 0;
            return strength;
        } else {
            return 1;
        }
    }

    public void setBowStrength(ItemStack item, double multiplier){
        if (item == null) return;
        if (item.getItemMeta() == null) return;
        if (!(item.getType() == Material.BOW || item.getType() == Material.CROSSBOW)) return;
        ItemMeta meta = item.getItemMeta();
        if (multiplier < 0) multiplier = 0;

        meta.getPersistentDataContainer().set(key_bow_drawstrength, PersistentDataType.DOUBLE, multiplier);
        item.setItemMeta(meta);
        updateBowStrength(item);
    }
    */
}
