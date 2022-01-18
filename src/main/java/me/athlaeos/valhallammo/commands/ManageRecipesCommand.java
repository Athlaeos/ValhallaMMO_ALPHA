package me.athlaeos.valhallammo.commands;

import me.athlaeos.valhallammo.configs.ConfigManager;
import me.athlaeos.valhallammo.menus.CreateCustomRecipeMenu;
import me.athlaeos.valhallammo.menus.PlayerMenuUtilManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class GetEnchantMenuCommand implements Command {
	private String menu_description;

	public GetEnchantMenuCommand(){
		menu_description = Utils.chat("&7description");//ConfigManager.getInstance().getConfig("translations.yml").get().getString("menu_description");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		new CreateCustomRecipeMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility((Player) sender)).open();

		return false;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.recipes"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/valhalla recipes";
	}

	@Override
	public String[] getHelpEntry() {
		return new String[]{
				Utils.chat("&8&m                                             "),
				Utils.chat("&d/valhalla recipes"),
				Utils.chat("&7" + menu_description),
				Utils.chat("&7> &dvalhalla.recipes")
		};
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}
}
