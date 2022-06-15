package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.CooldownManager;
import me.athlaeos.valhallammo.utility.EntityUtils;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.util.Vector;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerMovementListener implements Listener {
    private final int update_delay;
    private static final Map<String, AttributeDataHolder> attributesToUpdate = new HashMap<>();
    private static final Map<UUID, Vector> lastMovementVectors = new HashMap<>();

    public PlayerMovementListener(){
        update_delay = ConfigManager.getInstance().getConfig("config.yml").get().getInt("movement_update_delay", 5000);

        registerAttributeToUpdate(new AttributeDataHolder("valhalla_movement_modifier", "MOVEMENT_SPEED_BONUS", Attribute.GENERIC_MOVEMENT_SPEED, AttributeModifier.Operation.ADD_SCALAR));
        registerAttributeToUpdate(new AttributeDataHolder("valhalla_health_modifier", "HEALTH_BONUS", Attribute.GENERIC_MAX_HEALTH, AttributeModifier.Operation.ADD_NUMBER));
        registerAttributeToUpdate(new AttributeDataHolder("valhalla_armor_modifier", "ARMOR_BONUS", Attribute.GENERIC_ARMOR, AttributeModifier.Operation.ADD_NUMBER));
        registerAttributeToUpdate(new AttributeDataHolder("valhalla_toughness_modifier", "TOUGHNESS_BONUS", Attribute.GENERIC_ARMOR_TOUGHNESS, AttributeModifier.Operation.ADD_NUMBER));
        registerAttributeToUpdate(new AttributeDataHolder("valhalla_luck_modifier", "LUCK_BONUS", Attribute.GENERIC_LUCK, AttributeModifier.Operation.ADD_NUMBER));
        registerAttributeToUpdate(new AttributeDataHolder("valhalla_attack_damage_modifier", "ATTACK_DAMAGE_BONUS", Attribute.GENERIC_ATTACK_DAMAGE, AttributeModifier.Operation.ADD_NUMBER));
        registerAttributeToUpdate(new AttributeDataHolder("valhalla_attack_speed_modifier", "ATTACK_SPEED_BONUS", Attribute.GENERIC_ATTACK_SPEED, AttributeModifier.Operation.ADD_SCALAR));
    }

    /**
     * When a player moves, the distance (squared) is recorded in this map. This can then be used elsewhere to figure out
     * exactly how fast a player is moving at a given time, since Player#getVelocity() doens't seem to work properly
     * when a player is just walking (standing still returns the same velocity as walking/running)
     * @return the last recorded distance of a player moving, squared
     */
    public static Map<UUID, Vector> getLastMovementVectors() {
        return lastMovementVectors;
    }

    private void registerAttributeToUpdate(AttributeDataHolder holder){
        attributesToUpdate.put(holder.getName(), holder);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent e){
        if (e.isCancelled()) return;
        if (e.getTo() == null) {
            lastMovementVectors.remove(e.getPlayer().getUniqueId());
            return;
        }
        lastMovementVectors.put(e.getPlayer().getUniqueId(), e.getTo().toVector().subtract(e.getFrom().toVector()));
        if (CooldownManager.getInstance().isCooldownPassed(e.getPlayer().getUniqueId(), "delay_combat_update")) {
            EntityDamagedListener.updateCombatStatus(e.getPlayer());
            CooldownManager.getInstance().setCooldown(e.getPlayer().getUniqueId(), 500, "delay_combat_update");
        }
        if (CooldownManager.getInstance().isCooldownPassed(e.getPlayer().getUniqueId(), "delay_movement_update")){
            ItemDamageListener.getExcludeEquipmentDamage().remove(e.getPlayer().getUniqueId());
            // This is to make sure that if a player dies their equipment doesn't stay unbreakable
            // (Dying instead deals a ludicrous amount of damage to you, which breaks armor sometimes.
            // Before dying your equipment is made unbreakable, so this will make it breakable again.)

            for (AttributeDataHolder holder : attributesToUpdate.values()){
                double value = AccumulativeStatManager.getInstance().getStats(holder.getStatSource(), e.getPlayer(), true);
                EntityUtils.addUniqueAttribute(e.getPlayer(), holder.getName(), holder.getType(), value, holder.getOperation());
//                AttributeInstance instance = e.getPlayer().getAttribute(holder.getType());
//                if (instance != null){
//                    for (AttributeModifier modifier : instance.getModifiers()){
//                        if (modifier.getName().equals(holder.getName())){
//                            instance.removeModifier(modifier);
//                        }
//                    }
//                    if (value == 0) continue;
//                    instance.addModifier(
//                            new AttributeModifier(holder.getName(), value, holder.getOperation())
//                    );
//                }
            }
            CooldownManager.getInstance().setCooldown(e.getPlayer().getUniqueId(), update_delay, "delay_movement_update");
        }
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent e){
        if (!e.isCancelled()){
            if (e.isSneaking()){
                double sneakSpeedBonus = AccumulativeStatManager.getInstance().getStats("SNEAK_MOVEMENT_SPEED_BONUS", e.getPlayer(), true);

                EntityUtils.addUniqueAttribute(e.getPlayer(), "valhalla_sneak_movement_modifier", Attribute.GENERIC_MOVEMENT_SPEED, sneakSpeedBonus, AttributeModifier.Operation.ADD_SCALAR);
//                AttributeInstance instance = e.getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
//                if (instance != null){
//                    for (AttributeModifier modifier : instance.getModifiers()){
//                        if (modifier.getName().equals("valhalla_sneak_movement_modifier")){
//                            instance.removeModifier(modifier);
//                            break;
//                        }
//                    }
//                    if (sneakSpeedBonus == 0) return;
//                    instance.addModifier(
//                            new AttributeModifier("valhalla_sneak_movement_modifier", sneakSpeedBonus, AttributeModifier.Operation.ADD_SCALAR)
//                    );
//                }
            } else {
                EntityUtils.removeUniqueAttribute(e.getPlayer(), "valhalla_sneak_movement_modifier", Attribute.GENERIC_MOVEMENT_SPEED);
//                AttributeInstance instance = e.getPlayer().getAttribute(Attribute.GENERIC_MOVEMENT_SPEED);
//                if (instance != null){
//                    for (AttributeModifier modifier : instance.getModifiers()){
//                        if (modifier.getName().equals("valhalla_sneak_movement_modifier")){
//                            instance.removeModifier(modifier);
//                            break;
//                        }
//                    }
//                }
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
