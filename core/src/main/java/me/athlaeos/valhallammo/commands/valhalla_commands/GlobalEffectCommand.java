package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.dom.EffectAdditionMode;
import me.athlaeos.valhallammo.managers.GlobalEffectManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.command.CommandSender;

import java.util.*;
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
						duration = Long.parseLong(args[3].replace("w", "").replace("d", "").replace("h", "")
								.replace("m", "").replace("s", ""));
						amplifier = Double.parseDouble(args[4]);
					} catch (IllegalArgumentException ignored){
						sender.sendMessage(Utils.chat(error_command_invalid_number));
						return true;
					}
					if (args[3].endsWith("w")){
						duration *= (7 * 24 * 60 * 60);
					} else if (args[3].endsWith("d")){
						duration *= (24 * 60 * 60);
					} else if (args[3].endsWith("h")){
						duration *= (60 * 60);
					} else if (args[3].endsWith("m")){
						duration *= (60);
					}
					duration *= 1000;

					String bossBar = null;
					BarColor color = null;
					BarStyle style = null;
					if (args.length >= 6){
						try {
							mode = EffectAdditionMode.valueOf(args[5]);
						} catch (IllegalArgumentException ignored){
							sender.sendMessage(Utils.chat(error_command_invalid_option));
							return true;
						}

						if (args.length >= 9){
							try {
								color = BarColor.valueOf(args[6]);
								style = BarStyle.valueOf(args[7]);
							} catch (IllegalArgumentException ignored){
								sender.sendMessage(Utils.chat(error_command_invalid_option));
								return true;
							}
							bossBar = String.join(" ", Arrays.copyOfRange(args, 8, args.length));
						}
					}
					GlobalEffectManager.getInstance().addEffect(effect, duration, amplifier, mode, bossBar, color, style);
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
							.replace("%duration_timestamp2%", Utils.toTimeStamp2((int) newDuration, 1000))
							.replace("%duration_seconds%", "" + Math.round(newDuration))
							.replace("%duration_minutes%", String.format("%.1f", newDuration / 60D))
							.replace("%duration_hours%", String.format("%.1f", newDuration / 3600D))
							.replace("%duration_days%", String.format("%.1f", newDuration / 3600D))
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
			List<String> validSuffixes = Arrays.asList("s", "m", "h", "d", "w");
			if (!(args[3].endsWith("w") || args[3].endsWith("d") || args[3].endsWith("h") || args[3].endsWith("m") || args[3].endsWith("s"))){
				return validSuffixes.stream().map(s -> args[3] + s).collect(Collectors.toList());
			}
			return Collections.singletonList("<duration>");
		} else if (args.length == 5) {
			return Collections.singletonList("<amplifier>");
		} else if (args.length == 6){
			return Arrays.stream(EffectAdditionMode.values()).map(EffectAdditionMode::toString).collect(Collectors.toList());
		} else if (args.length == 7){
			return Arrays.stream(BarColor.values()).map(Objects::toString).collect(Collectors.toList());
		} else if (args.length == 8){
			return Arrays.stream(BarStyle.values()).map(Objects::toString).collect(Collectors.toList());
		} else if (args.length == 9){
			return Collections.singletonList("<bar_title>");
		}
		return null;
	}
}
