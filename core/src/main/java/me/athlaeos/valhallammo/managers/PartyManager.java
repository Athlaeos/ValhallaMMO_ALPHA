package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Party;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import java.util.*;
import java.util.regex.Pattern;

public class PartyManager {
    private static PartyManager manager = null;
    private final String translation_false;
    private final String translation_true;
    private final String inviteMessage;
    private final String memberJoinedMessage;
    private final String removedFromPartyMessage;

    private final String partyDisbanded;
    private final String itemReceived;

    // Party names, chat, and description rules\
    private static final Pattern legalPartyNamePattern = Pattern.compile("^[ a-zA-Z0-9&]+$"); // letters, numbers, and & only
    private final int partyNameCharacterLimit;
    private final int partyNameCharacterMinimum;
    private final int partyDescriptionCharacterLimit;
    private final String partyChatFormat;
    private final String partySpyChatFormat;
    private final List<String> partyInfoFormat;
    private final boolean allowPartyNameColorCodes;
    private final boolean allowPartyDescriptionColorCodes;
    private final String leaderTitle;

    // Party global rules
    private final boolean enableParties;
    private final boolean enableEXPSharing;
    private final boolean enableItemSharing;
    private final long partyCreationCooldown;
    private final long itemShareCooldown;

    private final Map<String, Party.PermissionGroup> permissionGroups = new HashMap<>();

    private final Collection<UUID> playersInPartyChat = new HashSet<>();

    private final Map<UUID, Collection<String>> partyInvites = new HashMap<>();

    private static final Map<String, Party> allParties = new HashMap<>();

    private static final Map<UUID, Party> partyMembers = new HashMap<>();

    // Party default properties
    private final int defaultMemberLimit;
    private final int defaultEXPSharingRadius;
    private final boolean defaultUnlockedEXPSharing;
    private final boolean defaultUnlockedItemSharing;
    private final float defaultEXPSharingMultiplier;
    private Party.PermissionGroup defaultPartyPermission;

    public void loadParties(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("parties.yml").get();
        ConfigurationSection configSection = config.getConfigurationSection("");
        if (configSection != null){
            for (String party : configSection.getKeys(false)){
                String displayName = config.getString(party + ".display_name");
                if (displayName == null) {
                    ValhallaMMO.getPlugin().getServer().getLogger().warning("Party " + party + " does not have a display name! Could not create party");
                    continue;
                }
                String description = config.getString(party + ".description");
                String leaderString = config.getString(party + ".leader");
                if (leaderString == null) {
                    ValhallaMMO.getPlugin().getServer().getLogger().warning("Party " + party + " does not have a leader! Could not create party");
                    continue;
                }
                UUID leader = UUID.fromString(leaderString);
                boolean unlockedItemSharing = config.getBoolean(party + ".unlocked_item_sharing");
                boolean unlockedEXPSharing = config.getBoolean(party + ".unlocked_exp_sharing");
                boolean enabledItemSharing = config.getBoolean(party + ".enabled_item_sharing");
                boolean enabledEXPSharing = config.getBoolean(party + ".enabled_exp_sharing");
                int memberLimit = config.getInt(party + ".member_limit");
                int EXPShareRadius = config.getInt(party + ".exp_share_radius");
                float EXPShareMultiplier = (float) config.getDouble(party + ".exp_sharing_multiplier");
                boolean isOpen = config.getBoolean(party + ".open");
                Map<UUID, Party.PermissionGroup> members = new HashMap<>();
                ConfigurationSection memberSection = config.getConfigurationSection(party + ".members");
                if (memberSection != null){
                    for (String uuid : memberSection.getKeys(false)){
                        UUID member = UUID.fromString(uuid);
                        String rank = config.getString(party + ".members." + uuid);
                        Party.PermissionGroup permissionGroup = permissionGroups.get(rank);
                        if (permissionGroup == null) permissionGroup = defaultPartyPermission;
                        members.put(member, permissionGroup);
                    }
                }

                Party finalParty = new Party(party, displayName, leader);
                finalParty.setDescription(description);
                finalParty.setUnlockedItemSharing(unlockedItemSharing);
                finalParty.setUnlockedEXPSharing(unlockedEXPSharing);
                finalParty.setEnabledItemSharing(enabledItemSharing);
                finalParty.setEnabledEXPSharing(enabledEXPSharing);
                finalParty.setMemberLimit(memberLimit);
                finalParty.setEXPSharingRadius(EXPShareRadius);
                finalParty.setOpen(isOpen);
                finalParty.setEXPSharingMultiplier(EXPShareMultiplier);
                finalParty.getMembers().putAll(members);
                allParties.put(party, finalParty);

                for (UUID member : members.keySet()){
                    partyMembers.put(member, finalParty);
                }
            }
        }
    }

