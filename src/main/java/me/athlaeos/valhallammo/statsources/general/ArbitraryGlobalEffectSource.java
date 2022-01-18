package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.managers.GlobalEffectManager;
import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;

public class GlobalEffectExpGainSource extends AccumulativeStatSource {
    private final String effect;

    public GlobalEffectExpGainSource(String effect){
        this.effect = effect;
    }

    @Override
    public double add(Entity p, boolean use) {
        if (GlobalEffectManager.getInstance().isActive(effect)){
            return GlobalEffectManager.getInstance().getAmplifier(effect);
        }
        return 0;
    }
}
