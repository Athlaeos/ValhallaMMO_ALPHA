package me.athlaeos.valhallammo.commands;

import me.athlaeos.valhallammo.Main;
import me.athlaeos.valhallammo.configs.ConfigManager;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.dom.SkillType;
import me.athlaeos.valhallammo.managers.ProfileUtil;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.materials.MaterialClass;
import me.athlaeos.valhallammo.skills.smithing.SmithingSkill;
import me.athlaeos.valhallammo.skills.smithing.dom.SmithingProfile;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TempStatsCommand implements Command {
	private final String playerNotFound;
	private final Map<SkillType, List<String>> profileFormats = new HashMap<>();

	public TempStatsCommand(){
		playerNotFound = ConfigManager.getInstance().getConfig("messages.yml").get().getString("error_player_offline");
	}

	@Override
	public boolean execute(CommandSender sender, String[] args) {
		if (!(sender instanceof Player)){
			sender.sendMessage("&cOnly players can do this");
			return true;
		}
		Player target = (Player) sender;
		if (args.length == 2){
			target = Main.getPlugin().getServer().getPlayer(args[1]);
			if (target == null){
				sender.sendMessage(Utils.chat(playerNotFound));
				return true;
			}
		}
		Profile profile = ProfileUtil.getProfile(target, SkillType.SMITHING);
		if (profile != null){
			if (profile instanceof SmithingProfile){
				SmithingProfile smithingProfile = (SmithingProfile) profile;
				SmithingSkill skill = (SmithingSkill) SkillProgressionManager.getInstance().getSkill(SkillType.SMITHING);
				int nextLevelEXP = ((int) skill.expForNextlevel(smithingProfile.getLevel() + 1));
				sender.sendMessage(Utils.chat("&e" + target.getName() + "'s smithing"));
				sender.sendMessage(Utils.chat("&7Level: &e" + smithingProfile.getLevel()));
				sender.sendMessage(Utils.chat("&7Progress: &e" + Utils.round(smithingProfile.getExp(), 2) + "/" + ((nextLevelEXP == -1) ? "MAX" : nextLevelEXP)));
				sender.sendMessage(Utils.chat("&7Total Experience: &e" + smithingProfile.getLifetimeEXP()));
				sender.sendMessage(Utils.chat("&7Skills:"));
				sender.sendMessage(Utils.chat("&7General: &e" + smithingProfile.getGeneralCraftingQuality()));
				sender.sendMessage(Utils.chat("&#695031Wood: &e" + smithingProfile.getCraftingQuality(MaterialClass.WOOD)));
				sender.sendMessage(Utils.chat("&#695031Bow: &e" + smithingProfile.getCraftingQuality(MaterialClass.BOW)));
				sender.sendMessage(Utils.chat("&#695031Crossbow: &e" + smithingProfile.getCraftingQuality(MaterialClass.CROSSBOW)));
				sender.sendMessage(Utils.chat("&#695031Leather: &e" + smithingProfile.getCraftingQuality(MaterialClass.LEATHER)));
				sender.sendMessage(Utils.chat("&7Stone: &e" + smithingProfile.getCraftingQuality(MaterialClass.STONE)));
				sender.sendMessage(Utils.chat("&7Chainmail: &e" + smithingProfile.getCraftingQuality(MaterialClass.CHAINMAIL)));
				sender.sendMessage(Utils.chat("&eGold: &e" + smithingProfile.getCraftingQuality(MaterialClass.GOLD)));
				sender.sendMessage(Utils.chat("&7Iron: &e" + smithingProfile.getCraftingQuality(MaterialClass.IRON)));
				sender.sendMessage(Utils.chat("&9Prismarine: &e" + smithingProfile.getCraftingQuality(MaterialClass.PRISMARINE)));
				sender.sendMessage(Utils.chat("&bDiamond: &e" + smithingProfile.getCraftingQuality(MaterialClass.DIAMOND)));
				sender.sendMessage(Utils.chat("&5Alien: &e" + smithingProfile.getCraftingQuality(MaterialClass.MEMBRANE)));
				sender.sendMessage(Utils.chat("&8Netherite: &e" + smithingProfile.getCraftingQuality(MaterialClass.NETHERITE)));
				return true;
			}
		}
		sender.sendMessage(Utils.chat("&cNo Profile Found"));
		return true;
	}

	@Override
	public String[] getRequiredPermission() {
		return new String[]{"valhalla.profile" , "valhalla.profile.other"};
	}

	@Override
	public String getFailureMessage() {
		return "&4/valhalla profile <name>";
	}

	@Override
	public String getCommand() {
		return "/valhalla profile <name>";
	}

	@Override
	public String getDescription() {
		return "Temporary command used to view your own or someone else's progress for SMITHING, as well as showing you all skill quality scores";
	}

	@Override
	public List<String> getSubcommandArgs(CommandSender sender, String[] args) {
		return null;
	}

	private void registerSkillProfileFormats(){
		for (SkillType t : SkillType.values()){
			List<String> format = TranslationManager.getInstance().getList("profile_format_" + t.toString().toLowerCase());
			if (format == null) format = new ArrayList<>();
			profileFormats.put(t, format);
		}
	}
}
