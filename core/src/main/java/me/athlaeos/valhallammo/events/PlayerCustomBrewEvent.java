package me.athlaeos.valhallammo.events;

import me.athlaeos.valhallammo.crafting.recipetypes.DynamicBrewingRecipe;
import org.bukkit.block.BrewingStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerCustomBrewEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private boolean cancelled = false;
    private final Player brewer;
    private final DynamicBrewingRecipe recipe;
    private final BrewingStand stand;
    private final boolean success;

    public PlayerCustomBrewEvent(Player brewer, DynamicBrewingRecipe recipe, BrewingStand stand, boolean success){
        this.brewer = brewer;
        this.recipe = recipe;
        this.stand = stand;
        this.success = success;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }

    public Player getBrewer() {
        return brewer;
    }

    public DynamicBrewingRecipe getRecipe() {
        return recipe;
    }

    public BrewingStand getStand() {
        return stand;
    }

    /**
     * Returns if the recipe went successfully or not. If false, one of the dynamic item modifiers failed and returned null.
     * If true, recipe was successful and result was modified;
     * @return true if successful, false otherwise.
     */
    public boolean isSuccessful() {
        return success;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }
}
