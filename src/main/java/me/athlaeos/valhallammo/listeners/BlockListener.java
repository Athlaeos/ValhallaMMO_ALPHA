package me.athlaeos.valhallammo.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;

public class BlockBreakListener implements Listener {

    @EventHandler
    public void onBlockBreak(BlockBreakEvent e){

    }

    @EventHandler
    public void onBlockDropItems(BlockDropItemEvent e){
        ItemStack testStack = new ItemStack(Material.BARRIER);
        Item testItem = e.getBlock().getWorld().dropItem(e.getBlock().getLocation(), testStack);
        try {
            e.getItems().add(testItem);
        } catch (UnsupportedOperationException ex){
            System.out.println("sorry buddy, not allowed to add items :(");
        }
    }
}
