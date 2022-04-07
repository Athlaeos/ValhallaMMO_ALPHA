package me.athlaeos.valhallammo.commands;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.menus.PlayerMenuUtilManager;
import me.athlaeos.valhallammo.menus.SkillTreeMenu;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;

public class SkillsCommand implements CommandExecutor {
	private final String error_no_permission;

	public SkillsCommand(ValhallaMMO plugin) {
		error_no_permission = TranslationManager.getInstance().getTranslation("error_command_invalid_command");

		PluginCommand command = plugin.getCommand("skills");
		assert command != null;
		command.setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String name, String[] args) {
		if (sender.hasPermission("valhalla.skills")){
			new SkillTreeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender)).open();
		} else {
			Utils.sendMessage(sender, Utils.chat(error_no_permission));
		}
		return true;
	}
}