    public void saveParties(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("parties.yml").get();
        ConfigurationSection allEntries = config.getConfigurationSection("");
        if (allEntries != null){
            for (String entry : allEntries.getKeys(false)){
                config.set(entry, null);
            }
        }
        for (Party p : allParties.values()){
            config.set(p.getName() + ".display_name", p.getDisplayName());
            config.set(p.getName() + ".description", p.getDescription());
            config.set(p.getName() + ".leader", p.getLeader().toString());
            config.set(p.getName() + ".unlocked_item_sharing", p.isUnlockedItemSharing());
            config.set(p.getName() + ".unlocked_exp_sharing", p.isUnlockedEXPSharing());
            config.set(p.getName() + ".enabled_item_sharing", p.isEnabledItemSharing());
            config.set(p.getName() + ".enabled_exp_sharing", p.isEnabledEXPSharing());
            config.set(p.getName() + ".member_limit", p.getMemberLimit());
            config.set(p.getName() + ".exp_share_radius", p.getEXPSharingRadius());
            config.set(p.getName() + ".exp_sharing_multiplier", p.getEXPSharingMultiplier());
            config.set(p.getName() + ".open", p.isOpen());

            for (UUID member : p.getMembers().keySet()){
                Party.PermissionGroup group = p.getMembers().get(member);
                config.set(p.getName() + ".members." + member.toString(), group == null ? null : group.getName());
            }
        }
        ConfigManager.getInstance().saveConfig("parties.yml");
    }

    private void addInvitedPlayer(Player player, Party party){
        Collection<String> invites = partyInvites.getOrDefault(player.getUniqueId(), new HashSet<>());
        invites.add(party.getName());
        partyInvites.put(player.getUniqueId(), invites);
    }

    public String getPartySpyChatFormat() {
        return partySpyChatFormat;
    }

