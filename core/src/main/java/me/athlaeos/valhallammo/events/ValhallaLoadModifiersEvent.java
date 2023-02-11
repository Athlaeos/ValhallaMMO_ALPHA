package me.athlaeos.valhallammo.events;

import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.server.ServerEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;

public class ValhallaLoadModifiersEvent extends ServerEvent implements Cancellable {
    private static final HandlerList handlers = new HandlerList();
    private boolean cancelled = false;

    private final Collection<DynamicItemModifier> modifiersToRegister = new HashSet<>();

    public Collection<DynamicItemModifier> getModifiersToRegister() {
        return modifiersToRegister;
    }

    public void addModifierToRegister(DynamicItemModifier modifier){
        this.modifiersToRegister.add(modifier);
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList(){
        return handlers;
    }
}
