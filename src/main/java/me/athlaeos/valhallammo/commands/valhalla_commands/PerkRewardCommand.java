package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.dom.ObjectType;
import me.athlaeos.valhallammo.managers.PerkRewardsManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.perkrewards.PerkReward;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class PerkRewardCommand implements Command {
	private final String error_player_not_found;
	private final String error_command_invalid_reward;
	private final String error_command_invalid_argument_type;
	private final String description_command_reward;

	public PerkRewardCommand(){
		error_player_not_found = TranslationManager.getInstance().getTranslation("error_command_player_offline");
		error_command_invalid_reward = TranslationManager.getInstance().getTranslation("error_command_invalid_reward");
		error_command_invalid_argument_type = TranslationManager.getInstance().getTranslation("error_command_invalid_argument_type");
		description_command_reward = TranslationManager.getInstance().getTranslation("description_command_reward");
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

		if (args.length >= 3){
			if (args.length >= 4){
				targets.addAll(Utils.selectPlayers(sender, args[3]));
			}
			PerkReward baseReward = PerkRewardsManager.getInstance().getPerkRewards().get(args[1]);
			PerkReward createdReward = null;
			ObjectType expectedType;
			if (baseReward == null){
				Utils.sendMessage(sender, Utils.chat(error_command_invalid_reward));
				return true;
			} else {
				expectedType = baseReward.getType();
			}
			switch (expectedType){
				case DOUBLE:{
					try {
						double arg = Double.parseDouble(args[2]);
						createdReward = PerkRewardsManager.getInstance().createReward(args[1], arg);
					} catch (IllegalArgumentException ignored){
						sender.sendMessage(Utils.chat(error_command_invalid_argument_type
						.replace("%type%", expectedType.toString())
						.replace("%arg%", args[2])));
						return true;
					}
					break;
				}
				case INTEGER:{
					try {
						int arg = Integer.parseInt(args[2]);
						createdReward = PerkRewardsManager.getInstance().createReward(args[1], arg);
					} catch (IllegalArgumentException ignored){
						sender.sendMessage(Utils.chat(error_command_invalid_argument_type
								.replace("%type%", expectedType.toString())
								.replace("%arg%", args[2])));
						return true;
					}
					break;
				}
				case STRING:{
					createdReward = PerkRewardsManager.getInstance().createReward(args[1], args[2].replace("[]", " "));
					break;
				}
				case STRING_LIST:{
					List<String> arg = new ArrayList<>();
					if (args[2].contains(";")){
						for (String s : args[2].split(";")){
							arg.add(s.replace("[]", " "));
						}
					} else {
						arg.add(args[2]);
					}
					createdReward = PerkRewardsManager.getInstance().createReward(args[1], arg);
					break;
				}
				case BOOLEAN:{
					try {
						boolean arg = Boolean.parseBoolean(args[2]);
						createdReward = PerkRewardsManager.getInstance().createReward(args[1], arg);
					} catch (IllegalArgumentException ignored){
						sender.sendMessage(Utils.chat(error_command_invalid_argument_type
								.replace("%type%", expectedType.toString())
								.replace("%arg%", args[2])));
						return true;
					}
					break;
				}
				case NONE:{
					targets.addAll(Utils.selectPlayers(sender, args[2]));
					createdReward = PerkRewardsManager.getInstance().createReward(args[1], "");
				}
			}

			if (createdReward == null){
				Utils.sendMessage(sender, Utils.chat(error_command_invalid_reward));
				return true;
			}

			if (targets.isEmpty()){
				Utils.sendMessage(sender, Utils.chat(error_player_not_found));
				return true;
			}

			for (Player target : targets){
				createdReward.execute(target);
			}
			return true;
		}
		return false;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.reward"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/valhalla reward [reward] [argument] <player>";
	}

	@Override
	public String getDescription() {
		return description_command_reward;
	}

	@Override
	public String getCommand() {
		return "/valhalla reward [reward] [argument] <player>";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2){
			return new ArrayList<>(PerkRewardsManager.getInstance().getPerkRewards().keySet());
		}
		if (args.length >= 3){
			PerkReward reward = PerkRewardsManager.getInstance().getPerkRewards().get(args[1]);
			if (reward != null){
				if (args.length == 3){
					switch (reward.getType()){
						case BOOLEAN:{
							return Arrays.asList("true", "false");
						}
						case STRING:{
							return Collections.singletonList("string_arg");
						}
						case STRING_LIST:{
							if (args[2].equalsIgnoreCase("") ||
							args[2].equalsIgnoreCase(" ")){
								return Collections.singletonList("string_arg;");
							}
							return Collections.singletonList(args[2] + ";");
						}
						case INTEGER:{
							return Arrays.asList("1", "2", "3", "int_arg");
						}
						case DOUBLE:{
							return Arrays.asList("1.0", "2.0", "3.0", "double_arg");
						}
						case NONE:{
							return null;
						}
					}
				}
			}
		}
		return null;
	}
}
