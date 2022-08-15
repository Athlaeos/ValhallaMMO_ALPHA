package me.athlaeos.valhallammo.skills.archery;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.config.ConfigManager;
import me.athlaeos.valhallammo.utility.ShapeUtils;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.AbstractArrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.awt.*;
import java.util.*;

public class DefaultChargedShotAnimation implements IChargedShotAnimation{
    private Sound charged_shot_prefire_sound;
    private final float charged_shot_prefire_sound_volume;
    private final float charged_shot_prefire_sound_pitch;
    private Sound charged_shot_fire_sound;
    private final float charged_shot_fire_sound_volume;
    private final float charged_shot_fire_sound_pitch;
    private Particle charged_shot_ammo_particle;
    private Particle.DustOptions charged_shot_ammo_rgb = null;
    private Particle charged_shot_sonic_boom_particle;
    private Particle.DustOptions charged_shot_sonic_boom_rgb = null;
    private Particle charged_shot_trail_particle;
    private Particle.DustOptions charged_shot_trail_rgb = null;
    private final double charged_shot_sonic_boom_required_velocity;

    public DefaultChargedShotAnimation(){
        YamlConfiguration archeryConfig = ConfigManager.getInstance().getConfig("skill_archery.yml").get();
        charged_shot_prefire_sound_volume = (float) archeryConfig.getDouble("charged_shot_prefire_sound_volume");
        charged_shot_prefire_sound_pitch = (float) archeryConfig.getDouble("charged_shot_prefire_sound_pitch");
        try {
            charged_shot_prefire_sound = Sound.valueOf(archeryConfig.getString("charged_shot_prefire_sound"));
        } catch (IllegalArgumentException ignored){
            ValhallaMMO.getPlugin().getServer().getLogger().warning("Invalid sound name used for charged_shot_prefire_sound in skill_archery.yml. Disabled this effect");
            charged_shot_prefire_sound = null;
        }
        charged_shot_fire_sound_volume = (float) archeryConfig.getDouble("charged_shot_fire_sound_volume");
        charged_shot_fire_sound_pitch = (float) archeryConfig.getDouble("charged_shot_fire_sound_pitch");
        try {
            charged_shot_fire_sound = Sound.valueOf(archeryConfig.getString("charged_shot_fire_sound"));
        } catch (IllegalArgumentException ignored){
            ValhallaMMO.getPlugin().getServer().getLogger().warning("Invalid sound name used for charged_shot_fire_sound in skill_archery.yml. Disabled this effect");
            charged_shot_fire_sound = null;
        }
        try {
            charged_shot_ammo_particle = Particle.valueOf(archeryConfig.getString("charged_shot_ammo_particle"));
            if (charged_shot_ammo_particle == Particle.REDSTONE){
                Color c = Utils.hexToRgb(archeryConfig.getString("charged_shot_ammo_rgb", "#ffffff"));
                charged_shot_ammo_rgb = new Particle.DustOptions(org.bukkit.Color.fromRGB(
                        c.getRed(),
                        c.getGreen(),
                        c.getBlue()
                ), 0.5f);
            } else {
                charged_shot_ammo_rgb = null;
            }
        } catch (IllegalArgumentException ignored){
            ValhallaMMO.getPlugin().getServer().getLogger().warning("Invalid sound name used for charged_shot_ammo_particle in skill_archery.yml. Disabled this effect");
            charged_shot_ammo_particle = null;
        }
        try {
            charged_shot_sonic_boom_particle = Particle.valueOf(archeryConfig.getString("charged_shot_sonic_boom_particle"));
            if (charged_shot_sonic_boom_particle == Particle.REDSTONE){
                Color c = Utils.hexToRgb(archeryConfig.getString("charged_shot_sonic_boom_rgb", "#ffffff"));
                charged_shot_sonic_boom_rgb = new Particle.DustOptions(org.bukkit.Color.fromRGB(
                        c.getRed(),
                        c.getGreen(),
                        c.getBlue()
                ), 0.5f);
            } else {
                charged_shot_sonic_boom_rgb = null;
            }
        } catch (IllegalArgumentException ignored){
            ValhallaMMO.getPlugin().getServer().getLogger().warning("Invalid sound name used for charged_shot_sonic_boom_particle in skill_archery.yml. Disabled this effect");
            charged_shot_sonic_boom_particle = null;
        }
        try {
            charged_shot_trail_particle = Particle.valueOf(archeryConfig.getString("charged_shot_trail_particle"));
            if (charged_shot_trail_particle == Particle.REDSTONE){
                Color c = Utils.hexToRgb(archeryConfig.getString("charged_shot_trail_rgb", "#ffffff"));
                charged_shot_trail_rgb = new Particle.DustOptions(org.bukkit.Color.fromRGB(
                        c.getRed(),
                        c.getGreen(),
                        c.getBlue()
                ), 0.5f);
            } else {
                charged_shot_trail_rgb = null;
            }
        } catch (IllegalArgumentException ignored){
            ValhallaMMO.getPlugin().getServer().getLogger().warning("Invalid sound name used for charged_shot_trail_particle in skill_archery.yml. Disabled this effect");
            charged_shot_trail_particle = null;
        }
        charged_shot_sonic_boom_required_velocity = archeryConfig.getDouble("charged_shot_sonic_boom_required_velocity");

        startChargedShotAnimationRunnable();
    }