    public PartyManager(){
        partyDisbanded = TranslationManager.getInstance().getTranslation("status_command_party_disbanded");
        itemReceived = TranslationManager.getInstance().getTranslation("status_command_party_item_received");
        translation_false = TranslationManager.getInstance().getTranslation("translation_false");
        translation_true = TranslationManager.getInstance().getTranslation("translation_true");
        inviteMessage = TranslationManager.getInstance().getTranslation("status_command_party_invite_received");
        memberJoinedMessage = TranslationManager.getInstance().getTranslation("status_command_party_member_joined");
        removedFromPartyMessage = TranslationManager.getInstance().getTranslation("status_command_party_member_kicked");

        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        partyNameCharacterLimit = config.getInt("name_character_limit");
        partyNameCharacterMinimum = config.getInt("name_character_minimum");
        partyDescriptionCharacterLimit = config.getInt("description_character_limit");
        partyChatFormat = config.getString("party_chat_format");
        partySpyChatFormat = config.getString("party_spy_chat_format");
        partyInfoFormat = config.getStringList("party_info");
        allowPartyNameColorCodes = config.getBoolean("name_allow_colors");
        allowPartyDescriptionColorCodes = config.getBoolean("description_allow_colors");
        leaderTitle = config.getString("leader_title", "");

        enableParties = config.getBoolean("parties");
        enableEXPSharing = config.getBoolean("exp_sharing");
        enableItemSharing = config.getBoolean("item_sharing");
        itemShareCooldown = config.getLong("item_share_cooldown");
        partyCreationCooldown = config.getLong("party_creation_cooldown");

        defaultMemberLimit = config.getInt("party_capacity_default");
        defaultEXPSharingRadius = config.getInt("exp_sharing_radius");
        defaultUnlockedEXPSharing = config.getBoolean("exp_sharing_default");
        defaultUnlockedItemSharing = config.getBoolean("item_sharing_default");
        defaultEXPSharingMultiplier = (float) config.getDouble("exp_sharing_multiplier");

        ConfigurationSection ranksSection = config.getConfigurationSection("ranks");
        if (ranksSection != null){
            String defaultRank = config.getString("default_rank");
            boolean defaultFirst = !ranksSection.getKeys(false).contains(defaultRank);
            for (String rank : ranksSection.getKeys(false)){
                String title = config.getString("ranks." + rank + ".title", "");
                List<String> permissions = config.getStringList("ranks." + rank + ".permissions");
                int rating = config.getInt("ranks." + rank + ".rating", 0);
                Party.PermissionGroup r = new Party.PermissionGroup(rank, title, rating, permissions);
                if (defaultFirst) {
                    this.defaultPartyPermission = r;
                    defaultFirst = false;
                } else if (this.defaultPartyPermission == null && rank.equals(defaultRank)){
                    this.defaultPartyPermission = r;
                }
                registerRank(r);
            }
        }
    }

    // change leader
    // promote/demote member
    // invite member
    // accept invite
    // kick member
    // join party
    // leave party
    // toggle exp sharing
    // toggle item sharing
    // toggle party open status
    // share item
    // toggle party chat
    // send message in party chat without toggle
    // change description

    public Party createParty(Player creator, String name){
        String identifierName = name.toLowerCase().replace(" ", "_");
        identifierName = identifierName.replace("(&[0-9a-fA-FkmolnrKMOLNR])", "");
        return new Party(identifierName, name, creator.getUniqueId());
    }

    public ErrorStatus validatePartyName(String name){
        if (name.length() > partyNameCharacterLimit) return ErrorStatus.EXCEEDED_CHARACTER_LIMIT;
        if (name.length() < partyNameCharacterMinimum) return ErrorStatus.NOT_ENOUGH_CHARACTERS;
        if (!legalPartyNamePattern.matcher(name).matches()) return ErrorStatus.ILLEGAL_CHARACTERS_USED;
        return null;
    }

    public ErrorStatus verifyDescription(String description){
        if (description.length() > partyDescriptionCharacterLimit) return ErrorStatus.EXCEEDED_CHARACTER_LIMIT;
        return null;
    }

    public ErrorStatus registerParty(Player creator, Party party){
        if (!creator.hasPermission("valhalla.createparty")) return ErrorStatus.NO_PERMISSION;
        if (allParties.containsKey(party.getName())) return ErrorStatus.PARTY_ALREADY_EXISTS;
        if (!CooldownManager.getInstance().isCooldownPassed(creator.getUniqueId(), "cooldown_party_creation")) return ErrorStatus.ON_COOLDOWN;
        Party playersParty = getParty(creator);
        if (playersParty != null) return ErrorStatus.ALREADY_IN_PARTY; // player already in party
        allParties.put(party.getName(), party);
        ErrorStatus status = joinParty(creator, party);
        if (status == null){
            CooldownManager.getInstance().setCooldownIgnoreIfPermission(creator, (int) partyCreationCooldown, "cooldown_party_creation");
        }
        return status;
    }

