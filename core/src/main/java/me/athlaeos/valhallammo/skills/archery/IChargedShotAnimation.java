package me.athlaeos.valhallammo.skills.archery;

import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;

public interface IChargedShotAnimation {
    void onShoot(EntityShootBowEvent e);

    void onActivate(Player p);

    void onExpire(Player p);
}
