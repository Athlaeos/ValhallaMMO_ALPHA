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

public class PartyCommand implements TabExecutor {
	private final String status_command_party_invalid_rank;
	private final String error_command_player_offline;
	private final String error_command_player_required;
	private final String status_command_party_created;
	private final String status_command_party_open;
	private final String status_command_party_closed;
	private final String status_command_party_exp_sharing_enabled;
	private final String status_command_party_exp_sharing_disabled;
	private final String status_command_party_item_sharing_enabled;
	private final String status_command_party_item_sharing_disabled;
	private final String status_command_party_invite_sent;
	private final String status_command_party_player_kicked;
	private final String status_command_party_leadership_transferred;
	private final String status_command_party_rank_changed;
	private final String status_command_party_joined;
	private final String status_command_party_left;
	private final String status_command_party_item_shared;
	private final String status_command_party_invites_muted;
	private final String status_command_party_invites_unmuted;
	private final String status_command_party_description_updated;

	public PartyCommand(ValhallaMMO plugin) {
		status_command_party_created = TranslationManager.getInstance().getTranslation("status_command_party_created");
		error_command_player_offline = TranslationManager.getInstance().getTranslation("error_command_player_offline");
		error_command_player_required = TranslationManager.getInstance().getTranslation("error_command_player_required");
		status_command_party_invalid_rank = TranslationManager.getInstance().getTranslation("status_command_party_invalid_rank");
		status_command_party_open = TranslationManager.getInstance().getTranslation("status_command_party_open");
		status_command_party_closed = TranslationManager.getInstance().getTranslation("status_command_party_closed");
		status_command_party_exp_sharing_enabled = TranslationManager.getInstance().getTranslation("status_command_party_exp_sharing_enabled");
		status_command_party_exp_sharing_disabled = TranslationManager.getInstance().getTranslation("status_command_party_exp_sharing_disabled");
		status_command_party_item_sharing_enabled = TranslationManager.getInstance().getTranslation("status_command_party_item_sharing_enabled");
		status_command_party_item_sharing_disabled = TranslationManager.getInstance().getTranslation("status_command_party_item_sharing_disabled");
		status_command_party_invite_sent = TranslationManager.getInstance().getTranslation("status_command_party_invite_sent");
		status_command_party_player_kicked = TranslationManager.getInstance().getTranslation("status_command_party_player_kicked");
		status_command_party_leadership_transferred = TranslationManager.getInstance().getTranslation("status_command_party_leadership_transferred");
		status_command_party_rank_changed = TranslationManager.getInstance().getTranslation("status_command_party_rank_changed");
		status_command_party_joined = TranslationManager.getInstance().getTranslation("status_command_party_joined");
		status_command_party_left = TranslationManager.getInstance().getTranslation("status_command_party_left");
		status_command_party_item_shared = TranslationManager.getInstance().getTranslation("status_command_party_item_shared");
		status_command_party_invites_muted = TranslationManager.getInstance().getTranslation("status_command_party_invites_muted");
		status_command_party_invites_unmuted = TranslationManager.getInstance().getTranslation("status_command_party_invites_unmuted");
		status_command_party_description_updated = TranslationManager.getInstance().getTranslation("status_command_party_description_updated");

		PluginCommand command = plugin.getCommand("party");
		assert command != null;
		command.setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender s, org.bukkit.command.Command cmd, String name, String[] args) {
		if (!(s instanceof Player)) return true;
		Player sender = (Player) s;
		Player target = null;
		PartyManager m = PartyManager.getInstance();
		if (args.length > 0){
			if (args[0].equalsIgnoreCase("create")){
				if (args.length > 1) {
					String partyName = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
					PartyManager.ErrorStatus verifyStatus = m.validatePartyName(partyName);
					if (verifyStatus != null) {
						verifyStatus.sendErrorMessage(sender);
						return true;
					}
					PartyManager.ErrorStatus registerStatus = m.registerParty(sender, m.createParty(sender, partyName));
					if (registerStatus == null) {
						sender.sendMessage(Utils.chat(status_command_party_created));
					} else {
						registerStatus.sendErrorMessage(sender);
						return true;
					}
				} else {
					sender.sendMessage(Utils.chat("&4/party create <name>"));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("disband")){
				PartyManager.ErrorStatus disbandStatus = m.disbandParty(sender);
				if (disbandStatus != null) {
					disbandStatus.sendErrorMessage(sender);
					return true;
				}
				return true;
			} else if (args[0].equalsIgnoreCase("info")){
				PartyManager.ErrorStatus partyInfoStatus = m.displayPartyInfo(sender);
				if (partyInfoStatus != null){
					partyInfoStatus.sendErrorMessage(sender);
					return true;
				}
				return true;
			} else if (args[0].equalsIgnoreCase("setdescription")){
				if (args.length > 1){
					String description = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
					PartyManager.ErrorStatus verifyStatus = m.verifyDescription(description);
					if (verifyStatus != null){
						verifyStatus.sendErrorMessage(sender);
						return true;
					}
					PartyManager.ErrorStatus updateStatus = m.changeDescription(sender, description);
					if (updateStatus != null){
						updateStatus.sendErrorMessage(sender);
						return true;
					}
				} else {
					PartyManager.ErrorStatus verifyStatus = m.verifyDescription("");
					if (verifyStatus != null){
						verifyStatus.sendErrorMessage(sender);
						return true;
					}
					PartyManager.ErrorStatus updateStatus = m.changeDescription(sender, "");
					if (updateStatus != null){
						updateStatus.sendErrorMessage(sender);
						return true;
					}
				}
				sender.sendMessage(Utils.chat(status_command_party_description_updated));
				return true;
			} else if (args[0].equalsIgnoreCase("invite")){
				if (args.length > 1){
					target = ValhallaMMO.getPlugin().getServer().getPlayer(args[1]);
					if (target == null){
						sender.sendMessage(Utils.chat(error_command_player_offline));
						return true;
					}
					PartyManager.ErrorStatus inviteStatus = m.inviteMember(sender, target);
					if (inviteStatus != null){
						inviteStatus.sendErrorMessage(sender);
						return true;
					}
					sender.sendMessage(Utils.chat(status_command_party_invite_sent));
					return true;
				} else {
					sender.sendMessage(Utils.chat(error_command_player_required));
				}
			} else if (args[0].equalsIgnoreCase("kick")){
				if (args.length > 1) {
					Party party = PartyManager.getInstance().getParty(sender);
					if (party == null) {
						PartyManager.ErrorStatus.NOT_IN_PARTY.sendErrorMessage(sender);
						return true;
					}
					Map<String, OfflinePlayer> namesMap = Utils.getPlayersFromUUIDCollection(party.getMembers().keySet());
					OfflinePlayer offlinePlayer = namesMap.get(args[1]);
					if (offlinePlayer == null) {
						sender.sendMessage(Utils.chat(error_command_player_offline));
						return true;
					}
					UUID uuidTarget = offlinePlayer.getUniqueId();
					PartyManager.ErrorStatus kickStatus = m.kickMember(sender, uuidTarget);
					if (kickStatus != null) {
						kickStatus.sendErrorMessage(sender);
						return true;
					}
					sender.sendMessage(Utils.chat(status_command_party_player_kicked));
				} else {
					sender.sendMessage(Utils.chat(error_command_player_required));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("transferleader")){
				if (args.length > 1) {
					target = ValhallaMMO.getPlugin().getServer().getPlayer(args[1]);
					if (target == null) {
						sender.sendMessage(Utils.chat(error_command_player_offline));
						return true;
					}
					PartyManager.ErrorStatus changeLeaderStatus = m.changeLeader(sender, target);
					if (changeLeaderStatus != null) {
						changeLeaderStatus.sendErrorMessage(sender);
						return true;
					}
					sender.sendMessage(Utils.chat(status_command_party_leadership_transferred));
				} else {
					sender.sendMessage(Utils.chat(error_command_player_required));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("setrank")){
				if (args.length > 2) {
					target = ValhallaMMO.getPlugin().getServer().getPlayer(args[1]);
					if (target == null) {
						sender.sendMessage(Utils.chat(error_command_player_offline));
						return true;
					}
					Party.PermissionGroup permissionGroup = m.getPermissionGroups().get(args[2]);
					if (permissionGroup == null){
						sender.sendMessage(Utils.chat(status_command_party_invalid_rank));
						return true;
					}
					PartyManager.ErrorStatus rankStatus = m.setPermissionGroup(sender, target, permissionGroup);
					if (rankStatus != null) {
						rankStatus.sendErrorMessage(sender);
						return true;
					}
					sender.sendMessage(Utils.chat(status_command_party_rank_changed));
				} else {
					sender.sendMessage(Utils.chat("&4/party setrank <player> <rank>"));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("join")){
				if (args.length > 1) {
					PartyManager.ErrorStatus inviteStatus = m.acceptInvite(sender, args[1]);
					if (inviteStatus != null) {
						inviteStatus.sendErrorMessage(sender);
						return true;
					}
					sender.sendMessage(Utils.chat(status_command_party_joined));
					return true;
				} else {
					sender.sendMessage(Utils.chat("&4/party join <party>"));
				}
			} else if (args[0].equalsIgnoreCase("leave")){
				PartyManager.ErrorStatus inviteStatus = m.leaveParty(sender);
				if (inviteStatus != null) {
					inviteStatus.sendErrorMessage(sender);
					return true;
				}
				sender.sendMessage(Utils.chat(status_command_party_left));
				return true;
			} else if (args[0].equalsIgnoreCase("toggleexpsharing")){
				PartyManager.ErrorStatus inviteStatus = m.toggleEXPSharing(sender);
				if (inviteStatus != null) {
					inviteStatus.sendErrorMessage(sender);
					return true;
				}
				Party party = PartyManager.getInstance().getParty(sender);
				if (party == null) {
					PartyManager.ErrorStatus.NOT_IN_PARTY.sendErrorMessage(sender);
					return true;
				}
				if (party.isEnabledEXPSharing()){
					sender.sendMessage(Utils.chat(status_command_party_exp_sharing_enabled));
				} else {
					sender.sendMessage(Utils.chat(status_command_party_exp_sharing_disabled));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("toggleitemsharing")){
				PartyManager.ErrorStatus inviteStatus = m.toggleItemSharing(sender);
				if (inviteStatus != null) {
					inviteStatus.sendErrorMessage(sender);
					return true;
				}
				Party party = PartyManager.getInstance().getParty(sender);
				if (party == null) {
					PartyManager.ErrorStatus.NOT_IN_PARTY.sendErrorMessage(sender);
					return true;
				}
				if (party.isEnabledItemSharing()){
					sender.sendMessage(Utils.chat(status_command_party_item_sharing_enabled));
				} else {
					sender.sendMessage(Utils.chat(status_command_party_item_sharing_disabled));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("toggleopenstatus")){
				PartyManager.ErrorStatus inviteStatus = m.togglePartyOpenStatus(sender);
				if (inviteStatus != null) {
					inviteStatus.sendErrorMessage(sender);
					return true;
				}
				Party party = PartyManager.getInstance().getParty(sender);
				if (party == null) {
					PartyManager.ErrorStatus.NOT_IN_PARTY.sendErrorMessage(sender);
					return true;
				}
				if (party.isOpen()){
					sender.sendMessage(Utils.chat(status_command_party_open));
				} else {
					sender.sendMessage(Utils.chat(status_command_party_closed));
				}
				return true;
			} else if (args[0].equalsIgnoreCase("shareitem")){
				if (args.length > 1) {
					target = ValhallaMMO.getPlugin().getServer().getPlayer(args[1]);
					if (target == null) {
						sender.sendMessage(Utils.chat(error_command_player_offline));
						return true;
					}
					PartyManager.ErrorStatus changeLeaderStatus = m.shareItem(sender, target);
					if (changeLeaderStatus != null) {
						changeLeaderStatus.sendErrorMessage(sender);
						return true;
					}
					sender.sendMessage(Utils.chat(status_command_party_item_shared));
					return true;
				} else {
					sender.sendMessage(Utils.chat(error_command_player_required));
				}
			} else if (args[0].equalsIgnoreCase("toggleinvites")){
				if (PartyManager.getInstance().toggleInviteMute(sender)){
					sender.sendMessage(Utils.chat(status_command_party_invites_muted));
				} else {
					sender.sendMessage(Utils.chat(status_command_party_invites_unmuted));
				}
			}
		} else {
			PartyManager.ErrorStatus partyInfoStatus = m.displayPartyInfo(sender);
			if (partyInfoStatus != null){
				partyInfoStatus.sendErrorMessage(sender);
				return true;
			}
		}
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		if (args.length == 1){
			return Arrays.asList("toggleinvites", "disband", "create", "invite", "join", "leave", "kick", "shareitem", "info", "setdescription", "transferleader", "setrank", "toggleopenstatus", "toggleexpsharing", "toggleitemsharing");
		} else if (args.length == 2){
			if (args[0].equalsIgnoreCase("join")){
				if (sender instanceof Player){
					List<String> parties = new ArrayList<>(PartyManager.getInstance().getPartyInvites().getOrDefault(((Player) sender).getUniqueId(), new HashSet<>()));
					parties.addAll(PartyManager.getAllParties().values().stream().filter(Party::isOpen).map(Party::getName).collect(Collectors.toList()));
					return parties;
				}
			}
		} else if (args.length == 3){
			if (args[0].equalsIgnoreCase("setrank")) {
				return new ArrayList<>(PartyManager.getInstance().getPermissionGroups().keySet());
			}
		}
		return null;
	}
}
