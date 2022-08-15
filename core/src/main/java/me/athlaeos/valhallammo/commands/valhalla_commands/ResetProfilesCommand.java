package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.managers.CooldownManager;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;

public class ResetProfilesCommand implements Command {
	private final String reset_description;
	private final String error_player_not_found;
	private final String error_no_permission;
	private final String error_command_invalid_option;
	private final String status_command_soft_reset_success;
	private final String status_command_hard_reset_success;
	private final String error_command_player_required;
	private final String warning_profile_reset;

	private Long timeConsoleAttemptedReset = 0L;

	public ResetProfilesCommand(){
		reset_description = TranslationManager.getInstance().getTranslation("description_command_reset");
		error_player_not_found = TranslationManager.getInstance().getTranslation("error_command_player_offline");
		error_no_permission = TranslationManager.getInstance().getTranslation("error_command_no_permission");
		status_command_soft_reset_success = TranslationManager.getInstance().getTranslation("status_command_soft_reset_success");
		status_command_hard_reset_success = TranslationManager.getInstance().getTranslation("status_command_hard_reset_success");
		error_command_invalid_option = TranslationManager.getInstance().getTranslation("error_command_invalid_option");
		error_command_player_required = TranslationManager.getInstance().getTranslation("error_command_player_required");
		warning_profile_reset = TranslationManager.getInstance().getTranslation("warning_profile_reset");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player target = null;
		if (sender instanceof Player){
			target = (Player) sender;
		}
		boolean hard;
		if (args.length >= 3){
			if (sender.hasPermission("valhalla.reset.other")){
				target = ValhallaMMO.getPlugin().getServer().getPlayer(args[2]);
				if (target == null){
					Utils.sendMessage(sender, Utils.chat(error_player_not_found));
					return true;
				}
			} else {
				Utils.sendMessage(sender, Utils.chat(error_no_permission));
				return true;
			}
		}
		if (args.length >= 2){
			if (args[1].equalsIgnoreCase("true")){
				hard = true;
			} else if (args[1].equalsIgnoreCase("false")){
				if (!sender.hasPermission("valhalla.reset")){
					sender.sendMessage(Utils.chat(error_no_permission));
					return true;
				}
				hard = false;
			} else {
				sender.sendMessage(Utils.chat(error_command_invalid_option));
				return true;
			}
		} else {
			return false;
		}
		if (target == null){
			sender.sendMessage(Utils.chat(error_command_player_required));
			return true;
		}
		if (sender instanceof Player){
			if (hard && CooldownManager.getInstance().isCooldownPassed(((Player) sender).getUniqueId(), "reset_command_attempt")){
				sender.sendMessage(Utils.chat(warning_profile_reset));
				CooldownManager.getInstance().setCooldown(((Player) sender).getUniqueId(), 10000, "reset_command_attempt");
				return true;
			}
		} else {
			if (hard && timeConsoleAttemptedReset <= System.currentTimeMillis()){
				sender.sendMessage(Utils.chat(warning_profile_reset));
				timeConsoleAttemptedReset = System.currentTimeMillis() + 10000;
				return true;
			}
		}
		ProfileManager.getManager().resetProfiles(target, hard);
		Utils.sendMessage(sender, Utils.chat(hard ? status_command_hard_reset_success : status_command_soft_reset_success));
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.reset.other", "valhalla.reset"};
	}

	@Override
	public String getFailureMessage() {
		return "&c/valhalla reset [false/true (hard reset)] <player>";
	}

	@Override
	public String getDescription() {
		return reset_description;
	}

	@Override
	public String getCommand() {
		return "&c/valhalla reset [false/true (hard reset)] <player>";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2) return Arrays.asList("true", "false");
		return null;
	}
}
