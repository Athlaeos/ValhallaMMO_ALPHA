package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.crafting.recipetypes.DynamicCookingRecipe;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.block.Campfire;
import org.bukkit.block.Furnace;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockCookEvent;
import org.bukkit.event.inventory.FurnaceBurnEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.FurnaceStartSmeltEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.FurnaceInventory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class FurnaceListener implements Listener {

//    @EventHandler
//    public void onFurnaceInventoryClick(InventoryClickEvent event){
//        if (event.getClickedInventory() instanceof FurnaceInventory){
//            FurnaceInventory inventory = (FurnaceInventory) event.getClickedInventory();
//        }
//    }

    private final Map<Furnace, UUID> furnaceUsers = new HashMap<>();
    private final Map<Campfire, UUID> campFireUsers = new HashMap<>();
    private final Map<Furnace, DynamicCookingRecipe> activeFurnaceRecipes = new HashMap<>();
    private final Map<Campfire, Map<Integer, DynamicCookingRecipe>> activeCampfireRecipes = new HashMap<>();

    @EventHandler
    public void furnaceUserTracker(InventoryClickEvent e){
        if (e.getInventory() instanceof FurnaceInventory){
            FurnaceInventory inventory = (FurnaceInventory) e.getInventory();
            if (inventory.getHolder() != null){
                Furnace furnace = inventory.getHolder();
                furnaceUsers.put(furnace, e.getWhoClicked().getUniqueId());
                System.out.println("furnace now belongs to " + e.getWhoClicked().getName());
            }
        }
    }

    @EventHandler
    public void campfireUserTracker(PlayerInteractEvent e){
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (e.getClickedBlock() != null && (e.getClickedBlock().getType() == Material.CAMPFIRE || e.getClickedBlock().getType() == Material.SOUL_CAMPFIRE)){
                if (e.getClickedBlock().getState() instanceof Campfire){
                    Campfire campfire = (Campfire) e.getClickedBlock().getState();
                    campFireUsers.put(campfire, e.getPlayer().getUniqueId());
                    System.out.println("campfire now belongs to " + e.getPlayer().getName());
                    System.out.println("campfire recipe started");
                    System.out.println(String.format("Campfire indexes occupied: \n%s%s%s%s",
                            (Utils.isItemEmptyOrNull(campfire.getItem(0)) ? "" : "0" + campfire.getItem(0).getType() + "\n"),
                            (Utils.isItemEmptyOrNull(campfire.getItem(1)) ? "" : "1" + campfire.getItem(1).getType() + "\n"),
                            (Utils.isItemEmptyOrNull(campfire.getItem(2)) ? "" : "2" + campfire.getItem(2).getType() + "\n"),
                            (Utils.isItemEmptyOrNull(campfire.getItem(3)) ? "" : "3" + campfire.getItem(3).getType())));
//                activeCampfireRecipes.put(campfire, recipe);
                }
            }
        }
    }

    // BURN -> START -> SMELT -> START -> SMELT
    @EventHandler(priority = EventPriority.HIGH)
    public void onFurnaceStart(FurnaceStartSmeltEvent e){
        System.out.println("smelting block type: " + e.getBlock().getType());
        DynamicCookingRecipe recipe = CustomRecipeManager.getInstance().getCookingRecipesByKey().get(e.getRecipe().getKey());
        if (e.getBlock().getState() instanceof Furnace){
            //activeFurnaceRecipes.put((Furnace) e.getBlock().getState(), recipe);
            System.out.println("furnace recipe started");
        }
        if (recipe != null) {
        } else {
            if (e.getBlock().getState() instanceof Campfire){
                activeCampfireRecipes.remove((Campfire) e.getBlock().getState());
                System.out.println("campfire recipe overwritten");
            } else if (e.getBlock().getState() instanceof Furnace){
                activeFurnaceRecipes.remove((Furnace) e.getBlock().getState());
            }
        }
        System.out.println("furnace start smelt with HIGH");
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onFurnaceBurn(FurnaceBurnEvent e){
        if (e.getBlock().getState() instanceof Furnace){
            System.out.println("furnace burn with HIGHEST");
        }
    }

    @EventHandler
    public void onFurnaceSmelt(BlockCookEvent e){
        System.out.println("furnace smelt");
    }

    @EventHandler
    public void onFurnaceExtract(FurnaceExtractEvent event){
        System.out.println("furnace extract");
    }
}
