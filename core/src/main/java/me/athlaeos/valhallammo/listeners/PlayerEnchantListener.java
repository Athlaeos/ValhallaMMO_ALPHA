package me.athlaeos.valhallammo.listeners;

import me.athlaeos.valhallammo.ValhallaMMO;
import me.athlaeos.valhallammo.managers.CustomDurabilityManager;
import me.athlaeos.valhallammo.managers.SkillProgressionManager;
import me.athlaeos.valhallammo.managers.SmithingItemTreatmentManager;
import me.athlaeos.valhallammo.skills.EnchantmentApplicationSkill;
import me.athlaeos.valhallammo.skills.Skill;
import me.athlaeos.valhallammo.utility.Utils;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.enchantment.EnchantItemEvent;
import org.bukkit.event.enchantment.PrepareItemEnchantEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;

public class PlayerEnchantListener implements Listener {

    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerEnchant(EnchantItemEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getEnchantBlock().getWorld().getName())) return;
        if (!e.isCancelled()){
            for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (s != null){
                    if (s instanceof EnchantmentApplicationSkill){
                        ((EnchantmentApplicationSkill) s).onEnchantItem(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPrepareEnchant(PrepareItemEnchantEvent e){
        if (ValhallaMMO.isWorldBlacklisted(e.getEnchantBlock().getWorld().getName())) return;
        if (!e.isCancelled()){
            for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
                if (s != null){
                    if (s instanceof EnchantmentApplicationSkill){
                        ((EnchantmentApplicationSkill) s).onPrepareEnchant(e);
                    }
                }
            }
        }
    }

    @EventHandler(priority=EventPriority.NORMAL)
    public void onPrepareEnchant(PrepareAnvilEvent e){
        if (e.getInventory().getLocation() != null){
            if (e.getInventory().getLocation().getWorld() != null){
                if (ValhallaMMO.isWorldBlacklisted(e.getInventory().getLocation().getWorld().getName())) return;
            }
        }
        ItemStack item1 = e.getInventory().getItem(0);
        ItemStack item2 = e.getInventory().getItem(1);
        ItemStack result = e.getResult();
        if (!Utils.isItemEmptyOrNull(item1) && !Utils.isItemEmptyOrNull(item2) && !Utils.isItemEmptyOrNull(result)){
            double repairFraction = 0;
            if (item1.getType() == item2.getType()){
                if (SmithingItemTreatmentManager.getInstance().getWeaponId(item1) != SmithingItemTreatmentManager.getInstance().getWeaponId(item2)) {
                    e.setResult(null); // items are the same type, but have different weapon ids, and are therefore considered different
                } else {
                    repairFraction = 1;
                }
            }
            if (!Utils.isItemEmptyOrNull(e.getResult())){
                if (CustomDurabilityManager.getInstance().hasCustomDurability(result) && CustomDurabilityManager.getInstance().hasCustomDurability(item1)){
                    int maxDurability = CustomDurabilityManager.getInstance().getMaxDurability(result);
                    if (repairFraction == 1){
                        CustomDurabilityManager.getInstance().setDurability(result, maxDurability, maxDurability);
                    } else {
                        Damageable resultMeta = (Damageable) result.getItemMeta();
                        Damageable item1Meta = (Damageable) item1.getItemMeta();
                        if (resultMeta != null && item1Meta != null){
                            int resultDurability = result.getType().getMaxDurability() - resultMeta.getDamage();
                            int item1Durability = item1.getType().getMaxDurability() - item1Meta.getDamage();
                            repairFraction = (resultDurability - item1Durability) / (double) result.getType().getMaxDurability();

                            int addDurability = (int) Math.ceil(repairFraction * (double) maxDurability);
                            CustomDurabilityManager.getInstance().damageItem(result, -addDurability);
                        }
                    }

                    e.setResult(result);
                }
            }
        }

        for (Skill s : SkillProgressionManager.getInstance().getAllSkills().values()){
            if (s != null){
                if (s instanceof EnchantmentApplicationSkill){
                    ((EnchantmentApplicationSkill) s).onAnvilUsage(e);
                }
            }
        }
    }
}