    public ErrorStatus displayPartyInfo(Player p){
        Party party = getParty(p);
        if (party != null) {
            Party.PermissionGroup rank = party.getMembers().get(p.getUniqueId());
            String title = (rank == null) ? "" : rank.getTitle();
            if (party.getLeader().equals(p.getUniqueId())) title = leaderTitle;
            for (String s : partyInfoFormat){
                p.sendMessage(Utils.chat(s
                        .replace("%rank%", title)
                        .replace("%name%", party.getDisplayName())
                        .replace("%description%", party.getDescription())
                        .replace("%member_count%", "" + party.getMembers().size())
                        .replace("%member_cap%", "" + party.getMemberLimit())
                        .replace("%status_exp_sharing%", party.isEnabledEXPSharing() ? translation_true : translation_false)
                        .replace("%status_open%", party.isOpen() ? translation_true : translation_false)
                        .replace("%status_item_sharing%", party.isEnabledItemSharing() ? translation_true : translation_false))
                        .replace("%member_list%", String.join(", ", Utils.getPlayersFromUUIDCollection(party.getMembers().keySet()).keySet())));
            }
            return null;
        }
        return ErrorStatus.NOT_IN_PARTY;
    }

    public ErrorStatus hasPermission(Player player, String permission) {
        Party playersParty = partyMembers.get(player.getUniqueId());
        if (playersParty == null) return ErrorStatus.NOT_IN_PARTY; // player not in party
        if (!playersParty.hasPermission(player, permission)) return ErrorStatus.NO_PERMISSION;
        return null;
    }

    public ErrorStatus changeDescription(Player player, String description){
        Party playersParty = partyMembers.get(player.getUniqueId());
        if (playersParty == null) return ErrorStatus.NOT_IN_PARTY; // player not in party
        if (!playersParty.hasPermission(player, "manage_description")) return ErrorStatus.NO_PERMISSION;
        if (description.length() > partyDescriptionCharacterLimit) return ErrorStatus.EXCEEDED_CHARACTER_LIMIT;
        if (allowPartyDescriptionColorCodes){
            playersParty.setDescription(Utils.chat(description));
        } else {
            playersParty.setDescription(description);
        }
        return null;
    }

    public ErrorStatus togglePartyChat(Player player){
        Party playersParty = partyMembers.get(player.getUniqueId());
        if (playersParty == null) return ErrorStatus.NOT_IN_PARTY; // player not in party
        if (!playersParty.hasPermission(player, "party_chat")) {
            playersInPartyChat.remove(player.getUniqueId());
            return ErrorStatus.NO_PERMISSION;
        }
        if (!playersInPartyChat.remove(player.getUniqueId())) {
            playersInPartyChat.add(player.getUniqueId());
        }
        return null;
    }

    public ErrorStatus shareItem(Player from, Player to){
        if (from.getName().equals(to.getName())) return ErrorStatus.TARGET_SENDER_SAME; // player is trying to give to themselves
        Party playersParty = getParty(from);
        Party targetParty = getParty(to);
        if (playersParty == null) return ErrorStatus.SENDER_NO_PARTY;
        if (targetParty == null) return ErrorStatus.TARGET_NO_PARTY;
        if (!playersParty.getName().equals(targetParty.getName())) return ErrorStatus.NOT_IN_SAME_PARTY;
        if (!(playersParty.isEnabledItemSharing() && playersParty.isUnlockedItemSharing())) return ErrorStatus.FEATURE_NOT_UNLOCKED;
        ItemStack inHandItem = from.getInventory().getItemInMainHand();
        if (Utils.isItemEmptyOrNull(inHandItem)) return ErrorStatus.NO_ITEM;
        if (!CooldownManager.getInstance().isCooldownPassed(from.getUniqueId(), "cooldown_share_item")) return ErrorStatus.ON_COOLDOWN;
        inHandItem = inHandItem.clone();
        to.sendMessage(Utils.chat(itemReceived
                .replace("%player%", from.getName())
                .replace("%amount%", ""+ inHandItem.getAmount())
                .replace("%item%", Utils.getItemName(inHandItem))));
        from.getInventory().setItemInMainHand(null);
        Map<Integer, ItemStack> dropItems = to.getInventory().addItem(inHandItem);
        if (!dropItems.isEmpty()){
            for (ItemStack i : dropItems.values()){
                Item item = to.getWorld().dropItemNaturally(to.getLocation(), i); // if player had no space for item, drop it
                item.setOwner(to.getUniqueId());
            }
        }
        CooldownManager.getInstance().setCooldownIgnoreIfPermission(from, (int) itemShareCooldown, "cooldown_share_item");
        return null;
    }

