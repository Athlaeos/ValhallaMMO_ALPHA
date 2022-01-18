package me.athlaeos.valhallammo.commands;

import me.athlaeos.valhallammo.Main;
import me.athlaeos.valhallammo.dom.SkillType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class ResetProgressCommand implements Command {
	private final String reset_description;
	private final String error_player_not_found;
	private final String error_no_permission;
	private final String command_reset_success;

	public ResetProgressCommand(){
		reset_description = TranslationManager.getInstance().getTranslation("description_command_reset");
		error_player_not_found = TranslationManager.getInstance().getTranslation("error_command_player_offline");
		error_no_permission = TranslationManager.getInstance().getTranslation("error_command_no_permission");
		command_reset_success = TranslationManager.getInstance().getTranslation("command_reset_success");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage("only players can do this");
			return true;
		}
		Player target = (Player) sender;
		if (args.length >= 2){
			if (sender.hasPermission("valhalla.recipes")){
				target = Main.getPlugin().getServer().getPlayer(args[1]);
				if (target == null){
					Utils.sendMessage(sender, Utils.chat(error_player_not_found));
					return true;
				}
			} else {
				Utils.sendMessage(sender, Utils.chat(error_no_permission));
				return true;
			}
		}
		ProfileUtil.setProfile(target, null, SkillType.ACCOUNT);
		ProfileUtil.setProfile(target, null, SkillType.SMITHING);
		Utils.sendMessage(sender, Utils.chat(command_reset_success));
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.help", "valhalla.recipes"};
	}

	@Override
	public String getFailureMessage() {
		return "&c/valhalla reset <player>";
	}

	@Override
	public String getDescription() {
		return reset_description;
	}

	@Override
	public String getCommand() {
		return "/valhalla reset <player>";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}
}
