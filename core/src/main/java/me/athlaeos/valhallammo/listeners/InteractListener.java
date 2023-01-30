package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.recipetypes.AbstractCustomCraftingRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.ItemClassImprovementRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.ItemCraftingRecipe;
import me.athlaeos.valhallammo.crafting.recipetypes.ItemImprovementRecipe;
import me.athlaeos.valhallammo.dom.RequirementType;
import me.athlaeos.valhallammo.events.PlayerCustomCraftEvent;
import me.athlaeos.valhallammo.events.PlayerItemClassTinkerEvent;
import me.athlaeos.valhallammo.events.PlayerItemTinkerEvent;
import me.athlaeos.valhallammo.items.BlockCraftStateValidationManager;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.menus.CraftRecipeChoiceMenu;
import me.athlaeos.valhallammo.menus.CraftRecipeChoiceMenuUpdated;
import me.athlaeos.valhallammo.menus.PlayerMenuUtilManager;
import me.athlaeos.valhallammo.skills.InteractSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.EntityEffect;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class InteractListener implements Listener {
    private final Map<UUID, Material> itemsHeld = new HashMap<>();
    private String errorNoIngredients;
    private final Map<UUID, RecipeFrequencyDO> recipeFrequency = new HashMap<>();
    private static InteractListener listener;
    private static boolean old_menu = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("old_menu");

    private Collection<Material> swap_crafting_table_functionality;

    public InteractListener(){
        listener = this;
        errorNoIngredients = TranslationManager.getInstance().getTranslation("error_crafting_no_ingredients");
        swap_crafting_table_functionality = new HashSet<>(ItemUtils.getMaterialList(ConfigManager.getInstance().getConfig("config.yml").get().getStringList("swap_crafting_table_functionality")));
    }

    public void reload(){
        errorNoIngredients = TranslationManager.getInstance().getTranslation("error_crafting_no_ingredients");
        swap_crafting_table_functionality = new HashSet<>(ItemUtils.getMaterialList(ConfigManager.getInstance().getConfig("config.yml").get().getStringList("swap_crafting_table_functionality")));
    }

    public static InteractListener getListener() {
        return listener;
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onInteract(PlayerInteractEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getPlayer().getWorld().getName())) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK) EntityDamagedListener.getLastArmSwingReasons().put(e.getPlayer().getUniqueId(), EntityDamagedListener.ArmSwingReason.BLOCK_INTERACT);
        if (e.getAction() == Action.LEFT_CLICK_BLOCK) EntityDamagedListener.getLastArmSwingReasons().put(e.getPlayer().getUniqueId(), EntityDamagedListener.ArmSwingReason.BLOCK_DAMAGE);
        if (e.getAction() == Action.LEFT_CLICK_AIR) EntityDamagedListener.getLastArmSwingReasons().put(e.getPlayer().getUniqueId(), EntityDamagedListener.ArmSwingReason.ATTACK);
        if (e.useItemInHand() == Event.Result.DENY && e.useInteractedBlock() == Event.Result.DENY) return; // event is cancelled
        for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
            if (s != null){
                if (s instanceof InteractSkill){
                    ((InteractSkill) s).onInteract(e);
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityInteract(PlayerInteractEntityEvent e){
        EntityDamagedListener.getLastArmSwingReasons().put(e.getPlayer().getUniqueId(), EntityDamagedListener.ArmSwingReason.ENTITY_INTERACT);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onItemDrop(PlayerDropItemEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getPlayer().getWorld().getName())) return;
        if (!e.isCancelled()){
            EntityDamagedListener.getLastArmSwingReasons().put(e.getPlayer().getUniqueId(), EntityDamagedListener.ArmSwingReason.DROP_ITEM);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getPlayer().getWorld().getName())) return;
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.LEFT_CLICK_BLOCK){
            if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
                if (e.getClickedBlock() != null){
                    boolean swap_crafting_table_functionality = this.swap_crafting_table_functionality.contains(e.getClickedBlock().getType());
                    boolean open_custom = swap_crafting_table_functionality == !e.getPlayer().isSneaking();
                    if (swap_crafting_table_functionality && PlayerCraftChoiceManager.getInstance().getPlayerCurrentRecipe(e.getPlayer()) != null){
                        open_custom = false;
                    }
                    Material clickedBlockType = e.getClickedBlock().getType();
                    Material baseVersion = Optional.ofNullable(ItemUtils.getBaseMaterial(clickedBlockType)).orElse(clickedBlockType);

                    if (open_custom && e.getHand() == EquipmentSlot.HAND){
                        // Opening a recipe picking menu
                        CooldownManager.getInstance().startTimer(e.getPlayer().getUniqueId(), "benchmark");
                        if (old_menu){
                            int toolId = SmithingItemTreatmentManager.getInstance().getItemsToolId(e.getPlayer().getInventory().getItemInMainHand());
                            if (Utils.isItemEmptyOrNull(e.getPlayer().getInventory().getItemInMainHand()) || toolId >= 0){
                                if (CustomRecipeManager.getInstance().getCraftingStationRecipes().containsKey(baseVersion)){
                                    Collection<AbstractCustomCraftingRecipe> availableRecipes = CustomRecipeManager.getInstance().getRecipesByCraftingStation(clickedBlockType);
                                    availableRecipes = availableRecipes.stream().filter(abstractCustomCraftingRecipe -> {
                                        if (abstractCustomCraftingRecipe instanceof ItemCraftingRecipe){
                                            return RequirementType.isRecipeCraftable((ItemCraftingRecipe) abstractCustomCraftingRecipe, toolId);
                                        }
                                        return true;
                                    }).collect(Collectors.toList());
                                    CraftRecipeChoiceMenu menu = new CraftRecipeChoiceMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility(e.getPlayer()), availableRecipes, true);
                                    e.setCancelled(true);
                                    e.setUseItemInHand(Event.Result.DENY);
                                    menu.open();
                                }
                            } else {
                                if (CustomRecipeManager.getInstance().getItemImprovementRecipes().containsKey(clickedBlockType) || CustomRecipeManager.getInstance().getItemClassImprovementRecipes().containsKey(clickedBlockType)){
                                    Collection<AbstractCustomCraftingRecipe> availableRecipes = CustomRecipeManager.getInstance().getRecipesByCraftingStation(clickedBlockType, e.getPlayer().getInventory().getItemInMainHand());
                                    CraftRecipeChoiceMenu menu = new CraftRecipeChoiceMenu(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility(e.getPlayer()), availableRecipes, false);
                                    e.setCancelled(true);
                                    e.setUseItemInHand(Event.Result.DENY);
                                    menu.open();
                                }
                            }
                        } else {
                            Collection<AbstractCustomCraftingRecipe> availableRecipes = CustomRecipeManager.getInstance().getAllCustomRecipes().values();
                            availableRecipes = availableRecipes.stream().filter(abstractCustomCraftingRecipe -> abstractCustomCraftingRecipe.getCraftingBlock() == baseVersion).collect(Collectors.toList());
                            if (!availableRecipes.isEmpty()) {
                                CraftRecipeChoiceMenuUpdated menu = new CraftRecipeChoiceMenuUpdated(PlayerMenuUtilManager.getInstance().getPlayerMenuUtility(e.getPlayer()), availableRecipes, Utils.isItemEmptyOrNull(e.getPlayer().getInventory().getItemInMainHand()));
                                e.setCancelled(true);
                                e.setUseItemInHand(Event.Result.DENY);
                                menu.open();
                            }
                        }
                    }
                }
            } else if (e.getAction() == Action.LEFT_CLICK_BLOCK){
                if (PlayerCraftChoiceManager.getInstance().getPlayerCurrentRecipe(e.getPlayer()) != null){
                    e.setCancelled(false); // [CHANGE] from true to false, attempted fix to un-cancel block breaking if recipe selected
                    PlayerCraftChoiceManager.getInstance().setPlayerCurrentRecipe(e.getPlayer(), null);
                    resetFrequency(e.getPlayer());
                }
            }
        }
        if (e.getAction() == Action.RIGHT_CLICK_BLOCK){
            if (e.getClickedBlock() != null){
                Material baseVersion = Optional.ofNullable(ItemUtils.getBaseMaterial(e.getClickedBlock().getType())).orElse(e.getClickedBlock().getType());

                // Tracking how long players held a mouse button
                CooldownManager cooldownManager = CooldownManager.getInstance();
                if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    if (cooldownManager.getTimerResult(e.getPlayer().getUniqueId(), "time_since_right_click") > 500
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
                }

                if (e.getClickedBlock().getType().isInteractable()){
                    if (!cooldownManager.isCooldownPassed(e.getPlayer().getUniqueId(), "cancel_block_interactions")){
                        e.setCancelled(true);
                    }
                }

                // Cancelling block interactions if player is trying to craft something
                AbstractCustomCraftingRecipe currentRecipe = PlayerCraftChoiceManager.getInstance().getPlayerCurrentRecipe(e.getPlayer());
                if (currentRecipe != null){
                    ItemStack heldItem = e.getPlayer().getInventory().getItemInMainHand();
                    if (currentRecipe instanceof ItemCraftingRecipe) {
                        int toolId = SmithingItemTreatmentManager.getInstance().getItemsToolId(e.getPlayer().getInventory().getItemInMainHand());
                        boolean requires_tool = ((ItemCraftingRecipe) currentRecipe).getToolRequirementType() >= 0;
                        boolean craftable = RequirementType.isRecipeCraftable((ItemCraftingRecipe) currentRecipe, toolId);
                        boolean empty_hands = Utils.isItemEmptyOrNull(e.getPlayer().getInventory().getItemInMainHand());
                        if ((requires_tool) ? craftable : empty_hands){
                            if (BlockCraftStateValidationManager.getInstance().blockConditionsApply(e.getClickedBlock(), currentRecipe.getValidation())){
                                if (canPlayerCraft(e, currentRecipe)){
                                    // Player is crafting an item
                                    if (ItemUtils.isSimilarTo(e.getClickedBlock().getType(), currentRecipe.getCraftingBlock())){
                                        if (e.getClickedBlock().getType().isInteractable()){
                                            e.setCancelled(true);
                                        }
                                        // Checks if the player can craft the recipe once on first click, and once at the end.
                                        // If it's been more than 500ms since the players last right click, so if the player clicks a crafting
                                        // station, or their first click, the player is notified they dont have the materials.
                                        // If it has been shorter, so if the player has been right clicking the station for longer than 500ms,
                                        // then it checks if the required crafting time right clicking has been held before crafting.
                                        if (cooldownManager.getTimerResult(e.getPlayer().getUniqueId(), "time_since_right_click") > 500){
                                            if (!ItemUtils.canCraft(currentRecipe, e.getPlayer(), currentRecipe.requireExactMeta())){
                                                if (!errorNoIngredients.equals("")){
                                                    e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(errorNoIngredients)));
                                                }
                                                cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_held_right_click");
                                                PlayerCraftChoiceManager.getInstance().setPlayerCurrentRecipe(e.getPlayer(), null);
                                            } else {
                                                if (cooldownManager.isCooldownPassed(e.getPlayer().getUniqueId(), "sound_craft")){
                                                    Sound sound = MaterialCosmeticManager.getInstance().getCraftWorkSound(baseVersion);
                                                    if (sound != null){
                                                        e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), sound, .3F, 1F);
                                                    }
                                                    e.getPlayer().getWorld().spawnParticle(Particle.BLOCK_DUST, e.getClickedBlock().getLocation().add(0.5, 1.2, 0.5), 3, e.getClickedBlock().getBlockData());
                                                    cooldownManager.setCooldown(e.getPlayer().getUniqueId(), 500, "sound_craft");
                                                }
                                            }
                                        } else if (cooldownManager.getTimerResult(e.getPlayer().getUniqueId(), "time_held_right_click") >= (currentRecipe.getCraftingTime() * (1 - AccumulativeStatManager.getInstance().getStats("CRAFTING_TIME_REDUCTION", e.getPlayer(), true)))){
                                            if (ItemUtils.canCraft(currentRecipe, e.getPlayer(), currentRecipe.requireExactMeta())){
                                                PlayerCustomCraftEvent craftEvent = new PlayerCustomCraftEvent(e.getPlayer(), (ItemCraftingRecipe) currentRecipe, e.getClickedBlock(), true);
                                                ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(craftEvent);
                                                if (!craftEvent.isCancelled()){
                                                    incrementPlayerCraftFrequency(e.getPlayer(), currentRecipe);
                                                    if (requires_tool){
                                                        if (ItemUtils.damageItem(e.getPlayer(), heldItem, 1, EntityEffect.BREAK_EQUIPMENT_MAIN_HAND)){
                                                            e.getPlayer().getInventory().setItemInMainHand(null);
                                                        }
                                                    }
                                                }
                                            } else {
                                                if (!errorNoIngredients.equals("")){
                                                    e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(errorNoIngredients)));
                                                }
                                                PlayerCraftChoiceManager.getInstance().setPlayerCurrentRecipe(e.getPlayer(), null);
                                            }
                                            cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_held_right_click");
                                        } else {
                                            if (cooldownManager.isCooldownPassed(e.getPlayer().getUniqueId(), "sound_craft")){
                                                Sound sound = MaterialCosmeticManager.getInstance().getCraftWorkSound(baseVersion);
                                                if (sound != null){
                                                    e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), sound, .3F, 1F);
                                                }
                                                cooldownManager.setCooldown(e.getPlayer().getUniqueId(), 500, "sound_craft");
                                                e.getPlayer().getWorld().spawnParticle(Particle.BLOCK_DUST, e.getClickedBlock().getLocation().add(0.5, 1.2, 0.5), 3, e.getClickedBlock().getBlockData());
                                            }
                                        }
                                    } else {
                                        PlayerCraftChoiceManager.getInstance().setPlayerCurrentRecipe(e.getPlayer(), null);
                                        resetFrequency(e.getPlayer());
                                    }
                                } else {
                                    PlayerCraftChoiceManager.getInstance().setPlayerCurrentRecipe(e.getPlayer(), null);
                                    resetFrequency(e.getPlayer());
                                }
                            } else {
                                cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_held_right_click");
                                PlayerCraftChoiceManager.getInstance().setPlayerCurrentRecipe(e.getPlayer(), null);
                            }
                        }
                    } else if (currentRecipe instanceof ItemImprovementRecipe || currentRecipe instanceof ItemClassImprovementRecipe) {
                        if (!heldItem.getType().isAir()){
                            if (SmithingItemTreatmentManager.getInstance().isItemCustom(heldItem)){
                                boolean itemMatch = false;
                                if (currentRecipe instanceof ItemImprovementRecipe){ // [CHANGE] attempted fix to prevent improvement recipes being applied on unintended item types
                                    if (((ItemImprovementRecipe) currentRecipe).getRequiredItemType() == heldItem.getType()){
                                        itemMatch = true;
                                    }
                                } else {
                                    EquipmentClass equipmentClass = EquipmentClass.getClass(heldItem);
                                    if (equipmentClass != null){
                                        if (((ItemClassImprovementRecipe) currentRecipe).getRequiredEquipmentClass() == equipmentClass){
                                            itemMatch = true;
                                        }
                                    }
                                }
                                if (itemMatch){
                                    // Player might be trying to improve item
                                    if (BlockCraftStateValidationManager.getInstance().blockConditionsApply(e.getClickedBlock(), currentRecipe.getValidation())) {
                                        if (ItemUtils.isSimilarTo(currentRecipe.getCraftingBlock(), e.getClickedBlock().getType())){
                                            if (e.getClickedBlock().getType().isInteractable()){
                                                e.setCancelled(true);
                                            }
                                            if (cooldownManager.getTimerResult(e.getPlayer().getUniqueId(), "time_since_right_click") > 500){
                                                if (!ItemUtils.canCraft(currentRecipe, e.getPlayer(), currentRecipe.requireExactMeta())){
                                                    e.setCancelled(false);
                                                    cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_held_right_click");
                                                } else {
                                                    if (cooldownManager.isCooldownPassed(e.getPlayer().getUniqueId(), "sound_craft")){
                                                        Sound sound = MaterialCosmeticManager.getInstance().getCraftWorkSound(baseVersion);
                                                        if (sound != null){
                                                            e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), sound, .3F, 1F);
                                                        }
                                                        e.getPlayer().getWorld().spawnParticle(Particle.BLOCK_DUST, e.getClickedBlock().getLocation().add(0.5, 1.2, 0.5), 3, e.getClickedBlock().getBlockData());
                                                        cooldownManager.setCooldown(e.getPlayer().getUniqueId(), 500, "sound_craft");
                                                    }
                                                }
                                            } else if (cooldownManager.getTimerResult(e.getPlayer().getUniqueId(), "time_held_right_click") >= currentRecipe.getCraftingTime()){
                                                if (ItemUtils.canCraft(currentRecipe, e.getPlayer(), currentRecipe.requireExactMeta())){
                                                    if (currentRecipe instanceof ItemImprovementRecipe){
                                                        PlayerItemTinkerEvent tinkerEvent = new PlayerItemTinkerEvent(e.getPlayer(), (ItemImprovementRecipe) currentRecipe, e.getClickedBlock(), true);
                                                        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(tinkerEvent);
                                                    } else {
                                                        PlayerItemClassTinkerEvent tinkerEvent = new PlayerItemClassTinkerEvent(e.getPlayer(), (ItemClassImprovementRecipe) currentRecipe, e.getClickedBlock(), true);
                                                        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(tinkerEvent);
                                                    }
                                                } else {
                                                    if (!errorNoIngredients.equals("")){
                                                        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(errorNoIngredients)));
                                                    }
                                                }
                                                cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_held_right_click");
                                            } else {
                                                if (cooldownManager.isCooldownPassed(e.getPlayer().getUniqueId(), "sound_craft")){
                                                    Sound sound = MaterialCosmeticManager.getInstance().getCraftWorkSound(baseVersion);
                                                    if (sound != null){
                                                        e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), sound, .3F, 1F);
                                                    }
                                                    cooldownManager.setCooldown(e.getPlayer().getUniqueId(), 500, "sound_craft");
                                                    e.getPlayer().getWorld().spawnParticle(Particle.BLOCK_DUST, e.getClickedBlock().getLocation().add(0.5, 1.2, 0.5), 3, e.getClickedBlock().getBlockData());
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                if (e.getAction() == Action.RIGHT_CLICK_BLOCK || e.getAction() == Action.RIGHT_CLICK_AIR) {
                    cooldownManager.startTimer(e.getPlayer().getUniqueId(), "time_since_right_click");
                }
            }
        }
    }

    private void resetFrequency(Player p){
        recipeFrequency.remove(p.getUniqueId());
    }

    private void incrementPlayerCraftFrequency(Player p, AbstractCustomCraftingRecipe recipe){
        if (recipe == null) return;
        if (recipe.getConsecutiveCrafts() == -1) return;
        RecipeFrequencyDO frequencyDO = recipeFrequency.get(p.getUniqueId());
        if (frequencyDO != null){
            frequencyDO.setFrequency(frequencyDO.getFrequency() + 1);
            recipeFrequency.put(p.getUniqueId(), frequencyDO);
        }
    }

    private boolean canPlayerCraft(PlayerInteractEvent e, AbstractCustomCraftingRecipe recipe){
        if (recipe == null) return true;
        if (recipe.getConsecutiveCrafts() == -1) return true;
        Player p = e.getPlayer();
        RecipeFrequencyDO frequencyDO;
        if (!recipeFrequency.containsKey(p.getUniqueId())) {
            recipeFrequency.put(p.getUniqueId(), new RecipeFrequencyDO(0, recipe));
        } else {
            frequencyDO = recipeFrequency.get(p.getUniqueId());
            if (!frequencyDO.getRecipe().getName().equals(recipe.getName())){
                // player is trying to craft something else
                recipeFrequency.put(p.getUniqueId(), new RecipeFrequencyDO(0, recipe));
            }
        }
        frequencyDO = recipeFrequency.get(p.getUniqueId());
        return frequencyDO.getFrequency() < recipe.getConsecutiveCrafts();
    }

    private static class RecipeFrequencyDO{
        private int frequency;
        private AbstractCustomCraftingRecipe recipe;

        public RecipeFrequencyDO(int frequency, AbstractCustomCraftingRecipe recipe) {
            this.frequency = frequency;
            this.recipe = recipe;
        }

        public AbstractCustomCraftingRecipe getRecipe() {
            return recipe;
        }

        public int getFrequency() {
            return frequency;
        }

        public void setRecipe(AbstractCustomCraftingRecipe recipe) {
            this.recipe = recipe;
        }

        public void setFrequency(int frequency) {
            this.frequency = frequency;
        }
    }

}
