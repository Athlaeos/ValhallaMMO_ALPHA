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

import java.util.Arrays;

public class PartyChatCommand implements CommandExecutor {
    private final String error_command_player_required;
    private final String status_command_party_chat_enabled;
    private final String status_command_party_chat_disabled;

    public PartyChatCommand() {
        error_command_player_required = TranslationManager.getInstance().getTranslation("error_command_player_required");
        status_command_party_chat_enabled = TranslationManager.getInstance().getTranslation("status_command_party_chat_enabled");
        status_command_party_chat_disabled = TranslationManager.getInstance().getTranslation("status_command_party_chat_disabled");

        PluginCommand command = ValhallaMMO.getPlugin().getCommand("partychat");
        assert command != null;
        command.setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player){
            Player player = (Player) sender;
            PartyManager.ErrorStatus status;
            if (args.length == 0){
                status = PartyManager.getInstance().togglePartyChat(player);
                if (status == null){
                    if (PartyManager.getInstance().getPlayersInPartyChat().contains(player.getUniqueId())){
                        sender.sendMessage(Utils.chat(status_command_party_chat_enabled));
                    } else {
                        sender.sendMessage(Utils.chat(status_command_party_chat_disabled));
                    }
                    return true;
                }
            } else {
                status = PartyManager.getInstance().hasPermission(player, "party_chat");
                if (status == null){
                    player.chat("pc:" + String.join(" ", Arrays.copyOfRange(args, 0, args.length)));
                }
            }
            if (status != null){
                status.sendErrorMessage(player);
            }
        } else {
            sender.sendMessage(Utils.chat(error_command_player_required));
        }
        return true;
    }
}
