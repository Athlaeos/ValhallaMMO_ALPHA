package me.athlaeos.valhallammo.loottables;

import java.util.HashMap;
import java.util.Map;

public class LootTableManager {
    private static LootTableManager manager = null;

    private final Map<String, TieredLootTable> tieredLootTables = new HashMap<>();

    public LootTableManager(){

    }

    public void registerLootTable(TieredLootTable tieredLootTable){
        tieredLootTables.put(tieredLootTable.getName(), tieredLootTable);
    }

    public Map<String, TieredLootTable> getTieredLootTables() {
        return tieredLootTables;
    }

    public static LootTableManager getInstance(){
        if (manager == null) manager = new LootTableManager();
        return manager;
    }
}
