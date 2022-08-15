package me.athlaeos.valhallammo.nms;

import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public interface NMS extends Listener {
    void createAttackSwingListener(Player p);

    void removeAttackSwingListener(Player p);

    void clearAttackSwingListeners();
}
