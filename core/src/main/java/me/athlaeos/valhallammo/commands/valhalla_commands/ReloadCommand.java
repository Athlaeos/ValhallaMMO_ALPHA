package me.athlaeos.valhallammo.commands.valhalla_commands;

import me.athlaeos.valhallammo.commands.Command;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.listeners.EntityDamagedListener;
import me.athlaeos.valhallammo.listeners.InteractListener;
import me.athlaeos.valhallammo.listeners.JoinLeaveListener;
import me.athlaeos.valhallammo.listeners.VillagerInteractListener;
import me.athlaeos.valhallammo.loottables.LootManager;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand implements Command {
	private final String description_command_reload;
	private final String warning_command_reload;
	private final String status_command_reload_executed;

	public ReloadCommand(){
		description_command_reload = TranslationManager.getInstance().getTranslation("description_command_reload");
		warning_command_reload = TranslationManager.getInstance().getTranslation("warning_command_reload");
		status_command_reload_executed = TranslationManager.getInstance().getTranslation("status_command_reload_executed");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (args.length == 1){
			sender.sendMessage(Utils.chat(warning_command_reload));
			return true;
		} else if (args.length > 1){
			if (args[1].equalsIgnoreCase("confirm")){
				CustomRecipeManager.getInstance().saveRecipes(false);
				LootManager.getInstance().saveLootTables();

//				for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
//					// attempting to reload all skills configs, if
//					ConfigManager.getInstance().getConfig("skill_" + s.getType().toLowerCase() + ".yml").reload();
//					ConfigManager.getInstance().getConfig("progression_" + s.getType().toLowerCase() + ".yml").reload();
//				}

				for (String config : ConfigManager.getInstance().getConfigs().keySet()){
					ConfigManager.getInstance().getConfig(config).reload();
					ConfigManager.getInstance().getConfig(config).save();
				}

				TutorialBook.getTutorialBookInstance().reload();
				MaterialCosmeticManager.getInstance().reload();
				SkillProgressionManager.getInstance().reload();
				SmithingItemTreatmentManager.getInstance().reload();
				EnchantingItemEnchantmentsManager.getInstance().reload();
				AlchemyPotionTreatmentManager.getInstance().reload();
				EntityDamagedListener.getListener().reload();
				InteractListener.getListener().reload();
				JoinLeaveListener.getListener().reload();
				VillagerInteractListener.getListener().reload();
				BlockConversionManager.getInstance().reload();
				CustomDurabilityManager.getInstance().reload();
				CustomRecipeManager.getInstance().loadRecipesAsync();
				CustomRecipeManager.getInstance().disableRecipes();
				LootManager.getInstance().loadLootTables();
				PotionAttributesManager.getInstance().reload();
				PotionEffectManager.getInstance().reload();
				ProfileVersionManager.getInstance().reload();
				TranslationManager.getInstance().reload();
				TransmutationManager.getInstance().reload();
				ProfileStatsCommand.getInstance().reload();

				Utils.sendMessage(sender, Utils.chat(status_command_reload_executed));
			}
		}
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.reload"};
	}

	@Override
	public String getFailureMessage() {
		return "&c/valhalla reload";
	}

	@Override
	public String getDescription() {
		return description_command_reload;
	}

	@Override
	public String getCommand() {
		return "/valhalla reload";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}
}
