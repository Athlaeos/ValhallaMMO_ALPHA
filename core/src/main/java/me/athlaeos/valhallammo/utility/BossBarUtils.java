package me.athlaeos.valhallammo.utility;

import me.athlaeos.valhallammo.ValhallaMMO;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class BossBarUtils {
    private final static Map<Player, Map<String, TemporaryBossBar>> activeBossBars = new HashMap<>();

    public static Map<Player, Map<String, TemporaryBossBar>> getActiveBossBars() {
        return activeBossBars;
    }

    /**
     * Shows a boss bar to the player for a given amount of time
     * @param player the player to show the boss bar to
     * @param progress the progress of the boss bar
     * @param time the time (in TENTH SECONDS) to show the boss bar
     */
    public static void showBossBarToPlayer(final Player player, String title, double progress, final int time, String skillType, BarColor color, BarStyle style){
        TemporaryBossBar bossBar = null;
        if (progress < 0) progress = 0D;
        if (progress > 1) progress = 1D;
        if (activeBossBars.containsKey(player)){
            if (activeBossBars.get(player).containsKey(skillType)){
                bossBar = activeBossBars.get(player).get(skillType);
            }
        }
        if (bossBar == null){
            bossBar = new TemporaryBossBar(time, progress, title, player, skillType, color, style);
            Map<String, TemporaryBossBar> existingBossBars = activeBossBars.get(player);
            if (existingBossBars == null) existingBossBars = new HashMap<>();
            existingBossBars.put(skillType, bossBar);
            bossBar.runTaskTimer(ValhallaMMO.getPlugin(), 0L, 2L);
            activeBossBars.put(player, existingBossBars);
        }

        bossBar.setTimer(time);
        bossBar.setFraction(progress);
        bossBar.setText(title);
    }

    private static class TemporaryBossBar extends BukkitRunnable{
        private int timer;
        private double fraction;
        private String text;
        private final BossBar bossBar;
        private final Player p;
        private final String skillType;

        public TemporaryBossBar(int timer, double fraction, String text, Player p, String skillType, BarColor color, BarStyle style){
            this.timer = timer;
            this.fraction = fraction;
            this.text = text;
            this.p = p;
            this.skillType = skillType;
            this.bossBar = ValhallaMMO.getPlugin().getServer().createBossBar(text, color, style);
        }

        @Override
        public void run() {
            if (timer <= 0){
                bossBar.removeAll();
                Map<String, TemporaryBossBar> existingBossBars = activeBossBars.get(p);
                if (existingBossBars == null) existingBossBars = new HashMap<>();
                existingBossBars.remove(skillType);
                activeBossBars.put(p, existingBossBars);
                cancel();
            } else {
                bossBar.setTitle(text);
                bossBar.setProgress(fraction);
                bossBar.addPlayer(p);
                timer--;
            }
        }

        public void setTimer(int timer) {
            this.timer = timer;
        }

        public void setText(String text) {
            this.text = text;
        }

        public void setFraction(double fraction) {
            this.fraction = fraction;
        }
    }
}
