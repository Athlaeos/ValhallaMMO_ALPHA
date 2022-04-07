package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.managers.CooldownManager;
import me.athlaeos.valhallammo.items.EquipmentClass;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantInventory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VillagerInteractListener implements Listener {
    private Map<Villager.Profession, Map<EquipmentClass, List<String>>> matClassVillagerDialogue;
    private Map<Villager.Profession, Map<Material, List<String>>> materialVillagerDialogue;
    private int dialogue_delay;
    private static VillagerInteractListener listener;

    public VillagerInteractListener(){
        listener = this;
        loadConfig();
    }

    public static VillagerInteractListener getListener() {
        return listener;
    }

    public void reload(){
        loadConfig();
    }

    @EventHandler(priority= EventPriority.LOWEST)
    public void onVillagerInteract(InventoryOpenEvent e){
        if (e.getInventory() instanceof MerchantInventory){
            if (CooldownManager.getInstance().isCooldownPassed(e.getPlayer().getUniqueId(), "villager_dialogue_cooldown")){
                ItemStack clickedWith = e.getPlayer().getInventory().getItemInMainHand();
                if (clickedWith.getType() != Material.AIR){
                    if (e.getInventory().getHolder() instanceof Villager){
                        Villager clickedVillager = (Villager) e.getInventory().getHolder();

                        EquipmentClass clickedWithClass = EquipmentClass.getClass(clickedWith.getType());
                        List<String> possibleDialogue = new ArrayList<>();
                        if (clickedWithClass != null){
                            if (matClassVillagerDialogue.containsKey(clickedVillager.getProfession())){
                                possibleDialogue.addAll(matClassVillagerDialogue.get(clickedVillager.getProfession()).get(clickedWithClass));
                            }
                        } else {
                            if (materialVillagerDialogue.containsKey(clickedVillager.getProfession())){
                                possibleDialogue.addAll(materialVillagerDialogue.get(clickedVillager.getProfession()).get(clickedWith.getType()));
                            }
                        }
                        if (possibleDialogue.size() > 0){
                            e.setCancelled(true);
                            e.getPlayer().sendMessage(Utils.chat(possibleDialogue.get(Utils.getRandom().nextInt(possibleDialogue.size()))));
                            if (e.getPlayer() instanceof Player){
                                ((Player) e.getPlayer()).playSound(e.getPlayer().getLocation(), Sound.ENTITY_VILLAGER_TRADE, 1F, 1F);
                            }
                            CooldownManager.getInstance().setCooldown(e.getPlayer().getUniqueId(), dialogue_delay, "villager_dialogue_cooldown");
                        }
                    }
                }
            }
        }
    }

    public void loadConfig(){
        matClassVillagerDialogue = new HashMap<>();
        materialVillagerDialogue = new HashMap<>();
        YamlConfiguration config = ConfigManager.getInstance().getConfig("villagers.yml").get();
        dialogue_delay = config.getInt("dialogue_delay");

        ConfigurationSection professionSection = config.getConfigurationSection("villagers");
        if (professionSection != null){
            for (String professionString : professionSection.getKeys(false)){
                try {
                    Villager.Profession profession = Villager.Profession.valueOf(professionString);
                    Map<EquipmentClass, List<String>> matClassProfessionDialogue = new HashMap<>();
                    Map<Material, List<String>> materialProfessionDialogue = new HashMap<>();
                    ConfigurationSection dialogueSection = config.getConfigurationSection("villagers." + professionString + ".dialogue");
                    if (dialogueSection != null){
                        for (String materials : dialogueSection.getKeys(false)){
                            String[] splitConditions = materials.split("\\|");
                            List<String> possibleDialogue = config.getStringList("villagers." + professionString + ".dialogue." + materials);
                            if (possibleDialogue.size() == 0) continue;
                            for (String material : splitConditions){
                                try {
                                    EquipmentClass matClass = EquipmentClass.valueOf(material);
                                    List<String> currentDialogue = matClassProfessionDialogue.get(matClass);
                                    if (currentDialogue == null) currentDialogue = new ArrayList<>();
                                    currentDialogue.addAll(possibleDialogue);
                                    matClassProfessionDialogue.put(matClass, currentDialogue);
                                } catch (IllegalArgumentException ignored){
                                    try {
                                        Material specificMaterial = Material.valueOf(material);
                                        List<String> currentDialogue = materialProfessionDialogue.get(specificMaterial);
                                        if (currentDialogue == null) currentDialogue = new ArrayList<>();
                                        currentDialogue.addAll(possibleDialogue);
                                        materialProfessionDialogue.put(specificMaterial, currentDialogue);
                                    } catch (IllegalArgumentException ignored1){
                                        ValhallaMMO.getPlugin().getLogger().warning("[ValhallaMMO] invalid material or material class " + material + " given in villagers.yml");
                                    }
                                }
                            }
                        }
                    }
                    if (matClassProfessionDialogue.size() > 0){
                        matClassVillagerDialogue.put(profession, matClassProfessionDialogue);
                    }
                    if (materialProfessionDialogue.size() > 0){
                        materialVillagerDialogue.put(profession, materialProfessionDialogue);
                    }
                } catch (IllegalArgumentException ignored){
                }
            }
        }
    }
}
