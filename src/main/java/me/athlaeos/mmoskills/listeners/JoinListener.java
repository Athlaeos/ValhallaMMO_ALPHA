package me.athlaeos.mmoskills.listeners;

import me.athlaeos.mmoskills.skills.smithing.managers.ItemDamageManager;
import me.athlaeos.mmoskills.skills.smithing.managers.ItemDurabilityManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class JoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent e){
        ItemStack customDurabilityItem = new ItemStack(Material.DIAMOND_PICKAXE);
        ItemDurabilityManager.getInstance().setDurability(customDurabilityItem, 1000, 10000);
        e.getPlayer().getInventory().addItem(customDurabilityItem);

        ItemStack customDurabilityItem2 = new ItemStack(Material.GOLDEN_PICKAXE);
        ItemDurabilityManager.getInstance().setDurability(customDurabilityItem2, 3, 3);
        e.getPlayer().getInventory().addItem(customDurabilityItem2);

        ItemStack customDurabilityItem3 = new ItemStack(Material.DIAMOND_SWORD);
        ItemDamageManager.getInstance().setDamage(customDurabilityItem3, 5.5);
        e.getPlayer().getInventory().addItem(customDurabilityItem3);
    }
}