    @Override
    public void onShoot(EntityShootBowEvent e) {
        Player shooter = (Player) e.getEntity();
        AbstractArrow arrow = (AbstractArrow) e.getProjectile();
        updateChargedShotAnimation(shooter);
        if (charged_shot_fire_sound != null){
            shooter.getWorld().playSound(shooter.getLocation(), charged_shot_fire_sound, charged_shot_fire_sound_volume, charged_shot_fire_sound_pitch);
        }
        if (charged_shot_trail_particle != null){
            if (charged_shot_trail_particle == Particle.REDSTONE && charged_shot_trail_rgb != null){
                ArcherySkill.trailRedstoneProjectile(arrow, charged_shot_trail_rgb);
            } else {
                ArcherySkill.trailProjectile(arrow, charged_shot_trail_particle);
            }
        }
        if (charged_shot_sonic_boom_particle != null){
            if (arrow.getVelocity().length() >= charged_shot_sonic_boom_required_velocity){
                // sonic boom animation
                Location tC = new Location(shooter.getWorld(), 0, 0, 0);
                // true center (shooter eye location)
                Location c1C = new Location(shooter.getWorld(), 0, 10, 0);
                // circle 1 center
                Location c2C = new Location(shooter.getWorld(), 0, 15, 0);
                // circle 2 center
                Location c3C = new Location(shooter.getWorld(), 0, 20, 0);
                // circle 3 center

                java.util.List<Location> circle1 = new ArrayList<>(ShapeUtils.getRandomPointsInCircle(c1C, 0.5, 30));
                circle1.add(c1C);
                java.util.List<Location> circle2 = new ArrayList<>(ShapeUtils.getRandomPointsInCircle(c2C, 0.5, 30));
                circle2.add(c2C);
                java.util.List<Location> circle3 = new ArrayList<>(ShapeUtils.getRandomPointsInCircle(c3C, 0.5, 30));
                circle3.add(c3C);

                // The circles are pointing up by defaut, which is pitch -90, so 90 should be added to make the circles point horizontally
                // Applying the player's yaw ends up turning the circles in the opposite direction, so 180 is added to invert them
                ShapeUtils.transformExistingPoints(tC, 0, shooter.getEyeLocation().getPitch() + 90, 0, 1, circle1, circle2, circle3);
                ShapeUtils.transformExistingPoints(tC, shooter.getEyeLocation().getYaw() + 180, 0, 0, 1, circle1, circle2, circle3);

                ValhallaMMO.getPlugin().getServer().getScheduler().runTaskLater(ValhallaMMO.getPlugin(), () -> {
                    for (Location l : circle1){
                        if (l.equals(c1C)) continue;
                        if (charged_shot_sonic_boom_particle == Particle.REDSTONE){
                            arrow.getWorld().spawnParticle(charged_shot_sonic_boom_particle,
                                    l.clone().add(shooter.getEyeLocation().getX(), shooter.getEyeLocation().getY(), shooter.getEyeLocation().getZ()),
                                    0, charged_shot_sonic_boom_rgb);
                        } else {
                            arrow.getWorld().spawnParticle(charged_shot_sonic_boom_particle,
                                    l.clone().add(shooter.getEyeLocation().getX(), shooter.getEyeLocation().getY(), shooter.getEyeLocation().getZ()),
                                    0, (l.getX() - c1C.getX()) * 0.25, (l.getY() - c1C.getY()) * 0.25, (l.getZ() - c1C.getZ()) * 0.25);
                        }
                    }
                }, 2L);
                ValhallaMMO.getPlugin().getServer().getScheduler().runTaskLater(ValhallaMMO.getPlugin(), () -> {
                    for (Location l : circle2){
                        if (l.equals(c2C)) continue;
                        if (charged_shot_sonic_boom_particle == Particle.REDSTONE){
                            arrow.getWorld().spawnParticle(charged_shot_sonic_boom_particle,
                                    l.clone().add(shooter.getEyeLocation().getX(), shooter.getEyeLocation().getY(), shooter.getEyeLocation().getZ()),
                                    0, charged_shot_sonic_boom_rgb);
                        } else {
                            arrow.getWorld().spawnParticle(charged_shot_sonic_boom_particle,
                                    l.clone().add(shooter.getEyeLocation().getX(), shooter.getEyeLocation().getY(), shooter.getEyeLocation().getZ()),
                                    0, (l.getX() - c2C.getX()) * 0.15, (l.getY() - c2C.getY()) * 0.15, (l.getZ() - c2C.getZ()) * 0.15);
                        }
                    }
                }, 4L);
                ValhallaMMO.getPlugin().getServer().getScheduler().runTaskLater(ValhallaMMO.getPlugin(), () -> {
                    for (Location l : circle3){
                        if (l.equals(c3C)) continue;
                        if (charged_shot_sonic_boom_particle == Particle.REDSTONE){
                            arrow.getWorld().spawnParticle(charged_shot_sonic_boom_particle,
                                    l.clone().add(shooter.getEyeLocation().getX(), shooter.getEyeLocation().getY(), shooter.getEyeLocation().getZ()),
                                    0, charged_shot_sonic_boom_rgb);
                        } else {
                            arrow.getWorld().spawnParticle(charged_shot_sonic_boom_particle,
                                    l.clone().add(shooter.getEyeLocation().getX(), shooter.getEyeLocation().getY(), shooter.getEyeLocation().getZ()),
                                    0, (l.getX() - c3C.getX()) * 0.1, (l.getY() - c3C.getY()) * 0.1, (l.getZ() - c3C.getZ()) * 0.1);
                        }
                    }
                }, 6L);
            }
        }
    }

