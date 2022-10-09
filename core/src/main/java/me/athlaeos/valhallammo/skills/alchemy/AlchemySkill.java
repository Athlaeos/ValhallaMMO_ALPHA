package me.athlaeos.valhallammo.skills.alchemy;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.dom.Profile;
import me.athlaeos.valhallammo.events.PlayerSkillExperienceGainEvent;
import me.athlaeos.valhallammo.managers.AccumulativeStatManager;
import me.athlaeos.valhallammo.skills.InteractSkill;
import me.athlaeos.valhallammo.skills.ProjectileSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerPickupArrowEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;

import java.util.List;

public class AlchemySkill extends Skill implements ProjectileSkill, InteractSkill {

    private final boolean quick_empty_potions;
    public AlchemySkill(String type) {
        super(type);
        skillTreeMenuOrderPriority = 3;
        YamlConfiguration alchemyConfig = ConfigManager.getInstance().getConfig("skill_alchemy.yml").get();
        YamlConfiguration progressionConfig = ConfigManager.getInstance().getConfig("progression_alchemy.yml").get();
        quick_empty_potions = alchemyConfig.getBoolean("quick_empty_potions");

        this.loadCommonConfig(alchemyConfig, progressionConfig);
    }

    @Override
    public NamespacedKey getKey() {
        return new NamespacedKey(ValhallaMMO.getPlugin(), "valhalla_profile_alchemy");
    }

    @Override
    public Profile getCleanProfile() {
        return new AlchemyProfile(null);
    }

    @Override
    public void addEXP(Player p, double amount, boolean silent, PlayerSkillExperienceGainEvent.ExperienceGainReason reason) {
        double finalAmount = amount * ((AccumulativeStatManager.getInstance().getStats("ALCHEMY_EXP_GAIN", p, true) / 100D));
        super.addEXP(p, finalAmount, silent, reason);
    }

    @Override
    public void onProjectileLaunch(ProjectileLaunchEvent e) {
        if (e.getEntity().getShooter() instanceof LivingEntity){
            LivingEntity shooter = (LivingEntity) e.getEntity().getShooter();

            if (!(e.getEntity() instanceof AbstractArrow)) {
                double multiplier = AccumulativeStatManager.getInstance().getStats("ALCHEMY_POTION_VELOCITY", shooter, true);

                e.getEntity().setVelocity(e.getEntity().getVelocity().multiply(multiplier));
            }

            if (e.getEntity() instanceof ThrownPotion){
                ThrownPotion potion = (ThrownPotion) e.getEntity();
                if (shooter instanceof Player){
                    ItemStack itemPotion = potion.getItem();

                    // potion saving mechanic
                    if (e.getEntity().getShooter() instanceof Player){
                        Player thrower = (Player) e.getEntity().getShooter();
                        if (thrower.getGameMode() != GameMode.CREATIVE){
                            double chance = AccumulativeStatManager.getInstance().getStats("ALCHEMY_POTION_SAVE", thrower, true);
                            if (Utils.getRandom().nextDouble() < chance){
                                thrower.getInventory().addItem(itemPotion);
                            }
                        }
                    }

                    if (itemPotion.getType() == Material.LINGERING_POTION){
                        // correcting lingering potion duration
                        if (itemPotion.getItemMeta() instanceof PotionMeta){
                            PotionMeta meta = (PotionMeta) itemPotion.getItemMeta();
                            List<PotionEffect> effects = meta.getCustomEffects();

                            for (PotionEffect eff : effects){
                                meta.addCustomEffect(new PotionEffect(eff.getType(), (int) Math.floor(eff.getDuration() / 4D), eff.getAmplifier()), true);
                            }

                            itemPotion.setItemMeta(meta);
                            potion.setItem(itemPotion);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onEntityShootBow(EntityShootBowEvent e) {
        if (e.getProjectile() instanceof Arrow){
            ItemStack arrow = e.getConsumable();
            if (arrow == null) return;
            if (arrow.getItemMeta() instanceof PotionMeta){
                PotionMeta meta = (PotionMeta) arrow.getItemMeta();
                List<PotionEffect> effects = meta.getCustomEffects();

                for (PotionEffect eff : effects){
                    ((Arrow) e.getProjectile()).addCustomEffect(new PotionEffect(eff.getType(), (int) Math.floor(eff.getDuration() / 8D), eff.getAmplifier()), true);
                }
            }
        }
    }

    @Override
    public void onProjectileHit(ProjectileHitEvent event) {

    }

    @Override
    public void onArrowPickup(PlayerPickupArrowEvent event) {

    }

    @Override
    public void onEntityInteract(PlayerInteractEntityEvent event) {

    }

    @Override
    public void onAtEntityInteract(PlayerInteractAtEntityEvent event) {

    }

    @Override
    public void onInteract(PlayerInteractEvent event) {
        if (!quick_empty_potions) return;
        if (event.getClickedBlock() != null && (event.getClickedBlock().getType() == Material.CAULDRON || event.getClickedBlock().getType().toString().equals("WATER_CAULDRON"))){
            ItemStack inHandItem = event.getPlayer().getInventory().getItemInMainHand();
            if (Utils.isItemEmptyOrNull(inHandItem) || inHandItem.getType() != Material.POTION) return;
            inHandItem.setType(Material.GLASS_BOTTLE);
            event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.GLASS_BOTTLE, inHandItem.getAmount()));
            event.getClickedBlock().getWorld().playSound(event.getClickedBlock().getLocation().add(0.5, 0.5, 0.5), Sound.BLOCK_BREWING_STAND_BREW, 1F, 1F);
        }
    }
}
