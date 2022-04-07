package me.athlaeos.valhallammo.skills;

import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;

public interface EnchantmentApplicationSkill {
    void onEnchantItem(EnchantItemEvent event);

    void onPrepareEnchant(PrepareItemEnchantEvent event);

    void onAnvilUsage(PrepareAnvilEvent event);
}
