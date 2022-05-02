package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Party;
import me.athlaeos.valhallammo.managers.PartyManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.UUID;

public class ChatListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChat(AsyncPlayerChatEvent e){
        if (e.getMessage().startsWith("!")) {
            e.setMessage(e.getMessage().replaceFirst("!", ""));
            return;
        }
        Party party = PartyManager.getInstance().getParty(e.getPlayer());
        if (party != null){
            if (PartyManager.getInstance().getPlayersInPartyChat().contains(e.getPlayer().getUniqueId()) || e.getMessage().startsWith("pc:")){
                e.setMessage(e.getMessage().replaceFirst("pc:", ""));
                PartyManager.ErrorStatus status = PartyManager.getInstance().hasPermission(e.getPlayer(), "party_chat");
                if (status == null){
                    Party.PermissionGroup rank = party.getMembers().get(e.getPlayer().getUniqueId());
                    String title = party.getLeader().equals(e.getPlayer().getUniqueId()) ? PartyManager.getInstance().getLeaderTitle() : rank == null ? "" : rank.getTitle();
                    String messageFormat = Utils.chat(PartyManager.getInstance().getPartyChatFormat());
                    String newFormat = messageFormat
                            .replace("%rank%", Utils.chat(title));
                    e.setFormat(newFormat);

                    e.getRecipients().clear();
                    for (UUID member : party.getMembers().keySet()){
                        Player p = ValhallaMMO.getPlugin().getServer().getPlayer(member);
                        if (p != null){
                            e.getRecipients().add(p);
                        }
                    }
                } else {
                    status.sendErrorMessage(e.getPlayer());
                    e.setCancelled(true);
                }
            }
        } else if (e.getMessage().startsWith("pc:")){
            e.setCancelled(true);
            PartyManager.ErrorStatus.NOT_IN_PARTY.sendErrorMessage(e.getPlayer());
        }
    }
}
