package me.athlaeos.valhallammo.statsources.general;

import me.athlaeos.valhallammo.statsources.AccumulativeStatSource;
import org.bukkit.entity.Entity;
import org.bukkit.permissions.PermissionAttachmentInfo;

public class PermissionExpGainSource extends AccumulativeStatSource {
    private final String skillType;

    public PermissionExpGainSource(String skill){
        this.skillType = skill;
    }

    @Override
    public double add(Entity p, boolean use) {
        String skill = (skillType == null) ? "general" : skillType.toLowerCase();
        for (PermissionAttachmentInfo permission : p.getEffectivePermissions()){
            if (permission.getPermission().contains("valhalla.experience." + skill)){
                String[] split = permission.getPermission().split("\\.");
                if (split.length < 4) return 0;
                try {
                    return Integer.parseInt(split[3]);
                } catch (IllegalArgumentException ignored){
                    return 0;
                }
            }
        }
        return 0;
    }
}
