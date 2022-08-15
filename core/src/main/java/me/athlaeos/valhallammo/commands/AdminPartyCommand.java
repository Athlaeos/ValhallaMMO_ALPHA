package me.athlaeos.valhallammo.commands;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.Party;
import me.athlaeos.valhallammo.managers.PartyManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;

public class AdminPartyCommand implements TabExecutor {
	private final String error_command_invalid_number;
	private final String error_command_player_offline;
	private final String status_command_party_exp_sharing_multiplier_set;
	private final String status_command_party_exp_sharing_radius_set;
	private final String status_command_party_exp_sharing_enabled;
	private final String status_command_party_exp_sharing_disabled;
	private final String status_command_party_item_sharing_enabled;
	private final String status_command_party_item_sharing_disabled;
	private final String status_command_party_player_kicked;
	private final String status_command_party_description_updated;
	private final String status_command_party_leadership_transferred;
	private final String status_command_party_joined;
	private final String status_command_party_disbanded;
	private final String status_command_party_created;
	private final String status_command_party_created_by_admin;

	public AdminPartyCommand(ValhallaMMO plugin) {
		status_command_party_exp_sharing_radius_set = TranslationManager.getInstance().getTranslation("status_command_party_exp_sharing_radius_set");
		error_command_player_offline = TranslationManager.getInstance().getTranslation("error_command_player_offline");
		status_command_party_exp_sharing_multiplier_set = TranslationManager.getInstance().getTranslation("status_command_party_exp_sharing_multiplier_set");
		error_command_invalid_number = TranslationManager.getInstance().getTranslation("error_command_invalid_number");
		status_command_party_exp_sharing_enabled = TranslationManager.getInstance().getTranslation("status_command_party_exp_sharing_enabled");
		status_command_party_exp_sharing_disabled = TranslationManager.getInstance().getTranslation("status_command_party_exp_sharing_disabled");
		status_command_party_item_sharing_enabled = TranslationManager.getInstance().getTranslation("status_command_party_item_sharing_enabled");
		status_command_party_item_sharing_disabled = TranslationManager.getInstance().getTranslation("status_command_party_item_sharing_disabled");
		status_command_party_player_kicked = TranslationManager.getInstance().getTranslation("status_command_party_player_kicked");
		status_command_party_leadership_transferred = TranslationManager.getInstance().getTranslation("status_command_party_leadership_transferred");
		status_command_party_joined = TranslationManager.getInstance().getTranslation("status_command_party_joined");
		status_command_party_description_updated = TranslationManager.getInstance().getTranslation("status_command_party_description_updated");
		status_command_party_disbanded = TranslationManager.getInstance().getTranslation("status_command_party_disbanded");
		status_command_party_created = TranslationManager.getInstance().getTranslation("status_command_party_created");
		status_command_party_created_by_admin = TranslationManager.getInstance().getTranslation("status_command_party_created_by_admin");

		PluginCommand command = plugin.getCommand("parties");
		assert command != null;
		command.setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender s, Command cmd, String name, String[] args) {
		if (!s.hasPermission("valhalla.manageparties")) {
			PartyManager.ErrorStatus.NO_PERMISSION.sendErrorMessage(s);
			return true;
		}
		Player target;
		PartyManager m = PartyManager.getInstance();
		if (args.length > 0){
			Party party = null;
			if (args.length > 1){
				party = PartyManager.getAllParties().get(args[1]);
			}
			if (args[0].equalsIgnoreCase("create")){
				if (args.length > 2){
					target = ValhallaMMO.getPlugin().getServer().getPlayer(args[1]);
					if (target == null) {
						s.sendMessage(Utils.chat(error_command_player_offline));
						return true;
					}
					String partyName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
					PartyManager.ErrorStatus verifyStatus = m.validatePartyName(partyName);
					if (verifyStatus != null){
						verifyStatus.sendErrorMessage(s);
						return true;
					}
					Party newParty = m.createParty(target, partyName);
					PartyManager.ErrorStatus registryStatus = m.registerParty(target, newParty);
					if (registryStatus != null){
						registryStatus.sendErrorMessage(s);
						return true;
					}
					target.sendMessage(Utils.chat(status_command_party_created_by_admin.replace("%party%", partyName)));
					s.sendMessage(Utils.chat(status_command_party_created));
				} else {
					s.sendMessage(Utils.chat("&4/parties create <leader> <partyname>"));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("removemember")){
				if (args.length > 2){
					if (party == null){
						PartyManager.ErrorStatus.PARTY_DOES_NOT_EXIST.sendErrorMessage(s);
						return true;
					}
					Map<String, OfflinePlayer> namesMap = Utils.getPlayersFromUUIDCollection(party.getMembers().keySet());
					OfflinePlayer offlinePlayer = namesMap.get(args[2]);
					if (offlinePlayer == null) {
						PartyManager.ErrorStatus.TARGET_NO_PARTY.sendErrorMessage(s);
						return true;
					}
					UUID uuidTarget = offlinePlayer.getUniqueId();
					if (party.getLeader().equals(uuidTarget)) {
						PartyManager.ErrorStatus.CANNOT_KICK_LEADER.sendErrorMessage(s);
						return true;
					}
					party.getMembers().remove(uuidTarget);
					PartyManager.getPartyMembers().remove(uuidTarget);
					s.sendMessage(Utils.chat(status_command_party_player_kicked));
				} else {
					s.sendMessage(Utils.chat("&4/party removemember <party> <member>"));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("addmember")){
				if (args.length > 1){
					target = ValhallaMMO.getPlugin().getServer().getPlayer(args[2]);
					if (target == null) {
						s.sendMessage(Utils.chat(error_command_player_offline));
						return true;
					}
					if (party == null){
						PartyManager.ErrorStatus.PARTY_DOES_NOT_EXIST.sendErrorMessage(s);
						return true;
					}
					PartyManager.ErrorStatus joinStatus = m.joinParty(target, party);
					if (joinStatus != null){
						joinStatus.sendErrorMessage(s);
						return true;
					}
					s.sendMessage(Utils.chat(status_command_party_joined));
				} else {
					s.sendMessage(Utils.chat("&4/party addmember <party> <member>"));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("setdescription")){
				if (args.length > 2){
					String description = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
					if (party == null){
						PartyManager.ErrorStatus.PARTY_DOES_NOT_EXIST.sendErrorMessage(s);
						return true;
					}
					party.setDescription(m.isAllowPartyDescriptionColorCodes() ? Utils.chat(description) : description);
					s.sendMessage(Utils.chat(status_command_party_description_updated));
				} else {
					s.sendMessage(Utils.chat("&4/party setdescription <party> <description>"));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("transferleadership")){
				if (args.length > 2){
					Map<String, OfflinePlayer> namesMap = Utils.getPlayersFromUUIDCollection(party.getMembers().keySet());
					OfflinePlayer offlinePlayer = namesMap.get(args[2]);
					if (offlinePlayer == null) {
						PartyManager.ErrorStatus.NOT_IN_SAME_PARTY.sendErrorMessage(s);
						return true;
					}
					UUID uuidTarget = offlinePlayer.getUniqueId();
					party.setLeader(uuidTarget);
					s.sendMessage(Utils.chat(status_command_party_leadership_transferred));
				} else {
					s.sendMessage(Utils.chat("&4/party transferleadership <party> <newleader>"));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("delete")){
				if (args.length > 1){
					if (party == null){
						PartyManager.ErrorStatus.PARTY_DOES_NOT_EXIST.sendErrorMessage(s);
						return true;
					}
					for (UUID member : new HashSet<>(party.getMembers().keySet())){
						party.getMembers().remove(member);
						PartyManager.getPartyMembers().remove(member);
						Player onlinePlayer = ValhallaMMO.getPlugin().getServer().getPlayer(member);
						if (onlinePlayer != null){
							onlinePlayer.sendMessage(Utils.chat(status_command_party_disbanded));
						}
					}
					PartyManager.getAllParties().remove(party.getName());
					s.sendMessage(Utils.chat(status_command_party_disbanded));
				} else {
					s.sendMessage(Utils.chat("&4/party delete <party>"));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("toggleexpsharingunlocked")){
				if (args.length > 1){
					if (party == null){
						PartyManager.ErrorStatus.PARTY_DOES_NOT_EXIST.sendErrorMessage(s);
						return true;
					}
					party.setUnlockedEXPSharing(!party.isUnlockedEXPSharing());
					if (party.isUnlockedEXPSharing()){
						s.sendMessage(Utils.chat(status_command_party_exp_sharing_enabled));
					} else {
						s.sendMessage(Utils.chat(status_command_party_exp_sharing_disabled));
					}
				} else {
					s.sendMessage(Utils.chat("&4/party toggleexpsharingunlocked <party>"));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("toggleitemsharingunlocked")){
				if (args.length > 1){
					if (party == null){
						PartyManager.ErrorStatus.PARTY_DOES_NOT_EXIST.sendErrorMessage(s);
						return true;
					}
					party.setUnlockedItemSharing(!party.isUnlockedItemSharing());
					if (party.isUnlockedItemSharing()){
						s.sendMessage(Utils.chat(status_command_party_item_sharing_enabled));
					} else {
						s.sendMessage(Utils.chat(status_command_party_item_sharing_disabled));
					}
				} else {
					s.sendMessage(Utils.chat("&4/party toggleitemsharingunlocked <party>"));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("expsharingmultiplieradd")){
				if (args.length > 2){
					try {
						if (party == null){
							PartyManager.ErrorStatus.PARTY_DOES_NOT_EXIST.sendErrorMessage(s);
							return true;
						}
						float newMultiplier = Math.max(0, party.getEXPSharingMultiplier() + Float.parseFloat(args[2]));
						party.setEXPSharingMultiplier(newMultiplier);
						s.sendMessage(Utils.chat(status_command_party_exp_sharing_multiplier_set.replace("%value%", "" + newMultiplier)));
					} catch (IllegalArgumentException ignored){
						s.sendMessage(Utils.chat(error_command_invalid_number));
					}
				} else {
					s.sendMessage(Utils.chat("&4/party expsharingmultiplieradd <party> <amount>"));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("expsharingmultiplierset")){
				if (args.length > 2){
					try {
						if (party == null){
							PartyManager.ErrorStatus.PARTY_DOES_NOT_EXIST.sendErrorMessage(s);
							return true;
						}
						float newMultiplier = Math.max(0, Float.parseFloat(args[2]));
						party.setEXPSharingMultiplier(newMultiplier);
						s.sendMessage(Utils.chat(status_command_party_exp_sharing_multiplier_set.replace("%value%", "" + newMultiplier)));
					} catch (IllegalArgumentException ignored){
						s.sendMessage(Utils.chat(error_command_invalid_number));
					}
				} else {
					s.sendMessage(Utils.chat("&4/party expsharingmultiplierset <party> <amount>"));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("expsharingradiusadd")){
				if (args.length > 2){
					try {
						if (party == null){
							PartyManager.ErrorStatus.PARTY_DOES_NOT_EXIST.sendErrorMessage(s);
							return true;
						}
						int newRadius = Math.max(0, party.getEXPSharingRadius() + Integer.parseInt(args[2]));
						party.setEXPSharingRadius(newRadius);
						s.sendMessage(Utils.chat(status_command_party_exp_sharing_radius_set.replace("%value%", "" + newRadius)));
					} catch (IllegalArgumentException ignored){
						s.sendMessage(Utils.chat(error_command_invalid_number));
					}
				} else {
					s.sendMessage(Utils.chat("&4/party expsharingradiusadd <party> <amount>"));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("expsharingradiusset")){
				if (args.length > 2){
					try {
						if (party == null){
							PartyManager.ErrorStatus.PARTY_DOES_NOT_EXIST.sendErrorMessage(s);
							return true;
						}
						int newRadius = Math.max(0, Integer.parseInt(args[2]));
						party.setEXPSharingRadius(newRadius);
						s.sendMessage(Utils.chat(status_command_party_exp_sharing_radius_set.replace("%value%", "" + newRadius)));
					} catch (IllegalArgumentException ignored){
						s.sendMessage(Utils.chat(error_command_invalid_number));
					}
				} else {
					s.sendMessage(Utils.chat("&4/party expsharingradiusset <party> <amount>"));
				}
				return true;
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (!sender.hasPermission("valhalla.manageparties")) return new ArrayList<>();
		if (args.length == 1){
			return Arrays.asList("removemember", "addmember", "setdescription", "transferleadership", "create", "delete", "toggleexpsharingunlocked", "toggleitemsharingunlocked", "expsharingmultiplieradd", "expsharingmultiplierset", "expsharingradiusadd", "expsharingradiusset");
		} else if (args.length == 2){
			if (Arrays.asList("removemember", "addmember", "setdescription", "transferleadership", "delete", "toggleexpsharingunlocked", "toggleitemsharingunlocked", "expsharingmultiplieradd", "expsharingmultiplierset", "expsharingradiusadd", "expsharingradiusset").contains(args[0])){
				return PartyManager.getAllParties().values().stream().map(Party::getName).collect(Collectors.toList());
			}
		} else if (args.length == 3){
			switch(args[0].toLowerCase()){
				case "removemember":{
					Party p = PartyManager.getAllParties().get(args[1]);
					if (p == null) return Collections.singletonList("member");
					Map<String, OfflinePlayer> namesMap = Utils.getPlayersFromUUIDCollection(p.getMembers().keySet());
					return new ArrayList<>(namesMap.keySet());
				}
				case "addmember":{
					return ValhallaMMO.getPlugin().getServer().getOnlinePlayers().stream().filter(player -> !PartyManager.getPartyMembers().containsKey(player.getUniqueId())).map(Player::getName).collect(Collectors.toList());
				}
				case "transferleadership":
					return Collections.singletonList("newleader");
				case "expsharingmultiplieradd":
				case "expsharingmultiplierset":
				case "expsharingradiusadd":
				case "expsharingradiusset":
					return Collections.singletonList("amount");
			}
		}
		return null;
	}
}
