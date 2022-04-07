package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerExpChangeEvent;

public class PlayerExperienceAbsorbListener implements Listener {

    @EventHandler(priority= EventPriority.LOWEST)
    public void onPlayerAbsorb(PlayerExpChangeEvent e){
        if (e.getAmount() > 0){
            double expMultiplier = AccumulativeStatManager.getInstance().getStats("ENCHANTING_VANILLA_EXP_GAIN", e.getPlayer(), true);
            e.setAmount(Math.max(Utils.excessChance(e.getAmount() * expMultiplier), 0));
        }
    }
}
