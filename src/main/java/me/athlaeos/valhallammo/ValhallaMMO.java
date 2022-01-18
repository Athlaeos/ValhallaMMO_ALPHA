package me.athlaeos.valhallammo;

import me.athlaeos.valhallammo.commands.CommandManager;
import me.athlaeos.valhallammo.configs.ConfigUpdater;
import me.athlaeos.valhallammo.crafting.PlayerCustomCraftListener;
import me.athlaeos.valhallammo.crafting.PlayerShapedCraftListener;
import me.athlaeos.valhallammo.crafting.PlayerTinkerListener;
import me.athlaeos.valhallammo.crafting.dom.BrewingRecipe;
import me.athlaeos.valhallammo.listeners.*;
import me.athlaeos.valhallammo.managers.CustomRecipeManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public final class Main extends JavaPlugin {
    private static Main plugin;
    private InteractListener interactListener;
    private JoinListener joinListener;
    private ItemDamageListener itemDamageListener;
    private ItemMendListener itemMendListener;
    private PlayerShapedCraftListener playerCraftListener;
    private PlayerTinkerListener tinkerListener;
    private PlayerCustomCraftListener customCraftListener;
    private ItemDynamicallyModifiedListener itemModifiedListener;
    private VillagerInteractListener villagerInteractListener;
    private ProjectileShootListener projectileShootListener;
    private EntityDamagedListener entityDamagedListener;
    private PotionInventoryListener potionInventoryListener;

    @Override
    public void onEnable() {
        plugin = this;

        saveConfig("config.yml");
        saveConfig("recipes.yml");
        saveConfig("sounds.yml");
        saveConfig("skill_smithing.yml");
        saveConfig("skill_player.yml");
        saveConfig("progression_smithing.yml");
        saveConfig("progression_player.yml");
        saveConfig("villagers.yml");

        saveConfig("languages/en-us.yml");

        updateConfig("config.yml");
        updateConfig("languages/en-us.yml");

        CustomRecipeManager.getInstance().loadRecipesAsync();
        CustomRecipeManager.getInstance().disableRecipes();
        CommandManager.getInstance();

        // Plugin startup logic
        interactListener = new InteractListener();
        joinListener = new JoinListener();
        itemDamageListener = new ItemDamageListener();
        itemMendListener = new ItemMendListener();
        playerCraftListener = new PlayerShapedCraftListener();
        customCraftListener = new PlayerCustomCraftListener();
        tinkerListener = new PlayerTinkerListener();
        itemModifiedListener = new ItemDynamicallyModifiedListener();
        villagerInteractListener = new VillagerInteractListener();
        projectileShootListener = new ProjectileShootListener();
        entityDamagedListener = new EntityDamagedListener();
        potionInventoryListener = new PotionInventoryListener();
        this.getServer().getPluginManager().registerEvents(interactListener, this);
        this.getServer().getPluginManager().registerEvents(joinListener, this);
        this.getServer().getPluginManager().registerEvents(itemDamageListener, this);
        this.getServer().getPluginManager().registerEvents(itemMendListener, this);
        this.getServer().getPluginManager().registerEvents(playerCraftListener, this);
        this.getServer().getPluginManager().registerEvents(tinkerListener, this);
        this.getServer().getPluginManager().registerEvents(customCraftListener, this);
        this.getServer().getPluginManager().registerEvents(itemModifiedListener, this);
        this.getServer().getPluginManager().registerEvents(new MenuListener(), this);
        this.getServer().getPluginManager().registerEvents(new PlayerLevelSkillListener(), this);
        this.getServer().getPluginManager().registerEvents(villagerInteractListener, this);
        this.getServer().getPluginManager().registerEvents(projectileShootListener, this);
        this.getServer().getPluginManager().registerEvents(entityDamagedListener, this);
//        this.getServer().getPluginManager().registerEvents(potionInventoryListener, this);

        CustomRecipeManager.getInstance().registerBrewingRecipe(
                new BrewingRecipe("test", new ItemStack(Material.GREEN_DYE), Material.DIAMOND_CHESTPLATE, (inventory, item, ingredient) -> {
                    if (item.getType() == Material.DIAMOND_CHESTPLATE){
                        item.addUnsafeEnchantment(Enchantment.DURABILITY, 10);
                    } else {
                        System.out.println("item isnt diamond chestplate, its " + item.getType());
                    }
                }, false));

        SkillProgressionManager.getInstance().registerPerks();
    }

    @Override
    public void onDisable() {
        CustomRecipeManager.getInstance().saveRecipes(false);
        // Plugin shutdown logic
    }

    public InteractListener getInteractListener() {
        return interactListener;
    }

    public ItemDamageListener getItemDamageListener() {
        return itemDamageListener;
    }

    public JoinListener getJoinListener() {
        return joinListener;
    }

    public PlayerTinkerListener getTinkerListener() {
        return tinkerListener;
    }

    public PlayerCustomCraftListener getCustomCraftListener() {
        return customCraftListener;
    }

    public PlayerShapedCraftListener getPlayerCraftListener() {
        return playerCraftListener;
    }

    public ItemDynamicallyModifiedListener getItemModifiedListener() {
        return itemModifiedListener;
    }

    public VillagerInteractListener getVillagerInteractListener() {
        return villagerInteractListener;
    }

    public ProjectileShootListener getProjectileShootListener() {
        return projectileShootListener;
    }

    public EntityDamagedListener getEntityDamagedListener() {
        return entityDamagedListener;
    }

    public ItemMendListener getItemMendListener() {
        return itemMendListener;
    }

    public PotionInventoryListener getPotionInventoryListener() {
        return potionInventoryListener;
    }

    public static Main getPlugin() {
        return plugin;
    }

    private void saveConfig(String name){
        File config = new File(this.getDataFolder(), name);
        if (!config.exists()){
            this.saveResource(name, false);
        }
    }

    private void updateConfig(String name){
        File configFile = new File(getDataFolder(), name);
        try {
            ConfigUpdater.update(plugin, name, configFile, new ArrayList<>());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