    public ErrorStatus togglePartyOpenStatus(Player player){
        Party playersParty = partyMembers.get(player.getUniqueId());
        if (playersParty == null) return ErrorStatus.NOT_IN_PARTY; // player not in party
        if (!playersParty.hasPermission(player, "open_party")) return ErrorStatus.NO_PERMISSION;
        playersParty.setOpen(!playersParty.isOpen());
        return null;
    }

    public ErrorStatus toggleItemSharing(Player player){
        Party playersParty = partyMembers.get(player.getUniqueId());
        if (playersParty == null) return ErrorStatus.NOT_IN_PARTY; // player not in party
        if (!playersParty.hasPermission(player, "toggle_item_sharing")) return ErrorStatus.NO_PERMISSION;
        if (!playersParty.isUnlockedItemSharing()) return ErrorStatus.FEATURE_NOT_UNLOCKED;
        playersParty.setEnabledItemSharing(!playersParty.isEnabledItemSharing());
        return null;
    }

    public ErrorStatus toggleEXPSharing(Player player){
        Party playersParty = partyMembers.get(player.getUniqueId());
        if (playersParty == null) return ErrorStatus.NOT_IN_PARTY; // player not in party
        if (!playersParty.hasPermission(player, "toggle_exp_sharing")) return ErrorStatus.NO_PERMISSION;
        if (!playersParty.isUnlockedEXPSharing()) return ErrorStatus.FEATURE_NOT_UNLOCKED;
        playersParty.setEnabledEXPSharing(!playersParty.isEnabledEXPSharing());
        return null;
    }

    public ErrorStatus leaveParty(Player player){
        Party playersParty = partyMembers.get(player.getUniqueId());
        if (playersParty == null) return ErrorStatus.NOT_IN_PARTY; // player not in party
        if (playersParty.getLeader().equals(player.getUniqueId())) return ErrorStatus.CANNOT_KICK_LEADER; // player is the leader
        partyMembers.remove(player.getUniqueId());
        playersInPartyChat.remove(player.getUniqueId());
        playersParty.getMembers().remove(player.getUniqueId());
        return null;
    }

    public ErrorStatus acceptInvite(Player player, String party){
        Party partyToJoin = allParties.get(party);
        if (partyToJoin == null) return ErrorStatus.PARTY_DOES_NOT_EXIST;
        if (!partyToJoin.isOpen()){
            Collection<String> partyInvites = this.partyInvites.getOrDefault(player.getUniqueId(), new HashSet<>());
            if (!partyInvites.contains(party)) return ErrorStatus.NOT_INVITED;
        }
        ErrorStatus status = joinParty(player, partyToJoin);
        if (status == null){
            for (UUID member : partyToJoin.getMembers().keySet()){
                Player p = ValhallaMMO.getPlugin().getServer().getPlayer(member);
                if (p == null) continue;
                p.sendMessage(Utils.chat(memberJoinedMessage
                        .replace("%player%", player.getName())
                        .replace("%party%", partyToJoin.getDisplayName())));
            }
        }
        return status;
    }

