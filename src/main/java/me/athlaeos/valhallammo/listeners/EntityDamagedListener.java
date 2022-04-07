package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.items.attributewrappers.AttributeWrapper;
import me.athlaeos.valhallammo.loottables.ChancedEntityLootTable;
import me.athlaeos.valhallammo.loottables.LootManager;
import me.athlaeos.valhallammo.loottables.chance_based_entity_loot.GlobalChancedEntityLootTable;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.managers.ItemAttributesManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.skills.OffensiveSkill;
import me.athlaeos.valhallammo.skills.Skill;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class EntityDamagedListener implements Listener {
    private double tridentThrown;
    private double tridentThrownLoyal;
    private GlobalChancedEntityLootTable lootTable = null;
    private static EntityDamagedListener listener;

    public EntityDamagedListener(){
        listener = this;
        tridentThrown = ConfigManager.getInstance().getConfig("config.yml").get().getDouble("trident_damage_ranged");
        tridentThrownLoyal = ConfigManager.getInstance().getConfig("config.yml").get().getDouble("trident_damage_ranged_loyalty");
        ChancedEntityLootTable table = LootManager.getInstance().getChancedEntityLootTables().get("global_entities");
        if (table instanceof GlobalChancedEntityLootTable){
            lootTable = (GlobalChancedEntityLootTable) table;
        }
    }

    public static EntityDamagedListener getListener() {
        return listener;
    }

    public void reload(){
        tridentThrown = ConfigManager.getInstance().getConfig("config.yml").get().getDouble("trident_damage_ranged");
        tridentThrownLoyal = ConfigManager.getInstance().getConfig("config.yml").get().getDouble("trident_damage_ranged_loyalty");
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamaged(EntityDamageEvent e){
        if (e.getEntity() instanceof Player){
            switch (e.getCause()){
                case FIRE: case LAVA: case MELTING: case FIRE_TICK: case HOT_FLOOR:
                {
                    e.setDamage(calcDamageWithResistance(e.getDamage(), AccumulativeStatManager.getInstance().getStats("ATTRIBUTE_FIRE_RESISTANCE", e.getEntity(), true)));
                    break;
                }
                case MAGIC: case THORNS: case LIGHTNING: case DRAGON_BREATH:
                {
                    e.setDamage(calcDamageWithResistance(e.getDamage(), AccumulativeStatManager.getInstance().getStats("ATTRIBUTE_MAGIC_RESISTANCE", e.getEntity(), true)));
                    break;
                }
                case PROJECTILE:
                {
                    e.setDamage(calcDamageWithResistance(e.getDamage(), AccumulativeStatManager.getInstance().getStats("ATTRIBUTE_PROJECTILE_RESISTANCE", e.getEntity(), true)));
                    break;
                }
                case ENTITY_EXPLOSION: case BLOCK_EXPLOSION:
                {
                    e.setDamage(calcDamageWithResistance(e.getDamage(), AccumulativeStatManager.getInstance().getStats("ATTRIBUTE_EXPLOSION_RESISTANCE", e.getEntity(), true)));
                    break;
                }
                case POISON: case WITHER:
                {
                    e.setDamage(calcDamageWithResistance(e.getDamage(), AccumulativeStatManager.getInstance().getStats("ATTRIBUTE_POISON_RESISTANCE", e.getEntity(), true)));
                    break;
                }
            }
            if (e.getCause() != EntityDamageEvent.DamageCause.VOID){
                e.setDamage(calcDamageWithResistance(e.getDamage(), AccumulativeStatManager.getInstance().getStats("ATTRIBUTE_DAMAGE_RESISTANCE", e.getEntity(), true)));
            }
            if (e.getDamage() <= 0) e.setCancelled(true);
        }
    }

    private double calcDamageWithResistance(double damage, double resistance){
        return Math.max(0, damage * (1 - resistance));
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamagedByEntity(EntityDamageByEntityEvent e){
        if (!e.isCancelled()) {
            Entity damager = e.getDamager();
            if (damager instanceof Projectile){
                if (((Projectile) damager).getShooter() instanceof Entity){
                    damager = (Entity) ((Projectile) damager).getShooter();
                }
            }
            if (e.getDamager() instanceof Trident) {
                Trident t = (Trident) e.getDamager();
                AttributeWrapper wrapper = ItemAttributesManager.getInstance().getAttributeWrapper(t.getItem(), "GENERIC_ATTACK_DAMAGE");
                if (wrapper != null) {
                    if (t.getItem().getEnchantmentLevel(Enchantment.LOYALTY) == 0) {
                        e.setDamage(e.getDamage() * tridentThrown);
                    } else {
                        e.setDamage(e.getDamage() * tridentThrownLoyal);
                    }
                }
            }

            double multiplier = 1F;
            double mitigated = 0F;
            if (damager instanceof Player && e.getEntity() instanceof Animals){
                multiplier += AccumulativeStatManager.getInstance().getStats("FARMING_DAMAGE_ANIMAL_MULTIPLIER", damager, true) - 1;
            }
            if (e.getDamager() instanceof AbstractArrow && !(e.getDamager() instanceof Trident)){
                multiplier += AccumulativeStatManager.getInstance().getStats("ARCHERY_DAMAGE", damager, true);
            }
            mitigated -= AccumulativeStatManager.getInstance().getStats("DAMAGE_TAKEN", e.getEntity(), true);
            multiplier += AccumulativeStatManager.getInstance().getStats("DAMAGE_DEALT", damager, true);

            e.setDamage(Math.min((1F - mitigated), 1F) * (e.getDamage() * Math.max(0, multiplier)));

            for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (skill != null){
                    if (skill instanceof OffensiveSkill){
                        ((OffensiveSkill) skill).onEntityDamage(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDeath(EntityDeathEvent e){
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;

        for (Skill skill : SkillProgressionManager.getInstance().getAllSkills().values()){
            if (skill != null){
                if (skill instanceof OffensiveSkill){
                    ((OffensiveSkill) skill).onEntityKilled(e);
                }
            }
        }
        if (lootTable != null){
            List<ItemStack> newItems = new ArrayList<>(e.getDrops());
            if (!e.getDrops().isEmpty()){
                lootTable.onEntityKilled(e.getEntity(), newItems, 1);
            }
            e.getDrops().clear();
            e.getDrops().addAll(newItems);
        }
    }
}
