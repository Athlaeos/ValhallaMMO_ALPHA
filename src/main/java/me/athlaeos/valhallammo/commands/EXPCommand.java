package me.athlaeos.valhallammo.commands;

import me.athlaeos.valhallammo.Main;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.dom.Skill;
import me.athlaeos.valhallammo.dom.SkillType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class TempEXPCommand implements Command {
	private final String error_player_not_found;
	private final String error_command_invalid_number;
	private final String error_command_invalid_skill;
	private final String command_exp_success;

	public TempEXPCommand(){
		error_player_not_found = TranslationManager.getInstance().getTranslation("error_command_player_offline");
		error_command_invalid_number = TranslationManager.getInstance().getTranslation("error_command_invalid_number");
		error_command_invalid_skill = TranslationManager.getInstance().getTranslation("error_command_invalid_skill");
		command_exp_success = TranslationManager.getInstance().getTranslation("command_exp_success");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player target = null;
		if (sender instanceof Player){
			target = (Player) sender;
		}
		double amount;
		if (args.length >= 3){
			if (args.length >= 4){
				target = Main.getPlugin().getServer().getPlayer(args[3]);
			}
			if (target == null){
				Utils.sendMessage(sender, Utils.chat(error_player_not_found));
				return true;
			}

			try {
				amount = Double.parseDouble(args[2]);
			} catch (IllegalArgumentException ignored){
				Utils.sendMessage(sender, Utils.chat(error_command_invalid_number));
				return true;
			}

			SkillType skillType;
			try {
				skillType = SkillType.valueOf(args[1]);
			} catch (IllegalArgumentException ignored){
				Utils.sendMessage(sender, Utils.chat(error_command_invalid_skill));
				return true;
			}

			Profile profile = ProfileUtil.getProfile(target, skillType);
			if (profile != null){
				Skill skill = SkillProgressionManager.getInstance().getSkill(skillType);
				if (skill != null){
					skill.addEXP(target, amount);
					Utils.sendMessage(sender, command_exp_success
							.replace("%player%", target.getName())
							.replace("%amount%", String.format("%.2f", amount))
							.replace("%skill%", skillType.toString().toLowerCase()));
				} else {
					Utils.sendMessage(sender, Utils.chat(error_command_invalid_skill));
					return true;
				}
			}
			sender.sendMessage(Utils.chat("&cNo Profile Found"));
		}
		return false;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.recipes"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/valhalla exp [amount]";
	}

	@Override
	public String getDescription() {
		return "Temporary command used to give a player a given amount of SMITHING experience";
	}

	@Override
	public String getCommand() {
		return "/valhalla exp [amount]";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}
}
