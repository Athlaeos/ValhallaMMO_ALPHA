package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.managers.ItemDictionaryManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.menus.ItemDictionaryMenu;
import me.athlaeos.valhallammo.menus.PlayerMenuUtilManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemDictionaryCommand implements Command {
	private final String status_command_item_indexed;
	private final String error_command_invalid_number;
	private final String error_command_item_id_not_found;
	private final String error_command_item_required;
	private final String description_command_itemindex;
	private final String status_command_item_removed;

	public ItemDictionaryCommand(){
		status_command_item_indexed = TranslationManager.getInstance().getTranslation("status_command_item_indexed");
		error_command_invalid_number = TranslationManager.getInstance().getTranslation("error_command_invalid_number");
		error_command_item_id_not_found = TranslationManager.getInstance().getTranslation("error_command_item_id_not_found");
		error_command_item_required = TranslationManager.getInstance().getTranslation("error_command_item_required");
		description_command_itemindex = TranslationManager.getInstance().getTranslation("description_command_itemindex");
		status_command_item_removed = TranslationManager.getInstance().getTranslation("status_command_item_removed");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage(Utils.chat("&cOnly players can perform this command"));
			return true;
		}
		if (args.length >= 2){
			if (args[1].equalsIgnoreCase("add")){
				ItemStack inHandItem = ((Player) sender).getInventory().getItemInMainHand();
				if (!Utils.isItemEmptyOrNull(inHandItem)){
					int id = ItemDictionaryManager.getInstance().addItem(inHandItem);
					sender.sendMessage(Utils.chat(status_command_item_indexed.replace("%id%", "" + id)));
				} else {
					sender.sendMessage(Utils.chat(error_command_item_required));
				}
				return true;
			} else if (args[1].equalsIgnoreCase("set")){
				if (args.length >= 3){
					try {
						ItemStack inHandItem = ((Player) sender).getInventory().getItemInMainHand();
						if (!Utils.isItemEmptyOrNull(inHandItem)){
							int newId = Integer.parseInt(args[2].split("-")[0]);
							if (ItemDictionaryManager.getInstance().getItemDictionary().containsKey(newId)){
								ItemDictionaryManager.getInstance().getItemDictionary().put(newId, inHandItem);
								sender.sendMessage(Utils.chat(status_command_item_indexed.replace("%id%", args[2])));
							} else {
								sender.sendMessage(Utils.chat(error_command_item_id_not_found));
							}
						} else {
							sender.sendMessage(Utils.chat(error_command_item_required));
						}
						return true;
					} catch (IllegalArgumentException ignored){
						sender.sendMessage(Utils.chat(error_command_invalid_number));
					}
					return true;
				}
			} else if (args[1].equalsIgnoreCase("remove")){
				if (args.length >= 3){
					try {
						int id = Integer.parseInt(args[2].split("-")[0]);
						if (ItemDictionaryManager.getInstance().getItemDictionary().containsKey(id)){
							ItemDictionaryManager.getInstance().removeItem(id);
							sender.sendMessage(Utils.chat(status_command_item_removed));
						} else {
							sender.sendMessage(Utils.chat(error_command_item_id_not_found));
						}
						return true;
					} catch (IllegalArgumentException ignored){
						sender.sendMessage(Utils.chat(error_command_invalid_number));
					}
					return true;
				}
			} else if (args[1].equalsIgnoreCase("menu")){
				new ItemDictionaryMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender)).open();
				return true;
			}
		}
		return false;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.itemindex"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/valhalla itemindex <add/set/remove> [index]";
	}

	@Override
	public String getDescription() {
		return description_command_itemindex;
	}

	@Override
	public String getCommand() {
		return "/valhalla itemindex <add/set/remove> [index]";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		if (args.length == 2){
			return Arrays.asList("add", "set", "remove", "menu");
		}
		if (args.length == 3 && (args[1].equalsIgnoreCase("set") || args[1].equalsIgnoreCase("remove"))){
			List<String> suggestions = new ArrayList<>();
			for (int i : ItemDictionaryManager.getInstance().getItemDictionary().keySet()){
				ItemStack item = ItemDictionaryManager.getInstance().getItemDictionary().get(i);
				if (!Utils.isItemEmptyOrNull(item)){
					suggestions.add(i + "-" + Utils.getItemName(item));
				}
			}
			return suggestions;
		}
		return null;
	}
}
