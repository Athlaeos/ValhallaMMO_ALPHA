package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.NamespacedKey;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.persistence.PersistentDataType;

import java.util.List;

public class HideBossBarsCommand implements Command {
	private static final NamespacedKey hideBossBarsKey = new NamespacedKey(ValhallaMMO.getPlugin(), "bossbars_hidden");
	private final String status_command_bossbars_hidden;
	private final String status_command_bossbars_shown;
	private final String description_command_hidebossbars;

	public HideBossBarsCommand(){
		status_command_bossbars_hidden = TranslationManager.getInstance().getTranslation("status_command_bossbars_hidden");
		status_command_bossbars_shown = TranslationManager.getInstance().getTranslation("status_command_bossbars_shown");
		description_command_hidebossbars = TranslationManager.getInstance().getTranslation("description_command_hidebossbars");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage(Utils.chat("&cOnly players may perform this command for themselves."));
			return true;
		}

		if (toggleBossBars((Player) sender)){
			sender.sendMessage(Utils.chat(status_command_bossbars_shown));
		} else {
			sender.sendMessage(Utils.chat(status_command_bossbars_hidden));
		}
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{};
	}

	@Override
	public String getFailureMessage() {
		return "&4/valhalla toggleexp";
	}

	@Override
	public String getDescription() {
		return description_command_hidebossbars;
	}

	@Override
	public String getCommand() {
		return "/valhalla toggleexp";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}

	public static boolean showBossBars(Player p){
		return !p.getPersistentDataContainer().has(hideBossBarsKey, PersistentDataType.INTEGER);
	}

	public static boolean toggleBossBars(Player p){
		if (p.getPersistentDataContainer().has(hideBossBarsKey, PersistentDataType.INTEGER)){
			p.getPersistentDataContainer().remove(hideBossBarsKey);
			return true;
		} else {
			p.getPersistentDataContainer().set(hideBossBarsKey, PersistentDataType.INTEGER, 1);
			return false;
		}
	}
}
