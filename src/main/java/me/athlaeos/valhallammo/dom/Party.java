package me.athlaeos.valhallammo.dom;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Party {
    private final UUID id;
    private String name;
    private String description;
    private String password = null;
    private UUID leader;
    private final Map<UUID, PartyRank> members = new HashMap<>();
    private int member_limit;
    private boolean open = true;

    private boolean party_chat;
    private boolean exp_sharing;
    private boolean item_sharing;

    private double exp_sharing_multiplier;

    public Party(Player owner, String name, String description, int member_limit){
        this.id = UUID.randomUUID();
        this.leader = owner.getUniqueId();
        this.name = name;
        this.description = description;
        this.member_limit = member_limit;
    }

    public void addMember(Player member){
        members.put(member.getUniqueId(), null);
    }

    /**
     * Sets the rank of a member
     * @param member the member to set their rank
     * @param rank the integer representing the rank
     * @return true if the member's rank was changed, or false if the member is not in the party
     */
    public boolean setMemberRank(OfflinePlayer member, PartyRank rank){
        if (members.containsKey(member.getUniqueId())){
            members.put(member.getUniqueId(), rank);
            return true;
        }
        return false;
    }

    /**
     * Removes a member, if present
     * @param member the member to remove
     * @return true if member was removed, or false if they weren't in the party
     */
    public boolean removeMember(OfflinePlayer member){
        if (members.containsKey(member.getUniqueId())){
            members.remove(member.getUniqueId());
            return true;
        }
        return false;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public void setMember_limit(int member_limit) {
        this.member_limit = member_limit;
    }

    public void setOpen(boolean open) {
        this.open = open;
    }

    public void setParty_chat(boolean party_chat) {
        this.party_chat = party_chat;
    }

    public void setExp_sharing(boolean exp_sharing) {
        this.exp_sharing = exp_sharing;
    }

    public void setItem_sharing(boolean item_sharing) {
        this.item_sharing = item_sharing;
    }

    public void setExp_sharing_multiplier(double exp_sharing_multiplier) {
        this.exp_sharing_multiplier = exp_sharing_multiplier;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getPassword() {
        return password;
    }

    public UUID getLeader() {
        return leader;
    }

    public Map<UUID, PartyRank> getMembers() {
        return members;
    }

    public int getMember_limit() {
        return member_limit;
    }

    public boolean isOpen() {
        return open;
    }

    public boolean isParty_chat() {
        return party_chat;
    }

    public boolean isExp_sharing() {
        return exp_sharing;
    }

    public boolean isItem_sharing() {
        return item_sharing;
    }

    public double getExp_sharing_multiplier() {
        return exp_sharing_multiplier;
    }
}