    public ErrorStatus joinParty(Player player, Party party){
        if (partyMembers.containsKey(player.getUniqueId())) return ErrorStatus.ALREADY_IN_PARTY; // player already in party
        if (party == null || !allParties.containsKey(party.getName())) return ErrorStatus.PARTY_DOES_NOT_EXIST; // party does not exist
        if (party.getMembers().size() >= party.getMemberLimit()) return ErrorStatus.MEMBER_CAP_REACHED;
        party.getMembers().put(player.getUniqueId(), defaultPartyPermission);
        partyMembers.put(player.getUniqueId(), party);
        return null;
    }

    public ErrorStatus kickMember(Player executor, UUID who){
        Party playersParty = getParty(executor);
        Party targetParty = getParty(who);
        if (playersParty == null) return ErrorStatus.SENDER_NO_PARTY;
        if (targetParty == null) return ErrorStatus.TARGET_NO_PARTY;
        if (!playersParty.getName().equals(targetParty.getName())) return ErrorStatus.NOT_IN_SAME_PARTY;
        if (playersParty.getLeader().equals(who)) return ErrorStatus.CANNOT_KICK_LEADER;
        Party.PermissionGroup playerGroup = playersParty.getPermissions(executor);
        Party.PermissionGroup targetGroup = playersParty.getMembers().get(who);
        if (playerGroup == null) return ErrorStatus.SENDER_NO_RANK;
        if (playersParty.hasPermission(executor, "kick_members")){
            if (targetGroup == null || playerGroup.getRank() > targetGroup.getRank() || playersParty.getLeader().equals(executor.getUniqueId())){
                playersParty.getMembers().remove(who);
                partyMembers.remove(who);
                Player p = ValhallaMMO.getPlugin().getServer().getPlayer(who);
                if (p != null){
                    p.sendMessage(Utils.chat(removedFromPartyMessage));
                }
                return null;
            } else return ErrorStatus.TARGET_HIGHER_RANK;
        } else return ErrorStatus.NO_PERMISSION;
    }

    public ErrorStatus disbandParty(Player executor){
        Party playersParty = getParty(executor);
        if (playersParty == null) return ErrorStatus.SENDER_NO_PARTY;
        if (!playersParty.getLeader().equals(executor.getUniqueId())) return ErrorStatus.NO_PERMISSION;
        for (UUID member : playersParty.getMembers().keySet()){
            partyMembers.remove(member);
            Player onlinePlayer = ValhallaMMO.getPlugin().getServer().getPlayer(member);
            if (onlinePlayer != null){
                onlinePlayer.sendMessage(Utils.chat(partyDisbanded));
            }
        }
        allParties.remove(playersParty.getName());
        return null;
    }

    public boolean toggleInviteMute(Player p){
        if (p.hasMetadata("valhallammo_party_invite_mute")){
            List<MetadataValue> values = p.getMetadata("valhallammo_party_invite_mute");
            if (!values.isEmpty()){
                boolean muted = values.get(0).asBoolean();
                if (muted) {
                    p.removeMetadata("valhallammo_party_invite_mute", ValhallaMMO.getPlugin());
                } else {
                    p.setMetadata("valhallammo_party_invite_mute", new FixedMetadataValue(ValhallaMMO.getPlugin(), true));
                }
                return !muted;
            }
        }
        p.setMetadata("valhallammo_party_invite_mute", new FixedMetadataValue(ValhallaMMO.getPlugin(), true));
        return true;
    }

    public boolean hasInvitesMuted(Player p){
        if (p.hasMetadata("valhallammo_party_invite_mute")){
            List<MetadataValue> values = p.getMetadata("valhallammo_party_invite_mute");
            if (!values.isEmpty()){
                return values.get(0).asBoolean();
            }
        }
        return false;
    }

