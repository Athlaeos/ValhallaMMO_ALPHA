package me.athlaeos.valhallammo.domain;

import org.bukkit.entity.Player;

public class PlayerMenuUtility {
    private Player owner;
    private Player selectedPlayer;
    private int pageNumber = 0;
    private Menu previousMenu = null;

    public PlayerMenuUtility(org.bukkit.entity.Player owner) {
        this.owner = owner;
    }

    public org.bukkit.entity.Player getSelectedPlayer() {
        return selectedPlayer;
    }

    public void setSelectedPlayer(org.bukkit.entity.Player selectedPlayer) {
        this.selectedPlayer = selectedPlayer;
    }

    public void setPreviousMenu(Menu previousMenu) {
        this.previousMenu = previousMenu;
    }

    public Menu getPreviousMenu() {
        return previousMenu;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public void setPageNumber(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void incrementPageNumber(){
        pageNumber++;
    }

    public void decrementPageNumber(){
        pageNumber--;
    }

    public org.bukkit.entity.Player getOwner() {
        return owner;
    }

    public void setOwner(Player owner) {
        this.owner = owner;
    }
}
