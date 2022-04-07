package me.athlaeos.valhallammo.events;

import me.athlaeos.valhallammo.crafting.recipetypes.ItemImprovementRecipe;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerItemTinkerEvent extends Event implements Cancellable {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;
    private ItemImprovementRecipe recipe;
    private boolean applyDynamicModifiers = true;
    private boolean isCancelled;
    private final Block craftingStation;

    public PlayerItemTinkerEvent(Player player, ItemImprovementRecipe recipe, Block craftingStation) {
        this.player = player;
        this.recipe = recipe;
        this.craftingStation = craftingStation;
    }

    public PlayerItemTinkerEvent(Player player, ItemImprovementRecipe recipe, Block craftingStation, boolean applyDynamicModifiers) {
        this.player = player;
        this.recipe = recipe;
        this.craftingStation = craftingStation;
        this.applyDynamicModifiers = applyDynamicModifiers;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public static HandlerList getHandlerList() {
        return HANDLER_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemImprovementRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(ItemImprovementRecipe recipe) {
        this.recipe = recipe;
    }

    public boolean applyDynamicModifiers() {
        return applyDynamicModifiers;
    }

    public void setApplyDynamicModifiers(boolean applyDynamicModifiers) {
        this.applyDynamicModifiers = applyDynamicModifiers;
    }

    public Block getCraftingStation() {
        return craftingStation;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        this.isCancelled = cancel;
    }
}
