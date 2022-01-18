package me.athlaeos.valhallammo.crafting;

import me.athlaeos.valhallammo.crafting.recipetypes.AbstractCustomCraftingRecipe;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerCraftChoiceManager {
    private static PlayerCraftChoiceManager manager = null;
    private final Map<UUID, AbstractCustomCraftingRecipe> playerCraftChoices = new HashMap<>();

    public static PlayerCraftChoiceManager getInstance(){
        if (manager == null) manager = new PlayerCraftChoiceManager();
        return manager;
    }

    public AbstractCustomCraftingRecipe getPlayerCurrentRecipe(Player p){
        if (playerCraftChoices.containsKey(p.getUniqueId())){
            return playerCraftChoices.get(p.getUniqueId());
        }
        return null;
    }

    public void setPlayerCurrentRecipe(Player p, AbstractCustomCraftingRecipe recipe){
        if (recipe == null) playerCraftChoices.remove(p.getUniqueId());
        playerCraftChoices.put(p.getUniqueId(), recipe);
    }
}
