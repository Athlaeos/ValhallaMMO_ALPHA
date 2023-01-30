package me.athlaeos.valhallammo.events;

import me.athlaeos.valhallammo.crafting.recipetypes.DynamicCauldronRecipe;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.jetbrains.annotations.NotNull;

public class CauldronCompleteRecipeEvent extends BlockEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled;

    private final Player crafter;
    private final DynamicCauldronRecipe recipe;

    public CauldronCompleteRecipeEvent(@NotNull Block theBlock, DynamicCauldronRecipe recipe, Player thrower) {
        super(theBlock);
        this.recipe = recipe;
        this.crafter = thrower;
    }

    public Player getCrafter() {
        return crafter;
    }

    public DynamicCauldronRecipe getRecipe() {
        return recipe;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.cancelled = cancel;
    }
}
