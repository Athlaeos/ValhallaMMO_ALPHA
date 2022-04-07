package me.athlaeos.valhallammo.commands;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.managers.PartyManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

import java.util.Arrays;

public class PartyCommand implements CommandExecutor {
	private final String error_no_permission;

	public PartyCommand(ValhallaMMO plugin) {
		error_no_permission = TranslationManager.getInstance().getTranslation("error_command_invalid_command");

		PluginCommand command = plugin.getCommand("party");
		assert command != null;
		command.setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender s, org.bukkit.command.Command cmd, String name, String[] args) {
		if (!(s instanceof Player)) return true;
		Player sender = (Player) s;
		Player target = null;
		PartyManager m = PartyManager.getManager();
		if (args.length > 0){
			if (args[0].equalsIgnoreCase("create")){
				if (args.length > 1) {
					if (m.registerParty(m.createParty(sender, args[1]))) {
						// display "party created" message
					} else {
						// display "party not created" err message
					}
				}
			} else if (args[0].equalsIgnoreCase("info")){
				if (!m.displayPartyInfo(sender)){
					// display "no party" err message
				}
			} else if (args[0].equalsIgnoreCase("setdescription")){
				if (args.length > 2){
					target = ValhallaMMO.getPlugin().getServer().getPlayer(args[1]);
					String description = String.join(" ", Arrays.copyOfRange(args, target == null ? 1 : 2, args.length));
					if (m.hasPartyPermission(target != null ? target : sender, "manage_description")) {

					}
				}
			}
		}
		return false;
	}
}
