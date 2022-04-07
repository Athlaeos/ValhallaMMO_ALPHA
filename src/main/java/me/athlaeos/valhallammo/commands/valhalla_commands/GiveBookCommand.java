package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.managers.TutorialBook;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

public class GiveBookCommand implements Command {
	private final String error_player_not_found;
	private final String status_command_givebook_executed;
	private final String description_command_givebook;
	private final String error_command_givebook;

	public GiveBookCommand(){
		error_player_not_found = TranslationManager.getInstance().getTranslation("error_command_player_offline");
		status_command_givebook_executed = TranslationManager.getInstance().getTranslation("status_command_givebook_executed");
		description_command_givebook = TranslationManager.getInstance().getTranslation("description_command_givebook");
		error_command_givebook = TranslationManager.getInstance().getTranslation("error_command_givebook");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		Collection<Player> targets = new HashSet<>();
		if (args.length < 2){
			if (sender instanceof Player){
				targets.add((Player) sender);
			} else {
				sender.sendMessage(Utils.chat("&cOnly players may perform this command for themselves."));
				return true;
			}
		}

		if (args.length >= 2){
			targets.addAll(Utils.selectPlayers(sender, args[3]));

			if (targets.isEmpty()){
				sender.sendMessage(Utils.chat(error_player_not_found));
				return true;
			}
		}
		ItemStack book = TutorialBook.getTutorialBookInstance().getBook();
		if (book != null){
			for (Player target : targets){
				target.getInventory().addItem(book.clone());
				Utils.sendMessage(sender, Utils.chat(status_command_givebook_executed));
			}
		} else {
			sender.sendMessage(Utils.chat(error_command_givebook));
		}
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.givebook"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/valhalla givebook <player>";
	}

	@Override
	public String getDescription() {
		return description_command_givebook;
	}

	@Override
	public String getCommand() {
		return "/valhalla givebook <player>";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}
}
