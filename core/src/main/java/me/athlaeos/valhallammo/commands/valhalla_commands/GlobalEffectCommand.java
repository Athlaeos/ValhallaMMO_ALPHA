package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.dom.EffectAdditionMode;
import me.athlaeos.valhallammo.managers.GlobalEffectManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class GlobalEffectCommand implements Command {
	private final String description_command_globalbuff;
	private final String status_command_global_buff_applied;
	private final String status_command_global_buff_warning;
	private final String error_command_global_buff_expired;
	private final String status_command_global_buff_removed;
	private final String error_command_invalid_number;
	private final String error_command_invalid_option;

	public GlobalEffectCommand(){
		description_command_globalbuff = TranslationManager.getInstance().getTranslation("description_command_skills");
		status_command_global_buff_applied = TranslationManager.getInstance().getTranslation("status_command_global_buff_applied");
		status_command_global_buff_warning = TranslationManager.getInstance().getTranslation("status_command_global_buff_warning");
		error_command_global_buff_expired = TranslationManager.getInstance().getTranslation("error_command_global_buff_expired");
		status_command_global_buff_removed = TranslationManager.getInstance().getTranslation("status_command_global_buff_removed");
		error_command_invalid_number = TranslationManager.getInstance().getTranslation("error_command_invalid_number");
		error_command_invalid_option = TranslationManager.getInstance().getTranslation("error_command_invalid_option");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length >= 3){
			String effect = args[2];
			if (args[1].equalsIgnoreCase("remove")){
				if (GlobalEffectManager.getInstance().isActive(effect)){
					GlobalEffectManager.getInstance().getActiveGlobalEffects().remove(effect);
					sender.sendMessage(Utils.chat(status_command_global_buff_removed));
				} else {
					sender.sendMessage(Utils.chat(error_command_global_buff_expired));
				}
				return true;
			} else if (args[1].equalsIgnoreCase("add")){
				if (args.length >= 5){
					long duration;
					double amplifier;
					EffectAdditionMode mode = EffectAdditionMode.OVERWRITE;
					try {
						duration = Long.parseLong(args[3]);
						amplifier = Double.parseDouble(args[4]);
					} catch (IllegalArgumentException ignored){
						sender.sendMessage(Utils.chat(error_command_invalid_number));
						return true;
					}
					if (args.length >= 6){
						try {
							mode = EffectAdditionMode.valueOf(args[5]);
						} catch (IllegalArgumentException ignored){
							sender.sendMessage(Utils.chat(error_command_invalid_option));
							return true;
						}
					}
					GlobalEffectManager.getInstance().addEffect(effect, duration, amplifier, mode);
					double newAmplifier = GlobalEffectManager.getInstance().getAmplifier(effect);
					double newDuration = GlobalEffectManager.getInstance().getDuration(effect);
					String reply;
					if (GlobalEffectManager.getInstance().getValidEffects().contains(effect)) {
						reply = status_command_global_buff_applied;
					} else {
						reply = status_command_global_buff_warning;
					}
					sender.sendMessage(Utils.chat(reply
							.replace("%duration_timestamp%", Utils.toTimeStamp((int) newDuration, 1000))
							.replace("%duration_seconds%", "" + Math.round(newDuration / 1000D))
							.replace("%duration_minutes%", String.format("%.1f", newDuration / 60000D))
							.replace("%duration_hours%", String.format("%.1f", newDuration / 3600000D))
							.replace("%amplifier%", String.format("%.2f", newAmplifier))
							.replace("%effect%", effect)));
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.globalbuffs"};
	}

	@Override
	public String getFailureMessage() {
		return "&c/valhalla globalbuff remove/add [effect] [duration] [amplifier] <mode>";
	}

	@Override
	public String getDescription() {
		return description_command_globalbuff;
	}

	@Override
	public String getCommand() {
		return "/valhalla globalbuff remove/add [effect] [duration] [amplifier] <mode>";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2){
			return Arrays.asList("add", "remove");
		} else if (args.length == 3){
			return new ArrayList<>(GlobalEffectManager.getInstance().getValidEffects());
		} else if (args.length == 4) {
			return Collections.singletonList("<duration>");
		} else if (args.length == 5) {
			return Collections.singletonList("<amplifier>");
		} else if (args.length == 6){
			return Arrays.stream(EffectAdditionMode.values()).map(EffectAdditionMode::toString).collect(Collectors.toList());
		}
		return null;
	}
}
