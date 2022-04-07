package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.menus.ManageLootTableSelectionMenu;
import me.athlaeos.valhallammo.menus.PlayerMenuUtilManager;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ManageLootTablesCommand implements Command {
	private final String description_command_loot;

	public ManageLootTablesCommand(){
		description_command_loot = TranslationManager.getInstance().getTranslation("description_command_loot");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		new ManageLootTableSelectionMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender)).open();
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.loottables"};
	}

	@Override
	public String getFailureMessage() {
		return "&c/valhalla loot";
	}

	@Override
	public String getCommand() {
		return "/valhalla loot";
	}

	@Override
	public String getDescription() {
		return description_command_loot;
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}
}