    @Override
    public void onActivate(Player p) {
        p.playSound(p.getLocation(), charged_shot_prefire_sound, charged_shot_prefire_sound_volume, charged_shot_prefire_sound_pitch);
        updateChargedShotAnimation(p);
    }

    @Override
    public void onExpire(Player p) {

    }
    private final Map<Player, Collection<Location>> revolvingParticles = new HashMap<>();

    private void updateChargedShotAnimation(Player p){
        if (charged_shot_ammo_particle == null) return;
        ArcherySkill.ChargedShotData data = ArcherySkill.getChargedShotUsers().get(p.getUniqueId());
        if (data == null || data.getCharges() <= 0) {
            revolvingParticles.remove(p);
            return;
        }
        Location normalizedCenter = new Location(p.getWorld(), 0, 0, 0);
        Collection<Location> circle = ShapeUtils.getEvenCircle(normalizedCenter, 0.75, data.getCharges(), 0);
        revolvingParticles.put(p, circle);
    }

    private void startChargedShotAnimationRunnable(){
        if (charged_shot_ammo_particle == null) return;
        double cosPitch = Math.cos(Math.toRadians(0));
        double sinPitch = Math.sin(Math.toRadians(0));
        double cosYaw = Math.cos(Math.toRadians(2));
        double sinYaw = Math.sin(Math.toRadians(2));
        double cosRoll = Math.cos(Math.toRadians(0));
        double sinRoll = Math.sin(Math.toRadians(0));
        // math calculations are done at the very start so they aren't as intensive to run
        // the values stored here determine how much the ammo particles around players should float

        new BukkitRunnable(){
            @Override
            public void run() {
                for (Player p : new HashSet<>(revolvingParticles.keySet())){
                    if (!p.isValid() || !p.isOnline()) {
                        revolvingParticles.remove(p);
                        continue;
                    }
                    Location normalizedCenter = new Location(p.getWorld(), 0, 0, 0);
                    Collection<Location> circle = revolvingParticles.get(p);
                    Location pL = p.getLocation();
                    for (Location l : circle){
                        if (charged_shot_ammo_rgb != null){
                            p.getWorld().spawnParticle(charged_shot_ammo_particle, l.clone().add(pL.getX(), pL.getY() + 0.8, pL.getZ()), 0, charged_shot_ammo_rgb);
                        } else {
                            p.getWorld().spawnParticle(charged_shot_ammo_particle, l.clone().add(pL.getX(), pL.getY() + 0.8, pL.getZ()), 0);
                        }
                    }
                    Collection<Location> newCircle = ShapeUtils.transformPointsPredefined(normalizedCenter, circle,
                            cosPitch, sinPitch, cosYaw, sinYaw, cosRoll, sinRoll, 1);
                    revolvingParticles.put(p, newCircle);
                }
            }
        }.runTaskTimer(ValhallaMMO.getPlugin(), 0L, 1L);
    }
}
