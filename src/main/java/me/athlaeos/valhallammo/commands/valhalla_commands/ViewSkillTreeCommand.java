package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.menus.PlayerMenuUtilManager;
import me.athlaeos.valhallammo.menus.SkillTreeMenu;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ViewSkillTreeCommand implements Command {
	private final String skills_description;

	public ViewSkillTreeCommand(){
		skills_description = TranslationManager.getInstance().getTranslation("description_command_skills");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		new SkillTreeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender)).open();
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.skills"};
	}

	@Override
	public String getFailureMessage() {
		return "&c/valhalla skills";
	}

	@Override
	public String getDescription() {
		return skills_description;
	}

	@Override
	public String getCommand() {
		return "/valhalla skills";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}
}
