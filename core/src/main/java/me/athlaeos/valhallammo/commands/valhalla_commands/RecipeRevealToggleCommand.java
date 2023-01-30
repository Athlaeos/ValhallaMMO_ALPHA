package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

public class RecipeRevealToggleCommand implements Command {
	private final String description_command_revealrecipes;
	private final String status_command_revealrecipes_enabled;
	private final String status_command_revealrecipes_disabled;

	public RecipeRevealToggleCommand(){
		description_command_revealrecipes = TranslationManager.getInstance().getTranslation("description_command_revealrecipes");
		status_command_revealrecipes_enabled = TranslationManager.getInstance().getTranslation("status_command_revealrecipes_enabled");
		status_command_revealrecipes_disabled = TranslationManager.getInstance().getTranslation("status_command_revealrecipes_disabled");
	}

	private static final Collection<UUID> revealRecipesForCollection = new HashSet<>();

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (sender instanceof Player){
			if (revealRecipesForCollection.contains(((Player) sender).getUniqueId())){
				revealRecipesForCollection.remove(((Player) sender).getUniqueId());
				sender.sendMessage(Utils.chat(status_command_revealrecipes_disabled));
			} else {
				revealRecipesForCollection.add(((Player) sender).getUniqueId());
				sender.sendMessage(Utils.chat(status_command_revealrecipes_enabled));
			}
		}
		return true;
	}

	public static Collection<UUID> getRevealRecipesForCollection() {
		return revealRecipesForCollection;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.recipes"};
	}

	@Override
	public String getFailureMessage() {
		return "&c/valhalla revealrecipekeys";
	}

	@Override
	public String getDescription() {
		return description_command_revealrecipes;
	}

	@Override
	public String getCommand() {
		return "/valhalla revealrecipekeys";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}
}
