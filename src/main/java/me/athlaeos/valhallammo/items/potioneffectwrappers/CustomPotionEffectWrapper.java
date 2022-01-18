package me.athlaeos.valhallammo.items.potioneffectwrappers;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

public class VanillaPotionEffectWrapper extends PotionEffectWrapper{
    public VanillaPotionEffectWrapper(String potionEffect, double amplifier, int duration) {
        super(potionEffect, amplifier, duration);
    }

    @Override
    public void onApply(ItemMeta potion) {

    }

    @Override
    public void onRemove(ItemMeta potion) {

    }

    @Override
    public boolean isCompatible(ItemStack i) {
        return i.getItemMeta() instanceof PotionMeta;
    }
}
