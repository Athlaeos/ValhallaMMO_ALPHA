package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.menus.PlayerMenuUtilManager;
import me.athlaeos.valhallammo.menus.temporary.TemporaryRecipeViewMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TemporaryRecipesViewCommand implements Command {

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		new TemporaryRecipeViewMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender)).open();
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.viewunlockedrecipes"};
	}

	@Override
	public String getFailureMessage() {
		return "&c/valhalla viewrecipes";
	}

	@Override
	public String getCommand() {
		return "/valhalla viewrecipes";
	}

	@Override
	public String getDescription() {
		return "TEMPORARY command allowing players to safely view their unlocked recipes. Will be removed in the future when a better solution is implemented";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}
}