    public ErrorStatus inviteMember(Player executor, Player who){
        Party playersParty = getParty(executor);
        Party targetParty = getParty(who);
        if (targetParty == null) {
            if (playersParty != null){
                if (playersParty.getMembers().size() >= playersParty.getMemberLimit()) return ErrorStatus.MEMBER_CAP_REACHED;
                if (playersParty.hasPermission(executor, "invite_members")){
                    if (partyInvites.getOrDefault(who.getUniqueId(), new HashSet<>()).contains(playersParty.getName())) return ErrorStatus.TARGET_NOT_INVITEABLE;
                    addInvitedPlayer(who, playersParty);
                    if (!hasInvitesMuted(who)){
                        who.sendMessage(Utils.chat(inviteMessage.replace("%party%", playersParty.getDisplayName())));
                    }
                    return null;
                } else return ErrorStatus.NO_PERMISSION; // player does not have permission to invite members
            } else return ErrorStatus.SENDER_NO_PARTY; // player is not in a party
        } else return ErrorStatus.TARGET_NOT_INVITEABLE; // target is already in a party
    }

    public ErrorStatus setPermissionGroup(Player executor, Player who, Party.PermissionGroup permissionGroup){
        Party playersParty = getParty(executor);
        Party targetParty = getParty(who);
        if (playersParty == null) return ErrorStatus.SENDER_NO_PARTY;
        if (targetParty == null) return ErrorStatus.TARGET_NO_PARTY;
        if (!playersParty.getName().equals(targetParty.getName())) return ErrorStatus.NOT_IN_SAME_PARTY; // players are not in the same party
        if (executor.getUniqueId().equals(who.getUniqueId())) return ErrorStatus.TARGET_SENDER_SAME; // player is trying to promote themselves
        boolean hasPermission = playersParty.hasPermission(executor, "manage_roles");
        if (hasPermission){
            boolean isLeader = playersParty.getLeader().equals(executor.getUniqueId());
            if (isLeader){
                playersParty.getMembers().put(who.getUniqueId(), permissionGroup);
                return null; // player is leader, so rank should always be applied
            } else {
                Party.PermissionGroup executorGroup = playersParty.getPermissions(executor);
                Party.PermissionGroup playerGroup = playersParty.getPermissions(who);
                if (executorGroup != null){
                    if (playerGroup == null || playerGroup.getRank() <= executorGroup.getRank()){
                        if (permissionGroup.getRank() <= executorGroup.getRank()){
                            playersParty.getMembers().put(who.getUniqueId(), permissionGroup);
                            return null;
                        } else return ErrorStatus.RANK_HIGHER_SENDER;
                    } else return ErrorStatus.TARGET_HIGHER_RANK;
                } else return ErrorStatus.SENDER_NO_RANK;
            }
        } else return ErrorStatus.NO_PERMISSION;
    }

    public ErrorStatus changeLeader(Player leader, Player to){
        Party playersParty = getParty(leader);
        if (playersParty == null) return ErrorStatus.SENDER_NO_PARTY; // player is not in a party
        if (leader.getUniqueId().equals(to.getUniqueId())) return ErrorStatus.TARGET_SENDER_SAME; // player tries to transfer to themselves
        if (!playersParty.getLeader().equals(leader.getUniqueId())) return ErrorStatus.SENDER_NOT_LEADER; // player is not the leader
        playersParty.setLeader(to.getUniqueId());
        return null;
    }

    public Party getParty(Player p){
        if (partyMembers.containsKey(p.getUniqueId())) return partyMembers.get(p.getUniqueId());
        for (Party party : allParties.values()){
            if (party.getLeader().equals(p.getUniqueId())) return party;
        }
        return null;
    }

    public Party getParty(UUID p){
        if (partyMembers.containsKey(p)) return partyMembers.get(p);
        for (Party party : allParties.values()){
            if (party.getLeader().equals(p)) return party;
        }
        return null;
    }

    public void registerRank(Party.PermissionGroup group){
        permissionGroups.put(group.getName(), group);
    }

    public Map<String, Party.PermissionGroup> getPermissionGroups() {
        return permissionGroups;
    }

    public int getPartyNameCharacterLimit() {
        return partyNameCharacterLimit;
    }

