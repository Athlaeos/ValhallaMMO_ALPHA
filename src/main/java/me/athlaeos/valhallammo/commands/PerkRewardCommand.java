package me.athlaeos.valhallammo.commands;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.crafting.DynamicItemModifierManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ItemModifierCommand implements Command {
	private final String error_player_not_found;
	private final String error_command_invalid_number;
	private final String error_command_invalid_modifier;
	private final String error_command_item_required;
	private final String description_command_modify;

	public ItemModifierCommand(){
		error_player_not_found = TranslationManager.getInstance().getTranslation("error_command_player_offline");
		error_command_invalid_number = TranslationManager.getInstance().getTranslation("error_command_invalid_number");
		error_command_invalid_modifier = TranslationManager.getInstance().getTranslation("error_command_invalid_modifier");
		error_command_item_required = TranslationManager.getInstance().getTranslation("error_command_item_required");
		description_command_modify = TranslationManager.getInstance().getTranslation("description_command_modify");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Player target = null;
		if (sender instanceof Player){
			target = (Player) sender;
		} else {
			if (args.length < 4){
				sender.sendMessage(Utils.chat("&cA player name must be given"));
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
						strength = Double.parseDouble(args[2]);
						strength2 = Double.parseDouble(args[3]);
						strength3 = Double.parseDouble(args[4]);
					} catch (IllegalArgumentException ignored){
						Utils.sendMessage(sender, Utils.chat(error_command_invalid_number));
						return true;
					}

					if (args.length >= 6){
						target = ValhallaMMO.getPlugin().getServer().getPlayer(args[5]);
					}

					modifier = DynamicItemModifierManager.getInstance().createModifier(args[1], strength, strength2, strength3, ModifierPriority.NEUTRAL);
				} else {
					return false;
				}
			} else if (baseModifier instanceof DuoArgDynamicItemModifier){
				if (args.length >= 4){
					try {
						strength = Double.parseDouble(args[2]);
						strength2 = Double.parseDouble(args[3]);
					} catch (IllegalArgumentException ignored){
						Utils.sendMessage(sender, Utils.chat(error_command_invalid_number));
						return true;
					}

					if (args.length >= 5){
						target = ValhallaMMO.getPlugin().getServer().getPlayer(args[4]);
					}

					modifier = DynamicItemModifierManager.getInstance().createModifier(args[1], strength, strength2, ModifierPriority.NEUTRAL);
				} else {
					return false;
				}
			} else {
				try {
					strength = Double.parseDouble(args[2]);
				} catch (IllegalArgumentException ignored){
					Utils.sendMessage(sender, Utils.chat(error_command_invalid_number));
					return true;
				}

				if (args.length >= 4){
					target = ValhallaMMO.getPlugin().getServer().getPlayer(args[3]);
				}

				modifier = DynamicItemModifierManager.getInstance().createModifier(args[1], strength, ModifierPriority.NEUTRAL);
			}

			if (modifier == null){
				Utils.sendMessage(sender, Utils.chat(error_command_invalid_modifier));
				return true;
			}

			if (target == null){
				Utils.sendMessage(sender, Utils.chat(error_player_not_found));
				return true;
			}

			ItemStack heldItem = target.getInventory().getItemInMainHand();
			modifier.processItem(target, heldItem);
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
					return Collections.singletonList("<arg1>");
				}
				if (modifier instanceof TripleArgDynamicItemModifier){
					if (args.length == 4){
						return Collections.singletonList("<arg2>");
					}
					if (args.length == 5){
						return Collections.singletonList("<arg3>");
					}
				}
				if (modifier instanceof DuoArgDynamicItemModifier){
					if (args.length == 4){
						return Collections.singletonList("<arg2>");
					}
				}
			}
		}
		return null;
	}
}
