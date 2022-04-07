package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.managers.PartyManager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class ChatListener implements Listener {

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        PartyManager.getManager().onChatEvent(e);
    }

}
