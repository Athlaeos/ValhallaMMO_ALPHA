package me.athlaeos.valhallammo.dom;

import me.athlaeos.valhallammo.managers.PartyManager;
import org.bukkit.entity.Player;

import java.util.*;

public class Party {
    private final String name;
    private final String displayName;
    private String description = "";

    private boolean unlockedEXPSharing = PartyManager.getInstance().isDefaultUnlockedEXPSharing();
    private boolean unlockedItemSharing = PartyManager.getInstance().isDefaultUnlockedItemSharing();
    private boolean enabledEXPSharing = unlockedEXPSharing;
    private boolean enabledItemSharing = unlockedItemSharing;
    private int memberLimit = PartyManager.getInstance().getDefaultMemberLimit();
    private int EXPSharingRadius = PartyManager.getInstance().getDefaultEXPSharingRadius();
    private float EXPSharingMultiplier = PartyManager.getInstance().getDefaultEXPSharingMultiplier();
    private boolean isOpen = false;

    private UUID leader;
    private final Map<UUID, PermissionGroup> members = new HashMap<>();

    public Party(String name, String displayName, UUID leader){
        this.name = name;
        this.displayName = displayName;
        this.leader = leader;
    }

    public PermissionGroup getPermissions(Player p){
        return members.get(p.getUniqueId());
    }

    public boolean hasPermission(Player p, String permission){
        if (leader.equals(p.getUniqueId())) return true;
        PermissionGroup permissionGroup = getPermissions(p);
        if (permissionGroup != null){
            return getPermissions(p).getPermissions().contains(permission);
        }
        return false;
    }

    public boolean isEnabledEXPSharing() {
        return enabledEXPSharing;
    }

    public boolean isEnabledItemSharing() {
        return enabledItemSharing;
    }

    public void setEnabledEXPSharing(boolean enabledEXPSharing) {
        this.enabledEXPSharing = enabledEXPSharing;
    }

    public void setEnabledItemSharing(boolean enabledItemSharing) {
        this.enabledItemSharing = enabledItemSharing;
    }

    public float getEXPSharingMultiplier() {
        return EXPSharingMultiplier;
    }

    public void setEXPSharingMultiplier(float EXPSharingMultiplier) {
        this.EXPSharingMultiplier = EXPSharingMultiplier;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void setOpen(boolean open) {
        isOpen = open;
    }

    public void setLeader(UUID leader) {
        this.leader = leader;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isUnlockedEXPSharing() {
        return unlockedEXPSharing;
    }

    public boolean isUnlockedItemSharing() {
        return unlockedItemSharing;
    }

    public int getMemberLimit() {
        return memberLimit;
    }

    public int getEXPSharingRadius() {
        return EXPSharingRadius;
    }

    public UUID getLeader() {
        return leader;
    }

    public Map<UUID, PermissionGroup> getMembers() {
        return members;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setUnlockedEXPSharing(boolean unlockedEXPSharing) {
        this.unlockedEXPSharing = unlockedEXPSharing;
    }

    public void setUnlockedItemSharing(boolean unlockedItemSharing) {
        this.unlockedItemSharing = unlockedItemSharing;
    }

    public void setMemberLimit(int memberLimit) {
        this.memberLimit = memberLimit;
    }

    public void setEXPSharingRadius(int EXPSharingRadius) {
        this.EXPSharingRadius = EXPSharingRadius;
    }

    public static class PermissionGroup{
        private final String name;
        private final String title;
        private final Collection<String> permissions;
        private final int rank;

        public PermissionGroup(String name, String title, int rank, Collection<String> permissions){
            this.name = name;
            this.title = title;
            this.rank = rank;
            this.permissions = permissions;
        }

        public Collection<String> getPermissions() {
            return permissions;
        }

        public String getName() {
            return name;
        }

        public int getRank() {
            return rank;
        }

        public String getTitle() {
            return title;
        }
    }
}
