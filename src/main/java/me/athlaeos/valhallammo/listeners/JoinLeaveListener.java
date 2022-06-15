package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.ProfileVersionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.managers.TutorialBook;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;

public class JoinLeaveListener implements Listener {
    private boolean book_on_join;
    private String error_command_givebook;
    private static JoinLeaveListener listener;

    public JoinLeaveListener(){
        listener = this;
        book_on_join = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("book_on_join");
        error_command_givebook = TranslationManager.getInstance().getTranslation("error_command_givebook");
    }

    public void reload(){
        book_on_join = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("book_on_join");
        error_command_givebook = TranslationManager.getInstance().getTranslation("error_command_givebook");
    }

    public static JoinLeaveListener getListener() {
        return listener;
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onPlayerJoin(PlayerJoinEvent e){
        ProfileManager.getManager().loadPlayerProfiles(e.getPlayer());

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

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e){
        // the following code is to remove valhallammo's attribute modifiers off players when they log off
        // this is to prevent, in the case valhallammo is being uninstalled, no unintended attributes remain
        // stuck on the player.
        AttributeInstance knockbackResistanceInstance = e.getPlayer().getAttribute(Attribute.GENERIC_KNOCKBACK_RESISTANCE);
        if (knockbackResistanceInstance != null){
            Collection<AttributeModifier> modifiersToRemove = new HashSet<>();
            for (AttributeModifier modifier : knockbackResistanceInstance.getModifiers()){
                if (modifier.getName().equals("valhalla_knockback_resistance_modifier")){
                    modifiersToRemove.add(modifier);
                }
            }
            for (AttributeModifier modifier : modifiersToRemove){
                knockbackResistanceInstance.removeModifier(modifier);
            }
        }

        for (PlayerMovementListener.AttributeDataHolder holder : PlayerMovementListener.getAttributesToUpdate().values()){
            AttributeInstance instance = e.getPlayer().getAttribute(holder.getType());
            if (instance != null){
                Collection<AttributeModifier> modifiersToRemove = new HashSet<>();
                for (AttributeModifier modifier : instance.getModifiers()){
                    if (modifier.getName().equals(holder.getName())){
                        modifiersToRemove.add(modifier);
                    }
                }
                for (AttributeModifier modifier : modifiersToRemove){
                    instance.removeModifier(modifier);
                }
            }
        }
    }
}