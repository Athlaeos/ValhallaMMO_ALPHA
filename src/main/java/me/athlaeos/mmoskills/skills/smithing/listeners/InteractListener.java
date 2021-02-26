package me.athlaeos.mmoskills.skills.smithing.listeners;

import me.athlaeos.mmoskills.managers.CooldownManager;
import me.athlaeos.mmoskills.skills.smithing.managers.ItemMaterialManager;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.*;

public class InteractListener implements Listener {
    private Map<UUID, Material> itemsHeld = new HashMap<>();
    private List<Material> cancelGrindstoneInteract = new ArrayList<>();

    public InteractListener(){
        cancelGrindstoneInteract.addAll(ItemMaterialManager.getInstance().getSwords());
        cancelGrindstoneInteract.addAll(ItemMaterialManager.getInstance().getPickaxes());
        cancelGrindstoneInteract.addAll(ItemMaterialManager.getInstance().getAxes());
        cancelGrindstoneInteract.addAll(ItemMaterialManager.getInstance().getShovels());
        cancelGrindstoneInteract.addAll(ItemMaterialManager.getInstance().getHoes());
        cancelGrindstoneInteract.add(ItemMaterialManager.getInstance().getTrident());
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        trackRightClicksHeldDuration(e);
    }

    private void trackRightClicksHeldDuration(PlayerInteractEvent e){
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (e.getClickedBlock() != null){
                if (Arrays.asList(Material.GRINDSTONE, Material.ANVIL, Material.CHIPPED_ANVIL, Material.DAMAGED_ANVIL)
                        .contains(e.getClickedBlock().getType())){
                    if (cancelGrindstoneInteract.contains(e.getPlayer().getInventory().getItemInMainHand().getType())){
                        e.setCancelled(true);
                    }
                }
            }
        }
        {
            CooldownManager cooldownManager = CooldownManager.getInstance();
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                if (cooldownManager.getTimerResult(e.getPlayer().getUniqueId(), "time_since_right_click") > 250
                        || !itemsHeld.containsKey(e.getPlayer().getUniqueId())) {
                    cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_held_right_click");
                    itemsHeld.put(e.getPlayer().getUniqueId(), e.getPlayer().getInventory().getItemInMainHand().getType());
                }
                if (itemsHeld.containsKey(e.getPlayer().getUniqueId())) {
                    if (e.getPlayer().getInventory().getItemInMainHand().getType() != itemsHeld.get(e.getPlayer().getUniqueId())) {
                        cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_held_right_click");
                        itemsHeld.put(e.getPlayer().getUniqueId(), e.getPlayer().getInventory().getItemInMainHand().getType());
                    }
                }
                cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_since_right_click");
            }
        }
    }
}
