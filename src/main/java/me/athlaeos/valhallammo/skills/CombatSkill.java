package me.athlaeos.valhallammo.skills;

import me.athlaeos.valhallammo.events.PlayerEnterCombatEvent;
import me.athlaeos.valhallammo.events.PlayerLeaveCombatEvent;
import org.bukkit.event.block.BlockExplodeEvent;

public interface CombatSkill {
    void onCombatEnter(PlayerEnterCombatEvent event);

    void onCombatLeave(PlayerLeaveCombatEvent event);
}
