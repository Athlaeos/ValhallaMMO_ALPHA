package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.managers.ProfileVersionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.managers.TutorialBook;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class JoinListener implements Listener {
    private boolean book_on_join;
    private String error_command_givebook;
    private static JoinListener listener;

    public JoinListener(){
        listener = this;
        book_on_join = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("book_on_join");
        error_command_givebook = TranslationManager.getInstance().getTranslation("error_command_givebook");
    }

    public void reload(){
        book_on_join = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("book_on_join");
        error_command_givebook = TranslationManager.getInstance().getTranslation("error_command_givebook");
    }

    public static JoinListener getListener() {
        return listener;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent e){
        if (!e.getPlayer().hasPlayedBefore()){
            if (book_on_join){
                ItemStack book = TutorialBook.getTutorialBookInstance().getBook();
                if (book != null){
                    e.getPlayer().getInventory().addItem(book.clone());
                } else {
                    ValhallaMMO.getPlugin().getLogger().warning(ChatColor.stripColor(Utils.chat(error_command_givebook)));
                }
            }
        }
        ProfileVersionManager.getInstance().checkForReset(e.getPlayer());
    }
}
