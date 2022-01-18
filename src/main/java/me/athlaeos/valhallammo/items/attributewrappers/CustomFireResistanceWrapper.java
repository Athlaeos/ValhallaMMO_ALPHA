package me.athlaeos.valhallammo.items.attributewrappers;

import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class CustomProjectileResistanceWrapper extends AttributeWrapper{
    private final String translation_projectile_resistance = Utils.chat(TranslationManager.getInstance().getTranslation("translation_projectile_resistance"));
    private final String negativePrefix = TranslationManager.getInstance().getTranslation("stat_negative_prefix");
    private final String positivePrefix = TranslationManager.getInstance().getTranslation("stat_positive_prefix");

    public CustomProjectileResistanceWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
        super(amount, operation, slot);
        this.attribute = "CUSTOM_PROJECTILE_RESISTANCE";
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
        double strength = amount;
        if (strength == 0) return;

        if (!translation_projectile_resistance.equals("")){
            String strengthPart = String.format("%d", (int) Math.floor(strength * 100));
            String prefix = ((strength < 0) ? negativePrefix : positivePrefix);
            Utils.findAndReplaceLore(meta,
                    ChatColor.stripColor(Utils.chat(translation_projectile_resistance)),
                    String.format(prefix + "%s%% " + translation_projectile_resistance, strengthPart));
        }
    }

    private void removeLore(ItemMeta meta){
        if (meta == null) return;
        if (!translation_projectile_resistance.equals("")){
            Utils.removeIfLoreContains(meta,
                    ChatColor.stripColor(Utils.chat(translation_projectile_resistance)));
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
