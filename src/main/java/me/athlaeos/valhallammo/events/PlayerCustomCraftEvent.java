package me.athlaeos.valhallammo.skills.smithing.events;

import me.athlaeos.valhallammo.skills.smithing.recipes.dom.ItemCraftingRecipe;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.bukkit.inventory.ItemStack;

public class PlayerCustomCraftEvent extends Event {
    private static final HandlerList HANDLER_LIST = new HandlerList();
    private final Player player;
    private ItemCraftingRecipe recipe;
    private ItemStack result;
    private boolean applyDynamicModifiers = true;

    public PlayerCustomCraftEvent(Player player, ItemCraftingRecipe recipe, ItemStack result) {
        this.player = player;
        this.recipe = recipe;
        this.result = result;
    }

    public PlayerCustomCraftEvent(Player player, ItemCraftingRecipe recipe, ItemStack result, boolean applyDynamicModifiers) {
        this.player = player;
        this.recipe = recipe;
        this.result = result;
        this.applyDynamicModifiers = applyDynamicModifiers;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLER_LIST;
    }

    public Player getPlayer() {
        return player;
    }

    public ItemCraftingRecipe getRecipe() {
        return recipe;
    }

    public void setRecipe(ItemCraftingRecipe recipe) {
        this.recipe = recipe;
    }

    public void setResult(ItemStack result) {
        this.result = result;
    }

    public ItemStack getResult() {
        return result;
    }

    public boolean applyDynamicModifiers() {
        return applyDynamicModifiers;
    }

    public void setApplyDynamicModifiers(boolean applyDynamicModifiers) {
        this.applyDynamicModifiers = applyDynamicModifiers;
    }
}
