package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.managers.PlaceholderManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class ProfileStatsCommand implements Command {
	private final String playerNotFound;
	private final String error_command_invalid_skill;
	private final String error_command_no_permission;
	private final String description_command_profile;
	private final Map<String, List<String>> profileFormats = new HashMap<>();
	private static ProfileStatsCommand command;

	public static ProfileStatsCommand getInstance(){
		return command;
	}

	public void reload(){
		registerSkillProfileFormats();
	}

	public ProfileStatsCommand(){
		command = this;
		registerSkillProfileFormats();
		playerNotFound = TranslationManager.getInstance().getTranslation("error_command_player_offline");
		error_command_invalid_skill = TranslationManager.getInstance().getTranslation("error_command_invalid_skill");
		error_command_no_permission = TranslationManager.getInstance().getTranslation("error_command_no_permission");
		description_command_profile = TranslationManager.getInstance().getTranslation("description_command_profile");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player target;
		if (!(sender instanceof Player)){
			if (args.length < 3){
				sender.sendMessage(Utils.chat("&cOnly players can do this"));
				return true;
			} else {
				target = ValhallaMMO.getPlugin().getServer().getPlayer(args[2]);
				if (target == null){
					Utils.sendMessage(sender, Utils.chat(playerNotFound));
					return true;
				}
			}
		} else {
			target = (Player) sender;
		}

		if (args.length >= 3){
			if (sender.hasPermission("valhalla.profile.other")){
				target = ValhallaMMO.getPlugin().getServer().getPlayer(args[2]);
				if (target == null){
					Utils.sendMessage(sender, Utils.chat(playerNotFound));
					return true;
				}
			} else {
				Utils.sendMessage(sender, Utils.chat(error_command_no_permission));
				return true;
			}
		}
		if (args.length >= 2){
			String type = args[1].toUpperCase();
			if (SkillProgressionManager.getInstance().getSkill(type) == null) {
				Utils.sendMessage(sender, Utils.chat(error_command_invalid_skill));
				return true;
			}
			if (profileFormats.containsKey(type)){
				for (String s : profileFormats.get(type)){
					sender.sendMessage(Utils.chat(PlaceholderManager.parse(s, target)));
				}
			} else {
				sender.sendMessage(Utils.chat("&c:("));
			}
			return true;
		}
		return false;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.profile" , "valhalla.profile.other"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/valhalla profile <type> <name>";
	}

	@Override
	public String getCommand() {
		return "/valhalla profile <type> <name>";
	}

	@Override
	public String getDescription() {
		return description_command_profile;
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2){
			return SkillProgressionManager.getInstance().getAllSkills().keySet().stream().map(String::toLowerCase).collect(Collectors.toList());
		}
		return null;
	}

	private void registerSkillProfileFormats(){
		for (String t : SkillProgressionManager.getInstance().getAllSkills().keySet()){
			List<String> format = TranslationManager.getInstance().getList("profile_format_" + t.toLowerCase());
			if (format == null) format = new ArrayList<>();
			profileFormats.put(t, format);
		}
	}
}
