package me.athlaeos.valhallammo.items.attributewrappers;

import me.athlaeos.valhallammo.configs.ConfigManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomBowStrengthWrapper extends AttributeWrapper{
    private String bowStrengthTranslation = Utils.chat(ConfigManager.getInstance().getConfig("skill_smithing.yml").get().getString("draw_strength"));;

    public CustomBowStrengthWrapper(double amount, AttributeModifier.Operation operation, EquipmentSlot slot) {
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
        updateBowStrength(i);
    }

    public void updateBowStrength(ItemMeta meta){
        if (meta == null) return;
        double bow_strength = amount;

        List<String> lore = meta.getLore();
        if (lore == null) lore = new ArrayList<>();
        int strengthLoreIndex = -1;
        for (String l : lore){
            if (l.contains(" ")){
                String[] splitString = l.split(" ");
                if (splitString.length >= 2){
                    String matchString = String.join(" ", Arrays.copyOfRange(splitString, 0, splitString.length - 1));
                    if (bowStrengthTranslation.equals(matchString)){
                        strengthLoreIndex = lore.indexOf(l);
                        break;
                    }
                }
            }
        }

        if (strengthLoreIndex != -1) {
            lore.remove(strengthLoreIndex);
        }
        if (!bowStrengthTranslation.equals("")){
            lore.add(Utils.chat(String.format(bowStrengthTranslation + " %.2f", bow_strength)));
        }
        meta.setLore(lore);
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
