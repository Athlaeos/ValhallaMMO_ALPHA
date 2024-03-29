package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.CooldownManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class PlayerExperienceAbsorbListener implements Listener {

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerAbsorb(PlayerExpChangeEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getPlayer().getWorld().getName())) return;
        if (e.getAmount() > 0){
            if (CooldownManager.getInstance().isCooldownPassed(e.getPlayer().getUniqueId(), "valhalla_enchanting_vanilla_exp_multiplier_prevention")){
                double expMultiplier = AccumulativeStatManager.getInstance().getStats("ENCHANTING_VANILLA_EXP_GAIN", e.getPlayer(), true);
                e.setAmount(Math.max(Utils.excessChance(e.getAmount() * expMultiplier), 0));
            }
        }
    }
}
