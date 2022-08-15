package me.athlaeos.valhallammo.events;

import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class BlockDropItemStackEvent extends BlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final Player player;
    private boolean cancelled;
    private final BlockState blockState;
    private final List<ItemStack> items;

    /**
     * This event, once called, acts almost identical to the BlockDropItemEvent, except it takes in a list of ItemStacks
     * instead of a list of Item (entities). The event only has to be called and it then executed by the BlocksListener,
     * dropping the itemstacks. This event is to be used to handle manual dropping of items.
     * @param theBlock the block involved in this event
     * @param blockState the block state
     * @param player the player
     * @param items the items to be dropped
     */
    public BlockDropItemStackEvent(Block theBlock, BlockState blockState, Player player, List<ItemStack> items) {
        super(theBlock);
        this.player = player;
        this.blockState = blockState;
        this.items = items;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }
}
