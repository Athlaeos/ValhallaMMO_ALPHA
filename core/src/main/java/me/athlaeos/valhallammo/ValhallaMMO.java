package me.athlaeos.valhallammo;

import me.athlaeos.valhallammo.commands.*;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.config.ConfigUpdater;
import me.athlaeos.valhallammo.crafting.PlayerClassTinkerListener;
import me.athlaeos.valhallammo.crafting.PlayerCustomCraftListener;
import me.athlaeos.valhallammo.crafting.PlayerShapedCraftListener;
import me.athlaeos.valhallammo.crafting.PlayerTinkerListener;
import me.athlaeos.valhallammo.dom.ArtificialGlow;
import me.athlaeos.valhallammo.listeners.*;
import me.athlaeos.valhallammo.loottables.LootManager;
import me.athlaeos.valhallammo.managers.*;
import me.athlaeos.valhallammo.nms.NMS;
import me.athlaeos.valhallammo.persistence.DatabaseConnection;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public final class ValhallaMMO extends JavaPlugin {
    private static ValhallaMMO plugin;
    private static boolean metrics_enabled;
    private static Metrics metrics = null;
    private static NMS nms;
    private static boolean is_spigot;
    private static boolean pack_enabled = false;
    private static boolean trinketsHooked = false;

    private final static Collection<String> worldBlacklist = new HashSet<>();

    private InteractListener interactListener;
    private JoinLeaveListener joinListener;
    private ItemDamageListener itemDamageListener;
    private ItemMendListener itemMendListener;
    private PlayerShapedCraftListener playerCraftListener;
    private PlayerTinkerListener tinkerListener;
    private PlayerClassTinkerListener classTinkerListener;
    private PlayerCustomCraftListener customCraftListener;
    private VillagerInteractListener villagerInteractListener;
    private ProjectileListener projectileShootListener;
    private EntityDamagedListener entityDamagedListener;
    private PotionInventoryListener potionInventoryListener;
    private PotionInventoryListenerUpdated potionInventoryListenerUpdated;
    private PotionBrewListener potionBrewListener;
    private ItemConsumeListener itemConsumeListener;
    private PotionEffectListener potionSplashListener;
    private PlayerEnchantListener playerEnchantListener;
    private PlayerExperienceAbsorbListener playerExperienceAbsorbListener;
    private BlockListener blocksListener;
    private FishingListener fishingListener;
    private EntityBreedListener breedListener;
    private EntityTargetingListener entityTargetingListener;
    private PlayerMovementListener movementListener;
    private HealingListener healingListener;
    private ChatListener chatListener;


    @Override
    public void onEnable() {
        plugin = this;
        pack_enabled = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("resource_pack_config_override");
//        if (!setupNMS()){
//            this.getLogger().severe("This version of ValhallaMMO is not compatible, versions at or under 1.16.4 are not supported.");
//            this.getServer().getPluginManager().disablePlugin(this);
//            return;
//        } else {
//            this.getServer().getPluginManager().registerEvents(nms, this);
//        }

        this.getServer().getConsoleSender().sendMessage(Utils.chat("&6[&eValhallaMMO&6] &fEnabling ValhallaMMO, this might take a bit..."));
        ArtificialGlow.registerGlow();

        saveAndUpdateConfig("config.yml");
        saveConfig("recipes/brewing_recipes.yml");
        saveConfig("recipes/improvement_recipes.yml");
        saveConfig("recipes/class_improvement_recipes.yml");
        saveConfig("recipes/crafting_recipes.yml");
        saveConfig("recipes/shaped_recipes.yml");
        saveConfig("recipes/cooking_recipes.yml");
        saveConfig("sounds.yml");
        saveConfig("tutorial_book.yml");
        saveAndUpdateConfig("skill_smithing.yml");
        saveAndUpdateConfig("skill_archery.yml");
        saveAndUpdateConfig("skill_alchemy.yml");
        saveAndUpdateConfig("skill_enchanting.yml");
        saveAndUpdateConfig("skill_player.yml");
        saveAndUpdateConfig("skill_farming.yml");
        saveAndUpdateConfig("skill_mining.yml");
        saveAndUpdateConfig("skill_landscaping.yml");
        saveAndUpdateConfig("skill_light_armor.yml");
        saveAndUpdateConfig("skill_heavy_armor.yml");
        saveAndUpdateConfig("skill_light_weapons.yml");
        saveAndUpdateConfig("skill_heavy_weapons.yml");
        saveConfig("progression_archery.yml");
        saveConfig("progression_smithing.yml");
        saveConfig("progression_alchemy.yml");
        saveConfig("progression_enchanting.yml");
        saveConfig("progression_player.yml");
        saveConfig("progression_farming.yml");
        saveConfig("progression_mining.yml");
        saveConfig("progression_landscaping.yml");
        saveConfig("progression_light_armor.yml");
        saveConfig("progression_heavy_armor.yml");
        saveConfig("progression_light_weapons.yml");
        saveConfig("progression_heavy_weapons.yml");
        saveConfig("villagers.yml");
        saveConfig("alchemy_transmutations.yml");
        saveConfig("block_interact_conversions.yml");

        saveAndUpdateConfig("languages/en-us.yml");

        saveConfig("loot_tables/farming_fishing.yml");
        saveConfig("loot_tables/landscaping_digging.yml");

        DatabaseConnection connection = DatabaseConnection.getDatabaseConnection();
        metrics_enabled = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("metrics", true);
        if (metrics_enabled){
            metrics = new Metrics(this, 14942);
            metrics.addCustomChart(new Metrics.SimplePie("using_database_for_player_data", () -> connection.getConnection() == null ? "No" : "Yes"));
        }

        is_spigot = ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("is_spigot");
        if (is_spigot) getServer().getLogger().fine("ValhallaMMO is registered to be using Spigot, some mechanics may work differently");

        ProfileManager.getManager();

        new SkillsCommand(this);

        PerkRewardsManager.getInstance();
        BlockConversionManager.getInstance();
        TutorialBook.getTutorialBookInstance().loadBookContents();
        ItemDictionaryManager.getInstance().loadItemsAsync();
        CustomRecipeManager.getInstance().loadRecipesAsync();
        CustomRecipeManager.getInstance().disableRecipes();
        LootManager.getInstance().loadLootTables();
        ValhallaCommandManager.getInstance();
        TransmutationManager.getInstance();

        // Plugin startup logic
        interactListener = (InteractListener) registerListener(new InteractListener(), "interact");
        joinListener = (JoinLeaveListener) registerListener(new JoinLeaveListener(), "join");
        itemDamageListener = (ItemDamageListener) registerListener(new ItemDamageListener(), "item_damage");
        itemMendListener = (ItemMendListener) registerListener(new ItemMendListener(), "item_mend");
        playerCraftListener = (PlayerShapedCraftListener) registerListener(new PlayerShapedCraftListener(), "shaped_craft");
        customCraftListener = (PlayerCustomCraftListener) registerListener(new PlayerCustomCraftListener(), "custom_craft");
        tinkerListener = (PlayerTinkerListener) registerListener(new PlayerTinkerListener(), "custom_tinker");
        classTinkerListener = (PlayerClassTinkerListener) registerListener(new PlayerClassTinkerListener(), "custom_tinker");
        villagerInteractListener = (VillagerInteractListener) registerListener(new VillagerInteractListener(), "villager_interact");
        projectileShootListener = (ProjectileListener) registerListener(new ProjectileListener(), "projectile_shoot");
        entityDamagedListener = (EntityDamagedListener) registerListener(new EntityDamagedListener(), "entity_damaged");
//        potionInventoryListener = (PotionInventoryListener) registerListener(new PotionInventoryListener(), "potion_inventory");
        potionInventoryListenerUpdated = (PotionInventoryListenerUpdated) registerListener(new PotionInventoryListenerUpdated(), "potion_inventory");
        potionBrewListener = (PotionBrewListener) registerListener(new PotionBrewListener(), "potion_brew");
        itemConsumeListener = (ItemConsumeListener) registerListener(new ItemConsumeListener(), "item_consume");
        potionSplashListener = (PotionEffectListener) registerListener(new PotionEffectListener(), "potion_splash");
        playerEnchantListener = (PlayerEnchantListener) registerListener(new PlayerEnchantListener(), "player_enchant");
        playerExperienceAbsorbListener = (PlayerExperienceAbsorbListener) registerListener(new PlayerExperienceAbsorbListener(), "player_experience");
        movementListener = (PlayerMovementListener) registerListener(new PlayerMovementListener(), "player_movement");
        blocksListener = (BlockListener) registerListener(new BlockListener(), "blocks");
        fishingListener = (FishingListener) registerListener(new FishingListener(), "fishing");
        breedListener = (EntityBreedListener) registerListener(new EntityBreedListener(), "breeding");
        entityTargetingListener = (EntityTargetingListener) registerListener(new EntityTargetingListener(), "targeting");
        healingListener = (HealingListener) registerListener(new HealingListener(), "healing");
        //this.getServer().getPluginManager().registerEvents(new FurnaceListener(), this);

        if (ConfigManager.getInstance().getConfig("config.yml").get().getBoolean("parties")){
            chatListener = (ChatListener) registerListener(new ChatListener(), "player_chat");
            new PartyCommand(this);
            new PartyChatCommand();
            new PlayerEXPShareListener(this);
            new AdminPartyCommand(this);
            new PartySpyCommand();
            PartyManager.getInstance().loadParties();
        }

        this.getServer().getPluginManager().registerEvents(new MenuListener(), this);
        this.getServer().getPluginManager().registerEvents(new EntitySpawnListener(), this);

        SkillProgressionManager.getInstance().registerPerks();

        worldBlacklist.addAll(ConfigManager.getInstance().getConfig("config.yml").get().getStringList("world_blacklist"));
    }

    private static boolean trinketsHookedLock = false;
    public static void setTrinketsHooked(boolean trinketsHooked) {
        if (!trinketsHookedLock){
            ValhallaMMO.getPlugin().getServer().getLogger().info("ValhallaTrinkets found! Trinkets now contribute to stats as well");
            ValhallaMMO.trinketsHooked = trinketsHooked;
            trinketsHookedLock = true;
        }
    }

    public static boolean isTrinketsHooked() {
        return trinketsHooked;
    }

    public static boolean isWorldBlacklisted(String world) {
        return worldBlacklist.contains(world);
    }

    public ChatListener getChatListener() {
        return chatListener;
    }

    public static boolean isMetricsEnabled() {
        return metrics_enabled;
    }

    private void saveAndUpdateConfig(String config){
        saveConfig(config);
        updateConfig(config);
    }

    private Listener registerListener(Listener l, String key){
        YamlConfiguration config = ConfigManager.getInstance().getConfig("config.yml").get();
        if (config.getBoolean("enabled_listeners." + key, true)){
            this.getServer().getPluginManager().registerEvents(l, this);
            return l;
        }
        return null;
    }

    public static boolean isSpigot() {
        return is_spigot;
    }

    @Override
    public void onDisable() {
        if (ItemDictionaryManager.isShouldSaveItems()){
            ItemDictionaryManager.getInstance().saveItems();
        } else {
            ValhallaMMO.getPlugin().getServer().getLogger().info("No item adjustments detected, not saving indexed items!");
        }
        if (CustomRecipeManager.isShouldSaveRecipes()){
            CustomRecipeManager.getInstance().saveRecipes(false);
        } else {
            ValhallaMMO.getPlugin().getServer().getLogger().info("No recipe adjustments detected, not saving recipes!");
        }
        LootManager.getInstance().saveLootTables();

        ProfileManager.getManager().savePlayerProfiles();

        if (PartyManager.getInstance().isEnableParties()) {
            PartyManager.getInstance().saveParties();
        }
// Plugin shutdown logic
    }

    public BlockListener getBlocksListener() {
        return blocksListener;
    }

    public InteractListener getInteractListener() {
        return interactListener;
    }

    public ItemDamageListener getItemDamageListener() {
        return itemDamageListener;
    }

    public JoinLeaveListener getJoinListener() {
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

    public VillagerInteractListener getVillagerInteractListener() {
        return villagerInteractListener;
    }

    public ProjectileListener getProjectileShootListener() {
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

    public PotionInventoryListenerUpdated getPotionInventoryListenerUpdated() {
        return potionInventoryListenerUpdated;
    }

    public PotionBrewListener getPotionBrewListener() {
        return potionBrewListener;
    }

    public ItemConsumeListener getItemConsumeListener() {
        return itemConsumeListener;
    }

    public PotionEffectListener getPotionSplashListener() {
        return potionSplashListener;
    }

    public PlayerClassTinkerListener getClassTinkerListener() {
        return classTinkerListener;
    }

    public EntityTargetingListener getEntityTargetingListener() {
        return entityTargetingListener;
    }

    public PlayerEnchantListener getPlayerEnchantListener() {
        return playerEnchantListener;
    }

    public PlayerExperienceAbsorbListener getPlayerExperienceAbsorbListener() {
        return playerExperienceAbsorbListener;
    }

    public EntityBreedListener getBreedListener() {
        return breedListener;
    }

    public FishingListener getFishingListener() {
        return fishingListener;
    }

    public PlayerMovementListener getMovementListener() {
        return movementListener;
    }

    public static ValhallaMMO getPlugin() {
        return plugin;
    }

    public HealingListener getHealingListener() {
        return healingListener;
    }

    public void saveConfig(String name){
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

    private boolean setupNMS() {
        try {
            String version = getServer().getClass().getPackage().getName().split("\\.")[3];
            Class<?> clazz = Class.forName("me.athlaeos.valhallammo.nms.NMS_" + version);

            if (NMS.class.isAssignableFrom(clazz)) {
                nms = (NMS) clazz.getDeclaredConstructor().newInstance();
            }

            return nms != null;
        } catch (Exception ignored) {
            return false;
        }
    }

    public static NMS getNMS() {
        return nms;
    }

    public static boolean isPackEnabled() {
        return pack_enabled;
    }

    public static void setPackEnabled(boolean pack_enabled) {
        ValhallaMMO.pack_enabled = pack_enabled;
        ConfigManager.getInstance().getConfig("config.yml").get().set("resource_pack_config_override", pack_enabled);
        ConfigManager.getInstance().getConfig("config.yml").save();
    }
}
