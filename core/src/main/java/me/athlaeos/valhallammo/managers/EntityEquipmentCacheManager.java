package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.EntityProperties;
import me.athlaeos.valhallammo.utility.EntityUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.*;

public class EntityEquipmentCacheManager {
    private static EntityEquipmentCacheManager manager = null;
    private static final long CACHE_REFRESH_DELAY = 1500;
    private static final long CACHE_CLEANUP_DELAY = 600000;
    private final Map<UUID, EntityProperties> cachedEquipment = new HashMap<>();
    private final Map<UUID, Long> lastCacheRefreshAt = new HashMap<>();
    private long lastCacheCleanupAt = System.currentTimeMillis();

    public static EntityEquipmentCacheManager getInstance(){
        if (manager == null) manager = new EntityEquipmentCacheManager();
        return manager;
    }

    public EntityProperties getAndCacheEquipment(LivingEntity entity){
        cleanCache();
        if (!cachedEquipment.containsKey(entity.getUniqueId())) {
            cachedEquipment.put(entity.getUniqueId(), EntityUtils.getEntityProperties(entity, true, true, true, true));
            lastCacheRefreshAt.put(entity.getUniqueId(), System.currentTimeMillis());
        } else if (lastCacheRefreshAt.containsKey(entity.getUniqueId())){
            if (lastCacheRefreshAt.get(entity.getUniqueId()) + CACHE_REFRESH_DELAY < System.currentTimeMillis()){
                // last cache refresh was longer than cacheRefreshDelay milliseconds ago, refresh cached equipment
                cachedEquipment.put(entity.getUniqueId(), EntityUtils.getEntityProperties(entity, true, true, true, true));
                lastCacheRefreshAt.put(entity.getUniqueId(), System.currentTimeMillis());
            }
        }
        return cachedEquipment.get(entity.getUniqueId());
    }

    public void cleanCache(){
        if (lastCacheCleanupAt + CACHE_CLEANUP_DELAY < System.currentTimeMillis()){
            Collection<UUID> uuids = new HashSet<>(cachedEquipment.keySet());
            uuids.forEach(u -> {
                Entity entity = ValhallaMMO.getPlugin().getServer().getEntity(u);
                if (entity != null){
                    if (!entity.isValid()){
                        cachedEquipment.remove(u);
                        lastCacheRefreshAt.remove(u);
                    }
                } else {
                    cachedEquipment.remove(u);
                    lastCacheRefreshAt.remove(u);
                }
            });
            lastCacheCleanupAt = System.currentTimeMillis();
        }
    }

    public Map<UUID, EntityProperties> getCachedEquipment() {
        return cachedEquipment;
    }
}
