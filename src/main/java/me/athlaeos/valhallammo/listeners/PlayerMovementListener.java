package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.CooldownManager;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import java.util.HashMap;
import java.util.Map;

public class PlayerMovementListener implements Listener {
    private final int update_delay;
    private static Map<String, AttributeDataHolder> attributesToUpdate = new HashMap<>();

    public PlayerMovementListener(){
        update_delay = ConfigManager.getInstance().getConfig("config.yml").get().getInt("movement_update_delay", 5000);

        registerAttributeToUpdate(new AttributeDataHolder("valhalla_movement_modifier", "MOVEMENT_SPEED_BONUS", Attribute.GENERIC_MOVEMENT_SPEED, AttributeModifier.Operation.ADD_SCALAR));
        registerAttributeToUpdate(new AttributeDataHolder("valhalla_health_modifier", "HEALTH_BONUS", Attribute.GENERIC_MAX_HEALTH, AttributeModifier.Operation.ADD_NUMBER));
        registerAttributeToUpdate(new AttributeDataHolder("valhalla_armor_modifier", "ARMOR_BONUS", Attribute.GENERIC_ARMOR, AttributeModifier.Operation.ADD_NUMBER));
        registerAttributeToUpdate(new AttributeDataHolder("valhalla_toughness_modifier", "TOUGHNESS_BONUS", Attribute.GENERIC_ARMOR_TOUGHNESS, AttributeModifier.Operation.ADD_NUMBER));
        registerAttributeToUpdate(new AttributeDataHolder("valhalla_luck_modifier", "LUCK_BONUS", Attribute.GENERIC_LUCK, AttributeModifier.Operation.ADD_NUMBER));
        registerAttributeToUpdate(new AttributeDataHolder("valhalla_attack_damage_modifier", "ATTACK_DAMAGE_BONUS", Attribute.GENERIC_ATTACK_DAMAGE, AttributeModifier.Operation.ADD_NUMBER));
        registerAttributeToUpdate(new AttributeDataHolder("valhalla_attack_speed_modifier", "ATTACK_SPEED_BONUS", Attribute.GENERIC_ATTACK_SPEED, AttributeModifier.Operation.ADD_NUMBER));
    }

    private void registerAttributeToUpdate(AttributeDataHolder holder){
        attributesToUpdate.put(holder.getName(), holder);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if (e.isCancelled()) return;
        if (e.getTo() == null) return;
        if (CooldownManager.getInstance().isCooldownPassed(e.getPlayer().getUniqueId(), "delay_combat_update")) {
            EntityDamagedListener.updateCombatStatus(e.getPlayer());
            CooldownManager.getInstance().setCooldown(e.getPlayer().getUniqueId(), 500, "delay_combat_update");
        }
        if (CooldownManager.getInstance().isCooldownPassed(e.getPlayer().getUniqueId(), "delay_movement_update")){

            for (AttributeDataHolder holder : attributesToUpdate.values()){
                double value = AccumulativeStatManager.getInstance().getStats(holder.getStatSource(), e.getPlayer(), true);
                AttributeInstance instance = e.getPlayer().getAttribute(holder.getType());
                if (instance != null){
                    for (AttributeModifier modifier : instance.getModifiers()){
                        if (modifier.getName().equals(holder.getName())){
                            instance.removeModifier(modifier);
                        }
                    }
                    if (value == 0) continue;
                    instance.addModifier(
                            new AttributeModifier(holder.getName(), value, holder.getOperation())
                    );
                }
            }
            CooldownManager.getInstance().setCooldown(e.getPlayer().getUniqueId(), update_delay, "delay_movement_update");
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e){
        if (!e.isCancelled()){
            if (e.isSneaking()){
                double sneakSpeedBonus = AccumulativeStatManager.getInstance().getStats("SNEAK_MOVEMENT_SPEED_BONUS", e.getPlayer(), true);

                AttributeInstance instance = e.getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                if (instance != null){
                    for (AttributeModifier modifier : instance.getModifiers()){
                        if (modifier.getName().equals("valhalla_sneak_movement_modifier")){
                            instance.removeModifier(modifier);
                            break;
                        }
                    }
                    if (sneakSpeedBonus == 0) return;
                    instance.addModifier(
                            new AttributeModifier("valhalla_sneak_movement_modifier", sneakSpeedBonus, AttributeModifier.Operation.ADD_SCALAR)
                    );
                }
            } else {
                AttributeInstance instance = e.getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
                if (instance != null){
                    for (AttributeModifier modifier : instance.getModifiers()){
                        if (modifier.getName().equals("valhalla_sneak_movement_modifier")){
                            instance.removeModifier(modifier);
                            break;
                        }
                    }
                }
            }
        }
    }

    public static Map<String, AttributeDataHolder> getAttributesToUpdate() {
        return attributesToUpdate;
    }

    public static class AttributeDataHolder{
        private final String name;
        private final String statSource;
        private final Attribute type;
        private final AttributeModifier.Operation operation;

        public AttributeDataHolder(String name, String statSource, Attribute type, AttributeModifier.Operation operation){
            this.name = name;
            this.statSource = statSource;
            this.type = type;
            this.operation = operation;
        }

        public String getName() {
            return name;
        }

        public String getStatSource() {
            return statSource;
        }

        public Attribute getType() {
            return type;
        }

        public AttributeModifier.Operation getOperation() {
            return operation;
        }
    }
}
