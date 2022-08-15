package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.crafting.DynamicItemModifierManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class ItemModifierCommand implements Command {
	private final String error_player_not_found;
	private final String error_command_invalid_number;
	private final String error_command_invalid_modifier;
	private final String error_command_item_required;
	private final String description_command_modify;
	private final String error_command_modifier_failed;

	public ItemModifierCommand(){
		error_player_not_found = TranslationManager.getInstance().getTranslation("error_command_player_offline");
		error_command_invalid_number = TranslationManager.getInstance().getTranslation("error_command_invalid_number");
		error_command_invalid_modifier = TranslationManager.getInstance().getTranslation("error_command_invalid_modifier");
		error_command_item_required = TranslationManager.getInstance().getTranslation("error_command_item_required");
		description_command_modify = TranslationManager.getInstance().getTranslation("description_command_modify");
		error_command_modifier_failed = TranslationManager.getInstance().getTranslation("error_command_modifier_failed");
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
		double strength;
		double strength2;
		double strength3;
		if (args.length >= 3){
			if (DynamicItemModifierManager.getInstance().getModifiers().get(args[1]) == null){
				Utils.sendMessage(sender, Utils.chat(error_command_invalid_modifier));
				return true;
			}
			DynamicItemModifier baseModifier = DynamicItemModifierManager.getInstance().getModifiers().get(args[1]);
			DynamicItemModifier modifier;
			if (baseModifier instanceof TripleArgDynamicItemModifier){
				if (args.length >= 5){
					try {
						args[2] = (args[2].startsWith("-")) ? "-" + args[2].split("-")[1] : args[2].split("-")[0];
						args[3] = (args[3].startsWith("-")) ? "-" + args[3].split("-")[1] : args[3].split("-")[0];
						args[4] = (args[4].startsWith("-")) ? "-" + args[4].split("-")[1] : args[4].split("-")[0];
						strength = Double.parseDouble(args[2]);
						strength2 = Double.parseDouble(args[3]);
						strength3 = Double.parseDouble(args[4]);
					} catch (IllegalArgumentException ignored){
						Utils.sendMessage(sender, Utils.chat(error_command_invalid_number));
						return true;
					}

					if (args.length >= 6){
						targets.addAll(Utils.selectPlayers(sender, args[5]));
					}

					modifier = DynamicItemModifierManager.getInstance().createModifier(args[1], strength, strength2, strength3, ModifierPriority.NEUTRAL);
				} else {
					return false;
				}
			} else if (baseModifier instanceof DuoArgDynamicItemModifier){
				if (args.length >= 4){
					try {
						args[2] = (args[2].startsWith("-")) ? "-" + args[2].split("-")[1] : args[2].split("-")[0];
						args[3] = (args[3].startsWith("-")) ? "-" + args[3].split("-")[1] : args[3].split("-")[0];
						strength = Double.parseDouble(args[2]);
						strength2 = Double.parseDouble(args[3]);
					} catch (IllegalArgumentException ignored){
						Utils.sendMessage(sender, Utils.chat(error_command_invalid_number));
						return true;
					}

					if (args.length >= 5){
						targets.addAll(Utils.selectPlayers(sender, args[4]));
					}

					modifier = DynamicItemModifierManager.getInstance().createModifier(args[1], strength, strength2, ModifierPriority.NEUTRAL);
				} else {
					return false;
				}
			} else {
				try {
					args[2] = (args[2].startsWith("-")) ? "-" + args[2].split("-")[1] : args[2].split("-")[0];
					strength = Double.parseDouble(args[2]);
				} catch (IllegalArgumentException ignored){
					Utils.sendMessage(sender, Utils.chat(error_command_invalid_number));
					return true;
				}

				if (args.length >= 4){
					targets.addAll(Utils.selectPlayers(sender, args[3]));
				}

				modifier = DynamicItemModifierManager.getInstance().createModifier(args[1], strength, ModifierPriority.NEUTRAL);
			}

			if (modifier == null){
				Utils.sendMessage(sender, Utils.chat(error_command_invalid_modifier));
				return true;
			}

			if (targets.isEmpty()){
				Utils.sendMessage(sender, Utils.chat(error_player_not_found));
				return true;
			}

			for (Player target : targets){
				ItemStack result = target.getInventory().getItemInMainHand();
				if (!Utils.isItemEmptyOrNull(result)){
					result = modifier.processItem(target, result);
					if (result == null){
						sender.sendMessage(Utils.chat(error_command_modifier_failed));
						return true;
					}
					target.getInventory().setItemInMainHand(result);
				} else {
					sender.sendMessage(Utils.chat(error_command_item_required));
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.modify"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/valhalla modify [modifier] [strength] <player>";
	}

	@Override
	public String getDescription() {
		return description_command_modify;
	}

	@Override
	public String getCommand() {
		return "/valhalla modify [modifier] [strength] <player>";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2){
			return new ArrayList<>(DynamicItemModifierManager.getInstance().getModifiers().keySet());
		}
		if (args.length >= 3){
			DynamicItemModifier modifier = DynamicItemModifierManager.getInstance().getModifiers().get(args[1]);
			if (modifier != null){
				if (args.length == 3){
					List<String> alternativeArgs = modifier.tabAutoCompleteFirstArg();
					if (alternativeArgs == null || alternativeArgs.isEmpty()){
						return Collections.singletonList("<arg1>");
					} else {
						return alternativeArgs;
					}
				}
				if (modifier instanceof TripleArgDynamicItemModifier){
					if (args.length == 4){
						List<String> alternativeArgs = ((TripleArgDynamicItemModifier) modifier).tabAutoCompleteSecondArg();
						if (alternativeArgs == null || alternativeArgs.isEmpty()){
							return Collections.singletonList("<arg2>");
						} else {
							return alternativeArgs;
						}
					}
					if (args.length == 5){
						List<String> alternativeArgs = ((TripleArgDynamicItemModifier) modifier).tabAutoCompleteThirdArg();
						if (alternativeArgs == null || alternativeArgs.isEmpty()){
							return Collections.singletonList("<arg3>");
						} else {
							return alternativeArgs;
						}
					}
				}
				if (modifier instanceof DuoArgDynamicItemModifier){
					if (args.length == 4){
						List<String> alternativeArgs = ((DuoArgDynamicItemModifier) modifier).tabAutoCompleteSecondArg();
						if (alternativeArgs == null || alternativeArgs.isEmpty()){
							return Collections.singletonList("<arg2>");
						} else {
							return alternativeArgs;
						}
					}
				}
			}
		}
		return null;
	}
}
