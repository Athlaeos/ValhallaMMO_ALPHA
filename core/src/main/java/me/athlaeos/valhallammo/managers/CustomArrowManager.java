package me.athlaeos.valhallammo.managers;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.dom.CustomArrowAttribute;
import me.athlaeos.valhallammo.items.customarrowattributes.*;
import org.bukkit.NamespacedKey;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.*;

public class CustomArrowManager {
    private static CustomArrowManager manager = null;

    private final Map<String, CustomArrowAttribute> registeredArrowAttributes;
    private final NamespacedKey attributeKey = new NamespacedKey(ValhallaMMO.getPlugin(), "custom_arrow_attributes");

    public CustomArrowManager(){
        registeredArrowAttributes = new HashMap<>();

        registerArrowAttribute(new ExplodingArrow("explosive_arrow"));
        registerArrowAttribute(new IncendiaryArrow("incendiary_arrow"));
        registerArrowAttribute(new EnderArrow("ender_arrow"));
        registerArrowAttribute(new RemoveImmunityFramesArrow("no_iframes_arrow"));
        registerArrowAttribute(new LightningArrow("lightning_arrow"));
        registerArrowAttribute(new SmallFireballArrow("small_fireball_arrow"));
        registerArrowAttribute(new LargeFireballArrow("large_fireball_arrow"));
        registerArrowAttribute(new DragonFireballArrow("dragon_fireball_arrow"));
        registerArrowAttribute(new NoGravityArrow("gravityless_arrow"));
    }

    public void registerArrowAttribute(CustomArrowAttribute attribute){
        registeredArrowAttributes.put(attribute.getIdentifier(), attribute);
    }

    public void executeOnShoot(ItemStack arrow, EntityShootBowEvent event){
        for (ArrowAttribute a : getAttributes(arrow).values()){
            CustomArrowAttribute attribute = registeredArrowAttributes.get(a.getName());
            if (attribute != null){
                attribute.onBowShoot(event, a.getArgs());
            }
        }
    }

    public void executeOnHit(ItemStack arrow, ProjectileHitEvent event){
        for (ArrowAttribute a : getAttributes(arrow).values()){
            CustomArrowAttribute attribute = registeredArrowAttributes.get(a.getName());
            if (attribute != null){
                attribute.onProjectileHit(event, a.getArgs());
            }
        }
    }

    public void executeOnLaunch(ItemStack arrow, ProjectileLaunchEvent event){
        for (ArrowAttribute a : getAttributes(arrow).values()){
            CustomArrowAttribute attribute = registeredArrowAttributes.get(a.getName());
            if (attribute != null){
                attribute.onProjectileLaunch(event, a.getArgs());
            }
        }
    }

    public void executeOnPickup(ItemStack arrow, PlayerPickupArrowEvent event){
        for (ArrowAttribute a : getAttributes(arrow).values()){
            CustomArrowAttribute attribute = registeredArrowAttributes.get(a.getName());
            if (attribute != null){
                attribute.onArrowPickup(event, a.getArgs());
            }
        }
    }

    public void executeOnDamage(ItemStack arrow, EntityDamageByEntityEvent event){
        for (ArrowAttribute a : getAttributes(arrow).values()){
            CustomArrowAttribute attribute = registeredArrowAttributes.get(a.getName());
            if (attribute != null){
                attribute.onProjectileDamage(event, a.getArgs());
            }
        }
    }

    public Map<String, CustomArrowAttribute> getRegisteredArrowAttributes() {
        return registeredArrowAttributes;
    }

    public void addArrowAttribute(ItemStack arrow, String attribute, double... args){
        if (arrow == null) return;
        if (!arrow.getType().toString().contains("ARROW")) return;
        if (!registeredArrowAttributes.containsKey(attribute)) {
            return;
        }
        Map<String, ArrowAttribute> attributes = getAttributes(arrow);
        attributes.put(attribute, new ArrowAttribute(attribute, args));
        setAttributes(arrow, attributes);
    }

    public void setAttributes(ItemStack arrow, Map<String, ArrowAttribute> attributes){
        if (arrow == null) return;
        if (!arrow.getType().toString().contains("ARROW")) return;
        ItemMeta meta = arrow.getItemMeta();
        if (meta == null) return;
        if (attributes.isEmpty()){
            meta.getPersistentDataContainer().remove(attributeKey);
        } else {
            Collection<String> attributeStrings = new HashSet<>();
            for (ArrowAttribute a : attributes.values()){
                StringBuilder s = new StringBuilder(a.getName());
                for (double i : a.getArgs()){
                    s.append(":").append(i);
                }
                attributeStrings.add(s.toString());
            }
            meta.getPersistentDataContainer().set(attributeKey, PersistentDataType.STRING, String.join(";", attributeStrings));
        }
        arrow.setItemMeta(meta);
    }

    public Map<String, ArrowAttribute> getAttributes(ItemStack arrow){
        Map<String, ArrowAttribute> attributes = new HashMap<>();
        if (arrow == null) {
            return attributes;
        }
        if (!arrow.getType().toString().contains("ARROW")) {
            return attributes;
        }
        ItemMeta meta = arrow.getItemMeta();
        if (meta == null) {
            return attributes;
        }
        if (meta.getPersistentDataContainer().has(attributeKey, PersistentDataType.STRING)){
            String atrs = meta.getPersistentDataContainer().get(attributeKey, PersistentDataType.STRING);
            assert atrs != null;
            for (String s : atrs.split(";")){
                String[] args = s.split(":");
                if (args.length > 0){
                    String name = args[0];
                    if (!registeredArrowAttributes.containsKey(name)) {
                        continue;
                    }
                    if (args.length > 1){
                        double[] intArgs = new double[args.length - 1];
                        for (int i = 0; i < args.length - 1; i++){
                            try {
                                intArgs[i] = Double.parseDouble(args[i + 1]);
                            } catch (IllegalArgumentException ignored){
                            }
                        }
                        attributes.put(name, new ArrowAttribute(name, intArgs));
                    } else {
                        attributes.put(s, new ArrowAttribute(s, new double[0]));
                    }
                } else {
                    attributes.put(s, new ArrowAttribute(s, new double[0]));
                }
            }
        }
        return attributes;
    }

    public static CustomArrowManager getInstance(){
        if (manager == null) manager = new CustomArrowManager();
        return manager;
    }

    private static class ArrowAttribute{
        private final String name;
        private final double[] args;

        public ArrowAttribute(String name, double[] args){
            this.name = name;
            this.args = args;
        }

        public String getName() {
            return name;
        }

        public double[] getArgs() {
            return args;
        }
    }
}
