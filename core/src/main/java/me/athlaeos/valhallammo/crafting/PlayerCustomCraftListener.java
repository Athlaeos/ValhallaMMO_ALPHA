package me.athlaeos.valhallammo.crafting;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.events.PlayerCustomCraftEvent;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.managers.CooldownManager;
import me.athlaeos.valhallammo.managers.MaterialCosmeticManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.skills.smithing.SmithingSkill;
import me.athlaeos.valhallammo.utility.ItemUtils;
import me.athlaeos.valhallammo.utility.Utils;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PlayerCustomCraftListener implements Listener {
    CooldownManager cooldownManager = CooldownManager.getInstance();
    private final boolean spawnOnTopOfBlock;
    private final String errorNoSpace;
    private final String errorTinkeringFailed;

    public PlayerCustomCraftListener(){
        spawnOnTopOfBlock = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("craft_item_drop");
        errorNoSpace = TranslationManager.getInstance().getTranslation("error_crafting_no_space");
        errorTinkeringFailed = TranslationManager.getInstance().getTranslation("error_crafting_tinker_fail");
    }

    @EventHandler(priority =EventPriority.HIGHEST)
    public void onPlayerCustomCraft(PlayerCustomCraftEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getPlayer().getWorld().getName())) return;
        if (!e.isCancelled()){
            boolean playerHasSpace = e.getPlayer().getInventory().firstEmpty() > -1;
            if (playerHasSpace || spawnOnTopOfBlock){
                ItemStack result = e.getRecipe().getResult().clone();
                // Modify item based on the recipe's improvement modifiers
                List<DynamicItemModifier> modifiers = new ArrayList<>(e.getRecipe().getItemModifers());
                modifiers.sort(Comparator.comparingInt((DynamicItemModifier a) -> a.getPriority().getPriorityRating()));
                for (DynamicItemModifier modifier : modifiers){
                    if (result == null) break;
                    result = modifier.processItem(e.getPlayer(), result);
                }
                if (result != null){
                    if (e.getRecipe().getValidation() != null){
                        e.getRecipe().getValidation().executeAfter(e.getCraftingStation());
                    }
                    if (e.getRecipe().breakStation()){
                        BlockBreakEvent event = new BlockBreakEvent(e.getCraftingStation(), e.getPlayer());
                        ValhallaMMO.getPlugin().getServer().getPluginManager().callEvent(event);
                        if (event.isCancelled()){
                            return;
                        } else {
                            event.getBlock().setType(Material.AIR);
                        }
                    }
                    // player has inventory space and crafting requirements are met, ingredients are removed and result is added
                    ItemUtils.removeItems(e.getPlayer(), e.getRecipe().getIngredients(), e.getRecipe().requireExactMeta());

                    if (spawnOnTopOfBlock){
                        Item itemDrop = e.getPlayer().getWorld().dropItem(e.getCraftingStation().getLocation().add(0.5, 1.2, 0.5), result);
                        itemDrop.setPickupDelay(0);
                        itemDrop.setOwner(e.getPlayer().getUniqueId());
                        itemDrop.setThrower(e.getPlayer().getUniqueId());
                    } else {
                        e.getPlayer().getInventory().addItem(result);
                    }
                    Sound sound = MaterialCosmeticManager.getInstance().getCraftFinishSounds(e.getCraftingStation().getType());
                    if (sound != null){
                        e.getPlayer().getWorld().playSound(e.getPlayer().getLocation(), sound, .7F, 1F);
                    }
                    e.getPlayer().getWorld().spawnParticle(Particle.FIREWORKS_SPARK, e.getCraftingStation().getLocation().add(0.5, 1, 0.5), 15);

                    Skill skill = SkillProgressionManager.getInstance().getSkill("SMITHING");
                    if (skill != null){
                        if (skill instanceof SmithingSkill){
                            double expReward = ((SmithingSkill) skill).expForCraftedItem(e.getPlayer(), result);
                            skill.addEXP(e.getPlayer(), expReward, false, PlayerSkillExperienceGainEvent.ExperienceGainReason.SKILL_ACTION);
                        }
                    }
                } else {
                    // The recipe is cancelled
                    if (!errorTinkeringFailed.equals("")){
                        e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(errorTinkeringFailed)));
                    }
                }
                cooldownManager.setCooldown(e.getPlayer().getUniqueId(), 750, "cancel_block_interactions");
            } else {
                if (!errorNoSpace.equals("")){
                    e.getPlayer().spigot().sendMessage(ChatMessageType.ACTION_BAR, new TextComponent(Utils.chat(errorNoSpace)));
                }
            }
        }
    }
}
