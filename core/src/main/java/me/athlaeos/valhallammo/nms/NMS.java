package me.athlaeos.valhallammo.nms;

import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface NMS extends Listener {
    void createAttackSwingListener(Player p);

    void removeAttackSwingListener(Player p);

    void clearAttackSwingListeners();

    void setBookContents(ItemStack book, List<BaseComponent[]> pages);
}
