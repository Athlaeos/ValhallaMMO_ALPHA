package me.athlaeos.valhallammo.dom;

import java.util.Collection;

public class PartyRank {
    private final String name;
    private final String title;
    private final Collection<String> permissions;
    private final int rankRating;

    public PartyRank(String name, String title, Collection<String> permissions, int rankRating){
        this.name = name;
        this.title = title;
        this.permissions = permissions;
        this.rankRating = rankRating;
    }

    public String getName() {
        return name;
    }

    public Collection<String> getPermissions() {
        return permissions;
    }

    public int getRankRating() {
        return rankRating;
    }

    public String getTitle() {
        return title;
    }
}
