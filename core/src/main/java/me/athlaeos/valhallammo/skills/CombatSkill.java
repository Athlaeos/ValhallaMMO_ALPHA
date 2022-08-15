package me.athlaeos.valhallammo.skills;

import me.athlaeos.valhallammo.events.PlayerEnterCombatEvent;
import me.athlaeos.valhallammo.events.PlayerLeaveCombatEvent;
import me.athlaeos.valhallammo.events.ValhallaEntityStunEvent;
import me.athlaeos.valhallammo.events.ValhallaEntityCriticalHitEvent;

public interface CombatSkill {
    void onCombatEnter(PlayerEnterCombatEvent event);

    void onCombatLeave(PlayerLeaveCombatEvent event);

    void onEntityStun(ValhallaEntityStunEvent event);

    void onPlayerCriticalStrike(ValhallaEntityCriticalHitEvent event);
}
