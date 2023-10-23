package me.athlaeos.valhallammo.loottables;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.crafting.DynamicItemModifierManager;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DuoArgDynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.DynamicItemModifier;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.ModifierPriority;
import me.athlaeos.valhallammo.crafting.dynamicitemmodifiers.TripleArgDynamicItemModifier;
import me.athlaeos.valhallammo.loottables.chance_based_block_loot.*;
import me.athlaeos.valhallammo.loottables.chance_based_entity_loot.ChancedFarmingAnimalLootTable;
import me.athlaeos.valhallammo.loottables.chance_based_entity_loot.ChancedHeavyWeaponsLootTable;
import me.athlaeos.valhallammo.loottables.chance_based_entity_loot.ChancedLightWeaponsLootTable;
import me.athlaeos.valhallammo.loottables.chance_based_entity_loot.GlobalChancedEntityLootTable;
import me.athlaeos.valhallammo.loottables.tiered_loot_tables.TieredFishingLootTable;
import me.athlaeos.valhallammo.managers.TranslationManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Material;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class LootManager {
    private static LootManager manager = null;
    private static boolean shouldSave = false;

    private final Map<String, TieredLootTable> tieredLootTables = new HashMap<>();
    private final Map<String, ChancedBlockLootTable> chancedBlockLootTables = new HashMap<>();
    private final Map<String, ChancedEntityLootTable> chancedEntityLootTables = new HashMap<>();

    public static boolean shouldSave() {
        return shouldSave;
    }
    public static void setShouldSave(){
        shouldSave = true;
    }

    public LootManager(){
        registerLootTable(new TieredFishingLootTable());

        registerLootTable(new ChancedFarmingCropsLootTable());
        registerLootTable(new ChancedMiningLootTable());
        registerLootTable(new ChancedBlastMiningLootTable());
        registerLootTable(new ChancedDiggingLootTable());
        registerLootTable(new ChancedWoodstrippingLootTable());
        registerLootTable(new ChancedWoodcuttingLootTable());
        registerLootTable(new ChancedFarmingAnimalLootTable());
        registerLootTable(new GlobalChancedBlockLootTable());
        registerLootTable(new GlobalChancedEntityLootTable());
        registerLootTable(new ChancedLightWeaponsLootTable());
        registerLootTable(new ChancedHeavyWeaponsLootTable());

    }

    public boolean registerLootTable(TieredLootTable tieredLootTable){
        if (chancedBlockLootTables.containsKey(tieredLootTable.getName()) || chancedEntityLootTables.containsKey(tieredLootTable.getName())) return false;
        tieredLootTables.put(tieredLootTable.getName(), tieredLootTable);
        return true;
    }

    public boolean registerLootTable(ChancedBlockLootTable chancedBlockLootTable){
        if (tieredLootTables.containsKey(chancedBlockLootTable.getName()) || chancedEntityLootTables.containsKey(chancedBlockLootTable.getName())) return false;
        chancedBlockLootTables.put(chancedBlockLootTable.getName(), chancedBlockLootTable);
        return true;
    }

    public boolean registerLootTable(ChancedEntityLootTable chancedEntityLootTable){
        if (tieredLootTables.containsKey(chancedEntityLootTable.getName()) || chancedBlockLootTables.containsKey(chancedEntityLootTable.getName())) return false;
        chancedEntityLootTables.put(chancedEntityLootTable.getName(), chancedEntityLootTable);
        return true;
    }

    public void loadLootTables(){
        for (TieredLootTable table : tieredLootTables.values()){
            loadLootTable(table, "loot_tables/" + table.getName() + ".yml");
        }
        for (ChancedBlockLootTable table : chancedBlockLootTables.values()){
            loadLootTable(table, "loot_tables/" + table.getName() + ".yml");
        }
        for (ChancedEntityLootTable table : chancedEntityLootTables.values()){
            loadLootTable(table, "loot_tables/" + table.getName() + ".yml");
        }
    }

    public void saveLootTables(){
        for (TieredLootTable table : tieredLootTables.values()){
            saveLootTable(table, "loot_tables/" + table.getName() + ".yml");
        }
        for (ChancedBlockLootTable table : chancedBlockLootTables.values()){
            saveLootTable(table, "loot_tables/" + table.getName() + ".yml");
        }
        for (ChancedEntityLootTable table : chancedEntityLootTables.values()){
            saveLootTable(table, "loot_tables/" + table.getName() + ".yml");
        }
    }

    public void loadLootTable(ChancedBlockLootTable chancedBlockLootTable, String configPath){
        if (chancedBlockLootTable == null) return;

        YamlConfiguration config = ConfigManager.getInstance().getConfig(configPath).get();
        ConfigurationSection section = config.getConfigurationSection("entries");
        if (section == null) return;
        for (String entry : section.getKeys(false)){
            try {
                String name = config.getString("entries." + entry + ".name");
                if (name == null) throw new IllegalStateException("Loot entry name cannot be null");
                ItemStack drop = config.getItemStack("entries." + entry + ".drop");
                if (drop == null) throw new IllegalStateException("Loot entry drop cannot be null");
                drop = TranslationManager.getInstance().translateItemStack(drop);
                Set<Biome> biomeFilter = new HashSet<>();
                for (String s : config.getStringList("entries." + entry + ".biome_filter")){
                    try {
                        biomeFilter.add(Biome.valueOf(s));
                    } catch (IllegalArgumentException ignored){
                        ValhallaMMO.getPlugin().getLogger().warning(": Invalid biome " + s + " found in " + configPath + " at entries." + entry + ".biome_filter. Skipped");
                    }
                }
                Set<String> regionFilter = new HashSet<>(config.getStringList("entries." + entry + ".region_filter"));

                List<DynamicItemModifier> modifiers = new ArrayList<>();
                ConfigurationSection modifierSection = config.getConfigurationSection("entries." + entry + ".modifiers");
                if (modifierSection != null){
                    for (String mod : modifierSection.getKeys(false)){
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("entries." + entry + ".modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        double strength = config.getDouble("entries." + entry + ".modifiers." + mod + ".strength");
                        DynamicItemModifier modifier;
                        if (Utils.doesPathExist(config, "entries." + entry + ".modifiers." + mod, "strength2")){
                            // assuming at least DuoArgModifier
                            double strength2 = config.getDouble("entries." + entry + ".modifiers." + mod + ".strength2");
                            if (Utils.doesPathExist(config, "entries." + entry + ".modifiers." + mod, "strength3")){
                                // assuming TripleArgModifier
                                double strength3 = config.getDouble("entries." + entry + ".modifiers." + mod + ".strength3");
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, strength3, priority);
                            } else {
                                // assuming DoubleArgModifier
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, priority);
                            }
                        } else {
                            modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
                        }
                        if (modifier != null){
                            modifiers.add(modifier);
                        }
                    }
                }

                Material block;
                try {
                    String b = config.getString("entries." + entry + ".block");
                    if (b == null) {
                        ValhallaMMO.getPlugin().getLogger().severe(": Attempting to load loot table " + chancedBlockLootTable.getName() + ", but the loot entries did not match its type. Quit loading loot table.");
                        break;
                    }
                    block = Material.valueOf(b);
                    if (!block.isBlock()) throw new IllegalArgumentException();
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning(": Material at entries." + entry + ".block was not a valid block. Skipped entry.");
                    continue;
                }
                double chance = config.getDouble("entries." + entry + ".drop_chance", -1);
                if (chance < 0){
                    ValhallaMMO.getPlugin().getLogger().severe(": Drop chance at entries." + entry + ".drop_chance was absent/invalid. This implies the loot table entries may not be for its respective intended loot table. Quit loading loot table.");
                    break;
                }
                boolean overwriteNaturalDrops = config.getBoolean("entries." + entry + ".overwrite_natural_drops", false);

                chancedBlockLootTable.registerEntry(new ChancedBlockLootEntry(
                        name, block, drop, overwriteNaturalDrops, chance, modifiers, biomeFilter, regionFilter
                ));
            } catch (IllegalStateException e){
                ValhallaMMO.getPlugin().getLogger().warning(": " + e.getMessage());
            }
        }
    }

    public void loadLootTable(ChancedEntityLootTable chancedEntityLootTable, String configPath){
        if (chancedEntityLootTable == null) return;

        YamlConfiguration config = ConfigManager.getInstance().getConfig(configPath).get();
        ConfigurationSection section = config.getConfigurationSection("entries");
        if (section == null) return;
        for (String entry : section.getKeys(false)){
            try {
                String name = config.getString("entries." + entry + ".name");
                if (name == null) throw new IllegalStateException("Loot entry name cannot be null");
                ItemStack drop = config.getItemStack("entries." + entry + ".drop");
                if (drop == null) throw new IllegalStateException("Loot entry drop cannot be null");
                drop = TranslationManager.getInstance().translateItemStack(drop);
                Set<Biome> biomeFilter = new HashSet<>();
                for (String s : config.getStringList("entries." + entry + ".biome_filter")){
                    try {
                        biomeFilter.add(Biome.valueOf(s));
                    } catch (IllegalArgumentException ignored){
                        ValhallaMMO.getPlugin().getLogger().warning(": Invalid biome " + s + " found in " + configPath + " at entries." + entry + ".biome_filter. Skipped");
                    }
                }
                Set<String> regionFilter = new HashSet<>(config.getStringList("entries." + entry + ".region_filter"));

                List<DynamicItemModifier> modifiers = new ArrayList<>();
                ConfigurationSection modifierSection = config.getConfigurationSection("entries." + entry + ".modifiers");
                if (modifierSection != null){
                    for (String mod : modifierSection.getKeys(false)){
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("entries." + entry + ".modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        double strength = config.getDouble("entries." + entry + ".modifiers." + mod + ".strength");
                        DynamicItemModifier modifier;
                        if (Utils.doesPathExist(config, "entries." + entry + ".modifiers." + mod, "strength2")){
                            // assuming at least DuoArgModifier
                            double strength2 = config.getDouble("entries." + entry + ".modifiers." + mod + ".strength2");
                            if (Utils.doesPathExist(config, "entries." + entry + ".modifiers." + mod, "strength3")){
                                // assuming TripleArgModifier
                                double strength3 = config.getDouble("entries." + entry + ".modifiers." + mod + ".strength3");
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, strength3, priority);
                            } else {
                                // assuming DoubleArgModifier
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, priority);
                            }
                        } else {
                            modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
                        }
                        if (modifier != null){
                            modifiers.add(modifier);
                        }
                    }
                }

                EntityType entity;
                try {
                    String b = config.getString("entries." + entry + ".entity");
                    if (b == null) {
                        ValhallaMMO.getPlugin().getLogger().severe(": Attempting to load loot table " + chancedEntityLootTable.getName() + ", but the loot entries did not match its type. Quit loading loot table.");
                        break;
                    }
                    entity = EntityType.valueOf(b);
                } catch (IllegalArgumentException ignored){
                    ValhallaMMO.getPlugin().getLogger().warning(": Material at entries." + entry + ".block was not a valid block. Skipped entry.");
                    continue;
                }
                double chance = config.getDouble("entries." + entry + ".drop_chance", -1);
                if (chance < 0){
                    ValhallaMMO.getPlugin().getLogger().severe(": Drop chance at entries." + entry + ".drop_chance was absent/invalid. This implies the loot table entries may not be for its respective intended loot table. Quit loading loot table.");
                    break;
                }
                boolean overwriteNaturalDrops = config.getBoolean("entries." + entry + ".overwrite_drops", false);

                chancedEntityLootTable.registerEntry(new ChancedEntityLootEntry(
                        name, entity, drop, overwriteNaturalDrops, chance, modifiers, biomeFilter, regionFilter
                ));
            } catch (IllegalStateException e){
                ValhallaMMO.getPlugin().getLogger().warning(": " + e.getMessage());
            }
        }
    }

    public void loadLootTable(TieredLootTable tieredLootTable, String configPath){
        if (tieredLootTable == null) return;

        YamlConfiguration config = ConfigManager.getInstance().getConfig(configPath).get();
        ConfigurationSection section = config.getConfigurationSection("entries");
        if (section == null) return;
        for (String entry : section.getKeys(false)){
            try {
                String name = config.getString("entries." + entry + ".name");
                if (name == null) throw new IllegalStateException("Loot entry name cannot be null");
                ItemStack drop = config.getItemStack("entries." + entry + ".drop");
                if (drop == null) throw new IllegalStateException("Loot entry drop cannot be null");
                drop = TranslationManager.getInstance().translateItemStack(drop);
                Set<Biome> biomeFilter = new HashSet<>();
                for (String s : config.getStringList("entries." + entry + ".biome_filter")){
                    try {
                        biomeFilter.add(Biome.valueOf(s));
                    } catch (IllegalArgumentException ignored){
                        ValhallaMMO.getPlugin().getLogger().warning(": Invalid biome " + s + " found in " + configPath + " at entries." + entry + ".biome_filter. Skipped");
                    }
                }
                Set<String> regionFilter = new HashSet<>(config.getStringList("entries." + entry + ".region_filter"));

                List<DynamicItemModifier> modifiers = new ArrayList<>();
                ConfigurationSection modifierSection = config.getConfigurationSection("entries." + entry + ".modifiers");
                if (modifierSection != null){
                    for (String mod : modifierSection.getKeys(false)){
                        ModifierPriority priority = ModifierPriority.NEUTRAL;
                        try {
                            String stringPriority = config.getString("entries." + entry + ".modifiers." + mod + ".priority");
                            if (stringPriority == null) throw new IllegalArgumentException();
                            priority = ModifierPriority.valueOf(stringPriority);
                        } catch (IllegalArgumentException ignored){
                        }
                        double strength = config.getDouble("entries." + entry + ".modifiers." + mod + ".strength");
                        DynamicItemModifier modifier;
                        if (Utils.doesPathExist(config, "entries." + entry + ".modifiers." + mod, "strength2")){
                            // assuming at least DuoArgModifier
                            double strength2 = config.getDouble("entries." + entry + ".modifiers." + mod + ".strength2");
                            if (Utils.doesPathExist(config, "entries." + entry + ".modifiers." + mod, "strength3")){
                                // assuming TripleArgModifier
                                double strength3 = config.getDouble("entries." + entry + ".modifiers." + mod + ".strength3");
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, strength3, priority);
                            } else {
                                // assuming DoubleArgModifier
                                modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, strength2, priority);
                            }
                        } else {
                            modifier = DynamicItemModifierManager.getInstance().createModifier(mod, strength, priority);
                        }
                        if (modifier != null){
                            modifiers.add(modifier);
                        }
                    }
                }

                int weight = config.getInt("entries." + entry + ".weight", -1);
                if (weight < 0){
                    ValhallaMMO.getPlugin().getLogger().severe(": Weight at entries." + entry + ".weight was absent/invalid. This implies the loot table entries may not be for its respective intended loot table. Quit loading loot table.");
                    break;
                }
                int tier = config.getInt("entries." + entry + ".tier", -1);
                if (tier < 0){
                    ValhallaMMO.getPlugin().getLogger().severe(": Tier at entries." + entry + ".tier was absent/invalid. This implies the loot table entries may not be for its respective intended loot table. Quit loading loot table.");
                    break;
                }

                tieredLootTable.registerEntry(new TieredLootEntry(
                        name, tier, drop, weight, modifiers, biomeFilter, regionFilter
                ));
            } catch (IllegalStateException e){
                ValhallaMMO.getPlugin().getLogger().warning(": " + e.getMessage());
            }
        }
    }

    public void saveLootTable(TieredLootTable table, String configPath){
        YamlConfiguration config = ConfigManager.getInstance().getConfig(configPath).get();
        for (TieredLootEntry entry : table.getAllLootEntries().values()){
            config.set("entries." + entry.getName() + ".name", entry.getName());
            config.set("entries." + entry.getName() + ".drop", entry.getLoot());
            config.set("entries." + entry.getName() + ".biome_filter", entry.getBiomeFilter().stream().map(Biome::name).collect(Collectors.toList()));
            config.set("entries." + entry.getName() + ".region_filter", entry.getRegionFilter());
            config.set("entries." + entry.getName() + ".tier", entry.getTier());
            config.set("entries." + entry.getName() + ".weight", entry.getWeight());
            for (DynamicItemModifier modifier : entry.getModifiers()){
                if (modifier instanceof TripleArgDynamicItemModifier){
                    config.set("entries." + entry.getName() + ".modifiers." + modifier.getName() + ".strength3", Utils.round(((TripleArgDynamicItemModifier) modifier).getStrength3(), 6));
                }
                if (modifier instanceof DuoArgDynamicItemModifier){
                    config.set("entries." + entry.getName() + ".modifiers." + modifier.getName() + ".strength2", Utils.round(((DuoArgDynamicItemModifier) modifier).getStrength2(), 6));
                }
                config.set("entries." + entry.getName() + ".modifiers." + modifier.getName() + ".strength", Utils.round(modifier.getStrength(), 6));
                config.set("entries." + entry.getName() + ".modifiers." + modifier.getName() + ".priority", modifier.getPriority().toString());
            }
        }
        ConfigManager.getInstance().saveConfig(configPath);
    }

    public void saveLootTable(ChancedBlockLootTable table, String configPath){
        YamlConfiguration config = ConfigManager.getInstance().getConfig(configPath).get();
        for (ChancedBlockLootEntry entry : table.getAllLootEntries().values()){
            config.set("entries." + entry.getName() + ".name", entry.getName());
            config.set("entries." + entry.getName() + ".drop", entry.getLoot());
            config.set("entries." + entry.getName() + ".biome_filter", entry.getBiomeFilter().stream().map(Biome::name).collect(Collectors.toList()));
            config.set("entries." + entry.getName() + ".region_filter", entry.getRegionFilter());
            config.set("entries." + entry.getName() + ".drop_chance", Utils.round(entry.getChance(), 6));
            config.set("entries." + entry.getName() + ".block", entry.getBlock().toString());
            config.set("entries." + entry.getName() + ".overwrite_drops", entry.isOverwriteNaturalDrops());
            for (DynamicItemModifier modifier : entry.getModifiers()){
                if (modifier instanceof TripleArgDynamicItemModifier){
                    config.set("entries." + entry.getName() + ".modifiers." + modifier.getName() + ".strength3", Utils.round(((TripleArgDynamicItemModifier) modifier).getStrength3(), 6));
                }
                if (modifier instanceof DuoArgDynamicItemModifier){
                    config.set("entries." + entry.getName() + ".modifiers." + modifier.getName() + ".strength2", Utils.round(((DuoArgDynamicItemModifier) modifier).getStrength2(), 6));
                }
                config.set("entries." + entry.getName() + ".modifiers." + modifier.getName() + ".strength", Utils.round(modifier.getStrength(), 6));
                config.set("entries." + entry.getName() + ".modifiers." + modifier.getName() + ".priority", modifier.getPriority().toString());
            }
        }
        ConfigManager.getInstance().saveConfig(configPath);
    }

    public void saveLootTable(ChancedEntityLootTable table, String configPath){
        YamlConfiguration config = ConfigManager.getInstance().getConfig(configPath).get();
        for (ChancedEntityLootEntry entry : table.getAllLootEntries().values()){
            config.set("entries." + entry.getName() + ".name", entry.getName());
            config.set("entries." + entry.getName() + ".drop", entry.getLoot());
            config.set("entries." + entry.getName() + ".biome_filter", entry.getBiomeFilter().stream().map(Biome::name).collect(Collectors.toList()));
            config.set("entries." + entry.getName() + ".region_filter", entry.getRegionFilter());
            config.set("entries." + entry.getName() + ".drop_chance", Utils.round(entry.getChance(), 6));
            config.set("entries." + entry.getName() + ".entity", entry.getEntity().toString());
            config.set("entries." + entry.getName() + ".overwrite_drops", entry.isOverwriteNaturalDrops());
            for (DynamicItemModifier modifier : entry.getModifiers()){
                if (modifier instanceof TripleArgDynamicItemModifier){
                    config.set("entries." + entry.getName() + ".modifiers." + modifier.getName() + ".strength3", Utils.round(((TripleArgDynamicItemModifier) modifier).getStrength3(), 6));
                }
                if (modifier instanceof DuoArgDynamicItemModifier){
                    config.set("entries." + entry.getName() + ".modifiers." + modifier.getName() + ".strength2", Utils.round(((DuoArgDynamicItemModifier) modifier).getStrength2(), 6));
                }
                config.set("entries." + entry.getName() + ".modifiers." + modifier.getName() + ".strength", Utils.round(modifier.getStrength(), 6));
                config.set("entries." + entry.getName() + ".modifiers." + modifier.getName() + ".priority", modifier.getPriority().toString());
            }
        }
        ConfigManager.getInstance().saveConfig(configPath);
    }

    public Map<String, TieredLootTable> getTieredLootTables() {
        return tieredLootTables;
    }

    public Map<String, ChancedBlockLootTable> getChancedBlockLootTables() {
        return chancedBlockLootTables;
    }

    public Map<String, ChancedEntityLootTable> getChancedEntityLootTables() {
        return chancedEntityLootTables;
    }

    public static LootManager getInstance(){
        if (manager == null) manager = new LootManager();
        return manager;
    }
}
