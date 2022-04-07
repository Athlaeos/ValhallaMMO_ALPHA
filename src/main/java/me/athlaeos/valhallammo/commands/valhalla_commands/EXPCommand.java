package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.managers.ProfileManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

public class EXPCommand implements Command {
	private final String error_player_not_found;
	private final String error_command_invalid_number;
	private final String error_command_invalid_skill;
	private final String status_command_exp_success;
	private final String description_command_exp;

	public EXPCommand(){
		error_player_not_found = TranslationManager.getInstance().getTranslation("error_command_player_offline");
		error_command_invalid_number = TranslationManager.getInstance().getTranslation("error_command_invalid_number");
		error_command_invalid_skill = TranslationManager.getInstance().getTranslation("error_command_invalid_skill");
		status_command_exp_success = TranslationManager.getInstance().getTranslation("status_command_exp_success");
		description_command_exp = TranslationManager.getInstance().getTranslation("description_command_exp");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Collection<Player> targets = new HashSet<>();
		if (args.length < 4){
			if (sender instanceof Player){
				targets.add((Player) sender);
			} else {
				sender.sendMessage(Utils.chat("&cOnly players may perform this command for themselves."));
				return true;
			}
		}
		double amount;
		if (args.length >= 3){
			if (args.length >= 4){
				targets.addAll(Utils.selectPlayers(sender, args[3]));
			}

			try {
				amount = Double.parseDouble(args[2]);
				if (amount <= 0) throw new IllegalArgumentException();
			} catch (IllegalArgumentException ignored){
				Utils.sendMessage(sender, Utils.chat(error_command_invalid_number));
				return true;
			}

			String skillType;
			try {
				skillType = args[1].toUpperCase();
			} catch (IllegalArgumentException ignored){
				Utils.sendMessage(sender, Utils.chat(error_command_invalid_skill));
				return true;
			}

			if (targets.isEmpty()){
				sender.sendMessage(Utils.chat(error_player_not_found));
				return true;
			}

			for (Player target : targets){
				Profile profile = ProfileManager.getProfile(target, skillType);
				if (profile != null){
					Skill skill = SkillProgressionManager.getInstance().getSkill(skillType);
					if (skill != null){
						skill.addEXP(target, amount, false);
						Utils.sendMessage(sender, status_command_exp_success
								.replace("%player%", target.getName())
								.replace("%amount%", String.format("%.2f", amount))
								.replace("%skill%", TranslationManager.getInstance().getSkillTranslation(skillType)));
					} else {
						Utils.sendMessage(sender, Utils.chat(error_command_invalid_skill));
					}
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.exp"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/valhalla exp [skilltype] [amount] <player>";
	}

	@Override
	public String getDescription() {
		return description_command_exp;
	}

	@Override
	public String getCommand() {
		return "/valhalla exp [skilltype] [amount] <player>";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2){
			return SkillProgressionManager.getInstance().getAllSkills().keySet().stream().map(String::toLowerCase).collect(Collectors.toList());
		}
		return null;
	}
}
