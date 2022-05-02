package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Party;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.managers.PartyManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class PlayerEXPShareListener implements Listener {

    public PlayerEXPShareListener(ValhallaMMO plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerGainEXP(PlayerSkillExperienceGainEvent e){
        if (e.getReason() != PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION) return;
        Party party = PartyManager.getInstance().getParty(e.getPlayer());
        if (party == null) return;
        if (!party.isEnabledEXPSharing()) return;
        Collection<Player> nearbyMembers = new HashSet<>();
        for (UUID member : party.getMembers().keySet()){
            Player p = ValhallaMMO.getPlugin().getServer().getPlayer(member);
            if (p == null) continue;
            if (p.getWorld().equals(e.getPlayer().getWorld())) {
                if (Utils.quickWithinRange(e.getPlayer().getLocation(), p.getLocation(), party.getEXPSharingRadius())) {
                    nearbyMembers.add(p);
                }
            }
        }

        double expFraction = e.getAmount() / nearbyMembers.size();
        expFraction *= party.getEXPSharingMultiplier();
        e.setCancelled(true);

        for (Player p : nearbyMembers){
            e.getLeveledSkill().addEXP(p, expFraction, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.EXP_SHARE);
        }
    }
}
