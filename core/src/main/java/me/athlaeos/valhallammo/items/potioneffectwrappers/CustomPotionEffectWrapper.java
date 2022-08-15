package me.athlaeos.valhallammo.items.potioneffectwrappers;

import me.athlaeos.valhallammo.dom.PotionEffect;
import me.athlaeos.valhallammo.items.PotionType;
import me.athlaeos.valhallammo.managers.PotionEffectManager;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.ChatColor;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;

public class CustomPotionEffectWrapper extends PotionEffectWrapper{
    private final String matchString;
    private final PotionType type;
    private final String amplifierFormat;
    private final boolean instant;
    private double multiplyWith = 1;

    public CustomPotionEffectWrapper(String potionEffect, double amplifier, int duration, String matchString, String amplifierFormat, boolean instant) {
        super(potionEffect, amplifier, duration);
        this.matchString = matchString;
        this.amplifierFormat = amplifierFormat;
        this.instant = instant;
        PotionEffect basePotionEffect = PotionEffectManager.getInstance().getBasePotionEffect(potionEffect);
        if (basePotionEffect != null){
            this.type = basePotionEffect.getType();
        } else {
            this.type = PotionType.NEUTRAL;
        }
    }

    public CustomPotionEffectWrapper(String potionEffect, double amplifier, int duration, String matchString, String amplifierFormat, boolean instant, double multiplyWith) {
        super(potionEffect, amplifier, duration);
        this.matchString = matchString;
        this.amplifierFormat = amplifierFormat;
        this.instant = instant;
        this.multiplyWith = multiplyWith;
        PotionEffect basePotionEffect = PotionEffectManager.getInstance().getBasePotionEffect(potionEffect);
        if (basePotionEffect != null){
            this.type = basePotionEffect.getType();
        } else {
            this.type = PotionType.NEUTRAL;
        }
    }

    @Override
    public void onApply(ItemMeta potion) {
        updatePotionEffect(potion);
    }

    @Override
    public void onRemove(ItemMeta potion) {
        removeLore(potion);
    }

    @Override
    public boolean isCompatible(ItemStack i) {
        return i.getItemMeta() instanceof PotionMeta;
    }

    private void updatePotionEffect(ItemMeta meta){
        if (meta == null) return;
        if (matchString == null) return;
        if (meta.getItemFlags().contains(ItemFlag.HIDE_ATTRIBUTES)) {
            removeLore(meta);
            return;
        }
        double amplifier = this.amplifier * multiplyWith;
        int duration = this.duration;
        if (duration < 0) duration = 0;

        if (!matchString.equals("")){
            String color = ((type == PotionType.DEBUFF) ? "&c" : "&9");
            String formattedAmplifier = String.format(amplifierFormat, amplifier);
            if (instant){
                Utils.findAndReplaceLore(meta,
                        ChatColor.stripColor(Utils.chat(matchString)),
                        String.format(color + "%s %s", matchString, formattedAmplifier));
            } else {
                Utils.findAndReplaceLore(meta,
                        ChatColor.stripColor(Utils.chat(matchString)),
                        String.format(color + "%s %s (%s)", matchString, formattedAmplifier, Utils.toTimeStamp(duration, 1000)));
            }
        }
    }

    private void removeLore(ItemMeta meta){
        if (meta == null) return;
        if (matchString == null) return;
        if (!matchString.equals("")){
            Utils.removeIfLoreContains(meta,
                    ChatColor.stripColor(Utils.chat(matchString)));
        }
    }
}
