package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Party;
import me.athlaeos.valhallammo.dom.PartyRank;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class PartyManager {
    private static PartyManager manager = null;
    private final Map<String, PartyRank> ranks = new HashMap<>();
    private boolean partiesEnabled;
    private static Map<UUID, Boolean> playersInPartyChat = new HashMap<>();

    private static Map<String, Party> allParties = new HashMap<>();
    private static Map<UUID, Party> partyMembers = new HashMap<>();

    private String translation_false;
    private String translation_true;

    private String leader_title;
    private List<String> partyInfo;
    private String party_chat_format;
    private boolean description_allow_colors;
    private boolean name_allow_colors;
    private int name_character_limit;
    private boolean expSharingEnabled;
    private boolean itemSharingEnabled;
    private boolean partyChatEnabled;
    private int party_capacity;
    private PartyRank defaultRank = null;

    /**
     * Registers a party
     * @param p the party to register
     * @return true if party was registered, false if it already exists
     */
    public boolean registerParty(Party p){
        if (allParties.containsKey(p.getName())) return false;
        allParties.put(p.getName(), p);
        partyMembers.put(p.getLeader(), p);
        return true;
    }

    /**
     * Joins a player to a party
     * @param player the player to join to a party
     * @param party the party to join the player to
     * @return true if the player was joined to the party, false if the player was already in a party
     */
    public boolean joinParty(Player player, Party party){
        if (partyMembers.containsKey(player.getUniqueId())) return false; // player already in party
        if (!allParties.containsKey(party.getName())) return false;
        party.addMember(player);
        party.setMemberRank(player, defaultRank);
        partyMembers.put(player.getUniqueId(), party);
        return true;
    }

    public boolean leaveParty(Player player){
        Party playersParty = partyMembers.get(player.getUniqueId());
        if (playersParty == null) return false; // player not in party
        if (playersParty.getLeader().equals(player.getUniqueId())) return false; // player is the leader
        return playersParty.removeMember(player);
    }

    public PartyManager(){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        partiesEnabled = config.getBoolean("parties");
        if (!partiesEnabled) return;
        partyInfo = config.getStringList("party_info");
        expSharingEnabled = config.getBoolean("exp_sharing");
        itemSharingEnabled = config.getBoolean("item_sharing");
        partyChatEnabled = config.getBoolean("party_chat");
        party_chat_format = config.getString("party_chat_format");
        description_allow_colors = config.getBoolean("description_allow_colors");
        name_allow_colors = config.getBoolean("name_allow_colors");
        name_character_limit = config.getInt("name_character_limit");
        leader_title = config.getString("leader_title");
        party_capacity = config.getInt("party_capacity");
        translation_false = TranslationManager.getInstance().getTranslation("translation_false");
        translation_true = TranslationManager.getInstance().getTranslation("translation_true");

        ConfigurationSection ranksSection = config.getConfigurationSection("ranks");
        if (ranksSection != null){
            String defaultRank = config.getString("default_rank");
            boolean defaultFirst = !ranksSection.getKeys(false).contains(defaultRank);
            for (String rank : ranksSection.getKeys(false)){
                String title = config.getString("ranks." + rank + ".title", Utils.toPascalCase(rank.replace("_", " ")));
                List<String> permissions = config.getStringList("ranks." + rank + ".permissions");
                int rating = config.getInt("ranks." + rank + ".rating", 0);
                PartyRank r = new PartyRank(rank, title, permissions, rating);
                if (defaultFirst) {
                    this.defaultRank = r;
                    defaultFirst = false;
                } else if (this.defaultRank == null && rank.equals(defaultRank)){
                    this.defaultRank = r;
                }
                registerRank(r);
            }
        }
    }

    public Party createParty(Player owner, String name){
        return new Party(owner, name_allow_colors ? Utils.chat(name) : name, "", party_capacity);
    }

    public Party getParty(Player p){
        if (partyMembers.containsKey(p.getUniqueId())) return partyMembers.get(p.getUniqueId());
        for (Party party : allParties.values()){
            if (party.getLeader().equals(p.getUniqueId())) return party;
        }
        return null;
    }

    public boolean togglePartyChat(Player p){
        if (partyMembers.containsKey(p.getUniqueId())) {
            if (hasPartyPermission(p, "party_chat")){
                playersInPartyChat.putIfAbsent(p.getUniqueId(), false);
                playersInPartyChat.put(p.getUniqueId(), !playersInPartyChat.get(p.getUniqueId()));
                return true;
            }
        }
        return false;
    }

    public boolean changePartyDescription(Player p, String description){
        Party party = getParty(p);
        if (party != null) {
            party.setDescription(description_allow_colors ? Utils.chat(description) : description);
        }
        return false;
    }

    /**
     * transfers an item from the main hand of one person to another person in the same party
     * @param from the player who transfers their main hand item
     * @param to the player who should be given the main hand item
     * @return true if the item was transferred, false if that wasn't possible for some reason
     * (players don't share party, item is null, or from-player is not in the same party
     */
    public boolean shareItem(Player from, Player to){
        Party party = partyMembers.get(from.getUniqueId());
        if (party != null){
            if (party.getMembers().containsKey(to.getUniqueId())){ // if players aren't in the same party, don't share
                ItemStack inHandItem = from.getInventory().getItemInMainHand();
                if (Utils.isItemEmptyOrNull(inHandItem)) return false;
                inHandItem = inHandItem.clone();
                from.getInventory().setItemInMainHand(null);
                Map<Integer, ItemStack> dropItems = to.getInventory().addItem(inHandItem);
                if (!dropItems.isEmpty()){
                    for (ItemStack i : dropItems.values()){
                        Item item = to.getWorld().dropItemNaturally(to.getLocation(), i); // if player had no space for item, drop it
                        item.setOwner(to.getUniqueId());
                    }
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Toggles the player's party exp sharing
     * @param p the player who's party's exp sharing should be toggled
     * @return true if the exp sharing was toggled, false if the player has no party
     */
    public boolean togglePartyExpSharing(Player p){
        Party party = partyMembers.get(p.getUniqueId());
        if (party != null){
            party.setExp_sharing(!party.isExp_sharing());
            return true;
        }
        return false;
    }

    /**
     * Toggles the player's party item sharing
     * @param p the player who's party's item sharing should be toggled
     * @return true if the item sharing was toggled, false if the player has no party
     */
    public boolean togglePartyItemSharing(Player p){
        Party party = partyMembers.get(p.getUniqueId());
        if (party != null){
            party.setItem_sharing(!party.isItem_sharing());
            return true;
        }
        return false;
    }

    public boolean togglePartyPartyChat(Player p){
        Party party = partyMembers.get(p.getUniqueId());
        if (party != null){
            party.setParty_chat(!party.isParty_chat());
            return true;
        }
        return false;
    }

    public boolean displayPartyInfo(Player p){
        Party party = getParty(p);
        if (party != null) {
            PartyRank rank = party.getMembers().get(p.getUniqueId());
            String title = (rank == null) ? "" : rank.getTitle();
            if (party.getLeader().equals(p.getUniqueId())) title = leader_title;
            for (String s : partyInfo){
                p.sendMessage(Utils.chat(s
                        .replace("%rank%", title)
                        .replace("%member_count%", "" + (party.getMembers().size() + 1))
                        .replace("%member_cap%", "" + party.getMember_limit())
                        .replace("%status_exp_sharing%", party.isExp_sharing() ? translation_true : translation_false)
                        .replace("%status_open%", party.isOpen() ? translation_true : translation_false)
                        .replace("%status_item_sharing%", party.isItem_sharing() ? translation_true : translation_false)));
            }
            return true;
        }
        return false;
    }

    public boolean hasPartyPermission(Player p, String permission){
        Party party = partyMembers.get(p.getUniqueId());
        if (party == null) return false;
        if (party.getLeader().equals(p.getUniqueId())) return true;
        PartyRank rank = party.getMembers().get(p.getUniqueId());
        if (rank == null) return false;
        return rank.getPermissions().contains(permission);
    }

    public boolean hasPartyChatEnabled(Player p){
        return playersInPartyChat.getOrDefault(p.getUniqueId(), false);
    }

    public void onChatEvent(AsyncPlayerChatEvent e){
        if (!partiesEnabled) return;
        if (hasPartyChatEnabled(e.getPlayer())){
            if (e.getMessage().startsWith("!")) {
                e.setMessage(e.getMessage().replaceFirst("!", ""));
                return;
            }
            if (sendMessageInPartyChat(e.getPlayer(), e.getMessage())){
                e.setCancelled(true);
            }
        }
    }

    public boolean sendMessageInPartyChat(Player sender, String message){
        Party playersParty = partyMembers.get(sender.getUniqueId());
        if (playersParty != null){
            Collection<Player> recipients = new HashSet<>();
            PartyRank playersRank = playersParty.getMembers().get(sender.getUniqueId());
            String rankTitle = (playersRank == null) ? "" : playersRank.getTitle();
            if (playersParty.getLeader().equals(sender.getUniqueId())){
                rankTitle = leader_title;
            }
            for (UUID member : playersParty.getMembers().keySet()){
                Player onlinePlayer = ValhallaMMO.getPlugin().getServer().getPlayer(member);
                if (onlinePlayer != null){
                    recipients.add(onlinePlayer);
                }
            }
            Player leader = ValhallaMMO.getPlugin().getServer().getPlayer(playersParty.getLeader());
            if (leader != null){
                recipients.add(leader);
            }
            for (Player recipient : recipients){
                recipient.sendMessage(Utils.chat(party_chat_format)
                        .replace("%party_name%", playersParty.getName())
                        .replace("%who%", sender.getName())
                        .replace("%rank%", rankTitle)
                        .replace("%message%", message));
            }
            return true;
        }
        return false;
    }

    public Map<String, PartyRank> getRanks() {
        return ranks;
    }

    public void registerRank(PartyRank rank){
        ranks.put(rank.getName(), rank);
    }

    public static PartyManager getManager(){
        if (manager == null) manager = new PartyManager();
        return manager;
    }

    public void reload(){
        manager = null;
        getManager();
    }
}
