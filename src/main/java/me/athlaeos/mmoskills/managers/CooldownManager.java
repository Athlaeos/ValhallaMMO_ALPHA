package me.athlaeos.mmoskills.managers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CooldownManager {

    private static CooldownManager manager = null;

    private Map<String, Map<UUID, Long>> allCooldowns = new HashMap<>();
    private Map<String, Map<UUID, Long>> allTimers = new HashMap<>();
    public CooldownManager(){
    }

    public static CooldownManager getInstance(){
        if (manager == null){
            manager = new CooldownManager();
        }
        return manager;
    }

    public void setCooldown(UUID entity, int timems, String cooldownKey){
        if (!allCooldowns.containsKey(cooldownKey)) allCooldowns.put(cooldownKey, new HashMap<>());
        allCooldowns.get(cooldownKey).put(entity, System.currentTimeMillis() + timems);
    }

    public long getCooldown(UUID entity, String cooldownKey){
        if (!allCooldowns.containsKey(cooldownKey)) allCooldowns.put(cooldownKey, new HashMap<>());
        if (allCooldowns.get(cooldownKey).containsKey(entity)){
            return allCooldowns.get(cooldownKey).get(entity) - System.currentTimeMillis();
        }
        return 0;
    }

    public boolean isCooldownPassed(UUID entity, String cooldownKey){
        if (!allCooldowns.containsKey(cooldownKey)) allCooldowns.put(cooldownKey, new HashMap<>());
        if (allCooldowns.get(cooldownKey).containsKey(entity)){
            return allCooldowns.get(cooldownKey).get(entity) <= System.currentTimeMillis();
        }
        return true;
    }

    public void startTimer(UUID entity, String timerKey){
        if (!allTimers.containsKey(timerKey)) allTimers.put(timerKey, new HashMap<>());
        allTimers.get(timerKey).put(entity, System.currentTimeMillis());
    }

    public long getTimerResult(UUID entity, String timerKey){
        if (!allTimers.containsKey(timerKey)) allTimers.put(timerKey, new HashMap<>());
        if (allTimers.get(timerKey).containsKey(entity)){
            return System.currentTimeMillis() - allTimers.get(timerKey).get(entity);
        }
        return 0;
    }

    public void stopTimer(UUID entity, String timerKey){
        if (!allTimers.containsKey(timerKey)) allTimers.put(timerKey, new HashMap<>());
        allTimers.get(timerKey).remove(entity);
    }
}
