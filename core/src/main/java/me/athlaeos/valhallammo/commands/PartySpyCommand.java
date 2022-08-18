package me.athlaeos.valhallammo.commands;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.managers.PartyManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

public class PartySpyCommand implements CommandExecutor {
    private static final Collection<UUID> partySpies = new HashSet<>();
    private final String status_command_party_spy_enabled;
    private final String status_command_party_spy_disabled;

    public PartySpyCommand() {
        status_command_party_spy_enabled = TranslationManager.getInstance().getTranslation("status_command_party_spy_enabled");
        status_command_party_spy_disabled = TranslationManager.getInstance().getTranslation("status_command_party_spy_disabled");

        PluginCommand command = ValhallaMMO.getPlugin().getCommand("partyspy");
        assert command != null;
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("valhalla.manageparties")) {
            PartyManager.ErrorStatus.NO_PERMISSION.sendErrorMessage(sender);
            return true;
        }
        if (!(sender instanceof Player)){
            return true;
        }
        if (partySpies.contains(((Player) sender).getUniqueId())){
            partySpies.remove(((Player) sender).getUniqueId());
            sender.sendMessage(Utils.chat(status_command_party_spy_disabled));
        } else {
            partySpies.add(((Player) sender).getUniqueId());
            sender.sendMessage(Utils.chat(status_command_party_spy_enabled));
        }
        return true;
    }

    public static Collection<UUID> getPartySpies() {
        return partySpies;
    }
}