    public int getPartyDescriptionCharacterLimit() {
        return partyDescriptionCharacterLimit;
    }

    public String getPartyChatFormat() {
        return partyChatFormat;
    }

    public boolean isAllowPartyNameColorCodes() {
        return allowPartyNameColorCodes;
    }

    public boolean isAllowPartyDescriptionColorCodes() {
        return allowPartyDescriptionColorCodes;
    }

    public String getLeaderTitle() {
        return leaderTitle;
    }

    public boolean isEnableParties() {
        return enableParties;
    }

    public boolean isEnableEXPSharing() {
        return enableEXPSharing;
    }

    public boolean isEnableItemSharing() {
        return enableItemSharing;
    }

    public int getDefaultMemberLimit() {
        return defaultMemberLimit;
    }

    public int getDefaultEXPSharingRadius() {
        return defaultEXPSharingRadius;
    }

    public boolean isDefaultUnlockedEXPSharing() {
        return defaultUnlockedEXPSharing;
    }

    public boolean isDefaultUnlockedItemSharing() {
        return defaultUnlockedItemSharing;
    }

    public Party.PermissionGroup getDefaultPartyPermission() {
        return defaultPartyPermission;
    }

    public static Map<String, Party> getAllParties() {
        return allParties;
    }

    public static Map<UUID, Party> getPartyMembers() {
        return partyMembers;
    }

    public static PartyManager getInstance(){
        if (manager == null) manager = new PartyManager();
        return manager;
    }

    public Map<UUID, Collection<String>> getPartyInvites() {
        return partyInvites;
    }

    public Collection<UUID> getPlayersInPartyChat() {
        return playersInPartyChat;
    }

    public float getDefaultEXPSharingMultiplier() {
        return defaultEXPSharingMultiplier;
    }

    public enum ErrorStatus{
        NO_PERMISSION("error_command_no_permission"),
        TARGET_NOT_INVITEABLE("status_command_party_member_not_inviteable"),
        TARGET_NO_PARTY("status_command_party_target_no_party"),
        SENDER_NO_PARTY("status_command_party_sender_no_party"),
        TARGET_HIGHER_RANK("status_command_party_target_higher_rank"),
        RANK_HIGHER_SENDER("status_command_party_rank_higher_sender"),
        TARGET_SENDER_SAME("status_command_party_target_sender_same"),
        SENDER_NOT_LEADER("status_command_party_sender_not_leader"),
        SENDER_NO_RANK("status_command_party_sender_no_rank"),
        NOT_IN_SAME_PARTY("status_command_party_not_in_same_party"),
        ALREADY_IN_PARTY("status_command_party_already_in_party"),
        NOT_IN_PARTY("status_command_party_not_in_party"),
        PARTY_DOES_NOT_EXIST("status_command_party_party_not_found"),
        PARTY_ALREADY_EXISTS("status_command_party_party_already_exists"),
        FEATURE_NOT_UNLOCKED("status_command_party_feature_not_unlocked"),
        CANNOT_KICK_LEADER("status_command_party_cannot_kick_leader"),
        ON_COOLDOWN("status_command_party_on_cooldown"),
        NO_ITEM("status_command_party_no_item_held"),
        EXCEEDED_CHARACTER_LIMIT("status_command_party_character_limit_reached"),
        NOT_ENOUGH_CHARACTERS("status_command_party_name_not_long_enough"),
        ILLEGAL_CHARACTERS_USED("status_command_party_invalid_characters_used"),
        NOT_INVITED("status_command_party_not_invited"),
        MEMBER_CAP_REACHED("status_command_party_member_cap_reached");

        final String message;
        ErrorStatus(String translationPath){
            this.message = TranslationManager.getInstance().getTranslation(translationPath);
        }

        public void sendErrorMessage(CommandSender p){
            p.sendMessage(Utils.chat(message));
        }
    }
}
