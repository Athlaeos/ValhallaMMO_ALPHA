package me.athlaeos.mmoskills;

import me.athlaeos.mmoskills.skills.smithing.listeners.EntityDamageEntityListener;
import me.athlaeos.mmoskills.skills.smithing.listeners.ItemDamageListener;
import me.athlaeos.mmoskills.skills.smithing.listeners.InteractListener;
import me.athlaeos.mmoskills.listeners.JoinListener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class MMOSkills extends JavaPlugin {
    private static MMOSkills plugin;
    private final InteractListener interactListener = new InteractListener();
    private final JoinListener joinListener = new JoinListener();
    private final ItemDamageListener itemDamageListener = new ItemDamageListener();
    private final EntityDamageEntityListener entityDamageEntityListener = new EntityDamageEntityListener();

    @Override
    public void onEnable() {
        plugin = this;

        if (!(new File(this.getDataFolder(), "config.yml").exists())){
            this.saveResource("config.yml", false);
        }
        if (!(new File(this.getDataFolder(), "textconfiguration.yml").exists())){
            this.saveResource("textconfiguration.yml", false);
        }

        // Plugin startup logic
        this.getServer().getPluginManager().registerEvents(interactListener, this);
        this.getServer().getPluginManager().registerEvents(joinListener, this);
        this.getServer().getPluginManager().registerEvents(itemDamageListener, this);
        this.getServer().getPluginManager().registerEvents(entityDamageEntityListener, this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public InteractListener getInteractListener() {
        return interactListener;
    }

    public ItemDamageListener getItemDamageListener() {
        return itemDamageListener;
    }

    public JoinListener getJoinListener() {
        return joinListener;
    }

    public static MMOSkills getPlugin() {
        return plugin;
    }
}
